package link.reallth.usermatchbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.common.BaseResponse;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.model.dto.TeamCreateDTO;
import link.reallth.usermatchbackend.model.ro.TeamCreateRO;
import link.reallth.usermatchbackend.model.vo.TeamVO;
import link.reallth.usermatchbackend.service.TeamService;
import link.reallth.usermatchbackend.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

import static link.reallth.usermatchbackend.constants.ControllerConst.*;

/**
 * team controller
 *
 * @author ReAllTh
 */
@RestController("team")
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
}
