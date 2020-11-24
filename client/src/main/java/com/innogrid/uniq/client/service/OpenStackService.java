package com.innogrid.uniq.client.service;

import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coreopenstack.model.*;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.3.19
 * @brief 오픈스택 API 호출 서비스
 */
public interface OpenStackService {

    /**
     * @author wss
     * @date 2019.3.19
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 서버 조회
     */
    List<ServerInfo> getServers(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author ksh1006
     * @date 2020.5.15
     * @param params 오픈스택 목록 상세 정보 조회에 대한 파라미터
     * @brief 오픈스택 서버 상세 조회
     */
    public List<ServerInfo> getServers_Detail_openstack(String cloudId, String ServerId, String token);

    /**
     * @param info 오픈스택 서버 변경 파라미터
     * @param command 변경 하려는 정보에 대한 명령 (ex, 스팩 변경, 이름 등등)
     * @author wss
     * @date 2019.3.19
     * @brief 서버 수정
     */
    void updateServer(String cloudId, ServerInfo info, String command, UserInfo reqInfo, String token);

    /**
     * @param id 서버 ID
     * @return ServerInfo 조회된 단일 서버 정보
     * @author wss
     * @date 2019.3.19
     * @brief 단일 서버 조회
     */
    ServerInfo getServer(String cloudId, String id, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.3.19
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Image 조회
     */
    List<ImageInfo> getImages(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.3.25
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 KeyPair 조회
     */
    List<KeyPairInfo> getKeyPairs(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.3.19
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Flavor 조회
     */
    List<FlavorInfo> getFlavors(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.3.22
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Volume 조회
     */
    List<VolumeInfo> getVolumes(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.10.14
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 VolumeType 조회
     */
    Object getVolumeTypes(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.12
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Backup 조회
     */
    List<VolumeBackupInfo> getBackups(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.12
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Snapshot 조회
     */
    List<VolumeSnapshotInfo> getSnapshots(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author yjl
     * @date 2020.02.07
     * @param cloudId 요청하는 cloud 정보
     * @param id Snapshot ID
     * @Return VolumeSnapshot info
     * @brief 오픈스택 Snapshot delete
     */
    VolumeSnapshotInfo deleteSnapshot(String cloudId, String id, UserInfo reqInfo, String token);


    /**
     * @author wss
     * @date 2019.3.25
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Network 조회
     */
    List<NetworkInfo> getNetworks(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author ksh1006
     * @date 2020.5.15
     * @param params 오픈스택 네트워크 상세 정보 조회에 대한 파라미터
     * @brief 오픈스택 Network 상세 정보 조회
     */
    public NetworkInfo getNetworks_Detail_openstack(String cloudId, String networkId, String token);

    /**
     * @author wss
     * @date 2019.3.25
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Subnet 조회
     */
    List<SubnetInfo> getSubnets(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);


    /**
     * @author wss
     * @date 2019.3.25
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Router 조회
     */
    List<RouterInfo> getRouters(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.15
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 SecurityGroup 조회
     */
    List<SecurityGroupInfo> getSecurityGroups(String cloudId, Map<String, Object> params, Boolean project, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.15
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 FloatingIp 조회
     */
    List<FloatingIpInfo> getFloatingIps(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.16
     * @param cloudId 요청하는 cloud 정보
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 zone list 조회
     */
    List<AvailabilityZoneInfo> getZones(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.01
     * @param params 오픈스택 목록 정보 조회에 대한 파라미터
     * @brief 오픈스택 Project 조회
     */
    List<ProjectInfo> getProjects(String cloudId, Map<String, Object> params, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.10
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @param action Server에 대한 Action
     * @Return server info
     * @brief 오픈스택 Server action
     */
    ServerInfo action(String cloudId, String id, String action, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.15
     * @param cloudId 요청하는 cloud 정보
     * @param createData Server 생성 Data
     * @Return server info
     * @brief 오픈스택 Server Create
     */
    ServerInfo createServer(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.10
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @param snapshotName Snapshot Name
     * @Return server info
     * @brief 오픈스택 Server Snapshot Create
     */
    String createServerSnapshot(String cloudId, String id, String snapshotName, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.11
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @Return vnc url
     * @brief 오픈스택 Server VNC URL
     */
    String getServerVNCConsoleURL(String cloudId, String id, UserInfo reqInfo, String token);

    /**
     * @author hso
     * @date 2019.4.30
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @param params 메트릭 조건
     * @Return vnc url
     * @brief 오픈스택 Server VNC URL
     */
    Object getServerMetric(String cloudId, String id, UserInfo reqInfo, Map<String, Object> params, String token);

    /**
     * @author wss
     * @date 2019.4.11
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @param line line 수
     * @Return vnc url
     * @brief 오픈스택 Server console output
     */
    String getServerConsoleOutput(String cloudId, String id, int line, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.11
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @Return action log list
     * @brief 오픈스택 Server console output
     */
    List<ActionLogInfo> getServerActionLog(String cloudId, String id, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.11
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @Return volume list
     * @brief 오픈스택 Server Volume 정보
     */
    List<VolumeInfo> getServerVolumes(String cloudId, String id, UserInfo reqInfo, String token);

    /**
     * @author ksh1006
     * @date 2020.5.15
     * @param params Openstack 볼륨 상세 정보 조회에 대한 파라미터
     * @brief Openstack Volume 상세 정보 조회
     */
    public VolumeInfo getVolumes_Detail_openstack(String cloudId, String volumeId, String token);

    /**
     * @author wss
     * @date 2019.4.18
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @param volumeId volume ID
     * @Return Volume info
     * @brief 오픈스택 Server volume attach
     */
    VolumeInfo attachVolume(String cloudId, String id, String volumeId, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.18
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @param volumeId volume ID
     * @Return Volume info
     * @brief 오픈스택 Server volume detach
     */
    VolumeInfo detachVolume(String cloudId, String id, String volumeId, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.19
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @param networkId network ID
     * @Return Boolean 성공 여부
     * @brief 오픈스택 Server interface attach
     */
    Boolean attachInterface(String cloudId, String id, String networkId, String projectId, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.19
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @param portId port ID
     * @Return Boolean 성공 여부
     * @brief 오픈스택 Server interface detach
     */
    Boolean detachInterface(String cloudId, String id, String portId, String projectId, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.19
     * @param cloudId 요청하는 cloud 정보
     * @param id Server ID
     * @Return Server interface list
     * @brief 오픈스택 Server interface list
     */
    Object getServerInterface(String cloudId, String id, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.22
     * @param cloudId 요청하는 cloud 정보
     * @param serverId Server ID
     * @param projectId Server project Id
     * @param interfaceIp interfaceIp
     * @param floatingIp floatingIp
     * @param reqInfo 요청 유저 정보
     * @Return 성공 여부
     * @brief 오픈스택 Server 에 FloatingIp 추가
     */
    Boolean addFloatingIpToServer(String cloudId, String serverId, String interfaceIp, String floatingIp, String projectId, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.22
     * @param cloudId 요청하는 cloud 정보
     * @param serverId Server ID
     * @param projectId Server project Id
     * @param floatingIp floatingIp
     * @param reqInfo 요청 유저 정보
     * @Return 성공 여부
     * @brief 오픈스택 Server 에 FloatingIp 제거
     */
    Boolean removeFloatingIpToServer(String cloudId, String serverId, String floatingIp, String projectId, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.22
     * @param cloudId 요청하는 cloud 정보
     * @param poolName 풀 이름
     * @param reqInfo 요청 유저 정보
     * @Return FloatingIpInfo info
     * @brief 오픈스택 Pool 에 FloatingIp 추가
     */
    FloatingIpInfo allocateFloatingIp(String cloudId, String poolName, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.22
     * @param cloudId 요청하는 cloud 정보
     * @param floatingIpId FloatingIp ID
     * @param reqInfo 요청 유저 정보
     * @Return 성공 여부
     * @brief 오픈스택 Pool 에 FloatingIp 제거
     */
    Boolean deallocateFloatingIp(String cloudId, String floatingIpId, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.22
     * @param cloudId 요청하는 cloud 정보
     * @param reqInfo 요청 유저 정보
     * @Return Pool 리스트
     * @brief 오픈스택 Pool 리스트 조회
     */
    List<String> getFloatingIpPoolNames(String cloudId, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.4.22
     * @param cloudId 요청하는 cloud 정보
     * @Return Hypervisor 리스트
     * @brief 오픈스택 Hypervisor 리스트 조회
     */
    List<? extends Hypervisor> getHypervisors(String cloudId, String token);

    /**
     * @author wss
     * @date 2019.4.22
     * @param cloudId 요청하는 cloud 정보
     * @Return HypervisorStatistics
     * @brief 오픈스택 HypervisorStatistics 조회
     */
    HypervisorStatistics getHypervisorStatistics(String cloudId, String token);

    /**
     * @author wss
     * @date 2019.4.22
     * @param cloudId 요청하는 cloud 정보
     * @Return ResourceInfo
     * @brief 오픈스택 리소스 사용량 조회
     */
    ResourceInfo getResourceUsage(String cloudId, String token);

    /**
     * @author wss
     * @date 2019.10.10
     * @param cloudId 요청하는 cloud 정보
     * @param createData volume 생성 Data
     * @Return volume info
     * @brief 오픈스택 volume Create
     */
    VolumeInfo createVolume(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.10.10
     * @param cloudId 요청하는 cloud 정보
     * @param id Volume ID
     * @Return Volume info
     * @brief 오픈스택 Volume delete
     */
    VolumeInfo deleteVolume(String cloudId, String id, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.10.16
     * @param cloudId 요청하는 cloud 정보
     * @param createData network 생성 Data
     * @Return volume info
     * @brief 오픈스택 network Create
     */
    NetworkInfo createNetwork(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.10.16
     * @param cloudId 요청하는 cloud 정보
     * @param id network ID
     * @Return Network info
     * @brief 오픈스택 network delete
     */
    NetworkInfo deleteNetwork(String cloudId, String id, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2019.10.16
     * @param cloudId 요청하는 cloud 정보
     * @param id network ID
     * @param subnetId network ID
     * @Return Subnet info
     * @brief 오픈스택 subnet delete
     */
    SubnetInfo deleteSubnet(String cloudId, String id, String subnetId, UserInfo reqInfo, String token);

    /**
     * @author khk
     * @date 2019.11.28
     * @brief VM 모니터링 메트릭 정보 조회 (InfluxDB)
     * @param requestMetricInfo InfluxDB 조회 시 필요한 메트릭 쿼리 정보
     */
    Map<String, Object> getInfluxMetric(Map<String, Object> requestMetricInfo, String token);

    /**
     * @author wss
     * @date 2020.02.05
     * @param cloudId 요청하는 cloud 정보
     * @param createData image 생성 Data
     * @Return image info
     * @brief 오픈스택 image Create
     */
    ImageInfo createImage(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2020.02.07
     * @param cloudId 요청하는 cloud 정보
     * @param id image ID
     * @Return Image info
     * @brief 오픈스택 image delete
     */
    ImageInfo deleteImage(String cloudId, String id, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2020.02.27
     * @param cloudId 요청하는 cloud 정보
     * @param createData 생성 정보
     * @Return KeyPairInfo
     * @brief 오픈스택 Keypair 생성
     */
    KeyPairInfo createKeypair(String cloudId, Map<String, Object> createData, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2020.02.27
     * @param cloudId 요청하는 cloud 정보
     * @param keypairName Keypair 이름
     * @Return KeyPairInfo
     * @brief 오픈스택 Keypair 삭제
     */
    KeyPairInfo deleteKeypair(String cloudId, String keypairName, UserInfo reqInfo, String token);

    /**
     * @author wss
     * @date 2020.02.27
     * @param cloudId 요청하는 cloud 정보
     * @param serverId 서버 아이디
     * @param flavorId Flavor 아이디
     * @Return ServerInfo
     * @brief 오픈스택 resize
     */
    ServerInfo changeFlavor(String cloudId, String serverId, String flavorId, UserInfo reqInfo, String token);

}
