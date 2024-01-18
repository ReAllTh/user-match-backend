package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.constants.enums.POSITIONS;
import link.reallth.usermatchbackend.constants.enums.ROLE;
import link.reallth.usermatchbackend.constants.enums.STATUS;
import link.reallth.usermatchbackend.exception.BaseException;
import link.reallth.usermatchbackend.mapper.TeamMapper;
import link.reallth.usermatchbackend.model.dto.TeamCreateDTO;
import link.reallth.usermatchbackend.model.dto.TeamFindDTO;
import link.reallth.usermatchbackend.model.dto.TeamUpdateDTO;
import link.reallth.usermatchbackend.model.po.Team;
import link.reallth.usermatchbackend.model.po.TeamUser;
import link.reallth.usermatchbackend.model.po.User;
import link.reallth.usermatchbackend.model.vo.TeamVO;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.TeamService;
import link.reallth.usermatchbackend.service.TeamUserService;
import link.reallth.usermatchbackend.service.UserService;
import link.reallth.usermatchbackend.utils.BusinessBeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static link.reallth.usermatchbackend.constants.ExceptionDescConst.PASSWORD_INVALID_MSG;
import static link.reallth.usermatchbackend.constants.ExceptionDescConst.PERMISSION_DENIED;
import static link.reallth.usermatchbackend.constants.RegexConst.PASSWORD_REGEX;

