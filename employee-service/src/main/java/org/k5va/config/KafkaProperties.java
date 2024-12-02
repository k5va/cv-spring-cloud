package org.k5va.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.kafka.producer")
public class KafkaProperties {

    private String bootstrapServers;

    private String acks;

    private Integer retries;

    private Map<String, String> properties;
}
