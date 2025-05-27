package edu.scau.forestproject.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.scau.forestproject.mapper.DataManagementMapper;
import edu.scau.forestproject.pojo.*;
import edu.scau.forestproject.service.DataManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataManagementServiceImpl implements DataManagementService {

    @Autowired
    private DataManagementMapper dataManagementMapper;

    //User
    @Override
    public PageResult<User> userPage(UserQueryParam userQueryParam) {
        log.info("开始分页，页码:{}, 每页数量:{}", userQueryParam.getPage(), userQueryParam.getPageSize());
        PageHelper.startPage(userQueryParam.getPage(), userQueryParam.getPageSize());
        List<User> userList = dataManagementMapper.getUserList(userQueryParam);

//        Page<User> p = (Page<User>) userList;
//        return new PageResult<User>(p.getTotal(), p.getResult());
        // 关键修改：不要直接强制转换，而是使用PageInfo包装
        PageInfo<User> pageInfo = new PageInfo<>(userList);

        // 构建自定义的分页结果
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setRows(userList);

        return pageResult;

    }

    @Override
    public void delete(String username) {
        dataManagementMapper.deleteByUsername(username);
    }

    @Override
    public void updateUser(User user) {
        log.info("开始更新用户");
        dataManagementMapper.updateUserInfo(user);
    }

    //Plot
    @Override
    public PageResult<ProductionPlot> plotPage(PlotQueryParam plotQueryParam) {
        log.info("开始分页，页码:{}, 每页数量:{}", plotQueryParam.getPage(), plotQueryParam.getPageSize());
        PageHelper.startPage(plotQueryParam.getPage(), plotQueryParam.getPageSize());
        List<ProductionPlot> plotList = dataManagementMapper.getPlotList(plotQueryParam);
        log.info("获取的信息{}", plotList);
        PageInfo<ProductionPlot> pageInfo = new PageInfo<>(plotList);
        PageResult<ProductionPlot> pageResult = new PageResult<>();
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setRows(plotList);
        return pageResult;
    }

    @Override
    public void insert(ProductionPlot plot) {
        dataManagementMapper.insertPlot(plot);
    }

    @Override
    public void updatePlot(ProductionPlot productionPlot) {
        dataManagementMapper.updatePlotInfo(productionPlot);
    }

    @Override
    public void delete(Integer id) {
        log.info("删除地块id = {} 的 关系表中的数据", id);
        dataManagementMapper.deleteplot_has_crop(id);
        log.info("删除 id = {} 的地块", id);
        dataManagementMapper.deleteById(id);
    }
}
