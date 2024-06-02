package co.uk.zing.ledger.query.controller;


import co.uk.zing.ledger.command.service.ForexCommandHandlerService;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.query.service.AccountQueryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/forexquery")
public class ForexQueryController {

    @Autowired
    private ForexCommandHandlerService forexCommandHandler;

    @Autowired
    private AccountQueryService accountQueryService;


    @Operation(summary = "Get account details by account ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountId) {
        Account account = accountQueryService.getAccount(accountId);
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Get all accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of accounts"),
            @ApiResponse(responseCode = "404", description = "Accounts not found")
    })
    @GetMapping("/accounts/all")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountQueryService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
}
