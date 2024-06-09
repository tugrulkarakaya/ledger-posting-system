Feature: Forex Command Handler

  Scenario: Transaction is already processed
    Given a request id "test-request-id" that is already processed
    When I check if the request is processed
    Then the result should be true

  Scenario: Transaction is not processed
    Given a request id "test-request-id" that is not processed
    When I check if the request is processed
    Then the result should be false

  Scenario: Handle forex sync command successfully
    Given a forex command with sufficient funds
    When I handle the forex command
    Then the transaction should be saved
    And the accounts should be updated
    And an event should not be published

  Scenario: Handle forex Async command successfully
    Given a forex command with sufficient funds
    And the forex command is async
    When I handle the forex command
    Then the transaction should be saved
    And an event should be published

  Scenario: Handle forex command with insufficient funds
    Given a forex command with insufficient funds
    When I handle the forex command
    Then an InsufficientFundsException should be thrown

