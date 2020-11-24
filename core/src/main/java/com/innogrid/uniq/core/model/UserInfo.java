package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by root on 15. 3. 31.
 */
@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 4705455919358933691L;
    private String id;
    private String newId;
    private String groupId;
    private String groupName;
    private String name;
    private String password;
    private Boolean enabled;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp createdAt;
    private String email;
    private String contract;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp login;
    private int loginCount;
    private String description;
    private Boolean admin;
    private int roleCount;
    private String roleId;

}
