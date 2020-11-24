package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.storage.block.VolumeAttachment;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.4.11
 * @brief 오픈스택 VolumeAttachment 모델
 */
@Data
public class VolumeAttachmentInfo implements Serializable {

    private static final long serialVersionUID = -752413323884659479L;
    private String device;
    private String hostname;
    private String id;
    private String serverId;
    private String volumeId;
    private String serverName;

    public VolumeAttachmentInfo() {

    }

    public VolumeAttachmentInfo(VolumeAttachment info) {
        this.id = info.getId();
        this.device = info.getDevice();
        this.hostname = info.getHostname();
        this.serverId = info.getServerId();
        this.volumeId = info.getVolumeId();
    }
}
