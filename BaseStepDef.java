package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pom.FDPage;
import pom.HomePage;
import reusable.Base;
import reusable.TestContext;
import screenRecord.ScreenRecorderUtil;

public class BaseStepDef extends Base {

    TestContext testContext;
    HomePage homePage;
    FDPage fdPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;


    public BaseStepDef(TestContext context) {
        testContext = context;
        driver = testContext.getDriverManager().getWebDriver();
        scenario = testContext.getScenario();
        fileReader = testContext.getFileReaderManager().getExcelFileReader();
        configFileReader = testContext.getFileReaderManager().getConfigReader();
    }

    @Given("User has launched the merchant application")
    public void userHasLaunchedTheMerchantApplication() {
        if (configFileReader.getProperty("screenRecordingOption").contains("on")) {
            try {
                ScreenRecorderUtil.startRecord(scenario.getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }}
            driver.get(configFileReader.getApplicationUrl());
            waitForPageLoad(driver);

    }
    @Given("User has read testData {string} from excel sheet")
    public void userReadTestDataFromExcelSheet(String tcId) {
        fileReader.getAllTestCaseData(tcId);
    }

}

