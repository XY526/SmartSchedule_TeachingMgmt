package com.crytalwjh.smartschedule_teachingmgmt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AiConfig {
    @Value("${ai.tongyi.api-key}")
    private String tongyiApiKey;

    @Value("${ai.tongyi.api-url}")
    private String tongyiApiUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(getClientHttpRequestFactory());
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30 seconds
        factory.setReadTimeout(30000);    // 30 seconds
        return factory;
    }

    public String getTongyiApiKey() {
        return tongyiApiKey;
    }

    public String getTongyiApiUrl() {
        return tongyiApiUrl;
    }
} 