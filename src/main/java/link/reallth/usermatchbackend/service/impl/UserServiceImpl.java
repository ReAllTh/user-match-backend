package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.constants.CacheConst;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.constants.enums.ROLE;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.mapper.UserMapper;
import link.reallth.usermatchbackend.model.dto.UserFindDTO;
import link.reallth.usermatchbackend.model.dto.UserSignInDTO;
import link.reallth.usermatchbackend.model.dto.UserSignUpDTO;
import link.reallth.usermatchbackend.model.dto.UserUpdateDTO;
import link.reallth.usermatchbackend.model.po.User;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static link.reallth.usermatchbackend.constants.ExceptionDescConst.PASSWORD_INVALID_MSG;
import static link.reallth.usermatchbackend.constants.ExceptionDescConst.PERMISSION_DENIED;
import static link.reallth.usermatchbackend.constants.RegexConst.PASSWORD_REGEX;

/**
 * user service impl
 *
 * @author ReAllTh
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private static final String USER_SALT = "salt";
    private static final String PASSWORD = "password";
    private static final String CURRENT_USER = "currentUser";
    public static final String USERNAME_REGEX = "^\\w{4,16}$";
    public static final String USERNAME_INVALID_MSG = "the username must be a string of letters, numbers, and underscores between 4 and 16 characters in length";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,128}$";
    public static final String INVALID_EMAIL_MSG = "invalid email address";
    public static final String URL_REGEX = "^(?:/|(?:https?|ftp)://)[\\w/.\\-]{1,2084}$";
    public static final String URL_INVALID_MSG = "invalid url";

    @Resource
    private RedissonClient redissonClient;

    @Override
    public UserVO signUp(UserSignUpDTO userSignUpDTO, HttpSession session) {
        // extract info
        String username = userSignUpDTO.getUsername();
        String email = userSignUpDTO.getEmail();
        String password = userSignUpDTO.getPassword();
        String avatar = userSignUpDTO.getAvatar();
        List<String> tags = userSignUpDTO.getTags();
        // check username email and password
        UserServiceImpl.checkUsernameEmailAndPassword(username, email, password);
        // check if username or email already exist
        if (this.exists(new QueryWrapper<User>().eq("username", username)
                .or().eq("email", email)))
            throw new BaseException(CODES.PARAM_ERR, "username or email already exist");
        // check avatar address invalid
        if (StringUtils.isNotBlank(avatar) && !Pattern.matches(URL_REGEX, avatar))
            throw new BaseException(CODES.PARAM_ERR, URL_INVALID_MSG);
        // digest password
        String digested = DigestUtils.md5DigestAsHex((password + USER_SALT).getBytes(StandardCharsets.UTF_8));
        // format tags
        tags = tags == null ? Collections.emptyList() : tags.stream().sorted().map(StringUtils::lowerCase).toList();
        // generate a new user
        User newUser = new User();
        BeanUtils.copyProperties(userSignUpDTO, newUser, PASSWORD, "tags");
        newUser.setPassword(digested);
        newUser.setTags(new Gson().toJson(tags));
        // insert to db
        if (!this.save(newUser))
            throw new BaseException(CODES.SYSTEM_ERR, "database insert error");
        // keep signUp in status
        newUser = this.getById(newUser.getId());
        UserVO userVO = UserServiceImpl.getUserVO(newUser);
        session.setAttribute(CURRENT_USER, userVO);
        return userVO;
    }

    @Override
    public UserVO signIn(UserSignInDTO userSignInDTO, HttpSession session) {
        // check if already login
        if (session.getAttribute(CURRENT_USER) != null)
            throw new BaseException(CODES.BUSINESS_ERR, "already signed in");
        // extract info
        String username = userSignInDTO.getUsername();
        String email = userSignInDTO.getEmail();
        String password = userSignInDTO.getPassword();
        // check params
        UserServiceImpl.checkUsernameEmailAndPassword(username, email, password);
        // digest password
        String digested = DigestUtils.md5DigestAsHex((password + USER_SALT).getBytes(StandardCharsets.UTF_8));
        // generate target user
        User user = new User();
        BeanUtils.copyProperties(userSignInDTO, user, PASSWORD);
        user.setPassword(digested);
        // check if user signed up
        User targetUser = this.getOne(new QueryWrapper<>(user));
        if (targetUser == null)
            throw new BaseException(CODES.BUSINESS_ERR, "user dose not signed up");
        // keep signUp in status
        UserVO userVO = UserServiceImpl.getUserVO(targetUser);
        session.setAttribute(CURRENT_USER, userVO);
        return userVO;
    }

    @Override
    public UserVO currentUser(HttpSession session) {
        return (UserVO) session.getAttribute(CURRENT_USER);
    }

    @Override
    public void signOut(HttpSession session) {
        session.removeAttribute(CURRENT_USER);
    }

    @Override
    public boolean deleteById(String id, HttpSession session) {
        // check if permissions are legal
        UserVO currentUser = currentUser(session);
        if (currentUser == null || currentUser.getRole() != ROLE.ADMIN.getVal())
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // check if id blank
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, "id can not be blank");
        // check if target user exist
        if (!this.exists(new QueryWrapper<User>().eq("id", id)))
            throw new BaseException(CODES.PARAM_ERR, "no such user");
        // remove
        if (!this.removeById(id))
            throw new BaseException(CODES.SYSTEM_ERR, "database delete failed");
        return true;
    }

    @Override
    public List<UserVO> find(UserFindDTO userFindDTO, HttpSession session) {
        // check if signed in
        if (currentUser(session) == null)
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // get by id if id exist
        String id = userFindDTO.getId();
        if (StringUtils.isNotBlank(id))
            return Stream.of(this.getById(id)).map(UserServiceImpl::getUserVO).toList();
        // combination find by other params
        QueryWrapper<User> qw = new QueryWrapper<>();
        // like username
        String username = userFindDTO.getUsername();
        if (StringUtils.isNotBlank(username))
            qw.like("username", username);
        // like email
        String email = userFindDTO.getEmail();
        if (StringUtils.isNotBlank(email))
            qw.like("email", email);
        // is role
        Integer role = userFindDTO.getRole();
        if (role != null)
            qw.eq("role", role);
        // between time
        Date createTimeFrom = userFindDTO.getCreateTimeFrom();
        Date createTimeTo = userFindDTO.getCreateTimeTo();
        if (createTimeFrom != null)
            qw.ge("create_time", createTimeFrom);
        if (createTimeTo != null)
            qw.le("create_time", createTimeTo);
        // get current result stream
        Stream<UserVO> userStream = this.list(qw).stream().map(UserServiceImpl::getUserVO);
        // filter by tags
        List<String> tags = userFindDTO.getTags();
        if (CollectionUtils.isNotEmpty(tags))
            userStream = userStream.filter(user -> CollectionUtils.containsAll(user.getTags(), tags));
        // paging
        Integer page = userFindDTO.getPage();
        Integer pageSize = userFindDTO.getPageSize();
        if (page == null || pageSize == null)
            throw new BaseException(CODES.PARAM_ERR, "page and page size cannot be null");
        long skipLength = (long) (page - 1) * pageSize;
        return userStream.skip(skipLength).limit(pageSize).toList();
    }

    @Override
    public UserVO update(UserUpdateDTO userUpdateDTO, HttpSession session) {
        // check if signed in
        UserVO currentUser = this.currentUser(session);
        if (currentUser == null)
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // check permission
        if (currentUser.getRole() != ROLE.ADMIN.getVal() && !userUpdateDTO.getId().equals(currentUser.getId()))
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // check username
        String username = userUpdateDTO.getUsername();
        if (StringUtils.isNotBlank(username) && !Pattern.matches(USERNAME_REGEX, username))
            throw new BaseException(CODES.PARAM_ERR, USERNAME_INVALID_MSG);
        // check password
        String password = userUpdateDTO.getPassword();
        if (StringUtils.isNotBlank(password) && !Pattern.matches(PASSWORD_REGEX, password))
            throw new BaseException(CODES.PARAM_ERR, PASSWORD_INVALID_MSG);
        // check email
        String email = userUpdateDTO.getEmail();
        if (StringUtils.isNotBlank(email) && !Pattern.matches(EMAIL_REGEX, email))
            throw new BaseException(CODES.PARAM_ERR, INVALID_EMAIL_MSG);
        // check avatar
        String avatar = userUpdateDTO.getAvatar();
        if (StringUtils.isNotBlank(avatar) && !Pattern.matches(URL_REGEX, avatar))
            throw new BaseException(CODES.PARAM_ERR, URL_INVALID_MSG);
        // check role
        Integer role = userUpdateDTO.getRole();
        if (role != null && role != ROLE.ADMIN.getVal() && role != ROLE.DEFAULT.getVal())
            throw new BaseException(CODES.PARAM_ERR, "invalid role");
        // update
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        if (StringUtils.isNotBlank(password))
            user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        if (!this.update(user, new QueryWrapper<User>().eq("id", user.getId())))
            throw new BaseException(CODES.SYSTEM_ERR, "database update failed");
        user = this.getById(user.getId());
        UserVO newUser = UserServiceImpl.getUserVO(user);
        if (currentUser.getId().equals(newUser.getId()))
            session.setAttribute(CURRENT_USER, newUser);
        return newUser;
    }

    @Override
    public List<UserVO> mainPageUsers(int page) {
        if (page < 1)
            throw new BaseException(CODES.PERMISSION_ERR, "invalid page number");
        // try fetch from redis
        String key = CacheConst.MAIN_PAGE + page;
        RBucket<List<UserVO>> rBucket = redissonClient.getBucket(key);
        if (!rBucket.isExists()) {
            // cache
            List<UserVO> userVOList = this.list(new Page<>(page, 10)).stream().map(UserServiceImpl::getUserVO).toList();
            rBucket.set(userVOList, Duration.ofSeconds(10L + new Random().nextLong(10L)));
        }
        return rBucket.get();
    }

    @Override
    public List<UserVO> match(HttpSession session) {
        UserVO currentUser = this.currentUser(session);
        // check if signed in
        if (currentUser == null)
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // match
        Gson gson = new Gson();
        Queue<User> queue = new PriorityQueue<>(
                (user1, user2) -> {
                    int distance1 = UserServiceImpl.getDistance(user1.getTags(), gson.toJson(currentUser.getTags()));
                    int distance2 = UserServiceImpl.getDistance(user2.getTags(), gson.toJson(currentUser.getTags()));
                    return distance1 - distance2;
                }
        );
        queue.addAll(this.list(new QueryWrapper<User>().ne("id", currentUser.getId())));
        return queue.stream()
                .limit(5)
                .map(UserServiceImpl::getUserVO)
                .toList();
    }

    private static void checkUsernameEmailAndPassword(String username, String email, String password) {
        // check params blank
        if (StringUtils.isBlank(username) && StringUtils.isBlank(email))
            throw new BaseException(CODES.PARAM_ERR, "user and email cannot be both blank");
        if (StringUtils.isBlank(password))
            throw new BaseException(CODES.PARAM_ERR, "password cannot be blank");
        // check username invalid
        if (StringUtils.isNotBlank(username) && !Pattern.matches(USERNAME_REGEX, username))
            throw new BaseException(CODES.PARAM_ERR, USERNAME_INVALID_MSG);
        // check email invalid
        if (StringUtils.isNotBlank(email) && !Pattern.matches(EMAIL_REGEX, email))
            throw new BaseException(CODES.PARAM_ERR, INVALID_EMAIL_MSG);
        // check password invalid
        if (!Pattern.matches(PASSWORD_REGEX, password))
            throw new BaseException(CODES.PARAM_ERR, PASSWORD_INVALID_MSG);
    }

    /**
     * transfer user to user view object
     *
     * @param user user to be transfer
     * @return user view object
     */
    public static UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO, "tags");
        userVO.setTags(new Gson().fromJson(user.getTags(), new TypeToken<ArrayList<String>>() {
        }));
        return userVO;
    }

    /**
     * return edit distance of given two string
     *
     * @param s1 string 1
     * @param s2 string 2
     * @return edit distance of two strng
     */
    private static int getDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] d = new int[len1 + 1][len2 + 1];

        int i = 0;
        int j = 0;
        for (i = 0; i <= len1; i++)
            d[i][0] = i;
        for (j = 0; j <= len2; j++)
            d[0][j] = j;
        for (i = 1; i < len1 + 1; i++)
            for (j = 1; j < len2 + 1; j++) {
                int cost = 1;
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    cost = 0;
                }
                int delete = d[i - 1][j] + 1;
                int insert = d[i][j - 1] + 1;
                int substitution = d[i - 1][j - 1] + cost;
                d[i][j] = min(delete, insert, substitution);
            }
        return (d[len1][len2]);
    }

    /**
     * return min of three int
     *
     * @param d delete
     * @param i insert
     * @param s substitution
     * @return min
     */
    private static int min(int d, int i, int s) {
        int temp = Math.min(d, i);
        return Math.min(s, temp);
    }
}




