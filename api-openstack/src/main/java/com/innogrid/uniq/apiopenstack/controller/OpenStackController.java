package com.innogrid.uniq.apiopenstack.controller;

import com.innogrid.uniq.apiopenstack.service.OpenStackService;
import com.innogrid.uniq.core.model.*;
import com.innogrid.uniq.core.util.AES256Util;
import com.innogrid.uniq.core.util.ObjectSerializer;
import com.innogrid.uniq.coredb.dao.CredentialDao;
import com.innogrid.uniq.coredb.service.CredentialService;
import com.innogrid.uniq.coredb.service.MeterService;
import com.innogrid.uniq.coreopenstack.model.*;
import com.innogrid.uniq.coreopenstack.model.ImageInfo;
import com.innogrid.uniq.coreopenstack.model.ProjectInfo;
import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.openstack4j.model.compute.InterfaceAttachment;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.openstack4j.model.storage.block.VolumeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static javax.ws.rs.HttpMethod.POST;

/**
 * Created by wss on 19. 7. 10.
 */
@Controller
@RequestMapping("/infra/cloudServices/openstack")
public class OpenStackController {
    private static Logger logger = LoggerFactory.getLogger(OpenStackController.class);

    @Autowired
    private OpenStackService openStackService;

    @Autowired
    private MeterService meterService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CredentialDao credentialDao;

    @Autowired
    private AES256Util aes256Util;

    @RequestMapping(value = {"","/"}, method = RequestMethod.GET)
    @ResponseBody
    public List<CredentialInfo> getCredentialOpenstack(@RequestHeader(value = "credential") String credential) {

        String type = "openstack";
        logger.error("apiopenstack, openstackController, credential is ? : {}", credential);
        return openStackService.getCredential(credentialService.getCredentials(new HashMap<>()), type);
    }

    @RequestMapping(value = "/zones", method = RequestMethod.GET)
    @ResponseBody
    public List<AvailabilityZoneInfo> getZones(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam String type
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getZones(credentialInfo, project, type);
    }

    @RequestMapping(value = "/servers", method = RequestMethod.GET)
    @ResponseBody
    public List<ServerInfo> getServers(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String serverState,
            @RequestParam(required = false) Boolean webCheck
    ) {
        logger.error("apiopenstack, openstackController, credential is ? : {}", credential);
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        logger.error("apiopenstack, openstackController, webCheck is ? : {}", webCheck);
        logger.error("apiopenstack, openstackController, name is ? : {}", name);
        logger.error("apiopenstack, openstackController, serverState is ? : {}", serverState);

        if(webCheck == null) {
            webCheck = false;
        }

        if(name != null){
            return openStackService.getServers_Search(credentialInfo, project, name, "name");
        }else if (serverState != null){
            return openStackService.getServers_Search(credentialInfo, project, serverState, "serverState");
        }

        return openStackService.getServers(credentialInfo, project, webCheck);
    }

    @RequestMapping(value = "/servers/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<ServerInfo> getServers(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String id,
            @RequestParam(required = false) Boolean webCheck
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        if(webCheck == null) {
            webCheck = false;
        }

        return openStackService.getServer(credentialInfo, project, id, webCheck);
    }

    @RequestMapping(value = "/flavors", method = RequestMethod.GET)
    @ResponseBody
    public List<FlavorInfo> getFlavors(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getFlavors(credentialInfo, project);
    }

    @RequestMapping(value = "/flavors/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<FlavorInfo> getFlavor(
            @RequestHeader(value = "credential") String credential,
            @PathVariable(value = "id") String flavorId,
            @RequestParam(required = false) String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getFlavor(credentialInfo, flavorId, project);
    }

    @RequestMapping(value = "/images", method = RequestMethod.GET)
    @ResponseBody
    public List<ImageInfo> getImages(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(defaultValue = "false") Boolean active
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getImages(credentialInfo, project, active);
    }

