package org.k5va.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.k5va.config.CvTopicProperties;
import org.k5va.dto.CreateEmployeeDto;
import org.k5va.events.CreateCvEvent;
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
    private final KafkaTemplate<String, CreateCvEvent> kafkaTemplate;

    public void sendCreateCvEvent(@NonNull Long employeeId, @NonNull CreateEmployeeDto createEmployeeDto) {
        Assert.notNull(employeeId, "employeeId must not be null");
        Assert.notNull(createEmployeeDto, "Create employee dto must not be null");
        log.info("Sending create cv event with dto: {}", createEmployeeDto);

        try {
            CreateCvEvent createCvEvent = new CreateCvEvent(employeeId,
                    createEmployeeDto.education(),
                    createEmployeeDto.description(),
                    createEmployeeDto.workExperience(),
                    createEmployeeDto.skills(),
                    createEmployeeDto.languages(),
                    createEmployeeDto.certificates(),
                    createEmployeeDto.linkedId(),
                    createEmployeeDto.isOpenToWork());

            ProducerRecord<String, CreateCvEvent> producerRecord = createProducerRecord(createCvEvent);
            SendResult<String, CreateCvEvent> result = kafkaTemplate
                    .send(producerRecord)
                    .get();
            log.info("Create CV event sent: {}", result.getRecordMetadata().partition());
        } catch (Exception e) {
            log.error("Failed to send create CV event", e);
            throw new RuntimeException(e);
        }    }

    private ProducerRecord<String, CreateCvEvent> createProducerRecord(CreateCvEvent createCvEvent) {
        var producerRecord = new ProducerRecord<>(
                cvTopicProperties.getName(),
                createCvEvent.employeeId().toString(),
                createCvEvent
        );

        producerRecord.headers().add("messageId", generateMessageId());

        return producerRecord;
    }

    private byte[] generateMessageId() {
        return UUID.randomUUID().toString().getBytes();
    }
}
