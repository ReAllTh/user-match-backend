package link.reallth.usermatchbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.common.BaseResponse;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.model.dto.SignInDTO;
import link.reallth.usermatchbackend.model.dto.SignUpDTO;
import link.reallth.usermatchbackend.model.ro.SignInRO;
import link.reallth.usermatchbackend.model.ro.SignUpRO;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.UserService;
import link.reallth.usermatchbackend.utils.ResponseUtils;
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
     * @param signUpRO sign up request object
     * @param session  session
     * @return new user
     */
    @PostMapping("signUp")
    public BaseResponse<UserVO> signUp(SignUpRO signUpRO, HttpSession session) {
        if (signUpRO == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        if (session == null)
            throw new BaseException(CODES.ERROR, NULL_SESSION_MSG);
        SignUpDTO signUpDTO = new SignUpDTO();
        BeanUtils.copyProperties(signUpRO, signUpDTO);
        UserVO userVO = userService.signUp(signUpDTO, session);
        return ResponseUtils.success(userVO);
    }

    /**
     * user sign in
     *
     * @param signInRO sign in request object
     * @param session  session
     * @return target user
     */
    @PostMapping("signIn")
    public BaseResponse<UserVO> signIn(SignInRO signInRO, HttpSession session) {
        if (signInRO == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        if (session == null)
            throw new BaseException(CODES.ERROR, NULL_SESSION_MSG);
        SignInDTO signInDTO = new SignInDTO();
        BeanUtils.copyProperties(signInRO, signInDTO);
        UserVO userVO = userService.signIn(signInDTO, session);
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
        return ResponseUtils.success(null);
    }
}
