package co.uk.zing.ledger.command.repository;

import co.uk.zing.ledger.command.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {


    Optional<Transaction> findByRequestId(String requestId);


    @Query("SELECT t FROM Transaction t JOIN t.entries e WHERE e.accountId = :accountId AND t.createdAt BETWEEN :startDate AND :endDate AND e.discardedAt IS NULL")
    List<Transaction> findTransactionsByAccountIdAndDateRange(
            @Param("accountId") UUID accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.entries WHERE t.id = :id")
    Optional<Transaction> findByIdWithEntries(UUID id);
}
