package co.uk.zing.ledger.command.repository;

import co.uk.zing.ledger.command.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, UUID> {

    @Query("SELECT SUM(e.amount) FROM Entry e WHERE e.accountId = :accountId AND e.direction = 'Debit' AND e.status = 'Posted'")
    BigDecimal findPostedDebits(@Param("accountId") UUID accountId);

    @Query("SELECT SUM(e.amount) FROM Entry e WHERE e.accountId = :accountId AND e.direction = 'Credit' AND e.status = 'Posted'")
    BigDecimal findPostedCredits(@Param("accountId") UUID accountId);

    @Query("SELECT SUM(e.amount) FROM Entry e WHERE e.accountId = :accountId AND e.direction = 'Debit' AND e.status = 'Pending' AND e.discardedAt IS NULL")
    BigDecimal findPendingDebits(@Param("accountId") UUID accountId);

    @Query("SELECT SUM(e.amount) FROM Entry e WHERE e.accountId = :accountId AND e.direction = 'Credit' AND e.status = 'Pending' AND e.discardedAt IS NULL")
    BigDecimal findPendingCredits(@Param("accountId") UUID accountId);

}
