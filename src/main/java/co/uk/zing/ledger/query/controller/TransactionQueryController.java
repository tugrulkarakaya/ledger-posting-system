package co.uk.zing.ledger.query.controller;

import co.uk.zing.ledger.query.dto.TransactionDto;
import co.uk.zing.ledger.query.service.TransactionQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionQueryController {

    @Autowired
    private TransactionQueryService transactionQueryService;

    @GetMapping
    public List<TransactionDto> getTransactions(
            @RequestParam UUID accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return transactionQueryService.getTransactions(accountId, startDate, endDate);
    }
}
