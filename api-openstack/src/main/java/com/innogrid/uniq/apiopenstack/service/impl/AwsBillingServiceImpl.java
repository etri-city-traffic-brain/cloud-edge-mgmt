//package com.innogrid.uniq.apiopenstack.service.impl;
//
//import com.innogrid.uniq.apiaws.service.AwsBillingService;
//import com.innogrid.uniq.core.exception.CityHubUnAuthorizedException;
//import com.innogrid.uniq.core.exception.CredentialException;
//import com.innogrid.uniq.core.model.CredentialInfo;
//import com.innogrid.uniq.coreaws.model.CostDashboardnfo;
//import com.innogrid.uniq.coreaws.model.RequestUsageInfo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
//import software.amazon.awssdk.services.costexplorer.model.*;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * @author kkm
// * @date 2019.5.22
// * @brief
// */
//@Service
//public class AwsBillingServiceImpl implements AwsBillingService {
//    private final static Logger logger = LoggerFactory.getLogger(AwsBillingServiceImpl.class);
//
//    private CostExplorerClient getCostClient(CredentialInfo info) {
//
//        System.setProperty("aws.accessKeyId", info.getAccessId());
//        System.setProperty("aws.secretAccessKey", info.getAccessToken());
//
//        //AmazonElasticLoadBalancing elb = AmazonElasticLoadBalancingClientBuilder.defaultClient();
//
//        try {
//            CostExplorerClient ce = CostExplorerClient.builder().region(Region.AWS_GLOBAL).build();
//
//            return ce;
//        } catch (Exception e) {
//            logger.error("Failed to validate credential : '{}'", e.getMessage());
//            e.printStackTrace();
//            throw new CityHubUnAuthorizedException(e.getMessage());
//        }
//    }
//
//    public CostDashboardnfo getUsageDetail(CredentialInfo credentialInfo, RequestUsageInfo requestUsageInfo) {
//
//        if(credentialInfo == null) return null;
//
//        /*String startDate = "2019-05-01";
//        String endDate = "2019-05-31";
//        String granularity = "MONTHLY";*/
//
//        CostExplorerClient ce = getCostClient(credentialInfo);
//        List<String> metricList = new ArrayList<>();
//        metricList.add("BLENDED_COST");
//        metricList.add("UNBLENDED_COST");
//        metricList.add("AMORTIZED_COST");
//        metricList.add("NET_AMORTIZED_COST");
//        metricList.add("NET_UNBLENDED_COST");
//        metricList.add("USAGE_QUANTITY");
//        metricList.add("NORMALIZED_USAGE_AMOUNT");
//
//        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
//                .granularity(requestUsageInfo.getGranularity())
//                .timePeriod(DateInterval.builder().start(requestUsageInfo.getStartDate()).end(requestUsageInfo.getEndDate()).build())
//                .metrics(metricList)
//                .build();
//
//        GetCostAndUsageResponse response = ce.getCostAndUsage(request);
//        CostDashboardnfo info =  new CostDashboardnfo();
//
//        info.setBlendedCost(Double.valueOf(String.format("%.2f", (Double.valueOf(response.resultsByTime().get(0).total().get("BlendedCost").amount())))));
//        info.setAmortizedCost(Double.valueOf(String.format("%.2f", (Double.valueOf(response.resultsByTime().get(0).total().get("AmortizedCost").amount())))));
//        info.setNormalizedUsageAmount(Double.valueOf(String.format("%.2f", (Double.valueOf(response.resultsByTime().get(0).total().get("NormalizedUsageAmount").amount())))));
//        info.setUsageQuantity(Double.valueOf(String.format("%.2f", (Double.valueOf(response.resultsByTime().get(0).total().get("UsageQuantity").amount())))));
//        info.setNetUnblendedCost(Double.valueOf(String.format("%.2f", (Double.valueOf(response.resultsByTime().get(0).total().get("NetUnblendedCost").amount())))));
//        info.setNetAmortizedCost(Double.valueOf(String.format("%.2f", (Double.valueOf(response.resultsByTime().get(0).total().get("NetAmortizedCost").amount())))));
//        info.setUnblendedCost(Double.valueOf(String.format("%.2f", (Double.valueOf(response.resultsByTime().get(0).total().get("UnblendedCost").amount())))));
//
//        ce.close();
//
//        return info;
//    }
//
//    public CostDashboardnfo getForecastUsageDetail(CredentialInfo credentialInfo, RequestUsageInfo requestUsageInfo) {
//        if (credentialInfo == null) throw new CredentialException();
//
//        double TAX_RATE = 0.1; //세율
//        int DAY = 24 * 60 * 60 * 1000;
//
//        CostDashboardnfo info = new CostDashboardnfo();
//
//        //달력 가져오기
//        SimpleDateFormat dateTemplate = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar calendar = GregorianCalendar.getInstance();
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
//
//        Date currentMonthStartDate = calendar.getTime(); //월 시작일
//
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//        Date currentMonthEndDate = calendar.getTime(); //월 마지막일
//
//        Date today = new Date(); //오늘 날짜
//
//        calendar.setTime(today);
//        calendar.add(Calendar.DATE, -2);
//        Date beforeYesterday = calendar.getTime(); //그제 날짜 가져오기
//
//        //남은 날짜
//        long remainDateTime = currentMonthEndDate.getTime() - today.getTime();
//        long remainDate = remainDateTime / DAY;
//
//        //월 시작일부터 오늘까지 지난 날짜
//        long pastDaysTime = today.getTime() - currentMonthStartDate.getTime();
//        long pastDays = pastDaysTime / DAY;//지난 일수
//
//        //분모
//        double divider = 2;
//
//        //지난 날짜가 2보다 작을시 1로 나눔
//        if (pastDays < 2) {
//            divider = 1;
//        }
//
//        CostExplorerClient ce = getCostClient(credentialInfo);
//
//        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
//                .granularity(requestUsageInfo.getGranularity())
//                .timePeriod(DateInterval.builder().start(dateTemplate.format(currentMonthStartDate)).end(dateTemplate.format(today)).build())
//                .metrics("BLENDED_COST")
//                .build();
//
//        GetCostAndUsageResponse response = ce.getCostAndUsage(request);
//
//        //해당 월 1일부터 어제까지 사용한 금액
//        double currentUsageCost = Double.parseDouble(response.resultsByTime().get(0).total().get("BlendedCost").amount());
//
//        //세금 적용
//        currentUsageCost = currentUsageCost + currentUsageCost * TAX_RATE;
//
//        GetCostAndUsageRequest request2 = GetCostAndUsageRequest.builder()
//                .granularity(requestUsageInfo.getGranularity())
//                .timePeriod(DateInterval.builder().start(dateTemplate.format(beforeYesterday)).end(dateTemplate.format(today)).build())
//                .metrics("BLENDED_COST")
//                .build();
//
//        GetCostAndUsageResponse response2 = ce.getCostAndUsage(request2);
//
//        //최근 2일 사용량
//        double recentUsageCost = Double.parseDouble(response2.resultsByTime().get(0).total().get("BlendedCost").amount());
//
//        //예상 평균치 (최근 2일 사용량 / (2 또는 1) )
//        double forecastMean = recentUsageCost / divider;
//
//        //예상 사용액
//        //총합 = 현재사용량 + (예상평균치 * 잔여일수)
//        double total = currentUsageCost + (forecastMean * remainDate);
//
//        info.setBlendedCost(total);
//
//        return info;
//    }
//
////    public CostDashboardnfo getForecastUsageDetail(CredentialInfo credentialInfo, RequestUsageInfo requestUsageInfo) {
////
////        if(credentialInfo == null) return null;
////
////        /*String startDate = "2019-05-01";
////        String endDate = "2019-05-31";
////        String granularity = "MONTHLY";*/
////
////        CostExplorerClient ce = getCostClient(credentialInfo);
////        List<String> metricList = new ArrayList<>();
////        metricList.add("BLENDED_COST");
////        metricList.add("UNBLENDED_COST");
////        metricList.add("AMORTIZED_COST");
////        metricList.add("NET_AMORTIZED_COST");
////        metricList.add("NET_UNBLENDED_COST");
////        metricList.add("USAGE_QUANTITY");
////        metricList.add("NORMALIZED_USAGE_AMOUNT");
////
////        GetCostForecastRequest request = GetCostForecastRequest.builder()
////                .granularity(requestUsageInfo.getGranularity())
////                .timePeriod(DateInterval.builder().start(requestUsageInfo.getStartDate()).end(requestUsageInfo.getEndDate()).build())
////                .metric(Metric.BLENDED_COST)
////                .build();
////
////        logger.error("############ Metric.BLENDED_COST : {}", Metric.BLENDED_COST);
////
////        GetCostForecastResponse response = ce.getCostForecast(request);
////
////        logger.error("AwsBillingServiceImpl, getForecastUsageDetail , response : {}", response);
////
////        CostDashboardnfo info =  new CostDashboardnfo();
////
////        info.setBlendedCost(Double.valueOf(String.format("%.2f", (Double.valueOf(response.forecastResultsByTime().get(0).meanValue())))));
////
////        ce.close();
////
////
////        logger.error("#################");
////        logger.error("AwsBillingServiceImpl, getForecastUsageDetail , request : {}", request);
////        logger.error("AwsBillingServiceImpl, getForecastUsageDetail , startDate : {}", requestUsageInfo.getStartDate());
////        logger.error("AwsBillingServiceImpl, getForecastUsageDetail , endDate : {}", requestUsageInfo.getEndDate());
////        logger.error("AwsBillingServiceImpl, getForecastUsageDetail , metricList : {}", metricList);
////        logger.error("AwsBillingServiceImpl, getForecastUsageDetail , granularity : {}", requestUsageInfo.getGranularity());
////        logger.error("AwsBillingServiceImpl, getForecastUsageDetail , info : {}", info);
////        logger.error("#################");
////
////        return info;
////    }
//}
