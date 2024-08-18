Feature: Summary

  Background:
    Given User has launched the merchant application

  @regressionExecution @summaryReg @secondaryRegression
  Scenario Outline: Validate account summary page
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "ACCOUNT SUMMARY"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User navigates on the account summary page
    And User read the cif id and account holder name on profile menu
    Then User can verify the account summary page
  ## Quick Links Steps Uses All Scenarios ##
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "account summary home page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "account summary home page"
                       ## ---*---*--- ##
    When User clicks on one of the view button of available accounts
    Then User will be navigated on the account details section
    And User can verify the account details section
    And User verify recent transaction section
    And User verify additional details section
   # When User clicks nominee edit in additional details section
   # Then User verify nominee update page and enter all details all details
   # And User verify update nominee details in additional details section
    And User verify average monthly balance section
    When User clicks on 'edit nick name' icon
    Then User can see 'update nick name' popup is opened
    And User successfully able to update the nick name
    And User verify available balance tool tip message on accounts details page
    When Use clicks on available balance drop down section on accounts details page
    And User verify available balance drop down section on accounts details page
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "account details page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "account details page"
    And User clicks on quick link debit card and verify the page
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID | CIFID    |
      | TC_01 | 27456305 |


  Scenario Outline: Validate for cash credit and overdue draft accounts in summary page
    And User navigates on the account summary page
    Then User can verify "<Accounts>" in account summary page
    And User clicks on log out the session and verifies


    Examples:
      | CIFID    | Accounts  |
#      | 29454167 | Cash Credit |
      | 22363529 | Overdraft |