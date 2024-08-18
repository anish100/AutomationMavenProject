Feature: Payments

  Background:
    Given User has launched the merchant application

  @regressionExecution @paymentReg @primaryRegression
  Scenario Outline: Validating Transfer to payee & Quick transfer payments
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "MONEY TRANSFER"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User will be navigated on the money transfer home page
    Then User verify money transfer home page
 #  Transfer to payee
    When User clicks on transfer to payee tab
    And User select payee for "IMPS"
    And User select all needed details
    And User select payment mode for "IMPS"
    And User verify payment summary for common steps
    And User stored the values in a variable
    When User clicks make payment button
    And User enter the otp and verify the otp
    And User verify transaction details page
    And User clicks back to money transfer page button
    When User clicks on transfer to payee tab
    And User select payee for "NEFT"
    And User select all needed details
    And User select payment mode for "NEFT"
    And User verify payment summary for common steps
    And User stored the values in a variable
    When User clicks make payment button
    And User enter the otp and verify the otp
    And User verify transaction details page
    And User clicks back to money transfer page button
 # Quick Transfer
    When User clicks on quick account transfer tab
    Then User verify quick account transfer page
    And User select "other bank payee" from the list
    And User select all needed details in quick account transfer page
    And User verify the find ifsc page
    And User verify payment summary for common steps
    And User stored the values in a variable
    When User clicks make payment button
    And User enter the otp and verify the otp
    And If the transaction limit is reached, the user goes to the money transfer home
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |

  @regressionExecution @paymentReg @primaryRegression
  Scenario Outline: Validating Transfer to self  & Multiple transfer payments
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "MONEY TRANSFER"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User will be navigated on the money transfer home page
    Then User verify money transfer home page
    When User clicks transfer to self tab
    Then User verify transfer to self page
    And User User select all needed details in transfer to self page
    Then User verify payment summary for common steps
    And User stored the values in a variable
    When User clicks make payment button
    And User enter the otp and verify the otp
    And User verify the error toast message if appeared
    And User verify transaction details page
    And User clicks back to money transfer page button
 # Multiple Transfer
    When User clicks multiple payment tab
    Then User verify multiple payment page
    And User select all needed details and add payees
    Then User verify the multiple payment page after add payments
    When User clicks make payment button
    And User enter the otp and verify the otp
    And User verify the error toast message if appeared
    Then User verify multiple pay transaction details page
    And User clicks back to money transfer home page
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |

  @regressionExecution @scheduledReg @primaryRegression
  Scenario Outline: Validating scheduled transfer payments
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "MONEY TRANSFER"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User will be navigated on the money transfer home page
    Then User verify money transfer home page
 #  Transfer to payee
    When User clicks on transfer to payee tab
    And User select payee for "NEFT"
    And User select all needed details
    And User select payment mode for "NEFT"
    And User select "One Time" scheduled payment
    And User verify  scheduled payment summary
    And User verify payment summary for common steps
    And User stored the values in a variable
    When User clicks make payment button
    And User enter the otp and verify the otp
    And User verify transaction details page
    When User clicks back to money transfer home page
    And User verify schedule transactions
    And User verify schedule transaction paid popup
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |