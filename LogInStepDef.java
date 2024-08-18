package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;
import pom.*;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import java.util.ListIterator;


public class LogInStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(LogInStepDef.class);

    LogInPage logInPage;
    DashboardPage dashboardPage;
    LoanPage loanPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;


    public LogInStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        logInPage = context.getPageObjectManager().getLogInPage();
        dashboardPage = context.getPageObjectManager().getDashboardPage();
        loanPage = context.getPageObjectManager().getLoanPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
    }

    @When("User clicks on log in button")
    public void userClicksOnLogInButton() throws Exception {
        staticWait(2000);
        javaScriptExecutorClickElement(driver,logInPage.logInButton);
        //clickOnButton(logInPage.logInButton);
    }

    @Then("User verify the error message user not found")
    public void userVerifyTheErrorMessageUserNotFound() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(logInPage.merchantUserNotFoundPopup.isDisplayed(), "log in user not found pop up not displayed");
        softAssert.assertTrue(logInPage.userLockedPopUpOkButton.isDisplayed(), "in pop up ok button not displayed");
        logInPage.userLockedPopUpOkButton.click();
    }

    @Then("User verify the log in page")
    public void userVerifyTheLogInPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("login"), "log in page url not displayed");
        softAssert.assertTrue(logInPage.logInPageHeader.isDisplayed(), "log in page header not displayed");
        softAssert.assertTrue(logInPage.logInPageName.isDisplayed(), "log in page name not displayed");
        softAssert.assertTrue(logInPage.userNameField.isDisplayed(), "username field not displayed");
        softAssert.assertTrue(logInPage.passwordField.isDisplayed(), "password field not displayed");
        softAssert.assertTrue(logInPage.logInButton.isDisplayed(), "log in button not displayed");
        softAssert.assertTrue(logInPage.forgetPasswordOption.isDisplayed(), "forget password option not displayed");
        softAssert.assertTrue(logInPage.registerNowOption.isDisplayed(), "register now option not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks profile menu")
    public void userClicksProfileMenu() {
        dashboardPage.profileMenu.click();
        waitTillElementToBeClickable(driver, logInPage.logOutInPage);
    }

    @Then("User clicks on log out option")
    public void userClicksOnLogOutOption() {
        clickOnButton(logInPage.logOutInPage);
    }

    @And("User verify the popup for log out")
    public void userVerifyThePopupForLogOut() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(logInPage.logOutPopUpHeader.isDisplayed(), "log out pop up header not displayed");
        softAssert.assertTrue(logInPage.logOutPopUpText.isDisplayed(), "log out pop up text not displayed");
        softAssert.assertTrue(logInPage.logOutPopUpCancelButton.isDisplayed(), "logout pop up cancel button not displayed");
        softAssert.assertTrue(logInPage.logOutPopUpLogOutButton.isDisplayed(), "logout button in pop up not displayed ");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks log out button in popup")
    public void userClicksLogOutButtonInPopup() {
        clickOnButton(logInPage.logOutPopUpLogOutButton);
        waitTillVisibilityElement(driver, logInPage.logOutMessage);
    }

    @Then("User verify the log out status")
    public void userVerifyTheLogOutStatus() {
        softAssert = new SoftAssert();
        String homePageUrl = "https://mibsit-mr.aubankuat.in/";
        softAssert.assertEquals(driver.getCurrentUrl(), homePageUrl, "home page url is not matched");
        softAssert.assertTrue(logInPage.logOutMessage.isDisplayed(), "log out successful message not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    /*************************** In Valid Scenario ********************************/

//    @And("User enters in valid userName and valid password")
//    public void userEntersInValidUserNameAndValidPassword() {
//        logInPage.userNameField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getUserNameForLogIn() + "t76rqe76");
//        logInPage.passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getPasswordForLogIn());
//    }

//    @And("User enters valid userName and invalid password")
//    public void usersEnterValidUserNameAndInvalidPassword() {
//        logInPage.userNameField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getUserNameForLogIn());
//        logInPage.passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getPasswordForLogIn() + "gyusiu");
//    }


    @And("User verify the error message Invalid Login Credential")
    public void userVerifyTheErrorMessageInvalidLoginCredential() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(logInPage.userNameError.getText().contains("Invalid Login Credential"), "log in error message not be the same ");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the error message Invalid Password")
    public void userVerifyTheErrorMessageInvalidPassword() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(logInPage.userNameError.getText().contains("Invalid Password, 2 more attempt left"), "log in error message not be the same ");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the error message for second time Invalid Password")
    public void userVerifyTheErrorMessageForSecondTimeInvalidPassword() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(logInPage.userNameError.getText().contains("Invalid Password, 1 more attempt left"), "log in error message not be the same ");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }
//
//    @When("User enters invalid userName and invalid password")
//    public void userEntersInvalidUserNameAndInvalidPassword() {
//        staticWait(2000);
////        logInPage.userNameField.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.logInTestData.get("inValidUserName"));
////        logInPage.passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.logInTestData.get("inValidPassword"));
//        logInPage.userNameField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getUserNameForLogIn() + "t76rqe76");
//        logInPage.passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a"), getPasswordForLogIn() + "gyusiu");
//
//    }

    @And("User verify the error message for application locked message")
    public void userVerifyTheErrorMessageForApplicationLockedMessage() {
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(logInPage.userLockedPopUpMessage.getText().contains("Your application is locked because of too many"), "pop up not displayed");
            attachScreenshot(driver, scenario);
            logInPage.userLockedPopUpOkButton.click();
        } catch (NoSuchElementException e) {
        }
    }

}








