package com.innogrid.uniq.client.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by root on 15. 2. 17.
 */
public class JsonUtil {
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper mapper = new ObjectMapper();

    private static JsonFactory factory = new JsonFactory();;

    public static String stringify(Object object) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            String string = mapper.writeValueAsString(object);

            if(logger.isInfoEnabled()) {
                logger.info("stringify object : {}", string);
            }

            return string;
        } catch (JsonProcessingException e) {
            logger.error("Failed to stringify JSON : '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T> T bind(String jsonString, Class<T> tClass) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return mapper.readValue(jsonString, tClass);
        } catch (IOException e) {
            logger.error("Failed to bind JSON : '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T> T bind(String jsonString, CollectionType collectionType) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return mapper.readValue(jsonString, collectionType);
        } catch (IOException e) {
            logger.error("Failed to bind JSON : '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static JsonParser parser(String jsonString) {
        try {
            return factory.createParser(jsonString);
        } catch (IOException e) {
            logger.error("Failed to parse JSON : '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static JsonNode tree(String jsonString) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return mapper.readTree(jsonString);
        } catch (IOException e) {
            logger.error("Failed to read tree : '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
