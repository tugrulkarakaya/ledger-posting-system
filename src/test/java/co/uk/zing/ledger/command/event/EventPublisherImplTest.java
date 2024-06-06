package co.uk.zing.ledger.command.event;

import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventPublisherImplTest {

    @Mock
    private KafkaTemplate<String, ForexTransactionCreatedEvent> kafkaTemplate;

    @InjectMocks
    private EventPublisherImpl eventPublisher;

    @Captor
    private ArgumentCaptor<ForexTransactionCreatedEvent> eventCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPublish_Success() {
        ForexTransactionCreatedEvent event = new ForexTransactionCreatedEvent();

        // Arrange
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("topic", 0), 0, 0, System.currentTimeMillis(), 0, 0);
        SendResult<String, ForexTransactionCreatedEvent> sendResult = new SendResult<>(null, recordMetadata);
        CompletableFuture<SendResult<String, ForexTransactionCreatedEvent>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), any())).thenReturn(future);

        eventPublisher.publish(event);

        // Verify that the event was sent once
        verify(kafkaTemplate, times(1)).send(anyString(), eventCaptor.capture());
        assertEquals(event, eventCaptor.getValue());
    }

    @Test
    public void testPublish_HandleFailedEvent() {
        ForexTransactionCreatedEvent event = new ForexTransactionCreatedEvent();

        // Simulate failure on all attempts
        doThrow(new RuntimeException("Kafka send failed"))
                .when(kafkaTemplate).send(anyString(), any(ForexTransactionCreatedEvent.class));

        eventPublisher.publish(event);

        // Verify that the event was retried MAX_RETRIES times
        verify(kafkaTemplate, times(EventPublisherImpl.MAX_RETRIES)).send(anyString(), eventCaptor.capture());
        assertEquals(event, eventCaptor.getValue());

        // Here you can add verification for handleFailedEvent if it has any side effects
        // For example, if handleFailedEvent saves to a database or logs the event, you can verify those interactions
    }

    @Test
    public void testHandleFailedEvent() {
        ForexTransactionCreatedEvent event = new ForexTransactionCreatedEvent();

        // Directly invoke the handleFailedEvent method
        eventPublisher.handleFailedEvent(event);

        // Verify the expected behavior of handleFailedEvent
        // This depends on what handleFailedEvent is supposed to do
        // For example, you can check if a method was called or a field was updated
    }
}
