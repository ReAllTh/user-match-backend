package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.mapper.UserMapper;
import link.reallth.usermatchbackend.model.dto.SignInDTO;
import link.reallth.usermatchbackend.model.dto.SignUpDTO;
import link.reallth.usermatchbackend.model.po.User;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * user service impl
 *
 * @author ReAllTh
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private static final String SALT = "salt";
    private static final String PASSWORD = "password";
    private static final String CURRENT_USE = "currentUser";

    @Override
    public UserVO signUp(SignUpDTO signUpDTO, HttpSession session) {
        // extract info
        String username = signUpDTO.getUsername();
        String email = signUpDTO.getEmail();
        String password = signUpDTO.getPassword();
        String avatar = signUpDTO.getAvatar();
        List<String> tags = signUpDTO.getTags();
        // check username email and password
        this.checkUsernameEmailAndPassword(username, email, password);
        // check if username or email already exist
        if (this.count(new QueryWrapper<User>().eq("username", username)
                .or().eq("email", email)) > 0)
            throw new BaseException(CODES.PARAM_ERR, "username or email already exist");
        // check avatar address invalid
        if (StringUtils.isNotBlank(avatar) && !Pattern.matches("^(?:/|(?:https?|ftp)://)[\\w/.\\-]{1,2084}$", avatar))
            throw new BaseException(CODES.PARAM_ERR, "invalid url");
        // digest password
        String digested = DigestUtils.md5DigestAsHex((password + SALT).getBytes(StandardCharsets.UTF_8));
        // format tags
        tags = tags.stream().sorted().map(StringUtils::lowerCase).toList();
        // generate a new user
        User newUser = new User();
        BeanUtils.copyProperties(signUpDTO, newUser, PASSWORD, "tags");
        newUser.setPassword(digested);
        newUser.setTags(new Gson().toJson(tags));
        // insert to db
        if (!this.save(newUser))
            throw new BaseException(CODES.SYSTEM_ERR, "database insert error");
        // keep signUp in status
        newUser = this.getById(newUser.getId());
        UserVO newUserVO = new UserVO();
        BeanUtils.copyProperties(newUser, newUserVO);
        newUserVO.setTags(new Gson().fromJson(newUser.getTags(), new TypeToken<ArrayList<String>>() {
        }));
        session.setAttribute(CURRENT_USE, newUserVO);
        return newUserVO;
    }

    @Override
    public UserVO signIn(SignInDTO signInDTO, HttpSession session) {
        // check if already login
        if (session.getAttribute(CURRENT_USE) != null)
            throw new BaseException(CODES.BUSINESS_ERR, "already login");
        // extract info
        String username = signInDTO.getUsername();
        String email = signInDTO.getEmail();
        String password = signInDTO.getPassword();
        // check params
        this.checkUsernameEmailAndPassword(username, email, password);
        // digest password
        String digested = DigestUtils.md5DigestAsHex((password + SALT).getBytes(StandardCharsets.UTF_8));
        // generate target user
        User user = new User();
        BeanUtils.copyProperties(signInDTO, user, PASSWORD);
        user.setPassword(digested);
        // check if user signed up
        User targetUser = this.getOne(new QueryWrapper<>(user));
        if (targetUser == null)
            throw new BaseException(CODES.BUSINESS_ERR, "user dose not signed up");
        // keep signUp in status
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(targetUser, userVO);
        session.setAttribute(CURRENT_USE, userVO);
        return userVO;
    }

    @Override
    public UserVO currentUser(HttpSession session) {
        return (UserVO) session.getAttribute(CURRENT_USE);
    }

    @Override
    public void signOut(HttpSession session) {
        session.removeAttribute(CURRENT_USE);
    }

    private void checkUsernameEmailAndPassword(String username, String email, String password) {
        // check params blank
        if (StringUtils.isBlank(username) && StringUtils.isBlank(email))
            throw new BaseException(CODES.PARAM_ERR, "user and email cannot be both blank");
        if (StringUtils.isBlank(password))
            throw new BaseException(CODES.PARAM_ERR, "password cannot be blank");
        // check username invalid
        if (StringUtils.isNotBlank(username) && !Pattern.matches("^\\w{4,16}$", username))
            throw new BaseException(CODES.PARAM_ERR, "the username must be a string of letters, numbers, and underscores between 4 and 16 characters in length");
        // check email invalid
        if (StringUtils.isNotBlank(email) && !Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,128}$", email))
            throw new BaseException(CODES.PARAM_ERR, "invalid email address");
        // check password invalid
        if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,18}$", password))
            throw new BaseException(CODES.PARAM_ERR, "password must contain a combination of uppercase and lowercase letters and numbers, special characters can be used, and the length should be between 6-18");
    }
}




