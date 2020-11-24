package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wss
 * @date 2019.7.10
 * @brief 오픈스택 서버 생성 모델
 */
@Data
public class CreateServerInfo implements Serializable {
    private static final long serialVersionUID = -2601096376172640314L;
    private String name;
    private String zone;
    private String sourceType;
    private String sourceId;
    private String flavorId;
    private List<String> networks;
    private List<String> securityGroups;
    private String keyPair;
    private Boolean configDrive;
    private String script;
    private Boolean deleteOnTermination;
    private Boolean newVolume;
    private Integer size;

    private String network;
    private String securityGroup;
    private String keypair;
    private String flavor;

    private String imageId;
    private String flavorName;
    private List<String> securityGroupName;
    private Boolean volumeCreated;
    private List<String> networkId;


    public CreateServerInfo() {
    }
}
