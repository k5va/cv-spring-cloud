package org.k5va.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.OutboxType;
import org.k5va.generated.tables.records.OutboxRecord;
import org.k5va.processor.OutboxRecordProcessor;
import org.k5va.repository.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final OutboxRecordProcessor recordProcessor;

    @Transactional
    public void processAll() {
        outboxRepository
                .selectForProcessing()
                .forEach(this::process);
    }

    private void process(OutboxRecord outboxRecord) {
        try {
            recordProcessor.process(outboxRecord);
            outboxRepository.delete(outboxRecord);
            log.info("Processed data: {}", outboxRecord.getPayload());
        } catch (Exception e) {
            log.warn("Failed to process data", e);
        }
    }

    @SneakyThrows
    public void create(String payload, OutboxType type) {
        OutboxRecord outboxRecord = new OutboxRecord();
        outboxRecord.setPayload(payload);
        outboxRecord.setType(type.name());
        outboxRepository.create(outboxRecord);
    }
}
