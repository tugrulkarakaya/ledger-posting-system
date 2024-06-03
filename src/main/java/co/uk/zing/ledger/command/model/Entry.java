package co.uk.zing.ledger.command.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Entry {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private LocalDateTime entryTime;
    private String type;
    private String direction; // Debit or Credit

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    public Entry(UUID accountId, BigDecimal amount, LocalDateTime entryTime, String type, String direction) {
        this.accountId = accountId;
        this.amount = amount;
        this.entryTime = entryTime;
        this.type = type;
        this.direction = direction;
    }
}
