Feature: Statement

  Background:
    Given  User has launched the merchant application

  @regressionExecution @statementReg @primaryRegression
  Scenario Outline:Validate Account statement Journey
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "ACCOUNT STATEMENT"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User navigates to account statement page
    Then User verify the account statement page
    And User select required account from select account
    Then User can validate the transaction section
    When User click on any one of the transaction from the transaction list
    Then User validate the popup details
    And User clicks cancel button on popup
    When User enter reference number accountStatement page
    Then User verify the transaction details by reference number
    And User verify the calendar details on duration filter
    When User apply the filter on account statement page
    Then User can verify the statement details as per the filter
    And User remove all applied filter in account statement
    And User verify transaction section page shows label
    When User clicks on maximize button in transaction section
    Then User verify the maximized transaction section
    And User verify the value date sorting function in transaction section
    Then User clicks on maximize button in transaction section
    When User click transaction date sorting functionality in transaction section
    Then User validate the transaction date sorting functionality in transaction section
    And User validate the amount sorting functionality in transaction list
    When User click balance sorting functionality in transaction section
    Then User validate the balance sorting functionality in transaction section
    And User verify the quick download section
    And User verify the "current" financial year "downloading" pop up
    And User verify the "current" financial year "emailing" pop up
    And User verify the "previous" financial year "downloading" pop up
    And User verify the "previous" financial year "emailing" pop up
    When User clicks get statement button
    Then User verify get statement page
    And User clicks on log out the session and verifies


    Examples:
      | TC_ID |
      | TC_01 |
  @regressionExecution @statementReg @primaryRegression
  Scenario Outline: Validate more than 999 transaction in between the dates
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "ACCOUNT STATEMENT"
    And User clicks on store toggle button and navigates to bank dashboard page
    # Read the account details from account summary page
    And User read the CASA details on summary page

    And User navigates to account statement page
    Then User verify the account statement page
    And User select required account from select account
    When User apply the date filter on account statement page
    Then User verify the account transaction section
    And User validate the functions in transaction section page
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |

  @regressionExecution @statementReg @primaryRegression
  Scenario Outline: Validate more than 999 transaction in a day
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "ACCOUNT STATEMENT"
    And User clicks on store toggle button and navigates to bank dashboard page
    And User navigates to account statement page
    Then User verify the account statement page
    And User select required account from select account
    When User apply the date filter on account statement page
    Then User verify the account transaction section
    And User validate the functions in transaction section page
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |
