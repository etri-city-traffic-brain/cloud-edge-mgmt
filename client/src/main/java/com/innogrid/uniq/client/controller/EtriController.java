package com.innogrid.uniq.client.controller;

import com.innogrid.uniq.client.service.ApiService;
import com.innogrid.uniq.core.model.CredentialInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/edge/etri")
public class EtriController {
    private static Logger logger = LoggerFactory.getLogger(EtriController.class);


    @Autowired
    private ApiService apiService;

    @Secured({"ROLE_ADMIN", "ROLE_CLOUD_READ", "ROLE_CLOUD_WRITE"})
    @RequestMapping(value = {"/compute", "/compute/server"}, method = RequestMethod.GET)
    public String getServer(HttpServletRequest request, Principal principal, HttpSession session, Model model) {
        setPageInformation(request, session, model);

        return "view/etri/server";
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
