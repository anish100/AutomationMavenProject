package stepDefs;

import com.google.common.collect.ImmutableMap;
import dataProviders.ConfigFileReader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.util.ResultsUtils;
import net.bytebuddy.pool.TypePool;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import reusable.Base;
import reusable.TestContext;
import screenRecord.ScreenRecorderUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import static screenRecord.ScreenRecorderUtil.getScreenRecordingFileName;


public class Hooks extends Base {
    private static Logger logger = LogManager.getLogger(Hooks.class);
    TestContext testContext;
    ConfigFileReader configFileReader;


    public Hooks(TestContext context) {
        logger.info("hooks class object is created");
        testContext = context;
        configFileReader = testContext.getFileReaderManager().getConfigReader();

    }
    @Before(order=0)
    public void checkPreviousScenarios(Scenario scenario) {
       if(isAbortScenarios()){
           logger.error("A Something went wrong pop-up appeared during the previous scenario, causing the current scenario to be skipped");
           logger.error("Skipped Scenario name : "+scenario.getName());
           throw new RuntimeException("Scenarios are skipping due to error popup");
       }
       else{
           logger.info("Scenario execution working");
       }
    }
    @Before(order=1)
    public void beforeSetup(Scenario scenario) {
        testContext.setScenario(scenario);
        logger.info("This will run before the scenario run");
        errorList.clear();
    }

    @After(order = 1)
    public void Check_The_Execution_For_Any_Failures(Scenario scenario) {
        if (!scenario.isFailed()) {
            if (!getErrorList().isEmpty())
                Assert.fail("There are few assert failures please have a look on the report");
        }
        if (scenario.isFailed()) {
            attachScreenshot(testContext.getDriverManager().getWebDriver(), scenario);
            Assert.fail("There is a scenario failure. Please take a look at the report.");

        }
    }
    @After(order = 0)
    public  void tearDown() {
        testContext.getDriverManager().getWebDriver().quit();
        if (configFileReader.getProperty("screenRecordingOption").contains("on")) {
            try {
                ScreenRecorderUtil.stopRecord();
                logger.info("Screen Recording Stopped");
                File location = new File("C:\\Users\\987993\\Merchant_Web_Automation\\target\\test-recordings\\" + getScreenRecordingFileName());
                InputStream vid = new FileInputStream(location.toString());
            //    Allure.addAttachment(featureFileName(scenario) + " Execution Recording Video", "video/.avi", vid, "avi");
            } catch (Exception e) {
             //   throw new RuntimeException(e);
            }
        }
    }

}
