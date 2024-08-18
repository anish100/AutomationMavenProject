package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;
import pom.*;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(DashboardStepDef.class);

    //   LogInPage logInPage;
    DashboardPage dashboardPage;
    AccountSummaryPage accountSummaryPage;
    DepositsDashboardPage depositsDashboardPage;
    MoneyTransferStepDef moneyTransferStepDef;
    DebitCardPage debitCardPage;
    CollectionsPage collectionsPage;
    LoanPage loanPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String noSavingAccAvailableInDashBoardPage;
    String noCurrentAccAvailableInDashBoardPage;
    String fixedDeposit;
    String recurringDeposit;
    String taxSaver;
    boolean manageCardIsDisplayed;
    boolean loanAccActive;
    boolean manageCardVisibility;
    boolean sideMenuCloseState;

    public DashboardStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        moneyTransferStepDef = new MoneyTransferStepDef(context);
        dashboardPage = context.getPageObjectManager().getDashboardPage();
        accountSummaryPage = context.getPageObjectManager().getAccountSummaryPage();
        depositsDashboardPage = context.getPageObjectManager().getDepositDashboardPage();
        debitCardPage = context.getPageObjectManager().getDebitCardPage();
        collectionsPage = context.getPageObjectManager().getCollectionsPage();
        loanPage = context.getPageObjectManager().getLoanPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
    }

    @Then("User verify the store dashboard page screen")
    public void userVerifyTheStoreDashboardPageScreen() {
        softAssert = new SoftAssert();
        waitTillInvisibilityOfLoader(driver);
        try {
            scrollIntoViewUp(driver, dashboardPage.homePageLink);
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.dashBoardUrlPath), "dashboard page url not matched");
            softAssert.assertTrue(dashboardPage.serviceToggle.isDisplayed(), "service toggle icon not displayed");
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Dashboard page not displayed please verify the screen shot");
        }
        softAssert.assertTrue(dashboardPage.welcomeText.isDisplayed(), "dashboard page not displayed");
        softAssert.assertTrue(dashboardPage.supportIcon.isDisplayed(), "support icon not displayed");
        softAssert.assertTrue(dashboardPage.bellIcon.isDisplayed(), "bell icon not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on bank toggle button")
    public void userClicksOnBankToggleButton() {
        javaScriptExecutorClickElement(driver, dashboardPage.serviceToggle);
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User verify the bank dashboard page screen")
    public void userVerifyTheBankDashboardPageScreen() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.dashBoardUrlPath), "dashboard url not matched");
        softAssert.assertTrue(dashboardPage.bankToggleActive.isDisplayed(), "bank toggle is not selected");

        if (dashboardPage.overViewSection.size() != 0) {
            logger.info("This id belongs to ETB");
            softAssert.assertTrue(dashboardPage.overViewSection.size() != 0, "overview section are not displayed");
            softAssert.assertTrue(dashboardPage.viewAllButton.size() != 0, "view button are not displayed");
            softAssert.assertTrue(dashboardPage.paymentService.isDisplayed(), "payment service header not displayed");
            softAssert.assertTrue(dashboardPage.paymentServiceList.size() != 0, "payment service list are not displayed");
        } else {
            logger.info("This user id belongs to NTB");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the overview section when account open banner is present")
    public void userVerifyTheOverviewSectionWhenAccountOpenBannerIsPresent() {
        softAssert = new SoftAssert();
        if (!dashboardPage.overViewSectionBannerVerify.isEmpty()) {

            for (WebElement overViewBannerIterate : dashboardPage.overViewSectionBannerVerify) {
                String currentDashboardWindow = driver.getWindowHandle();
                boolean openCurrentAccountPopup = false;
                overViewBannerIterate.click();
                waitTillInvisibilityOfLoader(driver);
                try {
                    openCurrentAccountPopup = dashboardPage.openCurrentAccPopupButton.isDisplayed();
                } catch (NoSuchElementException e) {
                }
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl.contains(TextAssertion.dashBoardUrlPath)) {
                    if (openCurrentAccountPopup == true) {
                        logger.info("The user doesn't have CASA Open account pop up should display");
                        softAssert.assertTrue(dashboardPage.openCurrentAccPopupButton.isDisplayed(), "open current account pop up button not displayed");
                        dashboardPage.cancelPopupButton.click();
                        waitForPageLoad(driver);
                    } else {
                        for (String newWindow : driver.getWindowHandles()) {
                            if (!newWindow.equals(currentDashboardWindow)) {
                                driver.switchTo().window(newWindow);
                                logger.info("This id doesn't have any Casa Account new page should open");
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.applyNowPageUrlPath), "account open page not displayed");
                            }
                        }
                        driver.close();
                        driver.switchTo().window(currentDashboardWindow);
                        waitTillInvisibilityOfLoader(driver);
                    }
                } else if (driver.getCurrentUrl().contains(TextAssertion.applyForLoanPageUrlPath)) {
                    logger.info("The user doesn't have loan account,so it should navigates to the apply for loan page");
                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Apply for Loans"), "loan apply page not be the same");
                    dashboardPage.homePageLink.click();
                    waitTillInvisibilityOfLoader(driver);
                }
            }
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the overview section ensuring that details for available accounts are displayed")
    public void userVerifyTheOverviewSectionEnsuringThatDetailsForAvailableAccountsAreDisplayed() {
        softAssert = new SoftAssert();
        String availableBalInDashboardPage = null;
        int savingAccountsInNo = 0;
        int currentAccountInNo = 0;
        int overDraftAccountInNo = 0;
        int dormantAccountInNo = 0;
        if (!dashboardPage.overViewSection.isEmpty()) {
            // if(dashboardPage.typeOfCustomer.getText().contains("Total Amount")){
            for (int i = 1; i <= dashboardPage.accountsContainsList.size(); i++) {
                dashboardPage.accountsContainsList(i);
                dashboardPage.hiddenIcon(i).click();
                availableBalInDashboardPage = dashboardPage.availableTotalBal(i).getText();
                /*** Total Available CASA Accounts ***/
                String casaDetailsDashBoardPage = dashboardPage.numberOfCASA.getText();
                if (casaDetailsDashBoardPage.contains(".")) {
                    String[] casaSplit = casaDetailsDashBoardPage.split("Account");
                    noSavingAccAvailableInDashBoardPage = casaSplit[0].replaceAll("[^0-9]", "").trim();
                    noCurrentAccAvailableInDashBoardPage = casaSplit[1].replaceAll("[^0-9]", "").replace(".", "").trim();
                    logger.info("Total No Saving Acc :" + casaDetailsDashBoardPage);
                    int noOfCurrentAccInDashboardPage = Integer.parseInt(noCurrentAccAvailableInDashBoardPage.replace("[Aa-Zz]", ""));
                    logger.info("Total No Saving Acc :" + noOfCurrentAccInDashboardPage);
                } else {
                    if (casaDetailsDashBoardPage.contains("Savings")) {
                        noSavingAccAvailableInDashBoardPage = casaDetailsDashBoardPage.replaceAll("[^0-9]", "").trim();
                        logger.info(noSavingAccAvailableInDashBoardPage);

                    } else if (casaDetailsDashBoardPage.contains("Current")) {
                        noCurrentAccAvailableInDashBoardPage = casaDetailsDashBoardPage.replaceAll("[^0-9]", "");
                        logger.info(noCurrentAccAvailableInDashBoardPage);
                    }
                }
                int numberOfCASA = 0;
                for (int j = 0; j < casaDetailsDashBoardPage.length(); j++) {
                    char stringChar = casaDetailsDashBoardPage.charAt(j);
                    if (Character.isDigit(stringChar)) {
                        numberOfCASA += Character.getNumericValue(stringChar);
                    }
                }
                setNoOfCasa(numberOfCASA);
                logger.info("This account have " + dashboardPage.numberOfCASA.getText());
                logger.info("The total no of casa is :" + numberOfCASA);

                /********* Deposit List ************/
                String depositHomeDashBoard = dashboardPage.numberOfDeposit.getText();
                if (depositHomeDashBoard.contains(".")) {
                    String[] depositSplit = depositHomeDashBoard.split("Deposit");
                    fixedDeposit = depositSplit[0].replaceAll("[a-zA-Z]", "").trim();
                    recurringDeposit = depositSplit[1].replaceAll("[a-zA-Z]", "").replace(".", "").trim();
                    taxSaver = depositSplit[2].replaceAll("[a-zA-Z]", "").replace(".", "").trim();
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher match = pattern.matcher(dashboardPage.numberOfDeposit.getText());
                    int noOfDepositInTheDashBoardPage = 0;
                    while (match.find()) {
                        noOfDepositInTheDashBoardPage += Integer.parseInt(match.group());
                    }
                    logger.info("Total no of deposit available in dashboard page By Pattern " + noOfDepositInTheDashBoardPage);
                    int totalNoOfDepositInDashBoardPage = Integer.parseInt(fixedDeposit + recurringDeposit + taxSaver);
                    //    logger.info("Total no of deposit available in dashboard page "+totalNoOfDepositInDashBoardPage);

                } else {
                    if (depositHomeDashBoard.contains("Fixed")) {
                        fixedDeposit = depositHomeDashBoard.replaceAll("[^a-zA-Z]", "");
                    } else if (depositHomeDashBoard.contains("Recurring")) {
                        recurringDeposit = depositHomeDashBoard.replaceAll("[^a-zA-Z]", "");
                    } else if (depositHomeDashBoard.contains("Tax Saver")) {
                        taxSaver = depositHomeDashBoard.replaceAll("[^a-zA-Z]", "");
                    }
                }
                logger.info(dashboardPage.accountsContainsList(i).getText() + " balance is in dashboard page " + availableBalInDashboardPage);
                dashboardPage.viewAllButton(i).click();
                waitTillLoading(driver);
                try {
                    if (driver.getCurrentUrl().contains(TextAssertion.accountSummaryHomeUrlPath)) {
                        logger.info("User is on account summary home page");
                        accountSummaryPage.hiddenIconTotalBalance.click();
                        String totalBalanceInAccountSummaryHomePage = accountSummaryPage.totalBalance.getText();
                        logger.info("Displayed Total balance in account summary home page is " + totalBalanceInAccountSummaryHomePage);
                        try {
                            savingAccountsInNo = accountSummaryPage.savingAccountList.size();
                            currentAccountInNo = accountSummaryPage.currentAccList.size();
                            overDraftAccountInNo = accountSummaryPage.overDraftAccList.size();
                            dormantAccountInNo = accountSummaryPage.dormantAccCountList.size();
                            logger.info("Total No of Saving Account in Account summary Home Page is" + savingAccountsInNo);
                            logger.info(overDraftAccountInNo);
                        } catch (NoSuchElementException e) {
                            logger.debug("One of the account is not available " + e.getMessage());
                        }
                        int noOfSavingAccAvailableInAccountSummaryPage = savingAccountsInNo - overDraftAccountInNo;
                        logger.info(totalBalanceInAccountSummaryHomePage);
                        logger.info(availableBalInDashboardPage);
                        logger.info("Total no of Accounts Available in account summary page  " + noOfSavingAccAvailableInAccountSummaryPage);
                        logger.info(noSavingAccAvailableInDashBoardPage);
                        logger.info(currentAccountInNo);
                        logger.info(noCurrentAccAvailableInDashBoardPage);
                        softAssert.assertEquals(totalBalanceInAccountSummaryHomePage, availableBalInDashboardPage, "total balance not be the same");
                        softAssert.assertEquals(savingAccountsInNo, Integer.parseInt(noSavingAccAvailableInDashBoardPage), "No of saving account not matched");
                        softAssert.assertEquals(currentAccountInNo, Integer.parseInt(noCurrentAccAvailableInDashBoardPage), "No of current account not matched");
                        softAssert.assertTrue(accountSummaryPage.detailsStatementQuickLink.isDisplayed(), "detailed statement quick link not displayed");
                        softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(), "money transfer quick link not displayed");
                        logger.info("Total balance showing in CASA home page " + totalBalanceInAccountSummaryHomePage);
                        logger.info("Total No of Saving accounts in Acc Summary Page  " + noOfSavingAccAvailableInAccountSummaryPage);
                        logger.info("Total No of Current accounts in Acc Summary Page " + currentAccountInNo);
                        logger.info("Total No of DormantAccount in Acc Summary Page " + dormantAccountInNo);

                        if (accountSummaryPage.savingAccountList.size() == 0) {
                            logger.info("This account doesn't have any saving Account");
                        } else if (accountSummaryPage.currentAccList.size() == 0) {
                            logger.info("This account doesn't have any current Account");
                        }

                    } else if (driver.getCurrentUrl().contains(TextAssertion.depositHomeUrlPath)) {
                        String depositTotal = depositsDashboardPage.totalDeposit.getText();
                        String fdList = depositsDashboardPage.noOfFdDeposit.getText();
                        softAssert.assertEquals(depositTotal, availableBalInDashboardPage, "total deposit amount not be the same");
                        softAssert.assertTrue(depositsDashboardPage.openNowButton.isDisplayed(), "Open now button not displayed");
                        softAssert.assertEquals(depositsDashboardPage.noOfFdDeposit.getText(), fdList, "no of available fd doesn't matched with displayed in dashboard");
                        softAssert.assertEquals(depositsDashboardPage.rdList.size(), Integer.parseInt(recurringDeposit), "no of available rd doesn't matched with displayed in dashboard");
                        softAssert.assertTrue(accountSummaryPage.detailsStatementQuickLink.isDisplayed(), "detailed statement quick link not displayed");
                        softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(), "money transfer quick link not displayed");
                        System.out.println("noOfFdDeposit" + depositsDashboardPage.noOfFdDeposit.getText() + fdList);
                        System.out.println("rdList" + depositsDashboardPage.rdList.size() + Integer.parseInt(recurringDeposit));
                        logger.info("Total deposit amount showing in deposit dashboard page is " + depositTotal);

                        int totalNoOfDepositInDashboardPage = depositsDashboardPage.totalNoOfDepositList.size();
                        int totalNoOfFdList = totalNoOfDepositInDashboardPage - depositsDashboardPage.totalNoOfRdList.size();
                        int totalNoOFRdList = depositsDashboardPage.totalNoOfRdList.size() - depositsDashboardPage.totalNoOfTaxSaverList.size();
                        int totalNoOFTsList = depositsDashboardPage.totalNoOfTaxSaverList.size();
                        softAssert.assertEquals(Integer.parseInt(fixedDeposit), totalNoOfFdList, "total no of FD list are not matched");
                        softAssert.assertEquals(Integer.parseInt(recurringDeposit), totalNoOFRdList, "total no of RD list are not matched");
                        try {
                            softAssert.assertEquals(Integer.parseInt(taxSaver), totalNoOFTsList, "total no of TS list are not matched");
                        } catch (NumberFormatException exception) {
                            logger.debug("Tax saver Number not displayed,it still hidden");
                        }
                        logger.info("Total no of FD on dash board page is " + fixedDeposit + " , on deposit dashboard page is " + totalNoOfFdList);
                        logger.info("Total no of RD on dash board page is " + recurringDeposit + " , on deposit dashboard page is " + totalNoOFRdList);
                        logger.info("Total no of TS on dash board page is " + taxSaver + " , on deposit dashboard page is " + totalNoOFTsList);


                    } else if (driver.getCurrentUrl().contains(TextAssertion.loanHomePageUrlPath)) {
                        if (loanPage.loanPageSectionOutstandingBalanceList.size() > 1) {
                            double outstandingBalanceListLoanPageStringDouble = 0;
                            double availableBalInDashboardPageInt = Double.parseDouble(availableBalInDashboardPage.replaceAll("₹", "").replaceAll(",", "").trim());
                            for (WebElement outstandingBalanceListLoanPage : loanPage.loanPageSectionOutstandingBalanceList) {
                                String outstandingBalanceListLoanPageString = outstandingBalanceListLoanPage.getText().replaceAll("₹", "").replaceAll(",", "").trim();
                                double iterationAdditionValue = Double.parseDouble(outstandingBalanceListLoanPageString);
                                outstandingBalanceListLoanPageStringDouble += iterationAdditionValue;
                                logger.info(outstandingBalanceListLoanPageStringDouble);
                            }
                            softAssert.assertEquals(outstandingBalanceListLoanPageStringDouble, availableBalInDashboardPageInt, "loan outstanding balance not be the same");
                            logger.info(String.valueOf(outstandingBalanceListLoanPageStringDouble));
                            logger.info(String.valueOf(availableBalInDashboardPageInt));


                        } else {
                            String outStandingLoanPage = loanPage.loanPageSectionOutstandingBalance.getText();
                            softAssert.assertEquals(outStandingLoanPage, availableBalInDashboardPage, "loan outstanding balance not be the same");
                        }
                    }
                } catch (NoSuchElementException e) {
                    //    attachScreenshot(driver, scenario);
                    //    softAssert.fail("Please verify the page ");
                    logger.info(e.toString());
                    logger.info("This url page not displayed " + driver.getCurrentUrl());
                    //    logger.info(dashboardPage.pageHeader.getText() + "page header not displayed");
                }
                waitTillLoading(driver);
                dashboardPage.homePageLink.click();
                // driver.navigate().back();
                waitTillLoading(driver);

                try {
                    softAssert.assertAll();

                } catch (AssertionError e) {
                    attachScreenshot(driver, scenario);
                    scenario.log(e.toString());
                    setErrorsInList(e.toString());
                }
            }
        }
    }

    @And("User verify the payment dashboard on Payment service section")
    public void userVerifyThePaymentDashboardOnPaymentServiceSection() {
        softAssert = new SoftAssert();
        if (dashboardPage.overViewSection.isEmpty()) {
            logger.info("NTB account doesn't have Payment service card section");
        } else {
            scrollIntoView(driver, dashboardPage.paymentService);
            dashboardPage.paymentDashBoardLink.click();
            waitTillLoading(driver);
            try {
                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.moneyTransferHomePageUrlPath), "money transfer url not be the same");
                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Money Transfer"), "money transfer home page header not be the same");
                //   driver.navigate().back();
                /** Naviagte to the dash board page**/
                dashboardPage.homePageLink.click();
                waitTillInvisibilityOfLoader(driver);
            } catch (NoSuchElementException e) {
                softAssert.fail("Money transfer page navigation is failed");
                logger.error("Navigation to the money transfer page is failed");
            }

        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the functions available in payment service sections")
    public void userVerifyTheFunctionsAvailableInPaymentServiceSections() {
        softAssert = new SoftAssert();
        try {
            scrollIntoView(driver, dashboardPage.paymentService);
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Payment service sections not displayed");
        }
        for (int i = 1; i <= dashboardPage.paymentServiceList.size(); i++) {
            //        scrollIntoView(driver, dashboardPage.paymentService);
            String paymentServiceSelectedList = dashboardPage.paymentServiceLists(i).getText();
            logger.info("User Navigated to  " + paymentServiceSelectedList + " page");
            dashboardPage.paymentServiceLists(i).click();
            waitTillInvisibilityOfLoader(driver);
            switch (paymentServiceSelectedList) {
                case "Pay to Payee":
                    moneyTransferStepDef.userVerifyTransferToPayeePage(); /** Here We verified by using methods**/
                    logger.info("User verified this page by using method ");
                    dashboardPage.homePageLink.click();
                    waitTillInvisibilityOfLoader(driver);
                    break;
                case "Quick A/c Transfer":
                    moneyTransferStepDef.userVerifyQuickAccountTransferPage();
                    logger.info("User verified this page by using method ");
                    dashboardPage.homePageLink.click();
                    waitTillInvisibilityOfLoader(driver);
                    break;
                case "Self Transfer":
                    if (getNoOfCasa() > 1) {
                        logger.info("User is on " + dashboardPage.pageHeader.getText());
                        logger.info("User verified this page by using method ");
                        moneyTransferStepDef.userVerifyTransferToSelfPage();
                        dashboardPage.homePageLink.click();
                        waitTillInvisibilityOfLoader(driver);
                    } else {
                        logger.info("The use doesn't have more than one account ,So self transfer function not enable");
                    }
                    break;
                case "Tax Payments":
                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.gstPageUrlPath), "tax payment page url not be the same");
                    softAssert.assertTrue(dashboardPage.pageHeader.isDisplayed(), paymentServiceSelectedList + " page header not displayed");
                    dashboardPage.homePageLink.click();
                    waitTillInvisibilityOfLoader(driver);
                    try {
                        softAssert.assertAll();

                    } catch (AssertionError | Exception e) {
                        attachScreenshot(driver, scenario);
                        scenario.log(e.toString());
                        setErrorsInList(e.toString());
                    }
                    break;
                case "Allowance":
                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.allowancePageUrlPath), "allowance page url not be the same");
                    softAssert.assertTrue(dashboardPage.pageHeader.isDisplayed(), paymentServiceSelectedList + " page header not displayed");
                    dashboardPage.homePageLink.click();
                    waitTillInvisibilityOfLoader(driver);
                    try {
                        softAssert.assertAll();

                    } catch (AssertionError | Exception e) {
                        attachScreenshot(driver, scenario);
                        scenario.log(e.toString());
                        setErrorsInList(e.toString());
                    }
                    break;
                case "Bill Payments":
                    logger.info(paymentServiceSelectedList + " " + dashboardPage.dashBoardPopUp.getText());
                    softAssert.assertTrue(dashboardPage.dashBoardPopUp.getText().contains("Please contact nearest branch"), "pop message not be the same");
                    softAssert.assertTrue(dashboardPage.dashBoardPopupOkButton.isDisplayed(), "pop ok button not be the same");
                    dashboardPage.dashBoardPopupOkButton.click();
                    try {
                        softAssert.assertAll();

                    } catch (AssertionError | Exception e) {
                        attachScreenshot(driver, scenario);
                        scenario.log(e.toString());
                        setErrorsInList(e.toString());
                    }
                    break;
                case "FasTag Recharge":
                    logger.info(paymentServiceSelectedList + " " + dashboardPage.dashBoardPopUp.getText());
                    softAssert.assertTrue(dashboardPage.dashBoardPopUp.getText().contains("Please contact nearest branch"), "pop message not be the same");
                    softAssert.assertTrue(dashboardPage.dashBoardPopupOkButton.isDisplayed(), "pop ok button not be the same");
                    dashboardPage.dashBoardPopupOkButton.click();
                    try {
                        softAssert.assertAll();

                    } catch (AssertionError | Exception e) {
                        attachScreenshot(driver, scenario);
                        scenario.log(e.toString());
                        setErrorsInList(e.toString());
                    }
                    break;
            }
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the side menu section")
    public void userVerifyTheSideMenuSection() {
        softAssert = new SoftAssert();
        clickOnButton(dashboardPage.homePageLink);
        if (dashboardPage.overViewSectionBannerVerify.size() == 3) {
            logger.info("This account doesn't have any CASA");
        } else if (!dashboardPage.overViewSection.isEmpty()) {
            try {
                if (dashboardPage.sideMenuExpandState.isDisplayed()) {
                    logger.info("Side menu is open state");
                }
            } catch (NoSuchElementException e) {
                logger.info("Side menu is close state");
                dashboardPage.sideMenuOpenButton.click();
                waitForPageLoad(driver);
            }
            ListIterator<WebElement> overViewContainerLists = dashboardPage.overViewSectionContainerListText.listIterator();
            while (overViewContainerLists.hasNext()) {
                WebElement overViewListIterate = overViewContainerLists.next();
                String overViewContainerHeader = overViewListIterate.getText();
                loanAccActive = overViewContainerHeader.contains("Outstanding Balance");
            }
            ListIterator<WebElement> subMenuIcon = dashboardPage.subMenuVisibleElements.listIterator();
            while (subMenuIcon.hasNext()) {
                WebElement subMenu = subMenuIcon.next();
                String dashBoardWindow = driver.getWindowHandle();
                int overviewContainerList = dashboardPage.overViewSection.size();
                subMenu.click();
                if (dashboardPage.overViewSectionBannerVerify.size() == 3) {
                    logger.info("This account doesn't have any casa");
                    dashboardPage.cancelPopupButton.click();
                } else {
                    for (int i = 1; i <= dashboardPage.subMenus.size(); i++) {
                        waitTillInvisibilityOfLoader(driver);
                        String navigatedToPage = dashboardPage.sideBarMenuAccounts(i).getText();
                        dashboardPage.sideBarMenuAccounts(i).click();
                        waitTillInvisibilityOfLoader(driver);
                        switch (navigatedToPage) {
                            case "Operative Accounts":
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.accountSummaryHomeUrlPath), "account summary page url not be the same");
                                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Bank Account"), "account summary page header not be the same");
                                softAssert.assertTrue(accountSummaryPage.detailsStatementQuickLink.isDisplayed(), "detailed statement quick link not displayed");
                                softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(), "money transfer quick link not displayed");
                                logger.info("User is on Account summary page");
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;
                            case "Loan Accounts":
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.loanHomePageUrlPath), "loan page url not be the same");
                                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Apply for Loans"), "loan page header not be the same");
                                //      logInPage.backButton.click();
                                logger.info("User is on loan home page");
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;

                            case "Deposit Accounts":
                                try {
                                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.depositHomeUrlPath), "deposit page url not be the same");
                                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Deposits"), "deposit page header not be the same");
                                    softAssert.assertTrue(depositsDashboardPage.openNowButton.isDisplayed(), "Open now button not displayed");
                                    softAssert.assertTrue(accountSummaryPage.detailsStatementQuickLink.isDisplayed(), "detailed statement quick link not displayed");
                                    softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(), "money transfer quick link not displayed");
                                    //    logInPage.backButton.click();
                                    dashboardPage.homePageLink.click();
                                    logger.info("User is on deposit dashboard page");
                                    waitTillInvisibilityOfLoader(driver);
                                } catch (NoSuchElementException e) {
                                    attachScreenshot(driver, scenario);
                                    softAssert.fail("Deposit Page header not displayed refer attached screen shot");
                                    logger.error("Deposit Page header not displayed");

                                }
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;

                            case "Money Transfer":
                                System.out.println(driver.getCurrentUrl());
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.moneyTransferHomePageUrlPath), "money transfer page url not be the same");
                                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Money Transfer"), "money transfer page header not be the same");
                                //    logInPage.backButton.click();
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;

                            case "Manage Payee":
                                System.out.println(driver.getCurrentUrl());
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.managePayeeHomePageUrlPath), "payees page url not be the same");
                                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Manage Payee"), "payees page header not be the same");
                                //    logInPage.backButton.click();
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;


                            case "GST Payment":
                                System.out.println(driver.getCurrentUrl());
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.taxPaymentPageUrlPath), "tax payments page url not be the same");
                                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("GST Payment"), "tax payments page header not be the same");
                                //    logInPage.backButton.click();
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;
                            case "Debit Cards":
                                try {
                                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.debitCardHomePageUrlPath), "debit card page url not be the same");
                                    softAssert.assertEquals(dashboardPage.pageHeader.getText(), "Debit Card Overview", "debit card payments page header not be the same");

                                } catch (NoSuchElementException exception) {
                                    attachScreenshot(driver, scenario);
                                    logger.error("Please check the screen shot ,Issues is debit card page");
                                }//    logInPage.backButton.click();
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;
                            case "Account Statement":
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.accountStatementPageUrlPath), "account Statement page url not be the same");
                                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Account Statements"), "account Statement  page header not be the same");
                                //    logInPage.backButton.click();
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;
                            case "Loan Statement ":
                                if (overviewContainerList >= 3) {
                                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.loanStatementPageUrlPath), "loan Statement page url not be the same");
                                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Loan Statement"), "loan Statement  page header not be the same");
                                } else {
                                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.applyForLoanPageUrlPath), "loan apply page url not be the same");
                                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Apply for Loans"), "loan apply page header not be the same");
                                    //  logInPage.backButton.click();
                                    logger.info("User is doesn't have any active loan so its navigated to loan apply page");
                                }
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;
                            case "Insurance":
                                try {
                                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.insurancePageUrlPath), "insurance path url not be the same");

                                    softAssert.assertTrue(dashboardPage.redirectToInsurancePortalPopUp.isDisplayed(), "insurance po up not displayed");
                                } catch (NoSuchElementException e) {
                                    attachScreenshot(driver, scenario);
                                    logger.error("Page header not displayed");
                                    //    softAssert.fail("Page header not displayed");
                                }
                                javaScriptExecutorClickElement(driver, dashboardPage.continueButton);
                                waitTillInvisibilityOfLoader(driver);
                                for (String windowHandle : driver.getWindowHandles()) {
                                    if (!windowHandle.equals(dashBoardWindow)) {
                                        driver.switchTo().window(windowHandle);
                                        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.insuranceNewDirectionPageUrl), "insurance redirection page url not be the same");

                                    }
                                }
                                driver.close();
                                driver.switchTo().window(dashBoardWindow);
                                if (driver.getWindowHandles().size() > 1) {
                                    logger.info("extra window there need to close");
                                    closeChildWindows(driver, dashBoardWindow);
                                }
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;
                            case "Investment":
                                try {
                                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.investPageUrlPath), "investment page url not be the same");
                                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Investment"), "investment  page header not be the same");
                                    softAssert.assertTrue(dashboardPage.investmentMenuList.size() == 4, "invest menu list showing");
                                    ListIterator<WebElement> investmentList = dashboardPage.investmentMenuList.listIterator();
                                    while (investmentList.hasNext()) {
                                        WebElement getTextName = investmentList.next();
                                        String investmentPageWindow = driver.getWindowHandle();
                                        String menuNameList = getTextName.getText();
                                        softAssert.assertTrue(dashboardPage.investMenuNavigationButton(menuNameList).isEnabled(), menuNameList + " is not enabled");
                                        waitTillInvisibilityOfLoader(driver);
                                    }
                                } catch (NoSuchElementException exce) {
                                    attachScreenshot(driver, scenario);
                                    logger.error("Elements are not displayed");
                                    exce.toString();
                                }
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
//                                ListIterator<WebElement> investmentList = dashboardPage.investmentMenuList.listIterator();
//                                while (investmentList.hasNext()) {
//                                    WebElement getTextName = investmentList.next();
//                                    String investmentPageWindow = driver.getWindowHandle();
//                                    String menuNameList = getTextName.getText();
//                                    staticWait(2000);
//
//                                    dashboardPage.investMenuNavigationButton(menuNameList).click();
//                                    waitTillInvisibilityOfLoader(driver);
//                                    try {
//                                        if (dashboardPage.toastMessage.getText().contains("omething")) {
//                                            //  attachScreenshot(driver, scenario);
//                                            softAssert.fail("Something went wrong toast screen appeared");
//                                        }
//                                    } catch (NoSuchElementException elements) {
//                                    }
//                                    switch (menuNameList) {
//                                        case "Apply for Mutual Funds":
//                                            try {
//                                                softAssert.assertTrue(dashboardPage.continueButton.isDisplayed(), "mutual funds portal pop up not displayed");
//                                                softAssert.assertTrue(dashboardPage.cancelPopupButton.isDisplayed(), "mutual funds portal pop up cancel button is not displayed");
//                                                dashboardPage.popUpContinueButton.click();
//                                                waitTillInvisibilityOfLoader(driver);
//                                                for (String mutualFundWindow : driver.getWindowHandles()) {
//                                                    if (!mutualFundWindow.equals(investmentPageWindow)) {
//                                                        driver.switchTo().window(mutualFundWindow);
//                                                        waitTillVisibilityOfUrl(driver, TextAssertion.mutualFundPageUrlPath);
//                                                        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.mutualFundPageUrlPath), "mutual fund window url not matched with expected");
//                                                    }
//                                                }
//                                                driver.close();
//                                                driver.switchTo().window(investmentPageWindow);
//                                            } catch (NoSuchElementException e) {
//                                                logger.error(e.getMessage());
//                                                attachScreenshot(driver, scenario);
//                                                softAssert.fail("Some elements not displayed");
//                                            }
//                                            break;
//                                        case "Apply for IPO":
//                                            try {
//                                                softAssert.assertTrue(dashboardPage.continueButton.isDisplayed(), "IPO portal pop up not displayed");
//                                                softAssert.assertTrue(dashboardPage.cancelPopupButton.isDisplayed(), "IPO portal pop up cancel button is not displayed");
//                                                waitTillElementToBeClickable(driver, dashboardPage.popUpContinueButton);
//                                                dashboardPage.popUpContinueButton.click();
//                                                waitTillInvisibilityOfLoader(driver);
//                                                for (String ipoWindow : driver.getWindowHandles()) {
//                                                    if (!ipoWindow.equals(investmentPageWindow)) {
//                                                        driver.switchTo().window(ipoWindow);
//                                                        softAssert.assertTrue(driver.getCurrentUrl().contains("ipo-onnet-aub/api/login"), "IPO window url not matched");
//                                                    }
//                                                }
//                                                driver.close();
//                                                driver.switchTo().window(investmentPageWindow);
//                                            } catch (NoSuchElementException e) {
//                                                logger.error(e.getMessage());
//                                                attachScreenshot(driver, scenario);
//                                                softAssert.fail("Some elements not displayed");
//                                            }
//
//                                            break;
//                                        case "Invest in Equity Trading":
//                                            try {
//                                                softAssert.assertTrue(dashboardPage.continueButton.isDisplayed(), "Equity pop up not displayed");
//                                                softAssert.assertTrue(dashboardPage.cancelPopupButton.isDisplayed(), "Equity pop up cancel button is not displayed");
//                                                waitTillElementToBeClickable(driver, dashboardPage.popUpContinueButton);
//                                                dashboardPage.popUpContinueButton.click();
//                                                waitTillInvisibilityOfLoader(driver);
//                                                for (String equityTrading : driver.getWindowHandles()) {
//                                                    if (!equityTrading.equals(investmentPageWindow)) {
//                                                        driver.switchTo().window(equityTrading);
//                                                        //                        softAssert.assertTrue(driver.getCurrentUrl().contains("AU_Bank/AULandingPage1.html"), "Equity window url not matched");
//                                                    }
//                                                }
//                                                driver.close();
//                                                driver.switchTo().window(investmentPageWindow);
//
//                                            } catch (NoSuchElementException e) {
//                                                logger.error(e.getMessage());
//                                                attachScreenshot(driver, scenario);
//                                                softAssert.fail("Some elements not displayed");
//                                            }
//                                            break;
//                                        case "Invest in NPS":
//                                            try {
//                                                for (String npsWindow : driver.getWindowHandles()) {
//                                                    if (!npsWindow.equals(investmentPageWindow)) {
//                                                        driver.switchTo().window(npsWindow);
//                                                        softAssert.assertTrue(driver.getCurrentUrl().contains("personal-banking/national-pension-system"), "mutual fund window url not matched");
//                                                    }
//                                                }
//                                                driver.close();
//                                                driver.switchTo().window(investmentPageWindow);
//                                            } catch (NoSuchElementException e) {
//                                                logger.error(e.getMessage());
//                                                attachScreenshot(driver, scenario);
//                                                softAssert.fail("Some elements not displayed");
//                                            }
//                                            break;
//                                    }
//                                }

                                break;

                            case "Deposits":
                                try {
                                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.depositHomeUrlPath), "deposit page url not be the same");
                                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains(""), "deposit  page header not be the same");
                                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Deposits"), "deposit page header not be the same");
                                    softAssert.assertTrue(depositsDashboardPage.openNowButton.isDisplayed(), "Open now button not displayed");
                                    softAssert.assertTrue(accountSummaryPage.detailsStatementQuickLink.isDisplayed(), "detailed statement quick link not displayed");
                                    softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(), "money transfer quick link not displayed");
                                    //    logInPage.backButton.click();

                                } catch (NoSuchElementException e) {
                                    attachScreenshot(driver, scenario);
                                    softAssert.fail("Deposit Page header not displayed refer attached screen shot");
                                    logger.error("Deposit page header not displayed");
                                }
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;

                            case "Raise New Request":
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.serviceRequestUrl), "raise new request page url not be the same");
                                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Service Request"), "raise new request  page header not be the same");
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;

                            case "Track Request":
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.trackServiceRequestPageUrl), "track request page url not be the same");
                                softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Track Service Request"), "track request  page header not be the same");
                                try {
                                    softAssert.assertAll();

                                } catch (AssertionError | Exception e) {
                                    attachScreenshot(driver, scenario);
                                    scenario.log(e.toString());
                                    setErrorsInList(e.toString());
                                }
                                break;
                        }
                    }

                }
                subMenu.click();
                dashboardPage.homePageLink.click();
                waitTillInvisibilityOfLoader(driver);
            }
        }

    }

    @And("User verify the menus details in apply now page")
    public void userVerifyTheMenusDetailsInApplyNowPage() {
        softAssert = new SoftAssert();
        dashboardPage.applyNowMenu.click();
        waitTillInvisibilityOfLoader(driver);
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.applyNowPageUrlPath), "apply now page url not be the same");
        softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Apply Now"), "apply now page header is not be the same");
        softAssert.assertTrue(!dashboardPage.applyNowMenusText.isEmpty(), "apply now menus are not displayed");
        softAssert.assertTrue(dashboardPage.applyNowMenusNavigateButton.size() == 5, "apply now all menus are not displayed please verify the menu list");
