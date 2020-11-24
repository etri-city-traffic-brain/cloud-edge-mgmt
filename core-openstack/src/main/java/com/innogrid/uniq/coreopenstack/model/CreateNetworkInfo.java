package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateNetworkInfo implements Serializable {
    private String name;
    private String tenantId;
    private Boolean shared;
    private Boolean adminStateUp;
    private String[] availabilityZones;

    //API Create Network
    private Boolean networkManaged;
    private Boolean networkShared;

}
