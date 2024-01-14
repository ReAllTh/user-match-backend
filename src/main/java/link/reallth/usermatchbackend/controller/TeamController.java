package link.reallth.usermatchbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.common.BaseResponse;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.model.dto.TeamCreateDTO;
import link.reallth.usermatchbackend.model.dto.TeamFindDTO;
import link.reallth.usermatchbackend.model.ro.TeamCreateRO;
import link.reallth.usermatchbackend.model.ro.TeamFindRO;
import link.reallth.usermatchbackend.model.vo.TeamVO;
import link.reallth.usermatchbackend.service.TeamService;
import link.reallth.usermatchbackend.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static link.reallth.usermatchbackend.constants.ControllerConst.*;

/**
 * team controller
 *
 * @author ReAllTh
 */
@RestController
@RequestMapping("team")
public class TeamController {

    @Resource
    private TeamService teamService;

    /**
     * team create
     *
     * @param teamCreateRO team create request object
     * @param session      session
     * @return team view object
     */
    @PostMapping("create")
    public BaseResponse<TeamVO> create(TeamCreateRO teamCreateRO, HttpSession session) {
        if (teamCreateRO == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        if (session == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_SESSION_MSG);
        TeamCreateDTO teamCreateDTO = new TeamCreateDTO();
        BeanUtils.copyProperties(teamCreateRO, teamCreateDTO);
        // parse string to date
        String expireTime = teamCreateRO.getExpireTime();
        if (StringUtils.isNotBlank(expireTime)) {
            try {
                teamCreateDTO.setExpireTime(DateUtils.parseDate(expireTime, DATE_PATTERN));
            } catch (ParseException e) {
                throw new BaseException(CODES.PARAM_ERR, "date format should be: " + DATE_PATTERN);
            }
        }
        TeamVO teamVO = teamService.create(teamCreateDTO, session);
        return ResponseUtils.success(teamVO);
    }

    /**
     * team disband
     *
     * @param id      target team id
     * @param session session
     * @return result
     */
    @PostMapping("disband")
    public BaseResponse<Boolean> disband(String id, HttpSession session) {
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        if (session == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_SESSION_MSG);
        boolean disband = teamService.disband(id, session);
        return ResponseUtils.success(disband);
    }

    /**
     * team find
     *
     * @param teamFindRO team find request object
     * @param session    session
     * @return result team list
     */
    @GetMapping("find")
    public BaseResponse<List<TeamVO>> find(TeamFindRO teamFindRO, HttpSession session) {
        if (teamFindRO == null)
            throw new BaseException(CODES.PARAM_ERR, "can not found get params");
        if (session == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_SESSION_MSG);
        TeamFindDTO teamFindDTO = new TeamFindDTO();
        BeanUtils.copyProperties(teamFindRO, teamFindDTO);
        // parse recent day to create time from
        Integer recentDay = teamFindRO.getRecentDay();
        if (recentDay != null && recentDay > 0 && recentDay < 365)
            teamFindDTO.setCreateTimeFrom(DateUtils.addDays(new Date(), -recentDay));
        List<TeamVO> teamVOS = teamService.find(teamFindDTO, session);
        return ResponseUtils.success(teamVOS);
    }
}
