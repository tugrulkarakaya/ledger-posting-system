Feature: Account creation

  Scenario: Successfully creating a new account
    Given the account creation endpoint is available
    When I send a request to create a new account with the currency "USD"
    Then the response status should be 201
    And the response body should contain the account details:
      | currency |
      | USD      |

  Scenario: Creating an account with missing currency
    Given the account creation endpoint is available
    When I send a request to create a new account without specifying a currency
    Then the response status should be 400
    And the response body should contain an error message "Currency is required"
