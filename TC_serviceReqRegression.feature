Feature: ServiceRequest

  Background:
    Given User has launched the merchant application

  @regressionExecution @serviceReg @secondaryRegression
  Scenario Outline: Validating  new cheque book request journey in service request
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "SERVICE REQUEST"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
 #  Read the account details from account summary page
    And User read the CASA details on summary page
    And User will be navigated on the service request home page
    Then User verify service request home page
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "service request home page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "service request home page"
    When User clicks on new cheque book tab
    Then User verify new cheque book page
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "new cheque book request page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "new cheque book request page"
    And User enters the details on new cheque book page
 #  When User clicks on update address on new cheque book page
 #  Then User verify and enters the all address update page
    And User clicks request cheque book button
    Then User verify the new cheque book status page
    And User navigates to the "service request home page"
 #  And User clicks on back button
    When User clicks on service request list
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "service request list page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "service request list page"
    Then User verify the service request list page and clicks on view button
    And User verify the track service request page
    And User clicks on log out the session and verifies


    Examples:
      | TC_ID |
      | TC_01 |

  @regressionExecution @serviceReg @secondaryRegression @positivePayReg
  Scenario Outline: Validating cheque pay journey in service request
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "SERVICE REQUEST"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User will be navigated on the service request home page
    Then User verify service request home page
    When User clicks on positive pay tab
    Then User navigates to the positive pay page
    And User selects the date from calendar for "positive pay"
    Then User clicks on proceed button
    And User verify the positive pay review page
    Then User verify the positive pay status page
    And User navigates to the service home page
    When User clicks on check status tab
    Then User navigates to check status page
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "cheque status page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "cheque status page"
    And User enter cheque details
    And User clicks on find status button
    And User verify the status of the cheque
    And User clicks on back button
    When User clicks on positive pay status tab
    Then User navigates to the positive pay status tab
    And User clicks on back button
    When User clicks on stop cheque tab
    Then User navigates to stop cheque status page
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "stop cheque page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "stop cheque page"
    And User enter cheque details
    And User verify the status of the stop cheque
    And User clicks on stop cheque button
    And User enter the otp and verify the otp
 #  And User clicks on back button
    And User clicks on log out the session and verifies
#Cheque number not valid

    Examples:
      | TC_ID |
      | TC_01 |

  @regressionExecution @serviceReg @secondaryRegression @updateNominee
  Scenario Outline: Validating update nominee details journey in service request
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "SERVICE REQUEST"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User will be navigated on the service request home page
    Then User verify service request home page
 # Update Nominee Details
    When User clicks on add Or update nominee tab
    Then User verify account nominee page
    When User clicks on add Or update nominee button
    Then User verify the update nominee side sheet
    And User enter the nominee details
    And User selects the date from calendar for "add nominee"
    Then User clicks on proceed to review button
    And User validates the nominee review page
    And User validate the navigation edit nominee details button
    Then User clicks on proceed to review button
    And User clicks on send otp button
    And User enter the otp and verify the otp
    Then User verify the nominee added popup
    And User validates the nominee's update on the nominee side sheet
    And User clicks on back button
    When User clicks on update emailId tab
    Then User navigates to update emailId page
    And User enter the details to update mail id
    And User enter the otp and verify the otp
    And User verify the update status page
    And User clicks on back to home page button
    And User navigates to the service request home page
    When User clicks on update communication address button
    Then User verify update communication address page
    When User clicks on the detailed statement in quick links
    Then User verify the account statement page
    And User navigates to the "update communication address page"
    When User clicks on the money transfer in quick links
    Then User verify money transfer home page
    And User navigates to the "update communication address page"
    And User enter all details to update communication address
    And User enter the otp and verify the otp
    And User verify the update communication address status page
    And User clicks on back to home page button
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |

  @regressionExecution @serviceReg @secondaryRegression @TrackReqReg
  Scenario Outline: Validating track request journey in service request
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "SERVICE REQUEST"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User clicks on store toggle button and navigates to bank dashboard page
      # Read the account details from account summary page
    And User read the CASA details on summary page
    And User will be navigated on the service request home page
    Then User verify service request home page
    When User clicks on form 15g tab
    Then User verify and enter the details in form page
    And User navigates to the form 15g review page
    When User clicks on proceed button to navigates status page
    And User verify the form 15g status page
    When User clicks on track service request tab
    Then User verify the track services request page
    When User selects the type of request for track request services
    Then User verify service request list page
    And User clicks on view button by reference number
    And User verify the track service request page
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |
