package edu.scau.forestproject.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class PlotQueryParam {
    private Integer page;//当前页码
    private Integer pageSize;//每页条目数
    private Integer id;//地块编号
    private String username;//地块负责人
    private String cropName;


}
