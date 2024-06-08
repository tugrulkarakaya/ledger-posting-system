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

    @Autowired
    private AccountCommandService accountCommandService;

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

        if (accountCommandService.getAvailableBalance(sourceAccount.getId()).compareTo(command.getAmount())<0) {
            throw new InsufficientFundsException("Insufficient funds in source account");
        }

        BigDecimal convertedAmount = command.getAmount().multiply(command.getExchangeRate());

        // Update account pending balances (not posted yet)
        sourceAccount.debit(command.getAmount());
        destinationAccount.credit(convertedAmount);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Create entries
        Entry debitEntry = new Entry( sourceAccount.getId(), command.getAmount(), LocalDateTime.now(), "Forex", "Debit","Pending");
        Entry creditEntry = new Entry( destinationAccount.getId(), convertedAmount, LocalDateTime.now(), "Forex", "Credit", "Pending");


        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setType("Forex");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setRequestId(command.getRequestId());

        // Set the transaction for each entry
        debitEntry.setTransaction(transaction);
        creditEntry.setTransaction(transaction);

        // add entries to the transaction
        transaction.setEntries(Arrays.asList(debitEntry, creditEntry));

        // Persist transaction
        transaction =  transactionRepository.save(transaction);

        // Publish events
        eventPublisher.publish(new ForexTransactionCreatedEvent(transaction.getId()));
        //ToDo: where is event listener
    }
}