package com.innogrid.uniq.coreopenstack.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.coreopenstack.util.JsonDateDeserializer;
import com.innogrid.uniq.coreopenstack.util.JsonDateSerializer;
import lombok.Data;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Server;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.3.19
 * @brief 오픈스택 서버용 모델
 */
@Data
public class ServerInfo implements Serializable {

    private static final long serialVersionUID = 2897465774550361154L;
    private String id;
    private String host;
    private String name;
    private String state;
    private String imageId;
    private String imageName;
    private String flavorId;
    private String flavorName;
    private int cpu;
    private int memory;
    private int disk;
    private String powerState;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Timestamp createdAt;
    private String projectId;
    private String projectName;
    private List<String> volumes;
    private List<AddressInfo> addresses;
    private Map<String, String> metaData;
    private String keyName;
    private String taskState;
    private String state2;
    public ServerInfo() {
    }

    public ServerInfo(Server server) {
        if(server == null) return;

        this.id = server.getId();
        this.host = server.getHost();
        this.name = server.getName();
        this.state = server.getStatus().value();
        this.imageId = server.getImageId();
        if(server.getImage() != null) this.imageName = server.getImage().getName();
        this.flavorId = server.getFlavorId();
        if(server.getFlavor() != null) {
            this.flavorName = server.getFlavor().getName();
            this.cpu = server.getFlavor().getVcpus();
            this.memory = server.getFlavor().getRam();
            this.disk = server.getFlavor().getDisk();
        }
        this.metaData = server.getMetadata();

        Iterator<String> keys = server.getAddresses().getAddresses().keySet().iterator();
        List<AddressInfo> list = new ArrayList<>();

        while (keys.hasNext()) {
            String key = keys.next();

            List<? extends Address> addresses = server.getAddresses().getAddresses().get(key);

            for(int i=0; i<addresses.size(); i++) {
                AddressInfo info = new AddressInfo(addresses.get(i));
                info.setNetworkName(key);
                list.add(info);
            }
        }
        this.addresses = list;
        this.projectId = server.getTenantId();

        this.powerState = server.getPowerState();
        this.createdAt = new Timestamp(server.getCreated().getTime());
        this.volumes = server.getOsExtendedVolumesAttached();
        this.keyName = server.getKeyName();
        this.taskState = server.getTaskState();
    }

    public AddressInfo getAddressInfo(String addr) {
        for(int i=0; i<this.addresses.size(); i++) {
            if(addresses.get(i).getAddr().equals(addr)) {
                return addresses.get(i);
            }
        }

        return null;
    }
}
