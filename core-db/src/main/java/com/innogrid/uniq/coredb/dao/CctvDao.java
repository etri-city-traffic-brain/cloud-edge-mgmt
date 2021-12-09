package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.CctvInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.03
 * @brief
 */
public interface CctvDao {
    List<CctvInfo> getCctvs(Map<String, Object> params);

    int getTotal(Map<String, Object> params);

    CctvInfo getCctvInfo(Map<String, Object> params);

    List<CctvInfo> getCctvs2(Map<String, Object> params);

    int getTotal2(Map<String, Object> params);

    CctvInfo getCctvInfo2(Map<String, Object> params);
}

