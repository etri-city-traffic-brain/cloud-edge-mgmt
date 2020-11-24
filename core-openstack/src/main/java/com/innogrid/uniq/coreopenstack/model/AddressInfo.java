package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.compute.Address;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.4.11
 * @brief 오픈스택 IP Address 모델
 */
@Data
public class AddressInfo implements Serializable {

    private static final long serialVersionUID = 502891212771252804L;
    private String macAddr;
    private int version;
    private String addr;
    private String type;
    private String networkName;

    public AddressInfo() {

    }

    public AddressInfo(Address info) {
        this.macAddr = info.getMacAddr();
        this.version = info.getVersion();
        this.addr = info.getAddr();
        this.type = info.getType();
    }
}
