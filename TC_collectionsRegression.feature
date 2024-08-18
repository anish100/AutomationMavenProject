Feature: Collections

  Background:

    Given  User has launched the merchant application

  @regressionExecution @collectionReg @secondaryRegression
  Scenario Outline: Validate the collections journey
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "COLLECTIONS"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User navigates to the dashboard page
    And User verify the store dashboard page screen when QR code available
    And User verify collection container on store dashboard page
    And User verify the QR code details on store dashboard page
    When User clicks on download QR button
    Then User verify the downloaded QR details
    When User clicks on generate qr button
    Then User verify the dynamic QR pop up
    And User verify the generated dynamic QR details
    When User clicks on collections on store dashboard page
    Then User verify the QR transaction page
    And User select the vpa id from drop down
    When there are no transactions, the user chooses a period for transactions
    When User clicks on any one transaction on transaction section
    Then User verify the transaction pop up on transaction section
#    When User clicks on download button collection page pop up
#    And User verify the download transaction in popup
    And User clicks on cancel button in transaction pop up
    When User clicks on transaction date sorting button
    Then User validate the transaction date ascending sorting function
    When User clicks on transaction date sorting button
    Then User validate the transaction date descending sorting function
    When User clicks on amount sorting button
    Then User validate the amount ascending sorting function
    When User clicks on amount sorting button
    Then User validate the amount descending sorting function
    When User apply the calendar filter on QR transaction page
    Then User verify the transaction section based on calendar filter
    #After Apply filter verify the sorting
    When User clicks on transaction date sorting button
    Then User validate the transaction date ascending sorting function
    When User clicks on transaction date sorting button
    Then User validate the transaction date descending sorting function
    When User clicks on amount sorting button
    Then User validate the amount ascending sorting function
    When User clicks on amount sorting button
    Then User validate the amount descending sorting function
     #After Apply filter verify the sorting
    When  User clicks on amount filter on QR transaction page
    Then User verify the transaction section based on amount filter
    Then User verify the transaction section based on calendar filter
#    When User clicks on download statement
#    Then User verify the downloaded statement file
    And User remove the applied filters
    When there are no transactions, the user chooses a period for transactions
#    When User clicks on download statement
#    Then User verify the downloaded statement file
    When User enters the "customer name" in the search box
    And User verifies the transaction section by "customer name"
    When User enters the "status" in the search box
    And User verifies the transaction section by "status"
    And User clicks on log out the session and verifies


    Examples:
      | TC_ID |
      | TC_01 |