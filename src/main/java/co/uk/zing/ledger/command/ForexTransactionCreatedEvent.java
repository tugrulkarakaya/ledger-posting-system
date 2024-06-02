package co.uk.zing.ledger.command;


import co.uk.zing.ledger.command.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

@Data
@NoArgsConstructor
public class ForexTransactionCreatedEvent implements Deserializer<ForexTransactionCreatedEvent> {
    private Transaction transaction;
    private ObjectMapper objectMapper = new ObjectMapper();

    public ForexTransactionCreatedEvent(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public ForexTransactionCreatedEvent deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, ForexTransactionCreatedEvent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}