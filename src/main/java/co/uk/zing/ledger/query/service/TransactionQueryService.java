package co.uk.zing.ledger.query.service;


import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.query.dto.TransactionDto;
import co.uk.zing.ledger.query.dto.EntryDto;
import co.uk.zing.ledger.command.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionQueryService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<TransactionDto> getTransactions(UUID accountId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findTransactionsByAccountIdAndDateRange(accountId, startDate, endDate);
        return transactions.stream()
                .map(t-> convertToDto(accountId, t))
                .collect(Collectors.toList());
    }

    private TransactionDto convertToDto(UUID accountId, Transaction transaction) {
        List<EntryDto> entryDtos = transaction.getEntries().stream()
                .filter(e-> e.getAccountId().equals(accountId))
                .filter(e-> Objects.isNull(e.getDiscardedAt()))
                .map(entry -> EntryDto.builder()
                        .id(entry.getId()).status(entry.getStatus()).discardedAt(entry.getDiscardedAt())
                        .entryTime(entry.getEntryTime()).accountId(entry.getAccountId())
                        .amount(entry.getAmount()).status(entry.getStatus())
                        .build())
                .collect(Collectors.toList());

        return new TransactionDto(
                transaction.getId(),
                transaction.getType(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt(),
                transaction.getRequestId(),
                entryDtos
        );
    }
}