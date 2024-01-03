package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import link.reallth.usermatchbackend.model.po.Team;
import link.reallth.usermatchbackend.service.TeamService;
import link.reallth.usermatchbackend.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author ReAllTh
* @description 针对表【team(team table)】的数据库操作Service实现
* @createDate 2024-03-12 16:29:41
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




