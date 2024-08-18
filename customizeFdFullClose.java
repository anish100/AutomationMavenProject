package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.asserts.SoftAssert;
import pom.*;
import reusable.Base;
import reusable.TestContext;

public class customizeFdFullClose  extends Base {
    private static final Logger logger = LogManager.getLogger(DepositDashboardStepDef.class);
    HomePage homePage;
    DepositsDashboardPage depositsDashboardPage;
    AccountStatementPage accountStatementPage;
    MoneyTransferPage moneyTransferPage;
    DepositDetailsPage depositDetailsPage;
    FDPage fdPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    DepositDashboardStepDef depositDashboardStepDef;
    DepositDetailsStepDef depositDetailsStepDef;
    DepositClosurePage depositClosurePage;
    HomePageStepDef homePageStepDef;

    public customizeFdFullClose(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        depositsDashboardPage = context.getPageObjectManager().getDepositDashboardPage();
        homePage = context.getPageObjectManager().getHomePage();
        fdPage = context.getPageObjectManager().getFDPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        moneyTransferPage = context.getPageObjectManager().getMoneyTransferPage();
        accountStatementPage = context.getPageObjectManager().getAccountStatementPage();
        depositDashboardStepDef=new DepositDashboardStepDef(context);
        depositDetailsStepDef=new DepositDetailsStepDef(context);
        depositDetailsPage=context.getPageObjectManager().getDepositDetailsPage();
        depositClosurePage=context.getPageObjectManager().getDepositClosurePage();
        homePageStepDef=new HomePageStepDef(context);
    }

    @And("User find available  fd list in deposit dashboard page")
    public void userFindAvailableFdListInDepositDashboardPage() {
        int sizeOfFdInDashBoardPage = depositsDashboardPage.noAvailableOfFd.size();

        int numberFdToBeClosed=sizeOfFdInDashBoardPage-3;
        for(int i=1;i<numberFdToBeClosed;i++){
            depositsDashboardPage.viewButtonDashboardPage.click();
            depositDetailsStepDef.userClicksOnCloseAccountButtonOfDepositDetailsPage();
            explicitWait(driver, 20).until(ExpectedConditions.elementToBeClickable(depositDetailsPage.fullCloserRadioButton)).click();
            depositDetailsPage.closeDepositProceedButton.click();
            waitTillInvisibilityOfLoader(driver);
//            depositClosurePage.selectCreditAccount(fileReader.fDTestData.get("creditAccount"));
//            waitTillInvisibilityOfLoader(driver);
            depositClosurePage.summaryNextButton.click();
            waitTillInvisibilityOfLoader(driver);
            homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
            depositClosurePage.summaryNextButton.click();
            homePageStepDef.userEnterTheOtpAndVerifyTheOtp();
            waitTillInvisibilityOfLoader(driver);
            fdPage.backToDepositButton.click();
            waitTillInvisibilityOfLoader(driver);
        }

    }
}
