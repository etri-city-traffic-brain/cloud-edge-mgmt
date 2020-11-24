package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.model.HistoryInfo;
import com.innogrid.uniq.coredb.dao.HistoryDao;
import com.innogrid.uniq.coredb.service.HistoryService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 15. 4. 3.
 */
@Service
@Transactional
public class HistoryServiceImpl implements HistoryService {
    private final static Logger logger = LoggerFactory.getLogger(HistoryServiceImpl.class);

    @Autowired
    HistoryDao historyDao;

    @Override
    public List<HistoryInfo> getHistorys(Map<String, Object> params) {
        List<HistoryInfo> list = historyDao.getHistorys(params);

        if(list == null) return new ArrayList<>();
        return list;
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        int historyTotalCnt = historyDao.getTotal(params);

        return historyTotalCnt;
    }

    @Override
    public HistoryInfo getHistory(Map<String, Object> params) {
        HistoryInfo info = historyDao.getHistory(params);

        return info;
    }

    @Override
    public int createHistory(HistoryInfo info) {
        int result = historyDao.insertHistory(info);

        return result;
    }
}
