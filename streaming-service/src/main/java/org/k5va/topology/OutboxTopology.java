package org.k5va.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.k5va.dto.CvDto;
import org.k5va.dto.OutboxDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OutboxTopology {

    private final ObjectMapper outboxObjectMapper;

    @Value("${app.kafka-topics.outbox-topic.name}")
    private String outboxTopic;

    @Value("${app.kafka-topics.unknown-outbox-topic.name}")
    private String unknownOutboxTopic;

    @Value("${app.kafka-topics.cv-topic.name}")
    private String cvTopic;

    @Bean
    public KStream<String, OutboxDto> outboxStream(StreamsBuilder streamsBuilder,
                                                   JsonSerde<OutboxDto> outboxDtoJsonSerde) {

        JsonSerde<CvDto> cvSerde = new JsonSerde<>();
        Serde<String> keySerde = Serdes.String();

        KStream<String, OutboxDto> outboxStream = streamsBuilder
                .stream(outboxTopic, Consumed.with(keySerde, outboxDtoJsonSerde))
                .peek((key, value) -> log.debug("Incoming outbox key {}, value {}", key, value));

        var outboxTypeStreamMap = outboxStream
                .split(Named.as("type-"))
                .branch((key, outboxDto) -> true, //TODO: check outbox type
                        Branched.as("cv"))
                .defaultBranch(Branched.as("unknown"));

        outboxTypeStreamMap
                .get("type-cv")
                .mapValues(this::parseCvDto)
                .to(cvTopic, Produced.with(keySerde, cvSerde));

        outboxTypeStreamMap
                .get("type-unknown")
                .to(unknownOutboxTopic, Produced.with(keySerde, outboxDtoJsonSerde));

        return outboxStream;
    }

    @SneakyThrows
    private CvDto parseCvDto(OutboxDto outboxDto) {
        return outboxObjectMapper.readValue(outboxDto.payload(), CvDto.class);
    }
}
