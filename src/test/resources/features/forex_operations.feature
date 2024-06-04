Feature: Forex Operation

  Scenario: Idempotent Forex Operations with Different Return Types
    Given accounts exist: Cashier GBP, Account1 GBP, Account2 EUR
    And the Cashier account has 10000 GBP posted credits
    When a forex operation is performed from Cashier to GBP with amount 1000 and rate 1
    And a forex operation is performed from GBP to EUR with rate 1.2
    And the same forex operation GBP to EUR is repeated
    Then the first forex operation returns status 202 Accepted
    And the second and third forex operations return status 200 OK
    And the final available balances should be Cashier 900000 GBP 50000 and EUR 60000


