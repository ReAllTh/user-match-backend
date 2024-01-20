package link.reallth.usermatchbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.model.po.TeamUser;
import link.reallth.usermatchbackend.model.vo.TeamVO;

/**
 * team user service
 *
 * @author ReAllTh
 */
public interface TeamUserService extends IService<TeamUser> {

    /**
     * team join
     *
     * @param id      target team id
     * @param session session
     * @return joined team view object
     */
    TeamVO join(String id, HttpSession session);

    /**
     * team quit
     *
     * @param id      target team id
     * @param session session
     * @return result
     */
    boolean quit(String id, HttpSession session);
}
