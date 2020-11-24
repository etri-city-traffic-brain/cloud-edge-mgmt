package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.HistoryInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 15. 4. 3.
 */
public interface HistoryService {
    public List<HistoryInfo> getHistorys(Map<String, Object> params);

    public int getTotal(Map<String, Object> params);

    public HistoryInfo getHistory(Map<String, Object> params);

    public int createHistory(HistoryInfo info);
}
