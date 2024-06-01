package co.uk.zing.ledger.command.repository;

import co.uk.zing.ledger.command.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
}
