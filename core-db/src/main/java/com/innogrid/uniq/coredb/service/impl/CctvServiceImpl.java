package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.CctvInfo;
import com.innogrid.uniq.core.model.PermissionInfo;
import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.CctvDao;
import com.innogrid.uniq.coredb.dao.PermissionDao;
import com.innogrid.uniq.coredb.dao.RoleDao;
import com.innogrid.uniq.coredb.dao.UserDao;
import com.innogrid.uniq.coredb.service.ActionService;
import com.innogrid.uniq.coredb.service.CctvService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author wss
 * @date 2019.4.03
 * @brief
 */
@Service
@Transactional
public class CctvServiceImpl implements CctvService {
    private final static Logger logger = LoggerFactory.getLogger(CctvServiceImpl.class);

    @Autowired
    private CctvDao cctvDao;

    @Override
    public List<CctvInfo> getCctvs(Map<String, Object> params) {
        List<CctvInfo> list = cctvDao.getCctvs(params);

        return list;
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        int totalCnt = cctvDao.getTotal(params);

        return totalCnt;
    }

    @Override
    public CctvInfo getCctvInfo(Map<String, Object> params) {
        CctvInfo info = cctvDao.getCctvInfo(params);

        return info;
    }

    @Override
    public List<CctvInfo> getCctvs2(Map<String, Object> params) {
        List<CctvInfo> list = cctvDao.getCctvs2(params);

        return list;
    }

    @Override
    public int getTotal2(Map<String, Object> params) {
        int totalCnt = cctvDao.getTotal2(params);

        return totalCnt;
    }

    @Override
    public CctvInfo getCctvInfo2(Map<String, Object> params) {
        CctvInfo info = cctvDao.getCctvInfo2(params);

        return info;
    }
}
