Feature: Loan

  Background:
    Given User has launched the merchant application

  @regressionExecution @loanReg @secondaryRegression
  Scenario Outline: Validate loan Journey
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "LOAN"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User navigates to the loan page
    Then User can verify the loan page
    When User clicks on the detailed statement in quick links
    Then User verify loan statement page
    And User navigates to the "loans home page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "loans home page"
    When User clicks apply for loan in loan page
    Then User verify apply for loan page
    When User clicks on the detailed statement in quick links
    Then User verify loan statement page
    And User navigates to the "apply for loan page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "apply for loan page"
    When User select loan type
    And User clicks on the detailed statement in quick links
    Then User verify loan statement page
    And User navigates to the "specific apply loan type"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "specific apply loan type"
    When User select loan type
    Then User verify the selected loan page
    When User clicks proceed button in selected loan page
    Then User verify apply loan success page
    And User navigates to the apply loan home page
    And User verify the navigation of loan type
    And User clicks back button navigates to loan home page
    When User clicks on one of the view button active loan account section
    Then User will be navigated on the loan details page
    When User clicks on the detailed statement in quick links
    Then User verify loan statement page
    And User navigates to the "loans details page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "loans details page"
    And User verify more details section in loan details page
    When User clicks on recent activity section
    And User verify recent activity section in loan details page
    When User clicks on transaction recent activity section
    Then User verify the popup in recent activity
#   And User verify the download in recent activity pop up
    And User clicks cancel button in recent activity pop up
    And User verify amortisation Table section in loan details page
#    And User verify amortisation statement download
    When User clicks pay now button in loan details page
    Then User verify pay over due page details
    And User enter the otp and verify the otp
    Then User verify the pay over due payment status page
    And User clicks on one of the view button active loan account section
    When User clicks loan statement button
    Then User verify loan statement page
    And User select the account the account from loan statement page
    When User apply filter on loan account statement section
    Then User verify loan account statement account section as per applied filter
#   And User verify downloadStatement in loan statement page
    And User remove all applied filter
    And User select transaction period from duration filter
    And User verify transaction date sorting functionality in loan account statement page
  #  And User verify description sorting functionality in loan account statement page
    And User verify amount sorting functionality in loan account statement page
    And User verify balance sorting functionality in loan account statement page
    And User verify transaction section page shows label in loan statement
    And User verify maximize functionality of loan account statement section
    When User clicks on maximize button in loan transaction section
    Then User verify the maximized transaction section
    And User verify the value date sorting function in loan transaction section
    And User clicks on maximize button in loan transaction section
    When User clicks on one of the loan account statement list
    Then User verify the popup in loan statement section
#   And User verify the download transaction in popup
    And User clicks on cancel button in transaction pop up
    And User verify the loan statement section by enter description
#   And User verify downloadStatement in loan statement page
    When User clicks get statement button
    Then User verify get statement page
#   And User verify get statement download file
#   When User click email button in getStatement page in loan
#   And User enters all needed details on getStatement page in loan
#   Then User verify the email sent Successfully in loan
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |