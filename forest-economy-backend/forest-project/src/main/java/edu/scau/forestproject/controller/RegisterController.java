package edu.scau.forestproject.controller;

import edu.scau.forestproject.pojo.LoginInfo;
import edu.scau.forestproject.pojo.Result;
import edu.scau.forestproject.pojo.User;
import edu.scau.forestproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RegisterController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        log.info("Registering user {}", user);
        userService.register(user);
        return Result.success();
    }
}
