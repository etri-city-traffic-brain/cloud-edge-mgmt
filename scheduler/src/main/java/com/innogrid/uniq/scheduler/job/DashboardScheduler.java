package com.innogrid.uniq.scheduler.job;

import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.ServiceDashboardInfo;
import com.innogrid.uniq.core.util.AES256Util;
import com.innogrid.uniq.core.util.ObjectSerializer;
import com.innogrid.uniq.coredb.service.CredentialService;
import com.innogrid.uniq.coredb.service.DashboardService;
import com.innogrid.uniq.coreopenstack.model.ResourceInfo;
import com.innogrid.uniq.scheduler.common.CommonProp;
import com.innogrid.uniq.scheduler.common.CommonUtils;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kkm
 * @date 2019.4.24
 * @brief
 */
@Service
@Transactional
public class DashboardScheduler {

    private final static Logger logger = LoggerFactory.getLogger(DashboardScheduler.class);

    @Autowired
    CredentialService credentialService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AES256Util aes256Util;

    @Scheduled(fixedRateString = "${scheduler.dashboard.service.period}")
    private void setServiceDashboard () {
        List<CredentialInfo> credentialInfoList = credentialService.getCredentialsFromMemory();
        Timestamp time = new Timestamp(new Date().getTime());

        for(int i=0; i<credentialInfoList.size(); i++) {
            CredentialInfo info = credentialInfoList.get(i);
            int idCount = dashboardService.getIDCount(info.getId());

            ServiceDashboardInfo dashboardInfo = new ServiceDashboardInfo();

            try {
                if (info.getType().equals("openstack")) {
                    UriComponentsBuilder url = UriComponentsBuilder.fromUriString(CommonProp.API_GATEWAY_URL);
                    url.path(CommonProp.OPENSTACK_PATH_LOCAL + "/resource");

                    ResourceInfo resourceInfo = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtils.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(info)))), new ParameterizedTypeReference<ResourceInfo>() {
                    }).getBody();
                    dashboardInfo.setId(info.getId());
                    dashboardInfo.setType(info.getType());
                    dashboardInfo.setTotalServer(resourceInfo.getServers());
                    dashboardInfo.setRunningServer(resourceInfo.getRunning());
                    dashboardInfo.setStoppedServer(resourceInfo.getStop());
                    dashboardInfo.setEtcServer(resourceInfo.getEtc());
                    dashboardInfo.setAccount(resourceInfo.getProjects());
                    dashboardInfo.setNetwork(resourceInfo.getNetworks());
                    dashboardInfo.setPublicIp(resourceInfo.getFloatingIps());
                    dashboardInfo.setSecurityGroup(resourceInfo.getSecurityGroups());
                    dashboardInfo.setHypervisorVcpus(resourceInfo.getHypervisorVcpus());
                    dashboardInfo.setHypervisorVcpusUsed(resourceInfo.getHypervisorVcpusUsed());
                    dashboardInfo.setHypervisorMemory(resourceInfo.getHypervisorMemory());
                    dashboardInfo.setHypervisorMemoryUsed(resourceInfo.getHypervisorMemoryUsed());
                    dashboardInfo.setHypervisorDisk(resourceInfo.getHypervisorDisk());
                    dashboardInfo.setHypervisorDiskUsed(resourceInfo.getHypervisorDiskUsed());

                    dashboardInfo.setImage(resourceInfo.getImages());
                    dashboardInfo.setFlavor(resourceInfo.getFlavor());
                    dashboardInfo.setKeypair(resourceInfo.getKeyPairs());
                    dashboardInfo.setVolume(resourceInfo.getVolumes());
                    dashboardInfo.setBackup(resourceInfo.getBackups());
                    dashboardInfo.setSnapshot(resourceInfo.getSnapshots());
                    dashboardInfo.setRouter(resourceInfo.getRouters());

                }

                dashboardInfo.setLastUpdatedAt(time);

                if (idCount > 0) {
                    dashboardService.updateServiceDashboard(dashboardInfo);
                } else {
                    dashboardService.createServiceDashboard(dashboardInfo);
                }

            } catch (Exception e) {
                logger.error("ServiceDashboard Scheduler Target : {}, Error : {}", info, e.getMessage());
            }
        }
    }

}