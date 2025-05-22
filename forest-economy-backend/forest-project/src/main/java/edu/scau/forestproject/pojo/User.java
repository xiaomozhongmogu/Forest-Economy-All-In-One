package edu.scau.forestproject.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String phoneNumber;
    private String password;
    private String residenceAddress;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;
    //type = 1 -----农户
    //type = 2 -----管理员
    private Integer type;
    private String token;


    private String cropName;
}
