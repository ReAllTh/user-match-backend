package link.reallth.usermatchbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.model.dto.UserSignInDTO;
import link.reallth.usermatchbackend.model.dto.UserSignUpDTO;
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
     * @param userSignUpDTO sign up info data transfer object
     * @param session       session
     * @return new user
     */
    UserVO signUp(UserSignUpDTO userSignUpDTO, HttpSession session);


    /**
     * user sign in
     *
     * @param userSignInDTO sign in data transfer object
     * @param session       session
     * @return new user
     */
    UserVO signIn(UserSignInDTO userSignInDTO, HttpSession session);

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

    /**
     * user delete
     *
     * @param id      user delete data tranfer object
     * @param session session
     * @return result
     */
    boolean deleteById(String id, HttpSession session);
}
