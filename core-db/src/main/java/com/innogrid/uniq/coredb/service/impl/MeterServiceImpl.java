package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;
import com.innogrid.uniq.coredb.dao.MeterDao;
import com.innogrid.uniq.coredb.service.MeterService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kkm on 19. 5. 30.
 */
@Service
@Transactional
public class MeterServiceImpl implements MeterService {
    private final static Logger logger = LoggerFactory.getLogger(MeterServiceImpl.class);

    @Autowired
    private MeterDao meterDao;

    @Override
    public List<MeterServerInfo> getMeterServers(Map<String, Object> params) {
        List<MeterServerInfo> list = meterDao.getMeterServers(params);

        if(list == null) return  new ArrayList<>();
        return list;
    }

    @Override
    public MeterServerInfo getMeterServer(Map<String, Object> params) {
        MeterServerInfo info = meterDao.getMeterServer(params);

        return info;
    }

    @Override
    public int updateMeterServer(MeterServerInfo info) {
        int result = meterDao.updateMeterServer(info);

        return result;
    }

    @Override
    public int createMeterServer(MeterServerInfo info) {
        int result = meterDao.createMeterServer(info);

        return result;
    }

    @Override
    public int getMeterServerIDCount(MeterServerInfo info) {
        int idCnt = meterDao.getMeterServerIDCount(info);

        return idCnt;
    }

    @Override
    public List<MeterServerAccumulateInfo> getMeterServerAccumulates(Map<String, Object> params) {
        List<MeterServerAccumulateInfo> list = meterDao.getMeterServerAccumulates(params);

        if(list == null) return new ArrayList<>();
        return list;
    }

    @Override
    public MeterServerAccumulateInfo getMeterServerAccumulate(Map<String, Object> params) {
        MeterServerAccumulateInfo info = meterDao.getMeterServerAccumulate(params);

        return info;
    }

    @Override
    public int updateMeterServerAccumulate(MeterServerAccumulateInfo info) {
        int result = meterDao.updateMeterServerAccumulate(info);

        return result;
    }

    @Override
    public int createMeterServerAccumulate(MeterServerAccumulateInfo info) {
        int result = meterDao.createMeterServerAccumulate(info);

        return result;
    }

    @Override
    public int getMeterServerAccumulateIDCount(MeterServerAccumulateInfo info) {
        int idCnt = meterDao.getMeterServerAccumulateIDCount(info);

        return idCnt;
    }
}
