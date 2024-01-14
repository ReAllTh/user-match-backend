package link.reallth.usermatchbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.model.dto.TeamCreateDTO;
import link.reallth.usermatchbackend.model.dto.TeamFindDTO;
import link.reallth.usermatchbackend.model.po.Team;
import link.reallth.usermatchbackend.model.vo.TeamVO;

import java.util.List;

/**
 * team service
 *
 * @author ReAllTh
 */
public interface TeamService extends IService<Team> {

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
     * @return result team list
     */
    List<TeamVO> find(TeamFindDTO teamFindDTO, HttpSession session);
}
