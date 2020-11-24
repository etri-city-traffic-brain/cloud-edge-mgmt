package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.core.model.ServiceDashboardInfo;
import com.innogrid.uniq.coredb.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by kkm on 19. 4. 24.
 */
@Controller
public class DashboardController {
    private static Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = {"/dashboard", "/dashboard/service/total"}, method = RequestMethod.GET)
    public String getCredential(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        Locale locale = LocaleContextHolder.getLocale();

        model.addAttribute("name",  messageSource.getMessage("w.t.multi", new Object[] {
                messageSource.getMessage("w.total", null, locale),
                messageSource.getMessage("w.dashboard", null, locale)
        }, locale));
        return "view/dashboard";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ", "ROLE_CLOUD_WRITE"})
    @RequestMapping(value = {"/dashboard/service/public"}, method = RequestMethod.GET)
    public String getPublicDashboard(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        Locale locale = LocaleContextHolder.getLocale();

        model.addAttribute("name", messageSource.getMessage("w.dashboard", null, locale));

        return "view/dashboard/public";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ", "ROLE_CLOUD_WRITE"})
    @RequestMapping(value = {"/dashboard/service/private"}, method = RequestMethod.GET)
    public String getPrivateDashboard(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        Locale locale = LocaleContextHolder.getLocale();

        model.addAttribute("name", messageSource.getMessage("w.dashboard", null, locale));

        return "view/dashboard/private";
    }

    /**
     * @return Map<String, Object> (page, total, rows, Server List)
     * @brief Server List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ", "ROLE_CLOUD_WRITE"})
    @RequestMapping(value = "/dashboard/resources", method = RequestMethod.GET)
    @ResponseBody
    public List<ServiceDashboardInfo> getServiceDashboard(@RequestHeader(value = "referer", required = false) final String referer,
                                                          HttpSession session
    ) {

        Map<String, Object> params = new HashMap<>();

        return dashboardService.serviceDashboards(params);
    }
}