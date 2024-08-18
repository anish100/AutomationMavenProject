package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.mk_latn.No;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.testng.asserts.SoftAssert;
import pom.DashboardPage;
import pom.HomePage;
import pom.LogInPage;
import pom.ProfilePage;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import java.util.ListIterator;
import java.util.Random;


public class ProfileStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(ProfileStepDef.class);
    ProfilePage profilePage;
    DashboardPage dashboardPage;
    ServiceRequestStepDef serviceRequestStepDef;
    LogInPage logInPage;
    HomePageStepDef homePageStepDef;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String updatedUserName;
    String updatedPassword;

    public ProfileStepDef(TestContext context) {
        profilePage = context.getPageObjectManager().getProfilepage();
        dashboardPage = context.getPageObjectManager().getDashboardPage();
        logInPage = context.getPageObjectManager().getLogInPage();
        driver = context.getDriverManager().getWebDriver();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        serviceRequestStepDef = new ServiceRequestStepDef(context);
        homePageStepDef = new HomePageStepDef(context);

    }


    @Then("User user verify the profile corner popup")
    public void userUserVerifyTheProfileCornerPopup() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(!dashboardPage.accountHolderName.getText().isEmpty(), "account holder name not displayed");
        softAssert.assertTrue(!dashboardPage.cifNumber.getText().isEmpty(), "cif number not displayed");
        softAssert.assertTrue(profilePage.settingButton.isDisplayed(), "setting option not displayed");
        softAssert.assertTrue(logInPage.logOutInPage.isDisplayed(), "log out option not displayed");
        setAccountHolderName(dashboardPage.accountHolderName.getText());
        setAccountCifId(dashboardPage.cifNumber.getText());
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on setting button")
    public void userClicksOnSettingButton() {
        clickOnButton(profilePage.settingButton);
        waitTillInvisibilityOfLoader(driver);

    }

    @Then("User navigates to the setting page")
    public void userNavigatesToTheSettingPage() {
        softAssert = new SoftAssert();
//        softAssert.assertTrue(!profilePage.accountHolderNameSettingPage.getText().isEmpty(), "account holder name is not displayed");
//        softAssert.assertTrue(getAccountHolderName().trim().contains(profilePage.accountHolderNameSettingPage.getText().trim()), "account holder name not matched with profile corner up");
        String cifIdSettingPageSplit[] = profilePage.cifIdSettingPage.getText().split("-");
        String cifIdSettingPage = cifIdSettingPageSplit[1].trim().replaceAll("[\\D]", "").trim();
        String cifIdProfilePopUP = StringUtils.right(getAccountCifId(), 4).trim();
        softAssert.assertTrue(cifIdProfilePopUP.contains(cifIdSettingPage), "account cif id not matched with profile corner up ");
        profilePage.cifIdCopyButtonSettingPageAndProfilePage.click();
        staticWait(1000);
        softAssert.assertTrue(profilePage.copiedMessage.isDisplayed(), "copied message not displayed");
        if (profilePage.qrCollectionOptionInSettingPage.getText().contains("QR Collection Setting")) {
            logger.info("This user have QR code,so these options should displayed");
            softAssert.assertTrue(profilePage.storeDetailsButton.isDisplayed(), "store details option not displayed");
            softAssert.assertTrue(profilePage.settlementSettingButton.isDisplayed(), " settlement setting option not displayed");
            try {
                softAssert.assertTrue(profilePage.manageQRButton.isDisplayed(), "manage RQ option not displayed");
            } catch (NoSuchElementException e) {
                //   attachScreenshot(driver, scenario);
                logger.debug("Manage QR option not provided");
            }
        } else {
            logger.info("This user doesn't have QR code,so these options not enabled");
        }
        softAssert.assertTrue(profilePage.backButton.isDisplayed(), "back button not displayed in setting page");
        softAssert.assertTrue(profilePage.changePasswordButton.isDisplayed(), "change password option not displayed");
        softAssert.assertTrue(profilePage.devicesButton.isDisplayed(), "devices option not displayed");
        softAssert.assertTrue(profilePage.notificationButton.isDisplayed(), "notification option not displayed");
        softAssert.assertTrue(profilePage.languageButton.isDisplayed(), "language option not displayed");
        softAssert.assertTrue(profilePage.aboutAUBizButton.isDisplayed(), "about AU Biz option not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the navigation details in setting page")
    public void userVerifyTheNavigationDetailsInSettingPage() {
        ListIterator<WebElement> daysToggles = null;
        ListIterator<WebElement> daysNameList = null;
        softAssert = new SoftAssert();
        for (int i = 1; i <= profilePage.settingPageOptionsList.size(); i++) {
            staticWait(3000);
            scrollIntoView(driver, profilePage.settingPageListText(i));
            String extractedListName = profilePage.settingPageListText(i).getText();
            /** Clicking the consecutive navigation button **/
            logger.info("User verify the option is " + extractedListName);
            staticWait(2000);
            if (extractedListName.isEmpty()) {
                logger.error("Element contains nothing");
            } else {
                profilePage.settingPageNavigationArrow(extractedListName).click();
                //    profilePage.settingPageListText(i).click();
                waitTillInvisibilityOfLoader(driver);
                scrollIntoViewUp(driver, profilePage.backButton);
                staticWait(2000);
                switch (extractedListName) {
                    case "Store Details":
                        softAssert.assertTrue(driver.getCurrentUrl().contains("profile/stoe-detail"), "");
                        softAssert.assertTrue(profilePage.pageHeader.getText().contains("Store Details"), "page header name not matched");
                        /**User validate the profile image function**/
                        userClicksOnAddImageOption();
                        userVerifyTheAddImagePopUp();
                        userVerifyTheAddImageFunction();
                        /********Store Update Mobile Number*******/
                        profilePage.mobileNumberUpdateButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        softAssert.assertTrue(profilePage.mobileUpdateStore.isDisplayed(), "update mobile number for store pop up is displayed");
                        Random random = new Random(System.currentTimeMillis());
                        int randomGenLast9digit = 100000000 + random.nextInt(900000000);
                        logger.info("Randomly Generate last 5 digit no" + randomGenLast9digit);
                        String randomlyGenerateMobileNo = "6" + Integer.toString(randomGenLast9digit);
                        profilePage.mobileUpdateStore.sendKeys(Keys.chord(Keys.CONTROL, "a"), randomlyGenerateMobileNo);
                        logger.info("Updated Store Mobile Number " + randomlyGenerateMobileNo);
                        profilePage.submitButton.click();
                        staticWait(2000);
                        if (profilePage.toastMessage.getText().contains("Something went wrong")) {
                            attachScreenshot(driver, scenario);
                            softAssert.fail("Store mobile number update failed");
                            try{
                                profilePage.cancelButton.click();
                            }
                            catch (NoSuchElementException exception){ }
                        }
                        waitTillInvisibilityOfLoader(driver);
//                        profilePage.cancelButton.click();
                        /***Store mobile number poup up***/
                        profilePage.emailIDUpdateButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        softAssert.assertTrue(profilePage.emailUpdateStore.isDisplayed(), "update mobile number for store pop up is displayed");
                        profilePage.emailUpdateStore.sendKeys(Keys.chord(Keys.CONTROL, "a"), "soundararajan.2911@gmail.com");
                        profilePage.submitButton.click();
                        staticWait(2000);
                        if (profilePage.toastMessage.getText().contains("Something went wrong")) {
                            attachScreenshot(driver, scenario);
                            softAssert.fail("Store mobile number update failed");
                            try{
                                profilePage.cancelButton.click();
                            }
                            catch (NoSuchElementException exception){ }
                        }
                        waitTillInvisibilityOfLoader(driver);
                        waitTillElementToBeClickable(driver, profilePage.annualIncomeExpected);
                        /** Annual Income Expected**/
                        profilePage.annualIncomeExpected.click();
                        waitTillVisibilityElement(driver, profilePage.sideBarPageHeader);
                        softAssert.assertTrue(profilePage.sideBarPageHeader.getText().contains("Select Expected Annual Income"), "expected income side header is not matched");
                        softAssert.assertTrue(profilePage.cancelButton.isDisplayed(), "cancel button not displayed");
                        softAssert.assertTrue(profilePage.saveButton.isDisplayed(), "save button not displayed");
                        softAssert.assertTrue(profilePage.rightSideBarCloseButton.isDisplayed(), "close button not displayed");
                        //***Select Particular value***/
                        profilePage.chooseAmountRadioButton(fileReader.profileTestData.get("selectExpectIncome")).click();
                        waitTillElementToBeClickable(driver, profilePage.saveButton);
                        profilePage.saveButton.click();
                        waitTillInvisibilityOfLoader(driver);

                        logger.info(profilePage.annualExpectedIncomeExpectedText.getText());
                        logger.info(profilePage.annualExpectedIncomeExpectedText.getAttribute("innerHTML"));
                        //     logger.info(profilePage.annualExpectedIncomeExpectedText.getAttribute("value"));
                        softAssert.assertTrue(!profilePage.annualExpectedIncomeExpectedText.getText().isEmpty(), "annual expected income not showing");
                        softAssert.assertEquals(profilePage.annualExpectedIncomeExpectedText.getText().trim(), fileReader.profileTestData.get("selectExpectIncome").trim(), "value not be the same");
                        try {
                            softAssert.assertAll();
                        } catch (AssertionError e) {
                            attachScreenshot(driver, scenario);
                            scenario.log(e.toString());
                            setErrorsInList(e.toString());
                        }
                        //   profilePage.rightSideBarCloseButton.click();
                        /** Annual Turnover Expected**/
                        profilePage.annualTurnOverExpected.click();
                        waitTillVisibilityElement(driver, profilePage.sideBarPageHeader);
                        logger.info(profilePage.sideBarPageHeader.getText());
                        // softAssert.assertTrue(profilePage.sideBarPageHeader.getText().contains("Select Expected Annual Turnover"), "expected turnover side header is not matched");
                        softAssert.assertEquals(profilePage.sideBarPageHeader.getText(), "Select Expected Annual Turnover", "expected turnover side header is not matched");
                        softAssert.assertTrue(profilePage.cancelButton.isDisplayed(), "cancel button not displayed");
                        softAssert.assertTrue(profilePage.saveButton.isDisplayed(), "save button not displayed");
                        softAssert.assertTrue(profilePage.rightSideBarCloseButton.isDisplayed(), "close button not displayed");
                        profilePage.chooseAmountRadioButton(fileReader.profileTestData.get("selectExpectTurnOver")).click();
                        waitTillElementToBeClickable(driver, profilePage.saveButton);
                        profilePage.saveButton.click();
                        waitTillInvisibilityOfLoader(driver);

                        softAssert.assertTrue(!profilePage.annualTurnOverExpectedText.getText().isEmpty(), "annual expected turnover not showing");
                        softAssert.assertEquals(profilePage.annualTurnOverExpectedText.getText().trim(), fileReader.profileTestData.get("selectExpectTurnOver").trim(), "value not be the same");
                        //   profilePage.rightSideBarCloseButton.click();
                        try {
                            softAssert.assertAll();
                        } catch (AssertionError e) {
                            attachScreenshot(driver, scenario);
                            scenario.log(e.toString());
                            setErrorsInList(e.toString());
                        }
                        /** Working Hours **/
                        profilePage.workingHourEditButton.click();
                        waitTillVisibilityElement(driver, profilePage.sideBarPageHeader);
                        softAssert.assertTrue(profilePage.sideBarPageHeader.getText().contains("Edit Working Hours"), "working hour side header is not matched");
                        softAssert.assertTrue(profilePage.cancelButton.isDisplayed(), "cancel button not displayed");
                        softAssert.assertTrue(profilePage.saveButton.isDisplayed(), "save button not displayed");
                        softAssert.assertTrue(profilePage.rightSideBarCloseButton.isDisplayed(), "close button not displayed");
                        /***Verifying Working hour list***/
                        profilePage.workingHourOpenAtDropDownButton.click();
                        profilePage.selectOpenTime(fileReader.profileTestData.get("shopOpensAt")).click();
                        waitTillElementToBeClickable(driver, profilePage.workingHourCloseAtDropDownButton);
                        profilePage.workingHourCloseAtDropDownButton.click();
                        profilePage.selectCloseTime(fileReader.profileTestData.get("shopClosesAt")).click();
                        staticWait(1000);
                        profilePage.open24HoursToggle.click();
                        staticWait(1000);
                        softAssert.assertEquals(profilePage.workingHourOpenAtText.getText(), "00:00 am", "after select 24 hours shop open time not showing");
                        softAssert.assertEquals(profilePage.workingHourClosesAtText.getText(), "11:59 pm", "after select 24 hours shop closes time not showing");
                        profilePage.allDaySelectBox.click();
                        daysToggles = profilePage.daysToggles.listIterator();
                        daysNameList = profilePage.daysTogglesInText.listIterator();
                        if (profilePage.allDaySelectBox.isSelected()) {
                            staticWait(2000);
                            while (daysToggles.hasNext() && daysNameList.hasNext()) {
                                WebElement dayToggleIterate = daysToggles.next();
                                WebElement daysNames = daysNameList.next();
                                String currentDay = daysNames.getText();
                                String daysTogglesStatus = dayToggleIterate.getAttribute("class");
                                logger.info("Iteration is on " + currentDay + " and status of the toggle is " + daysTogglesStatus);
                                softAssert.assertTrue(daysTogglesStatus.contains("knob_sm active_sm"), "after selecting all day toggle " + currentDay + " toggle is in active state");
                            }
                            for (WebElement eachDayToggle : profilePage.daysToggles) {
                                String toggle = eachDayToggle.getAttribute("class");
                                softAssert.assertTrue(toggle.contains("knob_sm active_sm"), "after selecting all day toggle is not in active state");
                            }
                        }

//Deselect All day Check box
                        profilePage.allDaySelectBox.click();
                        if (!profilePage.allDaySelectBox.isSelected()) {
                            staticWait(2000);
                            ListIterator<WebElement> unSelectDaysToggles = profilePage.daysToggles.listIterator();
                            ListIterator<WebElement> unSelectDaysNameList = profilePage.daysTogglesInText.listIterator();
                            while (unSelectDaysToggles.hasNext() && unSelectDaysNameList.hasNext()) {
                                WebElement dayToggleIterate = unSelectDaysToggles.next();
                                WebElement daysNames = unSelectDaysNameList.next();
                                String currentDay = daysNames.getText();
                                String daysTogglesStatus = dayToggleIterate.getAttribute("class");
                                logger.info("Iteration is on " + currentDay + " and status of the toggle is " + daysTogglesStatus);
                                softAssert.assertTrue(daysTogglesStatus.contains("knob_sm"), "after de-selecting day toggle " + currentDay + " toggle is active state");
                            }
                        }
                        profilePage.allDaySelectBox.click();
                        staticWait(2000);
                        if (!profilePage.saveButton.isEnabled()) {
                            profilePage.allDaySelectBox.click();
                            logger.info("Due to Disable save button we need to enable");
                        }
                        boolean isAllBoxSelected = profilePage.allDaySelectBox.isSelected();
                        profilePage.saveButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        softAssert.assertTrue(!profilePage.workingHourContainsText.getText().isEmpty(), "working hours details not showing");
//                           lo
//                            for (WebElement eachDayToggle : profilePage.daysToggles) {
//                                String toggle = eachDayToggle.getAttribute("class");
//                                softAssert.assertTrue(toggle.contains("knob_sm"), "after de selecting all day toggle is active state");
//                            }
                        if (isAllBoxSelected == true) {
                            softAssert.assertTrue(profilePage.activeDayListInProfilePage.size() == 7, "all days are not selected");
                        }
                        waitTillElementToBeClickable(driver, profilePage.backButton);
                        profilePage.backButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        try {
                            softAssert.assertAll();
                        } catch (AssertionError e) {
                            attachScreenshot(driver, scenario);
                            scenario.log(e.toString());
                            setErrorsInList(e.toString());
                        }
                        break;
                    case "Settlement Settings":
                        softAssert.assertTrue(driver.getCurrentUrl().contains("settlement/settings"), "settlement page url path not matched");
                        softAssert.assertTrue(profilePage.pageHeader.getText().contains("Settlement Settings"), "settlement page header not matched");
                        softAssert.assertTrue(profilePage.settlementSettingSelectVPA.isDisplayed(), "select VPA option not displayed");
                        softAssert.assertTrue(!profilePage.settlementAccountRadioButton.isEmpty(), "settlement setting account not displayed");
                        softAssert.assertTrue(profilePage.addAnotherAccount.isDisplayed(), "add another account button not displayed");
                        softAssert.assertTrue(!profilePage.settlementCycleTimeRadioButtonList.isEmpty(), "settlement cycle radio button not displayed");
                        softAssert.assertTrue(!profilePage.settlementCycleTimeRadioButtonList.isEmpty(), "settlement cycle radio button not displayed");
                        int noCycleType = profilePage.settlementCycleTimeRadioButtonList.size();
                        String checkAvailabilityOfReal= noCycleType == 3 ? "real time option available" : "real time option not available";
                        logger.info("for this user "+checkAvailabilityOfReal);
                        if (profilePage.settlementCycleRotateIcon.getAttribute("class").contains("up")) {
                            profilePage.settlementCycleRotateIcon.click();
                            for (int k = 0; k< noCycleType; k++) {
                                WebElement typeOfSettlement = profilePage.settlementCycleTimeRadioButtonList.get(k);
                                if (k == 0) {
                                    String selectedSettlementSettingType = profilePage.settlementSettingPageSelectedSettlementType.getText();
                                    softAssert.assertTrue(typeOfSettlement.isSelected(), "settlement type first element not selected");
                                    logger.info("Settlement selected type is " + selectedSettlementSettingType);
                                }
                            }
                        }
                        profilePage.backButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        break;
                    case "Manage QR":
                        break;
                    case "Change Password":
                        softAssert.assertTrue(profilePage.pageHeader.getText().contains("Change Password"), "page header name not matched");
                        if (profilePage.startFaceMatchButton.getText().contains("Proceed")) {
                            scrollIntoViewUp(driver, profilePage.pageHeader);
                            softAssert.assertTrue(driver.getCurrentUrl().contains("profile/change-password/verify-yourself"), extractedListName + " page url not matched");
                            softAssert.assertTrue(profilePage.verifyingDebitCardInPasswordPage.isDisplayed(), "verifying by debit card page not displayed");
                            softAssert.assertTrue(profilePage.verifyingFaceMatchPasswordPage.isDisplayed(), "verifying by face match option not displayed");
                            softAssert.assertTrue(profilePage.debitCardRadioButton.isDisplayed(), "debit card radio button is not displayed");
                            softAssert.assertTrue(profilePage.faceButtonRadioButton.isDisplayed(), "face match radio button is not displayed");
                            softAssert.assertFalse(profilePage.proceedButton.isEnabled(), "without selecting options radio button proceed button is enabled");
                            profilePage.debitCardRadioButton.click();
                            profilePage.proceedButton.click();
                            waitTillInvisibilityOfLoader(driver);
                            softAssert.assertTrue(driver.getCurrentUrl().contains("change-password/debit-cards-listing"), "change password by using card url path not matched");
                            softAssert.assertTrue(profilePage.proceedButton.isDisplayed(), "proceed button is not displayed");
                            //  softAssert.assertFalse(profilePage.proceedButton.isEnabled(),"with out selecting any debit card proceed button should not enabled");
                            try {
                                profilePage.debitCardListingPageRadioButton.click();
                            } catch (NoSuchElementException e) {
                                attachScreenshot(driver, scenario);
                                softAssert.fail("Debit card listing not showing,Please verify the screen shot");
                                logger.error(e.toString());
                            }
                            //      softAssert.assertTrue(profilePage.proceedButton.isEnabled(),"after selecting debit card proceed button not enabled");
/**Here doesn't verify the password details by credit card**/
                            profilePage.backButton.click();
                            waitTillInvisibilityOfLoader(driver);
                            profilePage.settingPageNavigationArrow(extractedListName).click();
                            waitTillInvisibilityOfLoader(driver);
                            profilePage.faceButtonRadioButton.click();
                            profilePage.proceedButton.click();
                        } //else {
                        softAssert.assertTrue(driver.getCurrentUrl().contains("profile/change-password/photo-auth"), extractedListName + " page url not matched");
                        logger.info("This user doesn't have debit card ,So verification by face match option only available");
                        softAssert.assertTrue(profilePage.startFaceMatchButton.getText().contains("Start Face Match"), "start face button not displayed");
                        profilePage.startFaceMatchButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        softAssert.assertTrue(profilePage.faceMatchPopUpHeader.isDisplayed(), "face match pop up not displayed");
                        softAssert.assertTrue(profilePage.cancelButton.isDisplayed(), "cancel button not displayed in face match popup");
                        softAssert.assertTrue(profilePage.faceMatchPopUpCapturePhotoButton.isDisplayed(), "capture button not displayed in face match pop up");
                        staticWait(1000);
                        profilePage.cancelButton.click();
                        //   }
                        clickOnButton(profilePage.backButton);
                        waitTillInvisibilityOfLoader(driver);
                        break;
                    case "Devices":
                        softAssert.assertTrue(driver.getCurrentUrl().contains("profile/manage-devices"), extractedListName + " page url not matched");
                        softAssert.assertTrue(profilePage.pageHeader.getText().contains("Devices"), "devices page header not matched");
                        clickOnButton(profilePage.backButton);
                        waitTillInvisibilityOfLoader(driver);
                        break;
                    case "Notification Settings":
                        softAssert.assertTrue(driver.getCurrentUrl().contains("profile/notification-settings"), extractedListName + " page url not matched");
                        softAssert.assertTrue(profilePage.pageHeader.getText().contains("Notifications"), "page header not matched");
                        softAssert.assertTrue(profilePage.infoMessageNotificationPage.getText().contains("Payment & OTP related notifications cannot be disabled"), "information message not matched");
                        for (WebElement toggleListInNotificationPage : profilePage.listNotificationPage) {
                            String notificationProvideName = toggleListInNotificationPage.getText();
                            staticWait(2000);
                            profilePage.notificationToggle(notificationProvideName).click();
                            staticWait(2000);
                            softAssert.assertTrue(profilePage.updatedMessage.isDisplayed(), notificationProvideName + " updated message not displayed");
                            staticWait(2000);
                            if (profilePage.notificationToggle(notificationProvideName).getAttribute("class").contains("knob_sm")) {
                                logger.info(notificationProvideName + " toggle is inactive");
                                profilePage.notificationToggle(notificationProvideName).click();
                                staticWait(2000);
                                softAssert.assertTrue(profilePage.updatedMessage.isDisplayed(), notificationProvideName + " updated message showing empty");
                            }
                            //    softAssert.assertTrue(profilePage.cancelButton.isDisplayed(), "cancel button is not displayed");
                            //    softAssert.assertTrue(profilePage.saveButton.isDisplayed(), "save button is not displayed");

                        }
                        profilePage.backButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        /**User navigates to setting page**/
                        break;
//                    case "Language":
//                        softAssert.assertTrue(!profilePage.availableLanguageInLanguage.isEmpty(), "language name not displayed");
//                        ListIterator<WebElement> languageList = profilePage.availableLanguageInLanguage.listIterator();
//                        while (languageList.hasNext()) {
//                            WebElement languageName = languageList.next();
//                            softAssert.assertTrue(!languageName.getText().isEmpty(), "language names not present in the list");
//                        }
//                        softAssert.assertTrue(profilePage.cancelButton.isDisplayed(), "cancel button not displayed");
//                        softAssert.assertTrue(profilePage.saveButton.isDisplayed(), "save button not displayed");
//                        softAssert.assertTrue(profilePage.rightSideBarCloseButton.isDisplayed(), "close button not displayed");
//                        //        profilePage.saveButton.click();
//                        profilePage.rightSideBarCloseButton.click();
//                        break;
                    case "About AU 0101 Business":
                        softAssert.assertTrue(profilePage.pageHeader.getText().contains("About AU 0101 Business"), "about AU Biz page header name not matched");
                        for (WebElement listInAboutAuBiz : profilePage.aboutAUBizList) {
                            String listInAboutAuBizText = listInAboutAuBiz.getText();
                            String aboutAUBizWindow = driver.getWindowHandle();
                            logger.info("User going to click " + aboutAUBizWindow);
                            profilePage.settingPageNavigationArrow(listInAboutAuBizText).click();
                            waitTillInvisibilityOfLoader(driver);
                            for (String windowHandle : driver.getWindowHandles()) {
                                if (!windowHandle.equals(aboutAUBizWindow)) {
                                    driver.switchTo().window(windowHandle);
                                    if (listInAboutAuBizText.contains("Privacy & Policy")) {
                                        softAssert.assertTrue(driver.getCurrentUrl().contains("privacy-policy"), "path of the url is not matched");
                                        softAssert.assertTrue(profilePage.privacyPolicyPageHeader.getText().contains("PRIVACY POLICY"), "privacy policy page header not displayed");
                                    } else if (listInAboutAuBizText.contains("Terms of Use")) {
                                        softAssert.assertTrue(driver.getCurrentUrl().contains("terms-and-conditions"), "path of the url is not matched");
                                        softAssert.assertTrue(profilePage.termsAndConditionPageHeader.getText().contains("Terms & Conditions"), "privacy policy page header not displayed");
                                    } else {
                                        softAssert.assertTrue(driver.getCurrentUrl().contains("about-us"), "path of the url is not matched");
                                    }
                                    driver.close();
                                    driver.switchTo().window(aboutAUBizWindow);

                                }
                            }

                        }
                        profilePage.backButton.click();
                        waitTillInvisibilityOfLoader(driver);
                        break;
                }
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

    @When("User clicks on profile setting button")
    public void userClicksOnProfileSettingButton() {
        waitTillVisibilityElement(driver, profilePage.profileSettingButton);
        profilePage.profileSettingButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User navigates to the profile setting page")
    public void userNavigatesToTheProfileSettingPage() {
        softAssert = new SoftAssert();
        try {
            waitTillVisibilityElement(driver, profilePage.backButton);
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.profileSettingPageUrlPath), "profile setting page url is not matched");
            softAssert.assertTrue(profilePage.backButton.isDisplayed(), "back button not displayed in setting page");
            String cifIdSettingPageSplit[] = profilePage.cifIdSettingPage.getText().split("-");
            String cifIdSettingPageStr = cifIdSettingPageSplit[1].trim().replaceAll("[^\\d]", "").trim();
            //      String cifIdSettingPage=StringUtils.right(cifIdSettingPageStr,4).trim();
            String cifIdProfilePopUP = StringUtils.right(getAccountCifId(), 4).trim();
            logger.info("Cif id in Profile pop up :" + cifIdProfilePopUP);
            logger.info("Cif id in Profile Setting Page :" + cifIdSettingPageStr);
            //     logger.info("Cif id in Profile Setting Page :"+cifIdSettingPage);
            softAssert.assertEquals(cifIdProfilePopUP, cifIdSettingPageStr, "account cif id not matched with profile corner up ");
            profilePage.cifIdCopyButtonSettingPageAndProfilePage.click();
            softAssert.assertTrue(profilePage.copiedMessage.isDisplayed(), "copied message not displayed");
            softAssert.assertTrue(profilePage.addImageButton.isDisplayed(), "add image option is not displayed");
            /**Perosnal Details Section**/
            softAssert.assertTrue(!profilePage.dateOfBirth.getText().isEmpty(), "date of birth details not displayed");
            softAssert.assertTrue(!profilePage.userName.getText().isEmpty(), "username not displayed");
            softAssert.assertTrue(profilePage.userNameUpdateButton.isDisplayed(), "update user name button not displayed");
            softAssert.assertTrue(!profilePage.panNumber.getText().isEmpty(), "PAN number not displayed");
            softAssert.assertTrue(!profilePage.aadhaarNumber.getText().isEmpty(), "aadhaar number not displayed");
            scrollIntoView(driver, profilePage.cKYCNumber);
            staticWait(2000);
            softAssert.assertTrue(!profilePage.cKYCNumber.getText().isEmpty(), "cKYC number not displayed");
            softAssert.assertTrue(profilePage.cKYCNumberViewButton.isDisplayed(), "cKYC Number button not displayed");
            profilePage.cKYCNumberViewButton.click();
            homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
            staticWait(2000);
            logger.info(profilePage.cKYCNumber.getText());
            logger.info(profilePage.cKYCNumber.getAttribute("innerHTML"));
            logger.info(profilePage.cKYCNumber.getAttribute("value"));


            softAssert.assertFalse(profilePage.cKYCNumber.getText().contains("xxxx"), "cKYC details still hidden");
            softAssert.assertTrue(profilePage.cKYCNumberViewButton.getText().contains("Hide"), "after clicks on view button ,hidden option not showing");
            //    softAssert.assertTrue(profilePage.cKYCNumber.getText().matches("\\d+"),"cKYC contains non numeric characters");
        } catch (NoSuchElementException noElements) {
            noElements.toString();
            softAssert.fail("This elements are missing " + noElements.getMessage());
        }
        /** Business Details **/
        softAssert.assertTrue(!profilePage.mobileNumber.getText().isEmpty(), "mobile number not displayed");
        softAssert.assertTrue(profilePage.mobileNumberUpdateButton.isDisplayed(), "mobile number update button not displayed");
        softAssert.assertTrue(!profilePage.emailId.getText().isEmpty(), "email id not displayed");
        softAssert.assertTrue(profilePage.emailIDUpdateButton.isDisplayed(), "update email id number update button not displayed");
        softAssert.assertTrue(!profilePage.registeredAddress.getText().isEmpty(), "registered address not displayed");
        softAssert.assertTrue(!profilePage.communicationAddress.getText().isEmpty(), "registered address not displayed");
        softAssert.assertTrue(profilePage.communicationAddressUpdateButton.isDisplayed(), "update email id number update button not displayed");
        softAssert.assertTrue(!profilePage.homeBranchAddress.getText().isEmpty(), "registered address not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on add image option")
    public void userClicksOnAddImageOption() {
        scrollIntoViewUp(driver, profilePage.addImageButton);
        profilePage.addImageButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the add image pop up")
    public void userVerifyTheAddImagePopUp() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(profilePage.addImagePopUpHeader.getText().contains("Add Image"), "add image pop up header not matched");
        //    softAssert.assertTrue(profilePage.labelInputImage.isDisplayed(),"upload image option not displayed");
        softAssert.assertTrue(profilePage.cancelButton.isDisplayed(), "cancel button not displayed in the add image pop up");
        softAssert.assertTrue(profilePage.confirmButton.isDisplayed(), "confirm button not displayed in the add image pop up");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the add image function")
    public void userVerifyTheAddImageFunction() {
        softAssert = new SoftAssert();
        staticWait(2000);
        profilePage.labelInputImage.sendKeys("C:\\Users\\987993\\Merchant_Web_Automation\\src\\main\\resources\\data\\DemoImage.jpg");
        waitTillVisibilityElement(driver, profilePage.confirmButton);
        softAssert.assertTrue(profilePage.uploadedProfileImageStatus.isDisplayed(), "uploaded image information not showing");
        staticWait(2000);
        javaScriptExecutorClickElement(driver, profilePage.confirmButton);
        waitTillVisibilityElement(driver, profilePage.toastMessage);
        softAssert.assertTrue(profilePage.toastMessage.getText().contains("uccessfully."), "profile image uploaded failed");
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User verify the update user name function")
    public void userVerifyTheUpdateUserNameFunction() {
        softAssert = new SoftAssert();
        /****For here we check the user name function Soundar 2025 to Soundar2024 ***/
        String currentUserName = profilePage.userName.getText();
        profilePage.userNameUpdateButton.click();
        staticWait(3000);
        /***Verify the update username page***/
        if (driver.getCurrentUrl().contains(TextAssertion.profileUpdateUserNamePageUrlPath)) {
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.profileUpdateUserNamePageUrlPath), "update user name url path not matched");
            softAssert.assertTrue((profilePage.pageHeader.getText().toLowerCase().contains(TextAssertion.profileUpdateUserNamePageHeader.toLowerCase())), "update user name page header is not matched");
            softAssert.assertTrue(profilePage.backButton.isDisplayed(), "update user name page back button is not displayed");
            softAssert.assertTrue(profilePage.enterUpdatedUserName.isDisplayed(), "enter user name tab not displayed");
            softAssert.assertFalse(profilePage.nextButton.isEnabled(), "without enter user name next button enabled");
            for (WebElement infoMessage : profilePage.informationMessageForUpdateMessage) {
                String info = infoMessage.getText();
                logger.info("info");
                if (info.contains("5 character minimum")) {
                    logger.info(info + "  message displayed");
                } else if (info.contains("At least one number")) {
                    logger.info(info + "  message displayed");
                } else if (info.contains("At least one character")) {
                    logger.info(info + "  message displayed");
                } else {
                    attachScreenshot(driver, scenario);
                    softAssert.fail("In update user name page information details not displayed");
                }
            }
            Random randomUser = new Random();
            int randomDigit = randomUser.nextInt(900) + 100;
            updatedUserName = "Soundaretb" + randomDigit;
            profilePage.enterUpdatedUserName.sendKeys(Keys.chord(Keys.CONTROL, "a"), updatedUserName);
//            if (currentUserName.equalsIgnoreCase(fileReader.profileTestData.get("userNameUpdate"))) {

//            } else if (currentUserName.equalsIgnoreCase(fileReader.profileTestData.get("newUserNameUpdate"))) {
//                profilePage.enterUpdatedUserName.sendKeys(fileReader.profileTestData.get("userNameUpdate"));
//            }
            setUserNameForLogIn(updatedUserName);
            softAssert.assertTrue(profilePage.nextButton.isEnabled(), "after entered user name next button not enabled");
            String sheetName = "ProfilePage";
            String columnName = "updatedUserNameETB";
            inputValueInExcel(sheetName, columnName, fileReader.profileTestData.get("updatedUserNameETB"), updatedUserName);
            logger.info("Updated User name for ETB " + updatedUserName);
            clickOnButton(profilePage.nextButton);
        } else {
            /**********Verify error message of NTB USER NAME************/
            profilePage.updateUserNameNTB.sendKeys(Keys.chord(Keys.CONTROL, "a"), "soun");
            waitTillVisibilityElement(driver, profilePage.updateNTBUserNameErrorMessages);
            softAssert.assertTrue(profilePage.updateNTBUserNameErrorMessages.getText().contains("character minimum"), "enter minimum character message not displayed");
            profilePage.updateUserNameNTB.sendKeys(Keys.chord(Keys.CONTROL, "a"), "soundar");
            waitTillVisibilityElement(driver, profilePage.updateNTBUserNameErrorMessages);
            softAssert.assertTrue(profilePage.updateNTBUserNameErrorMessages.getText().contains("At least one number"), "enter at least one digit character message not displayed");

            Random random = new Random();
            int randomDigit = random.nextInt(900) + 100;
            String updatedUserName = "SoundarNtb" + String.valueOf(randomDigit);
            String sheetName = "ProfilePage";
            String columnName = "userNameUpdateNTB";
            String existingUserName = fileReader.profileTestData.get("userNameUpdateNTB");
            inputValueInExcel(sheetName, columnName, existingUserName, updatedUserName);
            staticWait(2000);
            profilePage.updateUserNameNTB.sendKeys(Keys.chord(Keys.CONTROL, "a"), updatedUserName);
            setUserNameForLogIn(updatedUserName);
            logger.info("Update User Name for NTB  " + updatedUserName);
            softAssert.assertTrue(profilePage.cancelButton.isDisplayed(), "cancel button is not displayed");
            softAssert.assertTrue(profilePage.submitButton.isDisplayed(), "proceed button is not displayed");
        }
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the update user name popup")
    public void userVerifyTheUpdateUserNamePopup() {
        softAssert = new SoftAssert();
        if (driver.getCurrentUrl().contains("profile/profile-setting")) {
            profilePage.submitButton.click();
            waitTillInvisibilityOfLoader(driver);
        } else {
            softAssert.assertTrue(profilePage.updatedMessage.isDisplayed(), "user name updated message not displayed");
            softAssert.assertTrue(profilePage.okButton.isDisplayed(), "okay button not displayed");
            profilePage.okButton.click();
        }
        waitTillVisibilityElement(driver, profilePage.userName);
//        softAssert.assertTrue(profilePage.userName.getText().toLowerCase().trim().equalsIgnoreCase(updatedUserName.toLowerCase().trim()));

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the update mobile number function")
    public void userVerifyTheUpdateMobileNumberFunction() {
        softAssert = new SoftAssert();
        staticWait(3000);
        String profileSettingPageWindow = driver.getWindowHandle();
        profilePage.mobileNumberUpdateButton.click();
        waitTillInvisibilityOfLoader(driver);
        for (String mobileNumberUpdateWindow : driver.getWindowHandles()) {
            if (!mobileNumberUpdateWindow.equals(profileSettingPageWindow)) {
                driver.switchTo().window(mobileNumberUpdateWindow);
                try {
                    // waitTillVisibilityElement(driver, profilePage.enterMobileNumberUpdatePage);
                    softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.profileUpdateMobileNumberWindowUrlPath), "mobile number update page url not matched");
                    softAssert.assertTrue(profilePage.enterMobileNumberUpdatePage.isDisplayed(), "enter mobile option not displayed in this page");
                    softAssert.assertTrue(profilePage.getOtpButtonMobileNumberUpdatePage.isDisplayed(), "get OTP button not displayed");
                    softAssert.assertTrue(profilePage.currentMobileNoTab.isDisplayed(), "current mobile number tab is not displayed");
                    softAssert.assertTrue(profilePage.currentMobileNoTab.isDisplayed(), "current mobile number tab is not displayed");
                    softAssert.assertTrue(profilePage.visibleIconClosedState.isDisplayed(), "current mobile number is in hidden state icon not visible");
                    softAssert.assertTrue(profilePage.initiateVideoCallButton.isDisplayed(), "initiative video call button not displayed");
                    profilePage.enterMobileNumberUpdatePage.sendKeys("8148992911");
                    waitTillElementToBeClickable(driver, profilePage.getOtpButtonMobileNumberUpdatePage);
                    clickOnButton(profilePage.getOtpButtonMobileNumberUpdatePage);
                    waitTillVisibilityElement(driver, profilePage.successfulMessage);
                    softAssert.assertTrue(profilePage.successfulMessage.isDisplayed(), "sent otp successful message not displayed");
                    /****Enter Otp ***/
                    waitTillVisibilityElement(driver, profilePage.otpPopUp);
                    char[] otpChar = fileReader.logInTestData.get("commonOtp").toCharArray();
                    ListIterator<WebElement> otpElementList = profilePage.otpInMobileUpdateWindow.listIterator();
                    for (char otpNumber : otpChar) {
                        if (otpElementList.hasNext()) {
                            WebElement enterOtp = otpElementList.next();
                            enterOtp.sendKeys(Character.toString(otpNumber));
                        } else {
                            break;
                        }
                    }
                    staticWait(2000);
                    waitTillVisibilityElement(driver, profilePage.successfulMessage);
                    softAssert.assertTrue(profilePage.successfulMessage.isDisplayed(), " otp verified successful message not displayed");
                    staticWait(3000);
                    clickOnButton(profilePage.initiateVideoCallButton);
                    waitTillInvisibilityOfLoader(driver);
                    softAssert.assertTrue(driver.getCurrentUrl().contains("video-box/VB/initiate-video"), "navigates to video call page failed");
                    softAssert.assertTrue(profilePage.videoBankingHeader.getText().contains("Video Banking"), "video banking header not matched");


                } catch (NoSuchElementException e) {
                    attachScreenshot(driver, scenario);
                    logger.error("Missing elements is " + e.toString());
                    //  softAssert.fail("Update mobile number some elements are not displayed");
                }
                driver.close();
                driver.switchTo().window(profileSettingPageWindow);
                try {
                    if (profilePage.cancelButton.isDisplayed()) {
                        attachScreenshot(driver, scenario);
                        logger.debug("The mobile number update pop up also displayed please verify the report");
                        profilePage.cancelButton.click();
                    }
                } catch (NoSuchElementException e) {
                }
            } else if (mobileNumberUpdateWindow.equals(profileSettingPageWindow)) {
                //  logger.info("Mobile update window not open");
                try {
                    if (profilePage.cancelButton.isDisplayed()) {
                        attachScreenshot(driver, scenario);
                        logger.debug("The mobile number update pop up also displayed please verify the report");
                        profilePage.cancelButton.click();
                    }
                } catch (NoSuchElementException e) {
                }
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

    @When("User clicks on update email id option in profile setting page")
    public void userClicksOnUpdateEmailIdOptionInProfileSettingPage() {
        clickOnButton(profilePage.emailIDUpdateButton);
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User navigates to profile setting page")
    public void userNavigatesToProfileSettingPage() {
        profilePage.backButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @When("User clicks on update communication option in profile setting page")
    public void userClicksOnUpdateCommunicationOptionInProfileSettingPage() {
        profilePage.communicationAddressUpdateButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User verify the change password function")
    public void userVerifyTheChangePasswordFunction() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, profilePage.changePasswordButton);
        profilePage.changePasswordButton.click();
        waitTillInvisibilityOfLoader(driver);
        if (driver.getCurrentUrl().contains("change-password/user-authentication")) {
            logger.info("For NTB Users No need to verify the face match or debit card");
            softAssert.assertTrue(profilePage.pageHeader.getText().contains("Email OTP Verification"), "page header name is not matched");
            softAssert.assertTrue(profilePage.backButton.isDisplayed(), "back button is not displayed");
            /***Navigate to the otp page***/
            homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
            waitTillInvisibilityOfLoader(driver);
            /***Redirected to set password page ***/
            softAssert.assertTrue(profilePage.pageHeader.getText().contains("Change Password"), "page header name is not matched");
            softAssert.assertTrue(profilePage.backButton.isDisplayed(), "back button is not displayed");
            softAssert.assertTrue(!profilePage.proceedButton.isEnabled(), "with out enter any password proceed button should not be enabled");
            Random random = new Random();
            int randomDigitPassword = random.nextInt(900) + 100;
            updatedPassword = "SoundarNtb$" + String.valueOf(randomDigitPassword);
            setPasswordForLogIn(updatedPassword);
            /**Updated in excel sheet**/
            staticWait(2000);
            logger.info("Update password is " + updatedPassword);
            profilePage.enterNewPassword.sendKeys(updatedPassword);
            profilePage.confirmPassword.sendKeys(updatedPassword);
            staticWait(3000);
            profilePage.proceedButton.click();
            staticWait(5000);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify the change password status")
    public void userVerifyTheChangePasswordStatus() {
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(profilePage.successfulMessage.isDisplayed(), "password changed message not displayed");
            String sheetName = "ProfilePage";
            String columnName = "updatePasswordNTB";
            String existingPassword = fileReader.profileTestData.get("updatePasswordNTB");
            inputValueInExcel(sheetName, columnName, existingPassword, updatedPassword);
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Password changed successfully message not displayed");
        }
        softAssert.assertTrue(logInPage.logInButton.isDisplayed(), "log in button not displayed");
        logInPage.logInButton.click();
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User clicks on back button to navigates setting page")
    public void userClicksOnBackButtonToNavigatesSettingPage() {
        waitTillElementToBeClickable(driver, profilePage.backButton);
        profilePage.backButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User validate the updated credentials")
    public void userValidateTheUpdatedCredentials() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("auth/login"), "redirected to log in page failed");
        String scenarioName = scenario.getName();
        staticWait(2000);
        switch (scenarioName) {
            case "Validate profile journey for ETB users":
                logInPage.userNameField.sendKeys(getUserNameForLogIn());
                logInPage.passwordField.sendKeys(fileReader.profileTestData.get("password"));
                break;
            case "Validate update username & password on profile for NTB users":
                logInPage.userNameField.sendKeys(fileReader.profileTestData.get("userNameUpdateNTB"));
                logInPage.passwordField.sendKeys(getPasswordForLogIn());
                break;
        }
        waitTillElementToBeClickable(driver, logInPage.logInButton);
        logInPage.logInButton.click();
        waitTillInvisibilityOfLoader(driver);
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
        homePageStepDef.userVerifyAdvisoryPageForUserPractice();
        waitTillVisibilityOfUrl(driver,"https://mibsit-mr.aubankuat.in/mr-dashboard");
        softAssert.assertTrue(driver.getCurrentUrl().contains("mr-dashboard"), "navigate to the dashboard page is failed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User verify update entity name function")
    public void userVerifyUpdateEntityNameFunction() {
        profilePage.entityNameUpdateButton.click();
        waitTillInvisibilityOfLoader(driver);
        Random randomUser = new Random();
        int randomDigit = randomUser.nextInt(900) + 100;
        updatedUserName = "TeaShop" + randomDigit;
        profilePage.enterUpdatedEntityName.sendKeys(Keys.chord(Keys.CONTROL, "a"), updatedUserName);
        staticWait(1000);
        profilePage.submitButton.click();
        staticWait(2000);
//      softAssert.assertTrue(profilePage.successfulMessage.isDisplayed(), "entity updated failed");
//      profilePage.cancelButton.click();
//
//      try {
//            softAssert.assertAll();
//        } catch (AssertionError e) {
//            attachScreenshot(driver, scenario);
//            scenario.log(e.toString());
//            setErrorsInList(e.toString());
//        }
    }
}







