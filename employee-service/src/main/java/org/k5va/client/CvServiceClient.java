package org.k5va.client;

import lombok.RequiredArgsConstructor;
import org.k5va.dto.CvDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author Alexey Kulikov
 */
@Component
@RequiredArgsConstructor
public class CvServiceClient {
    private static final String CV_BY_ID_URL = "http://CV-SERVICE/cv/{id}";

    private final RestTemplate restTemplate;

    public CvDto getCvById(Long id) {
        return restTemplate.getForObject(CV_BY_ID_URL, CvDto.class, id);
    }
}
