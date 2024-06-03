package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Entry;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.exception.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void processForexTransaction(ForexTransactionCreatedEvent event) {
        Transaction transaction = transactionRepository.findByIdWithEntries(event.getTransactionId()).orElseThrow(AccountNotFoundException::new);
        for (Entry entry : transaction.getEntries()) {
            Account account = accountRepository.findById(entry.getAccountId()).orElseThrow();
            if ("Debit".equals(entry.getDirection())) {
                account.adjustPostedDebits(entry.getAmount());
                account.adjustPendingDebits(entry.getAmount().negate());
            } else if ("Credit".equals(entry.getDirection())) {
                account.adjustPostedCredits(entry.getAmount());
                account.adjustPendingCredits(entry.getAmount().negate());
            }
            accountRepository.save(account);
        }
        transaction.setStatus("Completed");
        transactionRepository.save(transaction);
    }
}