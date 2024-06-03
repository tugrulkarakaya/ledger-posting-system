package co.uk.zing.ledger.query.service;


import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.query.dto.TransactionDto;
import co.uk.zing.ledger.query.dto.EntryDto;
import co.uk.zing.ledger.command.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionQueryService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<TransactionDto> getTransactions(UUID accountId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findTransactionsByAccountIdAndDateRange(accountId, startDate, endDate);
        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TransactionDto convertToDto(Transaction transaction) {
        List<EntryDto> entryDtos = transaction.getEntries().stream()
                .map(entry -> new EntryDto(entry.getId(), entry.getAccountId(), entry.getAmount(), entry.getEntryTime(), entry.getType(), entry.getDirection()))
                .collect(Collectors.toList());

        return new TransactionDto(
                transaction.getId(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt(),
                transaction.getRequestId(),
                entryDtos
        );
    }
}