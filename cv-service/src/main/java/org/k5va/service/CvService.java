package org.k5va.service;

import lombok.RequiredArgsConstructor;
import org.k5va.dto.CvDto;
import org.k5va.mapper.CvMapper;
import org.k5va.repository.CvRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CvService {
    private final CvRepository cvRepository;
    private final CvMapper cvMapper;

    public CvDto getCv(String id) {
        return cvRepository.findById(id)
                .map(cvMapper::toCvDto)
                .orElseThrow();
    }

    public List<CvDto> getCvs() {
        return cvRepository.findAll()
                .stream()
                .map(cvMapper::toCvDto)
                .toList();
        }

    public CvDto getCvByEmployeeId(Long id) {
        return cvRepository.findByEmployeeId(id)
                .map(cvMapper::toCvDto)
                .orElseThrow(() -> new RuntimeException("Cv not found for employee: " + id));
    }

    public CvDto create(CvDto cvDto) {
        return Optional.of(cvRepository.save(cvMapper.toCvDocument(cvDto)))
                .map(cvMapper::toCvDto)
                .orElseThrow(() -> new RuntimeException("Cv not created"));
    }
}
