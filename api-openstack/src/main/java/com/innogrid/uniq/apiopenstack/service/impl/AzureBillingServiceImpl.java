package com.innogrid.uniq.apiopenstack.service.impl;

import com.innogrid.uniq.apiazure.service.AzureBillingService;
import com.innogrid.uniq.core.exception.CityHubUnAuthorizedException;
import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.coreazure.model.RequestUsageInfo;
import com.innogrid.uniq.coreazure.model.ResourceUsageInfo;
import com.innogrid.uniq.coreazure.model.TokenInfo;
import com.innogrid.uniq.coreazure.util.CommonUtil;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.rest.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.core.UriBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kkm
 * @date 2019.5.22
 * @brief
 */
@Service
public class AzureBillingServiceImpl implements AzureBillingService {
    private final static Logger logger = LoggerFactory.getLogger(AzureBillingServiceImpl.class);

    private String subscriptionId;
    private String subscriptionDisplayName;

    private final String GRANT_TYPE = "client_credentials";
    private final String RESOURCE_URL = "https://management.azure.com/";
    private final String API_VERSION = "2019-01-01";

    private Azure Credential(CredentialInfo credentialInfo) {
        Azure azure = null;
        try {
            ApplicationTokenCredentials credential = new ApplicationTokenCredentials(
                    credentialInfo.getAccessId(),
                    credentialInfo.getTenant(),
                    credentialInfo.getAccessToken(),
                    AzureEnvironment.AZURE
            );

            Azure.Authenticated authenticated = Azure.configure()
                    .withLogLevel(LogLevel.BASIC)
                    .authenticate(credential);
            if(credentialInfo.getProjectId()==null ||credentialInfo.getProjectId().isEmpty()){
                azure = authenticated.withDefaultSubscription();
            }else{
                azure = authenticated.withSubscription(credentialInfo.getSubscriptionId());
            }
            this.subscriptionDisplayName = azure.subscriptions().getById(azure.subscriptionId()).displayName();
            this.subscriptionId = azure.subscriptionId();

            return azure;
        } catch (Exception e) {
            logger.error("Failed to validate credential : '{}'", e.getMessage());
            e.printStackTrace();
            throw new CityHubUnAuthorizedException(e.getMessage());
        }
    }

    public TokenInfo getToken(CredentialInfo credentialInfo) {

        Azure azure = Credential(credentialInfo);

        // Request URL 정보 설정
        String uriTemplate = "https://login.microsoftonline.com/{tenantId}/oauth2/token";
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("tenantId", credentialInfo.getTenant());
        UriBuilder builder = UriBuilder.fromPath(uriTemplate);
        URI output = builder.buildFromMap(uriParams);
        String requestUrl = output.toASCIIString();

        // Request Body 정보 설정
        MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
        reqParams.add("grant_type", GRANT_TYPE);
        reqParams.add("client_id", credentialInfo.getAccessId());
        reqParams.add("client_secret", credentialInfo.getAccessToken());
        reqParams.add("resource", RESOURCE_URL);

        // REST API 호출 (토큰 발급)
        TokenInfo tokenInfo = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.postForObject(requestUrl, reqParams, Map.class);
            tokenInfo = new TokenInfo(response);
        } catch (RestClientException e) {
            logger.error("Failed to get token : '{}'", e.getMessage());
            e.printStackTrace();
        }

