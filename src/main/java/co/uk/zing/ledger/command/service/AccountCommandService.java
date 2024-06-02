package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Entry;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.exception.AccountNotFoundException;
import co.uk.zing.ledger.exception.InsufficientFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
public class AccountCommandService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountCommandService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void createForexTransaction(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) throws InsufficientFundsException {
        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new AccountNotFoundException(sourceAccountId.toString()));

        Account destinationAccount = accountRepository.findById(destinationAccountId)
                .orElseThrow(() -> new AccountNotFoundException(destinationAccountId.toString()));

        if (sourceAccount.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + sourceAccountId);
        }

        // Create entries
        Entry debitEntry = new Entry(UUID.randomUUID(), sourceAccountId, amount,  LocalDateTime.now(), "Debit", "forex");
        Entry creditEntry = new Entry(UUID.randomUUID(), destinationAccountId, amount,  LocalDateTime.now() ,"Credit", "forex");

        // Create transaction
        Transaction transaction = new Transaction(UUID.randomUUID(), "Forex", LocalDateTime.now(), null, "Completed", Arrays.asList(debitEntry, creditEntry));

        // Save transaction
        transactionRepository.save(transaction);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }

    public Account createAccount(String currency) {
        Account account = new Account();
        account.setCurrency(currency);
        return accountRepository.save(account);
    }
}

