package org.k5va;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.testing.testcontainers.ConnectorConfiguration;
import io.debezium.testing.testcontainers.DebeziumContainer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.k5va.config.DisableSecurityConfig;
import org.k5va.generated.tables.records.OutboxRecord;
import org.k5va.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {DisableSecurityConfig.class})
@SpringBootTest()
public class CdcTest {
    private static final Network network = Network.newNetwork();

    private static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.4"))
            .withNetwork(network);

    public static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(
                    DockerImageName
                            .parse("debezium/postgres:17-alpine")
                            .asCompatibleSubstituteFor("postgres"))
                    .withNetwork(network)
                    .withNetworkAliases("employees-db");

    public static DebeziumContainer debeziumContainer =
            new DebeziumContainer(DockerImageName.parse("debezium/connect:3.0.0.Final"))
                    .withNetwork(network)
                    .withKafka(kafkaContainer);

    static {
        Startables
                .deepStart(Stream.of(kafkaContainer, postgresContainer, debeziumContainer))
                .join();
        debeziumContainer.registerConnector("employees-dbz-connector",
                ConnectorConfiguration
                        .create()
                        .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                        .with("database.hostname", postgresContainer.getNetworkAliases().get(0))
                        .with("database.port", postgresContainer.getExposedPorts().get(0))
                        .with("database.user", postgresContainer.getUsername())
                        .with("database.password", postgresContainer.getPassword())
                        .with("database.dbname", postgresContainer.getDatabaseName())
                        .with("plugin.name", "pgoutput")
                        .with("database.server.name", "source")
                        .with("key.converter.schemas.enable", false)
                        .with("value.converter.schemas.enable", false)
                        .with("transforms", "unwrap")
                        .with("transforms.unwrap.type", "io.debezium.transforms.ExtractNewRecordState")
                        .with("value.converter", "org.apache.kafka.connect.json.JsonConverter")
                        .with("key.converter", "org.apache.kafka.connect.json.JsonConverter")
                        .with("table.include.list", "public.outbox")
                        .with("slot.name", "dbz_employees_slot")
                        .with("topic.prefix", "cv-app")
        );
    }

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private KafkaMessageListenerContainer<String, String> container;

    private BlockingQueue<ConsumerRecord<String, String>> records;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    void setUp() {
        var consumerFactory = new DefaultKafkaConsumerFactory<String, String>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                        ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class,
                        ConsumerConfig.GROUP_ID_CONFIG, "cv-app"
                )
        );

        var containerProperties = new ContainerProperties("cv-app.public.outbox");
        // Message listener container (consumer)
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        // queue for storing consumed messages
        records = new LinkedBlockingQueue<>();
        // setups a message listener to add messages to the queue
        container.setupMessageListener((MessageListener<String, String>) records::add);
        container.start();
        // waits until the container has a proper assignment to all 3 partitions
//        ContainerTestUtils.waitForAssignment(container, kafkaContainer.getPartitionsPerTopic());
    }

    @Test
    public void shouldAddMessageToKafkaOnOutboxRecordCreation() throws InterruptedException, JsonProcessingException {
        // given
        OutboxRecord outboxRecord = new OutboxRecord();
        outboxRecord.setPayload("test_payload");

        // when
        outboxRepository.create(outboxRecord);

        // then
        var record = records.poll(10_000, TimeUnit.MILLISECONDS);
        assertNotNull(record);
        assertNotNull(record.key());
        assertNotNull(record.value());
        assertEquals(outboxRecord.getPayload(), objectMapper.readTree(record.value()).get("payload").asText());
    }
}
