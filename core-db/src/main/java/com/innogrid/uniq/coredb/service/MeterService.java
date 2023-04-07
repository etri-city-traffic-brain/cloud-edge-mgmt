package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.MeterServerAccumulateBillingInfo;
import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.5.30
 * @brief Metering 서비스
 */
public interface MeterService {
    /**
     * @author kkm
     * @date 2019.5.30
     * @param params MeterServerInfo 목록 정보 조회에 대한 파라미터
     * @brief User 조회
     */
    public List<MeterServerInfo> getMeterServers(Map<String, Object> params);

    /**
     * @author kkm
     * @date 2019.5.30
     * @param params MeterServerInfo 목록 정보 조회에 대한 파라미터
     * @brief User 조회
     */
    public MeterServerInfo getMeterServer(Map<String, Object> params);


    /**
     * @param info MeterServer 수정 정보
     * @return 수정된 MeterServer 정보
     * @author kkm
     * @date 2019.5.30
     * @brief MeterServer 수정
     */
    public int updateMeterServer(MeterServerInfo info);

    /**
     * @param info MeterServer 생성 정보*
     * @return 생성된 MeterServer 정보
     * @author kkm
     * @date 2019.5.30
     * @brief MeterServer 생성
     */
    public int createMeterServer(MeterServerInfo info);

    /**
     * @param info MeterServer 개수 정보 조회에 대한 파라미터
     * @return int MeterServer ID 개수
     * @author ssa
     * @date 2023.2.15
     * @brief MeterServer ID 개수 조회
     */
    public int getMeterServerIDCount(MeterServerInfo info);

    /**
     * @author kkm
     * @date 2019.5.30
     * @param params MeterServerAccumulateInfo 목록 정보 조회에 대한 파라미터
     * @brief User 조회
     */
    public List<MeterServerAccumulateInfo> getMeterServerAccumulates(Map<String, Object> params);

    public List<MeterServerAccumulateBillingInfo> getMeterServerBillingAccumulateInfos(Map<String, Object> params);


    /**
     * @author kkm
     * @date 2019.5.30
     * @param params MeterServerAccumulateInfo 목록 정보 조회에 대한 파라미터
     * @brief User 조회
     */
    public MeterServerAccumulateInfo getMeterServerAccumulate(Map<String, Object> params);


    /**
     * @param info MeterServer 수정 정보
     * @return 수정된 MeterServer 정보
     * @author kkm
     * @date 2019.5.30
     * @brief MeterServer 수정
     */
    public int updateMeterServerAccumulate(MeterServerAccumulateInfo info);

    /**
     * @param info MeterServer 생성 정보*
     * @return 생성된 MeterServer 정보
     * @author kkm
     * @date 2019.5.30
     * @brief MeterServer 생성
     */
    public int createMeterServerAccumulate(MeterServerAccumulateInfo info);

    /**
     * @param info MeterServerAccumulate 개수 정보 조회에 대한 파라미터
     * @return int MeterServerAccumulate ID 개수
     * @author kkm
     * @date 2019.5.30
     * @brief MeterServerAccumulate ID 개수 조회
     */
    public int getMeterServerAccumulateIDCount(MeterServerAccumulateInfo info);
}
