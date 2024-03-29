package com.innogrid.uniq.scheduler.job;

import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;
import com.innogrid.uniq.core.util.AES256Util;
import com.innogrid.uniq.core.util.ObjectSerializer;
import com.innogrid.uniq.coredb.service.CredentialService;
import com.innogrid.uniq.coredb.service.DashboardService;
import com.innogrid.uniq.coredb.service.MeterService;
import com.innogrid.uniq.coredb.service.UserService;
import com.innogrid.uniq.coreopenstack.model.ServerInfo;
import com.innogrid.uniq.scheduler.common.CommonProp;
import com.innogrid.uniq.scheduler.common.CommonUtils;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author kkm
 * @date 2019.5.30
 * @brief
 */
@Service
@Transactional
public class MeteringScheduler {

    private final static Logger logger = LoggerFactory.getLogger(MeteringScheduler.class);

    @Autowired
    CredentialService credentialService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @Autowired
    MeterService meterService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${scheduler.metering.period}")
    private Integer duration;

    @Autowired
    private AES256Util aes256Util;


    @Scheduled(fixedRateString = "${scheduler.metering.period}")
    private void setMeteringServer () {

        logger.info("MeteringServer Start");

        List<CredentialInfo> credentialInfoList = credentialService.getCredentialsFromMemory();
        Timestamp time = new Timestamp(new Date().getTime());
        int billing_value = 0;

        for(int i=0; i<credentialInfoList.size(); i++) {
            CredentialInfo info = credentialInfoList.get(i);
            MeterServerInfo meterServerInfo = new MeterServerInfo();
            MeterServerAccumulateInfo meterServerAccumulateInfo = new MeterServerAccumulateInfo();

            try {
                if (info.getType().equals("openstack")) {
                    UriComponentsBuilder url = UriComponentsBuilder.fromUriString(CommonProp.API_GATEWAY_URL);
                    url.path(CommonProp.OPENSTACK_PATH_LOCAL + "/servers");
                    logger.info("MeteringServer server Start");

                    List<ServerInfo> serverInfos = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtils.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(info)))), new ParameterizedTypeReference<List<ServerInfo>>(){}).getBody();

                    for (ServerInfo serverInfo : serverInfos) {
                        int billing_cpu = serverInfo.getCpu()*11000;
                        int billing_memory = (serverInfo.getMemory()/100)*1500 ;
                        int billing_disk = serverInfo.getDisk();
                        logger.info("info.getId = " + serverInfo.getId());
                        logger.info("info.getId.projectId = " + info.getProjectId());
                        logger.info("billing_cpu = " + billing_cpu);
                        logger.info("billing_memory = " + billing_memory);
                        logger.info("serverInfo.getMemory() = " + serverInfo.getMemory());

                        billing_value = billing_cpu + billing_memory;

                        // MeterServer
                        meterServerInfo.setCloudType(info.getCloudType());
                        meterServerInfo.setCloudName(info.getType());
                        meterServerInfo.setInstanceId(serverInfo.getId());
                        meterServerInfo.setFlavorId(serverInfo.getFlavorName());
                        meterServerInfo.setStatus(serverInfo.getState2());

                        // Meter Server Accumulate
                        meterServerAccumulateInfo.setCredentialId(info.getId());
//                        meterServerAccumulateInfo.setCloudType(info.getCloudType());
                        meterServerAccumulateInfo.setCloudType(info.getType());
//                        meterServerAccumulateInfo.setCloudName(info.getType());
                        meterServerAccumulateInfo.setCloudName(info.getName());
                        meterServerAccumulateInfo.setInstanceId(serverInfo.getId());
                        meterServerAccumulateInfo.setInstanceName(serverInfo.getName());
                        meterServerAccumulateInfo.setImageId(serverInfo.getImageId());
                        meterServerAccumulateInfo.setFlavorId(serverInfo.getFlavorId());
                        meterServerAccumulateInfo.setFlavorName(serverInfo.getFlavorName());
                        meterServerAccumulateInfo.setFlavorVcpu(serverInfo.getCpu());
                        meterServerAccumulateInfo.setFlavorRam(serverInfo.getMemory());
                        meterServerAccumulateInfo.setFlavorDisk(serverInfo.getDisk());
                        meterServerAccumulateInfo.setProjectId(serverInfo.getProjectId());
                        meterServerAccumulateInfo.setMeterEndTime(time);
                        meterServerAccumulateInfo.setUpdatedAt(time);
                        meterServerAccumulateInfo.setCloudTarget(info.getUrl());
                        meterServerAccumulateInfo.setState(serverInfo.getState());
                        meterServerAccumulateInfo.setBilling(billing_value);

                        int idCount = meterService.getMeterServerAccumulateIDCount(meterServerAccumulateInfo);
                        int idCount2 = meterService.getMeterServerIDCount(meterServerInfo);
                        if (idCount > 0) {
                            meterServerAccumulateInfo.setMeterDuration(duration/1000);
                            meterService.updateMeterServerAccumulate(meterServerAccumulateInfo);
                        } else {
                            meterServerAccumulateInfo.setCreatedAt(time);
                            meterServerAccumulateInfo.setMeterStartTime(time);
                            meterService.createMeterServerAccumulate(meterServerAccumulateInfo);
                        }
                        if (idCount2 > 0) {
                            meterService.updateMeterServer(meterServerInfo);
                        } else {
                            meterServerInfo.setCreatedAt(time);
                            meterService.createMeterServer(meterServerInfo);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("MeteringServer Scheduler Target : {}, Error : {}", info, e.getMessage());
            }
        }
    }

}
