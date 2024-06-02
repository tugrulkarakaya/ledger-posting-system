package co.uk.zing.ledger.command.model;

import co.uk.zing.ledger.exception.InsufficientFundsException;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private UUID id;
    private String currency;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal postedDebits = BigDecimal.ZERO;
    private BigDecimal postedCredits = BigDecimal.ZERO;
    private BigDecimal pendingDebits = BigDecimal.ZERO;
    private BigDecimal pendingCredits = BigDecimal.ZERO;

    //ToDo: do not forget checking ObjectOptimisticLockingFailureException  exception is thrown or not.
    @Version
    private Long version; // For optimistic locking

    public Account(UUID id, String currency) {
        this.id = id;
        this.currency = currency;
        this.version = 0L;
        this.balance = BigDecimal.ZERO;
        this.postedDebits = BigDecimal.ZERO;
        this.postedCredits = BigDecimal.ZERO;
        this.pendingDebits = BigDecimal.ZERO;
        this.pendingCredits = BigDecimal.ZERO;
    }

    public BigDecimal getPostedCredits() {
        return postedCredits != null ? postedCredits : BigDecimal.ZERO;
    }

    public BigDecimal getPostedDebits() {
        return postedDebits != null ? postedDebits : BigDecimal.ZERO;
    }

    public BigDecimal getPendingDebits() {
        return pendingDebits != null ? pendingDebits : BigDecimal.ZERO;
    }

    public BigDecimal getPendingCredits() {
        return pendingCredits != null ? pendingCredits : BigDecimal.ZERO;
    }
    public BigDecimal getPostedBalance() {
        return getPostedCredits().subtract(getPostedDebits());
    }

    public BigDecimal getPendingBalance() {
        return getPendingCredits().subtract(getPendingDebits());
    }

    public BigDecimal getAvailableBalance() {
        return getPostedBalance().subtract(getPendingDebits());
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
        this.pendingDebits = this.getPendingDebits().add(amount);
    }

    private void addPendingCredit(BigDecimal amount) {
        this.pendingCredits = this.getPendingCredits().add(amount);
    }

    @Transactional
    public void postPendingEntries() {
        this.postedDebits = getPostedDebits().add(getPendingDebits());
        this.postedCredits = this.getPostedCredits().add(getPendingCredits());
        this.pendingDebits = BigDecimal.ZERO;
        this.pendingCredits = BigDecimal.ZERO;
    }

    public void clearPendingEntries() {
        this.pendingDebits = BigDecimal.ZERO;
        this.pendingCredits = BigDecimal.ZERO;
    }

    public void adjustPostedDebits(BigDecimal amount) {
        this.postedDebits = getPostedDebits().add(amount);
    }

    public void adjustPostedCredits(BigDecimal amount) {
        this.postedCredits = getPostedCredits().add(amount);
    }

    public void incrementVersion() {
        this.version++;
    }

}
