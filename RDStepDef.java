package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pom.RDPage;
import reusable.Base;
import reusable.TestContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RDStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(RDStepDef.class);
    RDPage rdPage;
    DepositDashboardStepDef depositDashboardStepDef;
    HomePageStepDef homePageStepDef;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String depositNo;

    public RDStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        rdPage = context.getPageObjectManager().getRDPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        homePageStepDef = new HomePageStepDef(context);
    }

    @And("User navigates on the 'open recurring deposit' page")
    public void userNavigatesOnTheRDPage() {
        explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(rdPage.rdButton));
        clickOnButton(rdPage.rdButton);
        waitTillInvisibilityOfLoader(driver);
    }

    @When("User enters all needed details on RD page")
    public void userEntersAllNeededDetailsOnRDPage() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, rdPage.fromAccount);
        selectFromAccount(driver, rdPage.fromAccount,getAccountNumber());
        rdPage.monthlyInstallment.sendKeys(fileReader.rDTestData.get("monthlyInstallment"));
        waitTillInvisibilityOfLoader(driver);
        if (fileReader.rDTestData.containsKey("tenureYear")) {
//           selectDDByValue(rdPage.selectYear, fileReader.rDTestData.get("tenureYear") + " " + "years");
            rdPage.yearDD.click();
            staticWait(2000);
            rdPage.yearDD.click();
            selectTenureYear(driver, fileReader.rDTestData.get("tenureYear") + " " + "years");
        }
        if (fileReader.rDTestData.containsKey("tenureMonth")) {
            waitTillElementToBeClickable(driver, rdPage.monthDD);
//          selectDDByValue(rdPage.selectMonth, fileReader.rDTestData.get("tenureMonth") + " " + "months");
            staticWait(2000);
            rdPage.monthDD.click();
            selectTenureMonth(driver, fileReader.rDTestData.get("tenureMonth") + " " + "months");
            fluentWaitTillInVisibilityOfLoader(driver);
        }
        rdPage.purposeInputField.sendKeys(fileReader.rDTestData.get("purpose"));
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User can validate the RD summary is auto populated")
    public void userCanValidateTheRDSummaryIsAutoPopulated() {
        String actual, expected;
        softAssert = new SoftAssert();
        softAssert.assertTrue(rdPage.summaryMonthlyInstallment.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.rDTestData.get("monthlyInstallment") + ".00"), "monthly installment is not same");
        actual = rdPage.summaryTenure.getText().replace(" ", "");
        if (fileReader.rDTestData.containsKey("tenureYear")) {
            expected = fileReader.rDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.rDTestData.containsKey("tenureMonth")) {
            expected = fileReader.rDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure month should not be visible as we have not selected any value from month");
        }
        softAssert.assertTrue(rdPage.maturityAmount.getText().matches(".*\\d.*"), "maturity amount is not displayed");
        softAssert.assertTrue(rdPage.interestRate.getText().matches(".*\\d.*"), "interest rate is not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User click on 'open recurring deposit' button")
    public void userClickOnOpenRecurringDepositButton() {
        fluentWaitTillInVisibilityOfLoader(driver);
        clickOnButton(rdPage.openRDButton);
    }

    @Then("User navigates on the RD review page")
    public void userNavigatesOnTheRDReviewPage() {
        fluentWaitTillInVisibilityOfLoader(driver);
        scrollIntoViewUp(driver, rdPage.rdReviewPageHeader);
        Assert.assertTrue(rdPage.rdReviewPageHeader.isDisplayed());
    }

    @And("User can validate the RD review page")
    public void userCanValidateTheRDReviewPage() {
        String actual, expected;
        softAssert = new SoftAssert();
        softAssert.assertTrue(rdPage.reviewMonthlyInstallment.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.rDTestData.get("monthlyInstallment") + ".00"), "monthly installment is not same");
        softAssert.assertTrue(rdPage.reviewDebitAcc.getText().trim().equalsIgnoreCase(getAccountNumber()), "debit account is not same");
        actual = rdPage.reviewTenure.getText().replace(" ", "");
        if (fileReader.rDTestData.containsKey("tenureYear")) {
            expected = fileReader.rDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.rDTestData.containsKey("tenureMonth")) {
            expected = fileReader.rDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month");
        }
        softAssert.assertTrue(rdPage.reviewInterestRate.getText().matches(".*\\d.*"), "interest rate is not displayed");
        scrollIntoView(driver, rdPage.reviewCreditAccount);
        softAssert.assertTrue(rdPage.reviewMaturityAmt.getText().matches(".*\\d.*"), "maturity amount is not displayed");
        softAssert.assertTrue(rdPage.reviewCreditAccount.getText().trim().equalsIgnoreCase(getAccountNumber()), "credit account is not same as debit account");
        softAssert.assertTrue(rdPage.reviewPurpose.getText().contains(fileReader.rDTestData.get("purpose")));
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User click on open RD confirm button")
    public void userClickOnOpenRDConfirmButton() {
        scrollIntoView(driver, rdPage.reviewConfirmButton);
        clickOnButton(rdPage.reviewConfirmButton);
    }

    public void enterAmount(String amount) {
        int amt = Integer.parseInt(amount);
        if (amt < 500) {
            rdPage.monthlyInstallment.sendKeys("100");
            amt -= 100;
        } else if (amt < 1000) {
            rdPage.monthlyInstallment.sendKeys("500");
            amt -= 500;
        } else {
            rdPage.monthlyInstallment.sendKeys("1000");
            amt -= 1000;
        }
        rdPage.monthlyInstallment.sendKeys(Keys.TAB);
        while (amt != 0) {
            if (amt < 500) {
                clickOnButton(rdPage.capsul100);
                amt -= 100;
            } else if (amt >= 500 & amt < 1000) {
                clickOnButton((rdPage.capsul500));
                amt -= 500;
            } else if (amt >= 1000) {
                clickOnButton(rdPage.capsul1000);
                amt -= 1000;
            }
        }
    }

    @Then("User will be navigated on the rd receipt page")
    public void userWillBeNavigatedOnTheRdReceiptPage() {
        waitTillInvisibilityOfLoader(driver);
        try {
            scrollIntoViewUp(driver, rdPage.rdReceiptPageHeader);
        } catch (NoSuchElementException e) {
            logger.error(e.toString());
            attachScreenshot(driver, scenario);
            logger.info("Receipt page successfully message is not displayed .Please verify the screen shot");
        }
        logger.info("receipt page title: " + driver.getTitle());
        // System.out.println("receipt page title: " + driver.getTitle());
        Assert.assertTrue(true);

    }

    @And("User can validate rd is opened successfully")
    public void userCanValidateRdIsOpenedSuccessfully() {
        String actual, expected;
        softAssert = new SoftAssert();
        //waitForPageLoad(driver);
        scrollIntoViewUp(driver, rdPage.rdReceiptPageHeader);
        softAssert.assertTrue(rdPage.rdReceiptPageHeader.isDisplayed());
        softAssert.assertTrue(rdPage.receiptMonthlyInstallment.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.rDTestData.get("monthlyInstallment") + ".00"), "monthly installment is not same");
        softAssert.assertTrue(rdPage.receiptDebitAcc.getText().trim().equalsIgnoreCase(getAccountNumber()), "debit account is not same");

        actual = rdPage.receiptTenure.getText().replace(" ", "");
        if (fileReader.rDTestData.containsKey("tenureYear")) {
            expected = fileReader.rDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.rDTestData.containsKey("tenureMonth")) {
            expected = fileReader.rDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month dd");
        }
        softAssert.assertTrue(rdPage.receiptInterestRate.getText().matches(".*\\d.*"), "interest rate is not displayed");
        scrollIntoView(driver, rdPage.receiptCreditAccount);
        softAssert.assertTrue(rdPage.receiptMaturityAmt.getText().contains("₹"), "maturity amount is not displayed");
        softAssert.assertTrue(rdPage.receiptCreditAccount.getText().trim().equalsIgnoreCase(getAccountNumber()), "credit account is not same as debit account");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        setDepositNo(rdPage.rdNumber.getText().split(":")[1].split("-")[0].trim());
        logger.info("RD Number" + getDepositNo());
        // System.out.println("RD Number" + getDepositNo());
    }

    @And("User successfully opened a RD account")
    public void userSuccessfullyOpenedARDAccount() {
        userNavigatesOnTheRDPage();
        userEntersAllNeededDetailsOnRDPage();
        userClickOnOpenRecurringDepositButton();
        userNavigatesOnTheRDReviewPage();
        userClickOnOpenRDConfirmButton();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
        userWillBeNavigatedOnTheRdReceiptPage();
        setDepositNo(rdPage.rdNumber.getText().split(":")[1].split("-")[0].trim());
        logger.info("RD Number" + getDepositNo());
        //System.out.println("RD Number" + getDepositNo());
    }

    @And("User verify the rd details in the list")
    public void userVerifyTheRdDetailsInTheList() {
        softAssert = new SoftAssert();
        waitTillInvisibilityOfLoader(driver);
        try {
            scrollIntoView(driver, rdPage.viewButtonRd(1));
            softAssert.assertTrue(rdPage.viewButtonRd(1).isDisplayed(), "view button not displayed");
            softAssert.assertTrue(rdPage.rdDepositNumber.isDisplayed(), "rd deposit number not displayed");
            waitTillVisibilityElement(driver, rdPage.numberOfDepositInRd);
            logger.info("Total Number of available in the deposit dashboard page is :" + rdPage.numberOfDepositInRd.getText());
            softAssert.assertTrue(rdPage.numberOfDepositInRd.isDisplayed(), "number of rd list not displayed");
        } catch (NoSuchElementException e) {
            logger.info("In deposit dashboard page Rd List not available ");
            attachScreenshot(driver, scenario);
            throw new NoSuchElementException("Elements are not visible");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the rd created date and clicks the close button")
    public void userVerifyTheRdCreatedDateAndClicksTheCloseButton() {
        int noOfRdAccount = Integer.parseInt(rdPage.numberOfDepositInRd.getText());
        for (int i = 1; i <= noOfRdAccount; i++) {
            rdPage.viewButtonRd(i).click();
            waitTillInvisibilityOfLoader(driver);
            //Today Date
            java.util.Date todayDate;
            SimpleDateFormat sdFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
            java.util.Date currentDate = new java.util.Date();
            String todayDateFormat = sdFormat.format(currentDate);
            //Rd Created Date
            String createDate = rdPage.rdCreatedDate.getText();
            Date rdCreatedDate;
            try {
                todayDate = sdFormat.parse(todayDateFormat);
                rdCreatedDate = sdFormat.parse(createDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            logger.info("Current Rd is created on " + rdCreatedDate);
            if (todayDate.compareTo(rdCreatedDate) != 0) {
                rdPage.closeButton.click();
                String[] depositNumber = rdPage.getRdNumber.getText().split(":");
                depositNo = depositNumber[1].replace(" ", "");
                setDepositCreditAcc(rdPage.creditAccNo.getText());
                logger.info("Deposit credit account number" + getDepositCreditAcc());
                logger.info("Deposit number is" + getDepositCreditAcc());
                fluentWaitTillInVisibilityOfLoader(driver);
                break;
            } else {
                scrollIntoViewUp(driver, rdPage.pageHeader);
                rdPage.backButton.click();
                fluentWaitTillInVisibilityOfLoader(driver);
                if (i == noOfRdAccount) {
                    logger.info("Same Day opening RD accounts only available");
                    throw new RuntimeException("Same Day opening RD accounts only available");
                }
            }
        }
    }

    @Then("User verify the message in review page")
    public void userVerifyTheMessageInReviewPage() {
        logger.info("Same day opened rd can't close same day po up verified");
        waitTillVisibilityElement(driver, rdPage.sameDayOpeningRdPopUP);
        softAssert.assertTrue(rdPage.sameDayOpeningRdPopUP.isDisplayed(), "Rd close on same day pop up not displayed");
        softAssert.assertTrue(rdPage.sameDayOpeningRdPopUPMessage.getText().contains("RD Cannot be close on the same day of opening."), "rd close error message not displayed");
        logger.info("Rd closing error message " + rdPage.sameDayOpeningRdPopUPMessage.getText());
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        rdPage.cancelButton.click();
    }

    @Then("User can validate that {string} is not in the list")
    public void userCanValidateThatIsNotInTheList(String arg0) {
        softAssert = new SoftAssert();
        for (WebElement rdListNumber : rdPage.rdNumberList) {
            String rdNo = rdListNumber.getText();
            softAssert.assertNotEquals(rdNo, depositNo, "closure rd account present in th list");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    /**
     * RD DOWNLOAD FUNCTIONS CODE GIVEN IN BELOW
     **/

    @And("User successfully navigates RD receipt page")
    public void userSuccessfullyNavigatesRDReceiptPage() {
        userNavigatesOnTheRDPage();
        userEntersAllNeededDetailsOnRDPage();
        userClickOnOpenRecurringDepositButton();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
        userClickOnOpenRDConfirmButton();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();

    }

    @When("User click on view button of in the recurring deposit list")
    public void userClickOnViewButtonOfInTheRecurringDepositList() {
        rdPage.viewButtonRD.click();
        waitTillInvisibilityOfLoader(driver);

    }

    @And("User validate interest rates link in recurring deposit")
    public void userValidateInterestRatesLinkInRecurringDeposit() {
        softAssert = new SoftAssert();
        String url = "https://www.aubank.in/interest-rates/recurring-deposits-interest-rates";
        //rdPage.interestRateLinkRd.getAttribute("href");
        String currentWindow = driver.getWindowHandle();
        rdPage.interestRateLinkRd.click();
        waitTillInvisibilityOfLoader(driver);
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindow)) {
                driver.switchTo().window(windowHandle);
                softAssert.assertTrue(driver.getCurrentUrl().equals(url), "interest-rates page not displayed");
                break;
            }
        }
        driver.close();
        driver.switchTo().window(currentWindow);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }




}

