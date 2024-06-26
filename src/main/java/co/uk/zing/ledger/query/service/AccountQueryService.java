package co.uk.zing.ledger.query.service;

import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountQueryService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Account getAccount(String accountId) {
        return accountRepository.findById(UUID.fromString(accountId)).orElseThrow(AccountNotFoundException::new);
    }


    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

//
//    public List<Transaction> getTransactions(String accountId) {
//        return transactionRepository.findById(UUID.fromString(accountId));
//    }
}