Feature: Dashboard

  Background:
    Given User has launched the merchant application
  @regressionExecution @dashBoardReg @primaryRegression
  Scenario Outline: Validate dash board journey
    Given User has read testData "<TC_ID>" from excel sheet
    And User navigates to the log in page
    When User enters valid log in credentials for "DASHBOARD"
    And User enter the otp and verify the otp
    And User validates the log-in status
    And User verify advisory page for user practice
    And User navigates to the dashboard page
    When User clicks on bank toggle button
    Then User verify the bank dashboard page screen
    And User verify the overview section when account open banner is present
    And User verify the overview section ensuring that details for available accounts are displayed
    And User verify the payment dashboard on Payment service section
    And User verify the functions available in payment service sections
    And User verify the Manage card section in bank dashboard screen
    And User verify the recent and scheduled transactions section
    And User verify the side menu section
    And User verify the investment page navigation details
    And User verify the menus details in apply now page
#   And User verify the video banking menu
    And User verify the emergency assistance tab
    And User verify the redirection in the emergency assistance page
#    And User validates the redirections on the FaQs page
    And User verify the side menu bar visibility in expand function
    And User verify the side menu bar visibility in closed state
    And User clicks on log out the session and verifies

    Examples:
      | TC_ID |
      | TC_01 |