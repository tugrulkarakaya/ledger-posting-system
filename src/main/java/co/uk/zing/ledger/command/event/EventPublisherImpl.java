package co.uk.zing.ledger.command.event;

import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherImpl implements EventPublisher {

    @Autowired
    private KafkaTemplate<String, ForexTransactionCreatedEvent> kafkaTemplate;

    @Override
    public void publish(ForexTransactionCreatedEvent event) {
        kafkaTemplate.send("forex-transactions", event);
    }
}