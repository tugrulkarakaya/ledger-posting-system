package co.uk.zing.ledger.command.controller;


import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.service.AccountCommandService;
import co.uk.zing.ledger.command.service.TransactionCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/command")
public class CommandController {

    private final AccountCommandService accountCommandService;
    private final TransactionCommandService transactionCommandService;

    @Autowired
    public CommandController(AccountCommandService accountCommandService, TransactionCommandService transactionCommandService) {
        this.accountCommandService = accountCommandService;
        this.transactionCommandService = transactionCommandService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountCommandService.createAccount(account.getCurrency());
        return ResponseEntity.ok(createdAccount);
    }
    @PostMapping("/accounts/name")
    public Account createAccount(@RequestBody String name) {
        return accountCommandService.createAccount(name);
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionCommandService.createTransaction(transaction);
        return ResponseEntity.ok(createdTransaction);
    }

    // Other command endpoints
}
