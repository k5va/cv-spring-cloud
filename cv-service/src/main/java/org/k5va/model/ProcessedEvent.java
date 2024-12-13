package org.k5va.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("processed_events")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProcessedEvent {
    @Id
    private String id;

    private String messageId;

    private String cvId;
}
