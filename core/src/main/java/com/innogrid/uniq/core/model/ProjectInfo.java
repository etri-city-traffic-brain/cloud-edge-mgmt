package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by wss on 19. 4. 05.
 */
@Data
public class ProjectInfo implements Serializable {
    private static final long serialVersionUID = -6872169223387323046L;
    private String id;
    private String type;
    private String projectId;
    private String projectName;
    private String description;
    private String groupId;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp createdAt;
    private String cloudId;
    private String cloudName;

}
