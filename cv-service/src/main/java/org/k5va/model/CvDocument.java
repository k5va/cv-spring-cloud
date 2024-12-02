package org.k5va.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("CV")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CvDocument {
    @Id
    private String id;

    private String education;

    private String description;

    private String workExperience;

    private List<String> skills;

    private List<String> languages;

    private List<String> certificates;

    private String linkedId;

    private boolean isOpenToWork;

    private Long employeeId;
}
