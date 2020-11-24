package com.innogrid.uniq.coreopenstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.coreopenstack.util.JsonDateDeserializer;
import com.innogrid.uniq.coreopenstack.util.JsonDateSerializer;
import lombok.Data;
import org.openstack4j.model.storage.block.Volume;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wss
 * @date 2019.3.22
 * @brief 오픈스택 Volume 모델
 */
@Data
public class VolumeInfo2 implements Serializable {

    private static final long serialVersionUID = -7056474800842404026L;
    private String id;
    private String name;
    private String description;
    private String volumeType;
    @JsonProperty("volumeState")
    private String state;
    @JsonProperty("volumeSize")
    private int size;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Timestamp createdAt;
//    private String zone;
//    private Boolean bootable;
//
//    private String projectId;in
//    private String projectName;
//    private List<VolumeAttachmentInfo> attachmentInfos;
//    private Map<String, String> metaData;
    public VolumeInfo2() {

    }

    public VolumeInfo2(Volume info) {
        if(info != null) {
            this.id = info.getId();
            this.name = info.getName();
            if (this.name == null || this.name.equals("")) {
                this.name = this.id;
            }

            this.volumeType = info.getVolumeType();

            this.description = info.getDescription();
            this.state = checkState(info.getStatus().value());
            this.size = info.getSize();
//            this.zone = info.getZone();
//            this.bootable = info.bootable();
            this.createdAt = new Timestamp(info.getCreated().getTime());
//            this.projectId = info.getTenantId();
//            this.metaData = info.getMetaData();
            List<VolumeAttachmentInfo> volumeAttachmentInfos = new ArrayList<>();

            for (int i = 0; i < info.getAttachments().size(); i++) {
                VolumeAttachmentInfo volumeAttachmentInfo = new VolumeAttachmentInfo(info.getAttachments().get(i));
                volumeAttachmentInfos.add(volumeAttachmentInfo);
            }
//            this.attachmentInfos = volumeAttachmentInfos;
        }
    }

    public String checkState(String state){
        switch (state){
            case "creating" :
                return "pending";

            case "unrecognized":
                return "notUsing";

            case "deleting":
                return "deallocating";

            default:
                return state;
        }
    }

//    public void setServerNameForVolumeAttachmentInfos(List<ServerInfo> servers) {
//        for(int i=0; i<this.attachmentInfos.size(); i++) {
//            VolumeAttachmentInfo info = this.attachmentInfos.get(i);
//            List<ServerInfo> result = servers.stream().filter(server -> server.getId().equals(info.getServerId())).collect(Collectors.toList());
//            if(result.size() > 0) {
//                info.setServerName( result.get(0).getName());
//            }
//        }
//    }
}
