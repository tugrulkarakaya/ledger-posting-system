package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountCommandService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    // Other account related command methods
}
