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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;
import pom.*;
import reusable.Base;
import reusable.TestContext;

import java.util.HashMap;

public class DepositClosureStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(DepositClosureStepDef.class);
    DepositClosurePage depositClosurePage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;


    public DepositClosureStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        depositClosurePage = context.getPageObjectManager().getDepositClosurePage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
    }

    @Then("User will be navigated on the {string} closure page")
    public void userWillBeNavigatedOnTheClosurePage(String type) {
        switch (type) {
            case "fd-partial":
                depositClosurePage.partialClosureHeader.isDisplayed();
                break;
            case "fd-full":
                depositClosurePage.fullClosureHeader.isDisplayed();
                break;
            case "rd":
                depositClosurePage.rdClosureHeader.isDisplayed();
        }
    }

    @When("User enters the withdrawal amount")
    public void userEntersTheWithdrawalAmount() {
        depositClosurePage.withdrawalAmount.sendKeys(fileReader.fDTestData.get("withdrawalAmount"));
        logger.info("User Enter the withdraw amount of"+fileReader.fDTestData.get("withdrawalAmount"));
        depositClosurePage.withdrawalAmount.sendKeys(Keys.TAB);
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User selects credit account for {string} closure")
    public void userSelectsCreditAccount(String type) {
        depositClosurePage.creditAccountDD.click();
        if (type.equalsIgnoreCase("fd"))
            depositClosurePage.selectCreditAccount(getAccountNumber());
        else
            depositClosurePage.selectCreditAccount(getAccountNumber());
        waitTillInvisibilityOfLoader(driver);
        staticWait(1000);
    }

    @Then("User can verify the amount details and {string} closure summary")
    public void userCanVerifyThePartialClosureSummary(String closureType) {
        softAssert = new SoftAssert();
        if (closureType.toLowerCase().contains("rd")) {
            softAssert.assertTrue(depositClosurePage.principalAmountOrAmountDeposited.getText().contains("₹"), "deposited amount is not visible in the account details");
            softAssert.assertTrue(depositClosurePage.summaryTaxToBeDeducted.getText().contains("₹"), "tax to be deducted is not displayed");
        } else {
            softAssert.assertTrue(depositClosurePage.principalAmountOrAmountDeposited.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "total amount is not same");
        }
        softAssert.assertTrue(depositClosurePage.interestEarned.getText().contains("₹"), "interest earned value is not displayed");
        softAssert.assertTrue(depositClosurePage.currentFDOrRDValue.getText().replace("₹", "").replace(",", "").length() != 0, "current fd value is not displayed");
        softAssert.assertTrue(depositClosurePage.summaryInterestEarned.getText().contains("₹"), "summary interest earned value is not displayed");
        softAssert.assertTrue(depositClosurePage.summaryPreClosurePenalty.getText().contains("₹"), "summary pre closure penalty is not displayed");
        softAssert.assertTrue(depositClosurePage.summaryNetPayable.getText().contains("₹"), "summary net payable value is not displayed");
        if (closureType.toLowerCase().contains("fd-full")) {
            softAssert.assertTrue(depositClosurePage.summaryWithdrawalOrPrincipalOrDepositedAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");
        } else if (closureType.toLowerCase().contains("fd-partial")) {
            softAssert.assertTrue(depositClosurePage.summaryWithdrawalOrPrincipalOrDepositedAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("withdrawalAmount") + ".00"), "withdrawal amount is not same");
            softAssert.assertTrue(depositClosurePage.summaryInterestToBeRecovered.getText().contains("₹"), "interest to be recovered is not displayed");
            softAssert.assertTrue(depositClosurePage.summaryTaxToBeDeducted.getText().contains("₹"), "tax to be deducted is not displayed");
        } else {
            softAssert.assertTrue(depositClosurePage.summaryWithdrawalOrPrincipalOrDepositedAmount.getText().contains("₹"), "amount deposited is not visible for RD");
        }
        // attachScreenshot(driver, scenario);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            logger.error("Some Assertion occurred please check the report");
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on confirm button")
    public void userClicksOnConfirmButton() {
        depositClosurePage.summaryNextButton.click();
       // depositClosurePage.conformButton.click();
    //    waitTillInvisibilityOfLoader(driver);
    }

    @Then("User will be navigated on the {string} closure review page")
    public void userWillBeNavigatedOnTheClosureReviewPage(String arg0) {
    try{    scrollIntoViewUp(driver, depositClosurePage.reviewPageHeader);
        waitForPageLoad(driver);
        depositClosurePage.reviewPageHeader.isDisplayed();}
    catch (NoSuchElementException e){
        attachScreenshot(driver,scenario);
        softAssert.fail("Review page header not visible");
    }
    }

    @And("User can verify the details on {string} closure review page")
    public void userCanVerifyTheDetailsOnClosureReviewPage(String closureType) {
        softAssert = new SoftAssert();
        softAssert.assertTrue(depositClosurePage.reviewWithdrawalOrFDValueOrDepositedAmount.getText().contains("₹"), "amount is not displayed");
        softAssert.assertTrue(depositClosurePage.reviewInterestEarned.getText().contains("₹"), "review interest earned value is not displayed");
        softAssert.assertTrue(depositClosurePage.reviewPreClosurePenalty.getText().contains("₹"), "review pre closure penalty is not displayed");
        softAssert.assertTrue(depositClosurePage.reviewNetPayable.getText().contains("₹"), "review net payable value is not displayed");

        switch (closureType.toLowerCase()) {
            case "fd-partial":
                softAssert.assertTrue(depositClosurePage.reviewInterestToBeRecovered.getText().contains("₹"), "interest to be recovered is not displayed");
                softAssert.assertTrue(depositClosurePage.reviewTaxToBeDeducted.getText().contains("₹"), "tax to be deducted is not displayed");
                break;
            case "rd":
                softAssert.assertTrue(depositClosurePage.reviewTaxToBeDeducted.getText().contains("₹"), "tax to be deducted is not displayed");
        }
        // attachScreenshot(driver, scenario);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @Then("User will be navigated on the {string} closure receipt page")
    public void userWillBeNavigatedOnTheFdPartialReceiptPage(String type) {
        waitTillInvisibilityOfLoader(driver);
        //  attachScreenshot(driver,scenario);
   try {
       scrollIntoViewUp(driver, depositClosurePage.receiptPageHeader);
       depositClosurePage.receiptPageHeader.isDisplayed();
   }
   catch (NoSuchElementException e){
       attachScreenshot(driver,scenario);
       softAssert.fail("Receipt page header not displayed");
   }
    }

    @And("User can verify that {string} closed successfully")
    public void userCanVerifyThatClosedSuccessfully(String closureType) {
        softAssert = new SoftAssert();
        softAssert.assertTrue(depositClosurePage.receiptInitialAmountOrFDValueOrRDValue.getText().contains("₹"), "amount is not displayed");
        softAssert.assertTrue(depositClosurePage.receiptReceivedAmount.getText().contains("₹"), "received amount is not displayed");
        scrollIntoView(driver, depositClosurePage.backToDepositButton);
        switch (closureType.toLowerCase()) {
            case "fd-partial":
                System.out.println("This is credit account: " + depositClosurePage.receiptCreditAccount.getText().trim());
                softAssert.assertEquals(depositClosurePage.receiptCreditAccount.getText().trim(),getAccountNumber(), "credit account is not same");
                softAssert.assertTrue(depositClosurePage.receiptWithdrawalAmount.getText().contains("₹"), "withdrawal amount is not displayed");
                softAssert.assertTrue(depositClosurePage.receiptRemainingFDAmount.getAttribute("innerHTML").contains("₹"), "Remaining FD amount is not displayed");
//              softAssert.assertTrue(depositClosurePage.receiptUpdatedMaturityAmount.getText().contains("₹"),"Updated maturity amount is not displayed");
                softAssert.assertTrue(!depositClosurePage.receiptMaturingOn.getText().isEmpty(), "maturing date is not displayed");
                break;
            case "fd-full":
                softAssert.assertTrue(depositClosurePage.receiptCreditAccount.getText().trim().contains(getAccountNumber()), "credit account is not same");
                softAssert.assertTrue(depositClosurePage.receiptInterestEarned.getText().contains("₹"), "interest earned is not displayed");
                softAssert.assertTrue(!depositClosurePage.receiptCreatedOn.getText().isEmpty(), "createdOn date is not displayed");
                softAssert.assertTrue(!depositClosurePage.receiptForClosedOn.getText().isEmpty(), "ForeClosed date is not displayed");
                break;
            case "rd":
//                System.out.println(depositClosurePage.receiptCreditAccount.getText());
//                System.out.println(depositClosurePage.receiptCreditAccount.getAttribute("innerHTML"));
//                boolean verifyCreditAccount=depositClosurePage.receiptCreditAccount.getText().length()!=0;
//                System.out.println(verifyCreditAccount);
                softAssert.assertTrue(depositClosurePage.receiptCreditAccount.getAttribute("innerHTML").length()!=0, "credit account is not displayed");
                softAssert.assertTrue(depositClosurePage.receiptMonthlyInstallment.getText().contains("₹"), "monthly installment is not displayed");
                softAssert.assertTrue(depositClosurePage.receiptCreatedOn.getText().length() != 0, "createdOn date is not displayed");
                softAssert.assertTrue(depositClosurePage.receiptInterestEarned.getText().contains("₹"), "interest earned is not displayed");
                softAssert.assertTrue(depositClosurePage.receiptForClosedOn.getText().length() != 0, "ForeClosed date is not displayed");
                logger.info("Deposit number is "+getDepositNo());
                //System.out.println(getDepositNo());
        }
        scrollIntoViewUp(driver,depositClosurePage.receiptPageHeader);
        // System.out.println(depositClosurePage.receiptRemainingFDAmount.getText()+"Reciept remaining amount");
        // attachScreenshot(driver, scenario);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }
}
