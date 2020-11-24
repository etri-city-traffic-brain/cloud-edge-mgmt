package com.innogrid.uniq.client.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innogrid.uniq.client.exception.ValidationFailException;
import com.innogrid.uniq.client.service.ApiService;
import com.innogrid.uniq.client.service.TokenService;
import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.CredentialInfo2;
import com.innogrid.uniq.coredb.service.CredentialService;
import com.innogrid.uniq.coredb.service.ProjectService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/api")
public class CloudServiceController {
    private static Logger logger = LoggerFactory.getLogger(CloudServiceController.class);

    @Autowired
    private ApiService apiService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private ProjectService projectService;

//    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/cloudServices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<CredentialInfo> getCredentialsInfo(
//            @RequestHeader(value = "credential") String credential,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cspType,
            @RequestParam(required = false) String cloudType,
            HttpSession session
    ) {

        if(session != null) {
            if(name != null){
            }else if(cspType != null){
            }else if(cloudType != null){
            }
            return apiService.getCredentialsInfo(credentialService.getCredentials(new HashMap<>()));
        }
        return null;
    }

    //    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/cloudServices", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object createCredential(
            @RequestBody CredentialInfo2 createData2,
            HttpSession session
    ) {

        String token = (String) session.getAttribute(TokenService.COOKIE_IN_TOKEN_NAME);

        logger.error("createData : {}", createData2);
        logger.error("token : {}", token);

        CredentialInfo createData = new CredentialInfo(createData2);

        logger.error("createData2 : {}", createData2);
        logger.error("createData : {}", createData);

        List<CredentialInfo> credential = credentialService.getCredentials(new HashMap<>());

        logger.error("credential : {}", credential);

        boolean isChecked = apiService.getCredentialsCheck(credential, createData.getType());
        boolean isChecked2 = apiService.getCredentialsNameCheck(credential, createData.getName());
        logger.error("isChecked : {}", isChecked);
        logger.error("isChecked2 : {}", isChecked2);

        if(isChecked && isChecked2){
            createData.setCloudType(apiService.getCloudType(createData));
            boolean isValid = apiService.validateCredential(createData, token);
            if(isValid) {
                String jsonString = null;
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

                CredentialInfo result = credentialService.createCredentialApi(createData);

                try {
                    jsonString = mapper.writeValueAsString(result);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                JSONObject jsonobj = JSONObject.fromObject(jsonString);

                logger.error("-------- jsonArray -------- ==== " + jsonobj);
                //생성 성공 시 리턴 값이 없다 하면
                return null;
                //생성 성공 시 리턴 값이 필요 하다 하면
            } else {
                throw new ValidationFailException("유효하지 않은 credential 정보 입니다.");
            }
        }else{
            throw new ValidationFailException("다른 credential 계정이 존재합니다.");
        }
    }
}
