package io.github.monalisamenezes.devbank.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String TRANSFER_NOTIFICATIONS_TOPIC = "transfer-notifications";

    @Bean
    public NewTopic transferNotificationsTopic() {
        return TopicBuilder.name(TRANSFER_NOTIFICATIONS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
