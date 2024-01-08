package link.reallth.usermatchbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.common.BaseResponse;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.model.dto.UserSignInDTO;
import link.reallth.usermatchbackend.model.dto.UserSignUpDTO;
import link.reallth.usermatchbackend.model.ro.UserSignInRO;
import link.reallth.usermatchbackend.model.ro.UserSignUpRO;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.UserService;
import link.reallth.usermatchbackend.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user controller
 *
 * @author ReAllTh
 */
@RestController("user")
public class UserController {
    @Resource
    private UserService userService;
    private static final String NULL_POST_MSG = "can not found post body";
    private static final String NULL_SESSION_MSG = "can not found post session";

    /**
     * user sign up
     *
     * @param userSignUpRO user sign up request object
     * @param session  session
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
     * @param session  session
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

    @PostMapping("delete")
    public BaseResponse<Boolean> delete(String id, HttpSession session) {
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        boolean result = userService.deleteById(id, session);
        return ResponseUtils.success(result);
    }
}