        return tokenInfo;
    }

    public ResourceUsageInfo getUsageDetail(CredentialInfo credentialInfo, RequestUsageInfo requestUsageInfo) {

        Azure azure = Credential(credentialInfo);

        // Request URL 정보 설정
        String uriTemplate = "https://management.azure.com/subscriptions/{subscriptionId}/providers/Microsoft.Consumption/usageDetails";
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("subscriptionId", this.subscriptionId);
        UriBuilder builder = UriBuilder.fromPath(uriTemplate);
        URI output = builder.buildFromMap(uriParams);

        // 리소스 조회 시작일자, 종료일자 필터 설정
        String startDt = requestUsageInfo.getStartDate();
        String endDt = requestUsageInfo.getEndDate();
        String filter = String.format("properties/usageStart ge '%s' AND properties/usageStart le '%s'", startDt, endDt);

        // 필터 설정
        UriComponents uriComponents = UriComponentsBuilder.fromUri(output)
                .queryParam("api-version", API_VERSION)
                .queryParam("$filter", filter)
                .build();

        String requestUrl = uriComponents.toUriString();

        TokenInfo token = getToken(credentialInfo);

        // Request Header 정보 셋팅
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.set("Authorization", token.getHeaderInfo());

        ResourceUsageInfo resourceUsageInfo = null;

        // REST API 호출 (사용 리소스 목록 조회)
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<ResourceUsageInfo> response = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity(header), ResourceUsageInfo.class);
            resourceUsageInfo = response.getBody();
        } catch (RestClientException e) {
            logger.error("Failed to get UsageDetail : '{}'", e.getMessage());
            e.printStackTrace();
        }

        return resourceUsageInfo;
    }

    public BigDecimal getExchangeRate(BigDecimal cost, String curCurrency, String changeCurrency) {

        // Request URL 정보 설정
        String uriTemplate = "https://earthquake.kr:23490/query/{exchange}";
        String exchange = changeCurrency.toUpperCase() + curCurrency.toUpperCase();
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("exchange", exchange);
        UriBuilder builder = UriBuilder.fromPath(uriTemplate);
        URI output = builder.buildFromMap(uriParams);

        String requestUrl = output.toString();

        BigDecimal exchangeRate = new BigDecimal(1);
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(requestUrl, Map.class);
            List<Object> exchangeList = (List<Object>)response.get(exchange);
            exchangeRate = new BigDecimal(exchangeList.get(0).toString());
            exchangeRate = exchangeRate.setScale(2, BigDecimal.ROUND_DOWN);
        } catch (RestClientException e) {
            logger.error("Failed to get ExchangeRate : '{}'", e.getMessage());
            e.printStackTrace();
        }
        BigDecimal exchangeCost = cost.divide(exchangeRate, 2, BigDecimal.ROUND_DOWN);
        return exchangeCost;
    }

    @Override
    public BigDecimal getForecastCost(CredentialInfo credentialInfo, RequestUsageInfo requestUsageInfo, BigDecimal totalCost) {
//        logger.info("[{}] Get ForecastCost", CommonUtil.getUserUUID());

        int estimateDays = 0;
        int forecastDays = 0;

        Calendar calendar = GregorianCalendar.getInstance();
        estimateDays = calendar.get(Calendar.DATE) - 1;

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        forecastDays = calendar.get(Calendar.DATE) - estimateDays;

        // 일자별 사용량 계산 (최근 2일 값 기준)
        int recentDaysCnt = 2;
        List<Double> recentDailyCost = new ArrayList<>();

        calendar.set(Calendar.DAY_OF_MONTH, estimateDays);
        SimpleDateFormat template = new SimpleDateFormat("yyyy-MM-dd");

        int avgDays = estimateDays < recentDaysCnt ? estimateDays : recentDaysCnt;
        for(int i=0; i<avgDays; i++) {
            calendar.add(Calendar.DATE, -i);
            Date startDt = calendar.getTime();
            Date endDt = startDt;

            requestUsageInfo.setStartDate(template.format(startDt));
            requestUsageInfo.setEndDate(template.format(endDt));

            ResourceUsageInfo dailyCostInfo = this.getUsageDetail(credentialInfo, requestUsageInfo);
            recentDailyCost.add(dailyCostInfo.getTotalCost().doubleValue());
        }

        BigDecimal dailyAvgCost = new BigDecimal(CommonUtil.getListAverage(recentDailyCost));
        dailyAvgCost = dailyAvgCost.setScale(2, RoundingMode.DOWN);

        // 예상 사용량 계산 (일별 예상금액 * 남은 일수)
        BigDecimal predictionCost = dailyAvgCost.multiply(new BigDecimal(forecastDays));
        BigDecimal forecastCost = totalCost.add(predictionCost);

//        logger.info("[{}] Get ForecastCost Complete", CommonUtil.getUserUUID());
        return forecastCost;
    }

    @Override
    public BigDecimal getCurrencyExchange(BigDecimal totalCost, String curCurrency, String changeCurrency) {
//        logger.info("[{}] Get CurrencyExchange", CommonUtil.getUserUUID());

        BigDecimal exchangeRate = getExchangeRate(totalCost, curCurrency, changeCurrency);
//        logger.info("[{}] Get CurrencyExchange Complete", CommonUtil.getUserUUID());
        return exchangeRate;
    }
}
