package org.k5va.client;

import org.k5va.dto.CvDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CV-SERVICE")
public interface CvServiceClient {

    @GetMapping("/cv/{id}")
    CvDto getCvById(@PathVariable Long id);
}
