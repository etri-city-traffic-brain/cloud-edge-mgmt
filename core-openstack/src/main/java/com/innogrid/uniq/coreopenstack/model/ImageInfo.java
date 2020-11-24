package com.innogrid.uniq.coreopenstack.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.coreopenstack.util.JsonDateDeserializer;
import com.innogrid.uniq.coreopenstack.util.JsonDateSerializer;
import lombok.Data;
import org.openstack4j.model.image.v2.Image;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author wss
 * @date 2019.3.21
 * @brief 오픈스택 Flavor 모델
 */
@Data
public class ImageInfo implements Serializable {

    private static final long serialVersionUID = 6636092893481923025L;
    private String id;
    private String name;
    private String type;
    private String state;
    private String visibility;
    private Boolean isProtected;
    private String diskFormat;
    private String containerFormat;
    private Long size;
    private Long minDisk;
    private Long minRam;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Timestamp createdAt;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Timestamp updatedAt;
    private String file;
    private String schema;
    private List<String> tag;
    private Long virtualSize;
    private String owner;
    private String checksum;
    private String instanceUuid;

    public ImageInfo() {

    }

    public ImageInfo(Image image) {
        if(image != null) {
            this.id = image.getId();
            this.name = image.getName();
            this.type = image.getAdditionalPropertyValue("image_type");
            if (this.type == null) this.type = "image";
            this.state = image.getStatus().value();
            this.visibility = image.getVisibility().value();
            this.isProtected = image.getIsProtected();
            if (image.getDiskFormat() != null) {
                this.diskFormat = image.getDiskFormat().value();
            }
            if (image.getContainerFormat() != null) {
                this.containerFormat = image.getContainerFormat().value();
            }
            this.size = image.getSize();
            this.minDisk = image.getMinDisk();
            this.minRam = image.getMinRam();
            this.createdAt = new Timestamp(image.getCreatedAt().getTime());
            this.updatedAt = new Timestamp(image.getUpdatedAt().getTime());
            this.file = image.getFile();
            this.schema = image.getSchema();
            this.tag = image.getTags();
            this.virtualSize = image.getVirtualSize();
            this.owner = image.getOwner();
            this.checksum = image.getChecksum();
            this.instanceUuid = image.getInstanceUuid();
        }
    }
}
