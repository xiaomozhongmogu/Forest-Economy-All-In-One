package edu.scau.forestproject.mapper;

import edu.scau.forestproject.pojo.crop.Crop;
import edu.scau.forestproject.pojo.plot.PlotQueryParam;
import edu.scau.forestproject.pojo.plot.ProductionPlot;
import edu.scau.forestproject.pojo.user.User;
import edu.scau.forestproject.pojo.user.UserQueryParam;
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

    @Delete("DELETE FROM productionplot_has_crop where ProductionPlot_id = #{id}")
    void deleteplot_has_crop(Integer id);

    @Delete("DELETE FROM ProductionPlot where id = #{id}")
    void deleteById(Integer id);

//    @Select("select * from Crop where cropName = #{cropName}")
    List<Crop> getCropList(String cropName);
}
