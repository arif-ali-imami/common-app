package com.echoItSolution.common_app.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClientBuilder(WebClient.Builder builder) {
//        String baseUrl = "http://localhost:9090/api/v1/account";
        String baseUrl = "http://USER-SERVICE/api/v1/account";
        return builder
                .baseUrl(baseUrl)
                .filter((request, next) -> {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getCredentials() != null){
                        ClientRequest clientRequest = ClientRequest.from(request)
                                .header("Authorization", "Bearer " + auth.getCredentials())
                                .build();
                        return next.exchange(clientRequest);
                    }
                    return next.exchange(request);
                }).build();
    }

}
