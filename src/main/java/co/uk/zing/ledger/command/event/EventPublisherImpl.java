package co.uk.zing.ledger.command.event;

import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherImpl implements EventPublisher {


    private final KafkaTemplate<String, ForexTransactionCreatedEvent> kafkaTemplate;

    static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 1000L; // 1 second

    public EventPublisherImpl(KafkaTemplate<String, ForexTransactionCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void publish(ForexTransactionCreatedEvent event) {
        int attempt = 0;
        boolean success = false;

        while (attempt < MAX_RETRIES && !success) {
            try {
                publishEventToBroker(event);
                success = true;
            } catch (Exception e) {
                attempt++;
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    handleFailedEvent(event);
                }
            }
        }
    }

    private void publishEventToBroker(ForexTransactionCreatedEvent event) {
        kafkaTemplate.send("forex-transactions", event);
    }

    void handleFailedEvent(ForexTransactionCreatedEvent event) {
        //ToDo: save transaction as failed.
        //event.getTransaction().setStatus("Failed");
    }
}