package edu.scau.forestproject.service;

import edu.scau.forestproject.pojo.LoginInfo;
import edu.scau.forestproject.pojo.User;

public interface UserService {

    LoginInfo login(User user);

    void delete(Integer id);

    void register(User user);
}
