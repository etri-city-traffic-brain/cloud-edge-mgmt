package com.innogrid.uniq.coreopenstack.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.coreopenstack.util.JsonDateDeserializer;
import com.innogrid.uniq.coreopenstack.util.JsonDateSerializer;
import lombok.Data;
import org.openstack4j.model.compute.InstanceAction;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author wss
 * @date 2019.4.11
 * @brief 오픈스택 서버 Action Log 모델
 */
@Data
public class ActionLogInfo implements Serializable {
    private static final long serialVersionUID = -8720470684604054121L;
    private String action;
    private String requestId;
    private String instanceUuid;
    private String message;
    private String projectId;
    private String userId;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Timestamp startDate;

    public ActionLogInfo() {
    }

    public ActionLogInfo(InstanceAction info) {
        this.action = info.getAction();
        this.requestId = info.getRequestId();
        this.instanceUuid = info.getInstanceUuid();
        this.message = info.getMessage();
        this.projectId = info.getProjectId();
        this.userId = info.getUserId();
        this.startDate = new Timestamp(info.getStartTime().getTime());
    }
}
