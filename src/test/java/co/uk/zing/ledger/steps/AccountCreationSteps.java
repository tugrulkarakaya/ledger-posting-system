package co.uk.zing.ledger.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class AccountCreationSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @Given("the account creation endpoint is available")
    public void theAccountCreationEndpointIsAvailable() {
        // This can be a simple check to ensure the application context is loaded
        Assertions.assertNotNull(restTemplate, "RestTemplate should be autowired and not null.");

    }

    @When("I send a request to create a new account with the currency {string}")
    public void iSendARequestToCreateANewAccountWithTheCurrency(String currency) {
        Map<String, String> accountDetails = new HashMap<>();
        accountDetails.put("currency", currency);

        response = restTemplate.postForEntity("/api/command/accounts/name", accountDetails, String.class);
    }

    @When("I send a request to create a new account without specifying a currency")
    public void iSendARequestToCreateANewAccountWithoutSpecifyingACurrency() {
        Map<String, String> accountDetails = new HashMap<>();

        response = restTemplate.postForEntity("/api/accounts", accountDetails, String.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        Assertions.assertEquals(HttpStatus.valueOf(statusCode), response.getStatusCode());
    }

    @Then("the response body should contain the account details:")
    public void theResponseBodyShouldContainTheAccountDetails(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> expectedDetails = dataTable.asMaps().get(0);

        for (Map.Entry<String, String> entry : expectedDetails.entrySet()) {
            Assertions.assertTrue(response.getBody().contains(entry.getKey() + "\":\"" + entry.getValue()));
        }
    }

    @Then("the response body should contain an error message {string}")
    public void theResponseBodyShouldContainAnErrorMessage(String errorMessage) {
        Assertions.assertTrue(response.getBody().contains(errorMessage));
    }
}
