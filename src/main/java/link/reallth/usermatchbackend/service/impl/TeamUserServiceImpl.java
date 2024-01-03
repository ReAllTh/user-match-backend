package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import link.reallth.usermatchbackend.model.po.TeamUser;
import link.reallth.usermatchbackend.service.TeamUserService;
import link.reallth.usermatchbackend.mapper.TeamUserMapper;
import org.springframework.stereotype.Service;

/**
* @author ReAllTh
* @description 针对表【team_user(team_user table)】的数据库操作Service实现
* @createDate 2024-03-12 16:29:34
*/
@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserMapper, TeamUser>
    implements TeamUserService{

}




