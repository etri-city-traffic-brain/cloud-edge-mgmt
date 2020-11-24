package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author kkm
 * @date 2019.6.22
 * @brief 그룹에 속한 사용자들이 컴퓨트, 네트워크, 스토리지, 인증 & 권한에서 수행한 작업내용 정보를 담는 클래스
 */
@Data
public class ActionInfo {
    private String id;
    private String userName;
    private String groupId;
    private String content;
    private Constants.ACTION_RESULT result;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp createdAt;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp updatedAt;
    private String resultDetail;
    private String userId;
    private String targetId;
    private String targetName;
    private Constants.ACTION_CODE actionCode;
    private Constants.HISTORY_TYPE type;
    private Object object;
}
