package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.mapper.TeamUserMapper;
import link.reallth.usermatchbackend.model.po.Team;
import link.reallth.usermatchbackend.model.po.TeamUser;
import link.reallth.usermatchbackend.model.vo.TeamVO;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.TeamService;
import link.reallth.usermatchbackend.service.TeamUserService;
import link.reallth.usermatchbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static link.reallth.usermatchbackend.constants.ExceptionDescConst.PERMISSION_DENIED;

/**
 * team user service impl
 *
 * @author ReAllTh
 */
@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserMapper, TeamUser>
        implements TeamUserService {

    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    @Override
    public TeamVO join(String id, HttpSession session) {
        // check if signed in
        UserVO currentUser = userService.currentUser(session);
        if (currentUser == null)
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // check id
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, "target team id can not be null");
        // check team
        Team targetTeam = teamService.getById(id);
        if (targetTeam == null || targetTeam.getExpireTime().before(new Date()))
            throw new BaseException(CODES.PARAM_ERR, "no such team");
        if (this.count(new QueryWrapper<TeamUser>().eq("team_id", id)) >= 5)
            throw new BaseException(CODES.PARAM_ERR, "team full");
        // join
        TeamUser teamUser = new TeamUser();
        teamUser.setTeamId(id);
        teamUser.setUserId(currentUser.getId());
        if (!this.save(teamUser))
            throw new BaseException(CODES.SYSTEM_ERR, "database insert failed");
        return teamService.getTeamVO(targetTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quit(String id, HttpSession session) {
        // check if signed in
        UserVO currentUser = userService.currentUser(session);
        if (currentUser == null)
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // check id
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, "target team id can not be null");
        // check team
        Team targetTeam = teamService.getById(id);
        if (targetTeam == null || targetTeam.getExpireTime().before(new Date()))
            throw new BaseException(CODES.PARAM_ERR, "no such team");
        // check user in team
        QueryWrapper<TeamUser> queryWrapper = new QueryWrapper<TeamUser>().eq("team_id", id).eq("user_id", currentUser.getId());
        TeamUser teamUser = this.getOne(queryWrapper);
        if (teamUser == null)
            throw new BaseException(CODES.PARAM_ERR, "current user not in this team");
        // delete
        if (!this.remove(queryWrapper))
            throw new BaseException(CODES.SYSTEM_ERR, "database delete failed");
        if (!this.exists(queryWrapper))
            teamService.removeById(targetTeam);
        return true;
    }
}




