package co.uk.zing.ledger.command.service;


import co.uk.zing.ledger.command.ForexCommand;
import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;
import co.uk.zing.ledger.command.event.EventPublisher;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Entry;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.exception.InsufficientFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ForexCommandHandlerService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EventPublisher eventPublisher;

    public boolean isProcessed(String requestId) {
        return transactionRepository.findByRequestId(requestId).isPresent();
    }
    @Transactional
    public void handle(ForexCommand command) {
        if (isProcessed(command.getRequestId())) {
            return;
        }

        // Validate accounts and balance
        Account sourceAccount = accountRepository.findById(UUID.fromString(command.getSourceAccountId())).orElseThrow();
        Account destinationAccount = accountRepository.findById(UUID.fromString(command.getDestinationAccountId())).orElseThrow();

        if (sourceAccount.getAvailableBalance().compareTo(command.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in source account");
        }

        // Create entries
        Entry debitEntry = new Entry(UUID.randomUUID(), sourceAccount.getId(), command.getAmount(), LocalDateTime.now(), "Forex", "Debit");
        BigDecimal convertedAmount = command.getAmount().multiply(command.getExchangeRate());
        Entry creditEntry = new Entry(UUID.randomUUID(), destinationAccount.getId(), convertedAmount, LocalDateTime.now(), "Forex", "Credit");

        // Update account pending balances (not posted yet)
        sourceAccount.debit(command.getAmount());
        destinationAccount.credit(convertedAmount);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setType("Forex");
        transaction.setStatus("Pending"); //ToDo: Convert To Enum
        transaction.setEntries(Arrays.asList(debitEntry, creditEntry));
        transaction.setCreatedAt(LocalDateTime.now());

        // Persist transaction
        transactionRepository.save(transaction);

        // Publish events
        eventPublisher.publish(new ForexTransactionCreatedEvent(transaction));
        //ToDo: where is event listener
    }
}