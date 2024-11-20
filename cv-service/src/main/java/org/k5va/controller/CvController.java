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

@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
@Slf4j
public class CvController {
    private final CvService cvService;

    @GetMapping("/{id}")
    public ResponseEntity<CvDto> getCv(@PathVariable Long id) {
        log.info("Get cv {}", id);
        return ResponseEntity.ok(cvService.getCv(id));
    }
}
