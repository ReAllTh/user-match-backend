package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
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
import link.reallth.usermatchbackend.utils.UserUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
        UserVO userVO = UserUtils.getUserVO(newUser);
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
        UserVO userVO = UserUtils.getUserVO(targetUser);
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
            return Stream.of(this.getById(id)).map(UserUtils::getUserVO).toList();
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
        Stream<UserVO> userStream = this.list(qw).stream().map(UserUtils::getUserVO);
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
        UserVO newUser = UserUtils.getUserVO(user);
        if (currentUser.getId().equals(newUser.getId()))
            session.setAttribute(CURRENT_USER, newUser);
        return newUser;
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
}




