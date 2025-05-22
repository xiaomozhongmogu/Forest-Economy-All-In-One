package edu.scau.forestproject.service;

import edu.scau.forestproject.pojo.*;

public interface DataManagementService {

    PageResult<User> userPage(UserQueryParam userQueryParam);

    void delete(String username);//删除用户

    PageResult<ProductionPlot> plotPage(PlotQueryParam plotQueryParam);

    void insert(ProductionPlot plot);

    void updateUser(User user);

    void updatePlot(ProductionPlot productionPlot);

    void delete(Integer id);//删除地块
}
