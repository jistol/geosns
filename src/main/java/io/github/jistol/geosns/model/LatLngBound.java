package io.github.jistol.geosns.model;

import lombok.Data;

@Data
public class LatLngBound {
    private Double west;
    private Double east;
    private Double north;
    private Double south;
}
