package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Entry;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.exception.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ForexEventConsumerService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ForexEventConsumerService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "forex-transactions", groupId = "ledger-posting-system-forex")
    public void consume(ForexTransactionCreatedEvent event) {
        try{
            processForexTransaction(event);
        } catch (Exception ex){
            log.error("Forex Transaction could not be processed", ex);
            throw  ex;
        }
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void processForexTransaction(ForexTransactionCreatedEvent event) {
        try {
            Transaction transaction = transactionRepository.findByIdWithEntries(event.getTransactionId()).orElseThrow(AccountNotFoundException::new);
            List<Entry> newEntries = new ArrayList<>();
            for (Entry entry : transaction.getEntries()) {
                Account account = accountRepository.findById(entry.getAccountId()).orElseThrow();
                Entry postedEntry = new Entry(entry.getAccountId(), entry.getAmount(), LocalDateTime.now(), "Forex", "Credit", "Posted");

                if ("Debit".equals(entry.getDirection())) {
                    account.adjustPostedDebits(entry.getAmount());
                    account.adjustPendingDebits(entry.getAmount().negate());
                } else if ("Credit".equals(entry.getDirection())) {
                    account.adjustPostedCredits(entry.getAmount());
                    account.adjustPendingCredits(entry.getAmount().negate());
                }
                entry.setDiscardedAt(LocalDateTime.now());
                postedEntry.setTransaction(transaction);
                newEntries.add(postedEntry);
                accountRepository.save(account);

            }
            transaction.setUpdatedAt(LocalDateTime.now());
            transaction.getEntries().addAll(newEntries);
            transactionRepository.save(transaction);
        } catch (OptimisticLockingFailureException ex) {
            log.error("Optimistic Locking error. account has updated by some other process");
            throw ex;
        }
    }
}