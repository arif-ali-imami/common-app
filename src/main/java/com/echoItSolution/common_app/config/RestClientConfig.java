package com.echoItSolution.common_app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClient;

//@Profile("booking") // only loaded when 'booking' profile is active : spring.profiles.active=booking
@RequiredArgsConstructor
@Configuration
public class RestClientConfig {

    private final DiscoveryClient discoveryClient;

    @Bean
    @LoadBalanced   // applies load balancing
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
//    @ConditionalOnProperty(value = "rest.client.enabled", havingValue = "true", matchIfMissing = false)
    public RestClient restClient(RestClient.Builder builder){
        // 1st approach with hard coded url
//        String baseUrl = "http://localhost:9090/api/v1/account";
        // 2nd approach with fetching service instance from eureka
//        String baseUrl = discoveryClient.getInstances("USER-SERVICE")
//                .stream()
//                .findFirst()
//                .map(si -> si.getUri().toString())
//                .orElseThrow(() -> new RuntimeException("No user-service instance found"))+"/api/v1/account";
        // 3rd approach with load balancing
        String baseUrl = "http://USER-SERVICE"; // with load balancing
        return builder
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getCredentials() != null) {
                        request.getHeaders().setBearerAuth(auth.getCredentials().toString());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
