package co.uk.zing.ledger.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
    private String entryId;
    private String accountId;
    private BigDecimal amount;
    private LocalDateTime entryTime;
}
