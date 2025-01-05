package org.k5va.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.k5va.config.CvTopicProperties;
import org.k5va.dto.CvDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CvProducer {
    private final CvTopicProperties cvTopicProperties;
    private final KafkaTemplate<String, CvDto> kafkaTemplate;

    public void sendCreateCvEvent(@NonNull Long employeeId, @NonNull CvDto cvDto) {
        Assert.notNull(employeeId, "employeeId must not be null");
        Assert.notNull(cvDto, "cvDto must not be null");
        log.info("Sending create cv event with dto: {}", cvDto);

        try {
            ProducerRecord<String, CvDto> producerRecord = createProducerRecord(cvDto);
            SendResult<String, CvDto> result = kafkaTemplate
                    .send(producerRecord)
                    .get();
            log.info("Create CV event sent: {}", result.getRecordMetadata().partition());
        } catch (Exception e) {
            log.error("Failed to send create CV event", e);
            throw new RuntimeException(e);
        }
    }

    private ProducerRecord<String, CvDto> createProducerRecord(CvDto cvDto) {
        var producerRecord = new ProducerRecord<>(
                cvTopicProperties.getName(),
                cvDto.employeeId().toString(),
                cvDto
        );

        producerRecord.headers().add("messageId", generateMessageId());

        return producerRecord;
    }

    private byte[] generateMessageId() {
        return UUID.randomUUID().toString().getBytes();
    }
}
