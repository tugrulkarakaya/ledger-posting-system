package co.uk.zing.ledger.command.service;

import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.exception.AccountNotFoundException;
import co.uk.zing.ledger.exception.MissingAccountNameException;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountCommandServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountCommandService accountCommandService;

    private Account account;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = new Account();
        account.setId(accountId);
        account.setCurrency("USD");
        account.setPostedDebits(BigDecimal.ZERO);
        account.setPostedCredits(BigDecimal.ZERO);
    }

    @Test
    void createAccount_shouldCreateAccountWithGivenCurrency() {
        String currency = "USD";
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account createdAccount = accountCommandService.createAccount(currency);

        assertNotNull(createdAccount);
        assertEquals(currency, createdAccount.getCurrency());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_shouldThrowMissingAccountNameExceptionWhenCurrencyIsEmpty() {
        String emptyCurrency = "";

        assertThrows(MissingAccountNameException.class, () -> accountCommandService.createAccount(emptyCurrency));
    }

    @Test
    void createAccount_shouldThrowMissingAccountNameExceptionWhenCurrencyIsNull() {
        String nullCurrency = null;

        assertThrows(MissingAccountNameException.class, () -> accountCommandService.createAccount(nullCurrency));
    }

    @Test
    void increasePostedDebits_shouldIncreaseDebitsForGivenAccount() {
        BigDecimal amount = BigDecimal.valueOf(100);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountCommandService.increasePostedDebits(accountId, amount);

        assertEquals(amount, account.getPostedDebits());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void increasePostedDebits_shouldThrowAccountNotFoundExceptionWhenAccountNotFound() {
        BigDecimal amount = BigDecimal.valueOf(100);
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountCommandService.increasePostedDebits(accountId, amount));
    }

    @Test
    void increasePostedCredits_shouldIncreaseCreditsForGivenAccount() {
        BigDecimal amount = BigDecimal.valueOf(100);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountCommandService.increasePostedCredits(accountId, amount);

        assertEquals(amount, account.getPostedCredits());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void increasePostedCredits_shouldThrowAccountNotFoundExceptionWhenAccountNotFound() {
        BigDecimal amount = BigDecimal.valueOf(100);
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountCommandService.increasePostedCredits(accountId, amount));
    }
}
