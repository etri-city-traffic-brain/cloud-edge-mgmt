package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.client.util.Pagination;
import com.innogrid.uniq.core.model.ActionInfo;
import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.coredb.service.ActionService;
import com.innogrid.uniq.coredb.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kkm on 19. 6. 23.
 */
@Controller
@RequestMapping("/usage")
public class ActionController {
    private static Logger logger = LoggerFactory.getLogger(ActionController.class);

    @Autowired
    ActionService actionService;

    @Autowired
    RoleService roleService;

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ", "ROLE_CLOUD_WRITE"})
    @RequestMapping(value = {"/history", "/history/action"}, method = RequestMethod.GET)
    public String getAction(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/action";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ", "ROLE_CLOUD_WRITE"})
    @RequestMapping(value = "/actions/{targetId}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> getActions(HttpSession session,
                                  @PathVariable(value = "targetId") String targetId,
                                  @RequestParam(required=false) Integer page,
                                  @RequestParam(required=false) Integer rows,
                                  @RequestParam(defaultValue="createdAt") String sidx,
                                  @RequestParam(defaultValue="desc") String sord,
                                  @RequestParam(required = false) String q0,
                                  @RequestParam(required = false) String q1,
                                  @RequestParam(required = false, defaultValue = "false") Boolean encode) {

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        if(encode) {
            params.put("targetId", new String(Base64.getDecoder().decode(targetId)));
        } else {
            params.put("targetId", targetId);
        }

        List<ActionInfo> list = actionService.actions(params);

        logger.error("list = " + list);

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    private void setPageInformation(HttpServletRequest request, HttpSession session, Model model) {
        String id = request.getParameter("id");
        String name = "";
        String type = "";

        List<CredentialInfo> menus = (List<CredentialInfo>) session.getAttribute("clouds");

        for(int i=0; i< menus.size(); i++) {
            if(menus.get(i).getId().equals(id)) {
                name = menus.get(i).getName();
                type = menus.get(i).getType();
                break;
            }
        }

        model.addAttribute("id", id);
        model.addAttribute("name", name);
        model.addAttribute("type", type);
    }
}
