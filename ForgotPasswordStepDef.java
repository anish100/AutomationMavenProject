package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pom.ForgotPasswordPage;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import java.util.ListIterator;

public class ForgotPasswordStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(LogInStepDef.class);

    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    ForgotPasswordPage forgotPasswordPage;
    HomePageStepDef homePageStepDef;
    boolean isDebitCardAvailable;
    RegistrationStepDef registrationStepDef;

    public ForgotPasswordStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        forgotPasswordPage = context.getPageObjectManager().getForgotPasswordPage();
        homePageStepDef = new HomePageStepDef(context);
        registrationStepDef = new RegistrationStepDef(context);
    }


    @Then("User verify the forget password page")
    public void userVerifyTheForgetPasswordPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.forgotPasswordPageUrl), "forgot page url path not be the same");
        softAssert.assertTrue(forgotPasswordPage.forgotPasswordPageHeader.isDisplayed(), "forgot password page header is not displayed");
        softAssert.assertTrue(forgotPasswordPage.enterUserNameOrMobileNumberOrEmailField.isDisplayed(), "enter user detail field not displayed");
        softAssert.assertTrue(forgotPasswordPage.proceedButton.isDisplayed(), "proceed button not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User enters the required user detail")
    public void userEntersTheRequiredUserDetail() {
        if (scenario.getName().contains("Validate forgot password journey for NTB users")) {
            forgotPasswordPage.enterUserNameOrMobileNumberOrEmailField.sendKeys(fileReader.forgotPasswordTestData.get("userNameNTB"));
        } else if (scenario.getName().contains("Validate forgot password journey for ETB users by Face Match")) {
            forgotPasswordPage.enterUserNameOrMobileNumberOrEmailField.sendKeys(fileReader.forgotPasswordTestData.get("userNameETB"));
        } else {
            forgotPasswordPage.enterUserNameOrMobileNumberOrEmailField.sendKeys(fileReader.forgotPasswordTestData.get("userNameETBDebitCard"));
        }
      //  forgotPasswordPage.proceedButton.click();
    }

    @And("User verify the changed password status page")
    public void userVerifyTheChangedPasswordStatusPage() {
        softAssert = new SoftAssert();
        try {
            waitTillInvisibilityOfLoader(driver);
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.passwordChangedSuccessfullyPageUrlPath), "page url not be the same");
            softAssert.assertTrue(forgotPasswordPage.passwordChangedSuccessfully.isDisplayed(), "successfully message not displayed");
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            Assert.fail("Password change successful message not displayed");
        }
        inputValueInExcel("ForgotPassWordPage", "updatedPasswordNTB", fileReader.forgotPasswordTestData.get("updatedPasswordNTB"), getPasswordForLogIn());
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify the log in function with newly created password")
    public void userVerifyTheLogInFunctionWithNewlyCreatedPassword() {
        forgotPasswordPage.userNameField.sendKeys(fileReader.forgotPasswordTestData.get("userNameNTB"));
        forgotPasswordPage.passwordField.sendKeys(getPasswordForLogIn());
    }

    @And("User clicks on forgot password link")
    public void userClicksOnForgotPasswordLink() {
        forgotPasswordPage.forgetPasswordOption.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User verify  verify yourself page")
    public void userVerifyVerifyYourselfPage() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, forgotPasswordPage.verifyYourSelfPageHeader);
        softAssert.assertTrue(forgotPasswordPage.verifyYourSelfPageHeader.isDisplayed(), "page header name not be the same");
        if (forgotPasswordPage.verifyDebitCardAvailability.getText().contains("Proceed")) {
            isDebitCardAvailable = true;
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.forgotPasswordVerifyYourSelfPageUrlPath), " page url not matched");
            softAssert.assertTrue(forgotPasswordPage.verifyDebitCardAvailability.getText().contains("Proceed"), "proceed button not displayed");
            softAssert.assertTrue(forgotPasswordPage.radioButtonList.size() == 2, "radio button list not displayed");
            softAssert.assertTrue(forgotPasswordPage.radiButtonType("Debit").isDisplayed(), "debit card radio button not displayed");
            softAssert.assertTrue(forgotPasswordPage.radiButtonType("Face").isDisplayed(), "Face match radio button not displayed");

        } else {
            isDebitCardAvailable = false;
            softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.forgotPasswordFaceMatchPageUrlPath), " page url not matched");
            softAssert.assertTrue(forgotPasswordPage.startFaceMatchButton.isDisplayed(), "start face match button not displayed");
        }
        System.out.println(isDebitCardAvailable);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on start face match button")
    public void userClicksOnStartFaceMatchButton() {
        if (isDebitCardAvailable == true) {
            forgotPasswordPage.radiButtonType("Face").click();
            forgotPasswordPage.proceedButton.click();
        }
        forgotPasswordPage.startFaceMatchButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the start face match pop up")
    public void userVerifyTheStartFaceMatchPopUp() {
        softAssert = new SoftAssert();
        waitTillInvisibilityOfLoader(driver);
        softAssert.assertTrue(forgotPasswordPage.faceMatchPopUpHeader.isDisplayed(), "face match pop up not displayed");
        softAssert.assertTrue(forgotPasswordPage.cancelButton.isDisplayed(), "cancel button not displayed in face match popup");
        softAssert.assertTrue(forgotPasswordPage.faceMatchPopUpCapturePhotoButton.isDisplayed(), "capture button not displayed in face match pop up");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        forgotPasswordPage.cancelButton.click();
        waitForPageLoad(driver);
    }

    @And("User navigates to the verify yourself page")
    public void userNavigatesToTheVerifyYourselfPage() {
        if (isDebitCardAvailable == true) {
            driver.navigate().back();
            try {
                if (forgotPasswordPage.continueButton.isDisplayed()) {
                    forgotPasswordPage.continueButton.click();
                    waitTillInvisibilityOfLoader(driver);
                }
            } catch (NoSuchElementException e) {
            }
            waitForPageLoad(driver);
        }
        userClicksOnForgotPasswordLink();
        userEntersTheRequiredUserDetail();
        homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
    }

    @When("User clicks on debit card radio button")
    public void userClicksOnDebitCardRadioButton() {
        if (isDebitCardAvailable == true) {
            forgotPasswordPage.radiButtonType("Debit").click();
            forgotPasswordPage.proceedButton.click();
        }
    }

    @Then("User navigates to the debit cards lists page")
    public void userNavigatesToTheDebitCardsListsPage() {
        if (isDebitCardAvailable == true) {
            softAssert = new SoftAssert();
            softAssert.assertTrue(driver.getCurrentUrl().contains("register/cif-debit-cards"), "debit card list page url not matched");
            softAssert.assertTrue(!forgotPasswordPage.radioButtonList.isEmpty(), "radio button list not displayed");
            softAssert.assertTrue(forgotPasswordPage.verifyYourSelfPageHeader.isDisplayed(), "verify your self page header not displayed");
            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        }
    }

    @And("User select any one card and enters atm pin")
    public void userSelectAnyOneCardAndEntersAtmPin() {
        if (isDebitCardAvailable == true) {
            forgotPasswordPage.radioButton.click();
            staticWait(3000);
            int enterPin = 1;
            ListIterator<WebElement> atmPinListBox = forgotPasswordPage.enterAtmPin.listIterator();
            while (atmPinListBox.hasNext()) {
                WebElement atmPinBox = atmPinListBox.next();
                System.out.println(String.valueOf(enterPin));
                atmPinBox.sendKeys(String.valueOf(enterPin));
                enterPin++;

            }
        }
    }

    @And("User verify the navigates to set password page")
    public void userVerifyTheNavigatesToSetPasswordPage() {
        try {
            if (forgotPasswordPage.tryAgainButton.isDisplayed()) {
                attachScreenshot(driver, scenario);
                Assert.fail("Navigates to set password page is failed");
            }
        } catch (NoSuchElementException exception) {
        }
    }

    @And("User update the password in test data sheet")
    public void userUpdateThePasswordInTestDataSheet() {
        logger.info("Password updated in test data sheet completed");
        inputValueInExcel("ForgotPasswordPage", "updatedPasswordETB", fileReader.forgotPasswordTestData.get("updatedPasswordETB"), getPasswordForLogIn());
    }

}

