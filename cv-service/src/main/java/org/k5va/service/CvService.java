package org.k5va.service;

import lombok.RequiredArgsConstructor;
import org.k5va.dto.CvDto;
import org.k5va.mapper.CvMapper;
import org.k5va.repository.CvRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CvService {
    private final CvRepository cvRepository;
    private final CvMapper cvMapper;

    public CvDto getCv(Long id) {
        return cvRepository.findById(id)
                .map(cvMapper::toCvDto)
                .orElseThrow();
    }
}
