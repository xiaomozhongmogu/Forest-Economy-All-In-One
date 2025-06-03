package edu.scau.forestproject.service;

import edu.scau.forestproject.pojo.*;
import edu.scau.forestproject.pojo.crop.Crop;
import edu.scau.forestproject.pojo.plot.PlotQueryParam;
import edu.scau.forestproject.pojo.plot.ProductionPlot;
import edu.scau.forestproject.pojo.user.User;
import edu.scau.forestproject.pojo.user.UserQueryParam;

public interface DataManagementService {

    PageResult<User> userPage(UserQueryParam userQueryParam);

    void delete(String username);//删除用户

    PageResult<ProductionPlot> plotPage(PlotQueryParam plotQueryParam);

    void insert(ProductionPlot plot);

    void updateUser(User user);

    void updatePlot(ProductionPlot productionPlot);

    void delete(Integer id);//删除地块

    PageResult<Crop> cropPage(String cropName, Integer page, Integer pageSize);
}
