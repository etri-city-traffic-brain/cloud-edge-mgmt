package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author kkm
 * @date 2019.5.30
 * @brief Server 미터링 정보
 */
@Data
public class MeterServerInfo implements Serializable {
	private static final long serialVersionUID = -6557373535112601826L;
	private String id;
	private String instanceId;
	private String flavorId;
	private String status;
	private String cloudType;
	private String cloudName;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Timestamp createdAt;
}
