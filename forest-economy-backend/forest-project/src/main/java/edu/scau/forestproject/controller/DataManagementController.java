package edu.scau.forestproject.controller;

import edu.scau.forestproject.pojo.*;
import edu.scau.forestproject.service.DataManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/data_management")
public class DataManagementController {

    @Autowired
    private DataManagementService dataManagementService;

    @GetMapping("/user")//农户信息
    public Result searchUser(UserQueryParam userQueryParam) {
        log.info("User info:{}", userQueryParam);
        PageResult<User> userPageResult = dataManagementService.userPage(userQueryParam);
        return Result.success(userPageResult);
    }

    @DeleteMapping("/user")
    public Result deleteUser(String username) {
        log.info("删除农户:{}", username);
        dataManagementService.delete(username);
        return Result.success();
    }

    @PutMapping("/user")
    public Result editUser(@RequestBody User user) {
        log.info("User info:{}", user);
        dataManagementService.updateUser(user);
        return Result.success();
    }

    @GetMapping("/plot")//农户信息
    public Result searchPlot(PlotQueryParam plotQueryParam) {
        log.info("Plot info:{}", plotQueryParam);
        PageResult<ProductionPlot> plotPageResult = dataManagementService.plotPage(plotQueryParam);
        return Result.success(plotPageResult);
    }

    @PostMapping("/plot")
    public Result addPlot(@RequestBody ProductionPlot plot) {
        log.info("添加地块:{}", plot);
        dataManagementService.insert(plot);
        return Result.success();
    }

    @DeleteMapping("/plot")
    public Result deletePlot(Integer id) {
        log.info("删除地块id:{}", id);
        dataManagementService.delete(id);
        return Result.success();
    }

    @PutMapping("/plot")
    public Result editPlot(@RequestBody ProductionPlot productionPlot) {
        log.info("Plot info:{}", productionPlot);
        dataManagementService.updatePlot(productionPlot);
        return Result.success();
    }
}
