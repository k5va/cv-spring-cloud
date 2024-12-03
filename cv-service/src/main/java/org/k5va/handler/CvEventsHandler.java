package org.k5va.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.CvDto;
import org.k5va.error.NonretryableException;
import org.k5va.service.CvService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${app.kafka-topics.cv-topic.name}")
@Slf4j
@RequiredArgsConstructor
public class CvEventsHandler {
    private final CvService cvService;

    @KafkaHandler
    public void handle(@Payload CvDto cvDto,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        try {
            log.info("Create CV event received: {} with key {}", cvDto, messageKey);
            CvDto createdCvDto = cvService.create(cvDto);
            log.info("Create CV event processed: {}", createdCvDto);
        } catch (Exception e) {
            throw new NonretryableException(e);
        }
    }
}
