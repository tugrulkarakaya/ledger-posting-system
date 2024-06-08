package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Entry;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.EntryRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.exception.AccountNotFoundException;
import co.uk.zing.ledger.exception.MissingAccountNameException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AccountCommandService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EntryRepository entryRepository;

    @Autowired
    public AccountCommandService(AccountRepository accountRepository, TransactionRepository transactionRepository, EntryRepository entryRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.entryRepository = entryRepository;
    }

    public Account createAccount(String currency) {
        if(Strings.isEmpty(currency) || currency.equals("{}")){
            throw new MissingAccountNameException("Currency is required");
        }
        Account account = new Account();
        account.setCurrency(currency);
        return accountRepository.save(account);
    }

    //This service is just for test purposes to add Money into system.
    @Transactional
    public void increasePostedDebits(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        try {
            Transaction transaction = new Transaction();
            transaction.setType("Test Transaction");
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setRequestId(UUID.randomUUID().toString());

            Entry postedEntry = new Entry(accountId, amount, LocalDateTime.now(), "Test", "Debit", "Posted");
            account.adjustPostedCredits(amount);
            postedEntry.setTransaction(transaction);
            accountRepository.save(account);
            transaction.setEntries(List.of(postedEntry));
            transactionRepository.save(transaction);
        } catch (OptimisticLockingFailureException ex) {
            log.error("Optimistic Locking error. account has updated by some other process");
            throw ex;
        }
    }

    @Transactional
    public void increasePostedCredits(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        try {
            Transaction transaction = new Transaction();
            transaction.setType("Test Transaction");
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setRequestId(UUID.randomUUID().toString());

            Entry postedEntry = new Entry(accountId, amount, LocalDateTime.now(), "Test", "Credit", "Posted");
            account.adjustPostedCredits(amount);
            postedEntry.setTransaction(transaction);
            accountRepository.save(account);
            transaction.setEntries(List.of(postedEntry));
            transactionRepository.save(transaction);
        } catch (OptimisticLockingFailureException ex) {
            log.error("Optimistic Locking error. account has updated by some other process");
            throw ex;
        }
    }

    public BigDecimal getPostedDebits(UUID accountId) {
        BigDecimal postedDebit =  entryRepository.findPostedDebits(accountId);
        postedDebit = Objects.isNull(postedDebit)?BigDecimal.ZERO:postedDebit;
        return postedDebit;
    }

    public BigDecimal getPostedCredits(UUID accountId) {
        BigDecimal postedCredit =  entryRepository.findPostedCredits(accountId);
        postedCredit = postedCredit == null? BigDecimal.ZERO: postedCredit;
        return postedCredit;
    }

    public BigDecimal getPendingDebits(UUID accountId) {
        BigDecimal postedDebits = getPostedDebits(accountId);
        BigDecimal pendingDebits = entryRepository.findPendingDebits(accountId);
        pendingDebits = Objects.isNull(pendingDebits)? BigDecimal.ZERO:pendingDebits;
        return postedDebits.add(pendingDebits);
    }

    public BigDecimal getPendingCredits(UUID accountId) {
        BigDecimal postedCredits = getPostedCredits(accountId);
        BigDecimal pendingCredits = entryRepository.findPendingCredits(accountId);
        pendingCredits = Objects.isNull(pendingCredits)? BigDecimal.ZERO: pendingCredits;
        return postedCredits.add(pendingCredits);
    }

    public BigDecimal getAvailableBalance(UUID accountId) {
        BigDecimal postedCredits = getPostedCredits(accountId);
        BigDecimal pendingDebits =getPendingDebits(accountId);
        return postedCredits.subtract(pendingDebits);
    }

}

