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

}
