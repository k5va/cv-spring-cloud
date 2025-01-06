package org.k5va.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.CvDto;
import org.k5va.generated.tables.records.OutboxRecord;
import org.k5va.producer.CvProducer;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CvOutboxProcessor implements OutboxProcessor {
    private final ObjectMapper objectMapper;
    private final CvProducer cvProducer;

    @SneakyThrows
    @Override
    public void process(OutboxRecord outboxRecord) {
        CvDto cvDto = objectMapper.readValue(outboxRecord.getPayload(), CvDto.class);
        cvProducer.sendCreateCvEvent(cvDto);
    }
}
