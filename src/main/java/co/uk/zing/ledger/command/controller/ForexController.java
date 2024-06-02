package co.uk.zing.ledger.command.controller;


import co.uk.zing.ledger.command.ForexCommand;
import co.uk.zing.ledger.command.ForexCommandHandler;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.query.service.AccountQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forex")
public class ForexController {

    @Autowired
    private ForexCommandHandler forexCommandHandler;

    @Autowired
    private AccountQueryService accountQueryService;

    @PostMapping
    public ResponseEntity<Void> executeForexOperation(@RequestBody ForexCommand command) {
        forexCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

