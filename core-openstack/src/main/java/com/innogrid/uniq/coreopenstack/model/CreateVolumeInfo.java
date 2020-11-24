package com.innogrid.uniq.coreopenstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreateVolumeInfo implements Serializable {
    private String name;
    private String description;
    @JsonProperty("type")
    private String sourceType;
    private String sourceId;
    private Integer sourceSize;
    private String volumeType;
    private Integer size;
    private String availabilityZone ;
    private String group;

}