//        dashboardPage.homePageLink.click();
//        waitTillInvisibilityOfLoader(driver);
//        scrollIntoView(driver,dashboardPage.applyNowMenu);
//        scrollByAction(driver, dashboardPage.homePageLink);
//        scrollToEndByAction(driver);
//        dashboardPage.applyNowMenu.click();
        waitTillInvisibilityOfLoader(driver);
        for (int i = 0; i < dashboardPage.applyNowMenusText.size(); i++) {
            WebElement applyNowMenuHeader = dashboardPage.applyNowMenusText.get(i);
            String applyNowMenusHeader = applyNowMenuHeader.getText();
            WebElement applyMenuNavigateButton = dashboardPage.applyNowMenusNavigateButton.get(i);
            logger.info(applyNowMenusHeader);
            String currentWindow = driver.getWindowHandle();
            applyMenuNavigateButton.click();
            waitTillInvisibilityOfLoader(driver);
            switch (applyNowMenusHeader) {
                case "Business Banking":
                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.businessBankingPageUrlPath), applyNowMenusHeader + " page url path name not be the same");
                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Apply for " + applyNowMenusHeader), applyNowMenusHeader + " page header not be the same");
                    softAssert.assertTrue(dashboardPage.proceedOrBookLockerButton.isDisplayed(), "proceed button is not displayed");
                    dashboardPage.applyNowMenu.click();
                    waitTillInvisibilityOfLoader(driver);
                    break;
                case "Locker":
                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.lockerPageUrlPath), applyNowMenusHeader + " page url path name not be the same");
                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Apply for " + applyNowMenusHeader), applyNowMenusHeader + " page header not be the same");
                    softAssert.assertTrue(dashboardPage.proceedOrBookLockerButton.isDisplayed(), "book locker button is not displayed");
                    dashboardPage.applyNowMenu.click();
                    waitTillInvisibilityOfLoader(driver);
                    break;
                case "Current Account":
                    for (String currentAccountWindow : driver.getWindowHandles()) {
                        if (!currentAccountWindow.equals(currentWindow)) {
                            driver.switchTo().window(currentAccountWindow);
                            waitForPageLoad(driver);
                            try {
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.currentAndSavingAccountPageUrlPath), applyNowMenusHeader + " page url not be the same");
                                //   softAssert.assertTrue(dashboardPage.currentAccountOpenPageHeader.getText().contains(applyNowMenusHeader), applyNowMenusHeader + "page header not be the same");
                            } catch (NoSuchElementException e) {
                                attachScreenshot(driver, scenario);
                                logger.error(applyNowMenusHeader + " opening online page not displayed");
                            }
                            driver.close();
                            staticWait(2000);
                            driver.switchTo().window(currentWindow);
                            staticWait(2000);
                        }
                    }
                    break;
                case "Savings Account":
                    for (String savingAccountWindow : driver.getWindowHandles()) {
                        if (!savingAccountWindow.equals(currentWindow)) {
                            driver.switchTo().window(savingAccountWindow);
                            waitTillInvisibilityOfLoader(driver);
                            try {
                                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.savingAccountPageUrlPath), applyNowMenusHeader + " page url not be the same");
                                softAssert.assertTrue(dashboardPage.savingsAccountOpenPageHeader.getText().contains(applyNowMenusHeader), applyNowMenusHeader + "page header not be the same");
                            } catch (NoSuchElementException e) {
                                attachScreenshot(driver, scenario);
                                logger.error(applyNowMenusHeader + " opening online page not displayed");
                            }
                            driver.close();
                            driver.switchTo().window(currentWindow);
                            staticWait(2000);
                        }
                    }
                    break;
