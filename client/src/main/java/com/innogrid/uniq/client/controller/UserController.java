package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.client.util.Pagination;
import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.core.model.UserInfo;
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
 * Created by wss on 19. 4. 02.
 */
@Controller
@RequestMapping("/auth")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    private MessageSource messageSource;

    @Secured({"ROLE_ADMIN", "ROLE_USER_READ", "ROLE_USER_WRITE"})
    @RequestMapping(value = {"/user"}, method = RequestMethod.GET)
    public String getUser(HttpServletRequest request, Principal principal, HttpSession session, Model model) {

        Locale locale = LocaleContextHolder.getLocale();

        model.addAttribute("name", messageSource.getMessage("title.tab.user", null, locale));

        return "view/user";
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER_READ", "ROLE_USER_WRITE"})
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> getUserList(HttpSession session,
                                   @RequestParam(required=false) Integer page,
                                   @RequestParam(required=false) Integer rows,
                                   @RequestParam(defaultValue="createdAt") String sidx,
                                   @RequestParam(defaultValue="desc") String sord,
                                   @RequestParam(required = false) String q0,
                                   @RequestParam(required = false) String q1) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        return Pagination.getPagination(page, userService.getTotal(params), rows, userService.getUserInfos(params));
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> checkUserId(HttpSession session,
                                   @PathVariable String id) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        int total = userService.getTotal(params);
        params.put("total", total);
        return params;
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER_WRITE"})
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteUser(@PathVariable String id, HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        UserInfo info = userService.getUserInfo(params);

        userService.deleteUser(info, userInfo);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public @ResponseBody
    UserInfo createUser(@RequestBody UserInfo info, HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        UserInfo result = userService.createUser(info, userInfo);

        return result;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    UserInfo updateUser(HttpSession session, @RequestBody UserInfo info, @PathVariable("id") String id) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        UserInfo result = userService.updateUser(info, userInfo);

        return result;
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER_READ", "ROLE_USER_WRITE"})
    @RequestMapping(value = "/users/{userId}/roles", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> getUserRoles(HttpSession session,
                                    @RequestParam(required=false) Integer page,
                                    @RequestParam(required=false) Integer rows,
                                    @RequestParam(required=false) Boolean not,
                                    @RequestParam(defaultValue="createdAt") String sidx,
                                    @RequestParam(defaultValue="desc") String sord,
                                    @RequestParam(required = false) String q0,
                                    @RequestParam(required = false) String q1,
                                    @PathVariable(value = "userId") String userId) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        if(not != null && not == true) {
            params.put("notUser", userId);
        } else {
            params.put("userId", userId);
        }

        return Pagination.getPagination(page, roleService.getTotal(params), rows, roleService.getRoles(params));
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER_WRITE"})
    @RequestMapping(value = "/users/{id}/roles/{roleId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteUser(
            @PathVariable(value = "id") String id,
            @PathVariable(value = "roleId") String roleId,
            HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        Map<String, Object> params = new HashMap<>();
        params.put("id", roleId);
        params.put("userId", id);

        RoleInfo info = roleService.getRoleInfo(params);

        roleService.deleteRoleUser(info, userInfo);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/users/{id}/roles", method = RequestMethod.POST)
    public @ResponseBody
    List<RoleInfo> createUserRoles(
            @RequestBody List<RoleInfo> roleInfos,
            @PathVariable(value = "id") String userId,
            HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        List<RoleInfo> result = roleService.createRolesToUser(roleInfos, userId, userInfo);

        return result;
    }
}
