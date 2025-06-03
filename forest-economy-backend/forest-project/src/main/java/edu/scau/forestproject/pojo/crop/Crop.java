package edu.scau.forestproject.pojo.crop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crop {
    private Integer id;
    private String cropName;
    private String otherName;
    private String description;
    private Integer cropQuantity;
}
