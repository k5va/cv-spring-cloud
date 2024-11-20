package org.k5va.repository;

import org.k5va.model.CvDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CvRepository extends MongoRepository<CvDocument, Long> {
}
