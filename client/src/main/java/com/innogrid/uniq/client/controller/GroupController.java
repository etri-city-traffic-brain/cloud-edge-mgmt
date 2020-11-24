package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.client.service.ApiService;
import com.innogrid.uniq.client.service.TokenService;
import com.innogrid.uniq.client.util.Pagination;
import com.innogrid.uniq.core.model.GroupInfo;
import com.innogrid.uniq.core.model.ProjectInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.service.GroupService;
import com.innogrid.uniq.coredb.service.ProjectService;
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
 * Created by wss on 19. 4. 01.
 */
@Controller
@RequestMapping("/auth")
public class GroupController {
    private static Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    GroupService groupService;

    @Autowired
    UserService userService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ApiService apiService;

    @Autowired
    private MessageSource messageSource;

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_READ", "ROLE_GROUP_WRITE"})
    @RequestMapping(value = {"/group"}, method = RequestMethod.GET)
    public String getGroup(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        Locale locale = LocaleContextHolder.getLocale();

        model.addAttribute("name", messageSource.getMessage("w.group", null, locale));

        return "view/group";
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_READ", "ROLE_GROUP_WRITE"})
    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> getGroups(HttpSession session,
                                 @RequestParam(required=false) Integer page,
                                 @RequestParam(required=false) Integer rows,
                                 @RequestParam(defaultValue="name") String sidx,
                                 @RequestParam(defaultValue="asc") String sord,
                                 @RequestParam(required = false) String q0,
                                 @RequestParam(required = false) String q1) {

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        return Pagination.getPagination(page, groupService.getTotal(params), rows, groupService.getGroups(params));
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_WRITE"})
    @RequestMapping(value = "/groups/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteGroup(@PathVariable String id, HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        GroupInfo info = groupService.getGroupInfo(params);

        groupService.deleteGroup(info, userInfo);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/groups", method = RequestMethod.POST)
    public @ResponseBody
    GroupInfo createGroup(@RequestBody GroupInfo info, HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        GroupInfo result = groupService.createGroup(info, userInfo);

        return result;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_WRITE"})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/groups/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    GroupInfo updateGroup(HttpSession session, @RequestBody GroupInfo info, @PathVariable("id") String id) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        GroupInfo result = groupService.updateGroup(info, userInfo);

        return result;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_READ", "ROLE_GROUP_WRITE"})
    @RequestMapping(value = "/groups/{id}/users", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> getUserList(HttpSession session,
                                   @RequestParam(required=false) Integer page,
                                   @RequestParam(required=false) Integer rows,
                                   @RequestParam(required=false) Boolean not,
                                   @RequestParam(defaultValue="name") String sidx,
                                   @RequestParam(defaultValue="asc") String sord,
                                   @RequestParam(required = false) String q0,
                                   @RequestParam(required = false) String q1,
                                   @PathVariable("id") String id) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        if(not != null && not == true) {
            params.put("notGroup", not);
        } else {
            params.put("groupId", id);
        }

        return Pagination.getPagination(page, userService.getTotal(params), rows, userService.getUserInfos(params));
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/groups/{id}/users", method = RequestMethod.POST)
    public @ResponseBody
    List<UserInfo> createGroupUser(
            @RequestBody List<UserInfo> userInfos,
            @PathVariable(value = "id") String groupId,
            HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        List<UserInfo> result = userService.createGroupUsers(userInfos, groupId, userInfo);

        return result;
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_READ", "ROLE_GROUP_WRITE"})
    @RequestMapping(value = "/groups/{id}/projects", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> getProjectList(HttpSession session,
                                      @RequestParam(required=false) Integer page,
                                      @RequestParam(required=false) Integer rows,
                                      @RequestParam(required=false) Boolean not,
                                      @RequestParam(defaultValue="name") String sidx,
                                      @RequestParam(defaultValue="asc") String sord,
                                      @RequestParam(required = false) String q0,
                                      @RequestParam(required = false) String q1,
                                      @PathVariable("id") String id) {
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("groupId", id);

        return Pagination.getPagination(page, projectService.getTotal(params), rows, apiService.getGroupProject(projectService.getProjects(params), token));
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_WRITE"})
    @RequestMapping(value = "/groups/{id}/projects/{projectId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteGroupProject(@PathVariable String id,
                                   @PathVariable String projectId,
                                   HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        Map<String, Object> params = new HashMap<>();
        params.put("id", projectId);
        params.put("groupId", id);

        ProjectInfo info = projectService.getProjectInfo(params);

        projectService.deleteProject(info, userInfo);
    }

    @Secured({"ROLE_ADMIN", "ROLE_GROUP_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/groups/{id}/projects", method = RequestMethod.POST)
    public @ResponseBody
    List<ProjectInfo> createGroupProject(
            @RequestBody List<ProjectInfo> projectInfos,
            @PathVariable(value = "id") String groupId,
            HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        List<ProjectInfo> result = projectService.syncProject(projectInfos, groupId, userInfo);

        return result;
    }
}
