package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import link.reallth.usermatchbackend.mapper.TeamMapper;
import link.reallth.usermatchbackend.model.po.Team;
import link.reallth.usermatchbackend.service.TeamService;
import org.springframework.stereotype.Service;

/**
 * team service impl
 *
 * @author ReAllTh
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

}




