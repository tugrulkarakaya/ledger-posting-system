package co.uk.zing.ledger.steps;

import co.uk.zing.ledger.command.ForexCommand;
import co.uk.zing.ledger.command.model.Account;
import co.uk.zing.ledger.exception.AccountNotFoundException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;

public class ForexIdempotencySteps {

    UUID Cashier, GBP, EUR;

    @Autowired
    private TestRestTemplate restTemplate;

    String GBP2EURRequestId = UUID.randomUUID().toString();
    HttpStatusCode gbp2eurForex1Code;
    HttpStatusCode gbp2eurForex2Code;

    @Given("accounts exist: Cashier GBP, Account1 GBP, Account2 EUR")
    public void performForexOperation() throws URISyntaxException {
        Cashier = createAccount("CASHIER");
        GBP = createAccount("USD");
        EUR = createAccount("EUR");
    }

    @And("the Cashier account has 10000 GBP posted credits")
    public void increasePostedBalanceForCashier() throws URISyntaxException {
        String url = getBaseUrl() + "/api/command/"+Cashier+"/increasePostedCredits?amount=10000";
        ResponseEntity<Void> voidResponse;
        voidResponse = restTemplate.postForEntity(new URI(url), null, Void.class);
        if(!voidResponse.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("System could not be started with money");
        }
    }

    @When("a forex operation is performed from Cashier to GBP with amount {int} and rate {double}")
    public void performForexOperationFromCashierToGBP(int amount, double rate) throws URISyntaxException {
        ForexCommand forexCommand = new ForexCommand();
        String requestId = UUID.randomUUID().toString();

        forexCommand.setRequestId(requestId);
        forexCommand.setExchangeRate(BigDecimal.valueOf(rate));
        forexCommand.setSourceAccountId(Cashier.toString());
        forexCommand.setDestinationAccountId(GBP.toString());
        forexCommand.setAmount(BigDecimal.valueOf(amount));

        ResponseEntity<Void> response;
        String url = getBaseUrl() + "/api/forex/create";
        response = restTemplate.postForEntity(new URI(url), forexCommand, Void.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Money could not be transferred");
        }
    }

    @When("a forex operation is performed from GBP to EUR with rate {double}")
    public void GBP2EUR(double rate) throws URISyntaxException, InterruptedException {
        Thread.sleep(1000); // as asynch operation of previous money transfer. so need to wait posted credit balance is bigger than 0
        gbp2eurForex1Code =  GBP2EUR(rate, GBP, EUR, 500);
    }

    @When("the same forex operation GBP to EUR is repeated")
    public void sameGBP2EUR() throws URISyntaxException {
        gbp2eurForex2Code =  GBP2EUR(1.2, GBP, EUR, 500);
    }


    @Then("the first forex operation returns status 202 Accepted")
    public void firstForexOperationReturnsAccepted() {
        Assertions.assertEquals(gbp2eurForex1Code.value(),202);
    }

    @Then("the second and third forex operations return status 200 OK")
    public void secondForexOperationReturnsAccepted() {
        Assertions.assertEquals(gbp2eurForex2Code.value(),200);
    }


    private HttpStatusCode GBP2EUR(double rate, UUID GBP, UUID EUR, int amount) throws URISyntaxException {
        ForexCommand forexCommand = new ForexCommand();

        forexCommand.setRequestId(GBP2EURRequestId);
        forexCommand.setExchangeRate(BigDecimal.valueOf(rate));
        forexCommand.setSourceAccountId(GBP.toString());
        forexCommand.setDestinationAccountId(EUR.toString());
        forexCommand.setAmount(BigDecimal.valueOf(amount));

        ResponseEntity<Void> response;
        String url = getBaseUrl() + "/api/forex/create";
        response = restTemplate.postForEntity(new URI(url), forexCommand, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Money could not be transferred");
        }
        return response.getStatusCode();
    }

    private String getBaseUrl() {
        return "http://localhost:8080";
    }

    private UUID createAccount(String currency) throws URISyntaxException {
        Map<String, String> accountDetails = new HashMap<>();
        accountDetails.put("currency", currency);
        String url = getBaseUrl() + "/api/command/accounts/name";
        ResponseEntity<Account> response;
        response = restTemplate.postForEntity(new URI(url), accountDetails, Account.class);
        if(response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getId();
        } else {
            throw new AccountNotFoundException(String.format("Account could not be created: %s", currency));
        }
    }


    @And("the final balances should be Cashier {long} GBP {long} and EUR {long}")
    public void theFinalBalancesShouldBeCashierGBPAndEUR(long cashier, long gbp, long eur) throws URISyntaxException, InterruptedException {
        Thread.sleep(1000);
        String urlCashier = getBaseUrl() + "/api/forexquery/accounts/"+Cashier;
        String urlGBP = getBaseUrl() + "/api/forexquery/accounts/"+GBP;
        String urlEUR = getBaseUrl() + "/api/forexquery/accounts/"+EUR;
        ResponseEntity<Account> responseCashier = restTemplate.getForEntity(new URI(urlCashier), Account.class);
        ResponseEntity<Account> responseGBP = restTemplate.getForEntity(new URI(urlGBP), Account.class);
        ResponseEntity<Account> responseEUR = restTemplate.getForEntity(new URI(urlEUR), Account.class);

        Assertions.assertEquals(BigDecimal.valueOf(cashier,2), Objects.requireNonNull(responseCashier.getBody()).getAvailableBalance());
        Assertions.assertEquals(BigDecimal.valueOf(gbp,2), Objects.requireNonNull(responseGBP.getBody()).getAvailableBalance());
        Assertions.assertEquals(BigDecimal.valueOf(eur,2), Objects.requireNonNull(responseEUR.getBody()).getAvailableBalance());
    }
}
