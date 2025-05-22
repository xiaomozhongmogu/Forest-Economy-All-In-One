package edu.scau.forestproject.pojo;

import lombok.Data;

@Data
public class PlotInsertParam {
    private Integer id;
    private String username;
    private String cropName;
    private String plotName;
    private String coordinate;
}
