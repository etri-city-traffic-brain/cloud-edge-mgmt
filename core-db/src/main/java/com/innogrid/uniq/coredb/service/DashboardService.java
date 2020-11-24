package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.ServiceDashboardInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.4.24
 * @brief Dashboard 서비스
 */
public interface DashboardService {


    /**
     * @author wss
     * @date 2019.4.26
     * @param params Dashboard 목록 정보 조회에 대한 파라미터
     * @brief Service Dashboard Server List 조회
     * @return Service Dashboard Server 목록
     */
    public List<ServiceDashboardInfo> serviceDashboards(Map<String, Object> params);

    /**
     * @author kkm
     * @date 2019.4.24
     * @param params Dashboard 목록 정보 조회에 대한 파라미터
     * @brief Service Dashboard 조회
     */
    public ServiceDashboardInfo serviceDashboard(Map<String, Object> params);

    /**
     * @param info Dashboard 수정 정보
     * @return 수정된 Dashboard 정보
     * @author kkm
     * @date 2019.4.24
     * @brief ServiceDashboard 수정
     */
    public int updateServiceDashboard(ServiceDashboardInfo info);

    /**
     * @param info Dashboard 생성 정보
     * @return 생성된 Dashboard 정보
     * @author kkm
     * @date 2019.4.24
     * @brief ServiceDashboard 생성
     */
    public int createServiceDashboard(ServiceDashboardInfo info);

    /**
     * @param id Dashboard 개수 정보 조회에 대한 파라미터
     * @return int Dashboard ID 개수
     * @author kkm
     * @date 2019.4.24
     * @brief Dashboard ID 개수 조회
     */
    public int getIDCount(String id);
}
