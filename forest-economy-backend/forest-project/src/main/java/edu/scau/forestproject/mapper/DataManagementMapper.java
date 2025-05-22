package edu.scau.forestproject.mapper;

import edu.scau.forestproject.pojo.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataManagementMapper {

    List<User> getUserList(UserQueryParam userQueryParam);

    @Delete("DELETE FROM User WHERE username = #{username}")
    void deleteByUsername(String username);

    List<ProductionPlot> getPlotList(PlotQueryParam plotQueryParam);

    void insertPlot(ProductionPlot plot);

    void updateUserInfo(User user);

    void updatePlotInfo(ProductionPlot productionPlot);

    @Delete("DELETE FROM ProductionPlot where id = #{id}")
    void deleteById(Integer id);
}
