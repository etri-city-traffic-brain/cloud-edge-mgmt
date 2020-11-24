package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.client.util.Pagination;
import com.innogrid.uniq.coredb.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 15. 4. 3.
 */
@Controller
public class HistoryController {
    private static Logger logger = LoggerFactory.getLogger(HistoryController.class);

    @Autowired
    HistoryService historyService;

    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/historys", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> getHistoryList(HttpSession session,
                                      @RequestParam(required=false) Integer page,
                                      @RequestParam(required=false) Integer rows,
                                      @RequestParam(defaultValue="createdAt") String sidx,
                                      @RequestParam(defaultValue="desc") String sord,
                                      @RequestParam(required = false) String q0,
                                      @RequestParam(required = false) String q1,
                                      @RequestParam(required = false) String userId,
                                      @RequestParam(required = false) String target) {

        logger.debug("getHistoryList");

        Map<String, Object> params = new HashMap<String, Object>();

        if(userId != null) {
            params.put("userId", userId);
        }
        if(target != null) {
            params.put("target", target);
        }

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        return Pagination.getPagination(page, historyService.getTotal(params), rows, historyService.getHistorys(params));
    }
}
