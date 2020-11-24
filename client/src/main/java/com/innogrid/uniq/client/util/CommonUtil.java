package com.innogrid.uniq.client.util;

import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.RequestInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.service.CredentialService;
import com.innogrid.uniq.coredb.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public final class CommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public final static int MAX_RETRY_COUNT = 10;
    public final static int SLEEP_TIME = 1000;

    public static int sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return ms;
    }

    public static void sendMessage(SimpMessagingTemplate simpMessagingTemplate, String path, String action, String reqUser, Object data) {
        if (simpMessagingTemplate != null) {
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setId(UUID.randomUUID().toString());
            requestInfo.setAction(action);
            requestInfo.setUserId(reqUser);
            requestInfo.setObject(data);
            simpMessagingTemplate.convertAndSend("/topic" + path +"/" + reqUser,
                    MessageBuilder.withPayload(requestInfo).build());
        }
    }

    /**
     * @author hkkim
     * @date 2019.5.23
     * @brief BigDecimal 자료형 변환 함수
     */
    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = null;
        if(value != null) {
            if(value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if(value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if(value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if(value instanceof Number) {
                ret = new BigDecimal(((Number)value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }

    /**
     * @author hkkim
     * @date 2019.5.23
     * @brief 리스트 내의 값 평균 계산 함수
     */
    public  static Double getListAverage(List<Double> list) {
        if(list.size() == 0) return new Double(0);

        Double result = new Double(0);
        for(Double num : list) {
            result += num;
        }
        result = result / list.size();
        return result;
    }

    /**
     * @author hkk
     * @date 2019.6.24
     * @brief User UUID 정보 조회
     */
    public static String getUserUUID() {
        UserInfo userInfo = null;

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            userInfo = (UserInfo) session.getAttribute("userInfo");
        } catch (Exception e) {
            // logger.error("Failed to get session UserId : '{}'", e.getMessage());
        }

        String userUUID = "";
        if(userInfo != null) {
            userUUID = userInfo.getId();
        }
        return userUUID;
    }

    public static void setSessionCloudList(HttpSession session, CredentialService credentialService, ProjectService projectService) {
        UserInfo info = (UserInfo) session.getAttribute("userInfo");
        List<CredentialInfo> clouds = credentialService.getCredentialsFromMemory();

        session.removeAttribute("clouds");
//        if(info.getGroupId() != null && !info.getGroupId().equals("")) {
//            List<ProjectInfo> projectInfoList = projectService.getProjects(new HashMap<String, Object>() {{
//                put("groupId", info.getGroupId());
//            }});
//
//            if(projectInfoList.size() == 0) {
//                session.setAttribute("clouds", new ArrayList<>());
//            } else {
//                List<CredentialInfo> groupClouds = new ArrayList<>();
//
//                for (int i = 0; i < clouds.size(); i++) {
//                    for (int j=0; j < projectInfoList.size(); j++) {
//                        if(clouds.get(i).getId().equals(projectInfoList.get(j).getCloudId())) {
//                            groupClouds.add(clouds.get(i));
//                        }
//                    }
//                }
//
//                session.setAttribute("clouds", groupClouds);
//            }.
//        } else {
//            session.setAttribute("clouds", clouds);
//        }

        session.setAttribute("clouds", clouds);
    }

    public static HttpHeaders getAuthHeaders(String credentialInfo, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("credential", credentialInfo);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        headers.set("Authorization", "Bearer " + token);

        return headers;
    }
}
