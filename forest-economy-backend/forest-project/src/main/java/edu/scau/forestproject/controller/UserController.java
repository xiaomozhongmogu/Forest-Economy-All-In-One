package edu.scau.forestproject.controller;

import edu.scau.forestproject.pojo.Result;
import edu.scau.forestproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @DeleteMapping("/home")
    public Result delete(Integer id) {
        log.info("delete user:{}", id);
        userService.delete(id);
        return Result.success();
    }

}
