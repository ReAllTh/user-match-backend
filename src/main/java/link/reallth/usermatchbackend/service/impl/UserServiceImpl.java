package link.reallth.usermatchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import link.reallth.usermatchbackend.model.po.User;
import link.reallth.usermatchbackend.service.UserService;
import link.reallth.usermatchbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author ReAllTh
* @description 针对表【user(user table)】的数据库操作Service实现
* @createDate 2024-03-12 16:29:25
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




