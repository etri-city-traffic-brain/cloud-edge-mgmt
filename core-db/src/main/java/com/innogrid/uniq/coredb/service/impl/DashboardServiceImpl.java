package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.model.ServiceDashboardInfo;
import com.innogrid.uniq.coredb.dao.DashboardDao;
import com.innogrid.uniq.coredb.service.DashboardService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kkm on 19. 4. 24
 */
@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {
    private final static Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private DashboardDao dashboardDao;

    @Override
    public List<ServiceDashboardInfo> serviceDashboards(Map<String, Object> params) {
        List<ServiceDashboardInfo> lists = dashboardDao.getDashboards(params);

        lists.add(new ServiceDashboardInfo("ETRI_EDGE"));
        lists.add(new ServiceDashboardInfo("REXGEN_EDGE"));
        if(lists == null) return new ArrayList<>();
        return lists;
    }

    @Override
    public ServiceDashboardInfo serviceDashboard(Map<String, Object> params) {
        ServiceDashboardInfo info = dashboardDao.getDashboard(params);

        return info;
    }

    @Override
    public int updateServiceDashboard(ServiceDashboardInfo info) {
        int result = dashboardDao.updateDashboard(info);

        return result;
    }

    @Override
    public int createServiceDashboard(ServiceDashboardInfo info) {
        int result = dashboardDao.createDashboard(info);

        return result;
    }

    @Override
    public int getIDCount(String id) {
        int idCnt = dashboardDao.getIDCount(id);

        return idCnt;
    }
}
