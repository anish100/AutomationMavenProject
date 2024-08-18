package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pom.*;
import reusable.Base;
import reusable.TestContext;

import java.io.File;

public class DepositDashboardStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(DepositDashboardStepDef.class);
    HomePage homePage;
    DepositsDashboardPage depositsDashboardPage;
    AccountStatementPage accountStatementPage;
    MoneyTransferPage moneyTransferPage;
    HomePageStepDef homePageStepDef;
    FDPage fdPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;


    public DepositDashboardStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        depositsDashboardPage = context.getPageObjectManager().getDepositDashboardPage();
        homePage = context.getPageObjectManager().getHomePage();
        fdPage = context.getPageObjectManager().getFDPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        moneyTransferPage = context.getPageObjectManager().getMoneyTransferPage();
        accountStatementPage = context.getPageObjectManager().getAccountStatementPage();
        homePageStepDef = new HomePageStepDef(context);
    }

    @And("User navigates on the 'select deposit account type' page")
    public void userNavigatesOnTheFDPage() {
        logger.info("User is on deposit dashboard page");
        explicitWait(driver, 10).until(ExpectedConditions.elementToBeClickable(depositsDashboardPage.openNowButton));
        clickOnButton(depositsDashboardPage.openNowButton);
        waitTillInvisibilityOfLoader(driver);
    }

    @When("User navigates back on the deposit dashboard page")
    public void userNavigatesBackOnTheDepositDashboardPage() {
        try {
            waitTillInvisibilityOfLoader(driver);
            fdPage.backToDepositButton.click();
            waitTillInvisibilityOfLoader(driver);
            explicitWait(driver, 30).until(ExpectedConditions.visibilityOf(homePage.openNowButton));
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            throw new RuntimeException();
        }
        scrollIntoViewUp(driver, depositsDashboardPage.pageHeader);
    }

    @Then("User can validate the newly opened FD in the FD list")
    public void userCanValidateTheNewlyOpenedFDInTheFDList() {

        softAssert = new SoftAssert();
        try {
            logger.info("Fd Number: " + getDepositNo());
            scrollIntoView(driver, depositsDashboardPage.getDepositTile(getDepositNo(), "FD"));
            //  attachScreenshot(driver, scenario);
            logger.info(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText());
            softAssert.assertTrue(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("depositAmount") + ".00"), "principal amount is not same");
//           softAssert.assertTrue(depositsDashboardPage.getDepositMaturingAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.fDTestData.get("maturityAmount")), "maturity amount is not same");
//            softAssert.assertTrue(depositsDashboardPage.getDepositInterestRateField(getDepositNo()).getText().trim().equalsIgnoreCase(fileReader.fDTestData.get("interestRate")), "interest rate is not same");
            softAssert.assertTrue(depositsDashboardPage.getDepositMaturingAmountField(getDepositNo()).getText().matches(".*\\d.*"), "maturity amount is not displayed");
            softAssert.assertTrue(depositsDashboardPage.getDepositInterestRateField(getDepositNo()).getText().matches(".*\\d.*"), "interest rate  is not displayed");
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

    @When("User click on view button of newly opened deposit")
    public void userClickOnViewButtonOfNewlyOpenedFD() {
        waitTillInvisibilityOfLoader(driver);
        //     scrollIntoView(driver, depositsDashboardPage.getDepositViewButton(getDepositNo()));

        try {
            scrollIntoView(driver, depositsDashboardPage.getDepositViewButton(getDepositNo()));
            depositsDashboardPage.getDepositViewButton(getDepositNo()).click();
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Deposit number not showing in home page");
        }

        //      depositsDashboardPage.getDepositViewButton(getDepositNo()).click();
        waitTillInvisibilityOfLoader(driver);
        // attachScreenshot(driver,scenario);
    }

    @Then("User can validate the newly opened RD in the RD list")
    public void userCanValidateTheNewlyOpenedRDInTheRDList() {
        softAssert = new SoftAssert();
        try {
            if (depositsDashboardPage.listDepositError.getText().equals("Something went wrong, please try again later.")) {
                // attachScreenshot(driver,scenario);
                softAssert.assertFalse(depositsDashboardPage.listDepositError.getText().contains("Something went wrong, please try again later"), "Deposit list not displayed");

            } else {
                logger.info("Rd Number: " + getDepositNo());
                // System.out.println("Rd Number: " + getDepositNo());
                scrollIntoView(driver, depositsDashboardPage.getDepositTile(getDepositNo(), "RD"));
                // attachScreenshot(driver, scenario);
                logger.info(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText());
                // System.out.println(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText());
                softAssert.assertTrue(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.rDTestData.get("monthlyInstallment") + ".00"), "monthly installment is not same");
                softAssert.assertTrue(depositsDashboardPage.getDepositMaturingAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.rDTestData.get("maturityAmount")), "maturity amount is not same");
                softAssert.assertTrue(depositsDashboardPage.getDepositInterestRateField(getDepositNo()).getText().trim().equalsIgnoreCase(fileReader.rDTestData.get("interestRate")), "interest rate is not same");
            }
        } catch (NoSuchElementException exception) {
            // attachScreenshot(driver,scenario);
            //  logger.error("Deposit List not displayed");
            // System.out.println("Deposit List not displayed");

        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User can validate the newly opened TS in the TS list")
    public void userCanValidateTheNewlyOpenedTSInTheTSList() {
        softAssert = new SoftAssert();
        try {
            logger.info("TS Number: " + getDepositNo());
            scrollIntoView(driver, depositsDashboardPage.getDepositTile(getDepositNo(), "TS"));
            logger.info(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText());
            softAssert.assertTrue(depositsDashboardPage.getDepositAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.sOTTestData.get("amount") + ".00"), "amount is not same");
            //softAssert.assertTrue(depositsDashboardPage.getDepositMaturingAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").contains("2817.00"), "maturity amount is not same");
            //  softAssert.assertTrue(depositsDashboardPage.getDepositMaturingAmountField(getDepositNo()).getText().replace("₹", "").replace(",", "").equalsIgnoreCase(fileReader.sOTTestData.get("maturityAmount")), "maturity amount is not same");
            softAssert.assertTrue(depositsDashboardPage.getDepositMaturingAmountField(getDepositNo()).getText().contains("₹"), "maturity amount is not displayed");
            softAssert.assertTrue(depositsDashboardPage.getDepositInterestRateField(getDepositNo()).getText().matches(".*\\d.*"), "interest rate is not same");
//          softAssert.assertTrue(depositsDashboardPage.getDepositInterestRateField(getDepositNo()).getText().trim().equalsIgnoreCase(fileReader.sOTTestData.get("interestRate")), "interest rate is not same");
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Deposit dashboard list showing failed");

        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User can validate that {string} is no more visible in the deposit list")
    public void userCanValidateThatFdIsNoMoreVisibleInTheDepositList(String type) {
        try {
            depositsDashboardPage.getDepositTile(getDepositNo(), type);
            Assert.fail(type + ": " + getDepositNo() + " is still visible in the list");
        } catch (NoSuchElementException ignore) {
            // attachScreenshot(driver, scenario);
        }
    }


    @And("User verify terms and condition")
    public void userVerifyTermsAndCondition() {
        softAssert = new SoftAssert();
        scrollIntoView(driver, depositsDashboardPage.termsConditionLink);
        try {
            String currentWindow = driver.getWindowHandle();
            depositsDashboardPage.termsConditionLink.click();
            waitTillInvisibilityOfLoader(driver);
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(currentWindow)) {
                    driver.switchTo().window(windowHandle);
                    softAssert.assertTrue(driver.getCurrentUrl().contains("terms-and-conditions"), "terms nad conditions page not displayed");
                    break;
                }
            }
            driver.close();
            driver.switchTo().window(currentWindow);
        } catch (TimeoutException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Not clickable the terms and condition link");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify short menu functionality")
    public void userVerifyShortMenuFunctionality() {
        try {
            softAssert = new SoftAssert();
            scrollIntoView(driver, depositsDashboardPage.getDepositShortMenuPopUp(getDepositNo()));
            depositsDashboardPage.getDepositShortMenuPopUp(getDepositNo()).click();
            staticWait(2000);
            //       softAssert.assertTrue(depositsDashboardPage.shortcutMenuViewStatement.isDisplayed(), "viewStatement not displayed");
            //       softAssert.assertTrue(depositsDashboardPage.shortcutMenuDownloadAdvice.isDisplayed(), "download view statement option not displayed");
            softAssert.assertTrue(depositsDashboardPage.shortcutMenuCloseAccount.isDisplayed(), "close option not displayed");
            depositsDashboardPage.shortcutMenuCloseAccount.click();
            waitTillInvisibilityOfLoader(driver);
            if (driver.getCurrentUrl().contains("dashboard")) {
                softAssert.assertTrue(depositsDashboardPage.closeFDPopUp.isDisplayed(), "close deposit pop up not displayed");
                softAssert.assertTrue(depositsDashboardPage.closeFullFDPopUp.isDisplayed(), "fully close fd option not displayed");
                softAssert.assertTrue(depositsDashboardPage.closePartialFDPopUp.isDisplayed(), "partial fd close not displayed");
                depositsDashboardPage.closeCancelPopUp.click();
                waitTillInvisibilityOfLoader(driver);

            } else {
                scrollIntoViewUp(driver, depositsDashboardPage.rdCloseHeader);
                softAssert.assertTrue(depositsDashboardPage.rdCloseHeader.isDisplayed(), "rd close page header not displayed");
                //  driver.navigate().back();
                waitTillInvisibilityOfLoader(driver);
                depositsDashboardPage.backButton.click();
                waitTillInvisibilityOfLoader(driver);
            }
            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        } catch (NoSuchElementException e) {
            logger.info("Short Menu functionality removed");
        }

    }

    @And("User verify interest certificates popup")
    public void userVerifyInterestCertificatesPopup() {
        softAssert = new SoftAssert();
        boolean visible;
        try {
            if (depositsDashboardPage.interestCertificate.isDisplayed()) {
                visible = true;
            }
        } catch (NoSuchElementException element) {
            visible = false;
        }
        if (visible = true) {
            depositsDashboardPage.interestCertificate.click();
            staticWait(2000);
            softAssert.assertTrue(depositsDashboardPage.interestCertificatePopUpHeader.isDisplayed(), "interest certificate popup heading not displayed");
            logger.info(depositsDashboardPage.interestCertificateFinancialYear.size() + "years of Interest certificate available");
            // System.out.println(depositsDashboardPage.interestCertificateFinancialYear.size() +"years of Interest certificate available");
            depositsDashboardPage.interestCertificateSelectRadioButton.click();
            depositsDashboardPage.interestCertificateDownloadButton.click();
            waitTillInvisibilityElement(driver, depositsDashboardPage.downloadingInitiate);
            staticWait(3000);
            if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("pdf")) {
                File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.pdf");
                waitTillInvisibilityOfLoader(driver);
                softAssert.assertTrue(downloadedFile.exists(), "interest certificate download failed");
                waitTillInvisibilityOfLoader(driver);
                if (downloadedFile.exists()) {
                    downloadedFile.delete();
                }
//            depositsDashboardPage.interestCertificateCancelButton.click();
//            waitTillInvisibilityOfLoader(driver);
            } else if (visible = false) {
                attachScreenshot(driver, scenario);
                logger.error("Deposits list not displayed");
                //System.out.println("Deposits list not displayed");

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

    @And("User verify interest rates link")
    public void userVerifyInterestRatesLink() {
        softAssert = new SoftAssert();
        String currentWindow = driver.getWindowHandle();
        waitTillVisibilityElement(driver, depositsDashboardPage.interestRateLink);
        depositsDashboardPage.interestRateLink.click();
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

    @And("User verify the quick links section in deposit home page")
    public void userVerifyTheQuickLinksSectionInDepositHomePage() {
        softAssert = new SoftAssert();
        //  Detailed Statement Quick links
        scrollIntoViewUp(driver, depositsDashboardPage.detailsStatementQuick);
        staticWait(2000);
        javaScriptExecutorClickElement(driver, depositsDashboardPage.detailsStatementQuick);
        staticWait(3000);
        scrollIntoViewUp(driver, depositsDashboardPage.accountStatementPageHeader);

        //Verify the page url and header
        softAssert.assertTrue(driver.getCurrentUrl().contains("statement"), "account statement page url not be the same");
        softAssert.assertTrue(accountStatementPage.pageHeader.getText().contains("Account Statements"), "account statement page header not displayed");
        softAssert.assertTrue(accountStatementPage.filterButton.isDisplayed(), "filter option not displayed");
        softAssert.assertTrue(accountStatementPage.dateFilter.isDisplayed(), "date filter option not displayed");
        softAssert.assertTrue(accountStatementPage.getStatementButton.isDisplayed(), "get statement button not displayed");

        // Click side menu and return to Deposit dashboard page
        clickOnButton(homePage.depositAccountsButton);
        waitTillInvisibilityOfLoader(driver);

        // Money Transfer Quick Link

        scrollIntoViewUp(driver, depositsDashboardPage.moneyTransferPageHeader);
        waitTillElementToBeClickable(driver, depositsDashboardPage.moneyTransferQuickLink);
        javaScriptExecutorClickElement(driver, depositsDashboardPage.moneyTransferQuickLink);
        waitTillInvisibilityOfLoader(driver);

        // Verify the money transfer page
        softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "money transfer home page not displayed");
        softAssert.assertTrue(moneyTransferPage.moneyTransferPageHeader.isDisplayed(), "money transfer header not displayed");
        softAssert.assertTrue(moneyTransferPage.transferPayeeTab.isDisplayed(), "transfer to payee tab not displayed");
        softAssert.assertTrue(moneyTransferPage.quickTransferTab.isDisplayed(), "quick account transfer tab not displayed");
        softAssert.assertTrue(moneyTransferPage.multiplePaymentsTab.isDisplayed(), "multiple payments tab not displayed");

        //   waitTillElementToBeClickable(driver, homePage.depositAccountsButton);
        clickOnButton(homePage.depositAccountsButton);
        waitTillInvisibilityOfLoader(driver);

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the quick links section in deposit details page")
    public void userVerifyTheQuickLinksSectionInDepositDetailsPage() {
        softAssert = new SoftAssert();
        //  Detailed Statement Quick links
        scrollIntoViewUp(driver, depositsDashboardPage.detailsStatementQuick);
        staticWait(2000);
        javaScriptExecutorClickElement(driver, depositsDashboardPage.detailsStatementQuick);
        staticWait(3000);
        scrollIntoViewUp(driver, depositsDashboardPage.accountStatementPageHeader);

        //Verify the page url and header
        softAssert.assertTrue(driver.getCurrentUrl().contains("statement"), "account statement page url not be the same");
        softAssert.assertTrue(accountStatementPage.pageHeader.getText().contains("Account Statements"), "account statement page header not displayed");
        softAssert.assertTrue(accountStatementPage.filterButton.isDisplayed(), "filter option not displayed");
        softAssert.assertTrue(accountStatementPage.dateFilter.isDisplayed(), "date filter option not displayed");
        softAssert.assertTrue(accountStatementPage.getStatementButton.isDisplayed(), "get statement button not displayed");

        // Click side menu and return to Deposit dashboard page
        clickOnButton(homePage.depositAccountsButton);
        waitTillInvisibilityOfLoader(driver);

        System.out.println(getDepositNo());
        scrollIntoView(driver, depositsDashboardPage.getDepositViewButton(getDepositNo()));
        depositsDashboardPage.getDepositViewButton(getDepositNo()).click();
        // Money Transfer Quick Link

        scrollIntoViewUp(driver, depositsDashboardPage.moneyTransferPageHeader);
        javaScriptExecutorClickElement(driver, depositsDashboardPage.moneyTransferQuickLink);
        waitTillInvisibilityOfLoader(driver);

        // Verify the money transfer page
        softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "money transfer home page not displayed");
        softAssert.assertTrue(moneyTransferPage.moneyTransferPageHeader.isDisplayed(), "money transfer header not displayed");
        softAssert.assertTrue(moneyTransferPage.transferPayeeTab.isDisplayed(), "transfer to payee tab not displayed");
        softAssert.assertTrue(moneyTransferPage.quickTransferTab.isDisplayed(), "quick account transfer tab not displayed");
        softAssert.assertTrue(moneyTransferPage.multiplePaymentsTab.isDisplayed(), "multiple payments tab not displayed");

        //   waitTillElementToBeClickable(driver, homePage.depositAccountsButton);
        clickOnButton(homePage.depositAccountsButton);
        waitTillInvisibilityOfLoader(driver);

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the available deposits on deposit dashboard page")
    public void userVerifyTheAvailableDepositsOnDepositDashboardPage() {
        if (depositsDashboardPage.availabilityOfDepositsAccounts.getText().contains("No Deposit Account yet!")) {
            logger.info("There is no deposit account available");
        } else {
            softAssert = new SoftAssert();
            int totalNoOfDepositInDashboardPage = depositsDashboardPage.totalNoOfDepositList.size();
            int totalNoOfFdList = totalNoOfDepositInDashboardPage - depositsDashboardPage.totalNoOfRdList.size();
            int totalNoOFRdList = depositsDashboardPage.totalNoOfRdList.size() - depositsDashboardPage.totalNoOfTaxSaverList.size();
            int totalNoOFTsList = depositsDashboardPage.totalNoOfTaxSaverList.size();

            logger.info("Total no of deposit list in the dashboard page is " + totalNoOfDepositInDashboardPage);
            logger.info("Total no of FD deposit list in the dashboard page is " + totalNoOfFdList);
            logger.info("Total no of RD deposit list in the dashboard page is " + totalNoOFRdList);
            logger.info("Total no of TS deposit list in the dashboard page is " + totalNoOFTsList);

            //    if (totalNoOfDepositInDashboardPage > 0) {
            double totalDepositAmountInDashBoardPage = Double.parseDouble(depositsDashboardPage.totalDepositAmountInDepositDashboardPage.getText().replaceAll("₹", "").replaceAll(",", "").trim());
            double totalMaturityAmountInDashBoardPage = Double.parseDouble(depositsDashboardPage.totalMaturityAmount.getText().replaceAll("₹", "").replaceAll(",", "").trim());

            double depositAmountList = 0;
            double maturityTotalAmount = 0;

            for (WebElement depositList : depositsDashboardPage.depositAmountList) {
                double depositAmountIterate = Double.parseDouble(depositList.getText().replaceAll("₹", "").replaceAll(",", "").trim());
                depositAmountList += depositAmountIterate;
            }
            logger.info("Total Amount of deposit " + depositAmountList);

            for (WebElement maturityList : depositsDashboardPage.maturityAmountList) {
                double depositAmountIterate = Double.parseDouble(maturityList.getText().replaceAll("₹", "").replaceAll(",", "").trim());
                maturityTotalAmount += depositAmountIterate;
            }
            logger.info("Total Amount of maturity " + maturityTotalAmount);
            softAssert.assertEquals(totalDepositAmountInDashBoardPage, depositAmountList, "total deposit amount not matched");
            softAssert.assertEquals(totalMaturityAmountInDashBoardPage, maturityTotalAmount, "total maturity amount not matched");
        }
        //    }
        //    else{
        //        attachScreenshot(driver,scenario);
        //        logger.info("No deposits available available on dashboard page");
        //    }
    }

    @Then("User can obtain the opened {string} deposit number from the receipt page")
    public void userCanObtainTheOpenedDepositNumberFromTheReceiptPage(String deposit) {
        if (deposit.contains("fd")) {
            try {
                setDepositNo(fdPage.fdNumber.getText().split(":")[1].split("-")[0].trim());
            } catch (NoSuchElementException e) {
                attachScreenshot(driver, scenario);
                softAssert.fail("Please check the screen shot");
            }
            logger.info("FD Number" + getDepositNo());
        } else if (deposit.contains("rd")) {
            setDepositNo(depositsDashboardPage.rdNumber.getText().split(":")[1].split("-")[0].trim());
            logger.info("RD Number" + getDepositNo());
        }
    }

    @And("User clicks on try again button if error message appear")
    public void userClicksOnTryAgainButtonIfErrorMessageAppear() {
        try {
            if (depositsDashboardPage.errorPopUp.isDisplayed()) {
                int noOfTries = 2;
                for (int i = 1; i <= noOfTries; i++) {
                    clickOnButton(depositsDashboardPage.tryAgainButton);
                    waitTillVisibilityElement(driver, homePage.otpPopUpHeader);
                    homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
                    logger.error("Error pop up is appeared so user clicks on try again button");
                    staticWait(4000);
                    if (!depositsDashboardPage.errorPopUp.isDisplayed()) {
                        logger.info("The error pop up not displayed");
                        break;
                    }
                }
            }
        } catch (NoSuchElementException ignore) {
            logger.info("Error message not appeared");
        }
    }

    @And("User validates {string} limits messages")
    public void userValidatesLimitsMessages(String depositType) {
        waitTillVisibilityElement(driver,fdPage.depositAmount);
        String balance[] = fdPage.availableBalance.getText().split(":");
        double availableBalance = Double.parseDouble(balance[1].replaceAll(",", "").replace("₹", "").trim());
        logger.info("Available balance is Rs." + availableBalance);
        fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "99");
        if (depositType.equals("fd")) {
            softAssert.assertTrue(fdPage.depositAmountMessage.getText().contains("Amount can not be less than ₹1,000"), "minimum amount enter message not displayed");
        } else if (depositType.equals("rd")) {
            softAssert.assertTrue(fdPage.depositAmountMessage.getText().contains("Amount can not be less than ₹100"), "minimum amount enter message not displayed");
            if (availableBalance > 200000) {
                fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "200000");
                softAssert.assertTrue(fdPage.depositAmountMessage.getText().contains("Amount should be less than ₹1,00,000"), "maximum limit amount message not displayed");
            }
        } else if (depositType.equals("ts")) {
            softAssert.assertTrue(fdPage.depositAmountMessage.getText().contains("Amount should be between ₹1,000 and ₹1,50,000"), "minimum amount enter message not displayed");
            if (availableBalance > 200000) {
                fdPage.depositAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), "200000");
                softAssert.assertTrue(fdPage.depositAmountMessage.getText().contains("Amount should be between ₹1,000 and ₹1,50,000"), "maximum limit amount message not displayed");
            }
        }
        fdPage.depositAmount.clear();
    }

}

