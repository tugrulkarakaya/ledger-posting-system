package co.uk.zing.ledger.command.model;

import co.uk.zing.ledger.exception.InsufficientFundsException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;


import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
//ToDo: @EntityListeners(AuditingEntityListener.class)  implement this for Audit records
public class Account {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String currency;
    private BigDecimal balance;
    private BigDecimal postedDebits;
    private BigDecimal postedCredits;
    private BigDecimal pendingDebits;
    private BigDecimal pendingCredits;

    //ToDo: do not forget checking ObjectOptimisticLockingFailureException  exception is thrown or not.
    @Version
    private Long version; // For optimistic locking

    public Account(UUID id, String currency) {
        this.id = id;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.postedDebits = BigDecimal.ZERO;
        this.postedCredits = BigDecimal.ZERO;
        this.pendingDebits = BigDecimal.ZERO;
        this.pendingCredits = BigDecimal.ZERO;
        this.version = 0L;
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

    public void debit(BigDecimal amount) throws InsufficientFundsException {
        if (getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for debit");
        }
        addPendingDebit(amount);
    }
    public void credit(BigDecimal amount) {
        addPendingCredit(amount);
    }
    private void addPendingDebit(BigDecimal amount) {
        this.pendingDebits = this.pendingDebits.add(amount);
    }

    private void addPendingCredit(BigDecimal amount) {
        this.pendingCredits = this.pendingCredits.add(amount);
    }

    @Transactional
    public void postPendingEntries() {
        this.postedDebits = this.postedDebits.add(this.pendingDebits);
        this.postedCredits = this.postedCredits.add(this.pendingCredits);
        this.pendingDebits = BigDecimal.ZERO;
        this.pendingCredits = BigDecimal.ZERO;
    }

    public void clearPendingEntries() {
        this.pendingDebits = BigDecimal.ZERO;
        this.pendingCredits = BigDecimal.ZERO;
    }

    public void adjustPostedDebits(BigDecimal amount) {
        this.postedDebits = this.postedDebits.add(amount);
    }

    public void adjustPostedCredits(BigDecimal amount) {
        this.postedCredits = this.postedCredits.add(amount);
    }

    public void incrementVersion() {
        this.version++;
    }

}
