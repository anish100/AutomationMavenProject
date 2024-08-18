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
import org.testng.asserts.SoftAssert;
import pom.DebitCardPage;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import java.util.ListIterator;
import java.util.Random;

public class DebitCardStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(DebitCardStepDef.class);
    DebitCardPage debitCardPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String expiryDateMonth;
    HomePageStepDef homePageStepDef;
    AccountSummaryStepDef accountSummaryStepDef;
    AccountStatementStepDef accountStatementStepDef;
    MoneyTransferStepDef moneyTransferStepDef;

    int atmLimitAmount;
    int posLimitAmount;
    int eComLimitAmount;
    int contactlessLimitAmount;
    boolean redeemPageErrorPopUp;

    public DebitCardStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        debitCardPage = context.getPageObjectManager().getDebitCardPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        homePageStepDef = new HomePageStepDef(context);
        accountSummaryStepDef = new AccountSummaryStepDef(context);
        accountStatementStepDef=new AccountStatementStepDef(context);
        moneyTransferStepDef=new MoneyTransferStepDef(context);
    }

    @Then("User verify debit card home page")
    public void userVerifyDebitCardHomePage() {
        softAssert = new SoftAssert();
        if (debitCardPage.debitCardHolderName.getText().contains("No Debit Card")) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Eros API Down");
            logger.debug("No Debit card to display");
            //System.out.println("No Debit card to display");
            driver.quit();

        } else {
            if (debitCardPage.debiCardType.getText().isEmpty()) {
                debitCardPage.previousCardNavigationButton.click();
            }
            staticWait(3000);
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.debitCardHomePageUrlPath), "debit card home page is not displayed");
            softAssert.assertTrue(debitCardPage.debiCardPageHeader.getText().contains("Debit Card Overview"), "debit card page header is not displayed");
            softAssert.assertTrue(debitCardPage.debiCardImage.isDisplayed(), "debit card image not displayed");
            softAssert.assertTrue(debitCardPage.debiCardStatus.isDisplayed(), "debit card status is not displayed");
            softAssert.assertTrue(debitCardPage.debiCardType.isDisplayed(), "debit card type not displayed");
            softAssert.assertTrue(debitCardPage.debiCardLinkedAccountNumber.isDisplayed(), "linked account number not displayed");
            softAssert.assertTrue(debitCardPage.quickLinkOfferManagementTab.isDisplayed(), "quick links statement tab is not displayed");
            softAssert.assertTrue(debitCardPage.quickLinksCreditCardsTab.isDisplayed(), "quick links money transfer tab is not displayed");
            softAssert.assertTrue(debitCardPage.toFlipCard.isDisplayed(), "click to view card details text not displayed");
            logger.debug("Debit card status is " + debitCardPage.debiCardStatus.getText());
            logger.info("Debit card Linked account " + debitCardPage.debiCardLinkedAccountNumber.getText());
            setPayeeAccountNumber(debitCardPage.debitCardNumber.getText());
            if (debitCardPage.debiCardStatus.getText().contains(TextAssertion.debitCardActiveState)) {
                softAssert.assertTrue(debitCardPage.regeneratePinTab.isEnabled(), "regenerate pin tab is not enable");
                softAssert.assertTrue(debitCardPage.cardUsageLimitsTab.isEnabled(), "card usage tab is not enable");
                softAssert.assertTrue(debitCardPage.rewardsTab.isDisplayed(), "reward tab is not enabled");
                softAssert.assertTrue(debitCardPage.blockCardTab.getText().contains("Block Card"), "block card tab is not enabled");
            } else if (debitCardPage.debiCardStatus.getText().contains(TextAssertion.debitCardBlockedState)) {
                softAssert.assertFalse(debitCardPage.regeneratePinTab.isEnabled(), "regenerate pin tab should not enable");
                softAssert.assertTrue(debitCardPage.blockCardTab.getText().contains("Unblock Card"), "un block card option is not enabled");
                softAssert.assertFalse(debitCardPage.cardUsageLimitsTab.isEnabled(), "card usage tab should not enable");
            } else if (debitCardPage.debiCardStatus.getText().contains(TextAssertion.debitCardInActiveState)) {
                softAssert.assertTrue(debitCardPage.regeneratePinTab.isEnabled(), "regenerate pin tab is not enable");
                softAssert.assertFalse(debitCardPage.cardUsageLimitsTab.isEnabled(), "card usage tab should not enable");
                softAssert.assertFalse(debitCardPage.blockCardTab.getText().contains("Block Card"), "block card tab should not enabled");
            }
        }
        if (debitCardPage.debiCardStatus.getText().contains(TextAssertion.debitCardBlockedState)) {
            int noOfDebitCard = debitCardPage.cardLists.size();
            if (noOfDebitCard > 1) {
                for (int i = 0; i <= noOfDebitCard; i++) {
                    debitCardPage.nextButton.click();
                    if (debitCardPage.debiCardStatus.getText().contains(TextAssertion.debitCardActiveState)) {
                        break;
                    }
                }
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

    @When("User click on debit card")
    public void userClickOnDebitCard() {
        //    if (debitCardPage.debiCardStatus.getText().contains("Active")) {
        staticWait(2000);
        debitCardPage.toFlipCard.click();
        staticWait(2000);
        //  }

    }

    @Then("User should verify the details after flip the card")
    public void userShouldVerifyTheDetailsAfterFlipTheCard() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(debitCardPage.debitCardNumber.isDisplayed(), "debit card number not displayed");
        softAssert.assertTrue(debitCardPage.debitCardExpiryDate.isDisplayed(), "debit card expiry date not displayed");
        softAssert.assertTrue(debitCardPage.debitCardCvv.isDisplayed(), "debit card cvv number not displayed");
        expiryDateMonth = debitCardPage.debitCardExpiryDate.getText();
        logger.info("Debit card Expiry date is " + expiryDateMonth);
        logger.info("Debit card number is " + debitCardPage.debitCardNumber.getText());
//      Debit Card Number Hidden
        waitTillElementToBeClickable(driver, debitCardPage.debitCardNumberHideIcon);
        debitCardPage.debitCardNumberHideIcon.click();
        waitTillInvisibilityOfLoader(driver);
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
        waitTillInvisibilityOfLoader(driver);
        logger.info("Debit card CVV number is " + debitCardPage.debitCardCvv.getText());
        softAssert.assertTrue(debitCardPage.debitCardNumber.getText().contains("XXXX"), "debit card number not hidden");
        softAssert.assertTrue(debitCardPage.debitCardExpiryDate.getText().contains("XX"), "debit card expiry date not hidden");
        softAssert.assertFalse(debitCardPage.debitCardCvv.getText().contains("XXX"), "debit card CVV number should be displayed");
//      CVV Number Hidden
        staticWait(2000);
        debitCardPage.debitCardCVVHideIcon.click();
        waitTillInvisibilityOfLoader(driver);
        softAssert.assertTrue(debitCardPage.debitCardCvv.getText().contains("XXX"), "debit card CVV not displayed");
        softAssert.assertFalse(debitCardPage.debitCardNumber.getText().contains("XXXX"), "debit card number not hidden");
        softAssert.assertFalse(debitCardPage.debitCardExpiryDate.getText().contains("XX"), "debit card expiry date not hidden");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks regenerate pin tab")
    public void userClicksRegeneratePinTab() {
        scrollIntoView(driver, debitCardPage.regeneratePinTab);
        logger.info("User clicks on regenerate pin tab");
        debitCardPage.regeneratePinTab.click();
        staticWait(2000);
    }

    @Then("User navigates to {string} page")
    public void userNavigatesToPage(String pageName) {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, debitCardPage.debiCardPageHeader);
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.generatePinPageUrlPath), "navigates regenerate pin page is failed");
        softAssert.assertTrue(debitCardPage.debiCardPageHeader.getText().contains("Generate PIN"), "generate pin page header is not matched");
        softAssert.assertTrue(debitCardPage.debiCardType.isDisplayed(), "debit card type is not displayed");
        softAssert.assertTrue(debitCardPage.debitCardNumberInPinGeneratePin.isDisplayed(), "debit card number is not displayed");
        softAssert.assertTrue(debitCardPage.quickLinkOfferManagementTab.isDisplayed(), "quick links statement tab is not displayed");
        softAssert.assertTrue(debitCardPage.quickLinksCreditCardsTab.isDisplayed(), "quick links money transfer tab is not displayed");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User enters the details and generate the pin")
    public void userEntersTheDetailsAndGenerateThePin() {
        softAssert = new SoftAssert();
        debitCardPage.enterExpiryDate.sendKeys(expiryDateMonth);
        staticWait(3000);
        int enterPin = 1;
        int conformPin = 1;
        for (int i = 0; i < debitCardPage.enterSetAtmPin.size(); i++) {
            debitCardPage.generatePin(i).sendKeys(String.valueOf(enterPin));
            enterPin += 3;
            logger.info("Debit card pin set by the user is :" + enterPin);
        }
        for (int i = 0; i < debitCardPage.conformSetAtmPin.size(); i++) {
            debitCardPage.conformPin(i).sendKeys(String.valueOf(conformPin));
            conformPin += 3;
        }
        logger.info("Debit card pin set by the user is :" + String.valueOf(enterPin));
        softAssert.assertTrue(debitCardPage.pinMatchConform.getText().contains("PIN matched"), "entered pin does not matched");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on continue button")
    public void userClicksOnContinueButton() {
        debitCardPage.continueButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the popup")
    public void userVerifyThePopup() {
        softAssert = new SoftAssert();
        // staticWait(3000);
        try {
            if (debitCardPage.successfulStatusMessage.getText().contains("uccessfully")) {
                softAssert.assertTrue(debitCardPage.successfulStatusMessage.getText().contains("successfully"), "pin generated successfully failed");
                softAssert.assertTrue(debitCardPage.referenceNumber.isDisplayed(), "reference number not displayed");
                debitCardPage.referenceNumberCopyButton.click();
                staticWait(2000);
                softAssert.assertTrue(debitCardPage.referenceNumberCopiedMessage.getText().toLowerCase().contains("copied"), "reference number copied failed");
                String urlId = driver.getCurrentUrl();
                debitCardPage.backToDebitCard.click();
                waitTillInvisibilityOfLoader(driver);
                if (urlId.contains(TextAssertion.blockCardPageUrlPath)) {
                    softAssert.assertTrue(debitCardPage.debiCardStatus.getText().contains(TextAssertion.debitCardBlockedState), "debit card not in active");
                } else {
                    softAssert.assertTrue(debitCardPage.debiCardStatus.getText().contains(TextAssertion.debitCardActiveState), "debit card not in active");
                }
            } else if (debitCardPage.successfulStatusMessage.getText().contains("unblock your debit card")) {
                softAssert.assertTrue(debitCardPage.successfulStatusMessage.getText().contains("unblock your debit card"), "debit card unblocked failed");
                softAssert.assertTrue(debitCardPage.referenceNumber.isDisplayed(), "reference number not displayed");
                debitCardPage.backToDebitCard.click();
                waitTillInvisibilityOfLoader(driver);
                softAssert.assertTrue(debitCardPage.debiCardStatus.getText().contains(TextAssertion.debitCardActiveState), "debit card not in active");
            }

            if (debitCardPage.statusFailedTryAgainButton.isDisplayed()) {
                attachScreenshot(driver, scenario);
                debitCardPage.statusFailedTryAgainButton.click();
                waitTillInvisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks card usage & limits tab")
    public void userClicksCardUsageLimitsTab() {
        if (debitCardPage.debiCardType.getText().isEmpty()) {
            for (int i = 0; i <= 3; i++) {
                debitCardPage.previousCardNavigationButton.click();
                if (!debitCardPage.debiCardType.getText().isEmpty()) {
                    break;
                }
            }
        }
        try {
            scrollIntoViewUp(driver, debitCardPage.debiCardPageHeader);
            setPayeeAccountNumber(debitCardPage.debiCardLinkedAccountNumber.getText());
            logger.info(debitCardPage.debiCardLinkedAccountNumber.getText());
            debitCardPage.cardUsageLimitsTab.click();
            waitTillInvisibilityOfLoader(driver);
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Please verify the attached screenshot");
        }
    }

    @Then("User verify the {string} page")
    public void userVerifyThePage(String pageName) {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, debitCardPage.debiCardPageHeader);
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.cardUsagePageUrlPath), "card usage and limit page not displayed");
        softAssert.assertTrue(debitCardPage.debiCardPageHeader.getText().equalsIgnoreCase(pageName), "page header name not matched");
        softAssert.assertTrue(debitCardPage.debiCardType.isDisplayed(), "debit card not displayed");
        softAssert.assertTrue(debitCardPage.debitCardNumberInPinGeneratePin.isDisplayed(), "debit card number not displayed");
        softAssert.assertTrue(debitCardPage.quickLinkOfferManagementTab.isDisplayed(), "quick links statement tab is not displayed");
        softAssert.assertTrue(debitCardPage.quickLinksCreditCardsTab.isDisplayed(), "quick links money transfer tab is not displayed");
        softAssert.assertTrue(debitCardPage.domesticTab.getText().contains("Domestic"), "domestic option tab not displayed");
        softAssert.assertTrue(debitCardPage.internationalTab.getText().contains("International"), "international option tab not displayed");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User set the amount limit in {string} option tab")
    public void userSetTheAmountLimitInOptionTab(String arg0) {
        scrollIntoView(driver, debitCardPage.domesticTab);
        Random random = new Random();
        atmLimitAmount = 20000 + random.nextInt(1000) + 1000;
        posLimitAmount = 10000 + random.nextInt(1000) + 1000;
        eComLimitAmount = 7000 + random.nextInt(1000) + 1000;
        contactlessLimitAmount = 14500 + random.nextInt(1000) + 1000;
        if (debitCardPage.atmUseToggle.getAttribute("class").contains("active")) {
            debitCardPage.atmUseEnterLimitAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(atmLimitAmount));
        } else {
            debitCardPage.atmUseToggle.click();
            debitCardPage.atmUseEnterLimitAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(atmLimitAmount));
        }
        if (debitCardPage.posToggle.getAttribute("class").contains("active")) {
            debitCardPage.posUseEnterLimitAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(posLimitAmount));
        } else {
            debitCardPage.posToggle.click();
            debitCardPage.posUseEnterLimitAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(posLimitAmount));
        }
        if (debitCardPage.eComOrOnlineUseToggle.getAttribute("class").contains("active")) {
            debitCardPage.eComOrOnlineUseEnterLimitAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(eComLimitAmount));
        } else {
            debitCardPage.eComOrOnlineUseToggle.click();
            debitCardPage.eComOrOnlineUseEnterLimitAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(eComLimitAmount));
        }
        if (debitCardPage.contactlessToggle.getAttribute("class").contains("active")) {
            debitCardPage.contactlessUseEnterLimitAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(contactlessLimitAmount));
        } else {
            debitCardPage.contactlessToggle.click();
            debitCardPage.contactlessUseEnterLimitAmount.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(contactlessLimitAmount));
        }
    }

    @When("User clicks on confirm button in card usage and limit page")
    public void userClicksOnConfirmButtonInCardUsageAndLimitPage() {
        scrollIntoView(driver, debitCardPage.confirmButton);
        debitCardPage.confirmButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify updated card usage & limits")
    public void userVerifyUpdatedCardUsageLimits() {
        softAssert = new SoftAssert();
        int atmUpdatedLimit = Integer.parseInt(debitCardPage.atmUseEnterLimitAmount.getAttribute("value").replace(",", "").trim());
        int posUpdatedLimit = Integer.parseInt(debitCardPage.posUseEnterLimitAmount.getAttribute("value").replace(",", "").trim());
        int eComOrOnlineUpdatedLimit = Integer.parseInt(debitCardPage.eComOrOnlineUseEnterLimitAmount.getAttribute("value").replace(",", "").trim());
        int contactlessUpdatedLimit = Integer.parseInt(debitCardPage.contactlessUseEnterLimitAmount.getAttribute("value").replace(",", "").trim());
        softAssert.assertEquals(atmUpdatedLimit, atmLimitAmount, "atm limit not updated");
        softAssert.assertEquals(posUpdatedLimit, posLimitAmount, "pos updated limit not updated");
        softAssert.assertEquals(eComOrOnlineUpdatedLimit, eComLimitAmount, "eComOrOnline updated limit not updated");
        softAssert.assertEquals(contactlessUpdatedLimit, contactlessLimitAmount, "contactless updated limit not updated");

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks international option tab")
    public void userClicksInternationalOptionTab() {
        debitCardPage.internationalTab.click();
        staticWait(2000);
    }

    @And("User clicks back navigates to debit card home page")
    public void userClicksBackNavigatesToDebitCardHomePage() {
//      driver.navigate().back();
        debitCardPage.backButton.click();
        waitTillInvisibilityOfLoader(driver);
        scrollIntoViewUp(driver, debitCardPage.debiCardPageHeader);
    }

    @When("User clicks block card tab")
    public void userClicksBlockCardTab() {
        if (debitCardPage.debiCardType.getText().isEmpty()) {
            debitCardPage.previousCardNavigationButton.click();
        }
        scrollIntoView(driver, debitCardPage.blockCardTab);
        debitCardPage.blockCardTab.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the block card page")
    public void userVerifyTheBlockCardPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.blockCardPageUrlPath), "card usage and limit page not displayed");
        softAssert.assertTrue(debitCardPage.debiCardPageHeader.getText().contains("Block Debit Card"), "page header name not matched");
        softAssert.assertTrue(debitCardPage.debiCardType.isDisplayed(), "debit card not displayed");
        softAssert.assertTrue(debitCardPage.debitCardNumberInPinGeneratePin.isDisplayed(), "debit card number not displayed");
        softAssert.assertTrue(debitCardPage.quickLinkOfferManagementTab.isDisplayed(), "quick links statement tab is not displayed");
        softAssert.assertTrue(debitCardPage.quickLinksCreditCardsTab.isDisplayed(), "quick links money transfer tab is not displayed");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User select details to block the card")
    public void userSelectDetailsToBlockTheCard() {
        debitCardPage.selectCardBlockType(fileReader.debitCardTestData.get("cardBlockType")).click();
        debitCardPage.clickReason.click();
        debitCardPage.selectReasonFromDD(fileReader.debitCardTestData.get("reasonForBlockCard")).click();
        if (fileReader.debitCardTestData.get("reasonForBlockCard").equals("Other")) {
            debitCardPage.enterOtherReason.sendKeys("Testing Purpose");
        }
    }

    @And("User verify multiple cards details")
    public void userVerifyMultipleCardsDetails() {
        softAssert = new SoftAssert();
        try {
            if (debitCardPage.cardLists.size() != 0) {
                softAssert.assertTrue(debitCardPage.nextButton.isDisplayed(), "next button not displayed");
                logger.info("Total number of debit cards : " + debitCardPage.cardLists.size());
            }
            int noOfDebitCard = debitCardPage.cardLists.size();
            softAssert.assertAll();

        } catch (Exception e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the linked account number after navigates")
    public void userVerifyTheLinkedAccountNumberAfterNavigates() {
        softAssert = new SoftAssert();
        String storedLinkedAccountNumber = getPayeeAccountNumber();
        softAssert.assertTrue(storedLinkedAccountNumber.contains(debitCardPage.debiCardLinkedAccountNumber.getText()), "After navigates from different page linked account number not matched");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks rewards tab")
    public void userClicksRewardsTab() {
        scrollIntoView(driver, debitCardPage.rewardsTab);
        clickOnButton(debitCardPage.redeemNowButton);
        waitTillInvisibilityOfLoader(driver);

    }
    @Then("User verify the rewards page")
    public void userVerifyTheRewardsPage() {
        redeemPageErrorPopUp=false;
        try {
            if (debitCardPage.backToDebitCardButton.isDisplayed()) {
            redeemPageErrorPopUp=true;
            }
            softAssert = new SoftAssert();
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.debitCardRewardsPageUrl), "The rewards page URL is not identical");
            softAssert.assertTrue(debitCardPage.backButton.isDisplayed(), "The back button on the rewards page is not displayed");
            softAssert.assertTrue(debitCardPage.debiCardPageHeader.getText().contains(TextAssertion.rewardsPageHeader), "The rewards page url is not identical");
            softAssert.assertTrue(debitCardPage.redeemNowButton.isDisplayed(), "The redeem now button is not displayed");
            softAssert.assertTrue(debitCardPage.checkAllOffersButton.isDisplayed(), "check all offers button is not displayed");
            softAssert.assertTrue(debitCardPage.detailsStatementQuick.isDisplayed(), "The detailed statement quick links is not displayed");
            softAssert.assertTrue(debitCardPage.moneyTransferQuickLink.isDisplayed(), "The money transfer quick links is not displayed");
        }
        catch (NoSuchElementException e){
            attachScreenshot(driver,scenario);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Some elements are missing please verify the exception");
        }
    }

    @When("User clicks on the redeem now button")
    public void userClicksOnTheRedeemNowButton() {
        if(redeemPageErrorPopUp==false){
        clickOnButton(debitCardPage.redeemNowButton);
        waitTillInvisibilityOfLoader(driver);
    }}

    @Then("User validating the rewards portal popup")
    public void userValidatingTheRewardsPortalPopup() {
        if(redeemPageErrorPopUp==false){
        softAssert = new SoftAssert();
        softAssert.assertTrue(debitCardPage.rewardsPortalPopup.isDisplayed(), "The rewards portal popup is not displayed");
        softAssert.assertTrue(debitCardPage.rewardsPortalPopupHeader.getText().contains(TextAssertion.rewardsPortalPopupHeader), "The rewards portal pop up header is not identical");
        softAssert.assertTrue(debitCardPage.cancelButton.isDisplayed(), "The cancel button is not displayed");
        softAssert.assertTrue(debitCardPage.continueButton.isDisplayed(), "The continue button is not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Some elements are missing please verify the exception");
        }
    }}

    @And("User validating the redirection of the redeem now button")
    public void userValidatingTheRedirectionOfTheRedeemNowButton() {
        if(redeemPageErrorPopUp==false){
        softAssert = new SoftAssert();
        String rewardsPageWindow = driver.getWindowHandle();
        debitCardPage.continueButton.click();
        waitTillANewWindowOpens(driver);
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(rewardsPageWindow)) {
                driver.switchTo().window(windowHandle);
                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.rewardsPortalPageUrl), "The rewards point page url is not identical");
                break;
            }
            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                scenario.log(e.toString());
                attachScreenshot(driver, scenario);
                setErrorsInList(e.toString());
            }

        }
        driver.close();
        driver.switchTo().window(rewardsPageWindow);
        waitTillVisibilityTittle(driver,TextAssertion.tittle);
    }}

    @When("User clicks on the check all offers button")
    public void userClicksOnTheCheckAllOffersButton() {
        if(redeemPageErrorPopUp==false){
        clickOnButton(debitCardPage.checkAllOffersButton);
        waitTillInvisibilityOfLoader(driver);
    }}

    @And("User validating the redirection of the check all offers button")
    public void userValidatingTheRedirectionOfTheCheckAllOffersButton() {
        if(redeemPageErrorPopUp==false){
        softAssert = new SoftAssert();
        waitTillANewWindowOpens(driver);
        String rewardsPageWindow = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(rewardsPageWindow)) {
                driver.switchTo().window(windowHandle);
                softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.checkOfferPageUrl), "The offer page page url is not identical");
            }
            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                scenario.log(e.toString());
                attachScreenshot(driver, scenario);
                setErrorsInList(e.toString());
            }

        }
        driver.close();
        driver.switchTo().window(rewardsPageWindow);
        waitTillVisibilityTittle(driver,TextAssertion.tittle);
    }}

    @And("User validating the redirection of quick links on the rewards page")
    public void userValidatingTheRedirectionOfQuickLinksOnTheRewardsPage() {
        if (redeemPageErrorPopUp == false) {
            if (debitCardPage.debiCardPageHeader.getText().contains(TextAssertion.rewardsPageHeader)) {
                accountSummaryStepDef.userClicksOnDetailedStatementInQuickLinksOnAccountsDetailsPage();
                waitTillVisibilityOfPageHeader(driver, TextAssertion.accountStatementPageHeader);
                accountStatementStepDef.userVerifyTheAccountStatementPage();
                homePageStepDef.userNavigatesToThe("my rewards page");
                accountSummaryStepDef.userClicksOnTheMoneyTransferInQuickLinksSectionOnAccountDetailsPage();
                waitTillVisibilityOfPageHeader(driver, TextAssertion.moneyTransferPageHeader);
                moneyTransferStepDef.userVerifyMoneyTransferHomePage();
            }
        }
    }
}

