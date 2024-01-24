package link.reallth.usermatchbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.model.dto.TeamCreateDTO;
import link.reallth.usermatchbackend.model.dto.TeamFindDTO;
import link.reallth.usermatchbackend.model.dto.TeamUpdateDTO;
import link.reallth.usermatchbackend.model.po.Team;
import link.reallth.usermatchbackend.model.po.TeamUser;
import link.reallth.usermatchbackend.model.vo.TeamVO;

import java.util.List;

/**
 * team user service
 *
 * @author ReAllTh
 */
public interface TeamUserService extends IService<TeamUser> {

    /**
     * team create
     *
     * @param teamCreateDTO team create data transfer object
     * @param session       session
     * @return new team view object
     */
    TeamVO create(TeamCreateDTO teamCreateDTO, HttpSession session);

    /**
     * team disband
     *
     * @param id      target team id
     * @param session session
     * @return result
     */
    boolean disband(String id, HttpSession session);

    /**
     * team find
     *
     * @param teamFindDTO team find data transfer object
     * @param session     session
     * @return reslut team list
     */
    List<TeamVO> find(TeamFindDTO teamFindDTO, HttpSession session);

    /**
     * team update
     *
     * @param teamUpdateDTO team update request object
     * @param session       session
     * @return updated team view object
     */
    TeamVO update(TeamUpdateDTO teamUpdateDTO, HttpSession session);

    /**
     * return team view object corresponding to team persistent object
     *
     * @param team team persistent object
     * @return team view object
     */
    TeamVO getTeamVO(Team team);

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

    /**
     * remove member
     *
     * @param teamId  target team id
     * @param userId  target member id
     * @param session session
     * @return result team view object
     */
    TeamVO removeMember(String teamId, String userId, HttpSession session);
}
