package org.k5va.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.CvDto;
import org.k5va.error.NonretryableException;
import org.k5va.events.CreateCvEvent;
import org.k5va.mapper.CvMapper;
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
    private final CvMapper cvMapper;

    @KafkaHandler
    public void handle(@Payload CreateCvEvent createCvEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        try {
            log.info("Create CV event received: {} with key {}", createCvEvent, messageKey);
//            var processedEvent = processedEventRepository.findByMessageId(messageId);
//            if (processedEvent.isPresent()) { // событие уже обработали ранее
//                log.info("Product created event already processed: {}", productCreatedEvent);
//                return; // закомитит offset в топике и это сообщение больше не будет обработано
//            }

            // perform some business logic
            CvDto createdCvDto = cvService.create(cvMapper.toCvDto(createCvEvent));
            log.info("Create CV event processed: {}", createdCvDto);

//            processedEventRepository.save(ProcessedEvent.builder()
//                    .productId(productCreatedEvent.getProductId())
//                    .messageId(messageId)
//                    .build());
        } catch (Exception e) {
            throw new NonretryableException(e);
        }
    }
}
