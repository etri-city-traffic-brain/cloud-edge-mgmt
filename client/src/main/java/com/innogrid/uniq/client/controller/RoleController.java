package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.client.util.Pagination;
import com.innogrid.uniq.core.model.PermissionInfo;
import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.core.model.UserInfo;
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
@RequestMapping("/auth")
public class RoleController {
    private static Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    private MessageSource messageSource;

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_READ", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = {"/role"}, method = RequestMethod.GET)
    public String getRole(HttpServletRequest request, Principal principal, HttpSession session, Model model) {

        Locale locale = LocaleContextHolder.getLocale();

        model.addAttribute("name", messageSource.getMessage("title.tab.role", null, locale));

        return "view/role";
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_READ", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getRoles(HttpSession session,
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

        return Pagination.getPagination(page, roleService.getTotal(params), rows, roleService.getRoles(params));
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = "/roles/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteRole(@PathVariable String id, HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        RoleInfo info = roleService.getRoleInfo(params);

        roleService.deleteRole(info, userInfo);
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/roles", method = RequestMethod.POST)
    public @ResponseBody
    RoleInfo createRole(@RequestBody RoleInfo info, HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        RoleInfo result = roleService.createRole(info, userInfo);

        return result;
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_WRITE"})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    RoleInfo updateRole(HttpSession session, @RequestBody RoleInfo info, @PathVariable("id") String id) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        RoleInfo result = roleService.updateRole(info, userInfo);

        return result;
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_READ", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = "/roles/{roleId}/users", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getRoleUsers(HttpSession session,
                                     @RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer rows,
                                     @RequestParam(required = false) Boolean not,
                                     @RequestParam(defaultValue = "name") String sidx,
                                     @RequestParam(defaultValue = "asc") String sord,
                                     @RequestParam(required = false) String q0,
                                     @RequestParam(required = false) String q1,
                                     @PathVariable(value = "roleId") String roleId) {

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        if (not != null && not == true) {
            params.put("notRole", roleId);
        } else {
            params.put("roleId", roleId);
        }

        return Pagination.getPagination(page, userService.getTotal(params), rows, userService.getUserInfos(params));
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = "/roles/{id}/users/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteRoleUser(
            @PathVariable(value = "id") String id,
            @PathVariable(value = "userId") String userId,
            HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        RoleInfo info = new RoleInfo();
        info.setUserId(userId);
        info.setId(id);

        roleService.deleteRoleUser(info, userInfo);
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_READ", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = "/roles/{roleId}/permissions", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getRolePermissions(HttpSession session,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer rows,
                                           @RequestParam(defaultValue = "name") String sidx,
                                           @RequestParam(defaultValue = "asc") String sord,
                                           @RequestParam(required = false) String q0,
                                           @RequestParam(required = false) String q1,
                                           @PathVariable(value = "roleId") String roleId) {

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("roleId", roleId);

        return Pagination.getPagination(page, permissionService.getTotal(params), rows, permissionService.getPermissions(params));
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_WRITE"})
    @RequestMapping(value = "/roles/{id}/permissions/{permissionId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteRolePermission(
            @PathVariable(value = "id") String id,
            @PathVariable(value = "permissionId") String permissionId,
            HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        PermissionInfo info = new PermissionInfo();
        info.setRoleId(id);
        info.setId(permissionId);

        permissionService.deletePermission(info, userInfo);
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/roles/{roleId}/users", method = RequestMethod.POST)
    public @ResponseBody
    List<UserInfo> createRoleUsers(
            @RequestBody List<UserInfo> userInfos,
            HttpSession session,
            @PathVariable(value = "roleId") String roleId
    ) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        List<UserInfo> result = roleService.createRoleToUsers(roleId, userInfos, userInfo);

        return result;
    }

    @Secured({"ROLE_ADMIN", "ROLE_ROLE_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/roles/{roleId}/permissions", method = RequestMethod.POST)
    public @ResponseBody
    List<PermissionInfo> createRolePermissions(
            @RequestBody List<PermissionInfo> permissionInfos,
            HttpSession session,
            @PathVariable(value = "roleId") String roleId
    ) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        List<PermissionInfo> result = permissionService.createPermissionsToRole(permissionInfos, roleId, userInfo);

        return result;
    }
}
