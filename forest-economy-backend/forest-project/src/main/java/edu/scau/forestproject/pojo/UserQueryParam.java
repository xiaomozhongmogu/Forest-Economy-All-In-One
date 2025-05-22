package edu.scau.forestproject.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserQueryParam {
    private Integer page;//当前页码
    private Integer pageSize;//每页条目数
    private String username;//用户姓名
    private String phoneNumber;//电话号码
    private String residenceAddress;//地址
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;//注册日期
    private String cropName;//作物名
    private Integer id;
}
