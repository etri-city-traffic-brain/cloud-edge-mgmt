package com.innogrid.uniq.scheduler;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.innogrid.uniq", exclude = DataSourceAutoConfiguration.class)
public class SchedulerApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(30000); // 읽기시간초과, ms
        factory.setConnectTimeout(30000); // 연결시간초과, ms
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100) // connection pool 적용
                .setMaxConnPerRoute(5) // connection pool 적용
                .build();
        factory.setHttpClient(httpClient); // 동기실행에 사용될 HttpClient 세팅 RestTemplate restTemplate = new RestTemplate(factory);

        RestTemplate restTemplate = new RestTemplate(factory);

        for(HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
            if(messageConverter instanceof AllEncompassingFormHttpMessageConverter) {
                ((AllEncompassingFormHttpMessageConverter) messageConverter).setCharset(Charset.forName("UTF-8"));
                ((AllEncompassingFormHttpMessageConverter) messageConverter).setMultipartCharset(Charset.forName("UTF-8"));
            }
        }
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        return restTemplate;
    }


    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

}
