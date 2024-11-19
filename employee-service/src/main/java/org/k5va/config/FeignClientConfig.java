package org.k5va.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Configuration
public class FeignClientConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            HttpStatus status = HttpStatus.resolve(response.status());
            return switch (status) {
                case NOT_FOUND -> new ResponseStatusException(status, methodKey + ": " + "Resource not found");
                case BAD_REQUEST -> new ResponseStatusException(status, methodKey + ": " + response.reason());
                default -> new ResponseStatusException(status, response.reason());
            };
        };
    }
}
