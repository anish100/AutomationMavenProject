Feature: Deposits FD & RD

  Background:
    Given User has launched the merchant application

  @regressionExecution @depositReg @primaryRegression
  Scenario Outline: Validate Fixed deposit and Recurring Deposit journeys
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "DEPOSITS"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
 #  Read the account details from account summary page
    And User read the CASA details on summary page
    And User navigates on the deposit dashboard page
 # Fd open
    And User navigates on the 'select deposit account type' page
    And User navigates on the 'open fixed deposit' page
 #  And User validates 'fd' page error messages
    And User enters all needed details on FD page
    Then User can validate the FD summary is auto populated
    When User click on 'open fixed deposit' button
    Then User navigates on the FD review page
    And User can validate the FD review page
    When User click on open FD confirm button
    And User enter the otp and verify the otp
    Then User will be navigated on the fd receipt page
    And User clicks on try again button if error message appear
    And User can validate fd is opened successfully
    Then User can obtain the opened "fd" deposit number from the receipt page
    And User navigates back on the deposit dashboard page
    And User click on view button of newly opened deposit
    And User navigated on the deposit details page
    When User clicks on close account button of deposit details page
    Then User can see closing your FD popup appeared
 #  Fd closure
    When User selects "full closure" radio button on closing your FD popup
    And User clicks on proceed button of closing your FD popup
    Then User will be navigated on the "fd-full" closure page
    When User selects credit account for "fd" closure
    Then User can verify the amount details and "fd-full" closure summary
    When User clicks on confirm button
    Then User will be navigated on the "fd-full" closure review page
    And User can verify the details on "fd-full" closure review page
    When User clicks on confirm button
    And User enter the otp and verify the otp
    Then User will be navigated on the "fd-full" closure receipt page
    And User clicks on try again button if error message appear
    And User can verify that "fd-full" closed successfully
    When User navigates back on the deposit dashboard page
    Then User can validate that "fd" is no more visible in the deposit list

 #  Rd Open Closed
    And User navigates on the 'select deposit account type' page
    And User navigates on the 'open recurring deposit' page
    And User enters all needed details on RD page
    Then User can validate the RD summary is auto populated
    When User click on 'open recurring deposit' button
    Then User navigates on the RD review page
    And User can validate the RD review page
    When User click on open RD confirm button
    And User enter the otp and verify the otp
    Then User will be navigated on the rd receipt page
    And User clicks on try again button if error message appear
    And User can validate rd is opened successfully
    Then User can obtain the opened "rd" deposit number from the receipt page
    And User navigates back on the deposit dashboard page
    And User click on view button of newly opened deposit
    And User navigated on the deposit details page
  # Rd closure
    When User clicks on close account button of deposit details page
    Then User will be navigated on the "rd" closure page
    When User selects credit account for "rd" closure
    Then User can verify the amount details and "rd" closure summary
    When User clicks on confirm button
    And User enter the otp and verify the otp
    Then User will be navigated on the "rd" closure review page
    And User can verify the details on "rd" closure review page
    When User clicks on confirm button
    And User enter the otp and verify the otp
    Then User verify the message in review page
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |
