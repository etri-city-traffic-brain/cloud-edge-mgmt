package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.ServiceDashboardInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.4.24
 * @brief
 */
public interface DashboardDao {

    List<ServiceDashboardInfo> getDashboards(Map<String, Object> params);

    ServiceDashboardInfo getDashboard(Map<String, Object> params);

    int updateDashboard(ServiceDashboardInfo info);

    int createDashboard(ServiceDashboardInfo info);

    int deleteDashboard(ServiceDashboardInfo info);

    int getIDCount(String id);
}

