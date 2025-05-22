package edu.scau.forestproject.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import edu.scau.forestproject.mapper.DataManagementMapper;
import edu.scau.forestproject.pojo.PageResult;
import edu.scau.forestproject.pojo.User;
import edu.scau.forestproject.pojo.UserQueryParam;
import edu.scau.forestproject.service.DataManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataManagementImpl implements DataManagementService {

    @Autowired
    private DataManagementMapper dataManagementMapper;

    @Override
    public PageResult<User> page(UserQueryParam userQueryParam) {
        PageHelper.startPage(userQueryParam.getPage(), userQueryParam.getPageSize());

        List<User> userList = dataManagementMapper.getUserList(userQueryParam);

        Page<User> p = (Page<User>) userList;
        return new PageResult<User>(p.getTotal(), p.getResult());
    }
}
