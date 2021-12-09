package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.client.util.Pagination;
import com.innogrid.uniq.core.model.PermissionInfo;
import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.service.CctvService;
import com.innogrid.uniq.coredb.service.PermissionService;
import com.innogrid.uniq.coredb.service.RoleService;
import com.innogrid.uniq.coredb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wss on 19. 4. 03.
 */
@Controller
@RequestMapping("/cctv")
public class CctvController {
    private static Logger logger = LoggerFactory.getLogger(CctvController.class);

    @Autowired
    CctvService cctvService;

    @Autowired
    private MessageSource messageSource;

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_READ", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = {"/cctv"}, method = RequestMethod.GET)
    public String getCctv(HttpServletRequest request, Principal principal, HttpSession session, Model model) {

        Locale locale = LocaleContextHolder.getLocale();

        model.addAttribute("name", messageSource.getMessage("title.tab.role", null, locale));

        return "view/cctv";
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_READ", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = "/cctvs", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getCctvs(HttpSession session,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer rows,
                                 @RequestParam(defaultValue = "name") String sidx,
                                 @RequestParam(defaultValue = "asc") String sord,
                                 @RequestParam(required = false) String q0,
                                 @RequestParam(required = false) String q1) {

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        return Pagination.getPagination(page, cctvService.getTotal(params), rows, cctvService.getCctvs(params));
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_READ", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = "/cctvs2", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getCctvs2(HttpSession session,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer rows,
                                 @RequestParam(defaultValue = "name") String sidx,
                                 @RequestParam(defaultValue = "asc") String sord,
                                 @RequestParam(required = false) String q0,
                                 @RequestParam(required = false) String q1) {

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        return Pagination.getPagination(page, cctvService.getTotal2(params), rows, cctvService.getCctvs2(params));
    }

}
