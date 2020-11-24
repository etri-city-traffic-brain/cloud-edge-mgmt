package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateImageInfo implements Serializable {
    private String name;
    private String description;
    private String url;
    private String format;
    private Long minDisk;
    private Long minRam;
    private String visibility;
    private Boolean protect;
}
