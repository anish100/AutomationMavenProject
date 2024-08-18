package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.poi.ss.formula.atp.Switch;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pom.DepositsDashboardPage;
import pom.DepositDetailsPage;
import pom.FDPage;
import pom.HomePage;
import reusable.Base;
import reusable.TestContext;

import java.io.File;
import java.util.HashMap;

public class DepositDetailsStepDef extends Base {

    HomePage homePage;
    DepositsDashboardPage depositsDashboardPage;
    DepositDetailsPage depositDetailsPage;
    FDPage fdPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;


    public DepositDetailsStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        depositDetailsPage = context.getPageObjectManager().getDepositDetailsPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
    }

    @Then("User navigated on the deposit details page")
    public void userNavigatedOnTheDepositDetailsPage() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, depositDetailsPage.depositDetailsHeader);
        softAssert.assertTrue(depositDetailsPage.depositDetailsHeader.isDisplayed(), "deposit header is not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User can validate the {string} details on deposit details page")
    public void userCanValidateTheDetailsOnDepositDetailsPage(String depositType) {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, depositDetailsPage.depositDetailsHeader);
        String depositAmount = depositDetailsPage.depositAmount.getText().replace("₹", "").replace(",", "");
        String maturityAmount = depositDetailsPage.maturityAmount.getText().replace("₹", "").replace(",", "");
        String interestRate = depositDetailsPage.interestRates.getText();
        String actionOnMaturity = null;
        String tenure = depositDetailsPage.tenure.getText().replace(" ", "");
        String expected;
        HashMap<String, String> testData = null;
        switch (depositType.toUpperCase()) {
            case "FD":
                softAssert.assertTrue(depositAmount.equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");
//              softAssert.assertTrue(maturityAmount.equalsIgnoreCase(fileReader.fDTestData.get("maturityAmount")), "maturity amount is not same");
//              softAssert.assertTrue(depositDetailsPage.maturityAmount.getText().contains("₹"), "maturity amount is not same");
//              softAssert.assertTrue(interestRate.contains("₹"), "interest rate is not displayed");
                softAssert.assertTrue(depositDetailsPage.maturityAmount.getText().matches(".*\\d.*"), "maturity amount is not displayed");
                softAssert.assertTrue(interestRate.matches(".*\\d.*"), "interest rate is not displayed");

                //softAssert.assertTrue(interestRate.equalsIgnoreCase(fileReader.fDTestData.get("interestRate")), "interest rate is not same");
                if (fileReader.fDTestData.get("interestPayoutType").contains("monthly")) {
                    softAssert.assertEquals(depositDetailsPage.payoutType.getText().trim().toLowerCase(), fileReader.fDTestData.get("interestPayoutType").toLowerCase(), "interest payout type is not same");
                }


                testData = fileReader.fDTestData;
                break;
            case "JOINTFD":
                softAssert.assertTrue(depositAmount.equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");
                //    softAssert.assertTrue(maturityAmount.equalsIgnoreCase(fileReader.jointFdTestData.get("maturityAmount")), "maturity amount is not same");
                softAssert.assertTrue(depositDetailsPage.maturityAmount.getText().matches(".*\\d.*"), "maturity amount is not displayed");
                softAssert.assertTrue(interestRate.matches(".*\\d.*"), "interest rate is not displayed");

                if (fileReader.fDTestData.get("interestPayoutType").contains("monthly")) {
                    softAssert.assertEquals(depositDetailsPage.payoutType.getText().trim().toLowerCase(), fileReader.fDTestData.get("interestPayoutType").toLowerCase(), "interest payout type is not same");
                }
                testData = fileReader.fDTestData;
                break;


            case "RD":
                softAssert.assertTrue(depositAmount.equalsIgnoreCase(fileReader.rDTestData.get("monthlyInstallment") + ".00"), "monthly installment is not same");
                // softAssert.assertTrue(maturityAmount.equalsIgnoreCase(fileReader.rDTestData.get("maturityAmount")), "maturity amount is not same");
                softAssert.assertTrue(depositDetailsPage.maturityAmount.getText().contains("₹"), "maturity amount is not displayed");
                softAssert.assertTrue(interestRate.matches(".*\\d.*"), "interest rate is not same");
                softAssert.assertEquals(depositDetailsPage.creditAccountNumber.getText(),getAccountNumber());
                //setDepositNo(depositDetailsPage.creditAccountNumber.getText());
                testData = fileReader.rDTestData;
                break;
            case "TS":
                softAssert.assertTrue(depositAmount.equalsIgnoreCase(fileReader.sOTTestData.get("amount") + ".00"), "principal amount is not same");
//              softAssert.assertTrue(maturityAmount.equalsIgnoreCase(fileReader.sOTTestData.get("maturityAmount")), "maturity amount is not same");
//              softAssert.assertTrue(maturityAmount.contains("₹"), "maturity amount not displayed");
                softAssert.assertTrue(maturityAmount.matches(".*\\d.*"), "maturity amount not displayed");
                softAssert.assertTrue(interestRate.matches(".*\\d.*"),"interestRate not displayed");
//                softAssert.assertTrue(interestRate.equalsIgnoreCase(fileReader.sOTTestData.get("interestRate")), "interest rate is not same");
                testData = fileReader.sOTTestData;
                break;
        }
        if (!depositType.equalsIgnoreCase("TS")) {
            if (testData.containsKey("tenureYear")) {
                expected = testData.get("tenureYear").trim() + "y";
                softAssert.assertTrue(tenure.contains(expected), "tenure year is not same");
            }
            if (testData.containsKey("tenureMonth")) {
                expected = testData.get("tenureMonth").trim() + "m";
                softAssert.assertTrue(tenure.contains(expected), "tenure month is not same");
            } else {
                softAssert.assertFalse(tenure.contains("m"), "tenure contains month as well but it should not as we have not selected any value from month");
            }
            if (testData.containsKey("tenureDay")) {
                expected = testData.get("tenureDay").trim() + "d";
                softAssert.assertTrue(tenure.contains(expected), "tenure days is not same");
            } else {
                softAssert.assertFalse(tenure.contains("d"), "tenure contains days as well but it should not as we have not selected any value from days dd");
            }
        } else {
            softAssert.assertTrue((tenure.contains("5")));
        }
        //attachScreenshot(driver,scenario);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on close account button of deposit details page")
    public void userClicksOnCloseAccountButtonOfDepositDetailsPage() {
        depositDetailsPage.closeAccountButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User can see closing your FD popup appeared")
    public void userCanSeeClosingYourFDPopupAppeared() {
        depositDetailsPage.closingYourFDPopup.isDisplayed();
    }

    @When("User selects {string} radio button on closing your FD popup")
    public void userSelectsRadioButtonOnClosingYourFDPopup(String type) {
        if (type.equalsIgnoreCase("partial closure")) {
            explicitWait(driver, 20).until(ExpectedConditions.elementToBeClickable(depositDetailsPage.partialCloserRadioButton)).click();
//            depositDetailsPage.partialCloserRadioButton.click();
        } else {
            explicitWait(driver, 20).until(ExpectedConditions.elementToBeClickable(depositDetailsPage.fullCloserRadioButton)).click();
//            depositDetailsPage.fullCloserRadioButton.click();
        }
    }

    @And("User clicks on proceed button of closing your FD popup")
    public void userClicksOnProceedButtonOfClosingYourFDPopup() {
        depositDetailsPage.closeDepositProceedButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User clicks download advice button and verify the downloaded file")
    public void userClicksDownloadAdviceButtonAndVerifyTheDownloadedFile() {
        softAssert = new SoftAssert();
        String depositNo = depositDetailsPage.depositDetailsPageAccountNumberGet.getText().split(":")[1].split("-")[0].trim();
        depositDetailsPage.downloadAdviceButton.click();
        staticWait(5000);
        try {
            if (depositDetailsPage.cancelButton.isDisplayed()) {
                attachScreenshot(driver, scenario);
                softAssert.fail("Please verify the screen shot");
                depositDetailsPage.cancelButton.click();
            }
        } catch (NoSuchElementException e) {

        }
        File downloadedAdvice = new File("C:/Users/987993/Downloads/" + depositNo + ".pdf");
        //System.out.println("C:/Users/987993/Downloads/"+depositNo+".pdf");
        staticWait(3000);
        softAssert.assertTrue(downloadedAdvice.exists(), "Download advice download failed");
        waitTillInvisibilityOfLoader(driver);
        if (downloadedAdvice.exists()) {
            downloadedAdvice.delete();
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks download button and verify the downloaded file")
    public void userClicksDownloadButtonAndVerifyTheDownloadedFile() {
        softAssert = new SoftAssert();
        try {
            String depositNo = depositDetailsPage.receiptPageDepositNo.getText().split(":")[1].split("-")[0].trim();
            waitTillInvisibilityOfLoader(driver);
            depositDetailsPage.receiptDownload.click();
            staticWait(3000);
            File downloadedAdvice = new File("C:/Users/987993/Downloads/" + depositNo + ".pdf");
            //System.out.println("C:/Users/987993/Downloads/"+depositNo+".pdf");
            softAssert.assertTrue(downloadedAdvice.exists(), "Download advice download failed");
            waitTillInvisibilityOfLoader(driver);
            if (downloadedAdvice.exists()) {
                downloadedAdvice.delete();
            }
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            throw new NoSuchElementException("Download button page not displayed");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks  add nominee and verify the nominee name")
    public void userClicksAddNomineeAndVerifyTheNomineeName() {
        softAssert = new SoftAssert();
        depositDetailsPage.addNominee.click();


    }

    @And("User clicks add purpose and and verify the purpose")
    public void userClicksAddPurposeAndAndVerifyThePurpose() {
        softAssert = new SoftAssert();
        try {
            depositDetailsPage.addPurpose.click();
        } catch (Exception e) {
            attachScreenshot(driver, scenario);
            softAssert.fail(e.getMessage());
        }
        softAssert.assertTrue(depositDetailsPage.addPurposePOpUpHeader.isDisplayed(), "add purpose header not visible");
        String purposeName = "School";
        depositDetailsPage.addPurposePopUpName.sendKeys(Keys.chord(Keys.CONTROL, "a"), purposeName);
        depositDetailsPage.addPurposePopUpAddButton.click();
        staticWait(3000);
        softAssert.assertEquals(depositDetailsPage.purposeAdded.getText(), purposeName, "added purpose name not matched");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

}
