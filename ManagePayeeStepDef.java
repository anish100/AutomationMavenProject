package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.K;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pom.AccountSummaryPage;
import pom.HomePage;
import pom.ManagePayeePage;
import reusable.Base;
import reusable.TestContext;

import java.time.Duration;
import java.util.ListIterator;


public class ManagePayeeStepDef extends Base {

    private static Logger logger = LogManager.getLogger(ManagePayeeStepDef.class);
    ManagePayeePage managePayeePage;
    HomePage homePage;
    AccountSummaryPage accountSummaryPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;

    private String payeeName;
    private String payeeAccountNumber;
    private String payeeBankName;
    private String payeeBankIfscCode;
    private String payeeBankAccount;
    boolean isNeedBankDetailsEnterManually;

    private String payeeEmailId;


    public ManagePayeeStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        managePayeePage = context.getPageObjectManager().getManagePayeePage();
        accountSummaryPage = context.getPageObjectManager().getAccountSummaryPage();
        homePage = context.getPageObjectManager().getHomePage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
    }

    @Then("User can verify the payees page")
    public void userCanVerifyThePayeesPage() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, managePayeePage.managePayeePageHead);
        softAssert.assertTrue(managePayeePage.managePayeePageHead.getText().contains("Manage Payee"), "payee page header not visible");
        try {
            softAssert.assertTrue(managePayeePage.managePayeeTableContents.isDisplayed(), "payee table not visible");
            softAssert.assertTrue(managePayeePage.payeeNameList.size() != 0, "payee name list not visible");
            softAssert.assertTrue(managePayeePage.contactDetailsList.size() != 0, "payee contact list not visible");
            if (managePayeePage.payButton.isEnabled()) {
                softAssert.assertFalse(managePayeePage.bankAccountList.getText().contains("Account Not Added"), "Account details not added");
            }
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            logger.info("No Payee In the List");
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks add new payee button")
    public void userClicksAddNewPayeeButton() {
        waitTillVisibilityElement(driver, managePayeePage.addNewPayeeButton);
        managePayeePage.addNewPayeeButton.click();
        waitTillVisibilityElement(driver, managePayeePage.addNewPayeeTittle);
    }

    @And("User can verify add new payee page")
    public void userCanVerifyAddNewPayeePage() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, managePayeePage.addNewPayeeTittle);
        softAssert.assertTrue(managePayeePage.addNewPayeeTittle.getText().contains("Add New Payee"), "add new payee page not visible");
        softAssert.assertTrue(managePayeePage.payeeDetails.isDisplayed(), "payee details not visible");
        softAssert.assertTrue(managePayeePage.addPayeeName.isDisplayed(),"payee name/ nick name text box is not displayed");
        softAssert.assertTrue(managePayeePage.addPayeeMobileNumber.isDisplayed(),"mobile number text box is not displayed");
        softAssert.assertTrue(managePayeePage.addPayeeEmailId.isDisplayed(),"email id text box is not displayed");
        softAssert.assertTrue(managePayeePage.quickLinksAddPayeePage.isDisplayed(), "quick links header not displayed");
        softAssert.assertTrue(accountSummaryPage.detailsStatementQuick.isDisplayed(), "quick link money transfer not displayed");
        softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(), "quick links transaction section not displayed");
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User enters invalid details and verify the error message")
    public void userEntersInvalidDetailsAndVerifyTheErrorMessage() {
        scrollIntoViewUp(driver,managePayeePage.pageHeader);
        managePayeePage.addPayeeName.sendKeys("Re");
        //addNewPayeeName=managePayeePage.addPayeeName.getAttribute("value");
        managePayeePage.addPayeeMobileNumber.sendKeys("81489");
        managePayeePage.addPayeeEmailId.sendKeys("sougmaicom");
        softAssert = new SoftAssert();
        softAssert.assertTrue(managePayeePage.addPayeeNameErrorMsg.isDisplayed(), "Name error message not display");
        softAssert.assertTrue(managePayeePage.addPayeeMobileNumberErrorMsg.isDisplayed(), "Mobile number error message not display");
        softAssert.assertTrue(managePayeePage.addPayeeEmailIdErrorMsg.isDisplayed(), "Email id error message not display");
        try {
            softAssert.assertAll();
            //attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User enters all needed details on add new payee page")
    public void userEntersAllNeededDetailsOnAddNewPayeePage() {
        managePayeePage.addPayeeName.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.managePayeeTestData.get("payeeName"));
        managePayeePage.addPayeeMobileNumber.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.managePayeeTestData.get("mobileNumber"));
        managePayeePage.addPayeeEmailId.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.managePayeeTestData.get("emailId"));
        try {
            if (managePayeePage.maximumAddPayeeReached.isDisplayed()) {
                attachScreenshot(driver, scenario);
                logger.info("Unable to add the payee because the maximum number of payees added has been reached.");
                Assert.fail("Unable to add the payee,Maximum number of payees added has been reached ");
            }
        } catch (NoSuchElementException e) {

        }
        managePayeePage.addPayeeBankDetails.click();
        waitTillElementToBeClickable(driver, managePayeePage.bankNameSearchBarInSelectPayeePage);
        //staticWait(3000);
        try {
            logger.info(fileReader.managePayeeTestData.get("bankName") + " is Selected from the list");
            managePayeePage.selectBankFromList(fileReader.managePayeeTestData.get("bankName")).click();
            if (managePayeePage.getBankName.getText().equalsIgnoreCase("AU SMALL FINANCE BANK LIMITED")) {
                waitTillInvisibilityOfLoader(driver);
                //Choose By Account number or Mobile Number from Excel sheet

                if (fileReader.managePayeeTestData.get("addBankByType").equalsIgnoreCase("Account Number")) {
                    logger.info("The user's selected bank type is the Account number");
                    managePayeePage.selectRadioButtonAUBank(fileReader.managePayeeTestData.get("addBankByType")).click();
                    managePayeePage.enterAccountNumberAU.sendKeys(fileReader.managePayeeTestData.get("accountNumber"), Keys.TAB);
                    logger.info("The entered bank account number is:" + fileReader.managePayeeTestData.get("accountNumber"));
                    waitTillInvisibilityOfLoader(driver);
                    //    managePayeePage.auAccountNextButton.click();
                    //   waitTillInvisibilityOfLoader(driver);
                } else if (fileReader.managePayeeTestData.get("addBankByType").equalsIgnoreCase("Mobile number")) {
                    logger.info("The user's selected bank type is the Mobile number");
                    managePayeePage.selectRadioButtonAUBank(fileReader.managePayeeTestData.get("addBankByType")).click();
                    managePayeePage.enterMobileNumber.sendKeys(fileReader.managePayeeTestData.get("auAccMobileNumber"), Keys.TAB);
                    logger.info("The entered bank account number is:" + fileReader.managePayeeTestData.get("auAccMobileNumber"));
                    //   managePayeePage.auAccountNextButton.click();
                    //  waitTillInvisibilityOfLoader(driver);
                }
            } else {
                logger.info("The entered bank account number is:" + fileReader.managePayeeTestData.get("otherBankAccountNumber"));
                managePayeePage.addAccountNumber.sendKeys(fileReader.managePayeeTestData.get("otherBankAccountNumber"), Keys.TAB);
                waitForPageLoad(driver);
                managePayeePage.selectAccountType.click();
                managePayeePage.selectAccountTypeList(fileReader.managePayeeTestData.get("otherBankAccountType"));
                logger.info("The selected bank account type is:" + fileReader.managePayeeTestData.get("otherBankAccountType"));
                waitForPageLoad(driver);
                if (managePayeePage.ifscRadioButton.isDisplayed()) {
                    managePayeePage.selectIfscBranch(fileReader.managePayeeTestData.get("addOtherBankByType")).click();
                    logger.info("The selected other bank type is:" + fileReader.managePayeeTestData.get("addOtherBankByType"));
                    waitForPageLoad(driver);

                    if (fileReader.managePayeeTestData.get("addOtherBankByType").equalsIgnoreCase("IFSC")) {
                        managePayeePage.enterIfscCode.sendKeys(fileReader.managePayeeTestData.get("ifscCode"), Keys.TAB);
                        waitTillInvisibilityOfLoader(driver);
                        //  managePayeePage.addverifyButton.click();
                        // waitTillInvisibilityOfLoader(driver);

                    } else { //if (managePayeePage.branchNameRadioButton.isDisplayed()) {
                        managePayeePage.enterBranchName.sendKeys(fileReader.managePayeeTestData.get("branchName"), Keys.TAB);
                        waitForPageLoad(driver);
                        managePayeePage.addverifyButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        String[] ifscCode = managePayeePage.getPayeeBankIfscCode.getText().replace("[^\\d]", "").split(":");
                        payeeBankIfscCode = ifscCode[1].trim();
                        //  managePayeePage.addPayeeNextButton.click();
                        //waitTillInvisibilityOfLoader(driver);

                    }
                }
            }
        } catch (NoSuchElementException e) {
            logger.error("Some Elements are not found please check x paths");
            //  System.out.println("Elements are in the method not visible");
        }
        managePayeePage.addverifyButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User add bank account by enter all details manually")
    public void userAddBankAccountByEnterAllDetailsManually() {
        try {
            if (managePayeePage.enterManuallyBankDetails.isDisplayed()) {
                logger.info("The user need to entered bank details manually");
                managePayeePage.addAccountHolderName.sendKeys(Keys.chord(Keys.CONTROL,"a"),fileReader.managePayeeTestData.get("payeeName"));
                managePayeePage.addAccountNumber.sendKeys(Keys.chord(Keys.CONTROL,"a"),fileReader.managePayeeTestData.get("otherBankAccountNumber"));
                managePayeePage.reEnterAccountNumber.sendKeys(Keys.chord(Keys.CONTROL,"a"),fileReader.managePayeeTestData.get("otherBankAccountNumber"));
                managePayeePage.selectAccountType.click();
                // managePayeePage.selectAccountTypeList(fileReader.managePayeeTestData.get("otherBankAccountType"));
                /**Here choose savings by index**/
                managePayeePage.selectAccountTypeListByIndex.click();
                waitForPageLoad(driver);
//                if (fileReader.managePayeeTestData.get("addOtherBankByType").equalsIgnoreCase("IFSC")) {
//                    managePayeePage.enterIfscCode.sendKeys(fileReader.managePayeeTestData.get("ifscCode"), Keys.TAB);
//                    waitTillInvisibilityOfLoader(driver);
//
//                } else { //if (managePayeePage.branchNameRadioButton.isDisplayed()) {
//                    managePayeePage.enterBranchName.sendKeys(fileReader.managePayeeTestData.get("branchName"), Keys.TAB);
//                    // managePayeePage.enterBranchName.sendKeys("RTGS-HO", Keys.TAB);
//                    waitForPageLoad(driver);
//
//                }
                if (managePayeePage.addAccountManuallyButton.isDisplayed()) {
                    managePayeePage.addAccountManuallyButton.click();
                    waitTillInvisibilityOfLoader(driver);
                }
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            logger.info("Bank details are added; there is no need to enter the details manually");
            System.out.println("Bank details added");
        }
    }

    @Then("User verify the bank details section after adding bank details")
    public void userVerifyTheBankDetailsSectionAfterAddingBankDetails() {
        fluentWaitTillVisibilityElement(driver,10,1,managePayeePage.addPayeeAccountName);
        scrollIntoView(driver, managePayeePage.addPayeeAccountName);
        // logger.info("Payee Name :"+managePayeePage.addPayeeAccountName.getText());
        setPayeeName(managePayeePage.addPayeeAccountName.getText());
        payeeBankAccount = managePayeePage.getPayeeAccountNumber.getText().replaceAll("[^\\d]", "").trim();
        setPayeeAccountNumber(payeeBankAccount);
        payeeBankName = managePayeePage.getPayeeBankName.getText();
        setPayeeBankName(payeeBankName);
        //  setPayeeMobileNumber("8148992911");
        // setPayeeMailId("xyz@gmail.com");
        logger.info("Payee Name :" + managePayeePage.addPayeeAccountName.getText());
        logger.info("Payee Bank Account number is :" + payeeBankAccount);
        logger.info("Payee Bank Name is :" + payeeBankName);


    }

    @When("User clicks next button")
    public void userClicksNextButton() {
        scrollIntoView(driver, managePayeePage.addPayeeNextButton);
        managePayeePage.addPayeeNextButton.click();
        waitTillInvisibilityOfLoader(driver);

    }

    @Then("User verify new payee added successfully")
    public void userVerifyNewPayeeAddedSuccessfully() {
        softAssert = new SoftAssert();
        try {    // if(managePayeePage.addedPayeeSuccessfully.isDisplayed()){
            softAssert.assertTrue(managePayeePage.addedPayeeSuccessfully.getText().contains("uccessfully"), "payee added successfully not displayed");
            /** Copy the Reference number ***/
            softAssert.assertTrue(managePayeePage.addedPayeeNameVerify.getText().contains(fileReader.managePayeeTestData.get("payeeName")), "added payee name not matched");
            logger.info("Payee newly added successfully");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            softAssert.fail("payee added successfully pop up not displayed");

        }
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks back to manage payee button")
    public void userClicksBackToManagePayeeButton() {
        managePayeePage.backToManagePayee.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User verify newly added payee in payment page list")
    public void userVerifyNewlyAddedPayeeInPaymentPageList() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, managePayeePage.pageHeader);
        softAssert.assertEquals(managePayeePage.accountPayeeNameInTable.getText().trim(),getPayeeName().trim(), "payee name not matched");
        softAssert.assertTrue(managePayeePage.payeeEmailInTable.getText().equalsIgnoreCase(fileReader.managePayeeTestData.get("emailId")), "payee email not matched");
        softAssert.assertTrue(managePayeePage.payeeMobileNumberInTable.getText().contains(fileReader.managePayeeTestData.get("mobileNumber")), "payee mobile number not matched");
        softAssert.assertTrue(managePayeePage.payeebankNameInTable.getText().equalsIgnoreCase(fileReader.managePayeeTestData.get("bankName")), "payee bank name not matched");
        logger.info("User verified newly added payee details in the payee list section");
        try {
            softAssert.assertAll();
            //  attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User enters payee details in search box")
    public void userEntersPayeeDetailsInSearchBox() {
        managePayeePage.payeePageSearchBox.sendKeys(fileReader.managePayeeTestData.get("payeeName"));
        staticWait(3000);
    }

    @Then("User verify the payment page list by payee details in search box")
    public void userVerifyThePaymentPageListByPayeeDetailsInSearchBox() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(managePayeePage.payeeNameInTable.getText().contains(fileReader.managePayeeTestData.get("payeeName")), "payee nick name not matched");
        softAssert.assertTrue(managePayeePage.accountPayeeNameInTable.getText().contains(getPayeeName()), "payee bank account name not matched");
       logger.info(managePayeePage.accountPayeeNameInTable.getText());
       logger.info(getPayeeName());
        softAssert.assertTrue(managePayeePage.payeeMobileNumberInTable.getText().contains(fileReader.managePayeeTestData.get("mobileNumber")), "payee mobile number not matched");
        softAssert.assertTrue(managePayeePage.payeeEmailInTable.getText().contains(fileReader.managePayeeTestData.get("emailId")), "payee email id not matched");
        softAssert.assertTrue(managePayeePage.payeebankNameInTable.getText().contains(payeeBankName), "payee bank name not matched");
        managePayeePage.payeePageSearchBox.clear();
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
            //attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks view button in payment page section")
    public void userClicksViewButtonInPaymentPageSection() {
        managePayeePage.paymentPageViewButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the payee details page")
    public void userVerifyThePayeeDetailsPage() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, managePayeePage.pageHeader);
        softAssert.assertTrue(managePayeePage.pageHeader.getText().contains("Payee Details"), "payee details header name is not identical");
        softAssert.assertTrue(managePayeePage.backButton.isDisplayed(),"back button is not displayed");
        softAssert.assertTrue(managePayeePage.payeeDetailsPayeeNickName.getText().contains(fileReader.managePayeeTestData.get("payeeName")), "payee nick name is not identical");
        softAssert.assertTrue(managePayeePage.payeeDetailsBankName.getText().equalsIgnoreCase(fileReader.managePayeeTestData.get("bankName")), "payee bank name not matched");
        softAssert.assertTrue(managePayeePage.payeeDetailsPageMobileNumber.getText().contains(fileReader.managePayeeTestData.get("mobileNumber")), "payee mobile number not matched");
        softAssert.assertTrue(managePayeePage.payeeDetailsPageEmail.getText().contains(fileReader.managePayeeTestData.get("emailId")), "payee mail id not matched");
        softAssert.assertTrue(managePayeePage.makePaymentButton.isDisplayed(),"make payment button is not displayed");
        softAssert.assertTrue(managePayeePage.transactionTabOnPayeeDetailsPage.isDisplayed(),"transaction tab is not displayed");
        softAssert.assertTrue(managePayeePage.otherDetailsTabOnPayeeDetailsPage.isDisplayed(),"others details tab is not displayed");
        softAssert.assertTrue(accountSummaryPage.detailsStatementQuick.isDisplayed(),"detailed statement in the quick links is not displayed");
        softAssert.assertTrue(accountSummaryPage.moneyTransferQuickLink.isDisplayed(),"money transfer in the quick links is not displayed");

        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on make payment button")
    public void userClicksOnMakePaymentButton() {
        waitTillElementToBeClickable(driver, managePayeePage.makePaymentButtonFromViewPage);
        managePayeePage.makePaymentButtonFromViewPage.click();
        waitTillInvisibilityOfLoader(driver);

    }

    @Then("User verify the payment page")
    public void userVerifyThePaymentPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("transfer-to-payee"), "transfer payee page not visible");

        driver.navigate().back();
        waitTillInvisibilityOfLoader(driver);
//        driver.navigate().refresh();
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks edit button and edit the payee details")
    public void userClicksEditButtonAndEditThePayeeDetails() {
        waitTillVisibilityElement(driver, managePayeePage.payeeDetailsEditButton);
        managePayeePage.payeeDetailsEditButton.click();
        waitTillElementToBeClickable(driver, managePayeePage.addPayeeName);

    }

    @Then("User enter required details")
    public void userEnterRequiredDetails() {
        scrollIntoViewUp(driver,managePayeePage.addPayeeName);
        managePayeePage.addPayeeName.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.managePayeeTestData.get("updatedPayeeName"));
        managePayeePage.addPayeeMobileNumber.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.managePayeeTestData.get("updatedMobileNumber"));
        managePayeePage.payeeEditEmail.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.managePayeeTestData.get("updatedMailId"));
        waitTillInvisibilityOfLoader(driver);
        logger.info("The user updated the payee details");
    }

    @When("User clicks submit button")
    public void userClicksSubmitButton() {
        scrollIntoView(driver, managePayeePage.payeeEditDetailsSubmitButton);
        waitTillElementToBeClickable(driver, managePayeePage.payeeEditDetailsSubmitButton);
        javaScriptExecutorClickElement(driver, managePayeePage.payeeEditDetailsSubmitButton);

    }

    @Then("User verify edit payee details")
    public void userVerifyEditPayeeDetails() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, managePayeePage.pageHeader);
        softAssert.assertTrue(managePayeePage.payeeUpdatedPopUp.isDisplayed(), "payee updated successfully not displayed");
        managePayeePage.payeeUpdatedBackToPayeesPopUp.click();
        waitTillInvisibilityOfLoader(driver);
        logger.info("Payee details are updated with new data's");
        softAssert.assertEquals(managePayeePage.payeeNameInTable.getText(), fileReader.managePayeeTestData.get("updatedPayeeName"), "payee name not matched");
        softAssert.assertEquals(managePayeePage.payeeEmailInTable.getText(), fileReader.managePayeeTestData.get("updatedMailId"), "payee email not matched");
        softAssert.assertTrue(managePayeePage.payeeMobileNumberInTable.getText().contains(fileReader.managePayeeTestData.get("updatedMobileNumber")), "payee mobile number not matched");
        softAssert.assertTrue(managePayeePage.payeebankNameInTable.getText().equalsIgnoreCase(fileReader.managePayeeTestData.get("bankName")), "payee bank name not matched");

        try {
            softAssert.assertAll();
            //  attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks deactivate button")
    public void userClicksDeactivateButton() {
//        driver.navigate().refresh();
        scrollIntoViewUp(driver, managePayeePage.pageHeader);
        waitTillVisibilityElement(driver, managePayeePage.deactivateButton);
        managePayeePage.deactivateButton.click();
        waitTillVisibilityElement(driver, managePayeePage.deactivateButtonPopup);
    }

    @And("User clicks deactivate button in popup")
    public void userClicksDeactivateButtonInPopup() {
        managePayeePage.deactivateButtonPopup.click();

    }

    @And("User can verify the payee deactivate message")
    public void userCanVerifyThePayeeDeactivateMessage() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, managePayeePage.deactivateSuccessful);
        //  Assert.assertTrue(managePayeePage.deactivateSuccessful.getText().contains("Payee deactivated successfully!"), "payee deactivated successfully pop up not displayed");
        // System.out.println(managePayeePage.deactivateSuccessful.getAttribute("innerHTML"));
        managePayeePage.deactivateMessageReferenceCopyButton.click();
        staticWait(1000);
        softAssert.assertTrue(managePayeePage.successfullMessage.isDisplayed(), "reference number copied message not displayed");
        managePayeePage.deactivateBackToManagePayee.click();
        logger.info("Payee details are deactivated");
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify deactivate payee details")
    public void userVerifyDeactivatePayeeDetails() {
        softAssert = new SoftAssert();
        for (WebElement payeeNames : managePayeePage.payeeNamesList) {
            String namesInList = payeeNames.getText();
            scrollIntoView(driver, payeeNames);
            softAssert.assertFalse(namesInList.equalsIgnoreCase(fileReader.managePayeeTestData.get("updatePayeeName")), "deactivate payee name displayed in list");
        }
        for (WebElement payeeMobileNumbers : managePayeePage.payeeMobileList) {
            String mobileInList = payeeMobileNumbers.getText();
            softAssert.assertFalse(mobileInList.equalsIgnoreCase(fileReader.managePayeeTestData.get("updatedMobileNumber")), "deactivate payee mobileNumber displayed in list");
        }
        for (WebElement payeeEmailLists : managePayeePage.payeeEmailList) {
            String emailInList = payeeEmailLists.getText();
            softAssert.assertFalse(emailInList.equalsIgnoreCase(fileReader.managePayeeTestData.get("emailId")), "deactivate emailId displayed in list");
        }
        //  attachScreenshot(driver, scenario);
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks pay button payments page section")
    public void userClicksPayButtonPaymentsPageSection() {
        try {
            scrollIntoView(driver, managePayeePage.payeePageSearchBox);
            ListIterator<WebElement> payButtons = managePayeePage.payButtonList.listIterator();
            while (payButtons.hasNext()) {
                WebElement payButton = payButtons.next();
                if (payButton.isEnabled()) {
                    payButton.click();
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            logger.info("No payee has been added to this account");
            //System.out.println("No Payee account is available");
        }
    }

    @When("User clicks on {string} drop icon on add new payee page")
    public void userClicksOnDropIconOnAddNewPayeePage(String textHeader) {
        scrollIntoView(driver,managePayeePage.dropDownIcon(textHeader));
        if (textHeader.equalsIgnoreCase("upi/vpa")) {
            managePayeePage.dropDownIcon(textHeader).click();
        } else if (textHeader.equalsIgnoreCase("address")) {
            managePayeePage.dropDownIcon(textHeader).click();
        } else if (textHeader.equalsIgnoreCase("tax and payments")) {
            managePayeePage.dropDownIcon(textHeader).click();
        }
    }

    @Then("User validates the {string} section on add new payee page")
    public void userValidatesTheSectionOnAddNewPayeePage(String textHeader) {
        softAssert = new SoftAssert();
        if (textHeader.equalsIgnoreCase("upi/vpa")) {
            softAssert.assertTrue(managePayeePage.upiVpaEnterTextBox.isDisplayed(), textHeader + " enter text box is not displayed");

        } else if (textHeader.equalsIgnoreCase("address")) {
            scrollIntoView(driver,managePayeePage.billNameEnterTextBox);
            softAssert.assertTrue(managePayeePage.billNameEnterTextBox.isDisplayed(), "bill name enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.billingIndiaRadioButton.isDisplayed(), "india option radio enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.billingOutSideIndiaRadioButton.isDisplayed(), "outside india option radio enter text bar is not displayed");
            softAssert.assertTrue(managePayeePage.addressLineOneEnterTextBox.isDisplayed(), "address line one enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.addressLineTwoEnterTextBox.isDisplayed(), "address line two enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.addressLineTwoEnterTextBox.isDisplayed(), "address line two enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.pinCodeEnterTextBox.isDisplayed(), "pinCode enter text bar is not displayed");
            softAssert.assertTrue(managePayeePage.shippingAddressCheckBox.isDisplayed(), "shipping address check box enter text bar is not displayed");
            /**After Clicking shipping checkbox validate the their options*/
            scrollIntoView(driver,managePayeePage.shippingAddressCheckBox);
            waitTillElementToBeClickable(driver,managePayeePage.shippingAddressCheckBox);
            managePayeePage.shippingAddressCheckBox.click();
            softAssert.assertTrue(managePayeePage.shippingName.isDisplayed(), "shipping name enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.shippingName.isDisplayed(), "shipping name enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.shippingIndiaRadioButton.isDisplayed(), "shipping india option radio enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.shippingOutSideIndiaRadioButton.isDisplayed(), "shipping outside india option radio enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.shippingAddressLineOneEnterTextBox.isDisplayed(), "shipping address line one enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.shippingAddressLineTwoEnterTextBox.isDisplayed(), "shipping address line two enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.shippingPinCodeEnterTextBox.isDisplayed(), "shipping pin code enter text box is not displayed");
        } else if (textHeader.equalsIgnoreCase("tax and payments")) {
            softAssert.assertTrue(managePayeePage.panNumberEnterTextBox.isDisplayed(), "pan number enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.gstNumberEnterTextBox.isDisplayed(), "gst number enter text box is not displayed");
            softAssert.assertTrue(managePayeePage.tanNumberEnterTextBox.isDisplayed(), "tan number enter text box is not displayed");
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
