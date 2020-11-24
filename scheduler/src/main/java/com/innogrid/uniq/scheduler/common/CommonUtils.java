package com.innogrid.uniq.scheduler.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;

public class CommonUtils {
    public static HttpHeaders getAuthHeaders(String credentialInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("credential", credentialInfo);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));

        return headers;
    }
}
