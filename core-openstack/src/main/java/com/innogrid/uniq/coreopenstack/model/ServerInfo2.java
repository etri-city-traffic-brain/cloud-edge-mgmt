package com.innogrid.uniq.coreopenstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.coreopenstack.util.JsonDateDeserializer;
import com.innogrid.uniq.coreopenstack.util.JsonDateSerializer;
import lombok.Data;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.SecurityGroup;
import org.openstack4j.model.compute.Server;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wss
 * @date 2019.3.19
 * @brief 오픈스택 서버용 모델
 */
@Data
public class ServerInfo2 implements Serializable {

    private static final long serialVersionUID = 2897465774550361154L;
    private String id;
    //    private String host;
    private String name;
    @JsonProperty("sourceType")
    private String imageName;
    private String imageId;
    private String flavorName;
    private String zone;

    @JsonProperty("serverState")
    private String state;

    @JsonProperty("keyPair")
    private String keyName;
    private int cpu;
    private int memory;
    private int disk;
    //    private String powerState;
    @JsonProperty("networkId")
    private String network;
    private String ip;
    //    private List<String> securityGroup;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Timestamp createdAt;
    @JsonProperty("securityGroupName")
    private String securityGroup;
    private Boolean volumeCreated;
    private String test1;
    private String test2;
    private String projectId;
    private String flavorId;
    private String state2;
//    private String projectName;
//    private List<String> volumes;
//    private List<AddressInfo> addresses;
//    private Map<String, String> metaData;
//    private String keyName;
//    private String taskState;

    public ServerInfo2() {
    }

    //    public ServerInfo2(Server server,List<SecurityGroupInfo> securityGroupInfoList) {
    public ServerInfo2(Server server,List<NetworkInfo> networkInfoList) {
        if (server == null) return;

        String tempNetName;

        this.id = server.getId();
//        this.host = server.getHost();
        this.name = server.getName();
        this.state2 = server.getStatus().value();
        this.projectId = server.getTenantId();
        this.state = checkState(server.getStatus().value());

        this.imageId = server.getImageId();

        this.zone = server.getAvailabilityZone();

        this.keyName = server.getKeyName();


        if (server.getFlavor() != null) {
            this.flavorName = server.getFlavor().getName();
            this.cpu = server.getFlavor().getVcpus();
            this.memory = server.getFlavor().getRam();
            this.disk = server.getFlavor().getDisk();
            this.flavorId = server.getFlavorId();
        }
//        this.metaData = server.getMetadata();

        Iterator<String> keys = server.getAddresses().getAddresses().keySet().iterator();
        List<AddressInfo> list = new ArrayList<>();
        while (keys.hasNext()) {
            String key = keys.next();

            List<? extends Address> addresses = server.getAddresses().getAddresses().get(key);

            for (int i = 0; i < addresses.size(); i++) {
                AddressInfo info = new AddressInfo(addresses.get(i));
                info.setNetworkName(key);
                list.add(info);

                if (info.getType().equals("floating")) {
                    this.ip = info.getAddr();
                }
                tempNetName = info.getNetworkName();
                this.network=setNetworkID(networkInfoList,tempNetName);
            }
        }
//        this.addresses = list;
//        this.projectId = server.getTenantId();
//        this.powerState = server.getPowerState();
        this.createdAt = new Timestamp(server.getCreated().getTime());
//        this.volumes = server.getOsExtendedVolumesAttached();
        List<? extends SecurityGroup> securityGroups =server.getSecurityGroups();
        if(securityGroups!=null){
            for(int i=0;i<securityGroups.size();i++) {
                this.securityGroup = securityGroups.get(i).getName();
            }
        }
        else{
            this.securityGroup=null;
        }

//        this.taskState = server.getTaskState();
//        for(int i=0;i<securityGroupInfoList.size();i++) {
//            if (securityGroupInfoList.get(i).getName() == null) {
//                this.securityGroup = null;
//            } else {
//                this.securityGroup.add(securityGroupInfoList.get(i).getName());
//            }
//
//
//        }
//        }
        if (server.getHost() == null) {
            this.volumeCreated = false;
        }else{
            this.volumeCreated = true;
        }
    }

    public String setNetworkID(List<NetworkInfo> networkInfoList,String networkName) {
        String networkId=null;
        for (int i = 0; i < networkInfoList.size(); i++) {
            if (networkInfoList.get(i).getId().equals(networkName) ){
                networkId=networkInfoList.get(i).getId();
            }
        }
        return networkId;
    }

    public String checkState(String state){
        switch (state){
            case "build" :
                return "pending";

            case "active":
                return "running";

            case "shutoff":
                return "stopped";

            default:
                return state;
        }
    }
}
