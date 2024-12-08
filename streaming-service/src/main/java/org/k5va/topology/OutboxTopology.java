package org.k5va.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.k5va.dto.CvDto;
import org.k5va.dto.OutboxDto;
import org.k5va.dto.OutboxType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@Slf4j
public class OutboxTopology {

    @Bean
    public KStream<String, OutboxDto> outboxStream(StreamsBuilder streamsBuilder,
                                                   JsonSerde<OutboxDto> outboxDtoJsonSerde,
                                                   ObjectMapper outboxObjectMapper,
                                                   @Value("${app.kafka-topics.outbox-topic.name}") String outboxTopic,
                                                   @Value("${app.kafka-topics.unknown-outbox-topic.name}") String unknownOutboxTopic,
                                                   @Value("${app.kafka-topics.cv-topic.name}") String cvTopic) {

        JsonSerde<CvDto> cvSerde = new JsonSerde<>();
        Serde<String> keySerde = Serdes.String();

        KStream<String, OutboxDto> outboxStream = streamsBuilder
                .stream(outboxTopic, Consumed.with(keySerde, outboxDtoJsonSerde))
                .process(OutboxProcessor::new)
                .peek((key, value) -> log.info("Incoming outbox key {}, value {}", key, value));

        var outboxTypeStreamMap = outboxStream
                .split(Named.as("type-"))
                .branch((key, outboxDto) -> outboxDto.type() == OutboxType.CV,
                        Branched.as("cv"))
                .defaultBranch(Branched.as("unknown"));

        outboxTypeStreamMap
                .get("type-cv")
                .mapValues(value -> parseCvDto(outboxObjectMapper, value))
                .to(cvTopic, Produced.with(keySerde, cvSerde));

        outboxTypeStreamMap
                .get("type-unknown")
                .to(unknownOutboxTopic, Produced.with(keySerde, outboxDtoJsonSerde));

        return outboxStream;
    }

    @SneakyThrows
    private CvDto parseCvDto(ObjectMapper outboxObjectMapper, OutboxDto outboxDto) {
        return outboxObjectMapper.readValue(outboxDto.payload(), CvDto.class);
    }

    static class OutboxProcessor implements Processor<String, OutboxDto, String, OutboxDto> {

        private ProcessorContext<String, OutboxDto> context;

        @Override
        public void init(ProcessorContext<String, OutboxDto> context) {
            this.context = context;
        }

        @Override
        public void process(Record<String, OutboxDto> record) {
            record.headers().add("messageId", record.value().id().getBytes());
            context.forward(record);
        }
    }
}
