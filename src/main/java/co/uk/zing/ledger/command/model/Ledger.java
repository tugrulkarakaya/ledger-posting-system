package co.uk.zing.ledger.command.model;

import lombok.Data;

import java.util.List;
import java.util.ArrayList;

@Data
public class Ledger {
    private List<Account> accounts = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
