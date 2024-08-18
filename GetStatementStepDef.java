package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.atp.Switch;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import pom.AccountStatementPage;
import pom.GetStatementPage;
import pom.LoanPage;
import reusable.Base;
import reusable.TestContext;

import java.io.File;
import java.nio.channels.ScatteringByteChannel;
import java.time.Duration;

public class GetStatementStepDef extends Base {

    private static Logger logger = LogManager.getLogger(GetStatementStepDef.class);
    GetStatementPage getStatementPage;
    LoanPage loanPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String currentUrl;

    public GetStatementStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        getStatementPage = context.getPageObjectManager().getStatementPage();
        loanPage = context.getPageObjectManager().getLoanPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
    }

    @When("User clicks get statement button")
    public void userClicksGetStatementButton() {
        currentUrl = driver.getCurrentUrl();
        waitTillElementToBeClickable(driver, getStatementPage.getStatementButton);
        getStatementPage.getStatementButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify get statement page")
    public void userVerifyGetStatementPage() {
        softAssert = new SoftAssert();
        logger.info("Current page header name is :" + getStatementPage.getStatementHeader.getText());
        softAssert.assertTrue(getStatementPage.getStatementHeader.getText().contains("Get Statement"), "get statement page header visible");
        softAssert.assertTrue(getStatementPage.clickAccountType.isDisplayed(), "account type bar not displayed");
        softAssert.assertTrue(getStatementPage.clickAccountNumber.isDisplayed(), "account number bar not displayed");
        softAssert.assertTrue(getStatementPage.durationSelect.isDisplayed(), "duration bar not displayed");
        if (currentUrl.contains("loanstatement")) {
            logger.info("User is on loan get statement page");
            softAssert.assertTrue(getStatementPage.pdfRadioButton.isDisplayed(), "pdf radio button not displayed");
        } else {
            scrollIntoView(driver, getStatementPage.xlsRadioButton);
            softAssert.assertTrue(getStatementPage.xlsRadioButton.isDisplayed(), "xls download button not displayed");
            softAssert.assertTrue(getStatementPage.csvRadioButton.isDisplayed(), "csv radio button not displayed");
            softAssert.assertTrue(getStatementPage.pdfRadioButton.isDisplayed(), "pdf radio button not displayed");
        }
        // waitTillInvisibilityOfLoader(driver);
        scrollIntoViewUp(driver, getStatementPage.getStatementHeader);
        waitTillElementToBeClickable(driver, getStatementPage.emailButton);
        getStatementPage.emailButton.click();
        waitTillVisibilityElement(driver, getStatementPage.clickAccountType);
        softAssert.assertTrue(getStatementPage.emailAddressEnter.isDisplayed(), "download section email enter text box not displayed");
        staticWait(2000);
        getStatementPage.addAnotherMailID.click();
        softAssert.assertTrue(getStatementPage.emailAddressEnterLists.size() == 2, "added another email tab not showing");
        staticWait(2000);
        getStatementPage.addAnotherMailID.click();
        softAssert.assertTrue(getStatementPage.emailAddressEnterLists.size() == 3, "added another email tab not showing");
        softAssert.assertFalse(getStatementPage.addAnotherMailID.isEnabled(), "after reaching maximum limit for added new mail option not disabled");
        softAssert.assertTrue(getStatementPage.clickAccountType.isDisplayed(), "download section account type bar not displayed");
        softAssert.assertTrue(getStatementPage.clickAccountNumber.isDisplayed(), "download section account number bar not displayed");
        softAssert.assertTrue(getStatementPage.durationSelect.isDisplayed(), "download section duration bar not displayed");
        softAssert.assertTrue(getStatementPage.emailButton.isDisplayed(), "download section email button not displayed");
        if (currentUrl.contains("loanstatement")) {
            softAssert.assertTrue(getStatementPage.pdfRadioButton.isDisplayed(), "email section pdf radio button not displayed");
        } else {
            scrollIntoView(driver, getStatementPage.xlsRadioButton);
            softAssert.assertTrue(getStatementPage.xlsRadioButton.isDisplayed(), "email section xls download button not displayed");
            softAssert.assertTrue(getStatementPage.csvRadioButton.isDisplayed(), "email section csv radio button not displayed");
            softAssert.assertTrue(getStatementPage.pdfRadioButton.isDisplayed(), "email section pdf radio button not displayed");
        }

        try {
            softAssert.assertAll();
            //  attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User enters all details on download section")
    public void userEntersAllDetailsOnDownloadSection() {
        softAssert = new SoftAssert();
        getStatementPage.downloadButton.click();
        softAssert.assertTrue(getStatementPage.getStatementHeader.getText().contains("Get Statement"), "get statement page header visible");
        if (currentUrl.contains("loanstatement")) {
            logger.info("User select the loan account number is :" + fileReader.loanTestData.get("getStatementTransaction type"));
            getStatementPage.clickAccountType.click();
            getStatementPage.selectAccountType(fileReader.loanTestData.get("getStatementTransaction type")).click();
            waitTillInvisibilityOfLoader(driver);
            getStatementPage.clickAccountNumber.click();
            logger.info("User select the loan account number is :" + fileReader.accStatementTestData.get("loanAccountNumber"));
            scrollIntoViewUp(driver, getStatementPage.selectAccountNumber(fileReader.loanTestData.get("loanAccountNumber")));
            getStatementPage.selectAccountNumber(fileReader.loanTestData.get("loanAccountNumber")).click();
            waitTillInvisibilityOfLoader(driver);
            getStatementPage.durationSelect.click();
            String fromDateSelect = fileReader.loanTestData.get("transactionFromDate");
            String[] splitDate = fromDateSelect.split("/");
            String fromDate = splitDate[0];
            String fromMonth = String.valueOf(Integer.valueOf(splitDate[1]) - 1);
            String fromYear = splitDate[2];
            logger.info("From Year " + fromYear);
            getStatementPage.yearClick.click();
            selectDDByValue(getStatementPage.yearSelect, fromYear);
            getStatementPage.monthClick.click();
            selectDDByValue(getStatementPage.monthSelect, fromMonth);
            getStatementPage.getDateFromDD(fromDate).click();
            String toSelect = fileReader.loanTestData.get("transactionToDate");
            String[] splitToDate = toSelect.split("/");
            String toDate = splitToDate[0];
            String toMonth = String.valueOf(Integer.valueOf(splitToDate[1]) - 1);
            String toYear = splitToDate[2];
            getStatementPage.yearClick.click();
            selectDDByValue(getStatementPage.yearSelect, toYear);
            getStatementPage.monthClick.click();
            selectDDByValue(getStatementPage.monthSelect, toMonth);
            getStatementPage.getDateFromDD(toDate).click();
        } else {
            getStatementPage.clickAccountType.click();
            logger.info("User select the account type is :" + fileReader.accStatementTestData.get("getStatementTransaction type"));
            getStatementPage.selectAccountType(fileReader.accStatementTestData.get("getStatementTransaction type")).click();
            waitTillInvisibilityOfLoader(driver);
            getStatementPage.clickAccountNumber.click();
            logger.info("User select the account number is :" + fileReader.accStatementTestData.get("accountNumber"));
            getStatementPage.selectAccountNumber(fileReader.accStatementTestData.get("accountNumber")).click();
            waitTillInvisibilityOfLoader(driver);
            getStatementPage.durationSelect.click();
            String fromDateSelect = fileReader.accStatementTestData.get("transactionFromDate");
            String[] splitDate = fromDateSelect.split("/");
            String fromDate = splitDate[0];
            String fromMonth = String.valueOf(Integer.valueOf(splitDate[1]) - 1);
            String fromYear = splitDate[2];
            getStatementPage.yearClick.click();
            selectDDByValue(getStatementPage.yearSelect, fromYear);
            getStatementPage.monthClick.click();
            selectDDByValue(getStatementPage.monthSelect, fromMonth);
            getStatementPage.getDateFromDD(fromDate).click();
            String toSelect = fileReader.accStatementTestData.get("transactionToDate");
            String[] splitToDate = toSelect.split("/");
            String toDate = splitToDate[0];
            String toMonth = String.valueOf(Integer.valueOf(splitToDate[1]) - 1);
            String toYear = splitToDate[2];
            getStatementPage.yearClick.click();
            selectDDByValue(getStatementPage.yearSelect, toYear);
            getStatementPage.monthClick.click();
            selectDDByValue(getStatementPage.monthSelect, toMonth);
            getStatementPage.getDateFromDD(toDate).click();
        }
        getStatementPage.durationApplyButton.click();
        waitTillInvisibilityOfLoader(driver);


        try {
            softAssert.assertAll();
            //  attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User verify get statement download file")
    public void userVerifyGetStatementDownloadFile() {
        softAssert = new SoftAssert();
        if (currentUrl.contains("loanstatement")) {
            getStatementPage.downloadFileType(fileReader.loanTestData.get("downloadType")).click();
            getStatementPage.downloadStatementButton.click();
            softAssert.assertTrue(getStatementPage.downloadInitiate.isDisplayed(), "downloading pop up not displayed");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(ExpectedConditions.invisibilityOf(getStatementPage.downloadInitiate));
            staticWait(3000);
            try {
                if (getStatementPage.downloadErrorPopUp.isDisplayed()) {
                    attachScreenshot(driver, scenario);
                    softAssert.fail("Download statement failed");
                }
            } catch (NoSuchElementException e) {
            }
            File transactionPdfFile = new File("C:/Users/987993/Downloads/Loan_Statement.pdf");
            staticWait(4000);
            softAssert.assertTrue(transactionPdfFile.exists(), "File download failed");
            logger.info("Statement download location is :" + transactionPdfFile);
            waitTillInvisibilityOfLoader(driver);
            if (transactionPdfFile.exists()) {
                transactionPdfFile.delete();

            }

        } else {
            waitForPageLoad(driver);
            logger.info("User select the file download type is PDF");
            getStatementPage.downloadFileType("PDF").click();
            getStatementPage.downloadStatementButton.click();
            softAssert.assertTrue(getStatementPage.downloadInitiate.isDisplayed(), "downloading pop up not displayed");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(ExpectedConditions.invisibilityOf(getStatementPage.downloadInitiate));

            try {
                if (getStatementPage.downloadErrorPopUp.isDisplayed()) {
                    attachScreenshot(driver, scenario);
                    logger.error("Download pop up error message displayed");
                    getStatementPage.downloadErrorPopUpBackButton.click();
                }
            } catch (NoSuchElementException e) {
            }


            if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("pdf")) {
                File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.pdf");
                staticWait(4000);
                softAssert.assertTrue(downloadedFile.exists(), "File download failed");
                logger.info("Account statement file location is " + downloadedFile);
                waitTillInvisibilityOfLoader(driver);
                if (downloadedFile.exists()) {
                    downloadedFile.delete();

                }
            }
            if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("xls")) {
                File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.xls");
                staticWait(4000);
                softAssert.assertTrue(downloadedFile.exists(), "File download failed");
                waitTillInvisibilityOfLoader(driver);
                if (downloadedFile.exists()) {
                    downloadedFile.delete();
                }
            }
            if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("csv")) {
                File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.csv");
                staticWait(4000);
                softAssert.assertTrue(downloadedFile.exists(), "File download failed");
                waitTillInvisibilityOfLoader(driver);
                if (downloadedFile.exists()) {
                    downloadedFile.delete();
                }
            }

            try {
                softAssert.assertAll();
                //  attachScreenshot(driver, scenario);
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());

            }
        }
    }

    @When("User click email button in getStatement page in loan")
    public void userClickEmailButtonInGetStatementPageInLoan() {
        scrollIntoViewUp(driver, getStatementPage.emailButton);
        waitTillInvisibilityOfLoader(driver);
        getStatementPage.emailButton.click();
    }

    @And("User enters all needed details on getStatement page in loan")
    public void userEntersAllNeededDetailsOnGetStatementPageInLoan() {

        waitTillElementToBeClickable(driver, getStatementPage.emailAddressEnter);
        getStatementPage.emailAddressEnterInt(1).sendKeys(Keys.chord(Keys.chord(Keys.chord(Keys.CONTROL, "a")), fileReader.accStatementTestData.get("emailId")));
        /**Add Another Email id **/
        getStatementPage.addAnotherMailID.click();
        getStatementPage.emailAddressEnterInt(2).sendKeys(Keys.chord(Keys.chord(Keys.chord(Keys.CONTROL, "a")), "aathira.2911@gmail.com"));
        if (currentUrl.contains("loanstatement")) {
            getStatementPage.clickAccountType.click();
            getStatementPage.selectAccountType(fileReader.loanTestData.get("getStatementTransaction type")).click();
            waitForPageLoad(driver);
            getStatementPage.clickAccountNumber.click();
            getStatementPage.selectAccountNumber(fileReader.loanTestData.get("loanAccountNumber")).click();
            waitTillInvisibilityOfLoader(driver);
        } else {
            getStatementPage.clickAccountType.click();
            getStatementPage.selectAccountType(fileReader.accStatementTestData.get("getStatementTransaction type")).click();
            waitTillInvisibilityOfLoader(driver);
            getStatementPage.clickAccountNumber.click();
            getStatementPage.selectAccountNumber(fileReader.accStatementTestData.get("accountNumber")).click();
            waitTillInvisibilityOfLoader(driver);
        }
        getStatementPage.durationSelect.click();

        String fromDateSelect = fileReader.accStatementTestData.get("transactionFromDate");
        String[] splitDate = fromDateSelect.split("/");
        String fromDate = splitDate[0];
        String fromMonth = String.valueOf(Integer.valueOf(splitDate[1]) - 1);
        String fromYear = splitDate[2];

        getStatementPage.yearClick.click();
        selectDDByValue(getStatementPage.yearSelect, fromYear);
        getStatementPage.monthClick.click();
        selectDDByValue(getStatementPage.monthSelect, fromMonth);
        getStatementPage.getDateFromDD(fromDate).click();

        String toSelect = fileReader.accStatementTestData.get("transactionToDate");
        String[] splitToDate = toSelect.split("/");
        String toDate = splitToDate[0];

        String toMonth = String.valueOf(Integer.valueOf(splitToDate[1]) - 1);
        String toYear = splitToDate[2];

        // waitTillInvisibilityOfLoader(driver);
        getStatementPage.yearClick.click();
        selectDDByValue(getStatementPage.yearSelect, toYear);
        getStatementPage.monthClick.click();
        selectDDByValue(getStatementPage.monthSelect, toMonth);
        getStatementPage.getDateFromDD(toDate).click();
        getStatementPage.durationApplyButton.click();
        getStatementPage.downloadFileType(fileReader.accStatementTestData.get("downloadType")).click();
        //waitTillInvisibilityOfLoader(driver);
        getStatementPage.getStatementSendEmailButton.click();

        try {
            if (getStatementPage.downloadErrorPopUp.isDisplayed()) {
                attachScreenshot(driver, scenario);
                getStatementPage.downloadErrorPopUpBackButton.click();
                softAssert.fail("Download failed please verify the screen shot");
            }
        } catch (NoSuchElementException e) {
        }


    }

    @Then("User validates the email sent successfully message")
    public void userValidatesTheEmailSentSuccessfullyMessage() {
        softAssert = new SoftAssert();
        try {
            if (getStatementPage.downloadErrorPopUpBackButton.isDisplayed()) {
                attachScreenshot(driver, scenario);
                logger.error("Download pop up error message displayed");
                getStatementPage.downloadErrorPopUpBackButton.click();
                softAssert.fail("Email sent failed please verify the screen shot");
            } else {
                softAssert.assertTrue(getStatementPage.emailingPopUp.isDisplayed(), "email sending popup not displayed");
                waitTillInvisibilityElement(driver, getStatementPage.emailingPopUp);
                scrollIntoView(driver, getStatementPage.toastMessage);
                softAssert.assertTrue(getStatementPage.toastMessage.getText().contains("uccessfully"), "Email sent successfully message not displayed");
            }
        } catch (NoSuchElementException e) {
        }

        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    /**
     * The given below codes referred to Download Function
     **/

    @Then("User chooses the necessary information to {string} for {string}")
    public void userChoosesTheNecessaryInformationToFor(String downloadOrEmail, String statementModule) {
        if (statementModule.contains("loan statement")) {
            waitTillElementToBeClickable(driver, getStatementPage.clickAccountType);
            getStatementPage.clickAccountType.click();
            try {
                waitTillElementToBeClickable(driver, getStatementPage.selectAccountType(fileReader.loanTestData.get("getStatementTransaction type")));
                getStatementPage.selectAccountType(fileReader.loanTestData.get("getStatementTransaction type")).click();
                waitTillElementToBeClickable(driver, getStatementPage.clickAccountNumber);
            } catch (Exception e) {
                attachScreenshot(driver, scenario);
                softAssert.fail("Select Account options not showing");
            }
            scrollIntoView(driver, getStatementPage.clickAccountNumber);
            getStatementPage.clickAccountNumber.click();
            staticWait(2000);
            waitTillElementToBeClickable(driver, getStatementPage.selectAccountNumberByIndex);
            logger.info(fileReader.loanTestData.get("loanAccountNumber"));
            getStatementPage.selectAccountNumber(fileReader.loanTestData.get("loanAccountNumber")).click();
            waitTillInvisibilityOfLoader(driver);
            getStatementPage.durationSelect.click();
            getStatementPage.last6monthCalendar.click();
            waitTillElementToBeClickable(driver, getStatementPage.durationApplyButton);
        } else if(statementModule.contains("account statement")) {
            getStatementPage.clickAccountType.click();
            try {
                getStatementPage.selectAccountType(fileReader.accStatementTestData.get("getStatementTransaction type")).click();
                waitTillInvisibilityOfLoader(driver);
            } catch (NullPointerException e) {
                attachScreenshot(driver, scenario);
                softAssert.fail("Select account type options not displayed,Please refer screen shot");
                logger.error(e.toString());
            }
            getStatementPage.clickAccountNumber.click();
            getStatementPage.selectAccountNumber(getAccountNumber()).click();
      //      getStatementPage.selectAccountNumberByIndex.click();
            fluentWaitTillInVisibilityOfLoader(driver);
            clickOnButton(getStatementPage.durationSelect);
         //   getStatementPage.durationSelect.click();
            String fromDateSelect = fileReader.accStatementTestData.get("transactionFromDate");
            String[] splitFromDate = fromDateSelect.split("/");
            String fromDate = splitFromDate[0];
            String fromMonth = String.valueOf(Integer.valueOf(splitFromDate[1]) - 1);
            String fromYear = splitFromDate[2];

            getStatementPage.yearClick.click();
            selectDDByValue(getStatementPage.yearSelect, fromYear);
            getStatementPage.monthClick.click();
            selectDDByValue(getStatementPage.monthSelect, fromMonth);
            getStatementPage.getDateFromDD(fromDate).click();
            staticWait(2000);
            String toSelect = fileReader.accStatementTestData.get("transactionToDate");
            String[] splitToDate = toSelect.split("/");
            String toDate = splitToDate[0];
            String toMonth = String.valueOf(Integer.valueOf(splitToDate[1]) - 1);
            String toYear = splitToDate[2];
            getStatementPage.yearClick.click();
            selectDDByValue(getStatementPage.yearSelect, toYear);
            getStatementPage.monthClick.click();
            selectDDByValue(getStatementPage.monthSelect, toMonth);
            getStatementPage.getDateFromDD(toDate).click();
        }
        getStatementPage.durationApplyButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User validated the downloadStatement in {string} format on get statement page")
    public void userValidatedTheDownloadStatementInFormatOnGetStatementPage(String fileFormat) {
        softAssert = new SoftAssert();
        switch (fileFormat) {
            case "pdf":
                getStatementPage.downloadFileType("PDF").click();
                break;
            case "xlsx":
                getStatementPage.downloadFileType("XLS").click();
                break;
            case "csv":
                getStatementPage.downloadFileType("CSV").click();
                break;
        }
        getStatementPage.downloadStatementButton.click();
        softAssert.assertTrue(getStatementPage.downloadInitiate.isDisplayed(), "downloading pop up not displayed");
        waitTillInvisibilityElement(driver, getStatementPage.downloadInitiate);
        try {
            if (getStatementPage.downloadErrorPopUp.isDisplayed()) {
                attachScreenshot(driver, scenario);
                logger.error("Download pop up error message displayed");
                getStatementPage.downloadErrorPopUpBackButton.click();
            }
        } catch (NoSuchElementException e) {
            logger.info("Download pop up error message not displayed");

        }
        File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement." + fileFormat + "");
        staticWait(2000);
        softAssert.assertTrue(downloadedFile.exists(), "File download failed");
        logger.info("Account statement file location is " + downloadedFile);
        waitForPageLoad(driver);
        if (downloadedFile.exists()) {
            downloadedFile.delete();
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on send email button")
    public void userClicksOnSendEmailButton() {
        getStatementPage.downloadFileType("PDF").click();
        getStatementPage.getStatementSendEmailButton.click();
    }


    @And("User validated the sending email in {string} format on get statement page")
    public void userValidatedTheSendingEmailInFormatOnGetStatementPage(String fileFormat) {
        switch (fileFormat) {
            case "pdf":
                getStatementPage.downloadFileType("PDF").click();
                break;
            case "xlsx":
                getStatementPage.downloadFileType("XLS").click();
                break;
            case "csv":
                getStatementPage.downloadFileType("CSV").click();
                break;
        }
        getStatementPage.getStatementSendEmailButton.click();
    }



}



