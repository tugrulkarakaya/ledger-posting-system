package co.uk.zing.ledger.steps;


import co.uk.zing.ledger.command.ForexCommand;
import co.uk.zing.ledger.command.ForexTransactionCreatedEvent;
import co.uk.zing.ledger.command.event.EventPublisher;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.command.model.Transaction;
import co.uk.zing.ledger.command.repository.AccountRepository;
import co.uk.zing.ledger.command.repository.TransactionRepository;
import co.uk.zing.ledger.command.service.AccountCommandService;
import co.uk.zing.ledger.command.service.ForexCommandHandlerService;
import co.uk.zing.ledger.command.service.ForexEventConsumerService;
import co.uk.zing.ledger.exception.InsufficientFundsException;
import io.cucumber.java.en.*;
import io.cucumber.java.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ForexCommandHandlerSteps {

    @Autowired
    private ForexCommandHandlerService forexCommandHandlerService;

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private EventPublisher eventPublisher;

    private ForexEventConsumerService forexEventConsumerService;
    private AccountCommandService accountCommandService;

    private String requestId;
    private ForexCommand forexCommand;
    private boolean isProcessed;
    private Exception exception;

    @Before
    public void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        eventPublisher = mock(EventPublisher.class);
        forexEventConsumerService = spy(new ForexEventConsumerService(accountRepository,transactionRepository,accountCommandService));
        accountCommandService = mock(AccountCommandService.class);

        forexCommandHandlerService = new ForexCommandHandlerService(eventPublisher, forexEventConsumerService);
        forexCommandHandlerService.setAccountRepository(accountRepository);
        forexCommandHandlerService.setTransactionRepository(transactionRepository);
        forexEventConsumerService.setAccountCommandService( accountCommandService); //Ugly It should have been started with proper constructor.
    }

    @Given("a request id {string} that is already processed")
    public void a_request_id_that_is_already_processed(String requestId) {
        this.requestId = requestId;
        when(transactionRepository.findByRequestId(requestId)).thenReturn(Optional.of(new Transaction()));
    }

    @Given("a request id {string} that is not processed")
    public void a_request_id_that_is_not_processed(String requestId) {
        this.requestId = requestId;
        when(transactionRepository.findByRequestId(requestId)).thenReturn(Optional.empty());
    }

    @Given("a forex command with sufficient funds")
    public void a_forex_command_with_sufficient_funds() {
        String sourceAccountId = UUID.randomUUID().toString();
        String destinationAccountId = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal exchangeRate = BigDecimal.valueOf(1.1);
        requestId = "test-request-id";

        forexCommand = ForexCommand.builder()
                .sourceAccountId(sourceAccountId)
                .destinationAccountId(destinationAccountId)
                .amount(amount).exchangeRate(exchangeRate)
                .requestId(requestId)
                .isSynchronize(true).build();

        Account sourceAccount = Account.builder().currency("Cashier").pendingCredits(BigDecimal.valueOf(0L)).postedCredits(BigDecimal.valueOf(20000L))
                .pendingDebits(BigDecimal.ZERO).postedDebits(BigDecimal.ZERO).id(UUID.fromString(sourceAccountId)).version(1L).build();

        Account destinationAccount = Account.builder().currency("Cashier").pendingCredits(BigDecimal.valueOf(0L)).postedCredits(BigDecimal.valueOf(100L))
                .pendingDebits(BigDecimal.ZERO).postedDebits(BigDecimal.ZERO).id(UUID.fromString(destinationAccountId)).version(1L).build();

        when(accountRepository.findById(UUID.fromString(sourceAccountId))).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(UUID.fromString(destinationAccountId))).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any())).thenReturn(new Account());
        when(accountCommandService.getAvailableBalance(any())).thenReturn(BigDecimal.valueOf(20000L));
        when(transactionRepository.save(any())).thenReturn(new Transaction());
    }

    @Given("a forex command with insufficient funds")
    public void a_forex_command_with_insufficient_funds() {
        String sourceAccountId = UUID.randomUUID().toString();
        String destinationAccountId = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.valueOf(300);
        BigDecimal exchangeRate = BigDecimal.valueOf(1.1);
        requestId = "test-request-id";

        forexCommand = ForexCommand.builder()
                .sourceAccountId(sourceAccountId)
                .destinationAccountId(destinationAccountId)
                .amount(amount).exchangeRate(exchangeRate)
                .requestId(requestId)
                .isSynchronize(true).build();

        Account sourceAccount = Account.builder().currency("Cashier").pendingCredits(BigDecimal.valueOf(0L)).postedCredits(BigDecimal.valueOf(200L))
                .pendingDebits(BigDecimal.ZERO).postedDebits(BigDecimal.ZERO).id(UUID.fromString(sourceAccountId)).version(1L).build();

        Account destinationAccount = Account.builder().currency("Cashier").pendingCredits(BigDecimal.valueOf(0L)).postedCredits(BigDecimal.valueOf(20000L))
                .pendingDebits(BigDecimal.ZERO).postedDebits(BigDecimal.ZERO).id(UUID.fromString(destinationAccountId)).version(1L).build();

        when(accountRepository.findById(UUID.fromString(sourceAccountId))).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(UUID.fromString(destinationAccountId))).thenReturn(Optional.of(destinationAccount));
    }

    @When("I check if the request is processed")
    public void i_check_if_the_request_is_processed() {
        isProcessed = forexCommandHandlerService.isProcessed(requestId);
    }

    @When("I handle the forex command")
    public void i_handle_the_forex_command() {
        try {
            forexCommandHandlerService.handle(forexCommand);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the result should be true")
    public void the_result_should_be_true() {
        Assertions.assertTrue(isProcessed);
    }

    @Then("the result should be false")
    public void the_result_should_be_false() {
        Assertions.assertFalse(isProcessed);
    }

    @Then("the transaction should be saved")
    public void the_transaction_should_be_saved() {
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Then("the accounts should be updated")
    public void the_accounts_should_be_updated() {
        verify(accountRepository, atLeast(2)).save(any(Account.class));
    }

    @Then("an event should not be published")
    public void an_event_should_not_be_published() {
        verify(eventPublisher, never()).publish(any(ForexTransactionCreatedEvent.class));
    }

    @Then("an event should be published")
    public void an_event_should_be_published() {
        verify(eventPublisher, times(1)).publish(any(ForexTransactionCreatedEvent.class));
    }

    @Then("an InsufficientFundsException should be thrown")
    public void an_insufficient_funds_exception_should_be_thrown() {
        Assertions.assertTrue(exception instanceof InsufficientFundsException);
    }

    @And("the forex command is async")
    public void theForexCommandIsAsync() {
        forexCommand.setSynchronize(false);
    }
}