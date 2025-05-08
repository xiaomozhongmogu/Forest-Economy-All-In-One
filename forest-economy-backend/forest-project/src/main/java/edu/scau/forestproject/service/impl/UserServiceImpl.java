package edu.scau.forestproject.service.impl;

import edu.scau.forestproject.mapper.UserMapper;
import edu.scau.forestproject.pojo.LoginInfo;
import edu.scau.forestproject.pojo.User;
import edu.scau.forestproject.service.UserService;
import edu.scau.forestproject.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginInfo login(User user) {
        User u = userMapper.selectByUsernameOrPhonenumberAndPassword(user);

        if (u != null) {
            log.info("登陆成功，用户信息：{}", u);
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", u.getId());
            claims.put("username", u.getUsername());
            String jwt = JwtUtils.generateToken(claims);
            return new LoginInfo(u.getId(), u.getUsername(), jwt);
        }

        return null;
    }

//    @Transactional(rollbackFor = Exception.class)//事务
    @Override
    public void delete(Integer id) {
        userMapper.deleteByID(id);
    }

    @Override
    public void register(User user) {
        userMapper.addUser(user);
    }
}
