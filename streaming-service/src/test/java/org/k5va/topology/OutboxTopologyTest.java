package org.k5va.topology;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.k5va.dto.CvDto;
import org.k5va.dto.OutboxDto;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class OutboxTopologyTest {
    public static final String OUTBOX_TOPIC = "cv-app.public.outbox";
    public static final String UNKNOWN_OUTBOX_TOPIC = "unknown-outbox-topic";
    public static final String CV_TOPIC = "cv-topic";

    private TestInputTopic<String, OutboxDto> outboxTopic;
    private TestOutputTopic<String, OutboxDto> unknownOutboxTopic;
    private TestOutputTopic<String, CvDto> cvTopic;
    private TopologyTestDriver topologyTestDriver;

    @BeforeEach
    public void setUp() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        Properties props = new Properties();
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());

        new OutboxTopology(outboxObjectMapper(), OUTBOX_TOPIC, UNKNOWN_OUTBOX_TOPIC, CV_TOPIC)
                .outboxStream(streamsBuilder, outboxDtoJsonSerde());

        Topology topology = streamsBuilder.build();
        topologyTestDriver = new TopologyTestDriver(topology, props);

        Serde<String> keySerde = Serdes.String();
        JsonSerde<OutboxDto> outboxSerde = outboxDtoJsonSerde();
        JsonSerde<CvDto> cvDtoSerde = new JsonSerde<>(CvDto.class);

        outboxTopic = topologyTestDriver.createInputTopic(
                OUTBOX_TOPIC,
                keySerde.serializer(),
                outboxSerde.serializer());

        unknownOutboxTopic = topologyTestDriver.createOutputTopic(
                UNKNOWN_OUTBOX_TOPIC,
                keySerde.deserializer(),
                outboxSerde.deserializer());

        cvTopic = topologyTestDriver.createOutputTopic(
                CV_TOPIC,
                keySerde.deserializer(),
                cvDtoSerde.deserializer());
    }

    @AfterEach
    void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    void shouldAddCvDtoToKafkaTopicOnValidOutbox() throws JsonProcessingException {
        //Given
        OutboxDto outboxDto = new OutboxDto("1", LocalDateTime.now(), """
                {
                  "id":null,
                  "education" : "MIT",
                  "description": "i am top programmer",
                  "workExperience": "I have been working for 5 years",
                  "skills": ["Java", "Python"],
                  "languages": ["Russian", "English"],
                  "certificates": ["Certificate 1", "Certificate 2"],
                  "linkedId": "linkedId",
                  "isOpenToWork": true,
                  "employeeId":18
                }
                """);
        CvDto cvDto = outboxObjectMapper().readValue(outboxDto.payload(), CvDto.class);

        //When
        outboxTopic.pipeInput(outboxDto.id(), outboxDto);

        //Then
        CvDto receivedCvDto = cvTopic.readRecord().value();
        assertEquals(cvDto, receivedCvDto);
        assertTrue(this.unknownOutboxTopic.isEmpty());
    }

    private ObjectMapper outboxObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private JsonSerde<OutboxDto> outboxDtoJsonSerde() {
        JsonSerde<OutboxDto> outboxSerde = new JsonSerde<>();
        outboxSerde.deserializer().configure(
                Map.of(
                        JsonDeserializer.TRUSTED_PACKAGES, "org.k5va.*",
                        JsonDeserializer.VALUE_DEFAULT_TYPE, OutboxDto.class
                ),
                false);

        return outboxSerde;
    }
}