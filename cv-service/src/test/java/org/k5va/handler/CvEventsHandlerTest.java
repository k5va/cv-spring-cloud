package org.k5va.handler;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.k5va.dto.CvDto;
import org.k5va.repository.CvRepository;
import org.k5va.service.CvService;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka
@SpringBootTest(
        properties = {
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"}
)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        MongoAutoConfiguration.class
})
class CvEventsHandlerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private CvService cvService;

    @MockBean
    private CvRepository cvRepository;

    @SpyBean
    private CvEventsHandler cvEventsHandler;

    @Value("${app.kafka-topics.cv-topic.name}")
    private String topic;

    @Test
    void handleIsCalled_onReceivedMessage() throws ExecutionException, InterruptedException {
        // given
        CvDto cvDto = new CvDto("1", "test", "test", "test",
                List.of("test"),
                List.of("test"),
                List.of("test"),
                "test",
                true,
                1L);

        String messageKey = cvDto.employeeId().toString();
        String messageId = UUID.randomUUID().toString();
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, messageKey, cvDto);
        producerRecord.headers().add(KafkaHeaders.RECEIVED_KEY, messageKey.getBytes());
        producerRecord.headers().add("messageId", messageId.getBytes());

        doReturn(cvDto).when(cvService).create(any(CvDto.class));

        ArgumentCaptor<String> messageKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CvDto> processedEventCaptor = ArgumentCaptor.forClass(CvDto.class);

        // when
        kafkaTemplate.send(producerRecord).get();

        // then
        verify(cvEventsHandler, timeout(5_000).times(1))
                .handle(processedEventCaptor.capture(),
                        messageKeyCaptor.capture(),
                        messageIdCaptor.capture());
        assertEquals(messageKey, messageKeyCaptor.getValue());
        assertEquals(messageId, messageIdCaptor.getValue());
        assertEquals(cvDto, processedEventCaptor.getValue());
    }
}