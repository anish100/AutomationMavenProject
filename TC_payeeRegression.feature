Feature: Payee

  Background:
    Given User has launched the merchant application

  @regressionExecution @payeeReg @secondaryRegression
  Scenario Outline: Validate Payees journey
    Given User has read testData "<TC_ID>" from excel sheet
    Then User navigates to the log in page
    When User enters valid log in credentials for "MANAGE PAYEES"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User navigates to the payees page
    Then User can verify the payees page
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "manage payee home page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "manage payee home page"
    When User clicks add new payee button
    And User can verify add new payee page
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "add new payee page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "add new payee page"
    And User enters all needed details on add new payee page
    And User add bank account by enter all details manually
    Then User verify the bank details section after adding bank details
    When User clicks next button
    And User enter the otp and verify the otp
    Then User verify new payee added successfully
    When User clicks back to manage payee button
    And User verify newly added payee in payment page list
    When User enters payee details in search box
    Then User verify the payment page list by payee details in search box
    And User clicks view button in payment page section
    Then User verify the payee details page
    When User clicks on make payment button
    Then User verify transfer to payee page
    And User navigates to the "payee details page"
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "payee details page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "payee details page"
    When User clicks edit button and edit the payee details
    Then User enter required details
    When User clicks submit button
    And User enter the otp and verify the otp
    Then User verify edit payee details
    When User clicks pay button payments page section
    Then User verify transfer to payee page
    And User navigates to the "payee details page"
    When User clicks deactivate button
    And User clicks deactivate button in popup
    And User enter the otp and verify the otp
    And User can verify the payee deactivate message
    Then User verify deactivate payee details
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |




