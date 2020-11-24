package com.innogrid.uniq.apiopenstack.service;

import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.coredb.dao.CredentialDao;
import com.innogrid.uniq.coreopenstack.model.*;
import org.openstack4j.model.compute.InterfaceAttachment;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.openstack4j.model.storage.block.VolumeType;

import java.net.MalformedURLException;

import java.util.List;

public interface OpenStackService {
    boolean validateCredential(CredentialInfo credentialInfo);

    List<ServerInfo> getServers(CredentialInfo credentialInfo, String projectId, Boolean webCheck);

    List<ServerInfo> getServers_Search(CredentialInfo credentialInfo, String projectId, String value, String type);

    List<ServerInfo> getServer(CredentialInfo credentialInfo, String projectId, String serverId, Boolean webCheck);

    List<ImageInfo> getImages(CredentialInfo credentialInfo, String projectId);

    List<ImageInfo> getImages(CredentialInfo credentialInfo, String projectId, Boolean active);

    List<KeyPairInfo> getKeyPairs(CredentialInfo credentialInfo, String projectId);

    List<FlavorInfo> getFlavors(CredentialInfo credentialInfo, String projectId);

    List<FlavorInfo> getFlavor(CredentialInfo credentialInfo, String flavorId, String projectId);

    List<VolumeInfo> getVolumes(CredentialInfo credentialInfo, String projectId, Boolean webCheck);

    List<VolumeInfo> getVolumes(CredentialInfo credentialInfo, String projectId, Boolean bootable, Boolean available, Boolean webCheck);

    List<VolumeInfo> getVolumes_Search(CredentialInfo credentialInfo, String projectId, Boolean bootable, Boolean available, String value, String type);

    List<VolumeInfo> getVolume(CredentialInfo credentialInfo, String projectId, String volumeId, Boolean webCheck);

    Object createVolume(CredentialInfo credentialInfo, String projectId, CreateVolumeInfo createVolumeInfo, Boolean webCheck);

    DeleteInfo deleteVolume(CredentialInfo credentialInfo, String projectId, String volumeId);

    List<? extends VolumeType> getVolumeTypes(CredentialInfo credentialInfo);

    List<VolumeBackupInfo> getBackups(CredentialInfo credentialInfo, String projectId);

    List<VolumeSnapshotInfo> getSnapshots(CredentialInfo credentialInfo, String projectId, Boolean available);

    List<VolumeSnapshotInfo> getSnapshotid(CredentialInfo credentialInfo, String projectId, String snapshotId, Boolean available);

    VolumeSnapshotInfo getSnapshot(CredentialInfo credentialInfo, String projectId, String snapshotId);

    VolumeSnapshotInfo createSnapsthot(CredentialInfo credentialInfo, String projectId);

    VolumeSnapshotInfo deleteSnapshot(CredentialInfo credentialInfo, String projectId, String snapshotId);

    VolumeSnapshotInfo updateSnapshot(CredentialInfo credentialInfo, String projectId, String snapshotId);

    List<NetworkInfo> getNetworks(CredentialInfo credentialInfo, String projectId, Boolean webCheck);

    List<NetworkInfo> getNetworks_Search(CredentialInfo credentialInfo, String projectId, String value, String type);

    List<NetworkInfo> getNetwork(CredentialInfo credentialInfo, String projectId, String networkId, Boolean webCheck);

    Object createNetwork(CredentialInfo credentialInfo, String projectId, CreateNetworkInfo createNetworkInfo, Boolean webCheck);

    DeleteInfo deleteNetwork(CredentialInfo credentialInfo, String projectId, String networkId);

    List<RouterInfo> getRouters(CredentialInfo credentialInfo, String projectId);

    RouterInfo getRouter(CredentialInfo credentialInfo, String projectId, String routerId);

    RouterInfo createRouter(CredentialInfo credentialInfo, String projectId);

    RouterInfo deleteRouter(CredentialInfo credentialInfo, String projectId, String routerId);

    public RouterInfo updateRouter(CredentialInfo credentialInfo, String projectId, String routerId);

    public List<SubnetInfo> getSubnets(CredentialInfo credentialInfo, String projectId, String networkId);

    SubnetInfo getSubnet(CredentialInfo credentialInfo, String projectId, String subnetId);

    SubnetInfo createSubnet(CredentialInfo credentialInfo, String projectId);

