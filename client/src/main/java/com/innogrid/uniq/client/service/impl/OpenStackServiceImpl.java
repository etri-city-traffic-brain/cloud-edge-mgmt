package com.innogrid.uniq.client.service.impl;

import com.innogrid.uniq.client.service.OpenStackService;
import com.innogrid.uniq.client.util.CommonUtil;
import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.core.util.AES256Util;
import com.innogrid.uniq.core.util.ObjectSerializer;
import com.innogrid.uniq.coredb.dao.ProjectDao;
import com.innogrid.uniq.coredb.service.ActionService;
import com.innogrid.uniq.coredb.service.CredentialService;
import com.innogrid.uniq.coreopenstack.model.*;
import fi.evident.dalesbred.Transactional;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

/**
 * @author wss
 * @date 2019.3.19
 * @brief
 */
@Service
@Transactional
public class OpenStackServiceImpl implements OpenStackService {
    private final static Logger logger = LoggerFactory.getLogger(OpenStackServiceImpl.class);
//    private final static String PATH = "";
    public final static String API_PATH = "/openstack/infra/cloudServices/openstack";   // localhost
//    public final static String API_PATH_public = "/api/cloudServices/openstack";     // public
    public final static String API_PATH_public = "/cloudServices/openstack";     // public

    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate; // InfluxDB 조회 템플릿

    @Autowired
    private ProjectDao projectDao;

    @Autowired(required = false)
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${apigateway.url}")
    private String apiUrl_public;

    @Value("${apigateway_local.url}")
    private String apiUrl;


    @Autowired
    private ActionService actionService;

    @Autowired
    private AES256Util aes256Util;

    private com.innogrid.uniq.core.model.ProjectInfo getProjectByGroupId(String cloudId, String groupId) {
        logger.info("[{}] Get Project By GroupId", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo info = projectDao.getProjectInfo(new HashMap<String, Object>(){{
            put("groupId", groupId);
            put("type", "openstack");
            put("cloudId", cloudId);
        }});

        logger.info("[{}] Get Project By GroupId Complete", CommonUtil.getUserUUID());
        return info;
    }

    private void sendMessage(String action, String reqUser, Object data) {
        CommonUtil.sendMessage(simpMessagingTemplate, API_PATH, action, reqUser, data);
    }

    @Override
    public List<ServerInfo> getServers(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Servers", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/servers");
//        url.path(API_PATH_public + "/servers");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);
        if(credentialInfo == null) {
            params.put("id", cloudId);
            credentialInfo = credentialService.getCredentialInfo(params);
        }

        url.queryParam("webCheck", true);

        List<ServerInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<ServerInfo>>(){}).getBody();

