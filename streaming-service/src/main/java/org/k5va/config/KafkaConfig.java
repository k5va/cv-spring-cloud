package org.k5va.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.k5va.dto.OutboxDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class KafkaConfig {
    @Value("${app.kafka-topics.unknown-outbox-topic.name}")
    private String unknownOutboxTopic;

    @Bean
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig(KafkaProperties kafkaProperties) {

        var props = new HashMap<String, Object>(kafkaProperties.getStreams().getProperties());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming-service");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    JsonSerde<OutboxDto> outboxDtoJsonSerde() {
        JsonSerde<OutboxDto> outboxSerde = new JsonSerde<>();
        outboxSerde.deserializer().configure(
                Map.of(
                        JsonDeserializer.TRUSTED_PACKAGES, "org.k5va.*",
                        JsonDeserializer.VALUE_DEFAULT_TYPE, OutboxDto.class
                ),
                false);

        return outboxSerde;
    }

    @Bean
    NewTopic unknownOutBoxTopic() {
        return TopicBuilder
                .name(unknownOutboxTopic)
                .build();
    }

    @Bean
    ObjectMapper outboxObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
