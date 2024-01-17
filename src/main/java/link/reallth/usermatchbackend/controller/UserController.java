package link.reallth.usermatchbackend.controller;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.common.BaseResponse;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.model.dto.UserFindDTO;
import link.reallth.usermatchbackend.model.dto.UserSignInDTO;
import link.reallth.usermatchbackend.model.dto.UserSignUpDTO;
import link.reallth.usermatchbackend.model.dto.UserUpdateDTO;
import link.reallth.usermatchbackend.model.ro.UserFindRO;
import link.reallth.usermatchbackend.model.ro.UserSignInRO;
import link.reallth.usermatchbackend.model.ro.UserSignUpRO;
import link.reallth.usermatchbackend.model.ro.UserUpdateRO;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.UserService;
import link.reallth.usermatchbackend.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

import static link.reallth.usermatchbackend.constants.ControllerConst.*;

/**
 * user controller
 *
 * @author ReAllTh
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * user sign up
     *
     * @param userSignUpRO user sign up request object
     * @param session      session
     * @return new user
     */
    @PostMapping("signUp")
    public BaseResponse<UserVO> signUp(UserSignUpRO userSignUpRO, HttpSession session) {
        if (userSignUpRO == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        if (session == null)
            throw new BaseException(CODES.ERROR, NULL_SESSION_MSG);
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        BeanUtils.copyProperties(userSignUpRO, userSignUpDTO);
        UserVO userVO = userService.signUp(userSignUpDTO, session);
        return ResponseUtils.success(userVO);
    }

    /**
     * user sign in
     *
     * @param userSignInRO user sign in request object
     * @param session      session
     * @return target user
     */
    @PostMapping("signIn")
    public BaseResponse<UserVO> signIn(UserSignInRO userSignInRO, HttpSession session) {
        if (userSignInRO == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        if (session == null)
            throw new BaseException(CODES.ERROR, NULL_SESSION_MSG);
        UserSignInDTO userSignInDTO = new UserSignInDTO();
        BeanUtils.copyProperties(userSignInRO, userSignInDTO);
        UserVO userVO = userService.signIn(userSignInDTO, session);
        return ResponseUtils.success(userVO);
    }

    /**
     * return current user
     *
     * @param session session
     * @return current
     */
    @GetMapping("current")
    public BaseResponse<UserVO> current(HttpSession session) {
        if (session == null)
            throw new BaseException(CODES.ERROR, NULL_SESSION_MSG);
        UserVO currentUser = userService.currentUser(session);
        return ResponseUtils.success(currentUser);
    }

    /**
     * user sign out
     *
     * @param session session
     * @return null
     */
    @PostMapping("signOut")
    public BaseResponse<Void> signOut(HttpSession session) {
        if (session == null)
            throw new BaseException(CODES.ERROR, NULL_SESSION_MSG);
        userService.signOut(session);
        return ResponseUtils.success();
    }

    /**
     * user delete
     *
     * @param id      target user id
     * @param session session
     * @return result
     */
    @PostMapping("delete")
    public BaseResponse<Boolean> delete(String id, HttpSession session) {
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        boolean result = userService.deleteById(id, session);
        return ResponseUtils.success(result);
    }

    /**
     * user find
     *
     * @param userFindRO user find request object
     * @param session    session
     * @return result user list
     */
    @GetMapping("find")
    public BaseResponse<List<UserVO>> find(UserFindRO userFindRO, HttpSession session) {
        if (userFindRO == null)
            throw new BaseException(CODES.PARAM_ERR, "can not found get params");
        if (session == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_SESSION_MSG);
        UserFindDTO userFindDTO = new UserFindDTO();
        BeanUtils.copyProperties(userFindRO, userFindDTO, "createTimeFrom", "createTimeTo");
        // parse string to date
        String createTimeFrom = userFindRO.getCreateTimeFrom();
        String createTimeTo = userFindRO.getCreateTimeTo();
        if (StringUtils.isNoneBlank(createTimeFrom, createTimeTo)) {
            try {
                userFindDTO.setCreateTimeFrom(DateUtils.parseDate(createTimeFrom, DATE_PATTERN));
                userFindDTO.setCreateTimeTo(DateUtils.parseDate(createTimeTo, DATE_PATTERN));
            } catch (ParseException e) {
                throw new BaseException(CODES.PARAM_ERR, "data format should be " + DATE_PATTERN);
            }
        }
        List<UserVO> userVOS = userService.find(userFindDTO, session);
        return ResponseUtils.success(userVOS);
    }

    /**
     * user update
     *
     * @param userUpdateRO user update request object
     * @param session      session
     * @return user view object
     */
    @PostMapping("update")
    public BaseResponse<UserVO> update(UserUpdateRO userUpdateRO, HttpSession session) {
        if (userUpdateRO == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        if (session == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_SESSION_MSG);
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        BeanUtils.copyProperties(userUpdateRO, userUpdateDTO);
        List<String> tags = userUpdateRO.getTags();
        // parse tags
        if (tags != null) {
            if (tags.size() > 20)
                throw new BaseException(CODES.PARAM_ERR, "too many tags");
            for (String tag : tags)
                if (tag.length() > 20)
                    throw new BaseException(CODES.PARAM_ERR, "tag too long: " + tag);
            userUpdateDTO.setTags(new Gson().toJson(tags));
        }
        UserVO newUser = userService.update(userUpdateDTO, session);
        return ResponseUtils.success(newUser);
    }
}