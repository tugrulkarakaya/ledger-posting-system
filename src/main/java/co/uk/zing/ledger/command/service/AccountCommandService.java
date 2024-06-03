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

