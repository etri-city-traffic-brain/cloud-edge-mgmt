package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.CctvInfo;

import java.util.List;
import java.util.Map;

/**
 * @author ksh
 * @date 2021.4.03
 * @brief Role 관리 서비스
 */
public interface CctvService {
    List<CctvInfo> getCctvs(Map<String, Object> params);

    int getTotal(Map<String, Object> params);

    CctvInfo getCctvInfo(Map<String, Object> params);

    List<CctvInfo> getCctvs2(Map<String, Object> params);

    int getTotal2(Map<String, Object> params);

    CctvInfo getCctvInfo2(Map<String, Object> params);
}
