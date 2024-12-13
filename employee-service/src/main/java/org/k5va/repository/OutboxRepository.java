package org.k5va.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.k5va.generated.tables.records.OutboxRecord;
import org.springframework.stereotype.Repository;

import static org.k5va.generated.tables.Outbox.OUTBOX;

@Repository
@RequiredArgsConstructor
public class OutboxRepository {
    private final DSLContext dslContext;

    public void create(OutboxRecord outboxRecord) {
        dslContext
                .insertInto(OUTBOX, OUTBOX.PAYLOAD, OUTBOX.TYPE)
                .values(outboxRecord.getPayload(), outboxRecord.getType())
                .execute();
    }
}
