package link.reallth.usermatchbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.common.BaseResponse;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.model.vo.TeamVO;
import link.reallth.usermatchbackend.service.TeamUserService;
import link.reallth.usermatchbackend.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static link.reallth.usermatchbackend.constants.ControllerConst.NULL_POST_MSG;
import static link.reallth.usermatchbackend.constants.ControllerConst.NULL_SESSION_MSG;

/**
 * team user controller
 *
 * @author ReAllTh
 */
@RestController
@RequestMapping("team_user")
public class TeamUserController {
    @Resource
    private TeamUserService teamUserService;

    /**
     * team join
     *
     * @param id      target team id
     * @param session session
     * @return target team view object
     */
    @PostMapping("join")
    public BaseResponse<TeamVO> join(String id, HttpSession session) {
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, NULL_POST_MSG);
        if (session == null)
            throw new BaseException(CODES.PARAM_ERR, NULL_SESSION_MSG);
        TeamVO teamVO = teamUserService.join(id, session);
        return ResponseUtils.success(teamVO);
    }
}
