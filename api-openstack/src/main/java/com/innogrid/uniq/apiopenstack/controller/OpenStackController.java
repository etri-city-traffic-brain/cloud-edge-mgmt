package com.innogrid.uniq.apiopenstack.controller;

import com.innogrid.uniq.apiopenstack.service.OpenStackService;
import com.innogrid.uniq.core.model.CctvInfo;
import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;
import com.innogrid.uniq.core.util.AES256Util;
import com.innogrid.uniq.core.util.ObjectSerializer;
import com.innogrid.uniq.coredb.dao.CredentialDao;
import com.innogrid.uniq.coredb.service.CredentialService;
import com.innogrid.uniq.coredb.service.MeterService;
import com.innogrid.uniq.coreopenstack.model.*;
import net.minidev.json.JSONObject;
import org.openstack4j.model.compute.InterfaceAttachment;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.openstack4j.model.storage.block.VolumeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        CredentialInfo credentialInfo = ObjectSerializer.deserializedData(aes256Util.decrypt(credential));

        return meterService.getMeterServerAccumulates(new HashMap<String, Object>(){{
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

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("page", page);
        params.put("rows", rows);

        return openStackService.getCctvs(params);
    }
}