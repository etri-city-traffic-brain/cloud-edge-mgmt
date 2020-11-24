package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.HistoryInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 15. 4. 3.
 */
public interface HistoryDao {
    public List<HistoryInfo> getHistorys(Map<String, Object> params);

    public HistoryInfo getHistory(Map<String, Object> params);

    public int getTotal(Map<String, Object> params);

    public int insertHistory(HistoryInfo info);

    public int deleteHistory(HistoryInfo info);
}
