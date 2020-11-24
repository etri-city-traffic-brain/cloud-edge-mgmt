package com.innogrid.uniq.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CookieUtil {

  private static Logger logger = LoggerFactory.getLogger(CookieUtil.class);

  public String getName(Cookie[] cookies, String name) {
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          try {
            return URLDecoder.decode(cookie.getValue(), "utf-8");
          } catch (UnsupportedEncodingException e) {
            logger.error("Failed to encoding cookie value : '{}'", e.getMessage());
          }
        }
      }
    }

    return "";
  }
}
