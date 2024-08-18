package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.asserts.SoftAssert;
import pom.AccountSummaryPage;
import pom.HomePage;
import reusable.Base;
import reusable.TestContext;

import java.io.File;
import java.util.ListIterator;
import java.util.Random;

public class AccountSummaryStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(AccountSummaryStepDef.class);

    AccountSummaryPage accountSummaryPage;
    HomePage homePage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;

    public AccountSummaryStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        accountSummaryPage = context.getPageObjectManager().getAccountSummaryPage();
        homePage = context.getPageObjectManager().getHomePage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
    }

    @Then("User can verify the account summary page")
    public void userCanVerifyTheAccountSummaryPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(!accountSummaryPage.totalBalance.getText().isEmpty());
        softAssert.assertTrue(accountSummaryPage.totalBalance.getText().contains("XX"), "total balance in default not in hidden");
        accountSummaryPage.hiddenIconTotalBalance.click();
        softAssert.assertFalse(accountSummaryPage.totalBalance.getText().contains("XX"), "total balance valued not visible");
        if (!accountSummaryPage.savingAccountList.isEmpty()) {
            accountSummaryPage.getAccountHolder("Saving");
            softAssert.assertTrue(!accountSummaryPage.getAccountHolder("Saving").getText().isEmpty(), "saving accountHolder data is not visible");
            softAssert.assertTrue(!accountSummaryPage.getAccountNumber("Saving").getText().isEmpty(), "saving account number is not displayed");
            softAssert.assertTrue(!accountSummaryPage.getAvailableBalance("Saving").getText().isEmpty(), "saving account balance in not displayed");
            softAssert.assertTrue(accountSummaryPage.getViewButton("Saving").isDisplayed(), "saving account view button is not displayed");
        }
        softAssert.assertTrue(accountSummaryPage.detailsStatementQuickLink.isDisplayed(), "view and download button is not displayed");
        softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(), "transfer button is not displayed");
        waitForPageLoad(driver);
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on one of the view button of available accounts")
    public void userClicksOnOneOfTheViewButtonOfAvailableAccounts() {
        scrollIntoView(driver, accountSummaryPage.getViewButton("Savings Accounts"));
        accountSummaryPage.getViewButton("Savings Accounts").click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User will be navigated on the account details section")
    public void userWillBeNavigatedOnTheAccountDetailsSection() {
        scrollIntoViewUp(driver, accountSummaryPage.accountDetailsHeader);
        accountSummaryPage.accountDetailsHeader.isDisplayed();
        logger.info("account details title: " + driver.getTitle());
    }

    @And("User can verify the account details section")
    public void userCanVerifyTheAccountDetailsSection() {
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(accountSummaryPage.customerID.getText().length() != 0, "customer id is not displayed");
            softAssert.assertTrue(accountSummaryPage.accountHolderName.getText().length() != 0, "account holder name is not displayed");
            softAssert.assertTrue(accountSummaryPage.accountNo.getText().length() != 0, "account no is not displayed");
            softAssert.assertTrue(accountSummaryPage.accountType.getText().length() != 0, "account type is not displayed");
            softAssert.assertTrue(accountSummaryPage.ifscCode.getText().length() != 0, "ifsc code is not displayed");
            softAssert.assertTrue(accountSummaryPage.branchNameAndCity.getText().length() != 0, "branch name and city is not displayed");
            softAssert.assertTrue(accountSummaryPage.availableBalance.getText().length() != 0, "available balance is not displayed");
        } catch (NoSuchElementException elementNotFound) {
            attachScreenshot(driver, scenario);
            softAssert.fail(elementNotFound.toString());
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User verify recent transaction section")
    public void userVerifyRecentTransactionSection() {
        // accountSummaryPage.recentTransactionsTab.click();
        // attachScreenshot(driver,scenario);
        scrollIntoView(driver, accountSummaryPage.firstRowTransactionDate);
        if (accountSummaryPage.recentTransactionList.size() != 0) {
            scrollIntoView(driver, accountSummaryPage.firstRowTransactionDate);
            softAssert.assertTrue(accountSummaryPage.firstRowTransactionDate.getText().length() != 0, "transaction date is not displayed");
            softAssert.assertTrue(accountSummaryPage.firstRowTransactionDescription.getText().length() != 0, "transaction description is not displayed");
            softAssert.assertTrue(accountSummaryPage.firstRowTransactionAmount.getText().length() != 0, "transaction amount is not displayed");
            softAssert.assertTrue(accountSummaryPage.firstRowTransactionBalance.getText().length() != 0, "transaction balance is not displayed");
//Popup
            accountSummaryPage.transactionFirstRow.click();
            softAssert.assertTrue(accountSummaryPage.transactionPopUpAmount.getText().replaceAll("[^\\d.]", "").trim().contains(accountSummaryPage.firstRowTransactionAmount.getText().replaceAll("[^\\d.]", "").trim()), "pop transaction amount not matched");
            softAssert.assertTrue(accountSummaryPage.transactionPopupDate.getText().contains(accountSummaryPage.firstRowTransactionDate.getText()), "popup transaction date not matched");
            softAssert.assertTrue(accountSummaryPage.transactionPopupDescription.getText().contains(accountSummaryPage.firstRowTransactionDescription.getText()), "popup transaction description not matched");

            try {
                accountSummaryPage.transactionPopupCopyButton.click();
                softAssert.assertTrue(accountSummaryPage.transactionPopupCopiedMessage.getText().contains("opied"), "copies successfully message not displayed");
                softAssert.assertTrue(accountSummaryPage.chequeNumberPopup.getText().trim().contains(getCopiedValue().trim()));
            } catch (NoSuchElementException exception) {
            }
            accountSummaryPage.transactionPopupDownload.click();
            File transactionPdfFile = new File("C:/Users/987993/Downloads/TxnRpt_" + getCurrentDateTime() + ".pdf");
            staticWait(3000);
            softAssert.assertTrue(transactionPdfFile.exists(), "Transaction loan Statement download failed");
            waitTillInvisibilityOfLoader(driver);
            if (transactionPdfFile.exists()) {
                transactionPdfFile.delete();
            }
            accountSummaryPage.transactionPopupCancel.click();
            waitForPageLoad(driver);
            //attachScreenshot(driver, scenario);
            scrollIntoViewUp(driver, accountSummaryPage.detailsStatementQuick);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks on 'edit nick name' icon")
    public void userClicksOnEditNickNameIcon() {
        scrollIntoViewUp(driver, accountSummaryPage.editNickName);
        accountSummaryPage.editNickName.click();
    }

    @Then("User can see 'update nick name' popup is opened")
    public void userCanSeeUpdateNickNamePopupIsOpened() {
        accountSummaryPage.updateNickNamePopup.isDisplayed();
        //attachScreenshot(driver, scenario);
    }

    @And("User successfully able to update the nick name")
    public void userSuccessfullyAbleToUpdateTheNickName() {
        accountSummaryPage.nickNameEditBox.sendKeys("har" + new Random().nextInt(10));
        accountSummaryPage.updateNickNameProceedButton.click();
        accountSummaryPage.updateNickNameSuccessfulMessage.isDisplayed();
        waitTillInvisibilityElement(driver, accountSummaryPage.updateNickNameSuccessfulMessage);
        //  attachScreenshot(driver,scenario);
    }

    @When("User clicks on 'detailed statement' button on account details section")
    public void userClicksOnDetailedStatementButtonOnAccountDetailsSection() {
        scrollIntoViewUp(driver, accountSummaryPage.detailsStatementQuick);
        staticWait(2000);
        javaScriptExecutorClickElement(driver, accountSummaryPage.detailsStatementQuick);
        staticWait(3000);
        scrollIntoViewUp(driver, accountSummaryPage.accountStatementPageHeader);

        //   waitTillInvisibilityOfLoader(driver);
    }


    @And("User clicks on 'money transfer' button on account details section")
    public void userClicksOnMoneyTransferButtonOnAccountDetailsSection() {
        scrollIntoViewUp(driver, accountSummaryPage.moneyTransferQuickLink);
        //waitTillElementToBeClickable(driver,accountSummaryPage.moneyTransferButton);
        javaScriptExecutorClickElement(driver, accountSummaryPage.moneyTransferQuickLink);
        //  staticWait(2000);
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User will be navigated on the money transfer page")
    public void userWillBeNavigatedOnTheMoneyTransferPage() {
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "navigates to money transfer failed");
            softAssert.assertTrue(accountSummaryPage.moneyTransferPageHeader.isDisplayed(), "money transfer header not displayed");
            // attachScreenshot(driver,scenario);
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Money Transfer header no displayed");
        }
        driver.navigate().back();
        waitForPageLoad(driver);
        //   waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User verify additional details section")
    public void userVerifyAdditionalDetailsSection() {
        accountSummaryPage.additionalDetailsSection.click();
        waitTillInvisibilityOfLoader(driver);

    }

    @When("User clicks nominee edit in additional details section")
    public void userClicksNomineeEditInAdditionalDetailsSection() {
        accountSummaryPage.additionalDetailsEditNickName.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify nominee update page and enter all details all details")
    public void userVerifyNomineeUpdatePageAndEnterAllDetailsAllDetails() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(accountSummaryPage.additionalDetailsUpdateNomineePage.getText().contains("Update"), "update nominee page not displayed");
        softAssert.assertTrue(accountSummaryPage.updateNomineePageCreateNominee.isDisplayed(), "create nominee option not displayed");
        accountSummaryPage.updateNomineePageCreateNominee.click();
        waitTillInvisibilityOfLoader(driver);
        accountSummaryPage.createNomineePageCloseButton.click();
        staticWait(3000);
        accountSummaryPage.createNomineeNamePageLeavePopupButton.click();
        waitTillInvisibilityOfLoader(driver);

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User verify update nominee details in additional details section")
    public void userVerifyUpdateNomineeDetailsInAdditionalDetailsSection() {

    }

    @And("User verify average monthly balance section")
    public void userVerifyAverageMonthlyBalanceSection() {
        softAssert = new SoftAssert();
        try {
            if (fileReader.accStatementTestData.get("getStatementTransaction type").contains("Saving")) {
                //if(accountSummaryPage.getViewButton("Saving Accounts").getText().contains("")){
                accountSummaryPage.averageMonthBalanceTab.click();
                softAssert.assertTrue(accountSummaryPage.averageMonthBalanceTitle.getText().contains("Average Monthly Balance"), "account monthly balance heading not matched");
                //softAssert.assertTrue(accountSummaryPage.averageMonthBalanceBar.isDisplayed(),"monthly balance bar not displayed");
                softAssert.assertTrue(accountSummaryPage.averageQtrBalanceBar.isDisplayed(), "quarter balance bar not displayed");

            }
        } catch (NoSuchElementException e) {
            System.out.println("Average Monthly Balance not displayed");
        }

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks on quick link debit card and verify the page")
    public void userClicksOnQuickLinkDebitCardAndVerifyThePage() {
        softAssert = new SoftAssert();
        boolean isDebitCardAvailable = false;
        if (accountSummaryPage.debitCardList.size() != 0) {
            isDebitCardAvailable = true;
            logger.info("The user has "+accountSummaryPage.debitCardList.size()+" debit card");
        }
        else{
            logger.info("A debit card is not present in the user's possession");

        }
        if (isDebitCardAvailable) {
            scrollIntoView(driver, accountSummaryPage.debitCardQuickLink);
            String accountNumberLink = accountSummaryPage.accountNo.getText();
            accountSummaryPage.debitCardQuickLink.click();
            waitTillInvisibilityOfLoader(driver);
            softAssert.assertTrue(driver.getCurrentUrl().contains("debit-card"), "debit card home page is not displayed");
            softAssert.assertTrue(accountSummaryPage.debiCardPageHeader.getText().contains("Debit Card Management"), "debit card page header is not displayed");
            softAssert.assertTrue(accountSummaryPage.debiCardLinkedAccountNumber.getText().contains(accountNumberLink), "linked account not be same");
            driver.navigate().back();
            waitTillInvisibilityOfLoader(driver);
        }

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }
//Cash Credit and Over draft Accounts

    @Then("User can verify {string} in account summary page")
    public void userCanVerifyInAccountSummaryPage(String accountType) {
        softAssert = new SoftAssert();
        softAssert.assertTrue(accountSummaryPage.totalBalance.getText().length() != 0);
        softAssert.assertTrue(accountSummaryPage.totalBalance.getText().contains("XX"), "total balance in default not in hidden");
        accountSummaryPage.hiddenIconTotalBalance.click();
        softAssert.assertFalse(accountSummaryPage.totalBalance.getText().contains("XX"), "total balance valued not visible");


        //  if(accountSummaryPage.overDraftAccList.size()!=0){
        scrollIntoView(driver, accountSummaryPage.getAccountHolder(accountType));
        softAssert.assertTrue(accountSummaryPage.getAccountHolder(accountType).getText().length() != 0, "current accountHolder data is not visible");
        softAssert.assertTrue(accountSummaryPage.getAccountNumber(accountType).getText().length() != 0, "current account number is not displayed");
        softAssert.assertTrue(accountSummaryPage.getAvailableBalance(accountType).getText().length() != 0, "current account balance in not displayed");
        softAssert.assertTrue(accountSummaryPage.getAvailableBalance(accountType).getText().contains("XX"), "current account balance in default not hidden");
        accountSummaryPage.hiddenIconAvailableBalance(accountType).click();
        softAssert.assertFalse(accountSummaryPage.getAvailableBalance(accountType).getText().contains("XX"), "current account balance in not visible");
        softAssert.assertTrue(accountSummaryPage.getViewButton(accountType).isDisplayed(), "current account view button is not displayed");
        //   }
        softAssert.assertTrue(accountSummaryPage.detailsStatementQuickLink.isDisplayed(), "view and download button is not displayed");
        softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(), "transfer button is not displayed");
        //attachScreenshot(driver,scenario);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify the quick links section")
    public void userVerifyTheQuickLinksSection() {
        softAssert = new SoftAssert();
        try {
            scrollIntoView(driver, accountSummaryPage.detailsStatementQuick);
            waitTillElementToBeClickable(driver, accountSummaryPage.detailsStatementQuickLink);
            javaScriptExecutorClickElement(driver, accountSummaryPage.detailsStatementQuick);
            waitTillVisibilityElement(driver, accountSummaryPage.accountStatementPageHeader);
            try {
                softAssert.assertTrue(driver.getCurrentUrl().contains("statement"), "account statement page url not be the same");
                softAssert.assertTrue(accountSummaryPage.accountStatementPageHeader.isDisplayed(), "account statement page header not displayed");
                driver.navigate().back();
            } catch (NoSuchElementException e) {
                attachScreenshot(driver, scenario);
                softAssert.fail("Account statement header not displayed");
                driver.navigate().back();
            }
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Quick Account statement tab not displayed");
        }

        try {
            waitTillElementToBeClickable(driver, accountSummaryPage.moneyTransferQuickLink);
            javaScriptExecutorClickElement(driver, accountSummaryPage.moneyTransferQuickLink);
            waitTillInvisibilityOfLoader(driver);
            try {
                softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "navigates to money transfer failed");
                softAssert.assertTrue(accountSummaryPage.moneyTransferPageHeader.isDisplayed(), "money transfer header not displayed");
                driver.navigate().back();
                waitTillInvisibilityOfLoader(driver);
                // attachScreenshot(driver,scenario);
            } catch (NoSuchElementException e) {
                attachScreenshot(driver, scenario);
                softAssert.fail("Money Transfer header no displayed");
                driver.navigate().back();
                waitTillInvisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Quick section Money transfer tab not displayed");
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on detailed statement in quick links on accounts details page")
    public void userClicksOnDetailedStatementInQuickLinksOnAccountsDetailsPage() {
        scrollIntoViewUp(driver, accountSummaryPage.detailsStatementQuick);
        javaScriptExecutorClickElement(driver, accountSummaryPage.detailsStatementQuick);
        waitTillInvisibilityOfLoader(driver);
    }

    @When("User clicks on the money transfer in quick links section on account details page")
    public void userClicksOnTheMoneyTransferInQuickLinksSectionOnAccountDetailsPage() {
        //From Account statement page to Accounts summary home page
        clickOnButton(homePage.operativeAccountButton);
        waitTillInvisibilityOfLoader(driver);
        //From Account summary home page to Accounts details page
        userClicksOnOneOfTheViewButtonOfAvailableAccounts();
        // Clicks on money transfer quick links
        scrollIntoViewUp(driver, accountSummaryPage.moneyTransferPageHeader);
        javaScriptExecutorClickElement(driver, accountSummaryPage.moneyTransferQuickLink);
        waitTillInvisibilityOfLoader(driver);
    }


    @And("User verify copy details option")
    public void userVerifyCopyDetailsOption() {
        softAssert = new SoftAssert();
        String accountName = null;
        String accountType = null;
        String accountNumber = null;
        String ifscCode = null;
        String branchName = null;
        accountSummaryPage.copyDetailsButton.click();
        String copiedDetails[] = getCopiedValue().split("\n");
        for (int i = 0; i <= 4; i++) {
            String copiedDetailsByNewLine = copiedDetails[i];
            String copiedValuesSplit[] = copiedDetailsByNewLine.split(":");
            String splitKey = copiedValuesSplit[0].trim();
            String splitValue = copiedValuesSplit[1].trim();
            if (copiedValuesSplit[0].contains("Account Holder Name")) {
                accountName = splitValue;
            } else if (copiedValuesSplit[0].contains("Account Type")) {
                accountType = splitValue;
            } else if (copiedValuesSplit[0].contains("Account Number")) {
                accountNumber = splitValue;
            } else if (copiedValuesSplit[0].contains("IFSC Code")) {
                ifscCode = splitValue;
            } else if (copiedValuesSplit[0].contains("Branch Name")) {
                branchName = splitValue;
            }
        }
        ListIterator<WebElement> keyNameOption = accountSummaryPage.accountDetailsSectionListKey.listIterator();
        while (keyNameOption.hasNext()) {
            WebElement keyOption = keyNameOption.next();
            String keyNameHeader = keyOption.getText();
            String valueNameHeader = accountSummaryPage.accountDetailsSectionListValue(keyNameHeader).getText();

            switch (keyNameHeader) {
                case "Account Holder Name":
                    softAssert.assertEquals(valueNameHeader.replaceAll("\\s+", ""), accountName.replaceAll("\\s+", ""), "copied " + keyNameHeader + " value not matched");
                    logger.info(valueNameHeader);
                    logger.info(accountName);
                    break;
                case "Account Type":
                    softAssert.assertEquals(valueNameHeader.trim(), accountType.trim(), "copied " + keyNameHeader + " value not matched");
                    break;
                case "Account Number":
                    softAssert.assertEquals(valueNameHeader.trim(), accountNumber.trim(), "copied " + keyNameHeader + " value not matched");
                    break;
                case "IFSC Code":
                    softAssert.assertEquals(valueNameHeader.trim(), ifscCode.trim(), "copied " + keyNameHeader + " value not matched");
                    break;
                case "Branch Name":
                    softAssert.assertEquals(valueNameHeader.trim(), branchName.trim(), "copied " + keyNameHeader + " value not matched");
                    break;
            }
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User verify available balance tool tip message on accounts details page")
    public void userVerifyAvailableBalanceToolTipMessageOnAccountsDetailsPage() {
        //User move the cursor to tool tip message
        softAssert = new SoftAssert();
        moveToElement(driver, accountSummaryPage.availableBalanceToolTip);
        //      softAssert.assertTrue(accountSummaryPage.availableBalanceToolTip.getText().length() != 0, "tool tip message not displayed");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("Use clicks on available balance drop down section on accounts details page")
    public void useClicksOnAvailableBalanceDropDownSectionOnAccountsDetailsPage() {
        accountSummaryPage.availableBalanceDropDownIcon.click();
        waitForPageLoad(driver);
    }

    @And("User verify available balance drop down section on accounts details page")
    public void userVerifyAvailableBalanceDropDownSectionOnAccountsDetailsPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(accountSummaryPage.availableBalanceDropDownSection.getText().matches(".*\\d.*"), "available balance is not showing");
        softAssert.assertTrue(accountSummaryPage.unclearBalanceDropDownSection.getText().matches(".*\\d.*"), "unclear balance is not showing");
        softAssert.assertTrue(accountSummaryPage.onHoldBalanceDropDownSection.getText().matches(".*\\d.*"), "on hold balance is not showing");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }


    }

}

