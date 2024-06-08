package co.uk.zing.ledger.command.controller;


import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.service.AccountCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/command")
public class CommandController {

    private final AccountCommandService accountCommandService;

    @Autowired
    public CommandController(AccountCommandService accountCommandService) {
        this.accountCommandService = accountCommandService;
    }


    @PostMapping("/accounts/name")
    public Account createAccount(@RequestBody String name) {
        return accountCommandService.createAccount(name);
    }

    @PostMapping("/{accountId}/increasePostedDebits")
    public ResponseEntity<Void> increasePostedDebits(
            @PathVariable UUID accountId,
            @RequestParam BigDecimal amount) {

        accountCommandService.increasePostedDebits(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/increasePostedCredits")
    public ResponseEntity<Void> increasePostedCredits(
            @PathVariable UUID accountId,
            @RequestParam BigDecimal amount) {

        accountCommandService.increasePostedCredits(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountCommandService.createAccount(account.getCurrency());
        return ResponseEntity.ok(createdAccount);
    }
}
