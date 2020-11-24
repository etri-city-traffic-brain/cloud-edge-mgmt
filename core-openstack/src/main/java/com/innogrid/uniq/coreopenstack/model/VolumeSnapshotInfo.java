package com.innogrid.uniq.coreopenstack.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.coreopenstack.util.JsonDateDeserializer;
import com.innogrid.uniq.coreopenstack.util.JsonDateSerializer;
import lombok.Data;
import org.openstack4j.model.storage.block.VolumeSnapshot;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.12
 * @brief 오픈스택 VolumeBackup 모델
 */
@Data
public class VolumeSnapshotInfo implements Serializable {

    private static final long serialVersionUID = -2957628912972766762L;
    private String id;
    private String name;
    private String description;
    private String volumeId;
    private String volumeName;
    private String state;
    private int size;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Timestamp createdAt;
    private Map<String, String> metaData;

    public VolumeSnapshotInfo() {

    }

    public VolumeSnapshotInfo(VolumeSnapshot info) {
        this.id = info.getId();
        this.name = info.getName();
        if(this.name == null || this.name.equals("")) {
            this.name = this.id;
        }
        this.description = info.getDescription();
        this.volumeId = info.getVolumeId();
        this.state = info.getStatus().value();
        this.size = info.getSize();
        this.createdAt = new Timestamp(info.getCreated().getTime());
        this.metaData = info.getMetaData();
    }
}
