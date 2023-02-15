package com.innogrid.uniq.scheduler.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonProp {

    public static String API_GATEWAY_URL;
//    @Value("${apigateway.url}")
    @Value("${apigateway_local.url}")
//    @Value("http://127.0.0.1:9001")
    public void setApiGatewayUrl(String apiUrl) {
        API_GATEWAY_URL = apiUrl;
    }


    public static String API_OPENSTACK_URL;
    @Value("${openstack.url}")
    public void setApiOpenstackUrl(String apiOpenstackUrl) { API_OPENSTACK_URL = apiOpenstackUrl; }



    public static final String OPENSTACK_PATH = "/infra/cloudServices/openstack";


    public static final String OPENSTACK_PATH_LOCAL = "/openstack/infra/cloudServices/openstack";

}
