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
import org.testng.asserts.SoftAssert;
import pom.TSPage;
import reusable.Base;
import reusable.TestContext;

public class TSStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(TSStepDef.class);
    TSPage tsPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    HomePageStepDef homePageStepDef;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;


    public TSStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        tsPage = context.getPageObjectManager().getTSPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        homePageStepDef=new HomePageStepDef(context);
    }

    @And("User navigates on the 'open tax saver' page")
    public void userNavigatesOnTheOpenTaxSaverPage() {
        explicitWait(driver, 10).until(ExpectedConditions.visibilityOf(tsPage.sotButton));
        clickOnButton(tsPage.sotButton);
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User enters all needed details on TS page")
    public void userEntersAllNeededDetailsOnTSPage() {
        softAssert = new SoftAssert();
        selectFromAccount(driver, tsPage.fromAccount,getAccountNumber());
        waitTillInvisibilityOfLoader(driver);
        tsPage.amount.sendKeys(Keys.chord(Keys.CONTROL,"a"),fileReader.sOTTestData.get("amount"));
        tsPage.amount.sendKeys(Keys.TAB);
        waitTillInvisibilityOfLoader(driver);

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    public void enterAmount(String amount) {
        int amt = Integer.parseInt(amount);
        if (amt < 5000) {
            tsPage.amount.sendKeys("1000");
            amt -= 1000;
        } else if (amt < 10000) {
            tsPage.amount.sendKeys("5000");
            amt -= 5000;
        } else {
            tsPage.amount.sendKeys("10000");
            amt -= 10000;
        }
        tsPage.amount.sendKeys(Keys.TAB);
        while (amt != 0) {
            if (amt < 5000) {
                clickOnButton(tsPage.capsul1000);
                amt -= 1000;
            } else if (amt < 10000) {
                clickOnButton((tsPage.capsul5000));
                amt -= 5000;
            } else {
                clickOnButton(tsPage.capsul10000);
                amt -= 10000;
            }
        }
    }

    @Then("User can validate the TS summary is auto populated")
    public void userCanValidateTheSOTSummaryIsAutoPopulated() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(tsPage.summaryPrincipalAmount.getText().replace("₹", "").replaceAll(",", "").trim().equalsIgnoreCase(fileReader.sOTTestData.get("amount") + ".00".trim()), "principal amount is not same");
        softAssert.assertTrue(tsPage.summaryTenure.getText().trim().equalsIgnoreCase("5 years"), "tenure is not 5 years");
        softAssert.assertTrue(tsPage.maturityAmount.getText().contains("₹"), "maturity amount is not displayed");
        softAssert.assertTrue(tsPage.summaryInterestAmount.getText().contains("₹"), "interest amount is not displayed");
        softAssert.assertTrue(tsPage.summaryInterestRate.getText().contains("%"), "interest rate is not displayed");

        // attachScreenshot(driver, scenario);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User click on 'open tax saver' button")
    public void userClickOnOpenTaxSaverButton() {
        waitTillInvisibilityOfLoader(driver);
        clickOnButton(tsPage.openTaxSaverButton);
    }

    @Then("User navigates on the 'open tax saver' review page")
    public void userNavigatesOnTheOpenTaxSaverReviewPage() {
        Assert.assertTrue(tsPage.sotReviewPageHeader.isDisplayed());
    }

    @And("User can validate the 'open tax saver' review page")
    public void userCanValidateTheOpenTaxSaverReviewPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(tsPage.reviewPrincipalAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.sOTTestData.get("amount") + ".00"), "principal amount is not same");
        softAssert.assertTrue(tsPage.reviewDebitAcc.getText().trim().equalsIgnoreCase(fileReader.sOTTestData.get("fromAccount")), "debit account is not same");
        softAssert.assertTrue(tsPage.reviewTenure.getText().trim().equalsIgnoreCase("5 years"), "tenure is not 5 years");
        scrollIntoView(driver, tsPage.reviewCreditAccount);
       // softAssert.assertTrue(sotPage.reviewMaturityAmt.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.sOTTestData.get("maturityAmount")), "maturity amount is not same");
        softAssert.assertTrue(tsPage.reviewInterestRate.getText().contains("%"), "interest rate is not displayed");
        softAssert.assertTrue(tsPage.reviewInterestAmount.getText().contains("₹"), "total interest amount is not displayed");
        softAssert.assertTrue(tsPage.reviewCreditAccount.getText().trim().equalsIgnoreCase(fileReader.sOTTestData.get("fromAccount")), "credit account is not same as debit account");

        //attachScreenshot(driver, scenario);
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User click on 'open tax saver' confirm button")
    public void userClickOnOpenTaxSaverConfirmButton() {
        clickOnButton(tsPage.reviewConfirmButton);
    //  waitTillInvisibilityOfLoader(driver);
    }

    @Then("User can validate tax saver opened successfully")
    public void userCanValidateTaxSaverOpenedSuccessfully() {
        softAssert = new SoftAssert();
      try {
          softAssert.assertTrue(tsPage.rdReceiptPageHeader.isDisplayed());
      }
      catch (NoSuchElementException e){
          attachScreenshot(driver,scenario);
          softAssert.fail("Successful message not displayed");
      }
        softAssert.assertTrue(tsPage.receiptPrincipalAmount.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.sOTTestData.get("amount") + ".00"), "principal amount is not same");
        softAssert.assertTrue(tsPage.receiptDebitAcc.getText().trim().equalsIgnoreCase(fileReader.sOTTestData.get("fromAccount")), "debit account is not same");
        softAssert.assertTrue(tsPage.receiptTenure.getText().trim().equalsIgnoreCase("5 years"), "tenure is not 5 years");
    //  attachScreenshot(driver, scenario);
        scrollIntoView(driver, tsPage.receiptCreditAccount);
    //  softAssert.assertTrue(sotPage.receiptMaturityAmt.getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.sOTTestData.get("maturityAmount")), "maturity amount is not same");
        softAssert.assertTrue(tsPage.receiptCreditAccount.getText().trim().equalsIgnoreCase(fileReader.sOTTestData.get("fromAccount")), "credit account is not same as debit account");
       //   softAssert.assertTrue(sotPage.receiptInterestRate.getText().contains("%"), "interest rate is not displayed");
         softAssert.assertTrue(tsPage.receiptTotalInterestAmt.getText().contains("₹"), "interest amount is not displayed");
         softAssert.assertTrue(tsPage.receiptMaturityAmt.getText().contains("₹"), "maturity amount is not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        setDepositNo(tsPage.sotNumber.getText().split(":")[1].split("-")[0].trim());
        logger.info("TS Number{}", getDepositNo());
    }

    @Then("User will be navigated on the TS receipt page")
    public void userWillBeNavigatedOnTheTSReceiptPage() {
        logger.info("TS page title: {}", driver.getTitle());
        Assert.assertTrue(true);
    }


/**
 * Tax Saver download functionality codes
 * */

    @And("User successfully navigates TS receipt page")
    public void userSuccessfullyNavigatesTSReceiptPage()  {
        userNavigatesOnTheOpenTaxSaverPage();
        userEntersAllNeededDetailsOnTSPage();
        userClickOnOpenTaxSaverButton();
        userClickOnOpenTaxSaverConfirmButton();
 //       homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
    }
    @When("User click on view button of in the tax saver list")
    public void userClickOnViewButtonOfInTheTaxSaverList() {
        tsPage.viewButtonTS.click();
         waitTillInvisibilityOfLoader(driver);
    }

    @And("User validate interest rates link in tax saver")
    public void userValidateInterestRatesLinkInTaxSaver() {
        softAssert=new SoftAssert();
        String currentWindow = driver.getWindowHandle();
        tsPage.interestRateLinkTs.click();
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
}
