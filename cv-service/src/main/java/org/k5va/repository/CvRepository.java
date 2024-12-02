package org.k5va.repository;

import org.k5va.model.CvDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.nio.channels.FileChannel;
import java.util.Optional;

public interface CvRepository extends MongoRepository<CvDocument, String> {
    Optional<CvDocument> findByEmployeeId(Long employeeId);
}
