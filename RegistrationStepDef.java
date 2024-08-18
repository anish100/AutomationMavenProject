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
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pom.LogInPage;
import pom.RegistrationPage;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegistrationStepDef extends Base {

    private static final Logger logger = LogManager.getLogger(RegistrationStepDef.class);
    WebDriver driver;
    RegistrationPage registrationPage;
    LogInPage logInPage;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String userNameNTB;
    String passwordNTB;


    public RegistrationStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        registrationPage = context.getPageObjectManager().getRegistrationPage();
        logInPage = context.getPageObjectManager().getLogInPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();

    }

    @When("User clicks on registration button")
    public void userClicksOnRegistrationButton() {
        clickOnButton(registrationPage.registrationLink);
        waitForPageLoad(driver);

    }

    @Then("User navigates to language page")
    public void userNavigatesToLanguagePage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("register/language"), "page url not matched");
        softAssert.assertTrue(registrationPage.languageChoosePageHeader.getText().contains("choose your preferred language"));
        softAssert.assertTrue(registrationPage.proceedButton.isDisplayed(), "proceed button not displayed");
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User select language the suitable language")
    public void userSelectLanguageTheSuitableLanguage() {
        registrationPage.selectLanguageByText("English");
        clickOnButton(registrationPage.proceedButton);
    }

    @Then("User verify the create account page")
    public void userVerifyTheCreateAccountPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("register/mobile"), "page url not be the same");
        softAssert.assertTrue(registrationPage.enterMobileNoReg.isDisplayed(), "enter mobile number bar not displayed");
        softAssert.assertTrue(registrationPage.createAccountPageHeader.getText().contains("Create your"), "page header not be the same");
        waitTillElementToBeClickable(driver, registrationPage.enterMobileNoReg);
        /*** Mobile Number Randomy create ***/
        Random random = new Random(System.currentTimeMillis());
        int randomGenLast9digit = 100000000 + random.nextInt(900000000);
        logger.info("Randomly Generate last 5 digit no" + randomGenLast9digit);
        String randomlyGenerateMobileNo = "6" + Integer.toString(randomGenLast9digit);
        sendKeys(registrationPage.enterMobileNoReg, randomlyGenerateMobileNo);
        logger.info("NTB Customer mobile number is :" + randomlyGenerateMobileNo);
        //  clickOnButton(registrationPage.proceedButton);
        // registrationPage.linksInPage.size();
        System.out.println(registrationPage.linksInPage.size());
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the privacy policy link")
    public void userVerifyThePrivacyPolicyLink() {
        softAssert = new SoftAssert();
        String currentWindow = driver.getWindowHandle();
        registrationPage.privacyPolicyLink.click();
        waitTillANewWindowOpens(driver);
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindow)) {
                driver.switchTo().window(windowHandle);
                try {
                    waitTillInvisibilityOfLoader(driver);
                    softAssert.assertTrue(driver.getCurrentUrl().contains("privacy-policy"), "privacy policy page url not be the same");
                    softAssert.assertTrue(registrationPage.privacyPolicyPageHeader.isDisplayed(), "page header not displayed");
                } catch (NoSuchElementException e) {
                    attachScreenshot(driver, scenario);
                    softAssert.fail("Page header not displayed");
                    logger.error("privacy page header not displayed " + e.getMessage());
                }
                break;
            }
        }
        driver.close();
        driver.switchTo().window(currentWindow);
        waitTillVisibilityTittle(driver, TextAssertion.tittle);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the terms and condition link")
    public void userVerifyTheTermsAndConditionLink() {
        softAssert = new SoftAssert();
        String currentWindow = driver.getWindowHandle();
        registrationPage.termAndConditionLink.click();
        waitTillANewWindowOpens(driver);
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindow)) {
                driver.switchTo().window(windowHandle);
                try {
                    waitTillInvisibilityOfLoader(driver);
                    softAssert.assertTrue(driver.getCurrentUrl().contains("terms-and-conditions"), "terms & condition page url not be the same");
                    softAssert.assertTrue(registrationPage.termsAndConditionPageHeader.isDisplayed(), "page header not displayed");
                } catch (NoSuchElementException e) {
                    attachScreenshot(driver, scenario);
                    softAssert.fail("Page header not displayed");
                    logger.error("Terms and condition page " + e.getMessage());
                }
                break;
            }
        }
        driver.close();
        driver.switchTo().window(currentWindow);
        waitTillVisibilityTittle(driver,TextAssertion.tittle);

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks on proceed button")
    public void userClicksOnProceedButton() {
        javaScriptExecutorClickElement(driver, registrationPage.proceedButton);
        waitForPageLoad(driver);
        //          waitTillInvisibilityOfLoader(driver);
    }

    @Then("User navigates to the customer id page")
    public void userNavigatesToTheCustomerIdPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("register/account-selection"));
        softAssert.assertTrue(registrationPage.selectCustomerIdPage.isDisplayed(), "select customer id page header not displayed");
        registrationPage.accountHolderName(fileReader.registrationTestData.get("accountHolderName"));
        clickOnButton(registrationPage.proceedButton);

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User enter the user details in NTB details page")
    public void userEnterTheUserDetailsInNTBDetailsPage() {
        waitTillVisibilityElement(driver, registrationPage.enterNameNTB);
        List<String> userNames = new ArrayList<>();
        userNames.add("AutoBdd");
        userNames.add("Automation Merchant");
        userNames.add("Selenium");
        List<String> usersMailId = new ArrayList<>();
        Random randoms = new Random();
        int randomDigit = randoms.nextInt(900) + 100;
        String digitStr = Integer.toString(randomDigit);
        usersMailId.add("aathiran." + digitStr + "@gmail.com");
        usersMailId.add("pradip." + digitStr + "@gmail.com");
        usersMailId.add("soundarararajan." + digitStr + "@gmail.com");
        usersMailId.add("aathiran." + digitStr + "@yahoo.com");
        usersMailId.add("mahesh." + digitStr + "@yahoo.co.in");
        usersMailId.add("soundarararajan." + digitStr + "@outlook.com");
        usersMailId.add("sagar." + digitStr + "@gmail.com");
        usersMailId.add("abahy." + digitStr + "@gmail.com");
        Random random = new Random();
        int nameRandomIndex = random.nextInt(userNames.size());
        int nameRandomIndexMail = random.nextInt(usersMailId.size());
        String randomName = userNames.get(nameRandomIndex);
        String randomMail = usersMailId.get(nameRandomIndexMail);
        registrationPage.enterNameNTB.sendKeys(randomName);
        registrationPage.enterBussNameNTB.sendKeys("MerchantProject");
        registrationPage.enterEmailNTB.sendKeys(randomMail);
        registrationPage.proceedButton.click();
        for (int i = 0; i <= 5; i++) {
            try {
                if (registrationPage.emailErrorPopUp.isDisplayed() || registrationPage.emailErrorPopUp.isDisplayed()) {
                    registrationPage.emailErrorPopUpOkButton.click();
                    List<String> usersMailId1 = new ArrayList<>();
                    int randomDigit1 = randoms.nextInt(900) + 100;
                    String digitStr1 = Integer.toString(randomDigit1);
                    usersMailId.add("aathira." + digitStr1 + "@gmail.com");
                    usersMailId.add("soundar." + digitStr1 + "@gmail.com");
                    int nameRandomIndexMail1 = random.nextInt(usersMailId.size());
                    String randomMail1 = usersMailId.get(nameRandomIndexMail1);
                    registrationPage.enterEmailNTB.sendKeys(Keys.chord(Keys.CONTROL, "a"), randomMail1);
                    registrationPage.proceedButton.click();
                    if (registrationPage.emailErrorPopUpOkButton.isDisplayed()) {
                        registrationPage.emailErrorPopUpOkButton.click();
                        registrationPage.proceedButton.click();

                    }
                } else {
                    break;
                }
            } catch (NoSuchElementException e) {

            }
        }
    }

    @And("User enters username on set username page")
    public void userEntersUsernameOnSetUsernamePage() {
        softAssert = new SoftAssert();
        String setName;
        setName = getUserName();
        try {
            registrationPage.setUserName.sendKeys(Keys.chord(Keys.CONTROL, "a"), setName);
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Set user name page is not displayed");
            throw exception;
        }
        logger.info("Assigned username by the user is :" + setName);
        userNameNTB = setName;
        waitTillVisibilityElement(driver, registrationPage.userNameAvailableMessage);
        if (registrationPage.userNameAvailableMessage.getText().equalsIgnoreCase("Username not available")) {
            logger.info("The user name set by the user is not available so check once again");
            setName = getUserName();
            //logger.info("Second time username Assigned by user is :"+setName);
            registrationPage.setUserName.sendKeys(Keys.chord(Keys.CONTROL, "a"), setName);
            userNameNTB = setName;
            logger.info("Second time username Assigned by user is :" + setName);
            clickOnButton(registrationPage.proceedButton);
        } else {
            waitTillElementToBeClickable(driver, registrationPage.proceedButton);
            clickOnButton(registrationPage.proceedButton);
        }
        setUserNameForLogIn(userNameNTB);
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            scenario.log(e.toString());
            attachScreenshot(driver, scenario);
            setErrorsInList(e.toString());
        }
    }

    @And("User set the password on the set password page")
    public void userSetThePasswordOnTheSetPasswordPage() {
        waitTillElementToBeClickable(driver, registrationPage.setPassword);
        Random random = new Random();
        String random4Digits = String.format("%04d", random.nextInt(10000));
        String setPassword = "Soundar@" + random4Digits;
        sendKeys(registrationPage.setPassword, setPassword);
        waitTillElementToBeClickable(driver, registrationPage.reEnterPassword);
        sendKeys(registrationPage.reEnterPassword, setPassword);
        logger.info("Assigned password by the user is :" + setPassword);
        passwordNTB = setPassword;
        setPasswordForLogIn(passwordNTB);
        staticWait(3000);
    }

    @And("User verifies the passwords matched message")
    public void userVerifiesThePasswordsMatchedMessage() {
        Assert.assertTrue(registrationPage.passwordVerifiedMessage.getText().equalsIgnoreCase("Password Matched"), "password matched message not displayed");
    }

    @Then("User verify the registration success page")
    public void userVerifyTheRegistrationSuccessPage() {
        softAssert = new SoftAssert();
        waitTillVisibilityOfUrl(driver, "register/success");
        softAssert.assertTrue(driver.getCurrentUrl().contains("register/success"), "registration success page url not match");
        try {
            softAssert.assertTrue(registrationPage.registrationSuccessMessage.isDisplayed(), "registration has been successfully completed message not displayed ");
            logger.info("NTB user registration completed successfully");
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Successfully Registration message not displayed");
            throw exception;
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

    @Then("User verify the log in function with newly registered credentials")
    public void userVerifyTheLogInFunctionWithNewlyRegisteredCredentials() {
        staticWait(2000);
        sendKeys(logInPage.userNameField, getUserNameForLogIn());
        sendKeys(logInPage.passwordField, getPasswordForLogIn());
    }

    public static String getUserName() {
        Random random = new Random();
        String userName = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int minLength = 4;
        int maxLength = 9;
        int randomCharacter = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < randomCharacter; i++) {
            int randomlyChar = random.nextInt(userName.length());
            char randomLetter = userName.charAt(randomlyChar);
            randomString.append(randomLetter);
        }
        int randomDigit = random.nextInt(9000) + 1000;
        String setName = randomString.toString() + randomDigit;
        return setName;
    }



}


