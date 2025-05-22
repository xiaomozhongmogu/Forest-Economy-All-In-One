package edu.scau.forestproject.mapper;

import edu.scau.forestproject.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface UserMapper {


    @Select("select id, username from User where (username = #{phoneNumber} or phoneNumber = #{phoneNumber}) and password = #{password}")
    User selectByUsernameOrPhonenumberAndPassword(User user);

    @Delete("DELETE FROM User WHERE id = #{id}")
    void deleteByID(Integer id);

    @Insert("INSERT INTO User (username, phoneNumber, password, registrationDate) VALUES (#{user.username}, #{user.phoneNumber}, #{user.password}, #{registrationDate})")
    void addUser(User user, LocalDate registrationDate);
}