/**
 * team service impl
 *
 * @author ReAllTh
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    public static final String TEAM_ID = "team_id";
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
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // check if this user create too many teams
        if (teamUserService.count(new QueryWrapper<TeamUser>().eq("user_id", currentUser.getId()).eq("team_pos", POSITIONS.CREATOR.getVal())) > 4)
            throw new BaseException(CODES.PERMISSION_ERR, "can not create more teams");
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
        // check status and password
        Integer status = teamCreateDTO.getStatus();
        if (status == null || (status != STATUS.PUBLIC.getVal() && status != STATUS.PRIVATE.getVal()))
            throw new BaseException(CODES.PARAM_ERR, "status is null or not public and private both.");
        String password = teamCreateDTO.getPassword();
        if (status == STATUS.PRIVATE.getVal()) {
            if (StringUtils.isBlank(password))
                throw new BaseException(CODES.PARAM_ERR, "private team needs a password");
            if (!Pattern.matches(PASSWORD_REGEX, password))
                throw new BaseException(CODES.PARAM_ERR, PASSWORD_INVALID_MSG);
            teamCreateDTO.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        }
        if (status == STATUS.PUBLIC.getVal() && StringUtils.isNotBlank(password))
            throw new BaseException(CODES.PARAM_ERR, "public team do not need a password");
        // save
        Team team = new Team();
        BeanUtils.copyProperties(teamCreateDTO, team);
        if (!this.save(team))
            throw new BaseException(CODES.SYSTEM_ERR, "database insert failed");
        team = this.getById(team.getId());
        TeamUser teamUser = new TeamUser();
        teamUser.setTeamId(team.getId());
        teamUser.setUserId(currentUser.getId());
        teamUser.setTeamPos(POSITIONS.CREATOR.getVal());
        if (!teamUserService.save(teamUser))
            throw new BaseException(CODES.SYSTEM_ERR, "database insert failed");
        // generate team view object
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(team, teamVO);
        teamVO.setCreator(currentUser);
        teamVO.setMembers(List.of(currentUser));
        return teamVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disband(String id, HttpSession session) {
        // check id
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, "id can not be blank");
        // check signed in
        UserVO currentUser = userService.currentUser(session);
        if (currentUser == null)
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // check target team
        Team tergetTeam = this.getById(id);
        if (tergetTeam == null)
            throw new BaseException(CODES.PARAM_ERR, "no such team");
        // check permission
        TeamUser teamUser = teamUserService.getOne(new QueryWrapper<TeamUser>().eq(TEAM_ID, tergetTeam.getId()).eq("user_id", currentUser.getId()));
        if (currentUser.getRole() != ROLE.ADMIN.getVal() && teamUser.getTeamPos() != POSITIONS.CREATOR.getVal())
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // delete
        if (!teamUserService.remove(new QueryWrapper<TeamUser>().eq(TEAM_ID, tergetTeam.getId())))
            throw new BaseException(CODES.SYSTEM_ERR, "database delete failed");
        if (!this.removeById(tergetTeam))
            throw new BaseException(CODES.SYSTEM_ERR, "database delete failed");
        return true;
    }

    @Override
    public List<TeamVO> find(TeamFindDTO teamFindDTO, HttpSession session) {
        // check if signed in
        UserVO currentUser = userService.currentUser(session);
        if (currentUser == null)
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // find by id if id exist
        String id = teamFindDTO.getId();
        if (StringUtils.isNotBlank(id)) {
            Team targetTeam = this.getById(id);
            TeamVO targetTeamVO = new TeamVO();
            BeanUtils.copyProperties(targetTeam, targetTeamVO);
            List<TeamUser> targetTeamUserList = teamUserService.list(new QueryWrapper<TeamUser>().eq(TEAM_ID, targetTeam.getId()));
            List<UserVO> members = new ArrayList<>();
            targetTeamUserList.forEach(teamUser -> {
                UserVO userVO = BusinessBeanUtils.getUserVO(userService.getById(teamUser.getUserId()));
                members.add(userVO);
                if (teamUser.getTeamPos() == POSITIONS.CREATOR.getVal())
                    targetTeamVO.setCreator(userVO);
            });
            targetTeamVO.setMembers(members);
            return Collections.singletonList(targetTeamVO);
        }
        QueryWrapper<Team> qw = new QueryWrapper<>();
        // search text
        String searchText = teamFindDTO.getSearchText();
        if (StringUtils.isNotBlank(searchText))
            qw.like("team_name", searchText).or().like("description", searchText);
        // status
        Integer status = teamFindDTO.getStatus();
        if (status != null) {
            if (status != STATUS.PUBLIC.getVal() && status != STATUS.PRIVATE.getVal())
                throw new BaseException(CODES.PARAM_ERR, "status code invalid");
            qw.eq("status", status);
        }
        // create time
        Date createTimeFrom = teamFindDTO.getCreateTimeFrom();
        if (createTimeFrom != null)
            qw.ge("create_time", createTimeFrom);
        // filter expired team
        qw.le("create_time", new Date());
        // filter based on current result set
        Stream<Team> teamStream = this.list(qw).stream();
        // is full
        Boolean isFull = teamFindDTO.getIsFull();
        if (isFull != null)
            teamStream = teamStream.filter(team -> {
                Integer maxSize = team.getMaxUser();
                int size = teamUserService.list(new QueryWrapper<TeamUser>().eq(TEAM_ID, team.getId())).size();
                return isFull == (size >= maxSize);
            });
        // creator name
        String creatorName = teamFindDTO.getCreatorName();
        if (StringUtils.isNotBlank(creatorName))
            teamStream = teamStream.filter(team -> {
                TeamUser teamUser = teamUserService.getOne(new QueryWrapper<TeamUser>().eq(TEAM_ID, team.getId()).eq("team_pos", POSITIONS.CREATOR.getVal()));
                User user = userService.getById(teamUser.getUserId());
                return user.getUsername().toLowerCase().contains(creatorName.toLowerCase());
            });
        // paging
        Integer page = teamFindDTO.getPage();
        Integer pageSize = teamFindDTO.getPageSize();
        if (page == null || pageSize == null)
            throw new BaseException(CODES.PARAM_ERR, "page and page size can not be null");
        long skipLength = (long) (page - 1) * pageSize;
        teamStream = teamStream.skip(skipLength).limit(pageSize);
        // return mapping
        return teamStream.map(this::getTeamVO).toList();
    }

    @Override
    public TeamVO update(TeamUpdateDTO teamUpdateDTO, HttpSession session) {
        // check if signed in
        UserVO currentUser = userService.currentUser(session);
        if (currentUser == null)
            throw new BaseException(CODES.PERMISSION_ERR, PERMISSION_DENIED);
        // check id
        String id = teamUpdateDTO.getId();
        if (StringUtils.isBlank(id))
            throw new BaseException(CODES.PARAM_ERR, "target team id can not be null");
        // check team name
        String teamName = teamUpdateDTO.getTeamName();
        if (StringUtils.isNotBlank(teamName) && teamName.length() > 16)
            throw new BaseException(CODES.PARAM_ERR, "team name exceeds 16 characters");
        // check team description
        String description = teamUpdateDTO.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 2048)
            throw new BaseException(CODES.PARAM_ERR, "description exceeds 2048 characters");
        // check team max user
        Integer maxUser = teamUpdateDTO.getMaxUser();
        if (maxUser != null && (maxUser < 2 || maxUser > 5))
            throw new BaseException(CODES.PARAM_ERR, "max user must between 2 and 5");
        // check expire time
        Date expireTime = teamUpdateDTO.getExpireTime();
        if (expireTime != null && expireTime.before(new Date()))
            throw new BaseException(CODES.PARAM_ERR, "expire time must before now");
        // check status and password
        Integer status = teamUpdateDTO.getStatus();
        if (status != null) {
            if (status != STATUS.PUBLIC.getVal() && status != STATUS.PRIVATE.getVal())
                throw new BaseException(CODES.PARAM_ERR, "status must public or private");
            String password = teamUpdateDTO.getPassword();
            if (status == STATUS.PRIVATE.getVal()) {
                if (StringUtils.isBlank(password))
                    throw new BaseException(CODES.PARAM_ERR, "private team needs a password");
                if (!Pattern.matches(PASSWORD_REGEX, password))
                    throw new BaseException(CODES.PARAM_ERR, PASSWORD_INVALID_MSG);
                teamUpdateDTO.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
            } else if (StringUtils.isNotBlank(password))
                throw new BaseException(CODES.PARAM_ERR, "public team do not need a password");
        }
        // update
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateDTO, team);
        if (!this.update(team, new QueryWrapper<Team>().eq("id", team.getId())))
            throw new BaseException(CODES.SYSTEM_ERR, "database update failed");
        team = this.getById(team.getId());
        return this.getTeamVO(team);
    }

    /**
     * transfer team po to team vo
     *
     * @param team team po to be transfer
     * @return team view object
     */
    public TeamVO getTeamVO(Team team) {
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(team, teamVO);
        List<UserVO> members = new ArrayList<>();
        teamUserService.list(new QueryWrapper<TeamUser>().eq(TEAM_ID, team.getId()))
                .forEach(teamUser -> {
                    UserVO userVO = BusinessBeanUtils.getUserVO(userService.getById(teamUser.getUserId()));
                    members.add(userVO);
                    if (teamUser.getTeamPos() == POSITIONS.CREATOR.getVal())
                        teamVO.setCreator(userVO);
                });
        teamVO.setMembers(members);
        return teamVO;
    }
}
