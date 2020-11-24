package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateSerializer;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by root on 15. 4. 3.
 */
public class HistoryInfo implements Serializable {

    private static final long serialVersionUID = 6069391975423501786L;
    private String id;
    private String userId;
    private String userName;
    private String action;
    private String result;
    private String content;
    private String target;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp createdAt;
    private String ip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "HistoryInfo{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", action='" + action + '\'' +
                ", result='" + result + '\'' +
                ", content='" + content + '\'' +
                ", target='" + target + '\'' +
                ", createdAt=" + createdAt +
                ", ip='" + ip + '\'' +
                '}';
    }
}
