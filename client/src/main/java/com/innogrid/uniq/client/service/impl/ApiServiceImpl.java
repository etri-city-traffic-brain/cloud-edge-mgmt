package com.innogrid.uniq.client.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innogrid.uniq.client.service.ApiService;
import com.innogrid.uniq.client.util.CommonUtil;
import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;
import com.innogrid.uniq.core.model.ProjectInfo;
import com.innogrid.uniq.core.util.AES256Util;
import com.innogrid.uniq.core.util.ObjectSerializer;
import com.innogrid.uniq.coredb.service.CredentialService;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ApiServiceImpl implements ApiService {
    private final static Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;


    @Value("${apigateway_local.url}")
    private String apiUrl;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private AES256Util aes256Util;

    @Override
    public List<CredentialInfo> getCredentialsProject(List<CredentialInfo> list, String token) {

        for(int i=0; i<list.size(); i++) {
            CredentialInfo info = list.get(i);
            List<ProjectInfo> projectInfos = new ArrayList<>();
            if(info.getType().equals("openstack")) {
                UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl + OpenStackServiceImpl.API_PATH);
                url.path("/projects");

                try {
                    List<com.innogrid.uniq.coreopenstack.model.ProjectInfo> openstackProjects = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(info)), token)), new ParameterizedTypeReference<List<com.innogrid.uniq.coreopenstack.model.ProjectInfo>>() {}).getBody();

                    for (int j = 0; j < openstackProjects.size(); j++) {
                        com.innogrid.uniq.coreopenstack.model.ProjectInfo openstackProject = openstackProjects.get(j);

                        ProjectInfo projectInfo = new ProjectInfo();
                        projectInfo.setProjectName(openstackProject.getName());
                        projectInfo.setProjectId(openstackProject.getId());
                        projectInfo.setDescription(openstackProject.getDescription());
                        projectInfo.setCloudId(info.getId());
                        projectInfo.setCloudName(info.getName());
                        projectInfo.setType(info.getType());

                        projectInfos.add(projectInfo);
                    }
                } catch (HttpServerErrorException e) {
                    logger.error("Failed to get CredentialsProject (Openstack): '{}'", e.getMessage());
                    continue;
                }

            } else {
                ProjectInfo projectInfo = new ProjectInfo();
                projectInfo.setProjectName(info.getName());
                projectInfo.setProjectId(info.getId());
                projectInfo.setDescription("");
                projectInfo.setCloudId(info.getId());
                projectInfo.setCloudName(info.getName());
                projectInfo.setType(info.getType());

                projectInfos.add(projectInfo);
            }
            info.setProjects(projectInfos);

        }

        return list;
    }

    @Override
    public List<CredentialInfo> getCredentialsInfo(List<CredentialInfo> list) {
        String jsonString = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        List<CredentialInfo> open = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CredentialInfo credentialInfo = new CredentialInfo();
            credentialInfo.setId(list.get(i).getType());
            credentialInfo.setName(list.get(i).getName());

            credentialInfo.setDomain(list.get(i).getDomain());
            credentialInfo.setUrl(list.get(i).getUrl());
            credentialInfo.setTenantId(list.get(i).getTenant());
            credentialInfo.setAccessId(list.get(i).getAccessId());
            credentialInfo.setAccessToken(list.get(i).getAccessToken());
            credentialInfo.setCreatedAt(list.get(i).getCreatedAt());
            credentialInfo.setProjects(list.get(i).getProjects());
            credentialInfo.setCloudType(list.get(i).getCloudType());
            open.add(credentialInfo);
        }

        try {
            jsonString = mapper.writeValueAsString(open);
        } catch(IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = JSONArray.fromObject(jsonString);

//            return apiService.getCredentialsInfo(credentialService.getCredentials(new HashMap<>()));
        logger.error("-------- jsonArray -------- ==== " + jsonArray);
        return jsonArray;
    }




    @Override
    public boolean validateCredential(CredentialInfo info, String token) {

        boolean isValid = true;

        // 클라우드 별 Credential 유효성 체크
        switch (info.getType()) {
            case "openstack":
                UriComponentsBuilder url2 = UriComponentsBuilder.fromUriString(apiUrl + OpenStackServiceImpl.API_PATH);
                url2.path("/validate");

                isValid = restTemplate.exchange(url2.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(info)), token)), new ParameterizedTypeReference<Map<String, Boolean>>(){}).getBody().get("result");
                break;

        }

        return isValid;
    }

    @Override
    public List<ProjectInfo> getGroupProject(List<ProjectInfo> list, String token) {

        for(int i=0; i<list.size(); i++) {
            ProjectInfo info = list.get(i);
            if(info.getType().equals("openstack")) {
                UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl + OpenStackServiceImpl.API_PATH);
                url.path("/projects/{projectId}");

                CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(info.getCloudId());

                com.innogrid.uniq.coreopenstack.model.ProjectInfo openstackProject = restTemplate.exchange(url.buildAndExpand(info.getProjectId()).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<com.innogrid.uniq.coreopenstack.model.ProjectInfo>(){}).getBody();

                info.setProjectName(openstackProject.getName());
                info.setDescription(openstackProject.getDescription());

            } else {
                info.setProjectName(info.getCloudName());
            }
        }

        return list;
    }

    @Override
    public List<MeterServerInfo> getMeterServers(String cloudId, String serverId, String token) {

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);
        List<MeterServerInfo> list = null;

         if(credentialInfo.getType().equals("openstack")) {
            UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl + OpenStackServiceImpl.API_PATH);
            url.path("/meter/servers/{id}");

            list = restTemplate.exchange(url.buildAndExpand(serverId).toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<MeterServerInfo>>(){}).getBody();
        }

        if(list == null) return new ArrayList<>();

        return list;
    }

    @Override
    public List<MeterServerAccumulateInfo> getMeterServerAccumulates(String cloudId, String token) {

        CredentialInfo credentialInfo = credentialService.getCredentialsFromMemoryById(cloudId);
        List<MeterServerAccumulateInfo> list = null;
        if(credentialInfo.getType().equals("openstack")) {
            UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl + OpenStackServiceImpl.API_PATH);
            url.path("/meter/servers");

            list = restTemplate.exchange(url.build().encode().toUri(), HttpMethod.GET, new HttpEntity(CommonUtil.getAuthHeaders(aes256Util.encrypt(ObjectSerializer.serializedData(credentialInfo)), token)), new ParameterizedTypeReference<List<MeterServerAccumulateInfo>>(){}).getBody();
        }
        if(list == null) return new ArrayList<>();

        return list;
    }

    @Override
    public boolean getCredentialsCheck(List<CredentialInfo> list, String type){
        if(type.equals("openstack")){
            return true;
        }else{
            for(CredentialInfo info : list){
                if(info.getType().equals(type)){
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public boolean getCredentialsNameCheck(List<CredentialInfo> list, String name){
        for(CredentialInfo info : list){
            if(info.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    public String getCloudType(CredentialInfo createData){
        String tempType = createData.getType();
        String type;
        if(tempType.equals("openstack")) {
            type = "private";
        }else{
            type = "public";
        }
        return type;
    }
}