    @RequestMapping(value = "/keypairs", method = RequestMethod.GET)
    @ResponseBody
    public List<KeyPairInfo> getKeyPairs(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getKeyPairs(credentialInfo, project);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/keypairs", method = RequestMethod.POST)
    public @ResponseBody
    KeyPairInfo createKeypair(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestBody KeyPairInfo keyPairInfo
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.createKeypair(credentialInfo, project, keyPairInfo);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/keypairs/{name}/delete", method = RequestMethod.POST)
    public @ResponseBody KeyPairInfo deleteKeypair(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "name") String keypairName
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.deleteKeypair(credentialInfo, project, keypairName);
    }

    @RequestMapping(value = "/volumes", method = RequestMethod.GET)
    @ResponseBody
    public List<VolumeInfo> getVolumes(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(defaultValue = "false") Boolean bootable,
            @RequestParam(defaultValue = "false") Boolean available,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String volumeState,
            @RequestParam(required = false) Boolean webCheck
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        logger.error("apiopenstack, openstackController, getVolumes webCheck is ? : {}", webCheck);

        if(webCheck == null) {
            webCheck = false;
        }

        if(name != null){
            return openStackService.getVolumes_Search(credentialInfo, project, bootable, available, name, "name");
        }else if (volumeState != null){
            return openStackService.getVolumes_Search(credentialInfo, project, bootable, available, volumeState, "volumeState");
        }

        return openStackService.getVolumes(credentialInfo, project, bootable, available, webCheck);
    }

    @RequestMapping(value = "/volumeTypes", method = RequestMethod.GET)
    @ResponseBody
    public List<? extends VolumeType> getVolumeTypes(
            @RequestHeader(value = "credential") String credential
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getVolumeTypes(credentialInfo);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/volumes", method = RequestMethod.POST)
    public @ResponseBody
    Object createVolume(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) Boolean webCheck,
            @RequestBody CreateVolumeInfo createVolumeInfo
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));
        if(webCheck==null){
            webCheck=false;
        }

