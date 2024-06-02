package co.uk.zing.ledger.command.event;

import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;

public interface EventPublisher {
    void publish(ForexTransactionCreatedEvent event);
}
