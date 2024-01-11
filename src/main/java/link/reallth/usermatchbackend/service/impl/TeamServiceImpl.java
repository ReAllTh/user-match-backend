package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.constants.enums.STATUS;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.mapper.TeamMapper;
import link.reallth.usermatchbackend.model.dto.TeamCreateDTO;
import link.reallth.usermatchbackend.model.po.Team;
import link.reallth.usermatchbackend.model.po.TeamUser;
import link.reallth.usermatchbackend.model.vo.TeamVO;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.TeamService;
import link.reallth.usermatchbackend.service.TeamUserService;
import link.reallth.usermatchbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * team service impl
 *
 * @author ReAllTh
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserService userService;
    @Resource
    private TeamUserService teamUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeamVO create(TeamCreateDTO teamCreateDTO, HttpSession session) {
        // check if signed in
        UserVO currentUser = userService.currentUser(session);
        if (currentUser == null)
            throw new BaseException(CODES.PERMISSION_ERR, "permission denied");
        // check team name
        String teamName = teamCreateDTO.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 16)
            throw new BaseException(CODES.PARAM_ERR, "team is null or exceeds 16 characters");
        // check team description
        String description = teamCreateDTO.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 2048)
            throw new BaseException(CODES.PARAM_ERR, "description exceeds 2048 characters");
        // check team max user
        Integer maxUser = teamCreateDTO.getMaxUser();
        if (maxUser == null || maxUser < 2 || maxUser > 5)
            throw new BaseException(CODES.PARAM_ERR, "max user is null or not between 2 and 5");
        // check expire time
        Date expireTime = teamCreateDTO.getExpireTime();
        if (expireTime == null || expireTime.before(new Date()))
            throw new BaseException(CODES.PARAM_ERR, "expire time is null or before now");
        // check status
        Integer status = teamCreateDTO.getStatus();
        if (status == null || (status != STATUS.PUBLIC.getVal() && status != STATUS.PRIVATE.getVal()))
            throw new BaseException(CODES.PARAM_ERR, "status is null or not public and private both.");
        // save
        Team team = new Team();
        BeanUtils.copyProperties(teamCreateDTO, team);
        if (!this.save(team))
            throw new BaseException(CODES.SYSTEM_ERR, "database insert failed");
        TeamUser teamUser = new TeamUser();
        teamUser.setTeamId(team.getId());
        teamUser.setUserId(currentUser.getId());
        teamUser.setJoinTime(team.getCreateTime());
        if (!teamUserService.save(teamUser))
            throw new BaseException(CODES.SYSTEM_ERR, "database insert failed");
        // generate team view object
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(team, teamVO);
        teamVO.setMembers(List.of(currentUser));
        return teamVO;
    }
}




