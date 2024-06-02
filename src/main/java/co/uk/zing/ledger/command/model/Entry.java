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
public class Entry {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private LocalDateTime entryTime;
    private String type;
    private String direction; // Debit or Credit
}