        return openStackService.createVolume(credentialInfo, project, createVolumeInfo, webCheck);
    }

    @RequestMapping(value = "/volumes/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<VolumeInfo> getVolume(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String volumeId,
            @RequestParam(required = false) Boolean webCheck
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        logger.error("apiopenstack, openstackController, webCheck is ? : {}", webCheck);

        if(webCheck == null) {
            webCheck = false;
        }

        return openStackService.getVolume(credentialInfo, project, volumeId, webCheck);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/volumes/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody DeleteInfo deleteVolume(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String volumeId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));
        return openStackService.deleteVolume(credentialInfo, project, volumeId);
    }

    @RequestMapping(value = "/volumes/{id}", method = RequestMethod.DELETE)
    public @ResponseBody DeleteInfo deleteVolume2(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String volumeId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.deleteVolume(credentialInfo, project, volumeId);
    }

    @RequestMapping(value = "/backups", method = RequestMethod.GET)
    @ResponseBody
    public List<VolumeBackupInfo> getBackups(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getBackups(credentialInfo, project);
    }

    @RequestMapping(value = "/snapshots", method = RequestMethod.GET)
    @ResponseBody
    public List<VolumeSnapshotInfo> getSnapshots(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(defaultValue = "false") Boolean available
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getSnapshots(credentialInfo, project, available);
    }

    @RequestMapping(value = "/snapshots/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<VolumeSnapshotInfo> getSnapshotid(
            @RequestParam(defaultValue = "false") Boolean available,
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String snapshotId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getSnapshotid(credentialInfo, snapshotId, project, available);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/snapshots/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody VolumeSnapshotInfo deleteSnapshot(
            @RequestHeader(value = "credential") String credential,
            @PathVariable(value = "id") String snapshotId,
            @RequestParam(required = false) String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.deleteSnapshot(credentialInfo, project, snapshotId);
    }

    @RequestMapping(value = "/networks", method = RequestMethod.GET)
    @ResponseBody
    public List<NetworkInfo> getNetworks(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String networkState,
            @RequestParam(required = false) Boolean webCheck
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        logger.error("apiopenstack, openstackController, getNetworks, webCheck is ? : {}", webCheck);

        if(webCheck == null) {
            webCheck = false;
        }

        if(name != null){
            return openStackService.getNetworks_Search(credentialInfo, project, name, "name");
        }else if (networkState != null){
            return openStackService.getNetworks_Search(credentialInfo, project, networkState, "networkState");
        }

        return openStackService.getNetworks(credentialInfo, project, webCheck);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/networks", method = RequestMethod.POST)
    public @ResponseBody
    Object createNetwork(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) Boolean webCheck,
            @RequestBody CreateNetworkInfo createNetworkInfo
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        if(webCheck==null){
            webCheck=false;
        }

        return openStackService.createNetwork(credentialInfo, project, createNetworkInfo, webCheck);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/networks/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody DeleteInfo deleteNetwork(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String networkId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.deleteNetwork(credentialInfo, project, networkId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/networks/{id}", method = RequestMethod.DELETE)
    public @ResponseBody DeleteInfo deleteNetwork2(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String networkId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.deleteNetwork(credentialInfo, project, networkId);
    }

    @RequestMapping(value = "/networks/{id}/subnets", method = RequestMethod.GET)
    @ResponseBody
    public List<SubnetInfo> getNetworkSubnets(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String networkId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getSubnets(credentialInfo, project, networkId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/networks/{id}/subnets/{subnetId}/delete", method = RequestMethod.POST)
    @ResponseBody
    public SubnetInfo deleteSubnet(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String networkId,
            @PathVariable(value = "subnetId") String subnetId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.deleteSubnet(credentialInfo, project, subnetId);
    }

    @RequestMapping(value = "/subnets", method = RequestMethod.GET)
    @ResponseBody
    public List<SubnetInfo> getSubnets(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getSubnets(credentialInfo, project, null);
    }

    @RequestMapping(value = "/routers", method = RequestMethod.GET)
    @ResponseBody
    public List<RouterInfo> getRouters(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getRouters(credentialInfo, project);
    }

    @RequestMapping(value = "/securitygroups", method = RequestMethod.GET)
    @ResponseBody
    public List<SecurityGroupInfo> getSecurityGroups(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getSecurityGroups(credentialInfo, project);
    }

    @RequestMapping(value = "/floatingips", method = RequestMethod.GET)
    @ResponseBody
    public List<FloatingIpInfo> getFloatingIps(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(defaultValue = "false") Boolean down
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getFloatingIps(credentialInfo, project, down);
    }

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    @ResponseBody
    public List<ProjectInfo> getProjects(
            @RequestHeader(value = "credential") String credential
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getProjects(credentialInfo);
    }

    @RequestMapping(value = "/projects/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public ProjectInfo getProjects(
            @RequestHeader(value = "credential") String credential,
            @PathVariable(value = "projectId") String project
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getProject(credentialInfo, project);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers", method = RequestMethod.POST)
    public @ResponseBody
    Object createServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) Boolean webCheck,
            @RequestBody CreateServerInfo createServerInfo
    ) {

        logger.error("createServer, webCheck = {}", webCheck);
        logger.error("createServer, createServerInfo = {}", createServerInfo);

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));
        if (webCheck==null){
            webCheck= false;
        }
        return openStackService.createServer(credentialInfo, project, createServerInfo, webCheck);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/start", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo startServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.start(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/stop", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo stopServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.stop(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/reboot", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo rebootServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId,
            @RequestParam(required = false, defaultValue = "false") Boolean hard
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        if(!hard) {
            return openStackService.rebootSoft(credentialInfo, project, serverId);
        } else {
            return openStackService.rebootHard(credentialInfo, project, serverId);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody DeleteInfo deleteServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.delete(credentialInfo, project, serverId);
    }

    @RequestMapping(value = "/servers/{id}", method = RequestMethod.DELETE)
    public @ResponseBody DeleteInfo deleteServer2(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.delete(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/pause", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo pauseServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.pause(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/unpause", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo unpauseServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.unpause(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/lock", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo lockServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.lock(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/unlock", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo unlockServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.unlock(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/suspend", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo suspendServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.suspend(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/resume", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo resumeServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.resume(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/rescue", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo rescueServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.rescue(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/unrescue", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo unrescueServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.unrescue(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/shelve", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo shelveServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.shelve(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/shelveoffload", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo shelveOffloadServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.shelveOffload(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/unshelve", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo unshelveServer(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.unshelve(credentialInfo, project, serverId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/snapshot", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> createServerSnapshot(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId,
            @RequestBody Map<String, String> param
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        String imageId = openStackService.createServerSnapshot(credentialInfo, project, serverId, param.get("name"));

        return new HashMap<String, String>(){{
            put("imageId", imageId);
        }};
    }

    @RequestMapping(value = "/servers/{id}/log", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getServerLog(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId,
            @RequestParam(value = "line") int line
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        String log = openStackService.getServerConsoleOutput(credentialInfo, project, serverId, line);

        return new HashMap<String, String>(){{
            put("log", log);
        }};
    }

    @RequestMapping(value = "/servers/{id}/console", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getServerConsole(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        String vncUrl = openStackService.getServerVNCConsoleURL(credentialInfo, project, serverId);

        return new HashMap<String, String>(){{
            put("url", vncUrl);
        }};
    }

    @RequestMapping(value = "/servers/{id}/metric", method = RequestMethod.GET)
    @ResponseBody
    public Object getServerMetric(
            @RequestHeader(value = "credential") String credential,
            @PathVariable(value = "id") String serverId,
            @RequestParam(value = "metricName") Integer metricName,
            @RequestParam(value = "statistic") String statistic,
            @RequestParam(value = "interval") Integer interval,
            @RequestParam(value = "endDate") Long endDate,
            @RequestParam(value = "startDate") Long startDate
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        RequestMetricInfo requestMetricInfo = new RequestMetricInfo();
        requestMetricInfo.setId(serverId);
        requestMetricInfo.setMetricName(metricName);
        requestMetricInfo.setStatistic(statistic);
        requestMetricInfo.setInterval(interval);
        requestMetricInfo.setEndDate(endDate);
        requestMetricInfo.setStartDate(startDate);

        return openStackService.getServerMetric(credentialInfo, requestMetricInfo);
    }

    @RequestMapping(value = "/servers/{id}/action", method = RequestMethod.GET)
    @ResponseBody
    public List<ActionLogInfo> getActions(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getServerActionLog(credentialInfo, project, serverId);
    }

    @RequestMapping(value = "/servers/{id}/volumes", method = RequestMethod.GET)
    @ResponseBody
    public List<VolumeInfo> getServerVolumes(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getServerVolumes(credentialInfo, project, serverId);
    }

    @RequestMapping(value = "/servers/{id}/volumes/{volumeId}", method = RequestMethod.POST)
    @ResponseBody
    public VolumeInfo actionServerVolume(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "volumeId") String volumeId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        if(action.get("action").equals("ATTACH_VOLUME")) {
            return openStackService.attachVolume(credentialInfo, project, serverId, volumeId);
        }  else if(action.get("action").equals("DETACH_VOLUME")) {
            return openStackService.detachVolume(credentialInfo, project, serverId, volumeId);
        } else {
            return new VolumeInfo();
        }
    }

    @RequestMapping(value = "/servers/{id}/interface", method = RequestMethod.GET)
    @ResponseBody
    public List<? extends InterfaceAttachment> getServerInterfaces(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getServerInterface(credentialInfo, project, serverId);
    }

    @RequestMapping(value = "/servers/{id}/interface", method = RequestMethod.POST)
    @ResponseBody
    public ServerInfo actionServerInterface(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId,
            @RequestBody Map<String, String> action
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        Boolean result = false;

        if(action.get("action").equals("ATTACH_INTERFACE")) {
            result = openStackService.attachInterface(credentialInfo, project, serverId, action.get("networkId"));
        } else if(action.get("action").equals("DETACH_INTERFACE")) {
            result =  openStackService.detachInterface(credentialInfo, project, serverId, action.get("portId"));
        }

        if(result) {
            return (ServerInfo) openStackService.getServer(credentialInfo, project, serverId, false);
        }

        return new ServerInfo();
    }

    @RequestMapping(value = "/servers/{id}/floatingip", method = RequestMethod.POST)
    @ResponseBody
    public ServerInfo actionServerFloatingIp(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId,
            @RequestBody Map<String, String> action
            ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        Boolean result = false;

        if(action.get("action").equals("CONNECT_FLOATING_IP")) {
            result = openStackService.addFloatingIpToServer(credentialInfo, project, serverId, action.get("interfaceIp"), action.get("floatingIp"));
        } else if(action.get("action").equals("DISCONNECT_FLOATING_IP")) {
            result = openStackService.removeFloatingIpToServer(credentialInfo, project, serverId, action.get("floatingIp"));
        }

        if(result) {
            return (ServerInfo) openStackService.getServer(credentialInfo, project, serverId, false);
        }

        return new ServerInfo();
    }

    @RequestMapping(value = "/servers/{id}/resize", method = RequestMethod.POST)
    @ResponseBody
    public ServerInfo changeFlavor(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String serverId,
            @RequestBody Map<String, String> data
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.changeFlavor(credentialInfo, project, serverId, data.get("flavorId"));
    }

    @RequestMapping(value = "/resource", method = RequestMethod.GET)
    @ResponseBody
    public ResourceInfo getUsage(
            @RequestHeader(value = "credential") String credential
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getResourceUsage(credentialInfo);
    }

    @RequestMapping(value = "/hypervisors", method = RequestMethod.GET)
    @ResponseBody
    public List<? extends Hypervisor> getHypervisors(
            @RequestHeader(value = "credential") String credential
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getHypervisors(credentialInfo);
    }

    @RequestMapping(value = "/hypervisorstatistics", method = RequestMethod.GET)
    @ResponseBody
    public HypervisorStatistics getHypervisorStatistics(
            @RequestHeader(value = "credential") String credential
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getHypervisorStatistics(credentialInfo);
    }

    @RequestMapping(value = "/floatingippools", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFloatingIpPoolNames(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.getFloatingIpPoolNames(credentialInfo, project);
    }

    @RequestMapping(value = "/floatingip", method = RequestMethod.POST)
    @ResponseBody
    public FloatingIpInfo allocateFloatingIp(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestBody Map<String, String> params
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.allocateFloatingIp(credentialInfo, project, params.get("poolName"));
    }

    @RequestMapping(value = "/floatingip/{floatingIpId}", method = RequestMethod.DELETE)
    public void deallocateFloatingIp(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "floatingIpId") String floatingIpId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        openStackService.deallocateFloatingIp(credentialInfo, project, floatingIpId);
    }

    @RequestMapping(value = "/meter/servers", method = RequestMethod.GET)
    @ResponseBody
    public List<MeterServerAccumulateInfo> getMeterServerAccumulateInfos(
            @RequestHeader(value = "credential") String credential
    ) {

        logger.error("/meter/servers credential ? : {}", credential);
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return meterService.getMeterServerAccumulates(new HashMap<String, Object>(){{
            put("cloudTarget", credentialInfo.getUrl());
        }});
    }

    @RequestMapping(value = "/meter/servers/billing", method = RequestMethod.GET)
    @ResponseBody
    public List<MeterServerAccumulateBillingInfo> getMeterServerBillingAccumulateInfos(
            @RequestHeader(value = "credential") String credential
    ) {

        logger.error("/meter/servers/billing credential ? : {}", credential);
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return meterService.getMeterServerBillingAccumulateInfos(new HashMap<String, Object>(){{
            put("cloudTarget", credentialInfo.getUrl());
        }});
    }

    @RequestMapping(value = "/meter/servers/{serverId}", method = RequestMethod.GET)
    @ResponseBody
    public List<MeterServerInfo> getMeterServerInfos(
            @RequestHeader(value = "credential") String credential,
            @PathVariable(value = "serverId") String serverId
    ) {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return meterService.getMeterServers(new HashMap<String, Object>(){{
            put("cloudTarget", credentialInfo.getUrl());
            put("instanceId", serverId);
        }});
    }

    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Boolean> checkValidate(
            @RequestHeader(value = "credential") String credential
    ) {
        logger.error("★★★★★★★★★★★★★★★★★ credential  정보는 = " + credential);

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        logger.error("★★★★★★★★★★★★★★★★★ credentialInfo  정보는 = " + credentialInfo);
        return new HashMap<String, Boolean> (){{
            put("result", openStackService.validateCredential(credentialInfo));
        }};
    }
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public @ResponseBody
    ImageInfo createImage(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @RequestBody CreateImageInfo createImageInfo
    ) throws MalformedURLException {

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.createImage(credentialInfo, project, createImageInfo);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/images/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody ImageInfo deleteImage(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String imageId
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return openStackService.deleteImage(credentialInfo, project, imageId);
    }

    @RequestMapping(value = "/networks/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<NetworkInfo> getNetwork(
            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String project,
            @PathVariable(value = "id") String id,
            @RequestParam(required = false) Boolean webCheck
    ) {
        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));


        logger.error("apiopenstack, openstackController, webCheck is ? : {}", webCheck);

        if(webCheck == null) {
            webCheck = false;
        }

        //logger.info("openstackgetNetwork : ",credentialInfo+"/"+project+"/"+id);
        return openStackService.getNetwork(credentialInfo, project, id, webCheck);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteCredential(@PathVariable(required = true) String id,
                                                 @RequestParam(required = false) String project,
                                                 @RequestHeader(value = "credential") String credential) {

        String type = "openstack";

        Map<String, Object> params = new HashMap<>();
        params.put("type", id);

        CredentialInfo credentialInfo = credentialService.getCredentialInfo(params);

//        JSONObject test = new JSONObject();
//        test.put("id", credentialInfo.getType());

        openStackService.deleteCredential(credentialInfo, project, id, credentialDao);

        //삭제 성공 시 리턴 값이 없다 하면
        return null;
        //삭제 성공 시 리턴 값이 필요 하다 하면
//        return test;
    }

    @RequestMapping(value = "/cctvs", method = RequestMethod.GET)
    public @ResponseBody
    List<CctvInfo> getCctvs(
                            @RequestParam(required = false) Integer page,
                            @RequestParam(required = false) Integer rows,
                            @RequestParam(defaultValue = "name") String sidx,
                            @RequestParam(defaultValue = "asc") String sord,
                            @RequestParam(required = false) String q0,
                            @RequestParam(required = false) String q1) {

        System.out.println("#@!#!@#!@#!@#@!RR!#F!#$@D!D#@");


        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("page", page);
        params.put("rows", rows);

        return openStackService.getCctvs(params);
    }

    // cpu 사용량 => 현재 usage_idle 로 설정되어있어 보완 필요
    @RequestMapping(value = {"/monitoring/cpu_usage"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getCpuUsageMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm1";
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm1\") |> range(start: - "+ Time_range +") |> filter(fn: (r) => " +
                    "r[\"_measurement\"] == \"cpu\") |> filter(fn: (r) => r[\"cpu\"] == \"cpu-total\") " +
                    "|> filter(fn: (r) => r[\"_field\"] == \"usage_idle\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 1]);
            data.put("host", "innogrid-test");
            data.put("start", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 7]);
            data.put("end", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 6]);
            data.put("now", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 5]);
            data.put("value", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 4]);
            array.add(data);
        }
        logger.error("array : {} ", array);
        return array;
    }

    // memory 사용량
    @RequestMapping(value = {"/monitoring/mem_usage"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getMemUsageMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm1";
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm1\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"mem\") |> filter(fn: (r) => r[\"_field\"] == \"used_percent\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "");
            data.put("host", "innogrid-test");
            data.put("start", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 3]);
            array.add(data);
        }
        return array;
    }

    // memory 총량
    @RequestMapping(value = {"/monitoring/mem_total"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getMemTotalMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm1";
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");

        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm1\") |> range(start: -" + Time_range + ")|> filter(fn: (r) => r[\"_measurement\"] == \"mem\") |> filter(fn: (r) => r[\"_field\"] == \"total\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "");
            data.put("host", "innogrid-test");
            data.put("start", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 3]);
            array.add(data);
        }
        return array;
    }

    // disk 사용량
    @RequestMapping(value = {"/monitoring/disk_usage"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDiskUsageMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm1";
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm1\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"disk\") |> filter(fn: (r) => r[\"_field\"] == \"used_percent\") |> filter(fn: (r) => r[\"device\"] == \"vda1\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("/").length; i++) {
            JSONObject data = new JSONObject();
            data.put("host", "innogrid-test");
            data.put("device", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 4]);
            data.put("start", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 10]);
            data.put("end", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 9]);
            data.put("now", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 8]);
            data.put("value", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 7]);

            array.add(data);
        }
        return array;
    }

    // disk 총량
    @RequestMapping(value = {"/monitoring/disk_total"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDiskTotalMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm1";
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm1\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"disk\") |> filter(fn: (r) => r[\"_field\"] == \"total\") |> filter(fn: (r) => r[\"device\"] == \"vda1\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("/").length; i++) {
            JSONObject data = new JSONObject();
            data.put("host", "innogrid-test");
            data.put("device", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 4]);
            data.put("start", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 10]);
            data.put("end", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 9]);
            data.put("now", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 8]);
            data.put("value", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 7]);

            array.add(data);
        }
        return array;
    }

    // diskio writes_bytes 정보
    @RequestMapping(value = {"/monitoring/diskio_wb"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDiskIoWriteMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        HttpUtils htppUtils = new HttpUtils();

        String url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm1";
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm1\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"diskio\") |> filter(fn: (r) => r[\"_field\"] == \"write_bytes\") |> filter(fn: (r) => r[\"host\"] == \"innogrid-test\") |> filter(fn: (r) => r[\"name\"] == \"vda\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("vda").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "vda");
            data.put("host", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 1]);
            data.put("start", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 7]);
            data.put("end", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 6]);
            data.put("now", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 5]);
            data.put("value", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 4]);

            array.add(data);
        }
        return array;
    }

    @RequestMapping(value = {"/monitoring/diskio_rb"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDiskIoReadMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model, CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
        response.setHeader("Access-Control-Allow-Origin", "*");

        HttpUtils htppUtils = new HttpUtils();

        String url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm1";
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid\") |> range(start: " + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"diskio\") |> filter(fn: (r) => r[\"_field\"] == \"read_bytes\") |> filter(fn: (r) => r[\"host\"] == \"innogrid-test\") |> filter(fn: (r) => r[\"name\"] == \"sda\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("sda").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "sda");
            data.put("host", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 1]);
            data.put("start", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 7]);
            data.put("end", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 6]);
            data.put("now", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 5]);
            data.put("value", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 4]);

            array.add(data);
        }
        return array;
    }

    @RequestMapping(value = {"/monitoring/cpu_core"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getCpuCoreMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm1";
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm1\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => " +
                    "r[\"_measurement\"] == \"system\") |> filter(fn: (r) => r[\"_field\"] == \"n_cpus\") " +
                    "|> filter(fn: (r) => r[\"host\"] == \"innogrid-test\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 1]);
            data.put("host", "innogrid-test");
            data.put("start", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("innogrid-test")[i]).split(",")[(result.split("innogrid-test")[i]).split(",").length - 3]);
            array.add(data);
        }
        return array;
    }

    class HttpUtils {
        public HttpURLConnection getHttpURLConnection(String strUrl, String method) {
            URL url;
            HttpURLConnection conn = null;
            try {
                url = new URL(strUrl);

                conn = (HttpURLConnection) url.openConnection(); //HttpURLConnection 객체 생성
                conn.setRequestMethod(POST); //Method 방식 설정. GET/POST/DELETE/PUT/HEAD/OPTIONS/TRACE
                conn.setConnectTimeout(5000); //연결제한 시간 설정. 5초 간 연결시도
                conn.setRequestProperty("Content-Type", "application/vnd.flux");
                conn.setRequestProperty("Authorization", "Token q7yGkB-dz4MkDY-AMKSnOL2blfuDssrk7WRPwRZB0igyqSVQmwGSRvT8J_ueX9sYXSzo5qYsiYNF5sVIJNK9CA==");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }

            return conn;

        }

        public String getHttpRespons(HttpURLConnection conn) {
            StringBuilder sb = null;

            try {
                if(conn.getResponseCode() == 200) {
                    // 정상적으로 데이터를 받았을 경우
                    //데이터 가져오기
                    System.out.println(conn.getResponseCode());
                    sb = readResopnseData(conn.getInputStream());
                    System.out.println(sb);
                }else{
                    // 정상적으로 데이터를 받지 못했을 경우

                    //오류코드, 오류 메시지 표출
                    System.out.println(conn.getResponseCode());
                    System.out.println(conn.getResponseMessage());
                    //오류정보 가져오기
                    sb = readResopnseData(conn.getErrorStream());
                    System.out.println("error : " + sb.toString());
                    return null;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally {
                conn.disconnect(); //연결 해제
            };
            if(sb == null) return null;

            return sb.toString();
        }

        public StringBuilder readResopnseData(InputStream in) {
            if(in == null ) return null;

            StringBuilder sb = new StringBuilder();
            String line = "";

            try (InputStreamReader ir = new InputStreamReader(in);
                 BufferedReader br = new BufferedReader(ir)){
                while( (line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return sb;
        }
    }
}