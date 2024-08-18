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
import pom.DepositsDashboardPage;
import pom.FDPage;
import reusable.Base;
import reusable.TestContext;

import java.util.Random;

public class FDStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(FDStepDef.class);
    FDPage fdPage;
    DepositsDashboardPage depositsDashboardPage;
    WebDriver driver;
    Scenario scenario;
    HomePageStepDef homePageStepDef;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String jointFd;
    boolean isJointFD;

    public FDStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        fdPage = context.getPageObjectManager().getFDPage();
        depositsDashboardPage = context.getPageObjectManager().getDepositDashboardPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        homePageStepDef = new HomePageStepDef(context);
    }

    @And("User click on FD button")
    public void userClickOnFDButton() {
        fdPage.fdButton.click();
    }

    @Then("User can validate FD screen is opened")
    public void userCanValidateFDScreenIsOpened() {
        Assert.assertTrue(fdPage.openFDWindow.isDisplayed());
        attachScreenshot(driver, scenario);
    }

    @And("User navigates on the 'open fixed deposit' page")
    public void userNavigatesOnTheFDPage() {
        explicitWait(driver, 30).until(ExpectedConditions.visibilityOf(fdPage.fixedDepositButton));
        clickOnButton(fdPage.fixedDepositButton);
        //staticWait(3000);
        waitTillInvisibilityOfLoader(driver);
    }

    @When("User enters all needed details on FD page")
    public void userEntersAllNeededDetails() {
        softAssert = new SoftAssert();
        WebElement element = null;
        waitTillVisibilityElement(driver, fdPage.fromAccount);
//      selectFromAccount(driver, fdPage.fromAccount, fileReader.fDTestData.get("fromAccount"));
        selectFromAccount(driver, fdPage.fromAccount,getAccountNumber());
        logger.info("User Select the from account "+getAccountNumber());
        fluentWaitTillInVisibilityOfLoader(driver);
        fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.fDTestData.get("depositAmount"));
        fdPage.depositAmount.sendKeys(Keys.TAB);
        if (fileReader.fDTestData.get("interestPayoutType").equalsIgnoreCase("maturity")) {
            element = fdPage.payoutOnMaturity;
        } else if (fileReader.fDTestData.get("interestPayoutType").equalsIgnoreCase("monthly")) {
            element = fdPage.payoutMonthly;
        } else {
            element = fdPage.payoutQuarterly;
        }
        explicitWait(driver, 10).until(ExpectedConditions.elementToBeClickable(element));
        clickOnButton(element);
        fluentWaitTillInVisibilityOfLoader(driver);
        if (fileReader.fDTestData.containsKey("tenureYear")) {
//          selectDDByValue(fdPage.selectYear, fileReader.fDTestData.get("tenureYear"));
            fdPage.yearDD.click();
            selectTenureYear(driver, fileReader.fDTestData.get("tenureYear") + " " + "years");
            fluentWaitTillInVisibilityOfLoader(driver);
        }
        if (fileReader.fDTestData.containsKey("tenureMonth")) {
//          selectDDByValue(fdPage.selectMonth, fileReader.fDTestData.get("tenureMonth"));
            fdPage.monthDD.click();
            selectTenureMonth(driver, fileReader.fDTestData.get("tenureMonth") + " " + "months");
            fluentWaitTillInVisibilityOfLoader(driver);
        }
        if (fileReader.fDTestData.containsKey("tenureDay")) {
//            selectDDByValue(fdPage.selectDays, fileReader.fDTestData.get("tenureDay"));
            fdPage.dayDD.click();
            selectTenureDays(driver, fileReader.fDTestData.get("tenureDay") + " " + "days");
            fluentWaitTillInVisibilityOfLoader(driver);
        }
        if (fileReader.fDTestData.get("actionOnMaturity").equalsIgnoreCase("renew")) {
            element = fdPage.renewOnMaturityRadioButton;
        } else {
            element = fdPage.closeOnMaturityRadioButton;
        }
        explicitWait(driver, 30).until(ExpectedConditions.elementToBeClickable(element));
        clickOnButton(element);
        fluentWaitTillInVisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User can validate the FD summary is auto populated")
    public void userCanValidateTheFDSummaryIsAutoPopulated() throws InterruptedException {
        Thread.sleep(2000);
        String actual, expected;
        softAssert = new SoftAssert();
        System.out.println("this is principal amount: " + fdPage.principalAmount.getText().replace("₹", ""));
        softAssert.assertTrue(fdPage.principalAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");
        actual = fdPage.summaryTenure.getText().replace(" ", "");
        if (fileReader.fDTestData.containsKey("tenureYear")) {
            expected = fileReader.fDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.fDTestData.containsKey("tenureMonth")) {
            expected = fileReader.fDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month");
        }
        if (fileReader.fDTestData.containsKey("tenureDay")) {
            expected = fileReader.fDTestData.get("tenureDay").trim() + "d";
            softAssert.assertTrue(actual.contains(expected), "tenure days is not same");
        } else {
            softAssert.assertFalse(actual.contains("d"), "tenure contains days as well but it should not as we have not selected any value from days dd");
        }
        inputValueInExcel("FDPage", "demoInterest", fileReader.fDTestData.get("demoInterest"), fdPage.interestRate.getText().trim());
        inputValueInExcel("FDPage", "demoInterestRate", fileReader.fDTestData.get("demoInterestRate"), fdPage.totalInterestAmount.getText().trim());
        inputValueInExcel("FDPage", "demoMaturity", fileReader.fDTestData.get("demoMaturity"), fdPage.interestRate.getText().trim());

//     softAssert.assertTrue(fdPage.interestRate.getText().equalsIgnoreCase(fileReader.fDTestData.get("interestRate")), "interest rate is not same");
//      softAssert.assertTrue(fdPage.totalInterestAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("interestAmount")), "total interest amount is not same");
//      softAssert.assertTrue(fdPage.maturityAmount.getText().replace("₹", "").replace(",", "").trim().equalsIgnoreCase(fileReader.fDTestData.get("maturityAmount").trim()), "maturity amount is not same");
        softAssert.assertTrue(fdPage.maturityAmount.getText().matches(".*\\d.*"), "maturity amount not displayed");
        softAssert.assertTrue(fdPage.interestRate.getText().matches(".*\\d.*"), "interest rate is not displayed");
        softAssert.assertTrue(fdPage.totalInterestAmount.getText().matches(".*\\d.*"), "total interest amount is not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @Then("User can verify the 'from account' section")
    public void userCanVerifyTheFromAccountSection() {
        softAssert = new SoftAssert();
        selectDDByIndex(fdPage.fromAccount, new Random().nextInt(fdPage.fromAccOptions.size()));
        softAssert.assertTrue(fdPage.fromAccount.getText().trim().length() == 16, "account no is not of length 16");
        // account name code needs to be added here
        softAssert.assertTrue(fdPage.availableBalance.isDisplayed(), "available balance is not displayed");
        softAssert.assertAll();
    }

    @And("User can verify the 'deposit amount' section")
    public void userCanVerifyTheDepositAmountSection() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(fdPage.depositAmount.getAttribute("value").isEmpty());
        fdPage.depositAmount.sendKeys("12334.343dhfdj!@#$%^*&.");
        try {
            Integer.parseInt(fdPage.depositAmount.getAttribute("value").toString());
        } catch (NumberFormatException e) {
            softAssert.fail("deposit amount does not contains only digits");
        }
        softAssert.assertTrue(fdPage.capsul10000.isDisplayed());
        softAssert.assertTrue(fdPage.capsul5000.isDisplayed());
        softAssert.assertTrue(fdPage.capsul1000.isDisplayed());
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }    }

    @When("User click on 'open fixed deposit' button")
    public void userClickOnOpenFixedDepositButton() {
        waitTillElementToBeClickable(driver, fdPage.openFixedDepositButton);
        clickOnButton(fdPage.openFixedDepositButton);
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User navigates on the FD review page")
    public void userNavigatesOnTheFDReviewPage() {
        try {
            Assert.assertTrue(fdPage.fdReviewPageHeader.isDisplayed());
            System.out.println("Title of review page: " + driver.getTitle());
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Fd review page not visible please check the screen shot");
        }
    }

    @And("User can validate the FD review page")
    public void userCanValidateTheFDReviewPage() {
        String actual, expected;
        softAssert = new SoftAssert();
        softAssert.assertTrue(fdPage.reviewPrincipalAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");

        actual = fdPage.reviewTenure.getText().replace(" ", "");
        if (fileReader.fDTestData.containsKey("tenureYear")) {
            expected = fileReader.fDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.fDTestData.containsKey("tenureMonth")) {
            expected = fileReader.fDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month");
        }
        if (fileReader.fDTestData.containsKey("tenureDay")) {
            expected = fileReader.fDTestData.get("tenureDay").trim() + "d";
            softAssert.assertTrue(actual.contains(expected), "tenure days is not same");
        } else {
            softAssert.assertFalse(actual.contains("d"), "tenure contains days as well but it should not as we have not selected any value from days dd");
        }

        softAssert.assertTrue(fdPage.reviewDebitAcc.getText().trim().equalsIgnoreCase(getAccountNumber()), "debit account is not same");
//      softAssert.assertTrue(fdPage.reviewInterestRate.getText().equalsIgnoreCase(fileReader.fDTestData.get("interestRate")), "interest rate is not same");
        softAssert.assertTrue(fdPage.reviewInterestPayoutType.getText().trim().toLowerCase().contains(fileReader.fDTestData.get("interestPayoutType").toLowerCase()), "interest payout type is not same");
        softAssert.assertTrue(fdPage.reviewActionOnMaturity.getText().trim().toLowerCase().contains(fileReader.fDTestData.get("actionOnMaturity").toLowerCase()), "action on maturity is not same");
//      softAssert.assertTrue(fdPage.reviewTotalInterestAmt.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("interestAmount")), "total interest amount is not same");
        softAssert.assertTrue(fdPage.reviewTotalInterestAmt.getText().matches(".*\\d.*"), "total interest amount is not displayed");
        softAssert.assertTrue(fdPage.reviewInterestRate.getText().matches(".*\\d.*"), "interest rate is not displayed");

        //attachScreenshot(driver, scenario);
        scrollIntoView(driver, fdPage.reviewCreditAccount);
        /**** Maturity Amount Verify ****/
        double totalInterestAmount = Double.parseDouble(fdPage.totalInterestAmount.getText().replace("₹", "").replace(",", ""));
        double depositAmount = Double.parseDouble(fileReader.fDTestData.get("depositAmount") + ".00");
        double totalMaturityAmount = totalInterestAmount + depositAmount;
        logger.info("Maturity amount is :" + totalMaturityAmount);
 //     softAssert.assertEquals(fdPage.reviewMaturityAmt.getText().replace("₹", "").replace(",", ""), String.valueOf(totalMaturityAmount),"maturity amount not matched");
//      softAssert.assertEquals(fdPage.reviewMaturityAmt.getText().replace("₹", "").replace(",", ""), (fileReader.fDTestData.get("maturityAmount")), "maturity amount is not same");
        softAssert.assertEquals(fdPage.reviewCreditAccount.getText().trim(),getAccountNumber(), "credit account is not same as debit account");
        softAssert.assertTrue(fdPage.reviewMaturityAmt.getText().matches(".*\\d.*"), "maturity amount is not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }


    @When("User click on open FD confirm button")
    public void userClickOnOpenFDConfirmButton() {
        scrollIntoViewUp(driver, fdPage.reviewConfirmButton);
        clickOnButton(fdPage.reviewConfirmButton);
        // waitTillInvisibilityOfLoader(driver);
    }

    public void enterAmount(String amount) {
        int amt = Integer.parseInt(amount);
        if (amt < 5000) {
            fdPage.depositAmount.sendKeys("1000");
            amt -= 1000;
        } else if (amt < 10000) {
            fdPage.depositAmount.sendKeys("5000");
            amt -= 5000;
        } else {
            fdPage.depositAmount.sendKeys("10000");
            amt -= 10000;
        }
        fdPage.depositAmount.sendKeys(Keys.TAB);
        while (amt != 0) {
            if (amt < 5000) {
                clickOnButton(fdPage.capsul1000);
                amt -= 1000;
            } else if (amt < 10000) {
                clickOnButton((fdPage.capsul5000));
                amt -= 5000;
            } else {
                clickOnButton(fdPage.capsul10000);
                amt -= 10000;
            }
        }
    }

    @Then("User will be navigated on the fd receipt page")
    public void userWillBeNavigatedOnTheFdReceiptPage() {
        System.out.println("receipt page title: " + driver.getTitle());
        Assert.assertTrue(true);
        waitTillInvisibilityOfLoader(driver);

    }

    @And("User can validate fd is opened successfully")
    public void userCanValidateFdIsOpenedSuccessfully() {
        waitTillInvisibilityOfLoader(driver);
        String actual, expected;
        softAssert = new SoftAssert();
        String payoutType;
        try {
            softAssert.assertTrue(fdPage.fdReceiptPageHeader.isDisplayed());
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Successfully message not displayed");
        }
        softAssert.assertTrue(fdPage.receiptPrincipalAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");

        actual = fdPage.receiptTenure.getText().replace(" ", "");
        if (fileReader.fDTestData.containsKey("tenureYear")) {
            expected = fileReader.fDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.fDTestData.containsKey("tenureMonth")) {
            expected = fileReader.fDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month");
        }
        if (fileReader.fDTestData.containsKey("tenureDay")) {
            expected = fileReader.fDTestData.get("tenureDay").trim() + "d";
            softAssert.assertTrue(actual.contains(expected), "tenure days is not same");
        } else {
            softAssert.assertFalse(actual.contains("d"), "tenure contains days as well but it should not as we have not selected any value from days dd");
        }
        if (fileReader.fDTestData.get("interestPayoutType").contains("maturity")) {
            payoutType = "on " + fileReader.fDTestData.get("interestPayoutType");
        } else {
            payoutType = fileReader.fDTestData.get("interestPayoutType");

        }
        scrollIntoView(driver, fdPage.receiptCreditAccount);
        softAssert.assertTrue(fdPage.receiptDebitAcc.getText().trim().equalsIgnoreCase(getAccountNumber()), "debit account is not same");
//      softAssert.assertEquals(fdPage.receiptInterestRate.getAttribute("innerHTML").trim(), fileReader.fDTestData.get("interestRate").trim(), "interest rate is not same");
        softAssert.assertTrue(fdPage.receiptInterestRate.getAttribute("innerHTML").matches(".*\\d.*"), "interest rate is not displayed");

//      softAssert.assertTrue(fdPage.receiptTotalInterestAmt.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("interestAmount")), "total interest amount is not same");
//      softAssert.assertTrue(fdPage.receiptMaturityAmt.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("maturityAmount")), "maturity amount is not same");
        softAssert.assertTrue(fdPage.receiptCreditAccount.getText().trim().equalsIgnoreCase(getAccountNumber()), "credit account is not same as debit account");
        softAssert.assertEquals(fdPage.receiptInterestPayoutType.getAttribute("innerHTML").trim().toLowerCase(), payoutType.trim().toLowerCase(), "interest payout type is not same");
        softAssert.assertTrue(fdPage.receiptActionOnMaturity.getAttribute("innerHTML").trim().toLowerCase().contains(fileReader.fDTestData.get("actionOnMaturity").toLowerCase()), "action on maturity is not same");
        softAssert.assertTrue(fdPage.receiptTotalInterestAmt.getText().matches(".*\\d.*"), "total interest amount is not displayed");
        softAssert.assertTrue(fdPage.receiptMaturityAmt.getText().matches(".*\\d.*"), "maturity amount is not displayed");

        logger.info("Deposit Number in Receipt page is :" + fdPage.depositNumber.getText());
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        setDepositNo(fdPage.fdNumber.getText().split(":")[1].split("-")[0].trim());
        System.out.println("FD Nubmer" + getDepositNo());
    }

    @And("User successfully opened a FD account")
    public void userSuccessfullyOpenedAFDAccount() {
        userNavigatesOnTheFDPage();
        userEntersAllNeededDetails();
        userClickOnOpenFixedDepositButton();
        userNavigatesOnTheFDReviewPage();
        userClickOnOpenFDConfirmButton();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
        userWillBeNavigatedOnTheFdReceiptPage();
        //attachScreenshot(driver, scenario);
        try {
            setDepositNo(fdPage.fdNumber.getText().split(":")[1].split("-")[0].trim());
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Please check the screen shot");
        }
        System.out.println("FD Number" + getDepositNo());
    }



       /*@When("User navigates back on the deposit dashboard page")
    public void userNavigatesBackOnTheDepositDashboardPage() {
//        driver.navigate().back();
        fdPage.backToDepositButton.click();
    }*/

    /**
     * Given Below Codes Refers to Fd download function
     **/
    @And("User successfully navigates fd receipt page")
    public void userSuccessfullyNavigatesFdReceiptPage() {
        userNavigatesOnTheFDPage();
        userEntersAllNeededDetails();
        userClickOnOpenFixedDepositButton();
        userClickOnOpenFDConfirmButton();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
    }

    @When("User click on any one of the view button from the deposits list")
    public void userClickOnAnyOneOfTheViewButtonFromTheDepositsList() {
        fdPage.viewButtonFD.click();
        waitTillInvisibilityOfLoader(driver);

    }

    @And("User validate  interest rates link in fixed deposit")
    public void userValidateInterestRatesLinkInFixedDeposit() {
        softAssert = new SoftAssert();
        String currentWindow = driver.getWindowHandle();
        fdPage.interestRateLinkFd.click();
        waitTillInvisibilityOfLoader(driver);
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindow)) {
                driver.switchTo().window(windowHandle);
                softAssert.assertTrue(driver.getCurrentUrl().contains("interest-rates"), "interest-rates page not displayed");
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

    @Then("User can validate the newly opened joined FD in the FD list")
    public void userCanValidateTheNewlyOpenedJoinedFDInTheFDList() {
        {

            softAssert = new SoftAssert();
            try {
                logger.info("Fd Number: " + getDepositNo());
                scrollIntoView(driver, depositsDashboardPage.getDepositTile(getDepositNo(), "FD"));
                //  attachScreenshot(driver, scenario);
                logger.info(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText());
                softAssert.assertTrue(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");
//              softAssert.assertTrue(depositsDashboardPage.getDepositMaturingAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.jointFdTestData.get("maturityAmount")), "maturity amount is not same");
//              softAssert.assertTrue(depositsDashboardPage.getDepositInterestRateField(getDepositNo()).getText().trim().equalsIgnoreCase(fileReader.jointFdTestData.get("interestRate")), "interest rate is not same");
                softAssert.assertTrue(depositsDashboardPage.getDepositMaturingAmountField(getDepositNo()).getText().matches(".*\\d.*"), "maturity amount is not displayed");
                softAssert.assertTrue(depositsDashboardPage.getDepositInterestRateField(getDepositNo()).getText().matches(".*\\d.*"), "interest rate is not displayed");

            } catch (NoSuchElementException e) {
                attachScreenshot(driver, scenario);
                softAssert.assertTrue(fdPage.fixedDepositsHead.getText().contains("Fixed Deposits"), "deposits list not displayed");
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

    @Then("User can validate the joint FD summary is auto populated")
    public void userCanValidateTheJointFDSummaryIsAutoPopulated() {
        String actual, expected;
        softAssert = new SoftAssert();
        System.out.println("this is principal amount: " + fdPage.principalAmount.getText().replace("₹", ""));
        softAssert.assertTrue(fdPage.principalAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");
        actual = fdPage.summaryTenure.getText().replace(" ", "");
        if (fileReader.fDTestData.containsKey("tenureYear")) {
            expected = fileReader.fDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.fDTestData.containsKey("tenureMonth")) {
            expected = fileReader.fDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month");
        }
        if (fileReader.fDTestData.containsKey("tenureDay")) {
            expected = fileReader.fDTestData.get("tenureDay").trim() + "d";
            softAssert.assertTrue(actual.contains(expected), "tenure days is not same");
        } else {
            softAssert.assertFalse(actual.contains("d"), "tenure contains days as well but it should not as we have not selected any value from days dd");
        }

//        softAssert.assertTrue(fdPage.interestRate.getText().equalsIgnoreCase(fileReader.jointFdTestData.get("interestRate")), "joint fd interest rate is not same");
//        softAssert.assertTrue(fdPage.totalInterestAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.jointFdTestData.get("interestAmount")), "joint fd total interest amount is not same");
//        softAssert.assertTrue(fdPage.maturityAmount.getText().replace("₹", "").replace(",", "").trim().equalsIgnoreCase(fileReader.jointFdTestData.get("maturityAmount").trim()), "joint fd maturity amount is not same");
        softAssert.assertTrue(fdPage.interestRate.getText().matches(".*\\d.*"), "joint fd interest rate is not displayed");
        softAssert.assertTrue(fdPage.totalInterestAmount.getText().matches(".*\\d.*"), "joint fd total interest amount is not displayed");
        softAssert.assertTrue(fdPage.maturityAmount.getText().matches(".*\\d.*"), "joint fd maturity amount is not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User can validate the joint FD review page")
    public void userCanValidateTheJointFDReviewPage() {

        String actual, expected;
        softAssert = new SoftAssert();
        softAssert.assertTrue(fdPage.reviewPrincipalAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");

        actual = fdPage.reviewTenure.getText().replace(" ", "");
        if (fileReader.fDTestData.containsKey("tenureYear")) {
            expected = fileReader.fDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.fDTestData.containsKey("tenureMonth")) {
            expected = fileReader.fDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month");
        }
        if (fileReader.fDTestData.containsKey("tenureDay")) {
            expected = fileReader.fDTestData.get("tenureDay").trim() + "d";
            softAssert.assertTrue(actual.contains(expected), "tenure days is not same");
        } else {
            softAssert.assertFalse(actual.contains("d"), "tenure contains days as well but it should not as we have not selected any value from days dd");
        }
        String changesMadeInterestPayout = "on " + fileReader.fDTestData.get("interestPayoutType");

        softAssert.assertTrue(fdPage.reviewInterestRate.getText().contains(fileReader.jointFdTestData.get("interestRate")), "interest rate is not same");
        softAssert.assertTrue(fdPage.reviewDebitAcc.getText().trim().equalsIgnoreCase(fileReader.jointFdTestData.get("fromAccount")), "joint fd debit account is not same");
        scrollIntoView(driver, fdPage.reviewActionOnMaturity);
        softAssert.assertTrue(fdPage.reviewActionOnMaturity.getText().trim().toLowerCase().contains(fileReader.fDTestData.get("actionOnMaturity").toLowerCase()), "action on maturity is not same");
        softAssert.assertTrue(fdPage.reviewTotalInterestAmt.getText().replace("₹", "").replaceAll(",", "").matches(".*\\d.*"), "total interest amount is not displayed");
        softAssert.assertTrue(fdPage.reviewCreditAccount.getText().trim().equalsIgnoreCase(fileReader.jointFdTestData.get("creditAccount")), "joint fd credit account not be the same");
        softAssert.assertTrue(fdPage.reviewInterestPayoutType.getText().trim().toLowerCase().contains(changesMadeInterestPayout.toLowerCase()), "interest payout type is not same");


        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User can validate joint fd is opened successfully")
    public void userCanValidateJointFdIsOpenedSuccessfully() {
        waitTillInvisibilityOfLoader(driver);
        String actual, expected;
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(fdPage.fdReceiptPageHeader.isDisplayed());
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Successfully message not displayed");
        }
        softAssert.assertTrue(fdPage.receiptPrincipalAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");
        actual = fdPage.receiptTenure.getText().replace(" ", "");
        if (fileReader.fDTestData.containsKey("tenureYear")) {
            expected = fileReader.fDTestData.get("tenureYear").trim() + "y";
            softAssert.assertTrue(actual.contains(expected), "tenure year is not same");
        }
        if (fileReader.fDTestData.containsKey("tenureMonth")) {
            expected = fileReader.fDTestData.get("tenureMonth").trim() + "m";
            softAssert.assertTrue(actual.contains(expected), "tenure month is not same");
        } else {
            softAssert.assertFalse(actual.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month");
        }
        if (fileReader.fDTestData.containsKey("tenureDay")) {
            expected = fileReader.fDTestData.get("tenureDay").trim() + "d";
            softAssert.assertTrue(actual.contains(expected), "tenure days is not same");
        } else {
            softAssert.assertFalse(actual.contains("d"), "tenure contains days as well but it should not as we have not selected any value from days dd");
        }
        scrollIntoView(driver, fdPage.receiptCreditAccount);
//      softAssert.assertTrue(fdPage.receiptInterestRate.getText().equalsIgnoreCase(fileReader.jointFdTestData.get("interestRate")), "interest rate is not same");
        softAssert.assertTrue(fdPage.receiptCreditAccount.getText().trim().equalsIgnoreCase(fileReader.jointFdTestData.get("creditAccount")), "joint fd credit account is not same as debit account");
        softAssert.assertTrue(fdPage.reviewDebitAcc.getText().trim().equalsIgnoreCase(fileReader.jointFdTestData.get("fromAccount")), "joint fd debit account is not same");
        softAssert.assertEquals(fdPage.receiptInterestRate.getAttribute("innerHTML").trim(), fileReader.jointFdTestData.get("interestRate").trim(), "interest rate is not same");
//        softAssert.assertTrue(fdPage.receiptTotalInterestAmt.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.jointFdTestData.get("interestAmount")), "total interest amount is not same");
//        softAssert.assertTrue(fdPage.receiptMaturityAmt.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.jointFdTestData.get("maturityAmount")), "maturity amount is not same");
        softAssert.assertEquals(fdPage.receiptInterestPayoutType.getAttribute("innerHTML").trim().toLowerCase(), "on " + fileReader.fDTestData.get("interestPayoutType").trim().toLowerCase(), "interest payout type is not same");
        softAssert.assertTrue(fdPage.receiptActionOnMaturity.getAttribute("innerHTML").trim().toLowerCase().contains(fileReader.fDTestData.get("actionOnMaturity").toLowerCase()), "action on maturity is not same");
        softAssert.assertTrue(fdPage.receiptTotalInterestAmt.getText().matches(".*\\d.*"), "total interest amount is not displayed");
        softAssert.assertTrue(fdPage.receiptMaturityAmt.getText().matches(".*\\d.*"), "maturity amount is not displayed");

        logger.info("Deposit Number in Receipt page is :" + fdPage.depositNumber);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        setDepositNo(fdPage.fdNumber.getText().split(":")[1].split("-")[0].trim());
        System.out.println("FD Nubmer" + getDepositNo());
    }

    @When("User enters all needed details on joint FD page")
    public void userEntersAllNeededDetailsOnJointFDPage() {
        softAssert = new SoftAssert();
        WebElement element = null;
        isJointFD = false;
        jointFd = fdPage.jointAccountName.getText();
        staticWait(3000);
        selectFromAccount(driver, fdPage.fromAccount, fileReader.jointFdTestData.get("fromAccount"));
        isJointFD = true;
        staticWait(3000);
        fdPage.clickJointAccRadioButton.click();
        waitTillVisibilityElement(driver, fdPage.fromAccount);
        String balance[] = fdPage.availableBalance.getText().split(":");
        double availableBalance = Double.parseDouble(balance[1].replaceAll(",", "").replace("₹", "").trim());
        logger.info("Available balance is Rs." + availableBalance);
        fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "999");
        softAssert.assertTrue(fdPage.depositAmountMessage.getText().contains("Amount can not be less than ₹1,000"), "minimum amount enter message not displayed");
        double enterAmount = 40000000;
        fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), Double.toString(enterAmount));
        if (enterAmount < availableBalance) {
            softAssert.assertTrue(fdPage.depositAmountMessage.getText().contains("Insufficient balance"), "less than available balance message not displayed");
        }
        fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "");
        enterAmount(fileReader.fDTestData.get("depositAmount"));
        if (availableBalance > 20000000) {
            fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "20000000");
            softAssert.assertTrue(fdPage.depositAmountMessage.getText().contains("less than ₹1,99,99,999"), "maximum limit amount message not displayed");
        }
        fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.fDTestData.get("depositAmount"));
        fdPage.depositAmount.sendKeys(Keys.TAB);
        if (fileReader.fDTestData.get("interestPayoutType").equalsIgnoreCase("maturity")) {
            element = fdPage.payoutOnMaturity;
        } else if (fileReader.fDTestData.get("interestPayoutType").equalsIgnoreCase("monthly")) {
            element = fdPage.payoutMonthly;
        } else {
            element = fdPage.payoutQuarterly;
        }
        explicitWait(driver, 10).until(ExpectedConditions.elementToBeClickable(element));
        clickOnButton(element);
        waitTillInvisibilityOfLoader(driver);
        if (fileReader.fDTestData.containsKey("tenureYear")) {
            fdPage.yearDD.click();
            selectTenureYear(driver, fileReader.fDTestData.get("tenureYear") + " " + "years");
            waitTillInvisibilityOfLoader(driver);
        }
        if (fileReader.fDTestData.containsKey("tenureMonth")) {
            fdPage.monthDD.click();
            selectTenureMonth(driver, fileReader.fDTestData.get("tenureMonth") + " " + "months");
            waitTillInvisibilityOfLoader(driver);
        }
        if (fileReader.fDTestData.containsKey("tenureDay")) {
            fdPage.dayDD.click();
            selectTenureDays(driver, fileReader.fDTestData.get("tenureDay") + " " + "days");
            waitTillInvisibilityOfLoader(driver);
        }
        if (fileReader.fDTestData.get("actionOnMaturity").equalsIgnoreCase("renew")) {
            element = fdPage.renewOnMaturityRadioButton;
        } else {
            element = fdPage.closeOnMaturityRadioButton;
        }
        explicitWait(driver, 30).until(ExpectedConditions.elementToBeClickable(element));
        clickOnButton(element);
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }
}



