package edu.scau.forestproject.pojo.plot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.locationtech.jts.geom.Geometry;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPlot {
    private Integer id;
    private String plotName;
    private String coordinate;

    private String username;
    private String cropName;
}
