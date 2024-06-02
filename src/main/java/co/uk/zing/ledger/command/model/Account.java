package co.uk.zing.ledger.command.model;

import co.uk.zing.ledger.exception.InsufficientFundsException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;


import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String currency;
    private BigDecimal postedDebits;
    private BigDecimal postedCredits;
    private BigDecimal pendingDebits;
    private BigDecimal pendingCredits;
    private int version; // For optimistic locking

    public Account(UUID id, String currency) {
        this.id = id;
        this.currency = currency;
        this.postedDebits = BigDecimal.ZERO;
        this.postedCredits = BigDecimal.ZERO;
        this.pendingDebits = BigDecimal.ZERO;
        this.pendingCredits = BigDecimal.ZERO;
        this.version = 0;
    }


    public BigDecimal getPostedBalance() {
        return postedCredits.subtract(postedDebits);
    }

    public BigDecimal getPendingBalance() {
        return pendingCredits.subtract(pendingDebits);
    }

    public BigDecimal getAvailableBalance() {
        return getPostedBalance().subtract(pendingDebits);
    }

    public void addPendingDebit(BigDecimal amount) {
        this.pendingDebits = this.pendingDebits.add(amount);
    }

    public void addPendingCredit(BigDecimal amount) {
        this.pendingCredits = this.pendingCredits.add(amount);
    }

    public void postPendingEntries() {
        this.postedDebits = this.postedDebits.add(this.pendingDebits);
        this.postedCredits = this.postedCredits.add(this.pendingCredits);
        this.pendingDebits = BigDecimal.ZERO;
        this.pendingCredits = BigDecimal.ZERO;
    }

    public void incrementVersion() {
        this.version++;
    }
}
