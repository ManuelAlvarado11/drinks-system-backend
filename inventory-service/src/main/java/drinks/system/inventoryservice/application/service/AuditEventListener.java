package drinks.system.inventoryservice.application.service;

import drinks.system.common.audit.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j @Component
public class AuditEventListener {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditEvent(AuditEvent event) {
        log.info("Audit [{}] {}/{} id={} - {}",
                event.action(), event.module(), event.entityName(), event.entityId(), event.description());
    }
}
