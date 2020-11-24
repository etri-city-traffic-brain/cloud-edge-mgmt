package com.innogrid.uniq.coreopenstack.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.coreopenstack.util.JsonDateDeserializer;
import com.innogrid.uniq.coreopenstack.util.JsonDateSerializer;
import lombok.Data;
import org.openstack4j.model.storage.block.VolumeBackup;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author wss
 * @date 2019.4.12
 * @brief 오픈스택 VolumeBackup 모델
 */
@Data
public class VolumeBackupInfo implements Serializable {

    private static final long serialVersionUID = -5616747970365141475L;
    private String id;
    private String name;
    private String description;
    private String volumeId;
    private String volumeName;
    private String container;
    private Boolean incremental;
    private String zone;
    private String state;
    private int size;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Timestamp createdAt;
    private String failReason;
    private int objectCount;
    private Boolean dependent;
    private String snapshotId;

    public VolumeBackupInfo() {

    }

    public VolumeBackupInfo(VolumeBackup info) {
        this.id = info.getId();
        this.name = info.getName();
        if(this.name == null || this.name.equals("")) {
            this.name = this.id;
        }
        this.description = info.getDescription();
        this.volumeId = info.getVolumeId();
        this.container = info.getContainer();
        this.incremental = info.isIncremental();
        this.zone = info.getZone();
        this.state = info.getStatus().value();
        this.size = info.getSize();
        this.createdAt = new Timestamp(info.getCreated().getTime());
        this.failReason = info.getFailReason();
        this.objectCount = info.getObjectCount();
        this.dependent = info.hasDependent();
        this.snapshotId = info.getSnapshotId();
    }
}