        logger.info("[{}] OpenStack Credential : ", CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token));
        logger.info("[{}] Get Servers Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();
        return lists;
    }

    @Override
    public List<ServerInfo> getServers_Detail_openstack(String cloudId, String ServerId, String token) {
        logger.info("nServerId_openstack1 = '{}'", ServerId);
        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/servers/" + ServerId);
//        url.path(API_PATH_public + "/servers/" + ServerId);

//        List<ServerInfo> info = null;

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        url.queryParam("webCheck", true);
//        List<NetworkInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<NetworkInfo>>(){}).getBody();
        List<ServerInfo> info = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<ServerInfo>>(){}).getBody();

        logger.info("openstack_detail_lists = '{}'", info);
        if(info == null) info = new ArrayList<>();

        return info;
    }


    @Override
    public void updateServer(String cloudId, ServerInfo info, String command, UserInfo reqInfo, String token) {

    }

    @Override
    public ServerInfo getServer(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Server : '{}'", CommonUtil.getUserUUID(), id);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
//        url.path(API_PATH + "/servers/{id}");
        url.path(API_PATH_public + "/servers/{id}");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ServerInfo info = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();


        logger.info("[{}] Get Server Complete : '{}", CommonUtil.getUserUUID(), info.getId());
        if(info == null) info = new ServerInfo();

        return info;
    }

    @Override
    public List<ImageInfo> getImages(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Images", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/images");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        if(params.get("active") != null) {
            url.queryParam("active", params.get("active"));
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<ImageInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<ImageInfo>>(){}).getBody();

        logger.info("[{}] Get Images Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<KeyPairInfo> getKeyPairs(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get KeyPairs", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/keypairs");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<KeyPairInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<KeyPairInfo>>(){}).getBody();

        logger.info("[{}] Get KeyPairs Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<FlavorInfo> getFlavors(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Flavors", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/flavors");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<FlavorInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<FlavorInfo>>(){}).getBody();

        logger.info("[{}] Get Flavors Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<VolumeInfo> getVolumes(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Volumes", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/volumes");
//        url.path(API_PATH_public + "/volumes");

        if(projectInfo != null) {
            logger.error("-------- #client, openstackServiceimpl, getVolumes, projectInfo ?? : {}", projectInfo);
            logger.error("-------- #client, openstackServiceimpl, getVolumes, projectInfo.getProjectId()) ?? : {}", projectInfo.getProjectId());
            url.queryParam("project", projectInfo.getProjectId());
        }
        if(params.get("bootable") != null) {
            logger.error("-------- #client, openstackServiceimpl, getVolumes, bootable ?? : {}", params.get("bootable"));
            url.queryParam("bootable", params.get("bootable"));
        }
        if(params.get("available") != null) {
            logger.error("-------- #client, openstackServiceimpl, getVolumes, available ?? : {}", params.get("available"));
            url.queryParam("available", params.get("available"));
        }


        logger.error("-------- client, openstackServiceimpl, getVolumes, projectInfo ?? : {}", projectInfo);
//        logger.error("-------- client, openstackServiceimpl, getVolumes, projectInfo.getProjectId()) ?? : {}", projectInfo.getProjectId());
        logger.error("-------- client, openstackServiceimpl, getVolumes, bootable ?? : {}", params.get("bootable"));
        logger.error("-------- client, openstackServiceimpl, getVolumes, available ?? : {}", params.get("available"));


        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        url.queryParam("webCheck", true);

        List<VolumeInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<VolumeInfo>>(){}).getBody();

        logger.info("[{}] Get Volumes Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public Object getVolumeTypes(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Volumes", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/volumeTypes");

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        Object lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<Object>(){}).getBody();

        logger.info("[{}] Get Volumes Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<VolumeBackupInfo> getBackups(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Volume Backups", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/backups");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<VolumeBackupInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<VolumeBackupInfo>>(){}).getBody();

        logger.info("[{}] Get Volume Backups Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<VolumeSnapshotInfo> getSnapshots(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Volume Snapshots", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/snapshots");
//        url.path(API_PATH_public + "/snapshots");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }
        if(params.get("available") != null) {
            url.queryParam("available", params.get("available"));
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<VolumeSnapshotInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<VolumeSnapshotInfo>>(){}).getBody();

        logger.info("[{}] Get Volume Snapshots Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }


    @Override
    public VolumeSnapshotInfo deleteSnapshot(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Delete Volume Snapshot : '{}'", CommonUtil.getUserUUID(), id);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        VolumeSnapshotInfo volumeSnapshot = null;

        Runnable function = null;

        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl_public);
        url10.path(API_PATH + "/snapshots/{id}/delete");
//        url10.path(API_PATH_public + "/snapshots/{id}/delete");
        if(projectInfo != null) {
            url10.queryParam("project", projectInfo.getProjectId());
        }
        volumeSnapshot = restTemplate.exchange(url10.buildAndExpand(id).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeSnapshotInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), volumeSnapshot.toString(), id,
                volumeSnapshot.getName(), Constants.ACTION_CODE.SNAPSHOT_DELETE, Constants.HISTORY_TYPE.OPENSTACK);

        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Delete Volume Snapshot Complete : '{}'", CommonUtil.getUserUUID(), volumeSnapshot.toString());
        return volumeSnapshot;
    }

    @Override
    public List<NetworkInfo> getNetworks(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Networks", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/networks");
//        url.path(API_PATH_public + "/networks");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        if(projectInfo == null && params.get("project") != null && ((Boolean) params.get("project")) == Boolean.TRUE) {
            url.queryParam("project", credentialInfo.getProjectId());
        }

        url.queryParam("webCheck", true);

        List<NetworkInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<NetworkInfo>>(){}).getBody();

        logger.info("[{}] Get Networks Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public NetworkInfo getNetworks_Detail_openstack(String cloudId, String networkId, String token) {
        logger.info("networkId_openstack1 = '{}'", networkId);
        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/networks/" + networkId);
//        url.path(API_PATH_public + "/networks/" + networkId);

        NetworkInfo info = null;

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        url.queryParam("webCheck", true);

//        List<NetworkInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<NetworkInfo>>(){}).getBody();
        info = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<NetworkInfo>(){}).getBody();

        logger.info("openstack_detail_lists = '{}'", info);

        return info;
    }

    @Override
    public List<SubnetInfo> getSubnets(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Subnets", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/networks/{id}/subnets");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        if(projectInfo == null && params.get("project") != null && ((Boolean) params.get("project")) == Boolean.TRUE) {
            url.queryParam("project", credentialInfo.getProjectId());
        }

        List<SubnetInfo> lists = restTemplate.exchange(url.buildAndExpand(params.get("networkId")).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<SubnetInfo>>(){}).getBody();

        logger.info("[{}] Get Subnets Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<RouterInfo> getRouters(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Routers", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/routers");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<RouterInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<RouterInfo>>(){}).getBody();

        logger.info("[{}] Get Routers Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<SecurityGroupInfo> getSecurityGroups(String cloudId, Map<String, Object> params, Boolean project, UserInfo reqInfo, String token) {
        logger.info("[{}] Get SecurityGroups", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());


        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/securitygroups");

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        if(projectInfo != null) {
            logger.error("projectInfo != null");
            url.queryParam("project", projectInfo.getProjectId());
        } else if(project) {
            logger.error("projectInfo == null, project true");
            url.queryParam("project", credentialInfo.getProjectId());
        }

        List<SecurityGroupInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<SecurityGroupInfo>>(){}).getBody();

        logger.info("[{}] Get SecurityGroups Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();
        return lists;
    }

    @Override
    public List<FloatingIpInfo> getFloatingIps(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get FloatingIps", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/floatingips");

        if(params.get("projectId") != null) {
            url.queryParam("project", (String) params.get("projectId"));
        } else if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        if(params.get("down") != null) {
            url.queryParam("down", (Boolean) params.get("down"));
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<FloatingIpInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<FloatingIpInfo>>(){}).getBody();

        logger.info("[{}] Get FloatingIps Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<AvailabilityZoneInfo> getZones(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Availability Zone", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/zones");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }
        if(params.get("type") != null) {
            url.queryParam("type", (String)params.get("type"));
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<AvailabilityZoneInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<AvailabilityZoneInfo>>(){}).getBody();

        logger.info("[{}] Get Availability Zone Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<ProjectInfo> getProjects(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Projects", CommonUtil.getUserUUID());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/projects");

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<ProjectInfo> lists = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<ProjectInfo>>(){}).getBody();

        logger.info("[{}] Get Projects Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public ServerInfo action(String cloudId, String serverId, String action, UserInfo reqInfo, String token) {
        logger.info("[{}] Execute Action to Server : '{}'", CommonUtil.getUserUUID(), action);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
        url1.path(API_PATH + "/servers/{id}");
        if(projectInfo != null) {
            url1.queryParam("project", projectInfo.getProjectId());
        }
        URI url = url1.buildAndExpand(serverId).toUri();

        ServerInfo server = null;

        Runnable function = null;

        switch (action) {
            case "START":
                UriComponentsBuilder url2 = UriComponentsBuilder.fromUriString(apiUrl);
                url2.path(API_PATH + "/servers/{id}/start");
                if(projectInfo != null) {
                    url2.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url2.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId1 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                        server.getName(), Constants.ACTION_CODE.SERVER_START, Constants.HISTORY_TYPE.OPENSTACK);

                function = () -> {

                    int duration = 0;
                    ServerInfo info = null;

                    actionService.setActionResult(actionId1, Constants.ACTION_RESULT.PROGRESSING);

                    while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                        CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                        info = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                        sendMessage(action, reqInfo.getId(), info);

                        if (info == null || info.getState().equals("active") || info.getState().equals("error")) {
                            if(info.getState().equals("active")) {
                                actionService.setActionResult(actionId1, Constants.ACTION_RESULT.SUCCESS);
                            } else {
                                actionService.setActionResult(actionId1, Constants.ACTION_RESULT.FAILED);
                            }
                            break;
                        }

                        duration += 1;
                    }
                };
                break;
            case "REBOOT_SOFT":
                UriComponentsBuilder url3 = UriComponentsBuilder.fromUriString(apiUrl);
                url3.path(API_PATH + "/servers/{id}/reboot");
                if(projectInfo != null) {
                    url3.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url3.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId2 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                        server.getName(), Constants.ACTION_CODE.SERVER_REBOOT_SOFT, Constants.HISTORY_TYPE.OPENSTACK);

                function = () -> {

                    int duration = 0;
                    ServerInfo info = null;

                    actionService.setActionResult(actionId2, Constants.ACTION_RESULT.PROGRESSING);

                    while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                        CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                        info = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                        sendMessage(action, reqInfo.getId(), info);

                        if (info == null || info.getState().equals("active") || info.getState().equals("error")) {
                            if(info.getState().equals("active")) {
                                actionService.setActionResult(actionId2, Constants.ACTION_RESULT.SUCCESS);
                            } else {
                                actionService.setActionResult(actionId2, Constants.ACTION_RESULT.FAILED);
                            }
                            break;
                        }

                        duration += 1;
                    }
                };
                break;
            case "REBOOT_HARD":
                UriComponentsBuilder url4 = UriComponentsBuilder.fromUriString(apiUrl);
                url4.path(API_PATH + "/servers/{id}/reboot");
                if(projectInfo != null) {
                    url4.queryParam("project", projectInfo.getProjectId());
                }
                url4.queryParam("hard", true);
                server = restTemplate.exchange(url4.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId3 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                        server.getName(), Constants.ACTION_CODE.SERVER_REBOOT_HARD, Constants.HISTORY_TYPE.OPENSTACK);

                function = () -> {

                    int duration = 0;
                    ServerInfo info = null;

                    actionService.setActionResult(actionId3, Constants.ACTION_RESULT.PROGRESSING);

                    while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                        CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                        info = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                        sendMessage(action, reqInfo.getId(), info);

                        if (info == null || info.getState().equals("active") || info.getState().equals("error")) {
                            if(info.getState().equals("active")) {
                                actionService.setActionResult(actionId3, Constants.ACTION_RESULT.SUCCESS);
                            } else {
                                actionService.setActionResult(actionId3, Constants.ACTION_RESULT.FAILED);
                            }
                            break;
                        }

                        duration += 1;
                    }
                };
                break;
            case "UNPAUSE":
                UriComponentsBuilder url5 = UriComponentsBuilder.fromUriString(apiUrl);
                url5.path(API_PATH + "/servers/{id}/unpause");
                if(projectInfo != null) {
                    url5.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url5.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId4 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                        server.getName(), Constants.ACTION_CODE.SERVER_UNPAUSE, Constants.HISTORY_TYPE.OPENSTACK);

                function = () -> {

                    int duration = 0;
                    ServerInfo info = null;

                    actionService.setActionResult(actionId4, Constants.ACTION_RESULT.PROGRESSING);

                    while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                        CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                        info = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                        sendMessage(action, reqInfo.getId(), info);

                        if (info == null || info.getState().equals("active") || info.getState().equals("error")) {
                            if(info.getState().equals("active")) {
                                actionService.setActionResult(actionId4, Constants.ACTION_RESULT.SUCCESS);
                            } else {
                                actionService.setActionResult(actionId4, Constants.ACTION_RESULT.FAILED);
                            }
                            break;
                        }

                        duration += 1;
                    }
                };
                break;
            case "RESUME":
                UriComponentsBuilder url6 = UriComponentsBuilder.fromUriString(apiUrl);
                url6.path(API_PATH + "/servers/{id}/resume");
                if(projectInfo != null) {
                    url6.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url6.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId5 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                        server.getName(), Constants.ACTION_CODE.SERVER_RESUME, Constants.HISTORY_TYPE.OPENSTACK);

                function = () -> {

                    int duration = 0;
                    ServerInfo info = null;

                    actionService.setActionResult(actionId5, Constants.ACTION_RESULT.PROGRESSING);

                    while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                        CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                        info = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                        sendMessage(action, reqInfo.getId(), info);

                        if (info == null || info.getState().equals("active") || info.getState().equals("error")) {
                            if(info.getState().equals("active")) {
                                actionService.setActionResult(actionId5, Constants.ACTION_RESULT.SUCCESS);
                            } else {
                                actionService.setActionResult(actionId5, Constants.ACTION_RESULT.FAILED);
                            }
                            break;
                        }

                        duration += 1;
                    }
                };
                break;
            case "STOP":
                UriComponentsBuilder url7 = UriComponentsBuilder.fromUriString(apiUrl);
                url7.path(API_PATH + "/servers/{id}/stop");
                if(projectInfo != null) {
                    url7.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url7.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId6 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                        server.getName(), Constants.ACTION_CODE.SERVER_SHUTDOWN, Constants.HISTORY_TYPE.OPENSTACK);

                function = () -> {

                    int duration = 0;
                    ServerInfo info = null;

                    actionService.setActionResult(actionId6, Constants.ACTION_RESULT.PROGRESSING);

                    while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                        CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                        info = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                        sendMessage(action, reqInfo.getId(), info);

                        if (info == null || info.getState().equals("shutoff") || info.getState().equals("error")) {
                            if(info.getState().equals("shutoff")) {
                                actionService.setActionResult(actionId6, Constants.ACTION_RESULT.SUCCESS);
                            } else {
                                actionService.setActionResult(actionId6, Constants.ACTION_RESULT.FAILED);
                            }
                            break;
                        }

                        duration += 1;
                    }
                };
                break;
            case "PAUSE":
                UriComponentsBuilder url8 = UriComponentsBuilder.fromUriString(apiUrl);
                url8.path(API_PATH + "/servers/{id}/pause");
                if(projectInfo != null) {
                    url8.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url8.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId7 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                        server.getName(), Constants.ACTION_CODE.SERVER_PAUSE, Constants.HISTORY_TYPE.OPENSTACK);

                function = () -> {

                    int duration = 0;
                    ServerInfo info = null;

                    actionService.setActionResult(actionId7, Constants.ACTION_RESULT.PROGRESSING);

                    while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                        CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                        info = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                        sendMessage(action, reqInfo.getId(), info);

                        if (info == null || info.getState().equals("paused") || info.getState().equals("error")) {
                            if(info.getState().equals("paused")) {
                                actionService.setActionResult(actionId7, Constants.ACTION_RESULT.SUCCESS);
                            } else {
                                actionService.setActionResult(actionId7, Constants.ACTION_RESULT.FAILED);
                            }
                            break;
                        }
                        duration += 1;
                    }
                };
                break;
            case "SUSPEND":
                UriComponentsBuilder url9 = UriComponentsBuilder.fromUriString(apiUrl);
                url9.path(API_PATH + "/servers/{id}/suspend");
                if(projectInfo != null) {
                    url9.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url9.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId8 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                        server.getName(), Constants.ACTION_CODE.SERVER_SUSPEND, Constants.HISTORY_TYPE.OPENSTACK);

                function = () -> {

                    int duration = 0;
                    ServerInfo info = null;

                    actionService.setActionResult(actionId8, Constants.ACTION_RESULT.PROGRESSING);

                    while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                        CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                        info = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                        sendMessage(action, reqInfo.getId(), info);

                        if (info == null || info.getState().equals("suspended") || info.getState().equals("error")) {
                            if(info.getState().equals("suspended")) {
                                actionService.setActionResult(actionId8, Constants.ACTION_RESULT.SUCCESS);
                            } else {
                                actionService.setActionResult(actionId8, Constants.ACTION_RESULT.FAILED);
                            }
                            break;
                        }

                        duration += 1;
                    }
                };
                break;
            case "DELETE":
