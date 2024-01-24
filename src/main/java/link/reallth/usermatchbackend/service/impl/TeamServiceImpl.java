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
}
