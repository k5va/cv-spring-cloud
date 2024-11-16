package org.k5va.service;

import org.k5va.dto.CvDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexey Kulikov
 */
@Service
public class CvService {
    public CvDto getCv(Long id) {
        return new CvDto(
                id,
                "education",
                "description",
                "workExperience",
                List.of("skills"),
                List.of("languages"),
                List.of("certificates"),
                "linkedId",
                true);
    }
}
