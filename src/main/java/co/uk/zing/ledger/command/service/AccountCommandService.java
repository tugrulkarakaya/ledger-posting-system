package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

//    @Transactional
//    public void createForexTransaction(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) throws InsufficientFundsException {
//        Account sourceAccount = accountRepository.findById(sourceAccountId)
//                .orElseThrow(() -> new AccountNotFoundException(sourceAccountId.toString()));
//
//        Account destinationAccount = accountRepository.findById(destinationAccountId)
//                .orElseThrow(() -> new AccountNotFoundException(destinationAccountId.toString()));
//
//        if (sourceAccount.getAvailableBalance().compareTo(amount) < 0) {
//            throw new InsufficientFundsException("Insufficient funds in account: " + sourceAccountId);
//        }
//
//        // Create entries
//        Entry debitEntry = new Entry( sourceAccountId, amount,  LocalDateTime.now(), "Debit", "forex");
//        Entry creditEntry = new Entry( destinationAccountId, amount,  LocalDateTime.now() ,"Credit", "forex");
//
//        // Create transaction
//        Transaction transaction = new Transaction(UUID.randomUUID(), "Forex", "Completed", Arrays.asList(debitEntry, creditEntry), UUID.randomUUID().toString());
//
//        // Associate each entry with the transaction
//        debitEntry.setTransaction(transaction);
//        creditEntry.setTransaction(transaction);
//
//        // Save transaction
//        transactionRepository.save(transaction);
//
//        accountRepository.save(sourceAccount);
//        accountRepository.save(destinationAccount);
//    }

//    @EventListener
//    public void onEvent(Event event) {
//        // Process transaction event
//        Transaction transaction = event.getTransaction();
//        List<Entry> entries = transaction.getEntries();
//
//        for (Entry entry : entries) {
//            Account account = accountRepository.findById(entry.getAccountId())
//                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
//
//            if (entry.getType() == EntryType.DEBIT) {
//                account.addPendingDebit(entry.getAmount());
//            } else {
//                account.addPendingCredit(entry.getAmount());
//            }
//
//            accountRepository.save(account);
//        }
//
//        // Update transaction status
//        transaction.setStatus(TransactionStatus.POSTED);
//        transaction.setUpdatedAt(LocalDateTime.now());
//        transactionRepository.save(transaction);
//    }

    public Account createAccount(String currency) {
        Account account = new Account();
        account.setCurrency(currency);
        return accountRepository.save(account);
    }

    @Transactional
    public void increasePostedDebits(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        account.setPostedDebits(account.getPostedDebits().add(amount));
        accountRepository.save(account);
    }

    @Transactional
    public void increasePostedCredits(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        account.setPostedCredits(account.getPostedCredits().add(amount));
        accountRepository.save(account);
    }
}

