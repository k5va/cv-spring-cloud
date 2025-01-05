package org.k5va.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.k5va.dto.CvDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    Map<String, Object> producerConfigs() {
        var config = new HashMap<String, Object>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getAcks());
        config.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getRetries());
        config.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProperties.getProperties().get("linger.ms"));
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProperties.getProperties().get("request.timeout.ms"));
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, kafkaProperties.getProperties().get("delivery.timeout.ms"));
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafkaProperties.getProperties().get("enable.idempotence"));

        return config;
    }

    @Bean
    ProducerFactory<String, CvDto> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, CvDto> kafkaTemplate(ProducerFactory<String, CvDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic cvTopic(CvTopicProperties cvTopicProperties) {
        return TopicBuilder
                .name(cvTopicProperties.getName())
                .partitions(cvTopicProperties.getPartitions())
                .replicas(cvTopicProperties.getReplicas())
                .configs(Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, cvTopicProperties.getMinInSyncReplicas()))
                .build();
    }
}
