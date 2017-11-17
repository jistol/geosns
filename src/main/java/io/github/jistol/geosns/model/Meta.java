package io.github.jistol.geosns.model;

import lombok.Data;

@Data
public class Meta {
    public static final String typeId = "id";
    public static final String typeFile = "file";

    private String type;
    private Long key;
    private Long deltaX;
    private Long deltaY;
}
