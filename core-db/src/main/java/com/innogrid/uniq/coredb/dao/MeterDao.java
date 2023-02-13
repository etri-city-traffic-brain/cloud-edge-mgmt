package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.5.30
 * @brief
 */
public interface MeterDao {

    List<MeterServerInfo> getMeterServers(Map<String, Object> params);

    MeterServerInfo getMeterServer(Map<String, Object> params);

    int updateMeterServer(MeterServerInfo info);

    int createMeterServer(MeterServerInfo info);

    List<MeterServerAccumulateInfo> getMeterServerAccumulates(Map<String, Object> params);

    MeterServerAccumulateInfo getMeterServerAccumulate(Map<String, Object> params);

    int updateMeterServerAccumulate(MeterServerAccumulateInfo info);

    int createMeterServerAccumulate(MeterServerAccumulateInfo info);

    int getMeterServerAccumulateIDCount(MeterServerAccumulateInfo info);
}

