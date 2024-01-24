package link.reallth.usermatchbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import link.reallth.usermatchbackend.model.dto.TeamCreateDTO;
import link.reallth.usermatchbackend.model.dto.TeamFindDTO;
import link.reallth.usermatchbackend.model.dto.TeamUpdateDTO;
import link.reallth.usermatchbackend.model.po.Team;
import link.reallth.usermatchbackend.model.vo.TeamVO;

import java.util.List;

/**
 * team service
 *
 * @author ReAllTh
 */
public interface TeamService extends IService<Team> {
}
