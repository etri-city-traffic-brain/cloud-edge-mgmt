package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.compute.ext.AvailabilityZone;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.4.16
 * @brief 오픈스택 Zone 모델
 */
@Data
public class AvailabilityZoneInfo implements Serializable {

    private static final long serialVersionUID = -42817319730654758L;
    private String zoneName;
    private Boolean available;
    private String resource;

    public AvailabilityZoneInfo() {

    }

    public AvailabilityZoneInfo(Object info) {
        if(info instanceof AvailabilityZone) {
            this.zoneName = ((AvailabilityZone)info).getZoneName();
            this.available = ((AvailabilityZone)info).getZoneState().getAvailable();
        } else if(info instanceof org.openstack4j.openstack.storage.block.domain.AvailabilityZone) {
            this.zoneName = ((org.openstack4j.openstack.storage.block.domain.AvailabilityZone)info).getZoneName();
            this.available = ((org.openstack4j.openstack.storage.block.domain.AvailabilityZone)info).getZoneState().getAvailable();
        } else if(info instanceof org.openstack4j.model.network.AvailabilityZone) {
            this.zoneName = ((org.openstack4j.model.network.AvailabilityZone)info).getName();
            this.available = ((org.openstack4j.model.network.AvailabilityZone)info).getState().equals("available");
            this.resource = ((org.openstack4j.model.network.AvailabilityZone)info).getResource();
        }
    }
}
