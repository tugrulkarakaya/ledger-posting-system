package co.uk.zing.ledger.command;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForexTransactionCreatedEvent  {
    private UUID transactionId;
}