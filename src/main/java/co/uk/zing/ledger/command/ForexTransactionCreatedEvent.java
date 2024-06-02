package co.uk.zing.ledger.command;


import co.uk.zing.ledger.command.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForexTransactionCreatedEvent {
    private Transaction transaction;
    private String idempotencyKey;
}