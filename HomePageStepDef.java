package stepDefs;

import dataProviders.ConfigFileReader;

import dataProviders.ExcelFileReader;
import io.cucumber.java.PendingException;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.*;
import io.qameta.allure.AllureLifecycle;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import PageObjectModel.GstpaymentPage;
import pom.*;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import javax.sound.midi.ShortMessage;
import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomePageStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(HomePageStepDef.class);
   
    HomePage homePage;
    LogInPage logInPage;
    DashboardPage dashboardPage;
    DepositsDashboardPage depositsDashboardPage;
    ManagePayeePage managePayeePage;
    
    
    
    LoanPage loanPage;
    AccountSummaryPage accountSummaryPage;
    ServiceRequestPage serviceRequestPage;
    FDPage fdPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    int numberOfCASA;

    public HomePageStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        homePage = context.getPageObjectManager().getHomePage();
        logInPage = context.getPageObjectManager().getLogInPage();
        dashboardPage = context.getPageObjectManager().getDashboardPage();
        fdPage = context.getPageObjectManager().getFDPage();
        depositsDashboardPage = context.getPageObjectManager().getDepositDashboardPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        serviceRequestPage = context.getPageObjectManager().getServiceRequestPage();
        accountSummaryPage = context.getPageObjectManager().getAccountSummaryPage();
        loanPage = context.getPageObjectManager().getLoanPage();
        managePayeePage = context.getPageObjectManager().getManagePayeePage();
        

    }


    @And("User navigates to the log in page")
    public void userNavigatesToTheLogInPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(logInPage.logInPageWelcomeText.isDisplayed(), "Welcome text is not displayed");
        softAssert.assertTrue(logInPage.logInPagePasswordIcon.getAttribute("class").contains("s-icon"), "password tab eye icon not displayed");
        softAssert.assertTrue(logInPage.forgetPasswordOption.isDisplayed(), "forget password link not displayed");
        softAssert.assertTrue(logInPage.registerNowOption.isDisplayed(), "register now link not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User enters valid log in credentials for {string}")
    public void userEntersValidLogInCredentialsFor(String moduleName) {
        logger.info(driver.getTitle());
        logger.info("Execution Scenario : " + scenario.getName());
//        logger.info("Feature File Name : " + featureFileName(scenario));
        switch (moduleName) {
            case "LOG IN JOURNEY":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
//                sendKeys(logInPage.userNameField, getLogInUserName());
//                sendKeys(logInPage.passwordField, getLogInPassword());
                break;

            case "DASHBOARD":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
//              sendKeys(logInPage.userNameField, getLogInUserName());
//              sendKeys(logInPage.passwordField, getLogInPassword());
                break;

            case "DEPOSITS":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
//              sendKeys(logInPage.userNameField, getLogInUserName());
//              sendKeys(logInPage.passwordField, getLogInPassword());
                break;

            case "JOINT FD":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
//              sendKeys(logInPage.userNameField, getLogInUserName());
//              sendKeys(logInPage.passwordField, getLogInPassword());
                break;

            case "ACCOUNT STATEMENT":
                if (scenario.getName().contains("Validate Account statement Journey") || scenario.getName().contains("Validate Download function in Account Statement")) {
                    setLogInUserName(configFileReader.getProperty("userName"));
                    setLogInPassword(configFileReader.getProperty("password"));
//                  sendKeys(logInPage.userNameField, getLogInUserName());
//                  sendKeys(logInPage.passwordField, getLogInPassword());

                } else if (scenario.getName().contains("Validate more than 999 transaction in between the dates") || scenario.getName().contains("download function for >999 transaction on Account statement Journey")) {
                    setLogInUserName(fileReader.accStatementTestData.get(">999TPPUserName"));
                    setLogInPassword(fileReader.accStatementTestData.get(">999TPPPassword"));
//                    sendKeys(logInPage.userNameField, getLogInUserName());
//                    sendKeys(logInPage.passwordField, getLogInPassword());

                } else if (scenario.getName().contains("Validate more than 999 transaction in a day")) {
                    setLogInUserName(fileReader.accStatementTestData.get(">999TPDUserName"));
                    setLogInPassword(fileReader.accStatementTestData.get(">999TPDPassword"));
//

                }
                break;
            case "LOAN":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
                               break;

            case "ACCOUNT SUMMARY":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
                break;

            case "MANAGE PAYEES":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
                break;

            case "MONEY TRANSFER":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
                break;

            case "SERVICE REQUEST":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
                break;

            case "PROFILE":
                if (scenario.getName().contains("Validate profile journey for ETB users")) {
                    setLogInUserName(configFileReader.getProperty("userName"));
                    setLogInPassword(configFileReader.getProperty("password"));
                } else {
                    setLogInUserName(fileReader.profileTestData.get("userNameUpdateNTB"));
                    setLogInPassword(fileReader.profileTestData.get("updatePasswordNTB"));
                    staticWait(2000);
//                    sendKeys(logInPage.userNameField, getLogInUserName());
//                    sendKeys(logInPage.passwordField, getLogInPassword());
                }
                break;
            case "COLLECTIONS":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));  staticWait(2000);
               break;

            case "DEBIT CARD":
                setLogInUserName(configFileReader.getProperty("userName"));
                setLogInPassword(configFileReader.getProperty("password"));
                break;
        }
        fluentWaitTillTheElementToBeClickable(driver,20,1,logInPage.userNameField);
        sendKeys(logInPage.userNameField, getLogInUserName());
        sendKeys(logInPage.passwordField, getLogInPassword());
        logger.info("User logged in by using User Name :" + getLogInUserName());
        logger.info("User logged in by using Password :" + getLogInPassword());
        fluentWaitTillTheElementToBeClickable(driver, 10, 1, homePage.logInButton);
        javaScriptExecutorClickElement(driver, homePage.logInButton);
        if (homePage.logInPopUp.getText().contains("Login Issue")) {
            throw new RuntimeException("Log in Error,Please verify the screen shot");
        }
    }

    @When("User enters {string} and {string}")
    public void userEntersAnd(String username, String password) {
        staticWait(2000);
        if (username.contains("inValid userName") && password.contains("inValid password")) {
            logInPage.userNameField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getUserNameForLogIn() + "t76rqe76");
            logInPage.passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getPasswordForLogIn() + "gyusiu");

        } else if (username.contains("inValid userName") && password.contains("valid password")) {
            logInPage.userNameField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getUserNameForLogIn() + "t76rqe76");
            logInPage.passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getPasswordForLogIn());


        } else if (username.contains("valid userName") && password.contains("inValid password")) {
            logInPage.userNameField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getUserNameForLogIn());
            logInPage.passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getPasswordForLogIn() + "gyusiu");
        }
    }

    @And("User clicks on store toggle button and navigates to bank dashboard page")
    public void userClicksOnStoreToggleButtonAndNavigatesToBankDashboardPage() {
        try {
            fluentWaitTillTheElementToBeClickable(driver, 15, 1, homePage.storeAndBankToggle);
            clickOnButton(homePage.storeAndBankToggle);
            logger.info("User is on Store Dashboard page");
            logger.info("The user logged in successfully and landed on Bank Dashboard page");
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Log in failed");
        }
    }

    @And("User navigates on the deposit dashboard page")
    public void userNavigatesOnTheDepositDashboardPage() {
        clickOnButton(homePage.accountsMenu);
        fluentWaitTillTheElementToBeClickable(driver, 20, 2, homePage.depositAccountsButton);
        clickOnButton(homePage.depositAccountsButton);
        fluentWaitTillInVisibilityOfLoader(driver);
        try {
            explicitWait(driver, 30).until(ExpectedConditions.visibilityOf(homePage.openNowButton));
            logger.info("Deposit page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            logger.error("Element not visibile", e);
            throw new TimeoutException("Element Not displayed");
        }

    }

    @And("User navigates on the deposit dashboard page for joint Fd")
    public void userNavigatesOnTheDepositDashboardPageForJointFd() {

        clickOnButton(homePage.accountsMenu);
        waitTillElementToBeClickable(driver, homePage.depositAccountsButton);
        clickOnButton(homePage.depositAccountsButton);
        waitTillInvisibilityOfLoader(driver);
        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.openNowButton));
            logger.info("Deposit page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
    }

    @And("User navigates on the account summary page")
    public void userNavigatesOnTheAccountSummaryPage() {
        waitTillVisibilityElement(driver, homePage.accountsMenu);
        clickOnButton(homePage.accountsMenu);
        waitTillElementToBeClickable(driver, homePage.operativeAccountButton);
        clickOnButton(homePage.operativeAccountButton);
        waitTillInvisibilityOfLoader(driver);
        logger.info("User is on Account Summary home page");
        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.pageHeader));
            System.out.println("Account Summary page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
    }
    
    @And("User navigates to account statement page")
    public void userNavigatesToAccountStatementPage() {
        waitTillElementToBeClickable(driver, homePage.statementsMenu);
        clickOnButton(homePage.statementsMenu);
        waitTillElementToBeClickable(driver, homePage.accountStatementButton);
        clickOnButton(homePage.accountStatementButton);
        waitTillInvisibilityOfLoader(driver);
        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.accountStatementsHeader));
            logger.info("Account Statement page tittle: " + homePage.accountStatementsHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
    }

    @And("User navigates to the payees page")
    public void userNavigatesToThePayeesPage() {
        waitTillVisibilityElement(driver, homePage.paymentMenu);
        clickOnButton(homePage.paymentMenu);
        //     waitTillInvisibilityOfLoader(driver);
        waitTillElementToBeClickable(driver, homePage.managePayeeButton);
        clickOnButton(homePage.managePayeeButton);
        waitTillInvisibilityOfLoader(driver);
        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.pageHeader));
            logger.info("Manage Payee page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
    }

    @And("User navigates to the loan page")
    public void userNavigatesToTheLoanPage() {
        String casaDetails = homePage.numberOfCASA.getText();
        numberOfCASA = 0;
        for (int i = 0; i < casaDetails.length(); i++) {
            char stringChar = casaDetails.charAt(i);
            if (Character.isDigit(stringChar)) {
                numberOfCASA += Character.getNumericValue(stringChar);
            }
        }
        setNoOfCasa(numberOfCASA);
        logger.info("No of CASA ACCOUNT :" + numberOfCASA);
        waitTillInvisibilityOfLoader(driver);
        clickOnButton(homePage.accountsMenu);
        waitTillElementToBeClickable(driver, homePage.loanModuleButton);
        clickOnButton(homePage.loanModuleButton);
        waitTillInvisibilityOfLoader(driver);

        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.pageHeader));
            logger.info("Loan page tittle: " + homePage.pageHeader.getText());
            // System.out.println("Loan page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
    }

    @And("User will be navigated on the money transfer home page")
    public void userWillBeNavigatedOnTheMoneyTransferHomePage() {
        fluentWaitTillVisibilityElement(driver, 10, 1, homePage.numberOfCASA);
        String casaDetails = null;
        try {
            casaDetails = homePage.numberOfCASA.getText();
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("CASA Summary not displayed in Dashboard page");

        }
        numberOfCASA = 0;
        for (int i = 0; i < casaDetails.length(); i++) {
            char stringChar = casaDetails.charAt(i);
            if (Character.isDigit(stringChar)) {
                numberOfCASA += Character.getNumericValue(stringChar);
            }
        }
        setNoOfCasa(numberOfCASA);
        logger.info("This account have " + homePage.numberOfCASA.getText());
        logger.info("The total no of casa is :" + numberOfCASA);
        // waitTillElementToBeClickable(driver, homePage.paymentMenu);
        fluentWaitTillTheElementToBeClickable(driver, 10, 1, homePage.paymentMenu);
        clickOnButton(homePage.paymentMenu);
        fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.moneyTransferButton);
        clickOnButton(homePage.moneyTransferButton);
        //      waitTillInvisibilityOfLoader(driver);
        try {

            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.pageHeader));
            logger.info("Money Transfer page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
    }

    @And("User will be navigated on the debit card home page")
    public void userWillBeNavigatedOnTheDebitCardHomePage() {
        clickOnButton(homePage.cardsMenu);
        waitTillElementToBeClickable(driver, homePage.debitCardButton);
        clickOnButton(homePage.debitCardButton);
        waitTillInvisibilityOfLoader(driver);
        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.pageHeader));
            logger.info("Debit Card page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
    }

    @And("User will be navigated on the service request home page")
    public void userWillBeNavigatedOnTheServiceRequestHomePage() {
        fluentWaitTillTheElementToBeClickable(driver, 20, 2, homePage.serviceRequestMenu);
        //   waitTillElementToBeClickable(driver, homePage.serviceRequestMenu);
        clickOnButton(homePage.serviceRequestMenu);
        fluentWaitTillTheElementToBeClickable(driver, 20, 2, homePage.raiseRequestButton);
        //  waitTillElementToBeClickable(driver, homePage.raiseRequestButton);
        clickOnButton(homePage.raiseRequestButton);
        waitTillInvisibilityOfLoader(driver);
        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.pageHeader));
            logger.info("Service Request Page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
    }

    @And("User navigates to the dashboard page")
    public void userNavigatesToTheDashboardPage() throws InterruptedException {
        waitTillVisibilityElement(driver, homePage.serviceToggle);
        logger.info("Dashboard page header : " + homePage.pageHeader.getText());

    }

    @And("User enter the otp and verify the otp")
    public void userEnterTheOtpAndVerifyTheOtp() {
        try {
            waitTillVisibilityElement(driver, homePage.resendOTP);
            if (homePage.resendOTP.isEnabled()) {
                homePage.resendOTP.click();
            }
            char[] otpChar = fileReader.logInTestData.get("commonOtp").toCharArray();
            List<Character> otpNumberList = new ArrayList<>();
            for (char otpCharList : otpChar) {
                otpNumberList.add(otpCharList);
            }
            ListIterator<WebElement> otpElementList = homePage.enterOtpList.listIterator();
            ListIterator<Character> enterOtpNumber = otpNumberList.listIterator();
            while (otpElementList.hasNext() && enterOtpNumber.hasNext()) {
                WebElement enterOtp = otpElementList.next();
                Character eachNumber = enterOtpNumber.next();
                enterOtp.sendKeys(Character.toString(eachNumber));
            }
        } catch (NoSuchElementException e) {
            logger.info("No need to enter the OTP");
        }
    }

    @And("User will be navigated on the profile pop up")
    public void userWillBeNavigatedOnTheProfilePopUp() {
        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(dashboardPage.profileMenu));
            logger.info("Dashboard page header : " + dashboardPage.profileMenu.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new TimeoutException("Element Not displayed");
        }
        dashboardPage.profileMenu.click();
        waitTillElementToBeClickable(driver, logInPage.logOutInPage);
        setAccountHolderName(dashboardPage.accountHolderName.getText());
        setAccountCifId(dashboardPage.cifNumber.getText());
        logger.info("Account Holder Name is " + getAccountHolderName());
        logger.info("Account Ciff :" + getAccountCifId());
    }

    // Common Steps for Back button
    @And("User clicks on back button")
    public void userClicksOnBackButton() {

        waitTillElementToBeClickable(driver, homePage.backButton);
        homePage.backButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    /*****
     * My Reference Try to Create New Methods  *****/


    @And("User clicks on log out the session and verifies")
    public void userClicksOnLogOutTheSessionAndVerifies() {
        softAssert = new SoftAssert();
        dashboardPage.profileMenu.click();
        waitTillVisibilityElement(driver, logInPage.logOutInPage);
        clickOnButton(logInPage.logOutInPage);
        clickOnButton(logInPage.logOutPopUpLogOutButton);
        waitTillVisibilityElement(driver, homePage.toastMessage);
        softAssert = new SoftAssert();
        softAssert.assertTrue(logInPage.logOutMessage.isDisplayed(), "log out successful message not displayed");
        staticWait(3000);
        softAssert.assertEquals(driver.getCurrentUrl(), TextAssertion.applicationUrl + TextAssertion.loginPageUrlPath, "home page url is not matched");
        logger.info("User logged out of the session");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }


    /************Quick Links Redirection steps for All Module **************/


    @When("User clicks on the detailed statement in quick links")
    public void userClicksOnTheDetailedStatementInQuickLinks() {
        scrollIntoViewUp(driver, accountSummaryPage.detailsStatementQuick);
        javaScriptExecutorClickElement(driver, accountSummaryPage.detailsStatementQuick);
        waitTillInvisibilityOfLoader(driver);
    }

    @When("User clicks on the money transfer in quick links")
    public void userClicksOnTheMoneyTransferInQuickLinks() {
        scrollIntoViewUp(driver, accountSummaryPage.moneyTransferQuickLink);
        javaScriptExecutorClickElement(driver, accountSummaryPage.moneyTransferQuickLink);
        waitTillInvisibilityOfLoader(driver);

    }

    /************Redirection steps for All Module to verify the Quick links section**************/

    @And("User navigates to the {string}")
    public void userNavigatesToThe(String pageName) {
        String currentPage = pageName.toLowerCase();
        switch (currentPage) {
            case "deposit dashboard page":
                clickOnButton(homePage.depositAccountsButton);
                waitTillInvisibilityOfLoader(driver);
                break;

            case "deposit details page":
                clickOnButton(homePage.depositAccountsButton);
                waitTillInvisibilityOfLoader(driver);
                scrollIntoView(driver, depositsDashboardPage.getDepositViewButton(getDepositNo()));
                depositsDashboardPage.getDepositViewButton(getDepositNo()).click();
                break;

            case "account summary home page":
                clickOnButton(homePage.operativeAccountButton);
                break;

            case "account details page":
                clickOnButton(homePage.operativeAccountButton);
                waitTillInvisibilityOfLoader(driver);
                scrollIntoView(driver, accountSummaryPage.getViewButton("Savings Accounts"));
                accountSummaryPage.getViewButton("Savings Accounts").click();
                break;

            case "manage payee home page":
                waitTillElementToBeClickable(driver, homePage.managePayeeButton);
                javaScriptExecutorClickElement(driver, homePage.managePayeeButton);
                break;

            case "add new payee page":
                waitTillElementToBeClickable(driver, homePage.managePayeeButton);
                javaScriptExecutorClickElement(driver, homePage.managePayeeButton);
                waitTillInvisibilityOfLoader(driver);
                clickOnButton(managePayeePage.addNewPayeeButton);
                break;

            case "payee details page":
                waitTillElementToBeClickable(driver, homePage.managePayeeButton);
                javaScriptExecutorClickElement(driver, homePage.managePayeeButton);
                waitTillInvisibilityOfLoader(driver);
                clickOnButton(managePayeePage.paymentPageViewButton);
                break;

            case "loans home page":
                waitTillVisibilityElement(driver, homePage.loanModuleButton);
                clickOnButton(homePage.loanModuleButton);
                break;

            case "apply for loan page":
                javaScriptExecutorClickElement(driver, homePage.loanModuleButton);
                waitTillVisibilityElement(driver, loanPage.loanPageLoanApplyButton);
                clickOnButton(loanPage.loanPageLoanApplyButton);
                break;

            case "specific apply loan type":
                javaScriptExecutorClickElement(driver, homePage.loanModuleButton);
                waitTillVisibilityElement(driver, loanPage.loanPageLoanApplyButton);
                clickOnButton(loanPage.loanPageLoanApplyButton);
                waitTillVisibilityElement(driver, loanPage.chooseLoanType(fileReader.loanTestData.get("typeOfLoanApply")));
                clickOnButton(loanPage.chooseLoanType(fileReader.loanTestData.get("typeOfLoanApply")));
                break;

            case "loans details page":
                waitTillElementToBeClickable(driver, homePage.loanModuleButton);
                javaScriptExecutorClickElement(driver, homePage.loanModuleButton);
                waitTillVisibilityElement(driver, loanPage.viewButtonForSpecificLoan(fileReader.loanTestData.get("loanAccountNumber")));
                clickOnButton(loanPage.viewButtonForSpecificLoan(fileReader.loanTestData.get("loanAccountNumber")));
                break;

            case "pay overdue page":
                waitTillElementToBeClickable(driver, homePage.loanModuleButton);
                javaScriptExecutorClickElement(driver, homePage.loanModuleButton);
                waitTillVisibilityElement(driver, loanPage.viewButtonForSpecificLoan(fileReader.loanTestData.get("loanAccountNumber")));
                clickOnButton(loanPage.viewButtonForSpecificLoan(fileReader.loanTestData.get("loanAccountNumber")));
                waitTillVisibilityElement(driver, loanPage.loanDetailsPagePayNowButton);
                clickOnButton(loanPage.loanDetailsPagePayNowButton);
                break;

            case "service request home page":
                waitTillElementToBeClickable(driver, homePage.raiseRequestButton);
                javaScriptExecutorClickElement(driver, homePage.raiseRequestButton);
                break;

            case "new cheque book request page":
                waitTillElementToBeClickable(driver, homePage.raiseRequestButton);
                javaScriptExecutorClickElement(driver, homePage.raiseRequestButton);
                waitTillVisibilityElement(driver, serviceRequestPage.clickOnTab(TextAssertion.newChequeBookTab));
                javaScriptExecutorClickElement(driver, serviceRequestPage.clickOnTab(TextAssertion.newChequeBookTab));
                break;

            case "cheque status page":
                waitTillElementToBeClickable(driver, homePage.raiseRequestButton);
                javaScriptExecutorClickElement(driver, homePage.raiseRequestButton);
                waitTillVisibilityElement(driver, serviceRequestPage.clickOnTab(TextAssertion.chequeStatusTab));
                javaScriptExecutorClickElement(driver, serviceRequestPage.clickOnTab(TextAssertion.chequeStatusTab));
                break;

            case "stop cheque page":
                clickOnButton(homePage.raiseRequestButton);
                waitTillVisibilityElement(driver, serviceRequestPage.stopChequeTab);
                javaScriptExecutorClickElement(driver, serviceRequestPage.stopChequeTab);
                break;

            case "update communication address page":
                waitTillElementToBeClickable(driver, homePage.raiseRequestButton);
                javaScriptExecutorClickElement(driver, homePage.raiseRequestButton);
                waitTillInvisibilityOfLoader(driver);
                serviceRequestPage.clickOnTab(TextAssertion.updateCommAddress).click();
                break;
            case "service request list page":
                waitTillElementToBeClickable(driver, homePage.raiseRequestButton);
                javaScriptExecutorClickElement(driver, homePage.raiseRequestButton);
                waitTillInvisibilityOfLoader(driver);
                serviceRequestPage.serviceRequestListSection.click();
                break;
            case "debit card home page":
                waitTillElementToBeClickable(driver, homePage.debitCardButton);
                javaScriptExecutorClickElement(driver, homePage.debitCardButton);
                break;
        }
        waitTillInvisibilityOfLoader(driver);
    }


    @And("User verify advisory page for user practice")
    public void userVerifyAdvisoryPageForUserPractice() {
        try {
            String currentPageNumber = null;
            String[] noOfPages = homePage.pageNoDetails.getText().split("of");
            String totalNoOfPages = noOfPages[1].trim();
            int noOfPagesAdvisory = Integer.parseInt(totalNoOfPages);
            System.out.println(noOfPagesAdvisory);
            for (int i = 1; i <= noOfPagesAdvisory; i++) {
                String[] currentPageSplit = homePage.pageNoDetails.getText().split("of");
                currentPageNumber = currentPageSplit[0].trim();
                staticWait(2000);
                javaScriptExecutorClickElement(driver, homePage.advisoryPageAcceptButton);
            }
            if (Integer.parseInt(currentPageNumber) == 1) {
                attachScreenshot(driver, scenario);
                Assert.fail("Advisory page navigation is failed");
            }

        } catch (Exception exception) {
        }
    }

    /*****************-*---*-------------------------*----------------*---*-*****************/


    @And("User navigates on the deposit dashboard page by {string} and {string}")
    public void userNavigatesOnTheDepositDashboardPageByAnd(String username, String password) {
        logger.info("Current Scenario :" + scenario.getName());
        logger.info("User logged in by using User Name:" + username);
        logger.info("User logged in by using Password :" + password);
        driver.navigate().refresh();
        waitTillInvisibilityOfLoader(driver);
        staticWait(2000);
        sendKeys(logInPage.userNameField, username);
        sendKeys(logInPage.passwordField, password);
        staticWait(2000);
        clickOnButton(homePage.logInButton);
        userEnterTheOtpAndVerifyTheOtp();
        waitTillVisibilityElement(driver, homePage.storeAndBankToggle);
        try {
            clickOnButton(homePage.storeAndBankToggle);
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Dashboard page not displayed please verify the report");
        }
        waitTillInvisibilityOfLoader(driver);
        logger.info("The user logged in successfully");
        logger.info("User is on dashboard page");
        clickOnButton(homePage.accountsMenu);
        waitTillInvisibilityOfLoader(driver);
        clickOnButton(homePage.depositAccountsButton);
        waitTillInvisibilityOfLoader(driver);
        try {
            explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(homePage.openNowButton));
            logger.info("Deposit page tittle: " + homePage.pageHeader.getText());
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            logger.error("Element not visibile", e);
            throw new TimeoutException("Element Not displayed");
        }

    }

    /****
     *   This Method using for to read the Savings and Current Account NUmber
     *
     * This step used for to reduce the script dependency of data
     *
     *
     *
     * **/
    public void importantDetailsToAvoidReadFromExcelSheet() {
        String currentAccountNumber;
        String savingAccountNumber;
//        String currentAccountAvailableBalance;
//        String savingAccountAvailableBalance;
//        String accountHolderName;
//        String depositAmount;
//        String maturityType;
//        String transferAmount;
//        String modeOfPayment;
//        savingAccountAvailableBalance= homePage.accountSummaryDetails("Savings","Balance").getText();
//        currentAccountAvailableBalance= homePage.accountSummaryDetails("Current","Balance").getText();
//
//        accountHolderName= homePage.accountSummaryDetails("Savings","Holder").getText();
        /***
         * Navigates to the Accounts Summary Page
         * ***/
        homePage.accountsMenu.click();
        homePage.accountSummaryButton.click();
        waitTillInvisibilityOfLoader(driver);

        /** Read the Account Number and Current Account Number values **/
        ListIterator<WebElement> casaList = homePage.casaDetails.listIterator();
        while (casaList.hasNext()) {
            WebElement casaDetails = casaList.next();
            String iterateCASAList = casaDetails.getText();
            if (iterateCASAList.contains("Current")) {
                setCurrentAccountNumber(homePage.accountSummaryDetails("Current", "Number").getText().trim());
            } else if (iterateCASAList.contains("Savings")) {
                setSavingAccountNumber(homePage.accountSummaryDetails("Savings", "Number").getText().trim());
                logger.info("Savings Account Number " + getSavingAccountNumber());

            } else if (iterateCASAList.isEmpty()) {
                logger.debug("There is no CASA available");
            }
        }
        homePage.accountsMenu.click();
        homePage.homePageButton.click();

/****
 * Enter account summary page
 * Read Data and store
 * Return to bank dashboard page
 *
 * *****/
    }


    @And("User read the casa details on account summary page")
    public void userReadTheCasaDetailsOnAccountSummaryPage() {
        homePage.accountsMenu.click();
        homePage.accountSummaryButton.click();
        waitTillInvisibilityOfLoader(driver);

        /** Read the Account Number and Current Account Number values **/

        ListIterator<WebElement> casaList = homePage.casaDetails.listIterator();
        while (casaList.hasNext()) {
            WebElement casaDetails = casaList.next();
            String iterateCASAList = casaDetails.getText();
            if (iterateCASAList.contains("Current")) {
                setCurrentAccountNumber(homePage.accountSummaryDetails("Current", "Number").getText().trim());
                logger.info(iterateCASAList + " " + getCurrentAccountNumber());
            } else if (iterateCASAList.contains("Savings")) {
                setSavingAccountNumber(homePage.accountSummaryDetails("Savings", "Number").getText().trim());
                logger.info(iterateCASAList + " " + getSavingAccountNumber());
            }
        }
        homePage.accountsMenu.click();
        homePage.homePageButton.click();
    }

    @And("User should validate their logged in status")
    public void userShouldValidateTheirLoggedInStatus() {
        try {
            if (homePage.logInPagePopUp.isDisplayed()) {
                attachScreenshot(driver, scenario);
                String errorText = homePage.logInPagePopUp.getText();
                logger.error("Log in failed due to " + errorText);
                throw new RuntimeException("errorText");
            }
        } catch (NoSuchElementException ignored) {
            logger.info("User logged in successfully");
        }
    }

    /**
     * Something went wrong pop up
     * validation
     * if its appears
     **/


    @And("User verify the error toast message if appeared")
    public void userVerifyTheErrorToastMessageIfAppeared() {
        try {
            if (homePage.toastMessage.isDisplayed()) {
                String toastMessageText = homePage.toastMessage.getText();
                logger.error(toastMessageText + " is appeared");
                attachScreenshot(driver, scenario);
                softAssert.fail("Due to that " + toastMessageText + " message its failed");

            }
        } catch (NoSuchElementException ignore) {
        }
    }

    @Then("If the user see something went wrong pop up execution stopped by the user")
    public void ifTheUserSeeSomethingWentWrongPopUpExecutionStoppedByTheUser() {
        try {
            if (homePage.logInPagePopUp.getText().contains(TextAssertion.logInErrorPopUp)) {
                setAbortScenarios(true);
                String errorMessageText = homePage.logInPagePopUp.getText();
                logger.error(errorMessageText + " is appeared");
                attachScreenshot(driver, scenario);
                //  Assert.fail("Due to that " + errorMessageText + " message its failed, can't execution furthur");
                throw new RuntimeException("Due to an error, the execution was aborted");
            }
        } catch (NoSuchElementException ignore) {
        }
    }

    @And("User selects the date from calendar for {string}")
    public void userSelectsTheDateFromCalendarFor(String module) {

        if (module.contains("positive pay")) {
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, serviceRequestPage.selectPaymentDate);
            serviceRequestPage.selectPaymentDate.click();
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthYear);
            homePage.calendarMonthYear.click();
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthYear);
            homePage.calendarMonthYear.click();
            while (true) {
                LocalDate toDaysDateFor = getToDayDate();
                LocalDate payCheckIssueDateFor = getUpComingDate(3);
                String currentDate = getFormatedDate(toDaysDateFor, "dd/MMMM/yyyy");
                String payCheckIssueDate = getFormatedDate(payCheckIssueDateFor, "dd/MMMM/yyyy");
                setDate(payCheckIssueDate);
                logger.info("Today's date is " + currentDate);
                logger.info("Paycheck Issue Date is " + payCheckIssueDate);
                String[] expectDateArr = payCheckIssueDate.split("/");
                String expectedYear = expectDateArr[2].trim();
                String expectedMonth = expectDateArr[1].trim();
                String expectedDate = expectDateArr[0].trim();
                logger.info("Expected Year is " + expectedYear);

                String actualYears = homePage.calendarMonthYear.getText().trim();

                String[] yearRange = actualYears.split("–");
                logger.info("Between Years " + actualYears);
                String startingYear = yearRange[0].trim();
                String endingYear = yearRange[1].trim();

                logger.info("Start Years " + startingYear);
                logger.info("End Years " + endingYear);

                Year startYear = Year.parse(startingYear);
                Year endYear = Year.parse(endingYear);
                Year yearExpected = Year.parse(expectedYear);
                if (yearExpected.isAfter(startYear) && yearExpected.isBefore(endYear)) {
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarYearSelect(expectedYear));
                    clickOnButton(homePage.calendarYearSelect(expectedYear));
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthSelect(expectedMonth));
                    clickOnButton(homePage.calendarMonthSelect(expectedMonth));
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarDateSelect(expectedDate));
                    clickOnButton(homePage.calendarDateSelect(expectedDate));
                    logger.info(expectedYear + " is within the range " + actualYears);
                    break;
                } else {
                    logger.info(yearExpected + " is not within the range " + actualYears);
                    if (yearExpected.isBefore(startYear)) {
                        clickOnButton(homePage.calendarMonthYearPreviousNavigationButton);
                    } else if (yearExpected.isAfter(endYear)) {
                        clickOnButton(homePage.calendarMonthYearNextNavigationButton);
                    }

                }
            }

        } else if (module.contains("add nominee")) {
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, serviceRequestPage.sideSheetNomineeDOB);
            serviceRequestPage.sideSheetNomineeDOB.click();
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthYear);
            homePage.calendarMonthYear.click();
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthYear);
            homePage.calendarMonthYear.click();
            while (true) {
                LocalDate toDaysDateFor = getToDayDate();
                LocalDate dateOfBirth = getPastYear(27);
                String currentDate = getFormatedDate(toDaysDateFor, "dd/MMMM/yyyy");
                String dobDate = getFormatedDate(dateOfBirth, "dd/MMMM/yyyy");
                String dobDateFormat = getFormatedDate(dateOfBirth, "dd MMM, yyyy");
                setDate(dobDateFormat.trim());
                logger.info("Today's date is " + currentDate);
                logger.info("Paycheck Issue Date is " + dobDate);
                String[] expectDateArr = dobDate.split("/");
                String expectedYear = expectDateArr[2].trim();
                String expectedMonth = expectDateArr[1].trim();
                String expectedDate = expectDateArr[0].trim();
                logger.info("Expected Year is " + expectedYear);

                String actualYears = homePage.calendarMonthYear.getText().trim();

                String[] yearRange = actualYears.split("–");
                logger.info("Between Years " + actualYears);
                String startingYear = yearRange[0].trim();
                String endingYear = yearRange[1].trim();

                logger.info("Start Years " + startingYear);
                logger.info("End Years " + endingYear);

                Year startYear = Year.parse(startingYear);
                Year endYear = Year.parse(endingYear);
                Year yearExpected = Year.parse(expectedYear);
                if (yearExpected.isAfter(startYear) && yearExpected.isBefore(endYear)) {
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarYearSelect(expectedYear));
                    clickOnButton(homePage.calendarYearSelect(expectedYear));
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthSelect(expectedMonth));
                    clickOnButton(homePage.calendarMonthSelect(expectedMonth));
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarDateSelect(expectedDate));
                    clickOnButton(homePage.calendarDateSelect(expectedDate));
                    logger.info(expectedYear + " is within the range " + actualYears);
                    break;
                } else {
                    logger.info(yearExpected + " is not within the range " + actualYears);
                    if (yearExpected.isBefore(startYear)) {
                        clickOnButton(homePage.calendarMonthYearPreviousNavigationButton);
                    } else if (yearExpected.isAfter(endYear)) {
                        clickOnButton(homePage.calendarMonthYearNextNavigationButton);
                    }

                }
            }

        }
    }


    // Account Number Read

    @And("User read the CASA details on summary page")
    public void userReadTheCASADetailsOnSummaryPage() {
        String accountHeader;
        String accountNumber = null;
        fluentWaitTillTheElementToBeClickable(driver, 20, 2, homePage.accountSummaryViewAllButton);
        clickOnButton(homePage.accountSummaryViewAllButton);
        fluentWaitTillVisibilityElement(driver, 20, 2, homePage.accountsContainerHeading);
//click View All Button
        ListIterator<WebElement> accountHeadings = homePage.accountsContainerHeadings.listIterator();
        while (accountHeadings.hasNext()) {
            WebElement accountType = accountHeadings.next();
            accountHeader = accountType.getText().trim();
            if (accountHeader.contains("Current")) {
                accountNumber = homePage.accountNumber(accountHeader).getText();
                setCurrentAccountNumber(accountNumber);
            } else if (accountHeader.contains("Savings")) {
                accountNumber = homePage.accountNumber(accountHeader).getText();
                setSavingAccountNumber(accountNumber);
            }
        }
        if (getSavingAccountNumber()!= null && getCurrentAccountNumber()!= null) {
            setAccountNumber(getSavingAccountNumber());
            logger.info("Here savings and current account are available ,so we set savings number as account number");
        }
        else if(getSavingAccountNumber()== null) {
            setAccountNumber(getCurrentAccountNumber());
            logger.info("Here savings acc number not available ,so we set current number as account number");

        } else {
            setAccountNumber(getSavingAccountNumber());
            logger.info("Here current acc number not available ,so we set savings number as account number");
        }
        logger.info("Set Deafult Account Number is "+ getAccountNumber());
        //Redirected to Home Page
        clickOnButton(homePage.homePageButton);
        waitTillInvisibilityOfLoaderFluent(driver);
    }


    @And("User validates the log-in status")
    public void userValidatesTheLogInStatus() {
        try {
            if (homePage.logInPagePopUp.getText().contains(TextAssertion.logInErrorPopUp)) {
                setAbortScenarios(true);
                String errorMessageText = homePage.logInPagePopUp.getText();
                logger.error(errorMessageText + " is appeared");
                attachScreenshot(driver, scenario);
                //  Assert.fail("Due to that " + errorMessageText + " message its failed, can't execution furthur");
                throw new RuntimeException("Due to an error, the execution was aborted");
            }
        } catch (NoSuchElementException ignore) {

        }


    }
  //GST pAGE
    
    @And("User navigates to gst payment page")
    public void userNavigatesToGstPaymentPage() {
    	WebElement element = driver.findElement(By.xpath("//*[text()='GST Payment']"));
    	((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		gstpaymentpage.gstPaymentIcon();
    	
    	
    	
    	
    }
}




