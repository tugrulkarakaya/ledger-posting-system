package co.uk.zing.ledger.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForexCommand {
    private String requestId;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private BigDecimal exchangeRate;

    // Getters and Setters
}