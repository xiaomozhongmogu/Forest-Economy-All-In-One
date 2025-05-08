package edu.scau.forestproject.controller;

import edu.scau.forestproject.pojo.LoginInfo;
import edu.scau.forestproject.pojo.User;
import edu.scau.forestproject.service.UserService;
import edu.scau.forestproject.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("login info:{}", user);
        LoginInfo loginInfo = userService.login(user);
        if (loginInfo != null) {
            return Result.success(loginInfo);
        }
        return Result.error("用户名/电话或密码错误");
    }

}
