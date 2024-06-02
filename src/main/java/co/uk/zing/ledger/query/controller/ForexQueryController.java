package co.uk.zing.ledger.query.controller;


import co.uk.zing.ledger.command.ForexCommandHandler;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.query.service.AccountQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forexquery")
public class ForexQueryController {

    @Autowired
    private ForexCommandHandler forexCommandHandler;

    @Autowired
    private AccountQueryService accountQueryService;


    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountId) {
        Account account = accountQueryService.getAccount(accountId);
        return ResponseEntity.ok(account);
    }
//
//    @GetMapping("/accounts/{accountId}/transactions")
//    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String accountId) {
//        List<Transaction> transactions = accountQueryService.getTransactions(accountId);
//        return ResponseEntity.ok(transactions);
//    }
}
