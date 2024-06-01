package co.uk.zing.ledger.command.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
public class Account {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String accountId;
    private BigDecimal balance;
}
