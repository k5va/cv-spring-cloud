package org.k5va.processor;

import org.k5va.generated.tables.records.OutboxRecord;

public interface OutboxRecordProcessor {
    void process(OutboxRecord outboxRecord);
}
