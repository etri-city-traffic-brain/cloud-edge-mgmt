package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.client.service.ApiService;
import com.innogrid.uniq.client.service.OpenStackService;
import com.innogrid.uniq.client.service.TokenService;
import com.innogrid.uniq.client.util.CommonUtil;
import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.client.util.Pagination;
import com.innogrid.uniq.coreopenstack.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;

/**
 * Created by wss on 19. 3. 15.
 */
@Controller
@RequestMapping("/private/openstack")
public class OpenstackController {
    private static Logger logger = LoggerFactory.getLogger(OpenstackController.class);

    @Autowired
    private OpenStackService openstackService;

    @Autowired
    private ApiService apiService;

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/compute", "/compute/server"}, method = RequestMethod.GET)
    public String getServer(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/server";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/compute/image"}, method = RequestMethod.GET)
    public String getImage(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/image";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/compute/keypair"}, method = RequestMethod.GET)
    public String getKeyPair(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/keypair";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/compute/flavor"}, method = RequestMethod.GET)
    public String getFlavor(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/flavor";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/volume", "/volume/volume"}, method = RequestMethod.GET)
    public String getVolume(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/volume";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/volume/backup"}, method = RequestMethod.GET)
    public String getBackup(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/backup";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/volume/snapshot"}, method = RequestMethod.GET)
    public String getSnapshot(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/snapshot";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"network", "/network/network"}, method = RequestMethod.GET)
    public String getNetwork(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/network";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/network/router"}, method = RequestMethod.GET)
    public String getRouter(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/router";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/network/securitygroup"}, method = RequestMethod.GET)
    public String getSecurityGroup(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/securityGroup";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/network/floatingip"}, method = RequestMethod.GET)
    public String getFloatingIP(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/floatingIp";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/management", "/management/project"}, method = RequestMethod.GET)
    public String getProject(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/project";
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = {"/meter", "/meter/server"}, method = RequestMethod.GET)
    public String getMeterServerAccumulate(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/openstack/meterServer";
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

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String, Object> (page, total, rows, zone List)
     * @brief zone List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/zones", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getZones(@RequestHeader(value = "referer", required = false) final String referer,
                                          HttpSession session,
                                          @RequestParam(value = "id") String cloudId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer rows,
                                          @RequestParam(defaultValue = "zoneName") String sidx,
                                          @RequestParam(defaultValue = "asc") String sord,
                                          @RequestParam(defaultValue = "compute") String type,
                                          @RequestParam(required = false) String q0,
                                          @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("type", type);

        List<AvailabilityZoneInfo> list = openstackService.getZones(cloudId, params, userInfo, token);
        list.sort(Comparator.comparing(AvailabilityZoneInfo::getZoneName));

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, Server List)
     * @brief Server List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/servers", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getServers(@RequestHeader(value = "referer", required = false) final String referer,
                                          HttpSession session,
                                          @RequestParam(value = "id") String cloudId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer rows,
                                          @RequestParam(defaultValue = "createdAt") String sidx,
                                          @RequestParam(defaultValue = "desc") String sord,
                                          @RequestParam(required = false) String q0,
                                          @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
