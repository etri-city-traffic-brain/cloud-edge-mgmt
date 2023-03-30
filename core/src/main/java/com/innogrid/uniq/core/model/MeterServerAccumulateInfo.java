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
 * @brief Server Accumulate 미터링 정보
 */
@Data
public class MeterServerAccumulateInfo implements Serializable {
	private static final long serialVersionUID = -2084761641670557563L;
	private String id;
	private String credentialId;
	private String cloudType;
	private String cloudName;
	private String projectId;
	private String instanceId;
	private String instanceName;
	private String imageId;
	private String flavorId;
	private String flavorName;
	private String State;
	private int flavorVcpu;
	private int flavorRam;
	private int flavorDisk;
	private int meterDuration;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Timestamp meterStartTime;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Timestamp meterEndTime;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Timestamp createdAt;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Timestamp updatedAt;
	private String cloudTarget;
	private int billing;
}
