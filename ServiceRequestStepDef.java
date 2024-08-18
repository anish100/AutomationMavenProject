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
import org.openqa.selenium.NoSuchElementException;

import org.testng.Assert;

import org.testng.asserts.SoftAssert;
import pom.DashboardPage;
import pom.HomePage;
import pom.ServiceRequestPage;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ServiceRequestStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(ServiceRequestStepDef.class);
    ServiceRequestPage serviceRequestPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    HomePage homePage;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String chequeBookRequestedDate;
    String newChequeBookReqReferenceNumber;
    Date todayDate;
    String referenceNoForm15GPage;
    DashboardPage dashboardPage;
    String referenceNoUpdateAddress;
    String serviceRequestDescription;
    int nameRandomIndexMail;
    String randomMail;
    String positivePayChequeNumber;
    String chequeIssuanceDate;
    boolean positivePayStatus;
    String positiveReferenceNumber;

    public ServiceRequestStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        serviceRequestPage = context.getPageObjectManager().getServiceRequestPage();
        homePage = context.getPageObjectManager().getHomePage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        dashboardPage = context.getPageObjectManager().getDashboardPage();
    }

    @Then("User verify service request home page")
    public void userVerifyServiceRequestHomePage() {
        try {
            softAssert = new SoftAssert();
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.serviceRequestHomePageUrl), "navigates to service request home page failed");
            softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.serviceRequestHomePageHeader), "service request page header not displayed");
            softAssert.assertTrue(serviceRequestPage.serviceRequestListSection.isDisplayed(), "service request list section not displayed");

            //Cheque Service Section
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.newChequeBookTab).isDisplayed(), "new cheque box tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.chequeStatusTab).isDisplayed(), "cheque status tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.stopChequeTab).isDisplayed(), "stop cheque tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.positivePayTab).isDisplayed(), "positive pay tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.positivePayTabStatus).isDisplayed(), "positive pay status tab not displayed");
            // Account Section
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.addNomineeTab).isDisplayed(), "add/update nominee tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.updateMailIdTab).isDisplayed(), "update mail id tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.updateCommAddress).isDisplayed(), "update communication address tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.updateMobileNumberTab).isDisplayed(), "update mobile number not displayed");
            //       softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.dormantAccountTab).isDisplayed(), "dormant account tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.trackServiceRequestTab).isDisplayed(), "track service request tab not displayed");
            softAssert.assertTrue(serviceRequestPage.clickOnTab(TextAssertion.form15GTab).isDisplayed(), "form 15G/H tab not displayed");
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Some Elements not displayed");
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on new cheque book tab")
    public void userClicksOnNewChequeBookTab() {
        serviceRequestPage.clickOnTab(TextAssertion.newChequeBookTab).click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify new cheque book page")
    public void userVerifyNewChequeBookPage() {
        softAssert = new SoftAssert();
        logger.info("User is on " + serviceRequestPage.pageHeader.getText());
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.newChequeBookPageUrl), "new cheque book request page url not matched");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.newChequeBookPageHeader), "new cheque book request header not matched");
        softAssert.assertTrue(serviceRequestPage.backButton.isDisplayed(), "back button not displayed");
        softAssert.assertTrue(serviceRequestPage.selectAccount.isDisplayed(), "select account bar not displayed");
        softAssert.assertTrue(serviceRequestPage.noOfSelectLeaves("25").isDisplayed(), "no of leaves on cheque book not displayed");
        softAssert.assertTrue(serviceRequestPage.chequeBookReceiveAddress("Branch").isDisplayed(), "cheque book receive option not displayed ");
        softAssert.assertTrue(!serviceRequestPage.deliveryAddress.getText().isEmpty(), "delivery address not displayed");
        softAssert.assertTrue(serviceRequestPage.updateAddress.isDisplayed(), "update address option not displayed");
        softAssert.assertTrue(serviceRequestPage.requestChequeBookButton.isDisplayed(), "request cheque book button not displayed");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User enters the details on new cheque book page")
    public void userEntersTheDetailsOnNewChequeBookPage() {
        serviceRequestPage.selectAccount.click();
        serviceRequestPage.selectAccountNumberFromDD(fileReader.serviceRequestTestData.get("accountNumber")).click();
        serviceRequestPage.noOfSelectLeaves(fileReader.serviceRequestTestData.get("noOfLeaves")).click();
        serviceRequestPage.chequeBookReceiveAddress(fileReader.serviceRequestTestData.get("chequeBookReceiveAt")).click();
        logger.info("User request for new cheque book for the account number of " + fileReader.serviceRequestTestData.get("accountNumber"));
        logger.info("User requested contains " + fileReader.serviceRequestTestData.get("noOfLeaves") + " no of leaves ");
        logger.info("User will receive the cheque book in the address of " + fileReader.serviceRequestTestData.get("chequeBookReceiveAt"));
    }

    @When("User clicks on update address on new cheque book page")
    public void userClicksOnUpdateAddressOnNewChequeBookPage() {
        serviceRequestPage.updateAddress.click();
        waitTillInvisibilityOfLoader(driver);
        try {
            if (serviceRequestPage.updateAddressContinueButton.isDisplayed()) {
                serviceRequestPage.updateAddressContinueButton.click();
                waitTillInvisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException e) {
            logger.info("Update address pop up not displayed");
            // System.out.println("Please note pop up not displayed");
        }
    }

    @And("User clicks request cheque book button")
    public void userClicksRequestChequeBookButton() {
        waitTillInvisibilityOfLoader(driver);
        waitTillElementToBeClickable(driver, serviceRequestPage.requestChequeBookButton);
        serviceRequestPage.requestChequeBookButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the new cheque book status page")
    public void userVerifyTheNewChequeBookStatusPage() {
        softAssert = new SoftAssert();

        if (driver.getCurrentUrl().equals(TextAssertion.newChequeBookPageUrl)) {
            logger.info("Cheque book request limit exceeded");
            softAssert.assertTrue(serviceRequestPage.limitExceedPopUp.getText().contains(TextAssertion.limitExceedPopUp));
            softAssert.assertTrue(!serviceRequestPage.limitExceedPopUpMessage.getText().isEmpty(), "limit exceed pop up message not displayed");
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH);
            todayDate = new Date();
            chequeBookRequestedDate = sdf.format(todayDate);
            logger.info("Cheque book requested on :" + chequeBookRequestedDate);
            // System.out.println(chequeBookRequestedDate);
            serviceRequestPage.limitExceedPopUpClose.click();
            staticWait(3000);
            serviceRequestPage.backButton.click();
            waitTillInvisibilityOfLoader(driver);
        } else if (driver.getCurrentUrl().equals(TextAssertion.newChequeBookStatusPageUrl)) {
            logger.info("Cheque book request limit not exceeded");
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.newChequeBookPageUrl), "new cheque book page url not matched");
            softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.newChequeBookPageHeader), "new cheque book page header not matched");
            softAssert.assertTrue(serviceRequestPage.placeNewServiceButton.isDisplayed(), "place new service button not displayed");
            softAssert.assertTrue(serviceRequestPage.successfulMessage.getText().contains("uccessfully"), "cheque book requested successful message not displayed");
            logger.info("Status of the cheque book request" + serviceRequestPage.successfulMessage.getText());
            softAssert.assertTrue(!serviceRequestPage.newChequeStatusReferenceNumber.getText().isEmpty(), "reference number not displayed");
            setReferenceNumberServiceRequest(serviceRequestPage.newChequeStatusReferenceNumber.getText());
            serviceRequestPage.newChequeStatusReferenceNumberCopyButton.click();
            staticWait(1000);
            softAssert.assertTrue(serviceRequestPage.newChequeStatusReferenceNumberCopiedMessage.getText().contains("opied"), "copied successfully pop up not displayed");
            //   softAssert.assertTrue(serviceRequestPage.newChequeStatusNoOfLeaves.getText().contains(fileReader.serviceRequestTestData.get("noOfLeaves") + "Leaves"), "no of leaves requested cheque book not matched");
            softAssert.assertTrue(!serviceRequestPage.newChequeStatusDateRequested.getText().isEmpty(), "cheque book requested date not displayed");
            softAssert.assertTrue(serviceRequestPage.newChequeStatusAccountNumber.getText().contains(fileReader.serviceRequestTestData.get("accountNumber")), "account number not matched");
            softAssert.assertTrue(!serviceRequestPage.newChequeStatusAddress.getText().isEmpty(), "address not displayed");
            softAssert.assertTrue(serviceRequestPage.backToHomePageButton.isDisplayed(), "back to home page button not displayed");
            chequeBookRequestedDate = serviceRequestPage.newChequeStatusDateRequested.getText();
            newChequeBookReqReferenceNumber = serviceRequestPage.newChequeStatusReferenceNumber.getText();
            softAssert.assertEquals(newChequeBookReqReferenceNumber.trim(), getCopiedValue().trim(), "copied value doesn't match");
            logger.info("Cheque book requested on :" + chequeBookRequestedDate);
            //     driver.navigate().back();
            serviceRequestPage.backButton.click();
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


    @When("User clicks on service request list")
    public void userClicksOnServiceRequestList() {
        serviceRequestPage.serviceRequestListSection.click();
        waitForPageLoad(driver);
    }


    @Then("User verify the service request list page and clicks on view button")
    public void userVerifyTheServiceRequestListPageAndClicksOnViewButton() {
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.serviceRequestUrl), "navigates to service request page is failed");
            softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.serviceRequestPageHeader), "service request page header not matched");
            softAssert.assertTrue(serviceRequestPage.serviceRequestViewButton(getReferenceNumberServiceRequest()).isDisplayed(), getReferenceNumberServiceRequest() + "for this reference number description showing empty");
            serviceRequestDescription = serviceRequestPage.serviceRequestDescription(getReferenceNumberServiceRequest()).getText();
            softAssert.assertFalse(serviceRequestDescription.isEmpty(), "description showing empty");
            softAssert.assertFalse(serviceRequestPage.serviceRequestDate(getReferenceNumberServiceRequest()).getText().isEmpty(), getReferenceNumberServiceRequest() + "for this reference number date showing empty");
            serviceRequestPage.serviceRequestViewButton(getReferenceNumberServiceRequest()).click();
            waitTillInvisibilityOfLoader(driver);

            softAssert.assertAll();
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            logger.error(exception.toString());
            // softAssert.fail("Cheque Book requisition not displayed");

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the track service request page")
    public void userVerifyTheTrackServiceRequestPage() {
        softAssert = new SoftAssert();
        logger.info("User is on " + serviceRequestPage.pageHeader.getText() + " page");
        try {
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.trackServiceRequestUrl), "service request track url not matched");
            softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.trackServiceRequestHeader), "track service request page not displayed");
            softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceNo.getText().length() != 0, "reference number not displayed");
            softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceReq.getText().length() != 0, "service request details not displayed");

            if (serviceRequestPage.trackServiceReqPageReqDetails.getText().contains("Request for Cheque Book")) {
                softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceAccountNo.getText().contains(fileReader.serviceRequestTestData.get("accountNumber")), "account number not matched");
                softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceReq.getText().contains("Cheque Book"), "service request details not be the same");
            } else {
                softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceReq.getText().contains("Form 15"), "service request details not be the same");
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            todayDate = new Date();
            String dateToday = dateFormat.format(todayDate);
            softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceDate.getText().contains(dateToday), "service request status page date is not matched");
            softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceChannel.getText().contains(fileReader.serviceRequestTestData.get("channelName")), "service request status page channel name matched");
            softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceStatus.getText().length() != 0, "service request status not displayed");
            waitTillElementToBeClickable(driver, serviceRequestPage.placeNewServiceButton);
            serviceRequestPage.placeNewServiceButton.click();

            softAssert.assertAll();
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            logger.error(exception.toString());
            softAssert.fail(exception.toString());
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    //Cheque Status Scenario
    @When("User clicks on check status tab")
    public void userClicksOnCheckStatusTab() {
        serviceRequestPage.clickOnTab(TextAssertion.chequeStatusTab).click();
        waitForPageLoad(driver);

    }

    @Then("User navigates to check status page")
    public void userNavigatesToCheckStatusPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.chequeStatusUrl), "cheque status url page not matched");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.chequeStatusHeader), "cheque status page header not matched");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User enter cheque details")
    public void userEnterChequeDetails() {
        waitTillElementToBeClickable(driver, serviceRequestPage.selectAccount);
        serviceRequestPage.selectAccount.click();
        serviceRequestPage.selectAccountNumberFromDD(fileReader.serviceRequestTestData.get("accountNumber")).click();
        waitTillLoading(driver);
        try {
            serviceRequestPage.enterChequeNumber.sendKeys(positivePayChequeNumber);
            waitTillInvisibilityOfLoader(driver);
        } catch (ElementNotInteractableException InteractableException) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Cheque number can't be entered please verify the screenshot");
        }
        int noOfTime = 5;
        for (int i = 0; i <= noOfTime; i++) {
            if (serviceRequestPage.invalidChequeDetails.getText().contains("Cheque number not valid")) {
                Random randomNum = new Random();
                int randomChequeNo = randomNum.nextInt(99) + 10;
                serviceRequestPage.enterChequeNumber.sendKeys("0000" + Integer.toString(randomChequeNo));
                logger.info("Current cheque Number is " + randomChequeNo);
            } else {
                break;
            }
        }
    }

    @And("User clicks on find status button")
    public void userClicksOnFindStatusButton() {
        waitTillElementToBeClickable(driver, serviceRequestPage.findStatusButton);
        serviceRequestPage.findStatusButton.click();
        staticWait(3000);
    }

    @And("User verify the status of the cheque")
    public void userVerifyTheStatusOfTheCheque() {
        softAssert.assertTrue(serviceRequestPage.chequeNumberStatus.getText().contains(positivePayChequeNumber), "cheque number not matched");
        softAssert.assertTrue(serviceRequestPage.chequeStatusRepo.getText().length() != 0, "cheque status not displayed");
        logger.info("Cheque status :" + serviceRequestPage.chequeStatusRepo.getText());
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks on positive pay status tab")
    public void userClicksOnPositivePayStatusTab() {
        serviceRequestPage.clickOnTab(TextAssertion.positivePayTabStatus).click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User navigates to the positive pay status tab")
    public void userNavigatesToThePositivePayStatusTab() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.positivePayStatusUrl), "positive pay status url not matched");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.positivePayStatusHeader), "positive page header not matched");
        if (serviceRequestPage.positivePayPageList.getText().contains("no results found")) {
            logger.info("No list is present");
            // System.out.println("No list Present");
        } else {
            if (positivePayStatus = true) {
                try {
                    serviceRequestPage.viewButtonPositivePayStatusPage(positivePayChequeNumber).click();
                } catch (NoSuchElementException e) {
                    attachScreenshot(driver, scenario);
                    logger.error("View button for the particular cheque number is not present please verify");
                }

                softAssert.assertTrue(serviceRequestPage.positivePayStatusPageBankAccNo.getText().contains(fileReader.serviceRequestTestData.get("accountNumber")), "bank account number not matched");
                softAssert.assertTrue(serviceRequestPage.positivePayStatusPageChequeNo.getText().contains(positivePayChequeNumber), "cheque number not be the same");
            } else {
                serviceRequestPage.viewButtonList.get(serviceRequestPage.viewButtonList.size() - 1).click();
                String accountHolderName = serviceRequestPage.payStatusPageAccHolderName.get(serviceRequestPage.payStatusPageAccHolderName.size() - 1).getText();
                String accountNumberStr = serviceRequestPage.payStatusPageAccNo.get(serviceRequestPage.payStatusPageAccNo.size() - 1).getText();
                String accountNumber = accountNumberStr.replaceAll("[^\\d]", "");
                String chequeNumberStr = serviceRequestPage.payStatusChequeNumber.get(serviceRequestPage.payStatusChequeNumber.size() - 1).getText();
                String chequeNumber = chequeNumberStr.replaceAll("[^\\d]", "");
                String amount = serviceRequestPage.payStatusPageAmount.get(serviceRequestPage.payStatusPageAmount.size() - 1).getText();
                softAssert.assertEquals(serviceRequestPage.positivePayStatusPageBankAccNo.getText(), accountNumber, "bank account number not matched");
                softAssert.assertEquals(serviceRequestPage.positivePayStatusPageChequeNo.getText(), chequeNumber, "cheque number not be the same");
                //   softAssert.assertEquals(serviceRequestPage.positivePayStatusPageAmount.getText(),amount, "entered amount not matched");

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

    @When("User clicks on stop cheque tab")
    public void userClicksOnStopChequeTab() {
        //  driver.navigate().back();
        /******* Navigates to Service request home page ********/
        clickOnButton(homePage.raiseRequestButton);
        waitTillInvisibilityOfLoader(driver);
        serviceRequestPage.stopChequeTab.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User navigates to stop cheque status page")
    public void userNavigatesToStopChequeStatusPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.stopChequeStatusUrl), "cheque status url page not matched");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.stopChequeStatusHeader), "cheque status page header not matched");
        waitTillInvisibilityOfLoader(driver);

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the status of the stop cheque")
    public void userVerifyTheStatusOfTheStopCheque() {
        if (serviceRequestPage.chequeInformation.getAttribute("class").contains("information")) {
            softAssert.assertTrue(serviceRequestPage.chequeNumberStatus.getText().contains(positivePayChequeNumber), "cheque number not matched");
            softAssert.assertTrue(serviceRequestPage.chequeStatusRepo.getText().length() != 0, "cheque status not displayed");
        } else {
            softAssert.fail("Need to enter correct cheque number");
            logger.error("Enter Correct Cheque Number error message displayed");
        }


        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks on stop cheque button")
    public void userClicksOnStopChequeButton() {
        try {
            if (serviceRequestPage.chequeStatusRepo.getText().contains("Unpaid")) {
                waitTillElementToBeClickable(driver, serviceRequestPage.enterReason);
                serviceRequestPage.enterReason.sendKeys("Amount enter incorrect");
                serviceRequestPage.stopChequeButton.click();
                staticWait(3000);
            } else {
                logger.info("Cheque payment already stopped");
            }
        } catch (NoSuchElementException chequeStatusNotShowing) {
            attachScreenshot(driver, scenario);
            logger.error("Those elements are missing ,check screenshot");
        }
    }

    @When("User clicks on update emailId tab")
    public void userClicksOnUpdateEmailIdTab() {
        serviceRequestPage.clickOnTab(TextAssertion.updateMailIdTab).click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User navigates to update emailId page")
    public void userNavigatesToUpdateEmailIdPage() {
        softAssert = new SoftAssert();
        logger.info("User is on " + serviceRequestPage.pageHeader.getText() + "page");
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.updateNomineeMailIdPageUrl), "email id update page url not matched");
        softAssert.assertTrue(serviceRequestPage.backButton.isDisplayed(), "back button is not displayed");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.updateNomineeMailIdHeader), "email id header not matched");
        softAssert.assertTrue(!serviceRequestPage.customerIdCommAddPage.getText().isEmpty(), "customer id in comm Add page showing as empty");
        softAssert.assertTrue(!serviceRequestPage.beforeUpdateEmailID.getText().isEmpty(), "current mail id showing as empty");
        softAssert.assertTrue(serviceRequestPage.enterUpdateEmail.isDisplayed(), "update mail tab is not displayed");
        softAssert.assertTrue(serviceRequestPage.nextButton.isDisplayed(), "next button in update comm address page is not displayed");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User enter the details to update mail id")
    public void userEnterTheDetailsToUpdateMailId() {
        String beforeUpdateMail = serviceRequestPage.beforeUpdateEmailID.getText();
        setPayeeMailId(beforeUpdateMail);
        logger.info("Users email id before updated " + beforeUpdateMail);
        //Random Class to use different email
        Random random = new Random();
        String digitCreate = String.valueOf(random.nextInt(1000));
        List<String> usersMailId = new ArrayList<>();
        usersMailId.add("cucumber" + digitCreate + "@gmail.com");
        usersMailId.add("AutoUser" + digitCreate + "@gmail.com");
        usersMailId.add("SeleniumJava" + digitCreate + "@gmail.com");
        usersMailId.add("Appium" + digitCreate + "@gmail.com");
        usersMailId.add("soundaraj" + digitCreate + "@gmail.com");
        nameRandomIndexMail = random.nextInt(usersMailId.size());
        String randomMailNames = usersMailId.get(nameRandomIndexMail);
        randomMail = randomMailNames + "." + digitCreate;
        logger.info("Randomly created mail is " + randomMail);
        serviceRequestPage.enterUpdateEmail.sendKeys(Keys.chord(Keys.CONTROL, "a"), randomMail);
        staticWait(2000);
        if (serviceRequestPage.enterUpdateEmailError.getAttribute("class").contains("s-icon-error")) {
            nameRandomIndexMail = random.nextInt(usersMailId.size());
            randomMail = usersMailId.get(nameRandomIndexMail);
            serviceRequestPage.enterUpdateEmail.sendKeys(Keys.chord(Keys.CONTROL, "a"), randomMail);
            logger.info("Updated email id is equal ,Updated new mail id is" + randomMail);
            serviceRequestPage.nextButton.click();
        } else {
            serviceRequestPage.nextButton.click();
        }

    }

    @And("User verify the update status page")
    public void userVerifyTheUpdateStatusPage() {
        softAssert = new SoftAssert();
        try {
            waitTillInvisibilityElement(driver, serviceRequestPage.nextButton);
            softAssert.assertTrue(serviceRequestPage.updateEmailPageEmailIdStatus.getAttribute("innerHTML").contains("Email ID updated successfully"), "email updated message not displayed");
            logger.info("User Email id status is :" + serviceRequestPage.updateEmailPageEmailIdStatus.getAttribute("innerHTML"));
            softAssert.assertTrue(serviceRequestPage.updateEmailPageEmailIdRefNumber.isDisplayed(), "updated email reference number is not displayed");
            softAssert.assertTrue(serviceRequestPage.updatedEmailId.isDisplayed(), "updated email id is not displayed");
            softAssert.assertNotEquals(serviceRequestPage.updatedEmailId.isDisplayed(), getPayeeMailId(), "user email id not updated");
            logger.info("Updated user email id is " + serviceRequestPage.updatedEmailId.getText());

        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            logger.error(e.toString());
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks on update communication address button")
    public void userClicksOnUpdateCommunicationAddressButton() {
        serviceRequestPage.clickOnTab(TextAssertion.updateCommAddress).click();
        waitTillInvisibilityOfLoader(driver);

    }

    @Then("User verify update communication address page")
    public void userVerifyUpdateCommunicationAddressPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("update-communication-address"), "update communication address url not matched");
        softAssert.assertTrue(serviceRequestPage.backButton.isDisplayed(), "update communication page back button is not displayed");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.updateCommAddress), "update communication page header is not matched");
        softAssert.assertTrue(serviceRequestPage.updateAddressPageSelectProof.isDisplayed(), "select proof tab not displayed");
        softAssert.assertTrue(serviceRequestPage.enterAddressLineOne.isDisplayed(), "address line one tab is not displayed");
        softAssert.assertTrue(serviceRequestPage.enterAddressLineTwo.isDisplayed(), "address line two tab is not displayed");
        scrollIntoView(driver, serviceRequestPage.enterPinCode);
        staticWait(2000);
        softAssert.assertTrue(serviceRequestPage.enterPinCode.isDisplayed(), "pin code tab is not displayed");
        softAssert.assertTrue(serviceRequestPage.uploadFile.isEnabled(), "upload file tab is not displayed");
        softAssert.assertTrue(serviceRequestPage.termsAndConditionAgreeCheckBox.isDisplayed(), "terms and condition check box is not displayed");
        softAssert.assertTrue(serviceRequestPage.nextButton.isDisplayed(), "next button id not displayed");
        scrollIntoViewUp(driver, serviceRequestPage.backButton);

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User enter all details to update communication address")
    public void userEnterAllDetailsToUpdateCommunicationAddress() {
        String address1[] = {"Elegance Apartment ,TharGroup PG,Rajat path"};
        Random random = new Random();
        int indexRandom = random.nextInt(address1.length);
        String addressLine1 = address1[indexRandom];
        serviceRequestPage.updateAddressPageSelectProof.click();
        serviceRequestPage.updateAddressSelectProofFromDD(fileReader.serviceRequestTestData.get("addressProof")).click();
        serviceRequestPage.enterAddressLineOne.sendKeys(addressLine1);
        serviceRequestPage.enterAddressLineTwo.sendKeys(fileReader.serviceRequestTestData.get("address"));
        serviceRequestPage.enterPinCode.sendKeys(fileReader.serviceRequestTestData.get("pinCode"));
        logger.info("Updated address is " + addressLine1 + " , " + fileReader.serviceRequestTestData.get("address"));
        staticWait(3000);
        // ((JavascriptExecutor)driver).executeScript("arguments[0].style.display = 'block';", serviceRequestPage.uploadFile);
        serviceRequestPage.uploadFile.sendKeys("C:\\Users\\987993\\Merchant_Web_Automation\\src\\main\\resources\\data\\DemoFileForAddressUpdate.pdf");
        // serviceRequestPage.uploadFile.submit();
        serviceRequestPage.checkBoxReadAndTermCondition.click();
        waitTillElementToBeClickable(driver, serviceRequestPage.checkBoxReadAndTermCondition);
        serviceRequestPage.nextButton.click();
        //     waitTillInvisibilityOfLoader(driver);
        if (driver.getCurrentUrl().contains("new-check-book/update-communication-address")) {
            Assert.assertTrue(serviceRequestPage.requestSendForUpdate.getText().contains("Request sent"), "updation message not displayed");
        }

    }

    @And("User verify the update communication address status page")
    public void userVerifyTheUpdateCommunicationAddressStatusPage() {
        softAssert = new SoftAssert();
        try {
            waitTillInvisibilityOfLoader(driver);
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.updateCommAddressStatus), "update communication address page url not displayed");
            softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.updateCommAddress), "update communication page header not matched");
            softAssert.assertTrue(serviceRequestPage.successMessage.isDisplayed(), "successfully message not displayed");
            logger.info("Update communication address status is " + serviceRequestPage.statusPageMessage.getText());
            softAssert.assertTrue(serviceRequestPage.serviceReqTrackReferenceNo.getText().length() != 0, "reference number not displayed");
            serviceRequestPage.newChequeStatusReferenceNumberCopyButton.click();
            staticWait(1000);
