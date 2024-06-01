package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Other transaction related command methods
}
