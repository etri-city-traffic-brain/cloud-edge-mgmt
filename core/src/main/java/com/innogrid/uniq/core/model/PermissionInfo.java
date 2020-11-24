package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by wss on 19. 4. 03.
 */
@Data
public class PermissionInfo implements Serializable {
    private static final long serialVersionUID = 5539966064939560559L;
    private String id;
    private String roleId;
    private String type;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp createdAt;

}