//            softAssert.assertTrue(serviceRequestPage.newChequeStatusReferenceNumberCopiedMessage.getText().contains("opied"), "copied successfully message not displayed");
            softAssert.assertTrue(serviceRequestPage.newChequeStatusReferenceNumberCopiedMessage.isDisplayed(), "copied successfully message not displayed");
            String copiedMessage = getCopiedValue().trim();
            referenceNoUpdateAddress = serviceRequestPage.serviceReqTrackReferenceNo.getText();
            softAssert.assertEquals(copiedMessage, referenceNoUpdateAddress.trim(), "copied reference value not matched");
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks on form {int}g tab")
    public void userClicksOnFormGTab(int arg0) {
        serviceRequestPage.clickOnTab(TextAssertion.form15GTab).click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify and enter the details in form page")
    public void userVerifyAndEnterTheDetailsInFormPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.form15ghRequestPageUrl), "form 15g page url not matched");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.form15ghRequestPageHeader), "form 15g page header not matched");
        sendKeys(serviceRequestPage.enterEstimatedTotalIncome, fileReader.serviceRequestTestData.get("estimatedIncome"));
        serviceRequestPage.selectForm15gCount.click();
        serviceRequestPage.selectForm15GCountDD(fileReader.serviceRequestTestData.get("noOfForm")).click();
        sendKeys(serviceRequestPage.enterAggregatedAmount, fileReader.serviceRequestTestData.get("aggreegateAmount"));
        serviceRequestPage.termsAndConditionAgreeCheckBox.click();
        serviceRequestPage.nextButton.click();
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User navigates to the form {int}g review page")
    public void userNavigatesToTheFormGReviewPage(int arg0) {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.form15ghRequestReviewPageUrl), "form 15 review page not matched");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.form15ghRequestPageHeader), "form 15 review page header not matched");
        softAssert.assertTrue(serviceRequestPage.form15ghReviewPageIncome.getText().replace("₹", "").replaceAll(",", "").trim().contains(fileReader.serviceRequestTestData.get("estimatedIncome") + ".00"), "form 15g review page income amount not matched ");
        softAssert.assertEquals(serviceRequestPage.form15ghReviewPageAggregateNumber.getText().trim(), fileReader.serviceRequestTestData.get("noOfForm"), "form 15g aggregate number not matched");
        softAssert.assertTrue(serviceRequestPage.form15ghReviewPageAggregateAmount.getText().replace("₹", "").replaceAll(",", "").trim().contains(fileReader.serviceRequestTestData.get("aggregateAmount") + ".00"), "form 15g review page aggregated amount not matched");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the form {int}g status page")
    public void userVerifyTheFormGStatusPage(int arg0) {
        //   try {
        softAssert = new SoftAssert();
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.form15ghRequestPageHeader), "form15g/h header not matched");
        softAssert.assertTrue(serviceRequestPage.statusPageMessage.getText().contains("uccessfully"), "successfully message not displayed");
        softAssert.assertTrue(serviceRequestPage.referenceNoForm15StatusPage.getText().length() != 0, "reference number not displayed");
        serviceRequestPage.newChequeStatusReferenceNumberCopyButton.click();
        softAssert.assertTrue(serviceRequestPage.newChequeStatusReferenceNumberCopiedMessage.getText().contains("uccessfully"), "copied successfully message not displayed");
        referenceNoForm15GPage = serviceRequestPage.serviceReqTrackReferenceNo.getText();
        softAssert.assertEquals(referenceNoForm15GPage.trim(), getCopiedValue().trim(), "copied value doesn't match");
        log.info("Form 15 Reference Number is " + referenceNoForm15GPage);
        serviceRequestPage.backToHomePageButton.click();
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks on track service request tab")
    public void userClicksOnTrackServiceRequestTab() {
        waitTillInvisibilityOfLoader(driver);
        serviceRequestPage.clickOnTab(TextAssertion.trackServiceRequestTab).click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the track services request page")
    public void userVerifyTheTrackServicesRequestPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.trackServiceRequestPageUrl), "track service request page url not matched");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.trackServiceRequestPageHeader), "track service request page header not displayed");
    }

    @And("User verify service request list page")
    public void userVerifyServiceRequestListPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.trackServiceRequestListPageUrl), "track service req list page url not matched");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.trackServiceRequestPageHeader), "track service req list page header not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on positive pay tab")
    public void userClicksOnPositivePayTab() {
        serviceRequestPage.clickOnTab(TextAssertion.positivePayTab).click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User navigates to the positive pay page")
    public void userNavigatesToThePositivePayPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(serviceRequestPage.pageHeader.isDisplayed(), "page header is not displayed");
        softAssert.assertTrue(serviceRequestPage.selectAccount.isDisplayed(), "positive pay select account tab not displayed");
        softAssert.assertTrue(serviceRequestPage.positivePayChequeNumber.isDisplayed(), "enter cheque number tab is not displayed");
        softAssert.assertTrue(serviceRequestPage.quickLinkInPositivePayPage.isDisplayed(), "quick link in positive pay not displayed");
        serviceRequestPage.quickLinkInPositivePayPage.click();
        waitTillVisibilityElement(driver, serviceRequestPage.pageHeader);
        softAssert.assertTrue(driver.getCurrentUrl().contains("service-request/positive-pay-status"), "page url not be the same");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains("Positive Pay Status"), "positive pay status page header not be the same");
        staticWait(2000);
        serviceRequestPage.backButton.click();
        waitTillVisibilityElement(driver, serviceRequestPage.quickLinkInPositivePayPage);
        serviceRequestPage.selectAccount.click();
        serviceRequestPage.selectAccountNumberFromDD(fileReader.serviceRequestTestData.get("accountNumber")).click();
        positivePayChequeNumber = fileReader.serviceRequestTestData.get("chequeNumber");
        sendKeys(serviceRequestPage.positivePayChequeNumber, positivePayChequeNumber);
        staticWait(3000);
        int noOfTimeToInputChequeNo = 20;
        int chequeNumber = Integer.parseInt(fileReader.serviceRequestTestData.get("chequeNumber"));
        for (int i = 0; i <= noOfTimeToInputChequeNo; i++) {
            if (serviceRequestPage.validateChequeNumber.getAttribute("class").contains("inline-message")) {
                int currentChequeNumber = chequeNumber++;
                positivePayChequeNumber = "0000" + currentChequeNumber;
                sendKeys(serviceRequestPage.positivePayChequeNumber, positivePayChequeNumber);
                staticWait(2000);
            } else {
                break;
            }
        }
        staticWait(2000);
        if (serviceRequestPage.validateChequeNumber.getAttribute("class").contains("check-success")) {
            serviceRequestPage.enterAmountPositivePay.sendKeys(fileReader.serviceRequestTestData.get("amountCheque"));
            sendKeys(serviceRequestPage.enterPayeeNamePositivePay, fileReader.serviceRequestTestData.get("payeeName"));
        } else {
            logger.error("User need to enter correct cheque number");
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @Then("User verify the positive pay status page")
    public void userVerifyThePositivePayStatusPage() {
        softAssert = new SoftAssert();
        if (driver.getCurrentUrl().contains(TextAssertion.positivePayReviewPage)) {
            logger.info("The Entered cheque number already in processed so transfer to check page");
            clickOnButton(serviceRequestPage.positivePayBackButton);
            clickOnButton(homePage.raiseRequestButton);
            waitTillInvisibilityOfLoader(driver);

        } else if (driver.getCurrentUrl().contains(TextAssertion.positivePaySuccessPageUrl)) {
            positivePayStatus = serviceRequestPage.positivePayStatusSuccessful.isDisplayed();
            logger.info("Pay status page showing Successful");
            serviceRequestPage.newChequeStatusReferenceNumberCopyButton.click();
            softAssert.assertTrue(serviceRequestPage.positivePayStatusSuccessful.isDisplayed(), "Successfully copied message not displayed");
            positiveReferenceNumber = serviceRequestPage.newChequeStatusReferenceNumber.getText();
            softAssert.assertEquals(positiveReferenceNumber.trim(), getCopiedValue().trim(), "copied value doesn't match");
            logger.info("Positive Pay status Reference number : " + positiveReferenceNumber);
            serviceRequestPage.backToHomePageButton.click();
            waitTillInvisibilityOfLoader(driver);
//       } else {
//           logger.error("In Positive pay page proceed button not enabled so navigation to the next page is failed");
//           //  driver.navigate().back();
//           serviceRequestPage.backButton.click();
//           waitTillVisibilityElement(driver, serviceRequestPage.serviceRequestListSection);
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the positive pay review page")
    public void userVerifyThePositivePayReviewPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(serviceRequestPage.positivePayReviewPageAccount.getText().contains(fileReader.serviceRequestTestData.get("accountNumber")), "payee account number not be the same");
        softAssert.assertTrue(serviceRequestPage.positivePayReviewPageChequeNumber.getText().contains(positivePayChequeNumber), "cheque number not be the same");
        try {
            softAssert.assertTrue(serviceRequestPage.positivePayReviewPageAmount.getText().replaceAll(",", "").replace("₹", "").contains(fileReader.serviceRequestTestData.get("amountCheque") + ".00"), "entered amount not be the same");
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            Assert.fail("Please check screen shot and report");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMMM/yyyy");
        LocalDate dateFormatter = LocalDate.parse(getDate(), formatter);
        DateTimeFormatter finalFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        String chequeIssuanceDateFormatChanged = dateFormatter.format(finalFormatter);
        logger.info("Cheque Issuance Date " + chequeIssuanceDateFormatChanged);
        softAssert.assertEquals(serviceRequestPage.positivePayReviewPageIssuanceDate.getText().trim(), chequeIssuanceDateFormatChanged.trim(), "issuance date not be the same");
        softAssert.assertTrue(serviceRequestPage.positivePayReviewPagePayeeName.getText().contains(fileReader.serviceRequestTestData.get("payeeName")), "payee name not be the same");
        serviceRequestPage.proceedButton.click();
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks on view button by reference number")
    public void userClicksOnViewButtonByReferenceNumber() {
        waitTillInvisibilityOfLoader(driver);
        serviceRequestPage.serviceRequestViewButtonForm15(referenceNoForm15GPage).click();
        waitTillInvisibilityOfLoader(driver);
    }


    @And("User navigates to the service request home page")
    public void userNavigatesToTheServiceRequestHomePage() {
        dashboardPage.homePageLink.click();
        waitTillInvisibilityOfLoader(driver);
        homePage.raiseRequestButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User navigates to the service home page")
    public void userNavigatesToTheServiceHomePage() {
        if (driver.getCurrentUrl().contains("mr-dashboard")) {
            waitTillElementToBeClickable(driver, homePage.raiseRequestButton);
            clickOnButton(homePage.raiseRequestButton);
            waitTillInvisibilityOfLoader(driver);
        }
    }

    @And("User clicks on back to home page button")
    public void userClicksOnBackToHomePageButton() {
        try {
            serviceRequestPage.backToHomePageButton.click();
            waitTillVisibilityElement(driver, serviceRequestPage.serviceRequestListSection);
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Back to homepage button is not displayed");
        }
    }

    @When("User clicks on proceed button to navigates status page")
    public void userClicksOnProceedButtonToNavigatesStatusPage() {
        serviceRequestPage.proceedButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User enters payee name and clicks on proceed button")
    public void userEntersPayeeNameAndClicksOnProceedButton() {
        staticWait(2000);
        if (serviceRequestPage.validateChequeNumber.getAttribute("class").contains("check-success")) {
            serviceRequestPage.enterAmountPositivePay.sendKeys(fileReader.serviceRequestTestData.get("amountCheque"));
        } else {
            logger.error("User need to enter correct cheque number");
        }
        fluentWaitTillVisibilityElement(driver, 5, 1, serviceRequestPage.selectPaymentDate);
        chequeIssuanceDate = serviceRequestPage.getSelectedPaymentDate.getAttribute("value").trim();
        sendKeys(serviceRequestPage.enterPayeeNamePositivePay, fileReader.serviceRequestTestData.get("payeeName"));
        serviceRequestPage.proceedButton.click();
    }

    @When("User selects the type of request for track request services")
    public void userSelectsTheTypeOfRequestForTrackRequestServices() {
        serviceRequestPage.trackServiceReqVia(fileReader.serviceRequestTestData.get("trackServiceReqVia")).click();
        logger.info("User track the service request by " + fileReader.serviceRequestTestData.get("trackServiceReqVia"));
        if (fileReader.serviceRequestTestData.get("trackServiceReqVia").contains("Request Number")) {
            serviceRequestPage.enterReqNumber.sendKeys(referenceNoForm15GPage);
        } else {
            serviceRequestPage.selectReqType.click();
            serviceRequestPage.selectReqTypeFromDD(fileReader.serviceRequestTestData.get("reqType")).click();
            //From Date
            fluentWaitTillTheElementToBeClickable(driver, 7, 1, serviceRequestPage.fromDateCalendarButton);
            serviceRequestPage.fromDateCalendarButton.click();
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthYear);
            homePage.calendarMonthYear.click();
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthYear);
            homePage.calendarMonthYear.click();

            LocalDate trackReqFromDate = getPastDate(40);
            String fromDateTrackReq = getFormatedDate(trackReqFromDate, "dd/MMMM/yyyy");
            logger.info("Track Request From date  " + fromDateTrackReq);

            String[] expectDateArr = fromDateTrackReq.split("/");
            String expectedYear = expectDateArr[2].trim();
            String expectedMonth = expectDateArr[1].trim();
            String expectedDate = expectDateArr[0].trim();
            logger.info("Expected From Date " + expectedDate + "/" + expectedMonth + "/" + expectedYear);

            String actualYears = homePage.calendarMonthYear.getText().trim();

            String[] yearRange = actualYears.split("–");
            logger.info("Between Years " + actualYears);
            String startingYear = yearRange[0].trim();
            String endingYear = yearRange[1].trim();

            logger.info("Start Years " + startingYear);
            logger.info("End Years " + endingYear);

            while (true) {
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
            //To Year
            fluentWaitTillTheElementToBeClickable(driver, 7, 1, serviceRequestPage.fromDateCalendarButton);
            serviceRequestPage.toDateCalendarButton.click();
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, serviceRequestPage.toCalendarMonthYear);
            serviceRequestPage.toCalendarMonthYear.click();
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, serviceRequestPage.toCalendarMonthYear);
            serviceRequestPage.toCalendarMonthYear.click();


            LocalDate trackReqToDate = getToDayDate();
            String currentDate = getFormatedDate(trackReqToDate, "dd/MMMM/yyyy");
            logger.info("Today's date is " + currentDate);

            String[] expectToDateArr = currentDate.split("/");
            String expectedToYear = expectToDateArr[2].trim();
            String expectedToMonth = expectToDateArr[1].trim();
            String expectedToDate = expectToDateArr[0].trim();
            logger.info("Expected To Date " + expectedToDate + "/" + expectedToMonth + "/" + expectedToYear);

            String actualToYears = serviceRequestPage.toCalendarMonthYear.getText().trim();

            String[] yearToRange = actualToYears.split("–");
            logger.info("Between Years " + yearToRange);
            String startingToYear = yearToRange[0].trim();
            String endingToYear = yearToRange[1].trim();

            logger.info("Start Years " + startingToYear);
            logger.info("End Years " + endingToYear);

            while (true) {
                Year startYear = Year.parse(startingToYear);
                Year endYear = Year.parse(endingToYear);
                Year yearExpected = Year.parse(expectedToYear);

                if (yearExpected.isAfter(startYear) && yearExpected.isBefore(endYear)) {
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarYearSelect(expectedToYear));
                    clickOnButton(homePage.calendarYearSelect(expectedToYear));
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, homePage.calendarMonthSelect(expectedToMonth));
                    clickOnButton(homePage.calendarMonthSelect(expectedToMonth));
                    fluentWaitTillTheElementToBeClickable(driver, 5, 1, serviceRequestPage.calendarToDateSelect(expectedToDate));
                    clickOnButton(serviceRequestPage.calendarToDateSelect(expectedToDate));
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
        serviceRequestPage.submitButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @When("User clicks on add Or update nominee tab")
    public void userClicksOnAddOrUpdateNomineeTab() {
        fluentWaitTillTheElementToBeClickable(driver, 10, 1, serviceRequestPage.clickOnTab(TextAssertion.addNomineeTab));
        serviceRequestPage.clickOnTab(TextAssertion.addNomineeTab).click();
    }

    @Then("User verify account nominee page")
    public void userVerifyAccountNomineePage() {
        softAssert = new SoftAssert();
        //waitTillElementToBeClickable(driver,serviceRequestPage.selectAccountType);
        fluentWaitTillVisibilityElement(driver, 20, 5, serviceRequestPage.selectAccountType);
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.accountNomineeUrlPath), "difference in the URL path for the nominee page");
        softAssert.assertTrue(serviceRequestPage.pageHeader.getText().contains(TextAssertion.accountNomineePageHeader), "account nominee page header is not identical");
        softAssert.assertTrue(serviceRequestPage.selectAccountType.isDisplayed(), "select account type tab not displayed");
        softAssert.assertTrue(serviceRequestPage.selectAccountNumber.isDisplayed(), "select account number tab not displayed");
        softAssert.assertTrue(!serviceRequestPage.accountHolderName.getText().isEmpty(), "account holder's name not showing");
        softAssert.assertTrue(serviceRequestPage.updateOrAddNomineeButton.isDisplayed(), "add or update nominee button is not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on add Or update nominee button")
    public void userClicksOnAddOrUpdateNomineeButton() {
        //      fluentWaitTillTheElementToBeClickable(driver,20,5,serviceRequestPage.updateOrAddNomineeButton);
        staticWait(3000);
        serviceRequestPage.updateOrAddNomineeButton.click();

    }

    @Then("User verify the update nominee side sheet")
    public void userVerifyTheUpdateNomineeSideSheet() {
        softAssert = new SoftAssert();
        fluentWaitTillVisibilityElement(driver, 5, 1, serviceRequestPage.sideSheetNomineeCloseButton);
        if (serviceRequestPage.sideSheetHeader.getText().contains("Create New Nominee")) {
            logger.info("This user need add new nominee here");
        } else if (serviceRequestPage.sideSheetHeader.getText().contains("Update Nominee")) {
            logger.info("This user need updated the nominee here");
            fluentWaitTillTheElementToBeClickable(driver, 5, 1, serviceRequestPage.sideSheetCreateNomineeButton);
            serviceRequestPage.sideSheetCreateNomineeButton.click();
        }
        fluentWaitTillTheElementToBeClickable(driver, 5, 1, serviceRequestPage.sideSheetNomineeName);
        softAssert.assertTrue(serviceRequestPage.sideSheetNomineeName.isDisplayed(), "nominee name tab not displayed");
        softAssert.assertTrue(serviceRequestPage.sideSheetSelectRelationShip.isDisplayed(), "select relationship tab is not displayed");
        softAssert.assertTrue(serviceRequestPage.sideSheetNomineeDOB.isDisplayed(), "select relationship tab is not displayed");
        softAssert.assertTrue(serviceRequestPage.sideSheetNomineeMobileNumber.isDisplayed(), "mobile number tab is not displayed");
        softAssert.assertTrue(serviceRequestPage.sideSheetNomineeAddressLine1.isDisplayed(), "address line tab 1 is not displayed");
        softAssert.assertTrue(serviceRequestPage.sideSheetNomineeAddressLine2.isDisplayed(), "address line tab 2 is not displayed");
        softAssert.assertTrue(serviceRequestPage.sideSheetNomineePinCode.isDisplayed(), "pincode tab is not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User enter the nominee details")
    public void userEnterTheNomineeDetails() {
        serviceRequestPage.sideSheetNomineeName.sendKeys("Usos");
        serviceRequestPage.sideSheetSelectRelationShip.click();
        serviceRequestPage.sideSheetSelectRelationShipFromDD("Brother").click();
        serviceRequestPage.sideSheetNomineeMobileNumber.sendKeys("8345678424");
        scrollIntoView(driver, serviceRequestPage.sideSheetNomineeAddressLine1);
        serviceRequestPage.sideSheetNomineeAddressLine1.sendKeys("Tamilnadu");
        serviceRequestPage.sideSheetNomineeAddressLine2.sendKeys("Erode");
        serviceRequestPage.sideSheetNomineePinCode.sendKeys("638116");
//Set all details for verify on review page
        setNomineeName(serviceRequestPage.sideSheetNomineeName.getAttribute("value").trim());
        setNomineeRelationship(serviceRequestPage.sideSheetSelectRelationShip.getText().trim());
        setMobileNumber(serviceRequestPage.sideSheetNomineeMobileNumber.getAttribute("value"));
        setAddress(serviceRequestPage.sideSheetNomineeAddressLine1.getAttribute("value") + ", "
                + serviceRequestPage.sideSheetNomineeAddressLine2.getAttribute("value").trim() + ", " +
                serviceRequestPage.pinCodeAddress.getText().trim().replace(",", "-") + "-" +
                serviceRequestPage.enterPinCode.getAttribute("value").trim()
        );
        logger.info("Name  " + getNomineeName());
        logger.info("Relations " + getNomineeRelationship());
        logger.info("Address " + getMobileNumber());
        logger.info("Pincode address " + getAddress());
    }

    @Then("User clicks on proceed to review button")
    public void userClicksOnProceedToReviewButton() {
        fluentWaitTillTheElementToBeClickable(driver, 10, 1, serviceRequestPage.reviewToProceedButton);
        javaScriptExecutorClickElement(driver, serviceRequestPage.reviewToProceedButton);
    }

    @And("User validates the nominee review page")
    public void userValidatesTheNomineeReviewPage() {
        softAssert = new SoftAssert();
        try{
            softAssert.assertEquals(serviceRequestPage.nomineeReviewPageHeader.getText().trim(), TextAssertion.reviewNomineeSheetHeader, "nominee review sheet header is not identical");
        }
        catch (UnhandledAlertException ignore){}
        softAssert.assertEquals(serviceRequestPage.nomineeReviewPageHeader.getText().trim(), TextAssertion.reviewNomineeSheetHeader, "nominee review sheet header is not identical");
        softAssert.assertEquals(serviceRequestPage.detailsNomineeReviewPage("Nominee Name").getText().trim(), getNomineeName(), "nominee review sheet name is not identical");
        softAssert.assertEquals(serviceRequestPage.detailsNomineeReviewPage("Relationship").getText().trim(), getNomineeRelationship(), "nominee review sheet relationship is not identical");
        softAssert.assertEquals(serviceRequestPage.detailsNomineeReviewPage("Date of Birth").getText().trim(), getDate(), "nominee review sheet dob is not identical");
        String[] mobileNumberArr=serviceRequestPage.detailsNomineeReviewPage("Mobile number").getText().split("\\s");
        String mobileNumber= mobileNumberArr[1].trim();
        softAssert.assertEquals(mobileNumber, getMobileNumber(), "nominee review sheet mobile number is not identical");
        softAssert.assertEquals(serviceRequestPage.detailsNomineeReviewPage("Nominee Address").getText().trim().replaceAll("\\s",""), getAddress().replaceAll("\\s",""), "nominee review sheet mobile number is not identical");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User validate the navigation edit nominee details button")
    public void userValidateTheNavigationEditNomineeDetailsButton() {
        try {
            clickOnButton(serviceRequestPage.editButtonNomineeReviewPage);
        } catch (UnhandledAlertException ignore) {
            attachScreenshot(driver, scenario);
            logger.info("Ignore the Alerts");
            clickOnButton(serviceRequestPage.editButtonNomineeReviewPage);
        }
        fluentWaitTillVisibilityElement(driver, 5, 1, serviceRequestPage.sideSheetNomineeName);
        userVerifyTheUpdateNomineeSideSheet();
    }

    @And("User clicks on send otp button")
    public void userClicksOnSendOtpButton() {
        fluentWaitTillVisibilityElement(driver, 5, 1, serviceRequestPage.sendOtpButtonNomineeReviewPage);
        clickOnButton(serviceRequestPage.sendOtpButtonNomineeReviewPage);
    }

    @Then("User verify the nominee added popup")
    public void userVerifyTheNomineeAddedPopup() {
        softAssert = new SoftAssert();
        fluentWaitTillVisibilityElement(driver, 10, 1, serviceRequestPage.nomineeAddedStatusOnPopUp);
        if (serviceRequestPage.nomineeAddedStatusOnPopUp.getText().contains("ucessfully")) {
            softAssert.assertTrue(serviceRequestPage.nomineeAddedStatusOnPopUp.getText().contains("ucessfully"), "nominee added successfully message is not displayed");
            serviceRequestPage.newChequeStatusReferenceNumberCopyButton.click();
            String nomineeAddedReferenceNumber = getCopiedValue();
            softAssert.assertTrue(!nomineeAddedReferenceNumber.trim().isEmpty(), "nominee reference number not visible");
            logger.info("Nominee Added Reference Number is " + nomineeAddedReferenceNumber);
        } else {
            attachScreenshot(driver, scenario);
            clickOnButton(serviceRequestPage.cancelButton);
            softAssert.fail("Add or update new nominee is failed ,Please check the screenshot");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        try{
        clickOnButton(serviceRequestPage.nomineePopUpOkButton);}
        catch (NoSuchElementException e){

        }
    }

    @And("User validates the nominee's update on the nominee side sheet")
    public void userValidatesTheNomineeSUpdateOnTheNomineeSideSheet() {
        fluentWaitTillVisibilityElement(driver, 10, 1, serviceRequestPage.nomineeName);
        softAssert.assertTrue(serviceRequestPage.nomineeName.getText().contains(getNomineeName()), "nominee name is not identical");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }
}