/****Fast Tag Options Removed On 03-02-2024 ****/

                case "Business Loan":
                    logger.debug(applyNowMenusHeader + "Inspect option not displayed");
                    staticWait(2000);
                    for (String newWindow : driver.getWindowHandles()) {
                        if (!newWindow.equals(currentWindow)) {
                            driver.switchTo().window(newWindow);
                            waitForPageLoad(driver);
                            try {
                                waitTillInvisibilityOfLoader(driver);
                                //      softAssert.assertEquals(driver.getCurrentUrl(),TextAssertion.businessLoanPageUrlPath, applyNowMenusHeader + " page url not be the same");
                                //            softAssert.assertTrue(dashboardPage.currentAccountOpenPageHeader.getText().contains(applyNowMenusHeader), applyNowMenusHeader + "page header not be the same");

                            } catch (NoSuchElementException e) {
                                attachScreenshot(driver, scenario);
                                logger.error(applyNowMenusHeader + " opening online page not displayed");
                            }
                            driver.close();
                            driver.switchTo().window(currentWindow);
                            break;

                        }
                    }

            }
        }
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();

        } catch (AssertionError | Exception e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User verify the video banking menu")
    public void userVerifyTheVideoBankingMenu() {
        softAssert = new SoftAssert();
        scrollIntoView(driver, dashboardPage.videoBankingMenu);
        waitTillVisibilityElement(driver, dashboardPage.videoBankingMenu);
        String dashboardWindow = driver.getWindowHandle();
        highlight(driver, dashboardPage.videoBankingMenu);
        //   javaScriptExecutorClickElement(driver, dashboardPage.videoBankingMenu);
        clickAndHighlight(driver, dashboardPage.videoBankingMenu);
        waitTillInvisibilityOfLoader(driver);
        for (String videoBankWindow : driver.getWindowHandles()) {
            if (!videoBankWindow.equals(dashboardWindow)) {
                driver.switchTo().window(videoBankWindow);
                waitTillVisibilityOfUrl(driver, driver.getCurrentUrl().concat("auth/initiateVBApp"));
                softAssert.assertTrue(driver.getCurrentUrl().contains("auth/initiateVBApp"), "video banking url not matched");
            }
        }
        driver.close();
        driver.switchTo().window(dashboardWindow);

    }

    @And("User verify the store dashboard side menu section")
    public void userVerifyTheStoreDashboardSideMenuSection() {
        softAssert = new SoftAssert();
        ListIterator<WebElement> sideMenu = dashboardPage.storeDashBoardSideBar.listIterator();
        while (sideMenu.hasNext()) {
            WebElement sideMenuList = sideMenu.next();
            String sideMenuIterator = sideMenuList.getText();
            sideMenuList.click();
            switch (sideMenuIterator) {
                case "Collections":
                    try {
                        if (dashboardPage.dashBoardPopUp.getText().contains("You don't have any QR")) {
                            logger.info("The user doesn't have QR");
                            softAssert.assertTrue(dashboardPage.storeSideMenuPopUpApplyButton.isDisplayed(), "apply button not displayed");
                            //      dashboardPage.sideMenuCloseButton.click();
                        } else if (dashboardPage.dashBoardPopUp.getText().contains("Oops!")) {
                            logger.info("The user application is not complete,After completing your AU QR journey to enable this feature");
                            softAssert.assertTrue(dashboardPage.storeSideMenuPopUpResumeApplicationButton.isDisplayed(), "apply button not displayed");
                            //       dashboardPage.storeSideMenuPopUpResumeApplicationButton.click();
                            dashboardPage.storeSideMenuPopUpClose.click();

                        } else if (dashboardPage.pageHeader.getText().contains("qrCollection")) {
                            logger.info("The use have QR code");
                            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.qrCollectionPageUrlPath), "url not be the same");
                        }
                    } catch (NoSuchElementException e) {
                    }
                    softAssert.assertTrue(sideMenuList.isDisplayed(), "collection menu not displayed");
                    break;

                case "Settlements":
                    try {
                        if (dashboardPage.dashBoardPopUp.getText().contains("You don't have any QR")) {
                            logger.info("The user does have QR");
                            softAssert.assertTrue(dashboardPage.storeSideMenuPopUpApplyButton.isDisplayed(), "apply button not displayed");
                            //      dashboardPage.sideMenuCloseButton.click();
                        } else if (dashboardPage.dashBoardPopUp.getText().contains("Oops!")) {
                            logger.info("The user application is not complete,After completing your AU QR journey to enable this feature");
                            softAssert.assertTrue(dashboardPage.storeSideMenuPopUpResumeApplicationButton.isDisplayed(), "apply button not displayed");
                            //      dashboardPage.storeSideMenuPopUpResumeApplicationButton.click();
                            dashboardPage.storeSideMenuPopUpClose.click();

                        } else if (dashboardPage.pageHeader.getText().contains("qrCollection")) {
                            logger.info("The user have QR code");
                            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.qrSettlementPageUrlPath), "url not be the same");
                        }
                        softAssert.assertTrue(sideMenuList.isDisplayed(), "Settlements menu not displayed");
                        break;
                    } catch (NoSuchElementException e) {
                        logger.error(e.toString());
                    }
            }
            try {
                softAssert.assertAll();

            } catch (AssertionError | Exception e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
            try {
                dashboardPage.storeSideMenuPopUpClose.click();
            } catch (NoSuchElementException e) {
            }
        }
    }

    @And("User verify the manage card section in store dashboard screen")
    public void userVerifyTheManageCardSectionInStoreDashboardScreen() {
        softAssert = new SoftAssert();
        ListIterator<WebElement> manageCardList = dashboardPage.storeDashboardManageCardSection.listIterator();
        while (manageCardList.hasNext()) {
            WebElement manageCardDetails = manageCardList.next();
            //      scrollIntoView(driver,manageCardDetails);
            String manageCardMenuList = manageCardDetails.getText();
            switch (manageCardMenuList) {
                case "Users":
                    softAssert.assertTrue(manageCardDetails.isDisplayed(), "users menu not displayed");
                    break;
                case "Reports":
                    softAssert.assertTrue(manageCardDetails.isDisplayed(), "reports menu not displayed");
                    break;
                case "Invoice":
                    softAssert.assertTrue(manageCardDetails.isDisplayed(), "invoice menu not displayed");
                    break;
            }
            try {
                softAssert.assertAll();

            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        }
    }

    @And("User verify the emergency assistance tab")
    public void userVerifyTheEmergencyAssistanceTab() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(dashboardPage.emergencyAssTab.isDisplayed(), "emergency dashboard is not displayed");
        dashboardPage.emergencyAssTab.click();
        waitTillInvisibilityOfLoader(driver);
        softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Support"), "emergency assistance page header not matched");
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.emergencyAssistancePath), "emergency assistance page url path not matched");
        softAssert.assertTrue(dashboardPage.backButton.isDisplayed(), "emergency assistance page back button is not displayed");
        softAssert.assertTrue(dashboardPage.emailUsButton.isDisplayed(), "email us button is not displayed");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the redirection in the emergency assistance page")
    public void userVerifyTheRedirectionInTheEmergencyAssistancePage() {
        softAssert = new SoftAssert();
        for (int i = 1; i < dashboardPage.emergencyAssitanceList.size(); i++) {
            WebElement disputeFaq = dashboardPage.emergencyAssitanceList.get(i);
            String optionHeader = disputeFaq.getText();
            String emergencyHomePage = driver.getWindowHandle();
            logger.info("User Navigates to " + optionHeader);
            waitTillInvisibilityOfLoader(driver);
            switch (optionHeader) {
                case "Fraud & Dispute":
                    clickOnButton(dashboardPage.emergencyNavigateButton(optionHeader));
                    waitTillInvisibilityOfLoader(driver);
                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.profileReportTransactionPageUrlPath), optionHeader + "page url path not be the same");
                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Report Transactions"), "page header name not be the same");
                    dashboardPage.backButton.click();
                    waitTillInvisibilityOfLoader(driver);
                    break;
                case "FaQs":
                    clickOnButton(dashboardPage.emergencyNavigateButton(optionHeader));
                    waitTillInvisibilityOfLoader(driver);
                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.faqPageUrlPath), optionHeader + "page url path not be the same");
                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("FaQs"), optionHeader + "page header not be the same");
                    softAssert.assertTrue(dashboardPage.helpAndSupportButton.isDisplayed(), "help and support button is not displayed");
                    softAssert.assertTrue(dashboardPage.bankBranchSearchBar.isDisplayed(), "bank branch search bar is not displayed");
                    dashboardPage.backButton.click();
                    waitTillInvisibilityOfLoader(driver);
                    break;
                case "VideoBanking":
                    clickOnButton(dashboardPage.emergencyNavigateButton("Other Way"));
                    waitTillANewWindowOpens(driver);
                    for (String windowHandle : driver.getWindowHandles()) {
                        if (!windowHandle.equals(emergencyHomePage)) {
                            driver.switchTo().window(windowHandle);
                        }
                    }
                    driver.close();
                    waitTillVisibilityTittle(driver,TextAssertion.tittle);
                    driver.switchTo().window(emergencyHomePage);
                    break;
                case "WhatsApp Banking":
                    clickOnButton(dashboardPage.emergencyNavigateButton(optionHeader));
                    waitTillInvisibilityOfLoader(driver);
                    softAssert.assertTrue(dashboardPage.whatsAppBankingQRCodePopUPQRCode.isDisplayed(), "whats app QR code image is not displayed");
               //     softAssert.assertTrue(dashboardPage.openWhatsAppButton.isDisplayed(), "open whats app button is not displayed");
                    softAssert.assertTrue(dashboardPage.closeButton.isDisplayed(), "close button is not displayed");
                    dashboardPage.closeButton.click();
                    break;
                case "AURO":
                    clickOnButton(dashboardPage.emergencyNavigateButton(optionHeader));
                    waitTillInvisibilityOfLoader(driver);
                    for (String windowHandle : driver.getWindowHandles()) {
                        if (!windowHandle.equals(emergencyHomePage)) {
                            driver.switchTo().window(windowHandle);
                            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.auroPageUrlPath), "auro page url is not identical");
                            String pageTitle=driver.getTitle();
                            logger.info("AURO Window title is :"+pageTitle);
                       //     softAssert.assertTrue(driver.getTitle().contains("AURO"), "auro page title not matched");
                        }
                    }
                    driver.close();
                    waitTillVisibilityTittle(driver,TextAssertion.tittle);
                    driver.switchTo().window(emergencyHomePage);
                    break;
                case "ATM & Branch Locator":
                    clickOnButton(dashboardPage.atmAndBranchLocatorNavigation);
                    waitTillInvisibilityOfLoader(driver);
                    for (String windowHandle : driver.getWindowHandles()) {
                        if (!windowHandle.equals(emergencyHomePage)) {
                            driver.switchTo().window(windowHandle);
                            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.atmAndBranchLocatorUrlPath), "atm and branch locator page url is not identical");
                            String pageTitle=driver.getTitle();
                            logger.info("ATM & Branch Locator Window title is :"+pageTitle);
                    //        softAssert.assertTrue(driver.getTitle().contains("Find your nearest branches"), "arm and branch locator page title not matched");
                        }
                    }
                    driver.close();
                    waitTillVisibilityTittle(driver,TextAssertion.tittle);
                    driver.switchTo().window(emergencyHomePage);
                    break;
            }
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the side menu bar visibility in expand function")
    public void userVerifyTheSideMenuBarVisibilityInExpandFunction() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, dashboardPage.homePageLink);
        sideMenuCloseState = false;
        try {
            dashboardPage.sideMenuOpenButton.click();
            sideMenuCloseState = dashboardPage.sideMenuExpandState.isDisplayed();
        } catch (NoSuchElementException e) {
            logger.debug("Side menu is in closed condition so need to expand");
        }
        if (sideMenuCloseState == true) {
            logger.info("Side menu is on expand state");
            softAssert.assertTrue(dashboardPage.storeDashBoardSideBar.size() != 0, "side menu bar List not displayed");
            softAssert.assertTrue(dashboardPage.emergencyAssTab.isDisplayed(), "emergency tab is not displayed");
            waitTillElementToBeClickable(driver, dashboardPage.sideMenuCloseButton);
            dashboardPage.sideMenuCloseButton.click();
        } else {
            logger.debug("Side menu is in closed state");
        }

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the side menu bar visibility in closed state")
    public void userVerifyTheSideMenuBarVisibilityInClosedState() {
        softAssert = new SoftAssert();
        sideMenuCloseState = false;
        try {
            dashboardPage.sideMenuCloseButton.click();
            sideMenuCloseState = dashboardPage.sideMenuExpandState.isDisplayed();
        } catch (NoSuchElementException e) {
            logger.debug("Side menu is in closed condition so need to expand");
        }
        if (sideMenuCloseState == true) {
            logger.info("Side menu is hidden state");
            softAssert.assertFalse(dashboardPage.storeDashBoardSideBar.size() != 0, "side menu bar List displayed");
            softAssert.assertFalse(dashboardPage.emergencyAssTab.isDisplayed(), "emergency tab is displayed");
            dashboardPage.sideMenuOpenButton.click();

        } else {
            logger.debug("Side menu is in open state");
        }
        dashboardPage.sideMenuOpenButton.click();
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }


    }

    @And("User verify the recent and scheduled transactions section")
    public void userVerifyTheRecentAndScheduledTransactionsSection() {
        softAssert = new SoftAssert();
        if (dashboardPage.overViewSection.isEmpty()) {
            logger.info("NTB account doesn't have any transaction details");
        } else {
            softAssert.assertTrue(dashboardPage.recentTransaction.isDisplayed(), "recent transaction header text not displayed");
            softAssert.assertTrue(dashboardPage.scheduledTransaction.isDisplayed(), "scheduled transaction header not displayed");
            softAssert.assertTrue(dashboardPage.recentTransactionSection.isDisplayed(), "scheduled transaction section not displayed");
            softAssert.assertTrue(dashboardPage.scheduledTransactionSection.isDisplayed(), "scheduled transaction section not displayed");
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User verify the Manage card section in bank dashboard screen")
    public void userVerifyTheManageCardSectionInBankDashboardScreen() {
        softAssert = new SoftAssert();

        try {
            scrollIntoView(driver, dashboardPage.manageCardTextDashBoardPage);
            manageCardVisibility = dashboardPage.manageCardTextDashBoardPage.isDisplayed();
        } catch (NoSuchElementException e) {
            manageCardVisibility = false;
        }
        System.out.println(manageCardVisibility);
        if (manageCardVisibility == true) {
            scrollIntoView(driver, dashboardPage.manageCardTextDashBoardPage);
            int noOfCardsAvailable = 0;
            ListIterator<WebElement> manageCards = dashboardPage.manageCardFind.listIterator();
            while (manageCards.hasNext()) {
                WebElement manageCardHeader = manageCards.next();
                manageCardIsDisplayed = manageCardHeader.getText().contains("Manage Cards");
            }
            if (manageCardIsDisplayed = true) {
                int j = 1;
                noOfCardsAvailable = dashboardPage.cardsAvailable.size();
                for (int i = 1; i <= dashboardPage.cardsAvailable.size(); i++) {
                    scrollIntoView(driver, dashboardPage.manageCardTextDashBoardPage);
                    int noOfCardDisplayed = dashboardPage.cardsAvailable.size();
                    String cardName = dashboardPage.cardsDetailsInDashBoardIndex(j).getText();
                    String endingCardNumber = dashboardPage.cardsDetailsInDashBoardIndex(j + 1).getText().replaceAll("[^\\d.]", "");
                    logger.info(cardName);
                    logger.info(endingCardNumber);
                    j = i + 2;
                    dashboardPage.cardsDetailsIndex(i).click();
                    waitTillInvisibilityOfLoader(driver);
                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.debitCardHomePageUrlPath), "debit card page url not displayed");
                    softAssert.assertTrue(dashboardPage.pageHeader.getText().contains("Debit Card Management"), "debit card page header not displayed");
                    softAssert.assertEquals(debitCardPage.cardLists.size(), noOfCardDisplayed, "no of debit card in dashboard and debit card home page not be the same");
                    dashboardPage.homePageLink.click();
                    waitTillInvisibilityOfLoader(driver);
                }
            } else {
                logger.info("There is no card available");
            }

            logger.info("In Manage card section have " + noOfCardsAvailable + " cards");
        } else {
            logger.info("Mange card section not displayed");
        }

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }


    @And("User verify the investment page navigation details")
    public void userVerifyTheInvestmentPageNavigationDetails() {
        boolean errorToastMessageDisplay=false;
        softAssert = new SoftAssert();
        if (dashboardPage.investAndInsuranceDownIcon.getAttribute("class").contains("icon-class-rotate-down")) {
            dashboardPage.investAndInsuranceDownIcon.click();
        }
        dashboardPage.investmentMenu.click();
        waitTillInvisibilityOfLoader(driver);
        String investmentPageWindow = driver.getWindowHandle();
        ListIterator<WebElement> investmentList = dashboardPage.investmentMenuList.listIterator();
        investmentList.next();
        while (investmentList.hasNext()) {
            WebElement getTextName = investmentList.next();
            String menuNameList = getTextName.getText();
            staticWait(2000);
            logger.info("Now we navigates to the "+menuNameList);
            dashboardPage.investMenuNavigationButton(menuNameList).click();
            try {
                if (dashboardPage.toastMessage.isDisplayed()) {
                    errorToastMessageDisplay = true;
                    String toastMessage = dashboardPage.toastMessage.getText();
                    attachScreenshot(driver, scenario);
                    waitTillInvisibilityElement(driver,dashboardPage.toastMessage);
                    softAssert.fail("Toast Message is appeared " + toastMessage);

                }
            } catch (NoSuchElementException e) {
            }
            if (errorToastMessageDisplay == false) {

                switch (menuNameList) {
                    case "Apply for Mutual Funds":
                        try {
                            softAssert.assertTrue(dashboardPage.continueButton.isDisplayed(), menuNameList + " pop up not displayed");
                            softAssert.assertTrue(dashboardPage.cancelPopupButton.isDisplayed(), menuNameList + " mutual funds portal pop up cancel button is not displayed");
                            dashboardPage.popUpContinueButton.click();
                            waitTillInvisibilityOfLoader(driver);
                            for (String mutualFundWindow : driver.getWindowHandles()) {
                                if (!mutualFundWindow.equals(investmentPageWindow)) {
                                    driver.switchTo().window(mutualFundWindow);
                                    softAssert.assertTrue(driver.getCurrentUrl().contains("client-dashboard"), "mutual fund window url not matched");
                                }
                            }
                            softAssert.assertAll();
                        } catch (NoSuchElementException noElement) {
                            attachScreenshot(driver, scenario);
                            softAssert.fail(menuNameList + " continue pop up not displayed");
                        } catch (AssertionError e) {
                            attachScreenshot(driver, scenario);
                            scenario.log(e.toString());
                            setErrorsInList(e.toString());

                        }
                        try {
                            driver.close();
                            driver.switchTo().window(investmentPageWindow);
                        } catch (Exception e) {

                        }
                        break;
                    case "Apply for IPO":
                        try {
                            softAssert.assertTrue(dashboardPage.continueButton.isDisplayed(), menuNameList + " pop up not displayed");
                            softAssert.assertTrue(dashboardPage.cancelPopupButton.isDisplayed(), menuNameList + " pop up cancel button is not displayed");
                            waitTillElementToBeClickable(driver, dashboardPage.popUpContinueButton);
                            dashboardPage.popUpContinueButton.click();
                            waitTillInvisibilityOfLoader(driver);
                            for (String ipoWindow : driver.getWindowHandles()) {
                                if (!ipoWindow.equals(investmentPageWindow)) {
                                    driver.switchTo().window(ipoWindow);
                                    softAssert.assertTrue(driver.getCurrentUrl().contains("ipo-onnet-aub/api/login"), "IPO window url not matched");
                                }
                            }
                            softAssert.assertAll();
                        } catch (NoSuchElementException noElement) {
                            attachScreenshot(driver, scenario);
                            softAssert.fail(menuNameList + " continue pop up not displayed");
                        } catch (AssertionError e) {
                            attachScreenshot(driver, scenario);
                            scenario.log(e.toString());
                            setErrorsInList(e.toString());

                        }
                        if (driver.getWindowHandles().size() > 1) {
                            driver.close();
                            driver.switchTo().window(investmentPageWindow);
                        }
                        break;
                    case "Invest in Equity Trading":
                        try {
                            softAssert.assertTrue(dashboardPage.continueButton.isDisplayed(), menuNameList + " pop up not displayed");
                            softAssert.assertTrue(dashboardPage.cancelPopupButton.isDisplayed(), menuNameList + " pop up cancel button is not displayed");
                            waitTillElementToBeClickable(driver, dashboardPage.popUpContinueButton);
                            dashboardPage.popUpContinueButton.click();
                            waitTillInvisibilityOfLoader(driver);

                            for (String equityTrading : driver.getWindowHandles()) {
                                if (!equityTrading.equals(investmentPageWindow)) {
                                    driver.switchTo().window(equityTrading);
                                    softAssert.assertTrue(driver.getCurrentUrl().contains("AU_Bank/AULandingPage1.html"), "Equity window url not matched");
                                }
                            }
                            softAssert.assertAll();
                        } catch (NoSuchElementException noElement) {
                            softAssert.fail(menuNameList + " continue pop up not displayed");
                        } catch (AssertionError e) {
                            attachScreenshot(driver, scenario);
                            scenario.log(e.toString());
                            setErrorsInList(e.toString());

                        }
                        if (driver.getWindowHandles().size() > 1) {
                            driver.close();
                            driver.switchTo().window(investmentPageWindow);
                        }
                        break;
                    case "Invest in NPS":
                        for (String npsWindow : driver.getWindowHandles()) {
                            if (!npsWindow.equals(investmentPageWindow)) {
                                driver.switchTo().window(npsWindow);
                                softAssert.assertTrue(driver.getCurrentUrl().contains("AU_Bank/AULandingPage1.html"), "NPS window url not matched");
                            }
                        }

                        try {
                            softAssert.assertAll();
                        } catch (AssertionError e) {
                            attachScreenshot(driver, scenario);
                            scenario.log(e.toString());
                            setErrorsInList(e.toString());

                        }
                        driver.close();
                        driver.switchTo().window(investmentPageWindow);
                        break;
                }
            }
        }

//            try {
//                softAssert.assertAll();
//            } catch (AssertionError e) {
//                attachScreenshot(driver, scenario);
//                scenario.log(e.toString());
//                setErrorsInList(e.toString());
//
//        }
        if (dashboardPage.investAndInsuranceDownIcon.getAttribute("class").contains("icon-class-rotate-up")) {
            dashboardPage.investAndInsuranceDownIcon.click();
        }
    }

    @And("User read the cif id and account holder name on profile menu")
    public void userReadTheCifIdAndAccountHolderNameOnProfileMenu() {
        dashboardPage.profileMenu.click();
        waitTillVisibilityElement(driver, dashboardPage.accountHolderName);
        logger.info("Account Holder Name is " + dashboardPage.accountHolderName.getText());
        logger.info("Account Ciff :" + dashboardPage.cifNumber.getText());
        setAccountCifId(dashboardPage.cifNumber.getText());
        setAccountHolderName(dashboardPage.accountHolderName.getText());
        dashboardPage.profileMenu.click();
    }

    @And("User verify the QR status in store dashboard page")
    public void userVerifyTheQRStatusInStoreDashboardPage() {
        if (dashboardPage.welcomeText.getText().contains("your store")) {
            logger.info("This User have QR code");
            softAssert.assertTrue(collectionsPage.qrCode.isDisplayed(), "qr code not displayed");
            softAssert.assertTrue(collectionsPage.vpaDropDown.isDisplayed(), "vpa bat is not displayed");
            softAssert.assertTrue(collectionsPage.copyButton.isDisplayed(), "qr code copy button not displayed");
            softAssert.assertTrue(collectionsPage.downloadButton.isDisplayed(), "qr download button is not displayed");
            softAssert.assertTrue(collectionsPage.dynamicQrEnterAmount.isDisplayed(), "enter amount tab for dynamic qr code is not displayed");
            softAssert.assertTrue(collectionsPage.dynamicQrRemarks.isDisplayed(), "remarks tab for dynamic qr is not displayed");
        } else {
            switch (dashboardPage.qrCodeStatus.getText()) {
                case "Your QR Application is under review":
                    logger.info("QR Code status " + dashboardPage.qrCodeStatus.getText());
                    break;
                case "Unlock seamless payment acceptance with AU QR":
                    softAssert.assertTrue(dashboardPage.applyNowButton.isDisplayed(), "for qr apply button not displayed");
                    softAssert.assertTrue(dashboardPage.knowMore.isDisplayed(), "know more button is not displayed");
                    logger.info("User doesn't have QR code");
                    break;
            }
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User validates the redirections on the FaQs page")
    public void userValidatesTheRedirectionsOnTheFaQsPage() {
        softAssert = new SoftAssert();
        clickOnButton(dashboardPage.emergencyNavigateButton("FaQs"));
        waitTillInvisibilityOfLoader(driver);
        ListIterator<WebElement> faqPageList = dashboardPage.faqOptionsList.listIterator();
        while (faqPageList.hasNext()) {
            WebElement faqPageListIterated = faqPageList.next();
            staticWait(3000);
            waitTillElementToBeClickable(driver,faqPageListIterated);
            String listGetText = faqPageListIterated.getText();
            logger.info("User will navigates to the "+listGetText);
            faqPageListIterated.click();
            if (listGetText.contains("Accounts")) {
                softAssert.assertTrue(dashboardPage.helpAndSupportButton.isDisplayed(), "accounts page quick link help and support button is not displayed");
                dashboardPage.backButton.click();
                waitTillInvisibilityOfLoader(driver);

            } else if (listGetText.contains("Bill Payments")) {
                softAssert.assertTrue(dashboardPage.helpAndSupportButton.isDisplayed(), "bill payments page quick link help and support button is not displayed");
                dashboardPage.backButton.click();
                waitTillInvisibilityOfLoader(driver);

            }
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

}
