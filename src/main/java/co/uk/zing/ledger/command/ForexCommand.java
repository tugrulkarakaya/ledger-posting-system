package co.uk.zing.ledger.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForexCommand {
    private String requestId;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    @Builder.Default
    private boolean isSynchronize = false;
    private BigDecimal exchangeRate;

    // Getters and Setters
}