//        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoiYWRtaW5TeXN0ZW0iLCJ1c2VySWQiOiJjaXR5aHViMDciLCJuaWNrbmFtZSI6ImNpdHlodWIwNyIsImVtYWlsIjoiY2l0eWh1YjA3QHRlc3QuY29tIiwicm9sZSI6IkluZnJhX0FkbWluIiwiaWF0IjoxNTg5MDAzOTY5LCJleHAiOjE1ODkwMDc1NjksImF1ZCI6Im1nMk1pamFIb000ckMxak8wU1Y3IiwiaXNzIjoidXJuOmRhdGFodWI6Y2l0eWh1YjpzZWN1cml0eSJ9.WH9U0L_uBQR-NRo7z9IR_t-8Zi9Rp6znq5jSP7r5IQCcfuWqjItRenB0zsuzRrju3VvunfnHgiY1vqJVu8Ag_O70YDSM2zNXdn0l2erNaKePkM1tryHiy7a5DkQ68gUtrkk0d7tW9E5IQVvkb-OgFAWsCfzX4phsRebUbivbrLomnUupNPurJKWBcRC83URQHU4cJrGUh7Y-J-wNbMa1Y0w4kq4zKO4M12zOO8RetU5lHnKDLMx3o50O1WnBx95MhE2qIxF5bjwzx2RiDqkI98PgzMUcHRmYeUl_lJEHJU8n1nR_GONGRz8cMuNUZnzUB8QTSg2TVujidEvK5HuYGQ";

        logger.info("userInfo = [{}] ", userInfo);
        logger.info("token = [{}] ", token);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        List<ServerInfo> list = openstackService.getServers(cloudId, params, userInfo, token);

        switch(sidx) {
            case "projectName":
                Function<ServerInfo, String> sortProjectName = info -> info.getProjectName();
                Pagination.sort(list, sortProjectName, sord);
                break;
            case "host":
                Function<ServerInfo, String> sortHost = info -> info.getHost();
                Pagination.sort(list, sortHost, sord);
                break;
            case "name":
                Function<ServerInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
            case "state":
                Function<ServerInfo, String> sortState = info -> info.getState();
                Pagination.sort(list, sortState, sord);
                break;
            case "imageName":
                Function<ServerInfo, String> sortImageName = info -> info.getImageName();
                Pagination.sort(list, sortImageName, sord);
                break;
            case "flavorName":
                Function<ServerInfo, String> sortFlavorName = info -> info.getFlavorName();
                Pagination.sort(list, sortFlavorName, sord);
                break;
            case "cpu":
                Function<ServerInfo, Integer> sortCpu = info -> info.getCpu();
                Pagination.sort(list, sortCpu, sord);
                break;
            case "memory":
                Function<ServerInfo, Integer> sortMemory = info -> info.getMemory();
                Pagination.sort(list, sortMemory, sord);
                break;
            case "disk":
                Function<ServerInfo, Integer> sortDisk = info -> info.getDisk();
                Pagination.sort(list, sortDisk, sord);
                break;
            case "addresses":
                Function<ServerInfo, String> sortAddress = info -> info.getAddresses().size() > 0? info.getAddresses().get(0).getAddr():"";
                Pagination.sort(list, sortAddress, sord);
                break;
            case "powerState":
                Function<ServerInfo, String> sortPowerState = info -> info.getPowerState();
                Pagination.sort(list, sortPowerState, sord);
                break;
            default:
                Function<ServerInfo, Timestamp> sortCreatedAt = info -> info.getCreatedAt();
                Pagination.sort(list, sortCreatedAt, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, Flavor List)
     * @brief Flavor List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/flavors", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
    @ResponseBody
    public Map<String, Object> getFlavors(@RequestHeader(value = "referer", required = false) final String referer,
                                          HttpSession session,
                                          @RequestParam(value = "id") String cloudId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer rows,
                                          @RequestParam(defaultValue = "name") String sidx,
                                          @RequestParam(defaultValue = "asc") String sord,
                                          @RequestParam(required = false) String q0,
                                          @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        List<FlavorInfo> list = openstackService.getFlavors(cloudId, params, userInfo, token);

        switch(sidx) {
            case "vcpus":
                Function<FlavorInfo, Integer> sortVcpus = info -> info.getVcpus();
                Pagination.sort(list, sortVcpus, sord);
                break;
            case "ram":
                Function<FlavorInfo, Integer> sortRam = info -> info.getRam();
                Pagination.sort(list, sortRam, sord);
                break;
            case "disk":
                Function<FlavorInfo, Integer> sortDisk = info -> info.getDisk();
                Pagination.sort(list, sortDisk, sord);
                break;
            case "ephemeral":
                Function<FlavorInfo, Integer> sortEphemeral = info -> info.getEphemeral();
                Pagination.sort(list, sortEphemeral, sord);
                break;
            case "swap":
                Function<FlavorInfo, Integer> sortSwap = info -> info.getSwap();
                Pagination.sort(list, sortSwap, sord);
                break;
            case "rxtxFactor":
                Function<FlavorInfo, Float> sortRxtx = info -> info.getRxtxFactor();
                Pagination.sort(list, sortRxtx, sord);
                break;
            case "id":
                Function<FlavorInfo, String> sortId = info -> info.getId();
                Pagination.sort(list, sortId, sord);
                break;
            case "isPublic":
                Function<FlavorInfo, Boolean> sortIsPublic = info -> info.getIsPublic();
                Pagination.sort(list, sortIsPublic, sord);
                break;
            default:
                Function<FlavorInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, Images List)
     * @brief Images List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/images", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
    @ResponseBody
    public Map<String, Object> getImages(@RequestHeader(value = "referer", required = false) final String referer,
                                          HttpSession session,
                                          @RequestParam(value = "id") String cloudId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer rows,
                                          @RequestParam(defaultValue = "name") String sidx,
                                          @RequestParam(defaultValue = "asc") String sord,
                                          @RequestParam(defaultValue = "false") Boolean active,
                                          @RequestParam(required = false) String q0,
                                          @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("active", active);

        List<ImageInfo> list = openstackService.getImages(cloudId, params, userInfo, token);

        switch(sidx) {
            case "type":
                Function<ImageInfo, String> sortType = info -> info.getType();
                Pagination.sort(list, sortType, sord);
                break;
            case "state":
                Function<ImageInfo, String> sortState = info -> info.getState();
                Pagination.sort(list, sortState, sord);
                break;
            case "visibility":
                Function<ImageInfo, String> sortVisibility = info -> info.getVisibility();
                Pagination.sort(list, sortVisibility, sord);
                break;
            case "isProtected":
                Function<ImageInfo, Boolean> sortIsProtected = info -> info.getIsProtected();
                Pagination.sort(list, sortIsProtected, sord);
                break;
            case "diskFormat":
                Function<ImageInfo, String> sortDiskFormat = info -> info.getDiskFormat();
                Pagination.sort(list, sortDiskFormat, sord);
                break;
            case "size":
                Function<ImageInfo, Long> sortSize = info -> info.getSize();
                Pagination.sort(list, sortSize, sord);
                break;
            default:
                Function<ImageInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, keypair List)
     * @brief keypair List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/keypairs", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
    @ResponseBody
    public Map<String, Object> getKeyPairs(@RequestHeader(value = "referer", required = false) final String referer,
                                           HttpSession session,
                                           @RequestParam(value = "id") String cloudId,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer rows,
                                           @RequestParam(defaultValue = "name") String sidx,
                                           @RequestParam(defaultValue = "asc") String sord,
                                           @RequestParam(required = false) String q0,
                                           @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        List<KeyPairInfo> list = openstackService.getKeyPairs(cloudId, params, userInfo, token);

        switch(sidx) {
            case "fingerprint":
                Function<KeyPairInfo, String> sortFingerprint = info -> info.getFingerprint();
                Pagination.sort(list, sortFingerprint, sord);
                break;
            default:
                Function<KeyPairInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, Volume List)
     * @brief Volume List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/volumes", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getVolumes(@RequestHeader(value = "referer", required = false) final String referer,
                                          HttpSession session,
                                          @RequestParam(value = "id") String cloudId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer rows,
                                          @RequestParam(defaultValue = "createdAt") String sidx,
                                          @RequestParam(defaultValue = "desc") String sord,
                                          @RequestParam(defaultValue = "false") Boolean bootable,
                                          @RequestParam(defaultValue = "false") Boolean available,
                                          @RequestParam(required = false) String q0,
                                          @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("bootable", bootable);
        params.put("available", available);

        List<VolumeInfo> list = openstackService.getVolumes(cloudId, params, userInfo, token);

        switch(sidx) {
            case "projectName":
                Function<VolumeInfo, String> sortProjectName = info -> info.getProjectName();
                Pagination.sort(list, sortProjectName, sord);
                break;
            case "name":
                Function<VolumeInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
            case "description":
                Function<VolumeInfo, String> sortDescription = info -> info.getDescription();
                Pagination.sort(list, sortDescription, sord);
                break;
            case "size":
                Function<VolumeInfo, Integer> sortSize = info -> info.getSize();
                Pagination.sort(list, sortSize, sord);
                break;
            case "state":
                Function<VolumeInfo, String> sortState = info -> info.getState();
                Pagination.sort(list, sortState, sord);
                break;
            case "attachmentInfos":
                Function<VolumeInfo, String> sortAttachment = info -> info.getAttachmentInfos().size() > 0? info.getAttachmentInfos().get(0).getServerName() : "";
                Pagination.sort(list, sortAttachment, sord);
                break;
            case "zone":
                Function<VolumeInfo, String> sortZone = info -> info.getZone();
                Pagination.sort(list, sortZone, sord);
                break;
            case "bootable":
                Function<VolumeInfo, Boolean> sortBootable = info -> info.getBootable();
                Pagination.sort(list, sortBootable, sord);
                break;
            default:
                Function<VolumeInfo, Timestamp> sortCreatedAt = info -> info.getCreatedAt();
                Pagination.sort(list, sortCreatedAt, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, Backup List)
     * @brief Backup List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/backups", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getBackups(@RequestHeader(value = "referer", required = false) final String referer,
                                          HttpSession session,
                                          @RequestParam(value = "id") String cloudId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer rows,
                                          @RequestParam(defaultValue = "name") String sidx,
                                          @RequestParam(defaultValue = "asc") String sord,
                                          @RequestParam(required = false) String q0,
                                          @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        List<VolumeBackupInfo> list = openstackService.getBackups(cloudId, params, userInfo, token);

        switch(sidx) {
            case "description":
                Function<VolumeBackupInfo, String> sortDescription = info -> info.getDescription();
                Pagination.sort(list, sortDescription, sord);
                break;
            case "size":
                Function<VolumeBackupInfo, Integer> sortSize = info -> info.getSize();
                Pagination.sort(list, sortSize, sord);
                break;
            case "state":
                Function<VolumeBackupInfo, String> sortState = info -> info.getState();
                Pagination.sort(list, sortState, sord);
                break;
            case "volumeName":
                Function<VolumeBackupInfo, String> sortVolumeName = info -> info.getVolumeName();
                Pagination.sort(list, sortVolumeName, sord);
                break;
            default:
                Function<VolumeBackupInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, Snapshot List)
     * @brief Snapshot List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/snapshots", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSnapshots(@RequestHeader(value = "referer", required = false) final String referer,
                                          HttpSession session,
                                          @RequestParam(value = "id") String cloudId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer rows,
                                          @RequestParam(defaultValue = "name") String sidx,
                                          @RequestParam(defaultValue = "asc") String sord,
                                          @RequestParam(defaultValue = "false") Boolean available,
                                          @RequestParam(required = false) String q0,
                                          @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("available", available);
        List<VolumeSnapshotInfo> list = openstackService.getSnapshots(cloudId, params, userInfo, token);

        switch(sidx) {
            case "description":
                Function<VolumeSnapshotInfo, String> sortDescription = info -> info.getDescription();
                Pagination.sort(list, sortDescription, sord);
                break;
            case "size":
                Function<VolumeSnapshotInfo, Integer> sortSize = info -> info.getSize();
                Pagination.sort(list, sortSize, sord);
                break;
            case "state":
                Function<VolumeSnapshotInfo, String> sortState = info -> info.getState();
                Pagination.sort(list, sortState, sord);
                break;
            case "volumeName":
                Function<VolumeSnapshotInfo, String> sortVolumeName = info -> info.getVolumeName();
                Pagination.sort(list, sortVolumeName, sord);
                break;
            default:
                Function<VolumeSnapshotInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/snapshots/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody
    VolumeSnapshotInfo deleteSnapshot(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String snapshotId,
            HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        return openstackService.deleteSnapshot(cloudId, snapshotId, userInfo, token);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, network List)
     * @brief network List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/networks", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
    @ResponseBody
    public Map<String, Object> getNetworks(@RequestHeader(value = "referer", required = false) final String referer,
                                         HttpSession session,
                                         @RequestParam(value = "id") String cloudId,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer rows,
                                         @RequestParam(defaultValue = "false") Boolean project,
                                         @RequestParam(defaultValue = "name") String sidx,
                                         @RequestParam(defaultValue = "asc") String sord,
                                         @RequestParam(required = false) String q0,
                                         @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("project", project);

        List<NetworkInfo> list = openstackService.getNetworks(cloudId, params, userInfo, token);

        switch(sidx) {
            case "projectName":
                Function<NetworkInfo, String> sortProjectName = info -> info.getProjectName();
                Pagination.sort(list, sortProjectName, sord);
                break;
            case "neutronSubnets":
                Function<NetworkInfo, String> sortNeutronSubnets = info -> info.getNeutronSubnets().size() > 0? info.getNeutronSubnets().get(0).getName() : "";
                Pagination.sort(list, sortNeutronSubnets, sord);
                break;
            case "shared":
                Function<NetworkInfo, Boolean> sortShared = info -> info.getShared();
                Pagination.sort(list, sortShared, sord);
                break;
            case "external":
                Function<NetworkInfo, Boolean> sortExternal = info -> info.getExternal();
                Pagination.sort(list, sortExternal, sord);
                break;
            case "state":
                Function<NetworkInfo, String> sortState = info -> info.getState();
                Pagination.sort(list, sortState, sord);
                break;
            case "adminStateUp":
                Function<NetworkInfo, Boolean> sortAdminStateUp = info -> info.getAdminStateUp();
                Pagination.sort(list, sortAdminStateUp, sord);
                break;
            case "visibilityZones":
                Function<NetworkInfo, String> sortVisibilityZones = info -> info.getVisibilityZones().size() > 0? info.getVisibilityZones().get(0) : "";
                Pagination.sort(list, sortVisibilityZones, sord);
                break;
            default:
                Function<NetworkInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String, Object> (page, total, rows, Volume List)
     * @brief Volume List
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/networks/{id}/subnets", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getNetworkSubnets(@RequestHeader(value = "referer", required = false) final String referer,
                                                 HttpSession session,
                                                 @RequestParam(value = "id") String cloudId,
                                                 @PathVariable(value = "id") String networkId,
                                                 @RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer rows,
                                                 @RequestParam(defaultValue = "name") String sidx,
                                                 @RequestParam(defaultValue = "asc") String sord,
                                                 @RequestParam(required = false) String q0,
                                                 @RequestParam(required = false) String q1) {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("networkId", networkId);

        List<SubnetInfo> list = openstackService.getSubnets(cloudId, params, userInfo, token);

        switch(sidx) {
            case "cidr":
                Function<SubnetInfo, String> sortCidrName = info -> info.getCidr();
                Pagination.sort(list, sortCidrName, sord);
                break;
            case "ipVersion":
                Function<SubnetInfo, Integer> sortIpVersion = info -> info.getIpVersion();
                Pagination.sort(list, sortIpVersion, sord);
                break;
            case "gateway":
                Function<SubnetInfo, String> sortGateway = info -> info.getGateway();
                Pagination.sort(list, sortGateway, sord);
                break;
            default:
                Function<SubnetInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, router List)
     * @brief router List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/routers", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
    @ResponseBody
    public Map<String, Object> getRouters(@RequestHeader(value = "referer", required = false) final String referer,
                                           HttpSession session,
                                           @RequestParam(value = "id") String cloudId,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer rows,
                                           @RequestParam(defaultValue = "name") String sidx,
                                           @RequestParam(defaultValue = "asc") String sord,
                                           @RequestParam(required = false) String q0,
                                           @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        List<RouterInfo> list = openstackService.getRouters(cloudId, params, userInfo, token);

        switch(sidx) {
            case "projectName":
                Function<RouterInfo, String> sortProjectName = info -> info.getProjectName();
                Pagination.sort(list, sortProjectName, sord);
                break;
            case "state":
                Function<RouterInfo, String> sortState = info -> info.getState();
                Pagination.sort(list, sortState, sord);
                break;
            case "networkName":
                Function<RouterInfo, String> sortNetworkName = info -> info.getNetworkName();
                Pagination.sort(list, sortNetworkName, sord);
                break;
            case "adminStateUp":
                Function<RouterInfo, Boolean> sortAdminStateUp = info -> info.getAdminStateUp();
                Pagination.sort(list, sortAdminStateUp, sord);
                break;
            case "visibilityZones":
                Function<RouterInfo, String> sortVisibilityZones = info -> info.getVisibilityZones().size() > 0? info.getVisibilityZones().get(0) : "";
                Pagination.sort(list, sortVisibilityZones, sord);
                break;
            default:
                Function<RouterInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String, Object> (page, total, rows, SecurityGroup List)
     * @brief SecurityGroup List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/securitygroups", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSecurityGroups(@RequestHeader(value = "referer", required = false) final String referer,
                                            HttpSession session,
                                            @RequestParam(value = "id") String cloudId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer rows,
                                            @RequestParam(defaultValue = "false") Boolean project,
                                            @RequestParam(defaultValue = "name") String sidx,
                                            @RequestParam(defaultValue = "asc") String sord,
                                            @RequestParam(required = false) String q0,
                                            @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        List<SecurityGroupInfo> list = openstackService.getSecurityGroups(cloudId, params, project, userInfo, token);

        switch(sidx) {
            case "id":
                Function<SecurityGroupInfo, String> sortId = info -> info.getId();
                Pagination.sort(list, sortId, sord);
                break;
            case "description":
                Function<SecurityGroupInfo, String> sortDescription = info -> info.getDescription();
                Pagination.sort(list, sortDescription, sord);
                break;
            default:
                Function<SecurityGroupInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, FloatingIp List)
     * @brief FloatingIp List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/floatingips", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getFloatingIps(@RequestHeader(value = "referer", required = false) final String referer,
                                            HttpSession session,
                                            @RequestParam(value = "id") String cloudId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer rows,
                                            @RequestParam(defaultValue = "false") Boolean down,
                                            @RequestParam(required = false) String projectId,
                                            @RequestParam(defaultValue = "floatingIpAddress") String sidx,
                                            @RequestParam(defaultValue = "asc") String sord,
                                            @RequestParam(required = false) String q0,
                                            @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("down", down);
        if(down != null && down == true) {
            params.put("projectId", projectId);
        }

        List<FloatingIpInfo> list = openstackService.getFloatingIps(cloudId, params, userInfo, token);

        switch(sidx) {
            case "projectName":
                Function<FloatingIpInfo, String> sortProjectName = info -> info.getProjectName();
                Pagination.sort(list, sortProjectName, sord);
                break;
            case "mapping":
                Function<FloatingIpInfo, String> sortMapping = info -> info.getServerName();
                Pagination.sort(list, sortMapping, sord);
                break;
            case "networkName":
                Function<FloatingIpInfo, String> sortNetworkName = info -> info.getNetworkName();
                Pagination.sort(list, sortNetworkName, sord);
                break;
            case "status":
                Function<FloatingIpInfo, String> sortStatus = info -> info.getStatus();
                Pagination.sort(list, sortStatus, sord);
                break;
            default:
                Function<FloatingIpInfo, String> sortFloatingIpAddress = info -> info.getFloatingIpAddress();
                Pagination.sort(list, sortFloatingIpAddress, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, Project List)
     * @brief Project List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getProjects(@RequestHeader(value = "referer", required = false) final String referer,
                                          HttpSession session,
                                          @RequestParam(value = "id") String cloudId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer rows,
                                          @RequestParam(defaultValue = "createdAt") String sidx,
                                          @RequestParam(defaultValue = "desc") String sord,
                                          @RequestParam(required = false) String q0,
                                          @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);

        List<ProjectInfo> list = openstackService.getProjects(cloudId, params, userInfo, token);

        switch(sidx) {
            case "name":
                Function<ProjectInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
            case "description":
                Function<ProjectInfo, String> sortDescription = info -> info.getDescription();
                Pagination.sort(list, sortDescription, sord);
                break;
            case "parentId":
                Function<ProjectInfo, String> sortParentId = info -> info.getParentId();
                Pagination.sort(list, sortParentId, sord);
                break;
            case "enabled":
                Function<ProjectInfo, Boolean> sortEnabled = info -> info.getEnabled();
                Pagination.sort(list, sortEnabled, sord);
                break;
            default:
                Function<ProjectInfo, String> sortId = info -> info.getId();
                Pagination.sort(list, sortId, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param cloudId cloudId
     * @param createData  data
     * @return ServerInfo
     * @brief Server 생성 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo createServer(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, Object> createData,
            HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        return openstackService.createServer(cloudId, createData, userInfo, token);
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}", method = RequestMethod.GET)
    public @ResponseBody
    List<ServerInfo> getServers_Detail_openstack(
            @RequestParam(value = "id") String cloudId,
            @PathVariable("id") String ServerId,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        System.out.println("####################### = " + ServerId);

        return openstackService.getServers_Detail_openstack(cloudId, ServerId, token);
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/networks/{id}", method = RequestMethod.GET)
    public @ResponseBody
    NetworkInfo getNetworks_Detail_openstack(
            @RequestParam(value = "id") String cloudId,
            @PathVariable("id") String networkId,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        System.out.println("####################### = " + networkId);

        return openstackService.getNetworks_Detail_openstack(cloudId, networkId, token);
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/volumes/{id}", method = RequestMethod.GET)
    public @ResponseBody
    VolumeInfo getVolumes_Detail_openstack(
            @RequestParam(value = "id") String cloudId,
            @PathVariable("id") String volumeId,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        System.out.println("####################### = " + volumeId);

        return openstackService.getVolumes_Detail_openstack(cloudId, volumeId, token);
    }

    /**
     * @param cloudId cloudId
     * @param action  actinoInfo
     * @param serverId 해당 서버 ID
     * @return ServerInfo
     * @brief Server Action 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/action", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo actionServer(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String serverId,
            HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        return openstackService.action(cloudId, serverId, action.get("action"), userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param name  snapshotName
     * @param serverId 해당 서버 ID
     * @return Map<String, String> image ID
     * @brief Server Snapshot 생성 요청
     */
    @Secured({"ROLE_CLOUD_WRITE", "ROLE_USER"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/snapshot", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> createServerSnapshot(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> name,
            @PathVariable(value = "id") String serverId,
            HttpSession session) throws Exception {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        String imageId = openstackService.createServerSnapshot(cloudId, serverId, name.get("name"), userInfo, token);

        return new HashMap<String, String>(){{
            put("imageId", imageId);
        }};
    }

    /**
     * @param cloudId cloudId
     * @param line  line 수
     * @param serverId 해당 서버 ID
     * @return Map<String, String> log
     * @brief Log List
     */
    @Secured({"ROLE_CLOUD_READ", "ROLE_USER"})
    @RequestMapping(value = "/servers/{id}/log", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getServerLog(@RequestHeader(value = "referer", required = false) final String referer,
                                           HttpSession session,
                                           @RequestParam(value = "id") String cloudId,
                                           @RequestParam(value = "line") int line,
                                           @PathVariable(value = "id") String serverId) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, String> result = new HashMap<>();

        String log = openstackService.getServerConsoleOutput(cloudId, serverId, line, userInfo, token);

        result.put("log", log);

        return result;
    }

    /**
     * @param cloudId cloudId
     * @param serverId 해당 서버 ID
     * @return Map<String, String> url
     * @brief VNC url 조회
     */
    @Secured({"ROLE_CLOUD_READ", "ROLE_USER"})
    @RequestMapping(value = "/servers/{id}/console", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getServerConsole(@RequestHeader(value = "referer", required = false) final String referer,
                                            HttpSession session,
                                            @RequestParam(value = "id") String cloudId,
                                            @PathVariable(value = "id") String serverId) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, String> result = new HashMap<>();

        String url = openstackService.getServerVNCConsoleURL(cloudId, serverId, userInfo, token);

        result.put("url", url);

        return result;
    }

    /**
     * @return Map<String,Object> (page, total, rows, metric)
     * @brief Server Metric View
     */
    @Secured({"ROLE_CLOUD_READ", "ROLE_USER"})
    @RequestMapping(value = "/servers/{id}/metric", method = RequestMethod.GET/*, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
    @ResponseBody
    public Object getServerMetric(@RequestHeader(value = "referer", required = false) final String referer,
                                  HttpSession session,
                                  @RequestParam(value = "id") String cloudId,
                                  @PathVariable(value = "id") String serverId,
                                  @RequestParam Map<String, Object> params) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Object datas = null;
        try {
            datas = openstackService.getServerMetric(cloudId, serverId, userInfo, params, token);
            /*logger.debug("monitoring : {} ", datas);*/
        }catch (Exception e){
            logger.error("Failed to get ServerMetric : '{}'", e.getMessage());
        }

        return datas;
    }

    @RequestMapping(value = "/servers/{id}/influxmetric", method = RequestMethod.GET)
    @ResponseBody
    public Object getVmInfluxMetric(HttpSession session,
                                    @RequestParam(value = "id") String cloudId,
                                    @PathVariable(value = "id") String serverId,
                                    @RequestParam Map<String, Object> params,
                                    @RequestParam String[] metricNames) {
        Object datas;

        Map<String, Object> charts = Collections.synchronizedMap(new HashMap<>());
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        params.put("vmId", serverId);
        for(int i=0; i<metricNames.length; i++) {
            try {
                params.put("metricName", metricNames[i]);
                charts.put(metricNames[i], openstackService.getInfluxMetric(params, token));
            } catch (NullPointerException e){
                logger.error("Failed to getVmMetric : '{}'", e.getMessage());
            } catch (Exception e){
                logger.error("Failed to getVmMetric : '{}'", e.getMessage());
            }
        }

        datas = charts;

        return datas;
    }


    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, Project List)
     * @brief Project List View
     */
    @Secured({"ROLE_CLOUD_READ", "ROLE_USER"})
    @RequestMapping(value = "/servers/{id}/action", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getActions(@RequestHeader(value = "referer", required = false) final String referer,
                                           HttpSession session,
                                           @RequestParam(value = "id") String cloudId,
                                           @PathVariable(value = "id") String serverId,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer rows,
                                           @RequestParam(defaultValue = "startDate") String sidx,
                                           @RequestParam(defaultValue = "desc") String sord,
                                           @RequestParam(required = false) String q0,
                                           @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        List<ActionLogInfo> list = openstackService.getServerActionLog(cloudId, serverId, userInfo, token);

        switch(sidx) {
            case "requestId":
                Function<ActionLogInfo, String> sortRequestId = info -> info.getRequestId();
                Pagination.sort(list, sortRequestId, sord);
                break;
            case "action":
                Function<ActionLogInfo, String> sortAction = info -> info.getAction();
                Pagination.sort(list, sortAction, sord);
                break;
            default:
                Function<ActionLogInfo, Timestamp> sortStartDate = info -> info.getStartDate();
                Pagination.sort(list, sortStartDate, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String, Object> (page, total, rows, Volume List)
     * @brief Volume List
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/servers/{id}/volumes", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getServerVolumes(@RequestHeader(value = "referer", required = false) final String referer,
                                           HttpSession session,
                                           @RequestParam(value = "id") String cloudId,
                                           @PathVariable(value = "id") String serverId,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer rows,
                                           @RequestParam(defaultValue = "name") String sidx,
                                           @RequestParam(defaultValue = "asc") String sord,
                                           @RequestParam(required = false) String q0,
                                           @RequestParam(required = false) String q1) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        List<VolumeInfo> list = openstackService.getServerVolumes(cloudId, serverId, userInfo, token);

        switch(sidx) {
            case "description":
                Function<VolumeInfo, String> sortDescription = info -> info.getDescription();
                Pagination.sort(list, sortDescription, sord);
                break;
            case "size":
                Function<VolumeInfo, Integer> sortSize = info -> info.getSize();
                Pagination.sort(list, sortSize, sord);
                break;
            case "state":
                Function<VolumeInfo, String> sortState = info -> info.getState();
                Pagination.sort(list, sortState, sord);
                break;
            case "attachmentInfos":
                Function<VolumeInfo, String> sortAttachment = info -> info.getAttachmentInfos().size() > 0? info.getAttachmentInfos().get(0).getServerName() : "";
                Pagination.sort(list, sortAttachment, sord);
                break;
            case "zone":
                Function<VolumeInfo, String> sortZone = info -> info.getZone();
                Pagination.sort(list, sortZone, sord);
                break;
            case "bootable":
                Function<VolumeInfo, Boolean> sortBootable = info -> info.getBootable();
                Pagination.sort(list, sortBootable, sord);
                break;
            default:
                Function<VolumeInfo, String> sortName = info -> info.getName();
                Pagination.sort(list, sortName, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param cloudId cloudId
     * @param action  actinoInfo
     * @param serverId 해당 서버 ID
     * @return ServerInfo
     * @brief Server Action 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/volumes/{volumeId}", method = RequestMethod.POST)
    public @ResponseBody
    VolumeInfo actionServerVolume(
            @RequestParam(value = "id") String cloudId,
            @PathVariable(value = "volumeId") String volumeId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String serverId,
            HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        if(action.get("action").equals("ATTACH_VOLUME")) {
            return openstackService.attachVolume(cloudId, serverId, volumeId, userInfo, token);
        } else if(action.get("action").equals("DETACH_VOLUME")) {
            return openstackService.detachVolume(cloudId, serverId, volumeId, userInfo, token);
        } else {
            return new VolumeInfo();
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/servers/{id}/interface", method = RequestMethod.GET)
    @ResponseBody
    public Object getServerInterfaces(@RequestHeader(value = "referer", required = false) final String referer,
                                                HttpSession session,
                                                @RequestParam(value = "id") String cloudId,
                                                @PathVariable(value = "id") String serverId) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Object list = openstackService.getServerInterface(cloudId, serverId, userInfo, token);

        return list;
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/interface", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo actionServerInterface(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String serverId,
            HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Boolean result = false;

        if(action.get("action").equals("ATTACH_INTERFACE")) {
            result = openstackService.attachInterface(cloudId, serverId, action.get("networkId"), action.get("projectId"), userInfo, token);
        } else if(action.get("action").equals("DETACH_INTERFACE")) {
            result =  openstackService.detachInterface(cloudId, serverId, action.get("portId"), action.get("projectId"), userInfo, token);
        }

        if(result) {
            return openstackService.getServer(cloudId, serverId, userInfo, token);
        }

        return new ServerInfo();
    }

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/floatingip", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo actionServerFloatingIp(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String serverId,
            HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Boolean result = false;

        if(action.get("action").equals("CONNECT_FLOATING_IP")) {
            result = openstackService.addFloatingIpToServer(cloudId, serverId, action.get("interfaceIp"), action.get("floatingIp") , action.get("projectId"), userInfo, token);
        } else if(action.get("action").equals("DISCONNECT_FLOATING_IP")) {
            result = openstackService.removeFloatingIpToServer(cloudId, serverId, action.get("floatingIp"), action.get("projectId"), userInfo, token);
        }

        if(result) {
            return openstackService.getServer(cloudId, serverId, userInfo, token);
        }

        return new ServerInfo();
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, network List)
     * @brief Meter Server List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ", "ROLE_CLOUD_WRITE"})
    @RequestMapping(value = "/meter/servers", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getMeterServerAccumulates(@RequestHeader(value = "referer", required = false) final String referer,
                                                         HttpSession session,
                                                         @RequestParam(value = "id") String cloudId,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer rows,
                                                         @RequestParam(defaultValue = "id") String sidx,
                                                         @RequestParam(defaultValue = "asc") String sord,
                                                         @RequestParam(required = false) String q0,
                                                         @RequestParam(required = false) String q1) {
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("credentialId", cloudId);

        List<MeterServerAccumulateInfo> list = apiService.getMeterServerAccumulates(cloudId, token);
        Function<MeterServerAccumulateInfo, String> sort;

        switch(sidx) {
            case "instanceId":
                sort = info -> info.getInstanceId();
                Pagination.sort(list, sort, sord);
                break;
            case "instanceName":
                sort = info -> info.getInstanceName();
                Pagination.sort(list, sort, sord);
                break;
            case "flavorName":
                sort = info -> info.getFlavorName();
                Pagination.sort(list, sort, sord);
                break;
            case "meterStartTime":
                Function<MeterServerAccumulateInfo, Timestamp> sortStartTime = info -> info.getMeterStartTime();
                Pagination.sort(list, sortStartTime, sord);
                break;
            case "meterEndTime":
                Function<MeterServerAccumulateInfo, Timestamp> sortEndTime = info -> info.getMeterEndTime();
                Pagination.sort(list, sortEndTime, sord);
                break;
            case "meterDuration":
                Function<MeterServerAccumulateInfo, Integer> sortDuration = info -> info.getMeterDuration();
                Pagination.sort(list, sortDuration, sord);
                break;
            default:
                sort = info -> info.getId();
                Pagination.sort(list, sort, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param page    current page value
     * @param rows    column list's count
     * @param sidx    sort standard
     * @param sord    asc or desc
     * @param q0      search condition
     * @param q1      search value
     * @return Map<String   ,       Object> (page, total, rows, network List)
     * @brief Meter Server List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ", "ROLE_CLOUD_WRITE"})
    @RequestMapping(value = "/meter/servers/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getMeterServer(@RequestHeader(value = "referer", required = false) final String referer,
                                              HttpSession session,
                                              @RequestParam(value = "id") String cloudId,
                                              @RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer rows,
                                              @RequestParam(defaultValue = "id") String sidx,
                                              @RequestParam(defaultValue = "asc") String sord,
                                              @RequestParam(required = false) String q0,
                                              @RequestParam(required = false) String q1,
                                              @PathVariable(value = "id") String serverId) {
        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        Map<String, Object> params = new HashMap<>();

        params.put("sidx", sidx);
        params.put("sord", sord);
        params.put("q0", StringUtils.trimWhitespace(q0));
        params.put("q1", StringUtils.trimWhitespace(q1));
        params.put("page", page);
        params.put("rows", rows);
        params.put("instanceId", serverId);

        List<MeterServerInfo> list = apiService.getMeterServers(cloudId, serverId, token);
        Function<MeterServerInfo, String> sort;

        switch(sidx) {
            case "instanceId":
                sort = info -> info.getInstanceId();
                Pagination.sort(list, sort, sord);
                break;
            case "flavorId":
                sort = info -> info.getFlavorId();
                Pagination.sort(list, sort, sord);
                break;
            case "status":
                sort = info -> info.getStatus();
                Pagination.sort(list, sort, sord);
                break;
            case "createdAt":
                Function<MeterServerInfo, Timestamp> sortCreatedAt = info -> info.getCreatedAt();
                Pagination.sort(list, sortCreatedAt, sord);
                break;
            default:
                sort = info -> info.getId();
                Pagination.sort(list, sort, sord);
                break;
        }

        return Pagination.getPagination(page, list.size(), rows, list);
    }

    /**
     * @param cloudId cloudId
     * @param createData  data
     * @return VolumeInfo
     * @brief Volume 생성 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/volumes", method = RequestMethod.POST)
    public @ResponseBody
    VolumeInfo createVolume(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, Object> createData,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.createVolume(cloudId, createData, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param action  actionInfo
     * @param volumeId 해당 서버 ID
     * @return VolumeInfo
     * @brief Volume Delete 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/volumes/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody
    VolumeInfo deleteVolume(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String volumeId,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.deleteVolume(cloudId, volumeId, userInfo, token);
    }

    /**
     * @return Map<String   ,       Object> (page, total, rows, VolumeType List)
     * @brief VolumeType List View
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ"})
    @RequestMapping(value = "/volumeTypes", method = RequestMethod.GET)
    @ResponseBody
    public Object getVolumeTypes(@RequestHeader(value = "referer", required = false) final String referer,
                                 HttpSession session,
                                 @RequestParam(value = "id") String cloudId) {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        Map<String, Object> params = new HashMap<>();

        return openstackService.getVolumeTypes(cloudId, params, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param createData  data
     * @return NetworkInfo
     * @brief Network 생성 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/networks", method = RequestMethod.POST)
    public @ResponseBody
    NetworkInfo createNetwork(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, Object> createData,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.createNetwork(cloudId, createData, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param action  actionInfo
     * @param networkId 해당 네트워크 ID
     * @return NetworkInfo
     * @brief Network Delete 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/networks/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody
    NetworkInfo deleteNetwork(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String networkId,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.deleteNetwork(cloudId, networkId, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param action  actionInfo
     * @param networkId 해당 네트워크 ID
     * @return SubnetInfo
     * @brief Subnet Delete 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/networks/{id}/subnets/{subnetId}/delete", method = RequestMethod.POST)
    public @ResponseBody
    SubnetInfo deleteSubnet(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String networkId,
            @PathVariable(value = "subnetId") String subnetId,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.deleteSubnet(cloudId, networkId, subnetId, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param createData  data
     * @return ImageInfo
     * @brief Image 생성 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public @ResponseBody
    ImageInfo createImage(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, Object> createData,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.createImage(cloudId, createData, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param action  actionInfo
     * @param imageId 해당 네트워크 ID
     * @return ImageInfo
     * @brief Image Delete 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/images/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody
    ImageInfo deleteImage(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "id") String imageId,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.deleteImage(cloudId, imageId, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param createData  data
     * @return KeyPairInfo
     * @brief keypair 생성 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/keypairs", method = RequestMethod.POST)
    public @ResponseBody
    KeyPairInfo createKeypair(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, Object> createData,
            HttpSession session) {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.createKeypair(cloudId, createData, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param action  actionInfo
     * @param name keypair name
     * @return KeyPairInfo
     * @brief keypair Delete 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/keypairs/{name}/delete", method = RequestMethod.POST)
    public @ResponseBody
    KeyPairInfo deleteKeypair(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> action,
            @PathVariable(value = "name") String name,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.deleteKeypair(cloudId, name, userInfo, token);
    }

    /**
     * @param cloudId cloudId
     * @param data  flavor ID
     * @param serverId 해당 서버 ID
     * @return ServerInfo
     * @brief Server resize 요청
     */
    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_WRITE"})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/servers/{id}/resize", method = RequestMethod.POST)
    public @ResponseBody
    ServerInfo resizeServer(
            @RequestParam(value = "id") String cloudId,
            @RequestBody Map<String, String> data,
            @PathVariable(value = "id") String serverId,
            HttpSession session) throws Exception {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);
        UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");

        return openstackService.changeFlavor(cloudId, serverId, data.get("flavorId"), userInfo, token);
    }

}
