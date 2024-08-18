package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.PendingException;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.asserts.SoftAssert;
import pom.HomePage;
import pom.LoanPage;
import pom.ManagePayeePage;
import pom.MoneyTransferPage;
import reusable.Base;
import reusable.TestContext;

import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.ListIterator;


public class MoneyTransferStepDef extends Base {

    private static Logger logger = LogManager.getLogger(MoneyTransferStepDef.class);
    MoneyTransferPage moneyTransferPage;
    ManagePayeePage managePayeePage;
    ManagePayeeStepDef managePayeeStepDef;
    HomePageStepDef homePageStepDef;
    HomePage homePage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;

    SoftAssert softAssert;
    String transferredAmount;
    String toPayeeAccNo;
    String summaryFromAccount;
    String summaryRemarks;
    String paymentMode;
    double balanceAvailable;
    String auBankPayeeName;
    String summaryToPayeeAccount;
    String transactionReferenceNo;
    String transferredDateTime;
    String currentUrl;
    String transactionSuccess;
    String transferredAmountInTransactionStatusPage;
    int noOfPaymentInList;
    String scheduledPayment;
    String onceOrRecurring;
    String transactionPayeeNameStatusPage;


    public MoneyTransferStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        moneyTransferPage = context.getPageObjectManager().getMoneyTransferPage();
        homePage = context.getPageObjectManager().getHomePage();
        managePayeePage = context.getPageObjectManager().getManagePayeePage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        homePageStepDef = new HomePageStepDef(context);
        managePayeeStepDef = new ManagePayeeStepDef(context);
    }

    @And("User verify money transfer home page")
    public void userVerifyMoneyTransferHomePage() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, moneyTransferPage.moneyTransferPageHeader);
        softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "money transfer home page not displayed");
        softAssert.assertTrue(moneyTransferPage.pageHeader.getText().contains("Money Transfer"), "money transfer header not matched");
        softAssert.assertTrue(moneyTransferPage.transferPayeeTab.isDisplayed(), "transfer to payee tab not displayed");
        softAssert.assertTrue(moneyTransferPage.quickTransferTab.isDisplayed(), "quick account transfer tab not displayed");
        softAssert.assertTrue(moneyTransferPage.multiplePaymentsTab.isDisplayed(), "multiple payments tab not displayed");
        softAssert.assertTrue(moneyTransferPage.transferToSelfTab.isDisplayed(), "transfer to self tab displayed");
        softAssert.assertTrue(moneyTransferPage.recentTransactionTab.isDisplayed(), "recent transaction tab not displayed");
        softAssert.assertTrue(moneyTransferPage.scheduleTransactionsTab.isDisplayed(), "schedule transactions tab not displayed");
        softAssert.assertTrue(moneyTransferPage.recentPayee.isDisplayed(), "recent payee not displayed");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on transfer to payee tab")
    public void userClicksOnTransferToPayeeTab() {
        softAssert = new SoftAssert();
        clickOnButton(moneyTransferPage.transferPayeeTab);
//        waitTillInvisibilityOfLoader(driver);
        try {
            if (homePage.toastMessage.isDisplayed()) {
                String toastMessage = homePage.toastMessage.getText();
                attachScreenshot(driver, scenario);
                softAssert.fail(toastMessage);

            }
        } catch (NoSuchElementException ignore) {
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify transfer to payee page")
    public void userVerifyTransferToPayeePage() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, moneyTransferPage.transferToPayeePageHeader);
        logger.info("User is on transfer to payee page");
        softAssert.assertTrue(driver.getCurrentUrl().contains("transfer-to-payee"), "navigate to transfer to payee page failed");
        softAssert.assertTrue(moneyTransferPage.transferToPayeePageHeader.isDisplayed(), "transfer to payee page not displayed");
        softAssert.assertTrue(moneyTransferPage.clickFromAccount.isDisplayed(), "select account tab not displayed");
        softAssert.assertTrue(moneyTransferPage.clickSelectPayee.isDisplayed(), "select payee tab not displayed");
        softAssert.assertTrue(moneyTransferPage.transferAmount.isDisplayed(), "transfer amount tab not displayed");
        softAssert.assertTrue(moneyTransferPage.paymentSummary.isDisplayed(), "payment summary header not displayed");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User select add new payee button in dropdown")
    public void userSelectAddNewPayeeButtonInDropdown() {
        staticWait(3000);
        //waitTillElementToBeClickable(driver, moneyTransferPage.clickSelectPayee);
        waitTillInvisibilityOfLoader(driver);
        moneyTransferPage.clickSelectPayee.click();
        staticWait(1000);
        moneyTransferPage.selectPayeeFromDD("Add New Payee").click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User clicks back navigates to transfer to payee page")
    public void userClicksBackNavigatesToTransferToPayeePage() {
        // driver.navigate().back();
        /****Naviagtes from Manage Payee page to Transfer payee page*****/
        clickOnButton(homePage.moneyTransferButton);
        waitTillInvisibilityOfLoader(driver);
        clickOnButton(moneyTransferPage.transferPayeeTab);
        waitTillInvisibilityOfLoader(driver);
        waitTillInvisibilityOfLoader(driver);
        Assert.assertTrue(driver.getCurrentUrl().contains("transfer-to-payee"), "navigates to transfer to payee page failed ");
    }

    @And("User select payee from the transfer to payee page")
    public void userSelectPayeeFromTheTransferToPayeePage() {
        moneyTransferPage.clickSelectPayee.click();
        logger.info("The current selected payee is :" + moneyTransferPage.selectPayeeIndexFirstPayee.getText());
        //   moneyTransferPage.selectPayeeFromDD(fileReader.moneyTransferTestData.get("selectPayeeName"));
        moneyTransferPage.selectPayeeIndexFirstPayee.click();
    }

    @And("User select all needed details")
    public void userSelectAllNeededDetails() {
        moneyTransferPage.clickFromAccount.click();
        logger.info("From Account number is :" + getAccountNumber());
        moneyTransferPage.selectAccountDD(getAccountNumber());
        fluentWaitTillInVisibilityOfLoader(driver);
        String available[] = moneyTransferPage.availableBalance.getText().split(":");
        String availableBalance = available[1].replace("₹", "").replace(",", "");
        balanceAvailable = Double.parseDouble(availableBalance);
        logger.info("Available balance is :" + balanceAvailable);

    }

    @And("User select payment mode")
    public void userSelectPaymentMode() {
        if (moneyTransferPage.detailsContainer.size() != 1) {
            moneyTransferPage.selectPaymentMode(fileReader.moneyTransferTestData.get("paymentMode"));
            logger.info("The user chooses the mode of payment to transfer :" + fileReader.moneyTransferTestData.get("paymentMode"));

        }
        moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.moneyTransferTestData.get("transferAmount"));
        logger.info("Transferred amount is Rs." + fileReader.moneyTransferTestData.get("transferAmount"));
        staticWait(2000);
    }


    @And("User select {string} scheduled payment")
    public void userSelectScheduledPayment(String scheduledType) {
        LocalDate scheduledPaymentDate = null;
        //  if (fileReader.moneyTransferTestData.get("scheduledPayment").contains("on")) {
        logger.info("The transaction has been scheduled");
        moneyTransferPage.scheduledPaymentToggle.click();
        if (scheduledType.equalsIgnoreCase("one time")) {
            moneyTransferPage.onceOrRecurring(scheduledType).click();

        } else if (scheduledType.equalsIgnoreCase("recurring")) {
            logger.info("A Recurring scheduled transaction has been selected.");
            waitForPageLoad(driver);
            moneyTransferPage.clickScheduledFrequency.click();
            moneyTransferPage.selectScheduledFrequency(fileReader.moneyTransferTestData.get("frequentPayment"));
            moneyTransferPage.scheduledRecurringNoOfPayment.sendKeys(fileReader.moneyTransferTestData.get("noOfPayemnt"));
        }
//Enter scheduled date
        fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.clickOnCalendarIcon);
        homePage.clickOnCalendarIcon.click();
        fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthYear);
        homePage.calendarMonthYear.click();
        fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthYear);
        homePage.calendarMonthYear.click();
        while (true) {
            if (scheduledType.equalsIgnoreCase("one time")) {
                scheduledPaymentDate = getUpComingDate(1);
            } else if (scheduledType.equalsIgnoreCase("recurring")) {
                scheduledPaymentDate = getUpComingDate(5);
            }
            String scheduledDate = getFormatedDate(scheduledPaymentDate, "dd/MMMM/yyyy");
            setDate(scheduledDate);
            logger.info("Today's date is " + scheduledDate);
            logger.info("Paycheck Issue Date is " + scheduledDate);
            String[] expectDateArr = scheduledDate.split("/");
            String expectedYear = expectDateArr[2].trim();
            String expectedMonth = expectDateArr[1].trim();
            String expectedDate = expectDateArr[0].trim();
            logger.info("Expected Year is " + expectedYear);
            String actualYears = homePage.calendarMonthYear.getText().trim();
            String[] yearRange = actualYears.split("–");
            logger.info("Between Years " + actualYears);
            String startingYear = yearRange[0].trim();
            String endingYear = yearRange[1].trim();
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
        logger.info("A one-time scheduled transaction has been selected");
        moneyTransferPage.transferAmount.sendKeys("18");
        staticWait(7000);
    }

    @And("User verify schedule transactions")
    public void userVerifyScheduleTransactions() {
        moneyTransferPage.scheduleTransactionsTab.click();
        softAssert = new SoftAssert();
        String scheduleDate = moneyTransferPage.scheduledTransactionDateFirstRow.getText();
        String scheduleToAccountNumber = moneyTransferPage.scheduledTransactionToAccFirstRow.getText();
        String scheduleSeriesOfPayment = moneyTransferPage.scheduledTransactionPaymentSeriesFirstRow.getText();
        String scheduledPaymentMode = moneyTransferPage.scheduledTransactionPaymentModeFirstRow.getText();
        String scheduledAmount = moneyTransferPage.scheduledTransactionPaymentAmountFirstRow.getText();
        moneyTransferPage.scheduledTransactionFirstRowInHomePage.click();
        explicitWait(driver, 2);
        softAssert.assertTrue(moneyTransferPage.transactionToAccount.getText().contains(scheduleToAccountNumber), "scheduled transaction amount not matched");
        softAssert.assertTrue(moneyTransferPage.transactionPaymentMode.getText().contains(scheduledPaymentMode), "scheduled payment mode not same");
        // softAssert.assertTrue(moneyTransferPage.scheduledTransactionFirstRowInHomePage.getText().trim().contains(firstRow),"scheduled transaction details not matched");
        softAssert.assertTrue(moneyTransferPage.scheduledTransactionSeries.getText().contains(scheduleSeriesOfPayment), "scheduled series of payment not same");
        softAssert.assertTrue(moneyTransferPage.scheduledTransactionAmount.getText().contains(scheduledAmount), "scheduled transaction amount not same");
        softAssert.assertTrue(moneyTransferPage.scheduledTransactionAmount.getText().contains(scheduleDate), "scheduled transaction date not matched");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify  scheduled payment summary")
    public void userVerifyScheduledPaymentSummary() {
        softAssert = new SoftAssert();
        if (driver.getCurrentUrl().contains("transfer-to-payee")) {
            logger.info("The user verify the transfer to payee page summary details");
            String payeeAccNo[] = moneyTransferPage.selectedAccountNumber.getText().split("-");
            toPayeeAccNo = payeeAccNo[1].trim();
        }
        if (driver.getCurrentUrl().contains("transfer-to-self")) {
            softAssert.assertTrue(moneyTransferPage.toPayeeAccountNumber.getText().contains(moneyTransferPage.toAccountNumberTransferSelfAccNo.getText()), "to account number not matched");
        } else {
            String toAccountNumberExtract = moneyTransferPage.getToAccNo.getText().replaceAll("[^0-9]", "");
            logger.info("To Account Number is : " + toAccountNumberExtract);
            softAssert.assertEquals(toAccountNumberExtract.trim(), moneyTransferPage.toPayeeAccountNumber.getText().trim(), "summary to payee account number not matched ");
        }
        if (fileReader.moneyTransferTestData.get("scheduledPayment").contains("on")) {
            softAssert.assertTrue(moneyTransferPage.scheduledSummaryDate.getText().contains((moneyTransferPage.scheduledDate.getText())), "scheduled date not matched");
            if (fileReader.moneyTransferTestData.get("scheduledType").contains("Recurring")) {
                softAssert.assertTrue(moneyTransferPage.scheduledSummaryFrequency.getText().contains(moneyTransferPage.clickScheduledFrequency.getText()), "frequency not matched");
                softAssert.assertTrue(moneyTransferPage.scheduledSummaryNoOfPayments.getText().contains(moneyTransferPage.scheduledRecurringNoOfPayment.getText()), "no of payments not matched");
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

    @And("User verify payment summary for common steps")
    public void userVerifyPaymentSummaryForCommonSteps() {
        if (driver.getCurrentUrl().contains("quick-acc-transfer")) {
            softAssert.assertTrue(moneyTransferPage.toPayeeAccountNumber.getText().contains(moneyTransferPage.payeeAccountNumberQuickAccNo.getAttribute("value")), "payee account number not matched");
        }
        staticWait(2000);
        String inputAmount = moneyTransferPage.transferAmount.getAttribute("value") + ".00";
        logger.info("Transferred Amount is " + inputAmount);
        transferredAmount = moneyTransferPage.sendingAmount.getText().replace("₹", "").replaceAll(",", "").trim();
        double transferredAmountInt = Double.parseDouble(inputAmount);
        softAssert.assertTrue(transferredAmountInt < balanceAvailable, "sending amount more than available balance");
        softAssert.assertEquals(transferredAmount, (inputAmount), "sending amount not matched");
        softAssert.assertTrue(moneyTransferPage.fromAccountNumber.getText().contains(moneyTransferPage.clickFromAccount.getText()), "From account number not matched");
        softAssert.assertEquals(moneyTransferPage.remarks.getAttribute("value").trim(), moneyTransferPage.summaryRemarks.getText().trim(), "remarks details not matched");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User stored the values in a variable")
    public void userStoredTheValuesInAVariable() {
        transferredAmount = moneyTransferPage.sendingAmount.getText();
        summaryFromAccount = moneyTransferPage.fromAccountNumber.getText();
        summaryToPayeeAccount = moneyTransferPage.toPayeeAccountNumber.getText();
        summaryRemarks = moneyTransferPage.summaryRemarks.getText();
        logger.info("Transferred Amount is :" + transferredAmount);
        logger.info("Transferred From account is :" + summaryFromAccount);
        logger.info("Transferred to account is :" + summaryToPayeeAccount);
        logger.info("Transferred Remarks is " + summaryRemarks);

    }

    @And("User clicks make payment button")
    public void userClicksMakePaymentButton() {
        currentUrl = driver.getCurrentUrl();
        fluentWaitTillVisibilityElement(driver, 10, 1, moneyTransferPage.makePaymentButton);
        moneyTransferPage.makePaymentButton.click();
    }

    @And("User verify transaction details page")
    public void userVerifyTransactionDetailsPage() {
        softAssert = new SoftAssert();
        try {
            fluentWaitTillInVisibilityOfLoader(driver);
            transactionSuccess = moneyTransferPage.transactionSuccessfulMessage.getText();
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Navigation to transaction details page is failed");
        }
        if (transactionSuccess.contains("Recurring")) {
            moneyTransferPage.transactionReferenceNumberCopyButton.click();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOf(moneyTransferPage.transactionReferenceNumberCopyMessage));
            softAssert.assertTrue(moneyTransferPage.transactionReferenceNumberCopyMessage.getText().contains("Copied successfully"), "transfer details page copy successfully message not displayed");
            transactionReferenceNo = moneyTransferPage.transactionReferenceNumber.getText();
            transferredDateTime = moneyTransferPage.transferredDateAndTime.getText();
            logger.info("The transaction reference number of Recurring is :" + transactionReferenceNo);
            logger.info("The transaction date and time Recurring is :" + transferredDateTime);

        } else {
            softAssert.assertTrue(driver.getCurrentUrl().contains("transaction-status"), "transfer details page navigates to transaction details page failed");
            softAssert.assertTrue(moneyTransferPage.transactionDetailsHeader.isDisplayed(), "transaction details header not displayed");
            softAssert.assertTrue(moneyTransferPage.transactionSuccessfulMessage.getText().contains("uccessful"), "transaction details successful message not displayed");
            softAssert.assertTrue(moneyTransferPage.transactionAmount.getText().contains(transferredAmount), "transaction details amount not matched");
            softAssert.assertEquals(moneyTransferPage.transactionToAccount.getText(), summaryToPayeeAccount, "transfer details payee account not matched");
            softAssert.assertTrue(moneyTransferPage.transactionFromAccount.getText().contains(summaryFromAccount), "transfer details page from account not matched");
            softAssert.assertTrue(moneyTransferPage.paymentMode.isDisplayed(), "transfer details page payment mode not displayed");
            paymentMode = moneyTransferPage.paymentMode.getText();
            transferredAmountInTransactionStatusPage = moneyTransferPage.transactionAmount.getText().replaceAll(",", "").replaceAll("₹", "").trim();
            logger.info("In Transaction details page, transferred amount is :" + moneyTransferPage.transactionAmount.getText());
            logger.info("In Transaction details page,to account is :" + moneyTransferPage.transactionToAccount.getText());
            logger.info("In Transaction details page,from account is :" + moneyTransferPage.transactionFromAccount.getText());
            //        if (transactionSuccess.equals("Transaction successful")) {
            //    waitTillElementToBeClickable(driver, moneyTransferPage.transactionReferenceNumberCopyButton);
            fluentWaitTillInVisibilityOfLoader(driver);
            moneyTransferPage.transactionReferenceNumberCopyButton.click();
            waitTillVisibilityElement(driver, moneyTransferPage.transactionReferenceNumberCopyMessage);
            softAssert.assertTrue(moneyTransferPage.transactionReferenceNumberCopyMessage.getText().contains("uccessfully"), "transfer details copy successfully message not displayed");
            transactionReferenceNo = moneyTransferPage.transactionReferenceNumber.getText();
            String copiedValue = getCopiedValue().trim();
            softAssert.assertEquals(transactionReferenceNo.trim(), copiedValue, "copied reference number not be the same");
            logger.info("In Transaction details page,transactionReference No is :" + transactionReferenceNo);
            logger.info(" Reference Number Copy Number :" + copiedValue);

            transferredDateTime = moneyTransferPage.transferredDateAndTime.getText();
            transactionSuccess = moneyTransferPage.transactionSuccessfulMessage.getText();
            transactionPayeeNameStatusPage = moneyTransferPage.transactionToAccountPayeeName.getText();

            logger.info("In Transaction details page,transaction date and time is :" + transferredDateTime);
            logger.info("In Transaction details page,transaction status is :" + transactionSuccess);
            if (currentUrl.contains("quick-acc-transfer")) {
                try {
                    moneyTransferPage.transactionDetailsAddPayeeQkTrans.click();
                    waitTillInvisibilityOfLoader(driver);
                    softAssert.assertTrue(managePayeePage.addNewPayeeTittle.getText().contains("Add New Payee"), "transfer details add new payee page not visible");
                    softAssert.assertTrue(managePayeePage.payeeDetails.isDisplayed(), "transfer details payee details not visible");
                    moneyTransferPage.backButton.click();
                    if (moneyTransferPage.leaveButton.isDisplayed()) {
                        moneyTransferPage.leaveButton.click();
                        waitTillInvisibilityOfLoader(driver);
                    }
                } catch (NoSuchElementException e) {
                    logger.info("The account number has already been added to the payee");
                }
                fluentWaitTillInVisibilityOfLoader(driver);
            }
        }
        try {
            softAssert.assertAll();
        } catch (
                AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("Verify the download in transaction details page")
    public void verifyTheDownloadInTransactionDetailsPage() {
        softAssert = new SoftAssert();
        if (transactionSuccess.equals("Transaction successful")) {
            moneyTransferPage.transactionDownloadButton.click();
            staticWait(3000);
            File transactionFile = new File("C:\\Users\\987993\\Downloads\\document.pdf");
            softAssert.assertTrue(transactionFile.exists(), "transaction statement file download failed");
            logger.info("The downloaded file location is :" + transactionFile);
            if (transactionFile.exists()) {
                transactionFile.delete();
                // }
            }
            waitTillInvisibilityOfLoader(driver);
        }

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks repeat button")
    public void userClicksRepeatButton() {
        if (transactionSuccess.equals("Transaction successful") | transactionSuccess.equals("Transaction Failed")) {
            fluentWaitTillInVisibilityOfLoader(driver);
            scrollIntoViewUp(driver, moneyTransferPage.pageHeader);
            moneyTransferPage.transactionRepeatButton.click();
            fluentWaitTillInVisibilityOfLoader(driver);
        }
    }

    @And("User verify available balance with debited amount")
    public void userVerifyAvailableBalanceWithDebitedAmount() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, moneyTransferPage.pageHeader);
        if (transactionSuccess.equals("Transaction successful") | transactionSuccess.equals("Transaction Failed")) {
            String transferAmountInStr = fileReader.moneyTransferTestData.get("transferAmount");
            double transferAmount = Double.parseDouble(transferredAmountInTransactionStatusPage);
            staticWait(3000);
            String afterTransferSt[] = moneyTransferPage.availableBalance.getText().split(":");
            String afterTransferStr = afterTransferSt[1].replace(" ₹", "").replace(",", "").trim();
            double afterTransferAvailBalance = Double.parseDouble(afterTransferStr);
            logger.info("Transaction Amount is Rs." + transferAmount);
            logger.info("Transaction Status  " + transactionSuccess);
            if (transactionSuccess.contains("Transaction successful")) {
                double beforePrevBal = balanceAvailable - transferAmount;
                softAssert.assertEquals(beforePrevBal, afterTransferAvailBalance, "transferred amount not debited from available balance");
                logger.info("Before transaction available balance is Rs." + beforePrevBal);
                logger.info("After transaction available balance is Rs." + afterTransferAvailBalance);
            } else {
                softAssert.assertTrue(balanceAvailable == afterTransferAvailBalance, "failed transaction amount debited from available balance");
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

    @And("User select payee for repeat payment")
    public void userSelectPayeeForRepeatPayment() {
        moneyTransferPage.clickSelectPayee.click();
        // moneyTransferPage.selectPayeeFromDD("Pradip");
        logger.info("For Repeat Payment payee name is :" + moneyTransferPage.selectPayeeIndexSecondPayee.getText());
        moneyTransferPage.selectPayeeIndexSecondPayee.click();
    }

    @And("User clicks back to money transfer home page")
    public void userClicksBackToMoneyTransferHomePage() {
        //   moneyTransferPage.backToMoneyTransferButton.click();
        try {
            if (moneyTransferPage.leaveButton.isDisplayed()) {
                moneyTransferPage.leaveButton.click();
                fluentWaitTillInVisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException e) {
        }
        fluentWaitTillTheElementToBeClickable(driver, 20, 1, homePage.moneyTransferButton);
        clickOnButton(homePage.moneyTransferButton);
        fluentWaitTillInVisibilityOfLoader(driver);
        try {
            if (moneyTransferPage.leaveButton.isDisplayed()) {
                moneyTransferPage.leaveButton.click();
                waitTillInvisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException e) {
        }
    }

    @Then("User verify transaction section")
    public void userVerifyTransactionSection() {
        softAssert = new SoftAssert();
        scrollIntoView(driver, moneyTransferPage.recentTransactionTab);
        softAssert.assertTrue(moneyTransferPage.successMessageRecentTransaction.getText().contains("Transferred"), "transaction success message displayed");
        softAssert.assertTrue(moneyTransferPage.amountInRecentTransaction.getText().contains(transferredAmount), "transferred amount not matched");
        String transactionTimeInStatusPageSplit[] = transferredDateTime.split("on");
        String transactionTimeInStatusPage = transactionTimeInStatusPageSplit[1];
        String transactionSectionDateAndTime = moneyTransferPage.transactionDateInRecentTransaction.getText() + " at " + moneyTransferPage.transactionTimeInRecentTransaction.getText();
        softAssert.assertEquals(transactionSectionDateAndTime.trim(), transactionTimeInStatusPage.trim(), "transaction date and time in recent transaction section and transaction status page not be the same");
        // softAssert.assertTrue(moneyTransferPage.transactionPayeeNameInRecentTransaction.getText().trim().contains(transactionPayeeNameStatusPage.trim()),"payee name in recent transaction in be the same with transaction status page ");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks view button")
    public void userClicksViewButton() {
        if (transactionSuccess.contains("Schedule")) {
            moneyTransferPage.scheduleTransactionsTab.click();
            waitTillInvisibilityOfLoader(driver);
        } else {
            moneyTransferPage.viewButton.click();
            waitTillInvisibilityOfLoader(driver);
        }
    }

    @Then("User verify transaction popup")
    public void userVerifyTransactionPopup() {
        if (transactionSuccess.equals("Transaction successful")) {
            softAssert = new SoftAssert();
            softAssert.assertTrue(moneyTransferPage.transactionPopupSuccess.getText().contains("Transferred"), "transaction popup success message not displayed");
        }
        softAssert.assertEquals(moneyTransferPage.transferredDateAndTime.getText(), transferredDateTime, "transaction date time not matched");
        softAssert.assertTrue(moneyTransferPage.transactionPopupAmount.getText().contains(moneyTransferPage.amountInRecentTransaction.getText()), "transaction popup amount not matched");
        softAssert.assertEquals(moneyTransferPage.transactionPopupFromAccount.getText(), summaryFromAccount, "transaction popup from account number not matched");
        softAssert.assertEquals(moneyTransferPage.transactionToAccount.getText(), summaryToPayeeAccount, "transaction popup payee account number not matched");
        softAssert.assertEquals(moneyTransferPage.transactionReferenceNumber.getText(), transactionReferenceNo, "transaction popup reference number not matched");
        moneyTransferPage.transactionReferenceNumberCopyButton.click();
        staticWait(2000);
        softAssert.assertTrue(moneyTransferPage.transactionReferenceNumberCopyMessage.isDisplayed(), "reference number copied message not displayed");
        transactionReferenceNo = moneyTransferPage.transactionReferenceNumber.getText();
        String copiedValue = getCopiedValue().trim();
        softAssert.assertEquals(transactionReferenceNo.trim(), copiedValue, "copied reference number not be the same");
        softAssert.assertEquals(moneyTransferPage.paymentMode.getText(), paymentMode, "transaction popup payment mode not match");
        softAssert.assertEquals(moneyTransferPage.transactionPopupRemarks.getText(), summaryRemarks, "transaction popup remarks  not matched");
        logger.info("Transaction pop up,transaction date and time is :" + moneyTransferPage.transferredDateAndTime.getText());
        logger.info("Transaction pop up,transaction amount is :" + moneyTransferPage.transactionPopupAmount.getText());
        logger.info("Transaction pop up,from account is :" + moneyTransferPage.transactionPopupFromAccount.getText());
        logger.info("Transaction pop up,to account is :" + moneyTransferPage.transactionToAccount.getText());
        logger.info("Transaction pop up,transaction reference number is " + moneyTransferPage.transactionReferenceNumber.getText());


        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify schedule transaction paid popup")
    public void userVerifyScheduleTransactionPaidPopup() {
        softAssert = new SoftAssert();
        if (transactionSuccess.contains("Recurring")) {
            scrollIntoView(driver, moneyTransferPage.paidTab);
            moneyTransferPage.paidTab.click();
            waitForPageLoad(driver);
            moneyTransferPage.viewButton.click();
            waitForPageLoad(driver);
            softAssert.assertTrue(moneyTransferPage.transactionAmount.getText().contains(moneyTransferPage.paidTabAmount.getText()), "recurring scheduled transaction amount not matched");
            softAssert.assertTrue(moneyTransferPage.transactionToAccount.getText().contains(moneyTransferPage.paidTabPayeeName.getText()), "recurring scheduled payee name not matched");
            softAssert.assertTrue(moneyTransferPage.transactionFromAccount.getText().contains(summaryFromAccount), "recurring scheduled from account number not matched");
            softAssert.assertTrue(moneyTransferPage.transactionReferenceNumber.getText().contains(transactionReferenceNo), "recurring scheduled transaction reference number not matched");
            softAssert.assertTrue(moneyTransferPage.transactionRemarks.getText().contains(summaryRemarks), "recurring scheduled transaction remarks not matched");
            moneyTransferPage.closeButton.click();

        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on quick account transfer tab")
    public void userClicksOnQuickAccountTransferTab() {
        moneyTransferPage.quickTransferTab.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify quick account transfer page")
    public void userVerifyQuickAccountTransferPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("quick-acc-transfer"), "navigates to quick account transfer failed");
        softAssert.assertTrue(moneyTransferPage.quickTransferHeader.isDisplayed(), "quick transfer header not displayed ");
        softAssert.assertTrue(moneyTransferPage.clickPayeeBank.isDisplayed(), "select payee bank tab not visible");
        softAssert.assertTrue(moneyTransferPage.clickAccType.isDisplayed(), "select account type tab of payee not visible");
        //     softAssert.assertTrue(moneyTransferPage.payAccountOrMobileNumberInput.isDisplayed(), "payee account number input bar not displayed");
        softAssert.assertTrue(moneyTransferPage.clickFromAccount.isDisplayed(), "from account bar not displayed");
        softAssert.assertTrue(moneyTransferPage.transferAmount.isDisplayed(), "transfer amount bar not displayed");
        softAssert.assertTrue(moneyTransferPage.paymentSummary.isDisplayed(), "payment summary header not displayed");
        try {
            softAssert.assertAll();
        } catch (
                AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User select {string} from the list")
    public void userSelectFromTheList(String payeeBankName) {
        scrollIntoViewUp(driver, moneyTransferPage.pageHeader);
        moneyTransferPage.clickPayeeBank.click();
        if (payeeBankName.contains("au bank payee")) {
            moneyTransferPage.selectPayeeBankName(fileReader.moneyTransferTestData.get("bankName"));
        } else if (payeeBankName.contains("other bank payee")) {
            moneyTransferPage.selectPayeeBankName(fileReader.moneyTransferTestData.get("quickTransferOtherBankName"));
        }
    }

    @And("User select all needed details in quick account transfer page")
    public void userSelectAllNeededDetailsInQuickAccountTransferPage() {
        String bankName = moneyTransferPage.getSelectedPayeeBankName.getText();
        logger.info("Payee Bank Name:" + bankName);
//Here need first selected
        moneyTransferPage.clickFromAccount.click();
        //      moneyTransferPage.selectAccountDD(fileReader.moneyTransferTestData.get("fromAccount"));
        moneyTransferPage.selectAccountDD(fileReader.moneyTransferTestData.get("quickTransferAccountNumber"));
        waitTillInvisibilityOfLoader(driver);

        if (bankName.contains("AU Small Finance Bank Limited")) {
            String accOrMobile = "Account Number";
            moneyTransferPage.selectRadioButton(accOrMobile);
            //If chose Acc Number
            if (accOrMobile.contains("Account Number")) {
                moneyTransferPage.payAccountOrMobileNumberInput.sendKeys(fileReader.moneyTransferTestData.get("auBankAccountNo"));
                waitTillInvisibilityOfLoader(driver);
                auBankPayeeName = moneyTransferPage.getAuPayeeName.getText();
                waitTillInvisibilityOfLoader(driver);
            }
            //If Choose Mobile Number
            else {
                moneyTransferPage.payAccountOrMobileNumberInput.sendKeys(fileReader.moneyTransferTestData.get("auBankMobileNo"));
                waitTillInvisibilityOfLoader(driver);
                moneyTransferPage.clickAuPayeeAccountNumber.click();
                /** Given Below account based on mobile number    **/
                moneyTransferPage.selectAccountFromDD(fileReader.moneyTransferTestData.get("payeeAccountByMobileNo"));
                waitTillInvisibilityOfLoader(driver);

            }
        } else {
            moneyTransferPage.clickAccType.click();
            moneyTransferPage.selectAccTye("Savings");
            /****
             * Here below code represent If chose bank is other and if ask IFSC or Branch option
             */
            try {
                if (moneyTransferPage.selectIfscOrBranch.isDisplayed()) {
                    if (fileReader.moneyTransferTestData.get("transferMode").equalsIgnoreCase("IFSC")) {
                        moneyTransferPage.selectIfscOrBranch.sendKeys(fileReader.moneyTransferTestData.get("ifscCode"));
                        waitTillInvisibilityOfLoader(driver);
                        /***
                         * Below steps for to verify the Find IFSC code option
                         */

                    } else {
                        moneyTransferPage.selectIfscOrBranch.sendKeys("");
                    }
                }
            } catch (NoSuchElementException e) {
                logger.info("Selected Bank account no need to ifsc code");
            }
            moneyTransferPage.otherBankAccountNumberInput.sendKeys(fileReader.moneyTransferTestData.get("otherBankAccNo"));
            moneyTransferPage.verifyButton.click();
            waitTillInvisibilityOfLoader(driver);
            try {
                if (moneyTransferPage.enterDetailsManually.isDisplayed()) {
                    waitTillVisibilityElement(driver, moneyTransferPage.enterDetailsManually);
                    moneyTransferPage.enterDetailsManually.click();
                    waitTillVisibilityElement(driver, moneyTransferPage.reEnterAccNumber);
                    moneyTransferPage.reEnterAccNumber.sendKeys(fileReader.moneyTransferTestData.get("otherBankAccNo"));
                    moneyTransferPage.payeeName.sendKeys(fileReader.moneyTransferTestData.get("selectPayeeName"));
                    waitForPageLoad(driver);
                }
            } catch (NoSuchElementException e) {
                // attachScreenshot(driver, scenario);
                //  System.out.println("Enter Account number verification failed");
            }
            //  waitTillInvisibilityOfLoader(driver);
        }
        //Common
        String availableBalance = moneyTransferPage.availableBalance.getText().replace("\\n", "").replaceAll("[^\\d.]", "");
        balanceAvailable = Double.parseDouble(availableBalance);
        moneyTransferPage.transferAmount.sendKeys(fileReader.moneyTransferTestData.get("transferAmount"));
        if (bankName.contains("AU Small Finance Bank Limited")) {
            System.out.println("AU BANK ACCOUNT");
        } else {
            moneyTransferPage.selectPaymentMode(fileReader.moneyTransferTestData.get("paymentMode"));
            moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.moneyTransferTestData.get("transferAmount"));
            logger.info("Transferred amount is Rs." + fileReader.moneyTransferTestData.get("transferAmount"));
            staticWait(2000);
        }
//        String payeeName = moneyTransferPage.payNameQuickTransfer.getText().substring(0,4);
//        String trimFirstFiveLetters = String.format("%4s", payeeName);
//        logger.info(trimFirstFiveLetters);
//        moneyTransferPage.remarks.sendKeys(Keys.chord(Keys.CONTROL, "a"), "quick transfer to " + payeeName);

    }

    @When("User clicks transfer to self tab")
    public void userClicksTransferToSelfTab() {
        if (getNoOfCasa() > 1) {
            Assert.assertTrue(moneyTransferPage.transferToSelfTab.isDisplayed(), "CIFF Contain only one CASA transfer to self not possible");
            if (moneyTransferPage.transferToSelfTab.isEnabled()) {
                moneyTransferPage.transferToSelfTab.click();
                waitTillInvisibilityOfLoader(driver);
            } else {
                softAssert.fail("Transfer to self tab not enabled ");
                attachScreenshot(driver, scenario);
                logger.debug("Transfer to self tab not enable");
            }
        }
    }

    @Then("User verify transfer to self page")
    public void userVerifyTransferToSelfPage() {
        softAssert = new SoftAssert();
        logger.info("Current Scenario is :" + moneyTransferPage.transferToSelfHeader.getText());
        softAssert.assertTrue(driver.getCurrentUrl().contains("transfer-to-self"), "navigates to transfer to self not displayed");
        try {
            softAssert.assertTrue(moneyTransferPage.transferToSelfHeader.isDisplayed(), "transfer to self page header not displayed");
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Transfer to self page header is not displayed");
        }
        softAssert.assertTrue(moneyTransferPage.clickToAccount.isDisplayed(), "transfer to self to account bar not displayed");
        softAssert.assertTrue(moneyTransferPage.clickFromAccount.isDisplayed(), "from account bar not displayed");
        softAssert.assertTrue(moneyTransferPage.transferAmount.isDisplayed(), "transfer amount bar not displayed");
        softAssert.assertTrue(moneyTransferPage.paymentSummary.isDisplayed(), "payment summary header not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User User select all needed details in transfer to self page")
    public void userUserSelectAllNeededDetailsInTransferToSelfPage() {
        moneyTransferPage.clickToAccount.click();
        logger.info("Transfer to self ,to account number is :" + fileReader.moneyTransferTestData.get("selfTransferToAcc"));
        moneyTransferPage.selectAccountDD(getCurrentAccountNumber());
        waitTillInvisibilityOfLoader(driver);
        moneyTransferPage.clickFromAccount.click();
        logger.info("Transfer to self ,from account number is :" + fileReader.moneyTransferTestData.get("fromAccount"));
        moneyTransferPage.selectAccountFromDD(getSavingAccountNumber());
        waitTillInvisibilityOfLoader(driver);
        String availableBalance = moneyTransferPage.availableBalance.getText().replace("\\n", "").replaceAll("[^\\d.]", "");
        balanceAvailable = Double.parseDouble(availableBalance);
        logger.info("Avaiable balance is Rs." + balanceAvailable);
        //System.out.println("Available Balance :"+availableBalance);
        moneyTransferPage.transferAmount.sendKeys(fileReader.moneyTransferTestData.get("transferAmount"));
    }

    @When("User clicks multiple payment tab")
    public void userClicksMultiplePaymentTab() {
        moneyTransferPage.multiplePaymentsTab.click();
    }

    @Then("User verify multiple payment page")
    public void userVerifyMultiplePaymentPage() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, homePage.backButton);
        logger.info("Current Scenario is :" + moneyTransferPage.multiplePaymentPageHeader.getText());
        softAssert.assertTrue(driver.getCurrentUrl().contains("multiple-payments"), "navigates to multiple payment failed");
        softAssert.assertTrue(moneyTransferPage.multiplePaymentPageHeader.isDisplayed(), "multiple payments header not visible");
        softAssert.assertTrue(moneyTransferPage.addPaymentsMessage.isDisplayed(), "at least add 2 payee message not displayed");
        softAssert.assertTrue(moneyTransferPage.addAnotherPaymentButton.isDisplayed(), "add another payment button not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User select all needed details for multiple payment")
    public void userSelectAllNeededDetailsForMultiplePayment() {
        moneyTransferPage.clickSelectPayee.click();
        String payeeNickName = "Name";
        moneyTransferPage.selectPayeeFromDD(payeeNickName).click();
        moneyTransferPage.clickFromAccount.click();
        moneyTransferPage.selectAccountDD("2302201144496284");
        //waitTillInvisibilityOfLoader(driver);
        String availableBalance = moneyTransferPage.availableBalance.getText().replace("\\n", "").replaceAll("[^\\d.]", "");
        balanceAvailable = Double.parseDouble(availableBalance);
        System.out.println("Available Balance :" + availableBalance);
        moneyTransferPage.transferAmount.sendKeys(fileReader.moneyTransferTestData.get("transferAmount"));

    }

    @And("User clicks add another payment button")
    public void userClicksAddAnotherPaymentButton() {
        moneyTransferPage.addAnotherPaymentButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User clicks add payment button")
    public void userClicksAddPaymentButton() {
        moneyTransferPage.addPaymentButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the multiple payment page after add payments")
    public void userVerifyTheMultiplePaymentPageAfterAddPayments() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(moneyTransferPage.totalAmountPayable.getText().contains("₹"), "amount not displayed");
        String totalAmountPayable = moneyTransferPage.totalAmountPayable.getText().replace("₹", "").replaceAll(",", "").trim();
        double totalAmountPayInt = Double.parseDouble(totalAmountPayable);
        String[] paymentSize = moneyTransferPage.noOfPaymentInTheList.getText().replace("Payment added", "").split("/");
        String paymentLength = paymentSize[0].trim();
        noOfPaymentInList = Integer.parseInt(paymentLength);
        softAssert.assertEquals(noOfPaymentInList, moneyTransferPage.paymentList.size(), "no of payments lists are not matched");

        double totalAmount = 0;
        for (WebElement amount : moneyTransferPage.paymentAmount) {
            String listAmount = amount.getText().replace("₹", "").replaceAll(",", "").trim();
            double totalAmountInt = Double.parseDouble(listAmount);
            totalAmount = totalAmountInt + totalAmount;
        }
        softAssert.assertEquals(totalAmount, totalAmountPayInt, "total amount not same");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify multiple pay transaction details page")
    public void userVerifyMultiplePayTransactionDetailsPage() {
        waitTillInvisibilityOfLoader(driver);
        softAssert = new SoftAssert();
        softAssert.assertTrue(moneyTransferPage.transactionStatus.getText().contains("Multiple Payments"), "multiple payment transaction failed");
        softAssert.assertEquals(moneyTransferPage.viewButtonList.size(), noOfPaymentInList, "payments list not same");
        logger.info(moneyTransferPage.viewButtonList.size());
        logger.info(noOfPaymentInList);

        ListIterator<WebElement> viewButtonList = moneyTransferPage.payeeAddedListContainer.listIterator();
        ListIterator<WebElement> toAccountList = moneyTransferPage.multipleTransactionListToAccount.listIterator();
        ListIterator<WebElement> amountList = moneyTransferPage.multipleTransactionAmountList.listIterator();
        ListIterator<WebElement> fromAccountList = moneyTransferPage.multipleTransactionListFromAccount.listIterator();
        ListIterator<WebElement> transactionModeList = moneyTransferPage.multipleTransactionModeList.listIterator();

        while (viewButtonList.hasNext() && toAccountList.hasNext() && amountList.hasNext() && fromAccountList.hasNext() && transactionModeList.hasNext()) {
            WebElement clickViewButton = viewButtonList.next();
            clickViewButton.click();
            WebElement toAccount = toAccountList.next();
            WebElement fromAccount = fromAccountList.next();
            WebElement amountTransfer = amountList.next();
            WebElement modeOfPay = transactionModeList.next();
            //    softAssert.assertTrue(moneyTransferPage.transactionPopupSuccess.getText().contains("uccess"), "transaction success message not displayed");
            softAssert.assertEquals(moneyTransferPage.transactionPopupAmount.getText(), amountTransfer.getText(), "transaction amount in popup not matched");
            softAssert.assertEquals(moneyTransferPage.transactionPopupFromAccount.getText(), fromAccount.getText(), "from account number not matched");
            transferredDateTime = moneyTransferPage.transferredDateAndTime.getText();
            paymentMode = moneyTransferPage.paymentMode.getText();
            softAssert.assertEquals(moneyTransferPage.paymentMode.getText(), modeOfPay.getText(), "payment mode not match");
            softAssert.assertEquals(moneyTransferPage.transactionToAccount.getText(), toAccount.getText(), "payee to account not matched");
            transactionReferenceNo = moneyTransferPage.transactionReferenceNumber.getText();
            moneyTransferPage.transactionReferenceNumberCopyButton.click();
            waitTillVisibilityElement(driver, moneyTransferPage.transactionReferenceNumberCopyMessage);
            softAssert.assertTrue(moneyTransferPage.transactionReferenceNumberCopyMessage.isDisplayed(), "copied message not displayed");
            String copiedValue = getCopiedValue().trim();
            softAssert.assertEquals(transactionReferenceNo.trim(), copiedValue, "copied reference number not be the same");

/******** Download functions are added in separate file ***********/
//            if (moneyTransferPage.transactionPopupSuccess.getText().contains("uccess")) {
//                moneyTransferPage.transactionDownloadButton.click();
//                staticWait(3000);
//                File transactionFile = new File("C:\\Users\\987993\\Downloads\\document.pdf");
//                softAssert.assertTrue(transactionFile.exists(), "transaction statement file download failed");
//                if (transactionFile.exists()) {
//                    transactionFile.delete();
//                    waitTillInvisibilityOfLoader(driver);
//                }
//            }

            moneyTransferPage.closeButton.click();
            waitForPageLoad(driver);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks back to money transfer page button")
    public void userClicksBackToMoneyTransferPageButton() {
        moneyTransferPage.backToMoneyTransferButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User select all needed details and add payees")
    public void userSelectAllNeededDetailsAndAddPayees() {
        softAssert = new SoftAssert();
        int noOfPayees = 2;
        String payeeNickName;

        for (int i = 0; i < noOfPayees; i++) {
            fluentWaitTillTheElementToBeClickable(driver, 10, 1, moneyTransferPage.addAnotherPaymentButton);
            moneyTransferPage.addAnotherPaymentButton.click();
            fluentWaitTillTheElementToBeClickable(driver, 10, 1, moneyTransferPage.clickSelectPayee);
            javaScriptExecutorClickElement(driver, moneyTransferPage.clickSelectPayee);
            staticWait(3000);
            moneyTransferPage.selectAccDDByIndex(i);
            try {
                if (moneyTransferPage.payeeMultipleAccPopUp.isDisplayed()) {
                    logger.info("This payee contains Multiple accounts");
                    moneyTransferPage.multipleAccountSelectAccountNoRadioBtn(fileReader.moneyTransferTestData.get("multipleTransferAccountNoChoose"));
                    moneyTransferPage.payeeMultipleAccPopUpConfirmButton.click();
                    waitTillInvisibilityElement(driver, moneyTransferPage.payeeMultipleAccPopUp);
                }
            } catch (NoSuchElementException e) {
                logger.info("Payee doesn't have multiple account");
                //System.out.println("Payee doesn't have multiple account");
            }
            moneyTransferPage.clickFromAccount.click();
            logger.info("Multiple payment transfer from :" + fileReader.moneyTransferTestData.get("fromAccount"));
            moneyTransferPage.selectAccountDD(fileReader.moneyTransferTestData.get("fromAccount"));
            waitTillInvisibilityOfLoader(driver);
            moneyTransferPage.transferAmount.sendKeys("1" + i);
            logger.info("Multiple transfer amount is Rs." + "1" + i);
            String availableBalance = moneyTransferPage.availableBalance.getText().replace("\\n", "").replaceAll("[^\\d.]", "");
            balanceAvailable = Double.parseDouble(availableBalance);
            logger.info("Available balance in account " + fileReader.moneyTransferTestData.get("fromAccount") + " is :" + availableBalance);
            //System.out.println(availableBalance);
            if (moneyTransferPage.detailsContainer.size() != 1) {
                moneyTransferPage.selectPaymentMode("IMPS");
                logger.info("Multiple transfer payment mode is IMPS");
            } else {
                logger.info("To payee have account is on AU Bank");
                // System.out.println("To payee have account is on AU Bank");
            }
            softAssert.assertTrue(moneyTransferPage.getToAccNo.getText().contains(moneyTransferPage.toPayeeAccountNumber.getText()), "multiple a/c transfer summary to payee account number not matched ");
            String inputAmount = moneyTransferPage.transferAmount.getAttribute("value");
            transferredAmount = moneyTransferPage.sendingAmount.getText();
            logger.info("Multiple payment transferred amount is Rs." + transferredAmount);
            softAssert.assertTrue(transferredAmount.contains(inputAmount), "multiple a/c transfer sending amount not matched");
            softAssert.assertTrue(moneyTransferPage.fromAccountNumber.getText().contains(moneyTransferPage.clickFromAccount.getText()), "multiple a/c transfer From account number not matched");
            softAssert.assertEquals(moneyTransferPage.remarks.getAttribute("value").trim(), moneyTransferPage.summaryRemarks.getText().trim(), "multiple a/c transfer remarks details not matched");
            summaryFromAccount = moneyTransferPage.fromAccountNumber.getText();
            summaryToPayeeAccount = moneyTransferPage.toPayeeAccountNumber.getText();
            summaryRemarks = moneyTransferPage.summaryRemarks.getText();
            logger.info("Multiple payment transfer from account is :" + summaryFromAccount);
            logger.info("Multiple payment transfer payee account is :" + summaryToPayeeAccount);
            logger.info("Multiple payment transfer remarks is :" + summaryRemarks);
            moneyTransferPage.addPaymentButton.click();
            waitTillInvisibilityOfLoader(driver);
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify transaction section multiple payment")
    public void userVerifyTransactionSectionMultiplePayment() {
        softAssert = new SoftAssert();
        scrollIntoView(driver, moneyTransferPage.recentTransactionTab);
        //waitTillVisibilityElement(driver,moneyTransferPage.viewButton);
        logger.info("Multiple payment transaction status is " + moneyTransferPage.successMessageRecentTransaction.getText());
        logger.info("Multiple payment transaction amount is Rs." + moneyTransferPage.amountInRecentTransaction.getText());
        softAssert.assertTrue(moneyTransferPage.successMessageRecentTransaction.getText().contains("Transferred"), "multiple a/c transfer transaction failed");
        softAssert.assertTrue(moneyTransferPage.amountInRecentTransaction.getText().contains(transferredAmount), "multiple a/c transfer transferred amount not matched");
        logger.info(moneyTransferPage.amountInRecentTransaction.getText());
        logger.info(transferredAmount);

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify transaction popup for multiple payment")
    public void userVerifyTransactionPopupForMultiplePayment() {
        softAssert = new SoftAssert();
        for (int i = 1; i <= 2; i++) {
            scrollIntoView(driver, moneyTransferPage.recentTransactionTab);
            waitTillElementToBeClickable(driver, moneyTransferPage.viewButton);
            moneyTransferPage.viewMultplePayee(i);
            softAssert.assertTrue(moneyTransferPage.transactionPopupSuccess.getText().contains("Transferred"), "multiple a/c transfer pop up transaction success failed");
            softAssert.assertEquals(moneyTransferPage.transactionPopupAmount.getText(), moneyTransferPage.recentPayeeAmount(i).getText(), "multiple a/c transfer pop up transfer amount not same");
            String[] dateTime = moneyTransferPage.recentPayeeDateAndTime(i).getText().split("\\n");
            String dateTimeStr = "Transferred on" + " " + dateTime[0] + " " + "at" + " " + dateTime[1];
            //System.out.println(dateTimeStr);
            softAssert.assertEquals(moneyTransferPage.transferredDateAndTime.getText(), dateTimeStr, "multiple a/c transfer pop up transaction date and time not matched");
            waitTillInvisibilityOfLoader(driver);

            softAssert.assertTrue(!moneyTransferPage.transactionPopupAmount.getText().isEmpty(), "transaction popup amount not displayed");
            softAssert.assertTrue(!moneyTransferPage.transactionPopupFromAccount.getText().isEmpty(), "transaction popup from account number not displayed");
            softAssert.assertTrue(!moneyTransferPage.transactionToAccount.getText().isEmpty(), "transaction popup payee account number not displayed");
            softAssert.assertTrue(!moneyTransferPage.transactionReferenceNumber.getText().isEmpty(), "transaction popup reference number not displayed");
            moneyTransferPage.transactionReferenceNumberCopyButton.click();
            staticWait(2000);
            softAssert.assertTrue(moneyTransferPage.transactionReferenceNumberCopyMessage.isDisplayed(), "reference number copied message not displayed");
            String copiedValue = getCopiedValue().trim();
            //   softAssert.assertEquals(moneyTransferPage.transactionReferenceNumber.getText().trim(), copiedValue, "copied reference number not be the same");
            softAssert.assertTrue(!moneyTransferPage.paymentMode.getText().isEmpty(), "transaction popup payment mode not displayed");
            softAssert.assertTrue(!moneyTransferPage.transactionPopupRemarks.getText().isEmpty(), "transaction popup remarks not displayed");
            moneyTransferPage.transactionReferenceNumberCopyButton.click();
            waitTillVisibilityElement(driver, moneyTransferPage.transactionReferenceNumberCopyMessage);
            softAssert.assertTrue(moneyTransferPage.transactionReferenceNumberCopyMessage.isDisplayed(), "copied message not displayed");

            logger.info("Multiple transactions pop up, and the transaction status is :" + moneyTransferPage.transactionPopupSuccess.getText());
            logger.info("Multiple transactions pop up, and the transaction amount is : Rs." + moneyTransferPage.transactionPopupAmount.getText());
            logger.info("Multiple transactions pop up, and the transaction status is :" + dateTimeStr);

            /******** Download functions are added in separate file ***********/

//            if (moneyTransferPage.transactionPopupSuccess.getText().contains("Transferred")) {
//               moneyTransferPage.transactionDownloadButton.click();
//                staticWait(3000);
//                File transactionFile = new File("C:\\Users\\987993\\Downloads\\document.pdf");
//                softAssert.assertTrue(transactionFile.exists(), "multiple a/c transfer pop up transaction statement file download failed");
//                logger.info("Transaction statement file download location is :" + transactionFile);
//                if (transactionFile.exists()) {
//                    transactionFile.delete();
//                    waitTillInvisibilityOfLoader(driver);
//                }
//
//            }
            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
            moneyTransferPage.transactionPopupRepeat.click();
            waitTillInvisibilityOfLoader(driver);
            staticWait(2000);
            moneyTransferPage.backButton.click();
            try {
                if (moneyTransferPage.leaveButton.isDisplayed()) {
                    moneyTransferPage.leaveButton.click();
                    waitTillInvisibilityOfLoader(driver);
                }
            } catch (NoSuchElementException e) {
            }

        }
    }

    @And("User navigates to transaction page")
    public void userNavigatesToTransactionPage() {
        scrollIntoViewUp(driver, moneyTransferPage.backButton);
        moneyTransferPage.backButton.click();
        fluentWaitTillInVisibilityOfLoader(driver);
        try {

            if (moneyTransferPage.leaveButton.isDisplayed()) {
                moneyTransferPage.leaveButton.click();
                waitTillInvisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException e) {
        }

    }


    /**
     * Given code fow download function
     **/
    @And("User Successfully transferred to transfer to payee transaction details page")
    public void userSuccessfullyTransferredToTransferToPayeeTransactionDetailsPage() {

        userClicksOnTransferToPayeeTab();
        userSelectPayeeFromTheTransferToPayeePage();
        userSelectAllNeededDetails();
        userSelectPaymentMode();
        userStoredTheValuesInAVariable();
        userClicksMakePaymentButton();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
        userVerifyTransactionDetailsPage();
        verifyTheDownloadInTransactionDetailsPage();
        userClicksBackToMoneyTransferHomePage();
        userClicksViewButton();
        verifyTheDownloadInTransactionDetailsPage();
        moneyTransferPage.closeButton.click();


    }

    @And("User Successfully transferred to transfer to self transaction details page")
    public void userSuccessfullyTransferredToTransferToSelfTransactionDetailsPage() {
        userClicksTransferToSelfTab();
        userUserSelectAllNeededDetailsInTransferToSelfPage();
        userStoredTheValuesInAVariable();
        userClicksMakePaymentButton();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
        userVerifyTransactionDetailsPage();
        verifyTheDownloadInTransactionDetailsPage();
        userClicksBackToMoneyTransferHomePage();
        userClicksViewButton();
        verifyTheDownloadInTransactionDetailsPage();
        moneyTransferPage.closeButton.click();


    }

    @And("User Successfully transferred to multi transfer transaction details page")
    public void userSuccessfullyTransferredToMultiTransferTransactionDetailsPage() {
        userClicksMultiplePaymentTab();
        userSelectAllNeededDetailsAndAddPayees();
        userClicksMakePaymentButton();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();


    }

    @Then("User verify the download statement in multi transfer transaction details page")
    public void userVerifyTheDownloadStatementInMultiTransferTransactionDetailsPage() {
        softAssert = new SoftAssert();
        ListIterator<WebElement> viewButtonList = moneyTransferPage.viewButtonList.listIterator();
        while (viewButtonList.hasNext()) {
            WebElement clickViewButton = viewButtonList.next();
            clickViewButton.click();
            if (moneyTransferPage.transactionPopupSuccess.getText().contains("uccess")) {
                moneyTransferPage.transactionDownloadButton.click();
                staticWait(3000);
                File transactionFile = new File("C:\\Users\\987993\\Downloads\\document.pdf");
                softAssert.assertTrue(transactionFile.exists(), "transaction statement file download failed");
                if (transactionFile.exists()) {
                    transactionFile.delete();
                    waitTillInvisibilityOfLoader(driver);
                }
            } else {
                System.out.println("Transferred amount failed so can't download the statement");
            }
            waitTillVisibilityElement(driver, moneyTransferPage.closeButton);
            moneyTransferPage.closeButton.click();
            waitTillInvisibilityElement(driver, moneyTransferPage.closeButton);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the transferred amount limit messages")
    public void userVerifyTheTransferredAmountLimitMessages() {
        /**  Validate the messages **/
        /** Insufficient Balance message**/
        waitTillInvisibilityOfLoader(driver);
        if (moneyTransferPage.detailsContainer.size() != 1) {
            if (moneyTransferPage.pageHeader.getText().contains("Multiple Payments")) {
                moneyTransferPage.addAnotherPaymentButton.click();
                waitTillInvisibilityOfLoader(driver);
            }
            String currentBalanceSplit[] = moneyTransferPage.availableBalance.getText().split(":");
            double currentAvailableBalance = Double.parseDouble(currentBalanceSplit[1].replace("₹", "").replaceAll(",", "").trim());
            moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "1000000");
            staticWait(2000);
            if (currentAvailableBalance > 1000000) {
                logger.info("Current Available balance is " + currentAvailableBalance);
                staticWait(3000);
                softAssert.assertTrue(moneyTransferPage.rtgsMethod.isEnabled(), "while transferring the amount of 10L  message RTGS not  enabled");
                softAssert.assertTrue(moneyTransferPage.neftMethod.isEnabled(), "while transferring the amount of 10L  message NEFT not enabled");
                softAssert.assertFalse(moneyTransferPage.impsMethod.isEnabled(), "while transferring the amount of 10L  message IMPS   enabled");
                staticWait(2000);
                moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "1000005");
                softAssert.assertTrue(moneyTransferPage.transferredAmountLimitMessages.getText().contains("You cannot transfer more than ₹10,00,000.00"), "limit exceed message message not displayed");
                staticWait(2000);
                moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "1000000");

            } else {
                logger.info("Current Available balance is " + currentAvailableBalance + " so Insufficient message will show");
                softAssert.assertTrue(moneyTransferPage.transferredAmountLimitMessages.getText().contains("Insufficient balance"), "insufficient message not displayed");
            }
            if (moneyTransferPage.transferredAmountLimitMessages.getText().contains("cooling period cannot pay more than ₹50,000")) {
                logger.info("Payee is under cooling period ,Limit amount is 50K");

            } else if (moneyTransferPage.transferredAmountLimitMessages.getText().contains("Insufficient balance")) {
                softAssert.assertTrue(moneyTransferPage.rtgsMethod.isEnabled(), "while insufficient message RTGS not enabled");
                softAssert.assertTrue(moneyTransferPage.neftMethod.isEnabled(), "while insufficient message NEFT not enabled");
                softAssert.assertFalse(moneyTransferPage.impsMethod.isEnabled(), "while insufficient message IMPS enbaled ");

            }

/***Verify the message while entering 5L****/

            moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "500000");
            staticWait(2000);
            if (currentAvailableBalance >= 500000) {
                softAssert.assertTrue(moneyTransferPage.rtgsMethod.isEnabled(), "while transferring the amount of 5L  message RTGS not  enabled");
                softAssert.assertTrue(moneyTransferPage.neftMethod.isEnabled(), "while transferring the amount of 5L  message NEFT not enabled");
                softAssert.assertTrue(moneyTransferPage.impsMethod.isEnabled(), "while transferring the amount of 5L  message IMPS not  enabled");
                moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "500001");
                staticWait(2000);
                softAssert.assertTrue(moneyTransferPage.rtgsMethod.isEnabled(), "while transferring the amount of 5L  message RTGS not  enabled");
                softAssert.assertTrue(moneyTransferPage.neftMethod.isEnabled(), "while transferring the amount of 5L  message NEFT not enabled");
                softAssert.assertFalse(moneyTransferPage.impsMethod.isEnabled(), "while transferring the amount of more than 5L  message IMPS enabled");
            }
            if (moneyTransferPage.transferredAmountLimitMessages.getText().contains("cooling period cannot pay more than ₹50,000")) {
                logger.info("Payee is under cooling period ,Limit amount is 50K");
            } else if (moneyTransferPage.transferredAmountLimitMessages.getText().contains("Insufficient balance")) {
                softAssert.assertTrue(moneyTransferPage.rtgsMethod.isEnabled(), "while insufficient message RTGS not enabled");
                softAssert.assertTrue(moneyTransferPage.neftMethod.isEnabled(), "while insufficient message NEFT not enabled");
                softAssert.assertTrue(moneyTransferPage.impsMethod.isEnabled(), "while insufficient message IMPS enbaled ");
            }

            staticWait(2000);
            /***Verify the message while entering 1.9L****/
            moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "199999");
            staticWait(2000);
            if (moneyTransferPage.transferredAmountLimitMessages.getText().contains("cooling period cannot pay more than ₹50,000")) {
                logger.info("Payee is under cooling period ,Limit amount is 50K");
            } else if (moneyTransferPage.transferredAmountLimitMessages.getText().contains("Insufficient balance")) {
                softAssert.assertFalse(moneyTransferPage.rtgsMethod.isEnabled(), "while insufficient message RTGS enabled");
                softAssert.assertTrue(moneyTransferPage.neftMethod.isEnabled(), "while insufficient message NEFT not enabled");
                softAssert.assertTrue(moneyTransferPage.impsMethod.isEnabled(), "while insufficient message IMPS not enbaled ");

            } else {
                softAssert.assertFalse(moneyTransferPage.rtgsMethod.isEnabled(), "while transferring the amount of 1.9L  message RTGS enabled");
                softAssert.assertTrue(moneyTransferPage.neftMethod.isEnabled(), "while transferring the amount of 1.9L  message NEFT not enabled");
                softAssert.assertTrue(moneyTransferPage.impsMethod.isEnabled(), "while transferring the amount of 1.9L  message IMPS not  enabled");
            }
            if (moneyTransferPage.pageHeader.getText().contains("Add Payment")) {
                waitTillInvisibilityOfLoader(driver);
                moneyTransferPage.backButton.click();
                waitTillInvisibilityOfLoader(driver);
            }
        } else {
            logger.info("Amount transfer to Au bank Payee So payment mode options not showing");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the transferred amount limit messages for quick transfer")
    public void userVerifyTheTransferredAmountLimitMessagesForQuickTransfer() {
        scrollIntoView(driver, moneyTransferPage.transferAmount);
        moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "50001");
        String availableBalance = moneyTransferPage.availableBalance.getText().replace("\\n", "").replaceAll("[^\\d.]", "");
        balanceAvailable = Double.parseDouble(availableBalance);
        staticWait(2000);
        if (balanceAvailable >= 50000) {
            logger.info("Current Available balance is " + balanceAvailable);
            softAssert.assertTrue(moneyTransferPage.neftMethod.isEnabled(), "while transferring the amount of 50K  message NEFT not enabled");
            softAssert.assertTrue(moneyTransferPage.impsMethod.isEnabled(), "while transferring the amount of 50K  message IMPS not  enabled");
            staticWait(2000);
            moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "50001");
            softAssert.assertTrue(moneyTransferPage.transferredAmountLimitMessages.getText().contains("Amount should be less than ₹50,000"), "limit exceed message message not displayed");
            logger.info("If Transferring amount more than the Limit ,this message will appear ," + moneyTransferPage.transferredAmountLimitMessages.getText());
        } else {
            logger.info("Current Available balance is " + balanceAvailable + " so Insufficient message will show");
            softAssert.assertTrue(moneyTransferPage.transferredAmountLimitMessages.getText().contains("Insufficient balance"), "insufficient message not displayed");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify available payee list in recent payee section")
    public void userVerifyAvailablePayeeListInRecentPayeeSection() {
        softAssert = new SoftAssert();
        if (moneyTransferPage.recentPayeeList.size() == 0) {
            softAssert.assertTrue(moneyTransferPage.addNewPayeeButton.isDisplayed(), "add new payee button not displayed");
            logger.info("In this use doesn't have any payee");

        } else {
            softAssert.assertTrue(moneyTransferPage.payButton.isDisplayed(), "pay button not displayed");
            softAssert.assertTrue(moneyTransferPage.allPayeeButton.isDisplayed(), "all payee button not displayed");
            logger.info("In user have payees");
        }

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            logger.error("Assertion error " + e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the transfer to self tab status")
    public void userVerifyTheTransferToSelfTabStatus() {

        if (getNoOfCasa() > 1) {
            softAssert.assertTrue(moneyTransferPage.transferToSelfTab.isEnabled(), "transfer to self tab should be enabled");
            logger.info("This user totally contain " + getNoOfCasa() + " ,transfer to self option is enabled");
        } else {
            softAssert.assertTrue(!moneyTransferPage.transferToSelfTab.isEnabled(), "transfer to self tab should not enabled");
            logger.info(fileReader.moneyTransferTestData.get("cifId") + "contains only one  Operative account");
        }
    }

    @Then("User verify download file in multiple pay transaction details page")
    public void userVerifyDownloadFileInMultiplePayTransactionDetailsPage() {

        softAssert = new SoftAssert();
        softAssert.assertTrue(moneyTransferPage.transactionStatus.getText().contains("Multiple Payments"), "multiple payment transaction failed");
        softAssert.assertEquals(moneyTransferPage.viewButtonList.size(), noOfPaymentInList, "payments list not same");
        ListIterator<WebElement> viewButtonList = moneyTransferPage.viewButtonList.listIterator();

        while (viewButtonList.hasNext()) {
            WebElement clickViewButton = viewButtonList.next();
            waitTillInvisibilityOfLoader(driver);
            clickViewButton.click();
            waitTillInvisibilityOfLoader(driver);
            if (moneyTransferPage.transactionPopupSuccess.getText().contains("uccess")) {
                moneyTransferPage.transactionDownloadButton.click();
                staticWait(3000);
                File transactionFile = new File("C:\\Users\\987993\\Downloads\\document.pdf");
                softAssert.assertTrue(transactionFile.exists(), "transaction statement file download failed");
                if (transactionFile.exists()) {
                    transactionFile.delete();
                    waitTillInvisibilityOfLoader(driver);
                }
            }

            moneyTransferPage.closeButton.click();
            waitForPageLoad(driver);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify  download function in transaction popup for multiple payment")
    public void userVerifyDownloadFunctionInTransactionPopupForMultiplePayment() {
        softAssert = new SoftAssert();
        for (int i = 1; i <= 2; i++) {
            scrollIntoView(driver, moneyTransferPage.recentTransactionTab);
            waitTillElementToBeClickable(driver, moneyTransferPage.viewButton);
            moneyTransferPage.viewMultplePayee(i);
            logger.info("Multiple transactions pop up, and the transaction status is :" + moneyTransferPage.transactionPopupSuccess.getText());
            logger.info("Multiple transactions pop up, and the transaction amount is : Rs." + moneyTransferPage.transactionPopupAmount.getText());
            if (moneyTransferPage.transactionPopupSuccess.getText().contains("Transferred")) {
                moneyTransferPage.transactionDownloadButton.click();
                staticWait(3000);
                File transactionFile = new File("C:\\Users\\987993\\Downloads\\document.pdf");
                softAssert.assertTrue(transactionFile.exists(), "multiple a/c transfer pop up transaction statement file download failed");
                logger.info("Transaction statement file download location is :" + transactionFile);
                if (transactionFile.exists()) {
                    transactionFile.delete();
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
            moneyTransferPage.transactionPopupRepeat.click();
            waitTillInvisibilityOfLoader(driver);
            javaScriptExecutorClickElement(driver, moneyTransferPage.backButton);
            //  moneyTransferPage.backButton.click();
            try {
                if (moneyTransferPage.leaveButton.isDisplayed()) {
                    moneyTransferPage.leaveButton.click();
                    waitTillInvisibilityOfLoader(driver);
                }
            } catch (NoSuchElementException e) {
            }

        }
    }

    @And("User clicks on add new payee option on multiple transfer")
    public void userClicksOnAddNewPayeeOptionOnMultipleTransfer() {
        moneyTransferPage.addAnotherPaymentButton.click();
        staticWait(4000);
        moneyTransferPage.clickSelectPayee.click();
        waitTillElementToBeClickable(driver, moneyTransferPage.multiplePaymentDropDownLists);
        moneyTransferPage.selectPayeeFromDD("Add New Payee").click();
        waitTillInvisibilityOfLoader(driver);

    }

    @And("User navigates to the multiple transfer page")
    public void userNavigatesToTheMultipleTransferPage() {
        clickOnButton(homePage.moneyTransferButton);
        waitTillInvisibilityOfLoader(driver);
        moneyTransferPage.multiplePaymentsTab.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User select payee for {string}")
    public void userSelectPayeeFor(String modeOfPayment) {
        fluentWaitTillTheElementToBeClickable(driver, 20, 1, moneyTransferPage.clickSelectPayee);
        clickOnButton(moneyTransferPage.clickSelectPayee);
        if (modeOfPayment.contains("IFT")) {

            moneyTransferPage.selectPayeeFromDD(fileReader.moneyTransferTestData.get("selectPayeeName")).click();
        } else {
//          moneyTransferPage.selectPayeeIndexSecondPayee.click();
            javaScriptExecutorClickElement(driver, moneyTransferPage.selectPayeeFromDD(fileReader.moneyTransferTestData.get("otherBankPayeeName")));

        }
    }

    @And("User select payment mode for {string}")
    public void userSelectPaymentModeFor(String modeOfTransfer) {
        if (moneyTransferPage.detailsContainer.size() != 1) {
            if (modeOfTransfer.contains("IMPS")) {
                javaScriptExecutorClickElement(driver, moneyTransferPage.impsMethod);
                moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.moneyTransferTestData.get("transferAmount"));
                staticWait(2000);
            } else if (modeOfTransfer.contains("NEFT")) {
                javaScriptExecutorClickElement(driver, moneyTransferPage.neftMethod);
                moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.moneyTransferTestData.get("transferAmount"));
            } else if (modeOfTransfer.contains("RTGS")) {
                javaScriptExecutorClickElement(driver, moneyTransferPage.rtgsMethod);
                logger.info("User selected RTGS NUMBER");
                moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "200001");
                staticWait(2000);
            }
        } else {
            logger.debug("Internal Fund Transfer so we no need to choose");
            moneyTransferPage.transferAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.moneyTransferTestData.get("transferAmount"));
            staticWait(2000);
        }
    }

    @And("User verify the find ifsc page")
    public void userVerifyTheFindIfscPage() {
        softAssert = new SoftAssert();
        boolean isFindIFSCDisplayed = false;
        try {
            if (moneyTransferPage.findIFSCButton.isDisplayed()) {
                isFindIFSCDisplayed = true;
                moneyTransferPage.findIFSCButton.click();
                waitTillVisibilityElement(driver, moneyTransferPage.findIFSCPopUpClosedButton);
                softAssert.assertTrue(moneyTransferPage.findIFSCPopUpClosedButton.isDisplayed(), "Find ifsc pop up close button not displayed");
                softAssert.assertTrue(moneyTransferPage.selectCityForIFSC.isEnabled(), "select city option not enabled");
                softAssert.assertTrue(moneyTransferPage.selectBranchForIFSC.isEnabled(), "select branch option not enabled");

            }
        } catch (NoSuchElementException e) {
            logger.debug("Find IFSC button not displayed");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        if (isFindIFSCDisplayed == true) {
            moneyTransferPage.findIFSCPopUpClosedButton.click();
            waitTillInvisibilityElement(driver, moneyTransferPage.findIFSCPopUpClosedButton);
        }
    }

    @And("User verify the navigation on recent payee list")
    public void userVerifyTheNavigationOnRecentPayeeList() {
        softAssert = new SoftAssert();
        if (moneyTransferPage.recentPayeeList.size() == 0) {
            softAssert.assertTrue(moneyTransferPage.addNewPayeeButton.isDisplayed(), "add new payee button not displayed");
            logger.info("In this use doesn't have any payee");

        } else {
            moneyTransferPage.payButton.click();
            waitTillInvisibilityOfLoader(driver);
            softAssert.assertTrue(driver.getCurrentUrl().contains("transfer-to-payee"), "transfer to payee page url not be the same");
            softAssert.assertTrue(moneyTransferPage.transferToPayeePageHeader.isDisplayed(), "transfer to payee page header not displayed");
            moneyTransferPage.backButton.click();
            waitTillElementToBeClickable(driver, moneyTransferPage.allPayeeButton);
            moneyTransferPage.allPayeeButton.click();
            waitTillInvisibilityOfLoader(driver);
            managePayeeStepDef.userCanVerifyThePayeesPage();
            logger.info("In user have payees");
            homePage.moneyTransferButton.click();
            waitTillInvisibilityOfLoader(driver);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User validates the transaction limit reached pop up")
    public void userValidatesTheTransactionLimitReachedPopUp() {
        try {
            if (moneyTransferPage.dailyQuickTransactionLimitExceedPopUp.getText().equalsIgnoreCase("Daily Transaction Limit Reached")) {
                attachScreenshot(driver, scenario);
                logger.info("user reached per day transaction limit");
                throw new PendingException("Daily Transaction Limit Reached Pop up Remaining steps are skipped");
            }
        } catch (NoSuchElementException e) {
            logger.debug("Daily Transaction Limit Reached Pop up not displayed");
        }
    }

    @And("User navigates to clicks on make payment button")
    public void userNavigatesToClicksOnMakePaymentButton() {
        userClicksOnQuickAccountTransferTab();
        userSelectFromTheList("au bank payee");
        userSelectAllNeededDetailsInQuickAccountTransferPage();
        userStoredTheValuesInAVariable();
        userClicksMakePaymentButton();
    }

    @And("User validates the download functions for quick transfer scenario")
    public void userValidatesTheDownloadFunctionsForQuickTransferScenario() {
        userVerifyTransactionDetailsPage();
        verifyTheDownloadInTransactionDetailsPage();
        userClicksBackToMoneyTransferHomePage();
        userClicksViewButton();
        verifyTheDownloadInTransactionDetailsPage();
        moneyTransferPage.closeButton.click();
    }

    @And("User validates the {string} popup")
    public void userValidatesThePopup(String message) {
        try {
            switch (message) {
                case "transaction limit reached":
                    if (moneyTransferPage.dailyQuickTransactionLimitExceedPopUp.getText().equalsIgnoreCase("Daily Transaction Limit Reached")) {
                        attachScreenshot(driver, scenario);
                        logger.info("user reached per day transaction limit");
                        staticWait(5000);
                        throw new PendingException("Daily Transaction Limit Reached Pop up Remaining steps are skipped");
                    }
                    break;
                case "Daily Transaction Limit Reached":
                    softAssert = new SoftAssert();
                    softAssert.assertTrue(moneyTransferPage.dailyQuickTransactionLimitExceedPopUp.getText().contains("Daily Transaction Limit"), "limit reached pop up is not identical");

            }
        } catch (NoSuchElementException e) {
            logger.debug("Daily Transaction Limit Reached Pop up not displayed");
        }
    }


    @And("If the transaction limit is reached, the user goes to the money transfer home")
    public void ifTheTransactionLimitIsReachedTheUserGoesToTheMoneyTransferHome() {
        boolean isDailyLimitReached = false;
        try {
            staticWait(2000);
            if (moneyTransferPage.dailyQuickTransactionLimitExceedPopUp.isDisplayed()) {
                attachScreenshot(driver, scenario);
                isDailyLimitReached = true;
                logger.info("user reached per day transaction limit");
                clickOnButton(moneyTransferPage.transactionLimitReachedPopUpButton);
                waitTillInvisibilityOfLoader(driver);
            }

        } catch (NoSuchElementException ignored) {
        }
        if (!isDailyLimitReached) {
            userVerifyTransactionDetailsPage();
            userClicksBackToMoneyTransferPageButton();

//            homePageStepDef.userClicksOnBackButton();
        }
    }

}













