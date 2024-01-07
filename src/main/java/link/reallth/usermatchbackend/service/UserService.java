package link.reallth.usermatchbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.model.dto.SignInDTO;
import link.reallth.usermatchbackend.model.dto.SignUpDTO;
import link.reallth.usermatchbackend.model.po.User;
import link.reallth.usermatchbackend.model.vo.UserVO;

/**
 * user service
 *
 * @author ReAllTh
 */
public interface UserService extends IService<User> {

    /**
     * user sign up
     *
     * @param signUpDTO sign up info dto
     * @param session   session
     * @return new user
     */
    UserVO signUp(SignUpDTO signUpDTO, HttpSession session);


    /**
     * user sign in
     *
     * @param signInDTO sign in dto
     * @param session   session
     * @return new user
     */
    UserVO signIn(SignInDTO signInDTO, HttpSession session);

    /**
     * return current user
     *
     * @return current user
     */
    UserVO currentUser(HttpSession session);

    /**
     * user sign out
     *
     * @param session session
     */
    void signOut(HttpSession session);
}
