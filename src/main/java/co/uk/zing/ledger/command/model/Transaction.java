package co.uk.zing.ledger.command.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String transactionId;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private LocalDateTime transactionTime;
}