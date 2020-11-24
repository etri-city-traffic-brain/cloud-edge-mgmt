package com.innogrid.uniq.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by kkm on 19. 5. 2
 *
 * @brief 프로퍼티 컨트롤러 클래스 (properties 파일 읽기)
 */
@Controller
public class PropertiesController {
  private static final Logger logger = LoggerFactory.getLogger(PropertiesController.class);


  @Autowired
  private MessageSource messageSource;

  /**
   * @param propertiesName Property Name
   * @param httpSession    user info
   * @param request        HttpServletRequest value
   * @param response       HttpServletResponse value
   * @brief Get Multilingual Property
   */
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/properties/{propertiesName}", method = RequestMethod.GET)
  public void getProperties(@PathVariable("propertiesName") String propertiesName,
                            HttpSession httpSession, HttpServletResponse response, HttpServletRequest request) throws IOException {
    logger.debug("propertiesController[getProperties]");

    Locale locales = request.getLocale();

    String configLocale = (String) httpSession.getAttribute("configLocale");
    if (configLocale == null) {
      configLocale = "ko";
    }

    OutputStream outputStream = null;
    InputStream inputStream = null;

    try {

      outputStream = response.getOutputStream();
      Resource resource = null;
      String propertiesPath = "/i18n/" + propertiesName + "_" + locales.toString().substring(0, 2) + ".properties";
      httpSession.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, request.getLocale());

      if (!configLocale.equals("auto")) {
        propertiesPath = "/i18n/" + propertiesName + "_" + configLocale + ".properties";
        httpSession.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, StringUtils.parseLocaleString(configLocale));
      }

      logger.debug("properties:" + propertiesPath);

      resource = new ClassPathResource(propertiesPath);

      inputStream = resource.getInputStream();

      while (true) {
        int data = inputStream.read();
        if (data == -1) {

          break;
        }
        outputStream.write(data);
      }

      inputStream.close();
      outputStream.close();


    } catch (Exception e) {
      logger.error("Failed to get Properties : '{}'", e.getMessage());
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
      if (outputStream != null) {
        outputStream.close();
      }


    }

  }
}