    SubnetInfo deleteSubnet(CredentialInfo credentialInfo, String projectId, String subnetId);

    List<SecurityGroupInfo> getSecurityGroups(CredentialInfo credentialInfo, String projectId);

    List<FloatingIpInfo> getFloatingIps(CredentialInfo credentialInfo, String projectId, Boolean down);

    List<AvailabilityZoneInfo> getZones(CredentialInfo credentialInfo, String projectId, String type);

    List<String> getFloatingIpPoolNames(CredentialInfo credentialInfo, String projectId);

    FloatingIpInfo allocateFloatingIp(CredentialInfo credentialInfo, String projectId, String poolName);

    Boolean deallocateFloatingIp(CredentialInfo credentialInfo, String projectId, String floatingIpId);

    Boolean addFloatingIpToServer(CredentialInfo credentialInfo, String projectId, String serverId, String interfaceIp, String floatingIp);

    Boolean removeFloatingIpToServer(CredentialInfo credentialInfo, String projectId, String serverId, String floatingIp);

    Boolean attachInterface(CredentialInfo credentialInfo, String projectId, String serverId, String networkId);

    Boolean detachInterface(CredentialInfo credentialInfo, String projectId, String serverId, String portId);

    List<? extends InterfaceAttachment> getServerInterface(CredentialInfo credentialInfo, String projectId, String serverId);

    List<ProjectInfo> getProjects(CredentialInfo credentialInfo);

    List<ProjectInfo> getProjectsInMemory(CredentialInfo credentialInfo);

    String getProjectName(CredentialInfo credentialInfo, String projectId);

    ProjectInfo getProject(CredentialInfo credentialInfo, String projectId);

    ServerInfo start(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo stop(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo rebootSoft(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo rebootHard(CredentialInfo credentialInfo, String projectId, String serverId);

    DeleteInfo delete(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo pause(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo unpause(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo lock(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo unlock(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo suspend(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo resume(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo rescue(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo unrescue(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo shelve(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo shelveOffload(CredentialInfo credentialInfo, String projectId, String serverId);

    ServerInfo unshelve(CredentialInfo credentialInfo, String projectId, String serverId);

    Object createServer(CredentialInfo credentialInfo, String projectId, CreateServerInfo createServerInfo,Boolean webCheck);

    String createServerSnapshot(CredentialInfo credentialInfo, String projectId, String serverId, String snapshotName);

    Object getServerMetric(CredentialInfo credentialInfo, RequestMetricInfo requestMetricInfo);

    String getServerVNCConsoleURL(CredentialInfo credentialInfo, String projectId, String serverId);

    String getServerConsoleOutput(CredentialInfo credentialInfo, String projectId, String serverId, int line);

    List<ActionLogInfo> getServerActionLog(CredentialInfo credentialInfo, String projectId, String serverId);

    List<VolumeInfo> getServerVolumes(CredentialInfo credentialInfo, String projectId, String serverId);

    VolumeInfo detachVolume(CredentialInfo credentialInfo, String projectId, String serverId, String volumeId);

    VolumeInfo attachVolume(CredentialInfo credentialInfo, String projectId, String serverId, String volumeId);

    List<? extends Hypervisor> getHypervisors(CredentialInfo credentialInfo);

    HypervisorStatistics getHypervisorStatistics(CredentialInfo credentialInfo);

    ResourceInfo getResourceUsage(CredentialInfo credentialInfo);

    ImageInfo getImage(CredentialInfo credentialInfo, String projectId, String imageId);

    ImageInfo createImage(CredentialInfo credentialInfo, String projectId, CreateImageInfo createImageInfo) throws MalformedURLException;

    ImageInfo deleteImage(CredentialInfo credentialInfo, String projectId, String imageId);

    KeyPairInfo createKeypair(CredentialInfo credentialInfo, String projectId, KeyPairInfo keyPairInfo);

    KeyPairInfo deleteKeypair(CredentialInfo credentialInfo, String projectId, String keypairName);

    ServerInfo changeFlavor(CredentialInfo credentialInfo, String projectId, String serverId, String flavorId);

    List<CredentialInfo> getCredential(List<CredentialInfo> list, String type);

    void deleteCredential(CredentialInfo info, String projectId, String credentialId, CredentialDao credentialDao);
}
