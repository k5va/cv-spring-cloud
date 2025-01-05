package org.k5va.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.CvDto;
import org.k5va.service.CvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
@Slf4j
public class CvController {
    private final CvService cvService;

    @GetMapping("/{id}")
    public ResponseEntity<CvDto> getCv(@PathVariable String id) {
        log.info("Get cv {}", id);
        return ResponseEntity.ok(cvService.getCv(id));
    }

    @GetMapping
    public ResponseEntity<List<CvDto>> getCvs() {
        log.info("Get all cvs");
        return ResponseEntity.ok(cvService.getCvs());
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<CvDto> getCvByEmployeeId(@PathVariable Long id) {
        log.info("Get cv by employee id {}", id);
        return ResponseEntity.ok(cvService.getCvByEmployeeId(id));
    }
}
