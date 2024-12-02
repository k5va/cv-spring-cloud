package org.k5va.repository.changelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.extern.slf4j.Slf4j;
import org.k5va.model.CvDocument;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.stream.IntStream;

@ChangeUnit(id = "seed-cvs", order = "001", author = "k5va")
@Slf4j
public class SeedCvsChangeLog {
    private static final int CVS_COUNT = 100;

    @Execution
    public void seedTweets(MongoTemplate mongoTemplate) {
        log.info("Seeding cvs...");

        List<CvDocument> cvs = IntStream.range(0, CVS_COUNT)
                .mapToObj(i -> CvDocument.builder()
                        .education("MIT")
                        .description("i am top programmer")
                        .workExperience("I have been working for 5 years")
                        .skills(List.of("Java", "Python"))
                        .languages(List.of("Russian", "English"))
                        .certificates(List.of("Certificate 1", "Certificate 2"))
                        .linkedId("linkedId")
                        .isOpenToWork(true)
                        .build())
                .toList();

        mongoTemplate.insertAll(cvs);
    }

    @RollbackExecution
    public void removeTweets(MongoTemplate mongoTemplate) {
        log.info("Removing cvs...");
        mongoTemplate.remove(CvDocument.class);
    }
}
