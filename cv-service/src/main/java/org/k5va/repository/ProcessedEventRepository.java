package org.k5va.repository;

import org.k5va.model.ProcessedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ProcessedEventRepository extends MongoRepository<ProcessedEvent, String> {
    @Query("{messageId: messageId}")
    Optional<ProcessedEvent> findByMessageId(String messageId);
}
