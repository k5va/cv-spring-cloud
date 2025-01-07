package org.k5va.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k5va.service.OutboxService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OutboxScheduler {
    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 10_000)
    public void run() {
        log.info("Outbox processing started");
        outboxService.processAll();
        log.info("Outbox processing finished");
    }
}
