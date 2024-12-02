package org.k5va.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("app.kafka-topics.cv-topic")
public class CvTopicProperties {
    private String name;
    private Integer partitions;
    private Integer replicas;
    private String minInSyncReplicas;
}
