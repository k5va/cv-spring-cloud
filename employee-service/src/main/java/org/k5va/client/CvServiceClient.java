package org.k5va.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.k5va.config.FeignClientConfig;
import org.k5va.dto.CvDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "CV-SERVICE", configuration = FeignClientConfig.class)
public interface CvServiceClient {
    Logger logger = LoggerFactory.getLogger(CvServiceClient.class);

    @GetMapping("/cv/employee/{id}")
    @Retry(name = "cv-service")
    @CircuitBreaker(name = "cv-service", fallbackMethod = "getCvByEmployeeIdFallback")
    CvDto getCvByEmployeeId(@PathVariable String id);

    default CvDto getCvByEmployeeIdFallback(String id, Throwable t) {
        logger.warn("CV-SERVICE is not available, using fallback: {}", t.getMessage());
        return new CvDto(
                id,
                "High school",
                "I am a student",
                "I have been working for 5 years",
                List.of("Java", "Python"),
                List.of("Russian", "English"),
                List.of("Certificate 1", "Certificate 2"),
                "linkedId",
                true,
                1L
        );
    }
}
