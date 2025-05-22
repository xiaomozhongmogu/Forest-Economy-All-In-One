package edu.scau.forestproject.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String username;
    private String phoneNumber;
    private String residenceAddress;
    private LocalDate registrationDate;
    private String cropName;
}
