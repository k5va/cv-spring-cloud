package org.k5va.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.CvDto;
import org.k5va.dto.OutboxType;
import org.k5va.generated.tables.records.OutboxRecord;
import org.k5va.producer.CvProducer;
import org.k5va.repository.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final CvProducer cvProducer;

    @Transactional
    public void processOutbox() {
        outboxRepository.selectRecordsToRetry().forEach(this::processOutBoxItem);
    }

    private void processOutBoxItem(OutboxRecord outboxItem) {
        try {
            CvDto cvDto = objectMapper.readValue(outboxItem.getPayload(), CvDto.class);
            cvProducer.sendCreateCvEvent(cvDto);
            outboxRepository.delete(outboxItem);
            log.info("Processed data: {}", outboxItem.getPayload());
        } catch (Exception e) {
            log.warn("Failed to process data", e);
        }
    }

    @SneakyThrows
    public void createOutboxRecord(CvDto cvDto) {
        OutboxRecord outboxRecord = new OutboxRecord();
        outboxRecord.setPayload(objectMapper.writeValueAsString(cvDto));
        outboxRecord.setType(OutboxType.CV.name());
        outboxRepository.create(outboxRecord);
    }
}
