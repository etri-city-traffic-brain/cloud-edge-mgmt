package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by wss on 19. 4. 01.
 */
@Data
public class GroupInfo implements Serializable {
    private static final long serialVersionUID = 5590909046184225659L;
    private String id;
    private String name;
    private String creator;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp createdAt;
    private String description;
    private int userCount;
    private int projectCount;

}