//                UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl);
                UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl_public);
//                url10.path(API_PATH + "/servers/{id}/delete");
                url10.path(API_PATH + "/servers/{id}");
//                url10.path(API_PATH_public + "/servers/{id}");
                if(projectInfo != null) {
                    url10.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url10.buildAndExpand(serverId).toUri(), HttpMethod.DELETE, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId9 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), serverId,
                        server.getName(), Constants.ACTION_CODE.SERVER_DELETE, Constants.HISTORY_TYPE.OPENSTACK);

                actionService.setActionResult(actionId9, Constants.ACTION_RESULT.SUCCESS);

                break;
            case "LOCK":
                UriComponentsBuilder url11 = UriComponentsBuilder.fromUriString(apiUrl);
                url11.path(API_PATH + "/servers/{id}/lock");
                if(projectInfo != null) {
                    url11.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url11.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId10 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), serverId,
                        server.getName(), Constants.ACTION_CODE.SERVER_LOCK, Constants.HISTORY_TYPE.OPENSTACK);

                actionService.setActionResult(actionId10, Constants.ACTION_RESULT.SUCCESS);
                break;
            case "UNLOCK":
                UriComponentsBuilder url12 = UriComponentsBuilder.fromUriString(apiUrl);
                url12.path(API_PATH + "/servers/{id}/unlock");
                if(projectInfo != null) {
                    url12.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url12.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId11 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), serverId,
                        server.getName(), Constants.ACTION_CODE.SERVER_UNLOCK, Constants.HISTORY_TYPE.OPENSTACK);

                actionService.setActionResult(actionId11, Constants.ACTION_RESULT.SUCCESS);
                break;
            case "RESCUE":
                UriComponentsBuilder url13 = UriComponentsBuilder.fromUriString(apiUrl);
                url13.path(API_PATH + "/servers/{id}/rescue");
                if(projectInfo != null) {
                    url13.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url13.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId12 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), serverId,
                        server.getName(), Constants.ACTION_CODE.SERVER_RESCUE, Constants.HISTORY_TYPE.OPENSTACK);

                actionService.setActionResult(actionId12, Constants.ACTION_RESULT.SUCCESS);
                break;
            case "UNRESCUE":
                UriComponentsBuilder url14 = UriComponentsBuilder.fromUriString(apiUrl);
                url14.path(API_PATH + "/servers/{id}/unrescue");
                if(projectInfo != null) {
                    url14.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url14.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId13 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), serverId,
                        server.getName(), Constants.ACTION_CODE.SERVER_UNRESCUE, Constants.HISTORY_TYPE.OPENSTACK);

                actionService.setActionResult(actionId13, Constants.ACTION_RESULT.SUCCESS);
                break;
            case "SHELVE":
                UriComponentsBuilder url15 = UriComponentsBuilder.fromUriString(apiUrl);
                url15.path(API_PATH + "/servers/{id}/shelve");
                if(projectInfo != null) {
                    url15.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url15.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId14 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), serverId,
                        server.getName(), Constants.ACTION_CODE.SERVER_SHELVE, Constants.HISTORY_TYPE.OPENSTACK);

                actionService.setActionResult(actionId14, Constants.ACTION_RESULT.SUCCESS);
                break;
            case "SHELVE_OFFLOAD":
                UriComponentsBuilder url16 = UriComponentsBuilder.fromUriString(apiUrl);
                url16.path(API_PATH + "/servers/{id}/shelveoffload");
                if(projectInfo != null) {
                    url16.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url16.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId15 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), serverId,
                        server.getName(), Constants.ACTION_CODE.SERVER_SHELVE_OFFLOAD, Constants.HISTORY_TYPE.OPENSTACK);

                actionService.setActionResult(actionId15, Constants.ACTION_RESULT.SUCCESS);
                break;
            case "UNSHELVE":
                UriComponentsBuilder url17 = UriComponentsBuilder.fromUriString(apiUrl);
                url17.path(API_PATH + "/servers/{id}/unshelve");
                if(projectInfo != null) {
                    url17.queryParam("project", projectInfo.getProjectId());
                }
                server = restTemplate.exchange(url17.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                String actionId16 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), serverId,
                        server.getName(), Constants.ACTION_CODE.SERVER_UNSHELVE, Constants.HISTORY_TYPE.OPENSTACK);

                actionService.setActionResult(actionId16, Constants.ACTION_RESULT.SUCCESS);
                break;
        }

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Execute Action to Server Complete : '{}'", CommonUtil.getUserUUID(), action);
        return server;
    }



    @Override
    public ServerInfo createServer(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token) {
        logger.info("[{}] Create Server", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/servers");
//        url.path(API_PATH_public + "/servers");

        UriComponentsBuilder url2 = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url2 = UriComponentsBuilder.fromUriString(apiUrl_public);
        url2.path(API_PATH + "/servers/{id}");
//        url2.path(API_PATH_public + "/servers/{id}");
        url.queryParam("webCheck", true);

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
            url2.queryParam("project", projectInfo.getProjectId());
        }

        logger.info("[{}] createData is ", createData);

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ServerInfo server = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.POST, new HttpEntity(createData, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                server.getName(), Constants.ACTION_CODE.SERVER_CREATE, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            ServerInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

//                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));
                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (CommonUtil.MAX_RETRY_COUNT - duration));

                info = restTemplate.exchange(url2.buildAndExpand(server.getId()).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                sendMessage("SERVER_CREATE", reqInfo.getId(), info);

                if (info == null || info.getState().equals("active") || info.getState().equals("error")) {
                    if(info.getState().equals("active")) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Create Server Complete : '{}'", CommonUtil.getUserUUID(), server.getId());
        return server;
    }

    @Override
    public String createServerSnapshot(String cloudId, String id, String snapshotName, UserInfo reqInfo, String token) {
        logger.info("[{}] Create Server Snapshot", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/snapshot");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        String snapshotId = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{put("name", snapshotName);}}, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<Map<String, String>>(){}).getBody().get("imageId");

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), snapshotId, snapshotId,
                snapshotName, Constants.ACTION_CODE.SNAPSHOT_CREATE, Constants.HISTORY_TYPE.OPENSTACK);

        if(snapshotId != null) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        logger.info("[{}] Create Server Snapshot Complete", CommonUtil.getUserUUID());

        return snapshotId;
    }

    @Override
    public String getServerVNCConsoleURL(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Server VNCConsole URL", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/console");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        String consoleUrl = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<Map<String, String>>(){}).getBody().get("url");

        logger.info("[{}] Get Server VNCConsole URL Complete", CommonUtil.getUserUUID());
        return consoleUrl;
    }

    @Override
    public Object getServerMetric(String cloudId, String id, UserInfo reqInfo, Map<String, Object> params, String token) {
        logger.info("[{}] Get Server Metric", CommonUtil.getUserUUID());
//        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/metric");

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            url.queryParam(entry.getKey(), entry.getValue());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        Object serverMetric = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<Map<String, Object>>(){}).getBody();
        logger.info("[{}] Get Server Metric Complete", CommonUtil.getUserUUID());
        return serverMetric;
    }

    @Override
    public Map<String, Object> getInfluxMetric(Map<String, Object> params, String token) {

        String MEAN_FUNCTION = "mean";

        String vmId = params.get("vmId").toString();
        String metricName = params.get("metricName").toString();
        String interval = params.get("interval").toString();
        String hour = params.get("hour").toString();
        String statistic = params.get("statistic").toString();
        String measurement = "";

        String function = MEAN_FUNCTION;
        if (!statistic.equals("")) {
            function = statistic;
        }

        List<String> chartType = new ArrayList<>();

        switch (metricName) {
            case "CpuUsage":
                measurement = "cpu";
                chartType.add(String.format("%s(usage_utilization) as Usage", function));
                break;
            case "Cpu":
                measurement = "cpu";
                chartType.add(String.format("%s(usage_user) as UsageUser", function));
                chartType.add(String.format("%s(usage_system) as System", function));
                chartType.add(String.format("%s(usage_nice) as Nice", function));
                chartType.add(String.format("(%s(usage_irq) + %s(usage_softirq)) as Intr", function, function));
                chartType.add(String.format("%s(usage_idle) as Idle", function));
                chartType.add(String.format("(%s(usage_iowait) + %s(usage_steal) + %s(usage_guest) + %s(usage_guest_nice)) as Other", function, function, function, function));
                break;
            case "CpuLoad":
                measurement = "system";
                chartType.add(String.format("%s(load1) as Load1", function));
                chartType.add(String.format("%s(load5) as Load5", function));
                chartType.add(String.format("%s(load15) as Load15", function));
                break;
            case "MemoryUsage":
                measurement = "mem";
                chartType.add(String.format("%s(used_percent) AS Usage", function));
                break;
            case "SwapUsage":
                measurement = "swap";
                chartType.add(String.format("%s(used_percent) AS Usage", function));
                break;
            case "Memory":
                measurement = "mem";
                chartType.add(String.format("%s(buffered) AS Buffed", function));
                chartType.add(String.format("%s(shared) AS Shared", function));
                chartType.add(String.format("%s(cached) AS Cached", function));
                chartType.add(String.format("%s(free) AS Free", function));
                break;
            case "DiskUsage":
                measurement = "disk";
                chartType.add(String.format("%s(used_percent) AS Usage", function));
                break;
            case "Disk":
                measurement = "diskio";
                chartType.add("non_negative_derivative(first(read_bytes), 1s) as Reads");
                chartType.add("non_negative_derivative(first(write_bytes), 1s) as Writes");
                break;
            case "Network":
                measurement = "net";
                chartType.add("non_negative_derivative(first(bytes_recv), 1s) as Input");
                chartType.add("non_negative_derivative(first(bytes_sent), 1s) as Output");
                break;
            case "Process":
                measurement = "processes";
                chartType.add(String.format("%s(running) AS Running", function));
                chartType.add(String.format("%s(total) AS Total", function));
                break;
        }

        String columns = StringUtils.collectionToDelimitedString(chartType, ",");

        Map<String, Object> metricData = new HashMap<>();

        if(chartType.size() == 0) {
            return metricData;
        }

        String queryString = String.format("SELECT %s FROM %s WHERE \"UUID\"='%s'AND time > now() - %s GROUP BY time(%s) FILL(0)", columns, measurement, vmId, hour, interval);
        System.out.println(queryString);
        logger.debug("queryString : " + queryString);

        Query query = BoundParameterQuery.QueryBuilder.newQuery(queryString)
                .forDatabase("openstackit")
                .create();

        try {
            QueryResult queryResult = influxDBTemplate.query(query);
            logger.debug("query : " + query);
            System.out.println(query);
            if (queryResult.hasError()) {
                logger.error("Failed to getVmMetric : '{}'", queryResult.getError());
                return metricData;
            }

            List<QueryResult.Result> results = queryResult.getResults();
            if(results.get(0) != null) {
                List<QueryResult.Series> series = results.get(0).getSeries();
                if (series != null) {

                    // 모니터링 메트릭 인덱스 정보 저장
                    Map<String, Integer> metricMap = new HashMap<>();
                    if (series.get(0).getColumns() != null) {
                        List<String> cols = series.get(0).getColumns();
                        for (int i = 0; i < cols.size(); i++) {
                            if(cols.get(i).equals("UsageUser")) { // User 키워드로 메트릭 조회 시 에러 발생 (내부적으로 변환 처리)
                                metricMap.put("User", i);
                                continue;
                            }
                            metricMap.put(cols.get(i), i);
                        }
                    }

                    // 모니터링 메트릭 데이터 정보 저장
                    ArrayList<Long> dates = new ArrayList<>();
                    Map<Integer, ArrayList<Object>> metricValMap = new HashMap<>();
                    if (series.get(0).getValues() != null) {
                        List<List<Object>> timePoints = series.get(0).getValues();
                        for(int i=0; i<timePoints.size(); i++) {
                            List<Object> metricVals = timePoints.get(i);

                            for(int j=0; j<metricVals.size(); j++) {

                                if(!metricValMap.containsKey(j)) {
                                    metricValMap.put(j, new ArrayList<>());
                                }

                                if(j == metricMap.get("time")) {
                                    // UTC 타임존 기준 일자 정보 설정
                                    SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    String dateStr = metricVals.get(j).toString();

                                    Date utcDate = utcFormat.parse(dateStr);
                                    // KST 타임존 기준 일자 정보 설정
                                    SimpleDateFormat kstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    kstFormat.setTimeZone(TimeZone.getTimeZone("KST"));
                                    String kstDateStr = kstFormat.format(utcDate);
                                    Date kstDate = kstFormat.parse(kstDateStr);

                                    long kstTimestamp = kstDate.getTime();
                                    dates.add(kstTimestamp);
                                } else {
                                    metricValMap.get(j).add(metricVals.get(j));
                                }
                            }
                        }
                    }

                    //  모니터링 메트릭 정보 매핑
                    for(String metricKey : metricMap.keySet()) {
                        Integer metricIdx = metricMap.get(metricKey);
                        if (metricKey.equals("time")) {
                            metricData.put("dates", dates);
                        } else {
                            metricData.put(metricKey, metricValMap.get(metricIdx));
                        }
                    }

                    // 모니터링 정보 리턴
                    return metricData;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to getVmMetric : '{}'", e.getMessage());
        }

        return metricData;
    }

    @Override
    public String getServerConsoleOutput(String cloudId, String id, int line, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Server Console Output", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/log");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        url.queryParam("line", line);

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        String consoleOutput = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<Map<String, String>>(){}).getBody().get("log");

        logger.info("[{}] Get Server Console Output Complete", CommonUtil.getUserUUID());
        return consoleOutput;
    }

    @Override
    public List<ActionLogInfo> getServerActionLog(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Server Action Log", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/action");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<ActionLogInfo> lists = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<ActionLogInfo>>(){}).getBody();

        logger.info("[{}] Get Server Action Log Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public List<VolumeInfo> getServerVolumes(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Server Volumes", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/volumes");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<VolumeInfo> lists = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<VolumeInfo>>(){}).getBody();

        logger.info("[{}] Get Server Volumes Complete", CommonUtil.getUserUUID());
        if(lists == null) lists = new ArrayList<>();

        return lists;
    }

    @Override
    public VolumeInfo getVolumes_Detail_openstack(String cloudId, String volumeId, String token) {
        logger.info("volumeId_openstack1 = '{}'", volumeId);
        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/volumes/" + volumeId);
//        url.path(API_PATH_public + "/volumes/" + volumeId);

        VolumeInfo info = null;

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        url.queryParam("webCheck", true);

        info = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeInfo>(){}).getBody();

        logger.info("openstack_detail_lists = '{}'", info);

        return info;
    }


    @Override
    public VolumeInfo attachVolume(String cloudId, String id, String volumeId, UserInfo reqInfo, String token) {
        logger.info("[{}] Attach Volume", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/volumes/{volumeId}");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        VolumeInfo volume = restTemplate.exchange(url.buildAndExpand(id, volumeId).toUri(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{put("action", "ATTACH_VOLUME");}}, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), volume.toString(), volume.getId(),
                volume.getName(), Constants.ACTION_CODE.VOLUME_ATTACH, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            VolumeInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
                url1.path(API_PATH + "/volumes/{volumeId}");

                if(projectInfo != null) {
                    url1.queryParam("project", projectInfo.getProjectId());
                }

                info = restTemplate.exchange(url1.buildAndExpand(volumeId).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeInfo>(){}).getBody();

                sendMessage("ATTACH_VOLUME", reqInfo.getId(), info);

                if (info == null || (info.getState().equals("in-use") && info.getAttachmentInfos().size() > 0) || info.getState().equals("available")) {
                    if((info.getState().equals("in-use") && info.getAttachmentInfos().size() > 0)) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Attach Volume Complete", CommonUtil.getUserUUID());
        return volume;
    }

    @Override
    public VolumeInfo detachVolume(String cloudId, String id, String volumeId, UserInfo reqInfo, String token) {
        logger.info("[{}] Detach Volume", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/volumes/{volumeId}");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        VolumeInfo volume = restTemplate.exchange(url.buildAndExpand(id, volumeId).toUri(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{put("action", "DETACH_VOLUME");}}, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), volume.toString(), volume.getId(),
                volume.getName(), Constants.ACTION_CODE.VOLUME_DETACH, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            VolumeInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
                url1.path(API_PATH + "/volumes/{volumeId}");

                if(projectInfo != null) {
                    url1.queryParam("project", projectInfo.getProjectId());
                }

                info = restTemplate.exchange(url1.buildAndExpand(volumeId).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeInfo>(){}).getBody();


                sendMessage("DETACH_VOLUME", reqInfo.getId(), info);

                if (info == null || info.getState().equals("available") || (info.getState().equals("in-use") && info.getAttachmentInfos().size() > 0)) {
                    if(info.getState().equals("available")) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Detach Volume Complete", CommonUtil.getUserUUID());
        return volume;
    }

    @Override
    public Boolean attachInterface(String cloudId, String id, String networkId, String projectId, UserInfo reqInfo, String token) {
        logger.info("[{}] Attach Interface", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/interface");

        if(projectId != null) {
            url.queryParam("project", projectId);
        } else if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ServerInfo server = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{
            put("action", "ATTACH_INTERFACE");
            put("networkId", networkId);
        }}, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                server.getName(), Constants.ACTION_CODE.INTERFACE_ATTACH, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            ServerInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
                url1.path(API_PATH + "/servers/{id}");

                if(projectInfo != null) {
                    url1.queryParam("project", projectInfo.getProjectId());
                }

                info = restTemplate.exchange(url1.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                sendMessage("ATTACH_INTERFACE", reqInfo.getId(), info);

                if (info == null || server.getAddresses().size() < info.getAddresses().size()) {
                    if(server.getAddresses().size() < info.getAddresses().size()) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Attach Interface Complete", CommonUtil.getUserUUID());
        return server != null;
    }

    @Override
    public Boolean detachInterface(String cloudId, String id, String portId, String projectId, UserInfo reqInfo, String token) {
        logger.info("[{}] Detach Interface", CommonUtil.getUserUUID());

        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/interface");

        if(projectId != null) {
            url.queryParam("project", projectId);
        } else if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ServerInfo server = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{
            put("action", "DETACH_INTERFACE");
            put("portId", portId);
        }}, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                server.getName(), Constants.ACTION_CODE.INTERFACE_DETACH, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            ServerInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
                url1.path(API_PATH + "/servers/{id}");

                if(projectInfo != null) {
                    url1.queryParam("project", projectInfo.getProjectId());
                }

                info = restTemplate.exchange(url1.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                sendMessage("DETACH_INTERFACE", reqInfo.getId(), info);

                if (info == null || server.getAddresses().size() > info.getAddresses().size()) {
                    if(server.getAddresses().size() > info.getAddresses().size()) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Detach Interface Complete", CommonUtil.getUserUUID());
        return server != null;
    }

    @Override
    public Object getServerInterface(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Get Server Interface", CommonUtil.getUserUUID());

        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/interface");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        Object list = restTemplate.exchange(url.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<Object>(){}).getBody();

        logger.info("[{}] Get Server Interface Complete", CommonUtil.getUserUUID());
        if(list == null) return new ArrayList<>();
        return list;
    }

    @Override
    public Boolean addFloatingIpToServer(String cloudId, String serverId, String interfaceIp, String floatingIp, String projectId, UserInfo reqInfo, String token) {
        logger.info("[{}] Add FloatingIp To Server", CommonUtil.getUserUUID());

        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/floatingip");

        if(projectId != null) {
            url.queryParam("project", projectId);
        } else if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ServerInfo server = restTemplate.exchange(url.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{
            put("interfaceIp", interfaceIp);
            put("floatingIp", floatingIp);
            put("action", "CONNECT_FLOATING_IP");
        }}, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                server.getName(), Constants.ACTION_CODE.FLOATING_IP_ADD, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            ServerInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
                url1.path(API_PATH + "/servers/{id}");

                if(projectInfo != null) {
                    url1.queryParam("project", projectInfo.getProjectId());
                }

                info = restTemplate.exchange(url1.buildAndExpand(serverId).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                sendMessage("CONNECT_FLOATING_IP", reqInfo.getId(), info);

                if (info == null || server.getAddresses().size() < info.getAddresses().size()) {
                    if(server.getAddresses().size() < info.getAddresses().size()) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Add FloatingIp To Server Complete", CommonUtil.getUserUUID());
        return server != null;
    }

    @Override
    public Boolean removeFloatingIpToServer(String cloudId, String serverId, String floatingIp, String projectId, UserInfo reqInfo, String token) {
        logger.info("[{}] Remove FloatingIp To Server", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/servers/{id}/floatingip");

        if(projectId != null) {
            url.queryParam("project", projectId);
        } else if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ServerInfo server = restTemplate.exchange(url.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{
            put("floatingIp", floatingIp);
            put("action", "DISCONNECT_FLOATING_IP");
        }}, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), server.toString(), server.getId(),
                server.getName(), Constants.ACTION_CODE.FLOATING_IP_REMOVE, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            ServerInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));

                UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
                url1.path(API_PATH + "/servers/{id}");

                if(projectInfo != null) {
                    url1.queryParam("project", projectInfo.getProjectId());
                }

                info = restTemplate.exchange(url1.buildAndExpand(serverId).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>(){}).getBody();

                sendMessage("DISCONNECT_FLOATING_IP", reqInfo.getId(), info);

                if (info == null || server.getAddresses().size() > info.getAddresses().size()) {
                    if(server.getAddresses().size() > info.getAddresses().size()) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Remove FloatingIp To Server Complete", CommonUtil.getUserUUID());
        return server != null;
    }

    @Override
    public FloatingIpInfo allocateFloatingIp(String cloudId, String poolName, UserInfo reqInfo, String token) {
        logger.info("[{}] Allocate FloatingIp To Server", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path("/floatingip");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        FloatingIpInfo info = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.POST, new HttpEntity(new HashMap<String, String>(){{
            put("poolName", poolName);
        }}, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<FloatingIpInfo>(){}).getBody();

        logger.info("[{}] Allocate FloatingIp To Server Complete", CommonUtil.getUserUUID());
        return info;
    }

    @Override
    public Boolean deallocateFloatingIp(String cloudId, String floatingIpId, UserInfo reqInfo, String token) {
        logger.info("[{}] Deallocate FloatingIp To Server", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path("/floatingip/{floatingIpId}");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        HttpStatus statusCode = restTemplate.exchange(url.buildAndExpand(floatingIpId).toUri(), HttpMethod.DELETE, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<Void>(){}).getStatusCode();

        logger.info("[{}] Deallocate FloatingIp To Server Complete", CommonUtil.getUserUUID());
//        return statusCode == HttpStatus.OK || statusCode == HttpStatus.NO_CONTENT;
        return statusCode == HttpStatus.NO_CONTENT;
    }

    @Override
    public List<String> getFloatingIpPoolNames(String cloudId, UserInfo reqInfo, String token) {
        logger.info("[{}] Get FloatingIp Pool Names", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/floatingIpools");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<String> list = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<String>>(){}).getBody();

        logger.info("[{}] Get FloatingIp Pool Names Complete", CommonUtil.getUserUUID());
        return list;
    }

    @Override
    public List<? extends Hypervisor> getHypervisors(String cloudId, String token) {
        logger.info("[{}] Get Hypervisors", CommonUtil.getUserUUID());
        UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
        url1.path(API_PATH + "/hypervisors");

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        List<? extends Hypervisor> list = restTemplate.exchange(url1.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<? extends Hypervisor>>(){}).getBody();
        logger.info("[{}] Get Hypervisors Complete", CommonUtil.getUserUUID());
        return list;
    }

    @Override
    public HypervisorStatistics getHypervisorStatistics(String cloudId, String token) {
        logger.info("[{}] Get Hypervisor Statistics", CommonUtil.getUserUUID());
        UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
        url1.path(API_PATH + "/hypervisorstatistics");

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        HypervisorStatistics info = restTemplate.exchange(url1.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<HypervisorStatistics>(){}).getBody();
        logger.info("[{}] Get Hypervisor Statistics Complete", CommonUtil.getUserUUID());
        return info;
    }

    @Override
    public ResourceInfo getResourceUsage(String cloudId, String token) {
        logger.info("[{}] Get ResourceUsage", CommonUtil.getUserUUID());
        UriComponentsBuilder url1 = UriComponentsBuilder.fromUriString(apiUrl);
        url1.path(API_PATH + "/resource");

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ResourceInfo info = restTemplate.exchange(url1.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ResourceInfo>(){}).getBody();
        logger.info("[{}] Get ResourceUsage Complete", CommonUtil.getUserUUID());
        return info;
    }

    @Override
    public VolumeInfo createVolume(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token) {
        logger.info("[{}] Create Volume", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/volumes");
//        url.path(API_PATH_public + "/volumes");

        UriComponentsBuilder url2 = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url2 = UriComponentsBuilder.fromUriString(apiUrl_public);
        url2.path(API_PATH + "/volumes/{id}");
//        url2.path(API_PATH_public + "/volumes/{id}");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
            url2.queryParam("project", projectInfo.getProjectId());
        }

        logger.info("[{}] createData is ", createData);

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);
        url.queryParam("webCheck", true);
        VolumeInfo volume = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.POST, new HttpEntity(createData, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), volume.toString(), volume.getId(),
                volume.getName(), Constants.ACTION_CODE.VOLUME_CREATE, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            VolumeInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

//                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));
                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (CommonUtil.MAX_RETRY_COUNT - duration));

                info = restTemplate.exchange(url2.buildAndExpand(volume.getId()).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeInfo>(){}).getBody();

                sendMessage("VOLUME_CREATE", reqInfo.getId(), info);

                if (info == null || info.getState().equals("available") || info.getState().equals("error")) {
                    if(info.getState().equals("available")) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Create Volume Complete : '{}'", CommonUtil.getUserUUID(), volume.getId());
        return volume;
    }

    @Override
    public VolumeInfo deleteVolume(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Delete Volume : '{}'", CommonUtil.getUserUUID(), id);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        VolumeInfo volume = null;

        Runnable function = null;

//        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl);
        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl_public);
//        url10.path(API_PATH + "/volumes/{id}/delete");
        url10.path(API_PATH + "/volumes/{id}");
//        url10.path(API_PATH_public + "/volumes/{id}");
        if(projectInfo != null) {
            url10.queryParam("project", projectInfo.getProjectId());
        }
        url10.queryParam("webCheck", true);

        volume = restTemplate.exchange(url10.buildAndExpand(id).toUri(), HttpMethod.DELETE, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<VolumeInfo>(){}).getBody();

        String actionId9 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), volume.toString(), id,
                volume.getName(), Constants.ACTION_CODE.VOLUME_DELETE, Constants.HISTORY_TYPE.OPENSTACK);

        actionService.setActionResult(actionId9, Constants.ACTION_RESULT.SUCCESS);

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Delete Volume Complete : '{}'", CommonUtil.getUserUUID(), volume.toString());
        return volume;
    }

    @Override
    public NetworkInfo createNetwork(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token) {
        logger.info("[{}] Create Network", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl_public);
        url.path(API_PATH + "/networks");
//        url.path(API_PATH_public + "/networks");

        UriComponentsBuilder url2 = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url2 = UriComponentsBuilder.fromUriString(apiUrl_public);
        url2.path(API_PATH + "/networks/{id}");
//        url2.path(API_PATH_public + "/networks/{id}");

        url.queryParam("webCheck", true);

        logger.info("[{}] createData is ", createData);

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
            url2.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        NetworkInfo networkInfo = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.POST, new HttpEntity(createData, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<NetworkInfo>(){}).getBody();

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), networkInfo.toString(), networkInfo.getId(),
                networkInfo.getName(), Constants.ACTION_CODE.NETWORK_CREATE, Constants.HISTORY_TYPE.OPENSTACK);

        Runnable function = () -> {

            int duration = 0;
            NetworkInfo info = null;

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

            while ( duration < CommonUtil.MAX_RETRY_COUNT ) {

//                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (duration + 1));
                CommonUtil.sleep(CommonUtil.SLEEP_TIME * (CommonUtil.MAX_RETRY_COUNT - duration));

                info = restTemplate.exchange(url2.buildAndExpand(networkInfo.getId()).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<NetworkInfo>(){}).getBody();

                sendMessage(Constants.ACTION_CODE.NETWORK_CREATE.toString(), reqInfo.getId(), info);

                if (info == null || info.getState().equals("active") || info.getState().equals("error")) {
                    if(info.getState().equals("active")) {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
                    } else {
                        actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
                    }
                    break;
                }

                duration += 1;
            }
        };

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Create Network Complete : '{}'", CommonUtil.getUserUUID(), networkInfo.getId());
        return networkInfo;
    }

    @Override
    public NetworkInfo deleteNetwork(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Delete Network : '{}'", CommonUtil.getUserUUID(), id);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        NetworkInfo networkInfo = null;
        Runnable function = null;

        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl);
//        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl_public);
//        url10.path(API_PATH + "/networks/{id}/delete");
        url10.path(API_PATH + "/networks/{id}");
//        url10.path(API_PATH_public + "/networks/{id}");
        if(projectInfo != null) {
            url10.queryParam("project", projectInfo.getProjectId());
        }
//        networkInfo = restTemplate.exchange(url10.buildAndExpand(id).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<NetworkInfo>(){}).getBody();
        networkInfo = restTemplate.exchange(url10.buildAndExpand(id).toUri(), HttpMethod.DELETE, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<NetworkInfo>(){}).getBody();

        String actionId9 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), networkInfo.toString(), id,
                networkInfo.getName(), Constants.ACTION_CODE.NETWORK_DELETE, Constants.HISTORY_TYPE.OPENSTACK);

        actionService.setActionResult(actionId9, Constants.ACTION_RESULT.SUCCESS);

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Delete Network Complete : '{}'", CommonUtil.getUserUUID(), networkInfo.toString());
        return networkInfo;
    }

    @Override
    public SubnetInfo deleteSubnet(String cloudId, String id, String subnetId, UserInfo reqInfo, String token) {
        logger.info("[{}] Delete Subnet : '{}'", CommonUtil.getUserUUID(), subnetId);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        SubnetInfo subnetInfo = null;

        Runnable function = null;

        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl);
        url10.path(API_PATH + "/networks/{id}/subnets/{subnetId}/delete");
        if(projectInfo != null) {
            url10.queryParam("project", projectInfo.getProjectId());
        }
        subnetInfo = restTemplate.exchange(url10.buildAndExpand(id, subnetId).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<SubnetInfo>(){}).getBody();

        String actionId9 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), subnetInfo.toString(), id,
                subnetInfo.getName(), Constants.ACTION_CODE.SUBNET_DELETE, Constants.HISTORY_TYPE.OPENSTACK);

        actionService.setActionResult(actionId9, Constants.ACTION_RESULT.SUCCESS);

        if(function != null) {
            new Thread(function).start();
        }

        logger.info("[{}] Delete Subnet Complete : '{}'", CommonUtil.getUserUUID(), subnetInfo.toString());
        return subnetInfo;
    }

    @Override
    public ImageInfo createImage(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token) {
        logger.info("[{}] Create Image", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/images");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), createData.toString(), null,
                (String)createData.get("name"), Constants.ACTION_CODE.IMAGE_CREATE, Constants.HISTORY_TYPE.OPENSTACK);
        actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

        try {
            ImageInfo imageInfo = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.POST, new HttpEntity(createData, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ImageInfo>() {
            }).getBody();

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            logger.info("[{}] Create Image Complete : '{}'", CommonUtil.getUserUUID(), imageInfo.getId());
            return imageInfo;

        } catch (Exception e) {
            logger.info("[{}] Create Image Fail : '{}'", CommonUtil.getUserUUID(), e.getMessage());
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);

            throw e;
        }
    }

    @Override
    public ImageInfo deleteImage(String cloudId, String id, UserInfo reqInfo, String token) {
        logger.info("[{}] Delete Image : '{}'", CommonUtil.getUserUUID(), id);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ImageInfo imageInfo = null;

        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl);
        url10.path(API_PATH + "/images/{id}/delete");
        if(projectInfo != null) {
            url10.queryParam("project", projectInfo.getProjectId());
        }

        String actionId9 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), id, id,
                id, Constants.ACTION_CODE.IMAGE_DELETE, Constants.HISTORY_TYPE.OPENSTACK);

        try {
            imageInfo = restTemplate.exchange(url10.buildAndExpand(id).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ImageInfo>() {
            }).getBody();

            logger.info("[{}] Delete Image Complete : '{}'", CommonUtil.getUserUUID(), imageInfo.toString());
            actionService.setActionResult(actionId9, Constants.ACTION_RESULT.SUCCESS);
        } catch (Exception e) {
            logger.info("[{}] Delete Image Fail : '{}'", CommonUtil.getUserUUID(), e.getMessage());
            actionService.setActionResult(actionId9, Constants.ACTION_RESULT.FAILED);

            throw e;
        }

        return imageInfo;
    }

    @Override
    public KeyPairInfo createKeypair(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token) {
        logger.info("[{}] Create Keypair", CommonUtil.getUserUUID());
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl);
        url.path(API_PATH + "/keypairs");

        if(projectInfo != null) {
            url.queryParam("project", projectInfo.getProjectId());
        }

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), createData.toString(), null,
                (String)createData.get("name"), Constants.ACTION_CODE.KEYPAIR_CREATE, Constants.HISTORY_TYPE.OPENSTACK);
        actionService.setActionResult(actionId, Constants.ACTION_RESULT.PROGRESSING);

        try {
            KeyPairInfo keyPairInfo = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.POST, new HttpEntity(createData, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<KeyPairInfo>() {
            }).getBody();

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            logger.info("[{}] Create Keypair Complete : '{}'", CommonUtil.getUserUUID(), keyPairInfo.getName());
            return keyPairInfo;

        } catch (Exception e) {
            logger.info("[{}] Create Keypair Fail : '{}'", CommonUtil.getUserUUID(), e.getMessage());
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);

            throw e;
        }
    }

    @Override
    public KeyPairInfo deleteKeypair(String cloudId, String keypairName, UserInfo reqInfo, String token) {
        logger.info("[{}] Delete Keypair : '{}'", CommonUtil.getUserUUID(), keypairName);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        KeyPairInfo keyPairInfo = null;

        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl);
        url10.path(API_PATH + "/keypairs/{keypairName}/delete");
        if(projectInfo != null) {
            url10.queryParam("project", projectInfo.getProjectId());
        }

        String actionId9 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), keypairName, keypairName,
                keypairName, Constants.ACTION_CODE.KEYPAIR_DELETE, Constants.HISTORY_TYPE.OPENSTACK);

        try {
            keyPairInfo = restTemplate.exchange(url10.buildAndExpand(keypairName).toUri(), HttpMethod.POST, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<KeyPairInfo>() {
            }).getBody();

            logger.info("[{}] Delete Keypair Complete : '{}'", CommonUtil.getUserUUID(), keypairName);
            actionService.setActionResult(actionId9, Constants.ACTION_RESULT.SUCCESS);
        } catch (Exception e) {
            logger.info("[{}] Delete Keypair Fail : '{}'", CommonUtil.getUserUUID(), e.getMessage());
            actionService.setActionResult(actionId9, Constants.ACTION_RESULT.FAILED);

            throw e;
        }

        return keyPairInfo;
    }

    @Override
    public ServerInfo changeFlavor(String cloudId, String serverId, String flavorId, UserInfo reqInfo, String token) {
        logger.info("[{}] resize Server : '{}', '{}'", CommonUtil.getUserUUID(), serverId, flavorId);
        com.innogrid.uniq.core.model.ProjectInfo projectInfo = getProjectByGroupId(cloudId, reqInfo.getGroupId());

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);

        ServerInfo serverInfo = null;

        UriComponentsBuilder url10 = UriComponentsBuilder.fromUriString(apiUrl);
        url10.path(API_PATH + "/servers/{id}/resize");
        if(projectInfo != null) {
            url10.queryParam("project", projectInfo.getProjectId());
        }

        String actionId9 = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), serverId + ", " + flavorId, serverId,
                serverId, Constants.ACTION_CODE.SERVER_RESIZE, Constants.HISTORY_TYPE.OPENSTACK);

        Map<String, String> createData =new HashMap<>();
        createData.put("flavorId", flavorId);

        try {
            serverInfo = restTemplate.exchange(url10.buildAndExpand(serverId).toUri(), HttpMethod.POST, new HttpEntity(createData, CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<ServerInfo>() {
            }).getBody();

            logger.info("[{}] resize Server Complete : '{}', '{}'", CommonUtil.getUserUUID(), serverId, flavorId);
            actionService.setActionResult(actionId9, Constants.ACTION_RESULT.SUCCESS);
        } catch (Exception e) {
            logger.info("[{}] resize Server Fail : '{}'", CommonUtil.getUserUUID(), e.getMessage());
            actionService.setActionResult(actionId9, Constants.ACTION_RESULT.FAILED);

            throw e;
        }

        return serverInfo;
    }
}
