package com.echoItSolution.common_app.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class CommonConfig {

    @Bean
    @SuppressWarnings("uncheck")
    public RestTemplate restTemplateConfig(RestTemplateBuilder builder){
        return builder
                .additionalInterceptors((request, body, execution) -> {
                    // Get current HTTP request
                    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs != null) {
                        HttpServletRequest currentRequest = attrs.getRequest();
                        String authHeader = currentRequest.getHeader("Authorization");
                        if (authHeader != null) {
                            request.getHeaders().set("Authorization", authHeader);
                        }
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
