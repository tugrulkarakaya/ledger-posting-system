package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Entry;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ForexEventConsumerService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ForexEventConsumerService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "forex-transactions", groupId = "ledger-posting-system-forex")
    public void consume(ForexTransactionCreatedEvent event) {
        processForexTransaction(event);
    }

    @Transactional
    public void processForexTransaction(ForexTransactionCreatedEvent event) {
        Transaction transaction = event.getTransaction();
        for (Entry entry : transaction.getEntries()) {
            Account account = accountRepository.findById(entry.getAccountId()).orElseThrow();
            if ("Debit".equals(entry.getDirection())) {
                account.adjustPostedDebits(entry.getAmount());
            } else if ("Credit".equals(entry.getDirection())) {
                account.adjustPostedCredits(entry.getAmount());
            }
            accountRepository.save(account);
        }
        transaction.setStatus("Completed");
        transactionRepository.save(transaction);
    }
}