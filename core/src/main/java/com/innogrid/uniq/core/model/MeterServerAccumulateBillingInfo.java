package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author ksh
 * @date 2023.4.7
 * @brief Server Accumulate 미터링 빌링 정보
 */
@Data
public class MeterServerAccumulateBillingInfo implements Serializable {
	private static final long serialVersionUID = -2084761641670557563L;
	private String id;
	private String instanceId;
	private String instanceName;
	private String flavorName;
	private int flavorVcpu;
	private int flavorRam;
	private int flavorDisk;
	private int meterDuration;
	private int billing;
}
