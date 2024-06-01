package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.model.Entry;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionCommandService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionCommandService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction createTransaction(List<Entry> entries) {
        Transaction transaction = new Transaction();
        transaction.setEntries(entries);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("PENDING");

        return transactionRepository.save(transaction);
    }
    // Other transaction related command methods
}
