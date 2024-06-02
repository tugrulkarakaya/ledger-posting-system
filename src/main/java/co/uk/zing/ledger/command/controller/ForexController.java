package co.uk.zing.ledger.command.controller;


import co.uk.zing.ledger.command.ForexCommand;
import co.uk.zing.ledger.command.service.ForexCommandHandlerService;
import co.uk.zing.ledger.query.service.AccountQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forex")
public class ForexController {

    @Autowired
    private ForexCommandHandlerService forexCommandHandler;

    @Autowired
    private AccountQueryService accountQueryService;

    @PostMapping
    public ResponseEntity<Void> executeForexOperation(@RequestBody ForexCommand command) {
        boolean isProcessed = forexCommandHandler.isProcessed(command.getRequestId());
        if (isProcessed) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        forexCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

