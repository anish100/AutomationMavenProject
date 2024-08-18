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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import pom.AccountStatementPage;
import reusable.Base;
import reusable.TestContext;

import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AccountStatementStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(AccountStatementStepDef.class);
    AccountStatementPage accountStatementPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String referenceNumber;
    String expectTransactionAmount;
    String beforeSortingButtonPositionViewBox;
    String dateFilterTextDetails;
    boolean isException;

    public AccountStatementStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        accountStatementPage = context.getPageObjectManager().getAccountStatementPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();

    }

    @And("User select required account from select account")
    public void userSelectRequiredAccountFromSelectAccount() {
        waitTillElementToBeClickable(driver, accountStatementPage.accountDD);
        accountStatementPage.accountDD.click();
        if (scenario.getName().contains("Validate Account statement Journey") || scenario.getName().contains("Validate Download function in Account Statement")) {
            accountStatementPage.selectAccountFromDD(getAccountNumber());
        } else if (scenario.getName().contains("Validate more than 999 transaction in between the dates")) {
            accountStatementPage.selectAccountFromDD(fileReader.accStatementTestData.get(">999TPPAccountNo"));
        } else if (scenario.getName().contains("Validate more than 999 transaction in a day")) {
            accountStatementPage.selectAccountFromDD(fileReader.accStatementTestData.get(">999TPDAccountNo"));
        }
        waitTillInvisibilityOfLoader(driver);

    }

    @Then("User can validate the transaction section")
    public void userCanValidateTheTransactionSection() {
        softAssert = new SoftAssert();
        isException = false;
        dateFilterTextDetails = accountStatementPage.dateFilterTextBegin.getText();

        if (accountStatementPage.noResultFound.getText().contains("No Result Found")) {
            accountStatementPage.dateFilter.click();
            accountStatementPage.lastOneYearFilter.click();
            waitTillInvisibilityOfLoader(driver);
            accountStatementPage.applyButton.click();
            //attachScreenshot(driver,scenario);
        }
        try {
            softAssert.assertTrue(!accountStatementPage.transactionDate.getText().isEmpty(), "Transaction Date not Visible");
            softAssert.assertTrue(!accountStatementPage.transactionDescription.getText().isEmpty(), "Description Not Visible");
            softAssert.assertTrue(!accountStatementPage.amount.getText().isEmpty(), "Amount Not Visible");
            softAssert.assertTrue(!accountStatementPage.balanceFirstRow.getText().isEmpty(), "Balance Not Visible");
            softAssert.assertTrue(accountStatementPage.downloadPdfButton.isDisplayed(), "Download PDF Button Not Displayed");
            softAssert.assertTrue(accountStatementPage.downloadXlsButton.isDisplayed(), "Download XLS Button Not Displayed");
            softAssert.assertTrue(accountStatementPage.downloadCsvButton.isDisplayed(), "Download CSV Button Not Displayed");
            softAssert.assertTrue(accountStatementPage.currentFinancialDownloadButton.isDisplayed(), "Current financial year Download Button not Displayed");
            softAssert.assertTrue(accountStatementPage.currentFinancialEmailButton.isDisplayed(), "Current financial year Email Button not Displayed");
            softAssert.assertTrue(accountStatementPage.previousFinancialDownloadButton.isDisplayed(), "Previous financial year Download Button not Displayed");
            softAssert.assertTrue(accountStatementPage.previousFinancialEmailButton.isDisplayed(), "Previous financial year Email Button not Displayed");
            softAssert.assertTrue(accountStatementPage.getStatementButton.isDisplayed(), "Email statement button not Displayed");
            expectTransactionAmount = accountStatementPage.amount.getText().replace("\\n", "").trim();
        } catch (NoSuchElementException e) {
            scrollIntoView(driver, accountStatementPage.dateFilter);
            attachScreenshot(driver, scenario);
            logger.error("Transaction section not visible ");
            isException = true;
            throw new NoSuchElementException("Transaction section not visible ");

        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User click on any one of the transaction from the transaction list")
    public void userClickOnAnyOneOfTheTransactionFromTheTransactionList() {
        if (accountStatementPage.noResultFound.getText().contains("No Result Found")) {
            accountStatementPage.dateFilter.click();
            accountStatementPage.lastOneYearFilter.click();
            waitTillInvisibilityOfLoader(driver);
            accountStatementPage.applyButton.click();
            waitTillInvisibilityOfLoader(driver);
            //attachScreenshot(driver,scenario);
        }
        accountStatementPage.transactionPopup.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User validate the popup details")
    public void userValidateThePopupDetails() throws AWTException {
        softAssert = new SoftAssert();
        referenceNumber = accountStatementPage.popupReferenceNo.getText();
        String actualTransactionAmount = accountStatementPage.popupTransactionAmount.getText().replaceAll("[^\\d.]", "").replace("\\n", "").trim();
        softAssert.assertEquals(actualTransactionAmount, expectTransactionAmount.replaceAll("[^\\d.]", ""), "Transaction amount not matched");
        softAssert.assertEquals(accountStatementPage.popupTransactionDate.getText(), accountStatementPage.transactionDateText.getText(), "Transaction date Not matched");
        softAssert.assertEquals(accountStatementPage.popupTransactionTime.getText().replace(" ", ""), accountStatementPage.transactionTime.getText().replace(" ", ""), "Transaction time not matched");
        softAssert.assertEquals(accountStatementPage.popupUserAccountDetails.getText().replace(" ", ""), accountStatementPage.descriptionDetails.getText().replace(" ", ""), "User description details not matched");
        accountStatementPage.popUpCopyButton.click();
        waitTillVisibilityElement(driver, accountStatementPage.successfullyMessage);
        softAssert.assertTrue(accountStatementPage.successfullyMessage.isDisplayed(), "copied successfully message not displayed");
        String clipBoardValue = getCopiedValue().trim();
        softAssert.assertEquals(referenceNumber.trim(), clipBoardValue, "copied value not be the same");
        logger.info("Copied from Button " + getCopiedValue());
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User verify the transactions download in popup")
    public void userVerifyTheTransactionsDownloadInPopup() {
        softAssert = new SoftAssert();
        File transactionPdfFilePreMin = new File("C:/Users/987993/Downloads/" + "TxnRpt_" + getCurrentDateTime() + ".pdf");
        waitTillElementToBeClickable(driver, accountStatementPage.popupDownloadButton);
        accountStatementPage.popupDownloadButton.click();
        File transactionPdfFile = new File("C:/Users/987993/Downloads/" + "TxnRpt_" + getCurrentDateTime() + ".pdf");
        softAssert.assertTrue(transactionPdfFile.exists() || transactionPdfFilePreMin.exists(), "Transaction Statement download failed");
        waitTillInvisibilityOfLoader(driver);
        if (transactionPdfFile.exists() || transactionPdfFilePreMin.exists()) {
            transactionPdfFile.delete();
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User clicks cancel button on popup")
    public void userClicksCancelButtonOnPopup() {
        waitForPageLoad(driver);
        accountStatementPage.cancelButton.click();
    }

    @When("User enter reference number accountStatement page")
    public void userEnterReferenceNumberAccountStatementPage() {
        accountStatementPage.enterReferenceNo.sendKeys(referenceNumber);
        accountStatementPage.transactionPopup.click();
    }

    @Then("User verify the transaction details by reference number")
    public void userVerifyTheTransactionDetailsByReferenceNumber() {
        softAssert = new SoftAssert();

        softAssert.assertEquals(accountStatementPage.popupTransactionDate.getText(), accountStatementPage.transactionDateText.getText(), "Transaction date Not matched");
        softAssert.assertEquals(accountStatementPage.popupTransactionTime.getText().replace(" ", ""), accountStatementPage.transactionTime.getText().replace(" ", ""), "Transaction time not matched");
        softAssert.assertEquals(accountStatementPage.popupUserAccountDetails.getText().replace(" ", ""), accountStatementPage.descriptionDetails.getText().replace(" ", ""), "User description details not matched");
        String actualTransactionAmount = accountStatementPage.popupTransactionAmount.getText().replace("\\n", "").replace("[^\\d]", "").trim();
        // String expectTransactionAmount = accountStatementPage.transactionAmount.getText().replaceAll("[^\\d]", "");
        String expectTransactionAmounts = accountStatementPage.amount.getText().replace("\\n", "").replace("[^\\d]", "").replace("-", "").replace("+", "").trim();
        softAssert.assertEquals(actualTransactionAmount, expectTransactionAmounts, "Transaction amount not matched");
        accountStatementPage.cancelButton.click();
        accountStatementPage.enterReferenceNo.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User apply the filter on account statement page")
    public void userApplyTheFilterOnAccountStatementPage() {
        waitTillElementToBeClickable(driver, accountStatementPage.filterButton);
        accountStatementPage.filterButton.click();
        waitTillInvisibilityOfLoader(driver);
        accountStatementPage.transactionType.click();
        accountStatementPage.getTransactionType(fileReader.accStatementTestData.get("filterTransactionType")).click();
        waitForPageLoad(driver);
        accountStatementPage.amountRangeFrom.sendKeys(fileReader.accStatementTestData.get("amountRangeFrom"));
        accountStatementPage.amountRangeTo.click();
        accountStatementPage.amountRangeTo.sendKeys(fileReader.accStatementTestData.get("amountRangeTo"));
        waitForPageLoad(driver);
        accountStatementPage.applyButton.click();
        waitTillInvisibilityOfLoader(driver);
        // accountStatementPage.dateFilter.click();
        javaScriptExecutorClickElement(driver, accountStatementPage.dateFilter);
      //  5/11/2023
       String fromDateSelect= getFormatedDate(getPastMonth(3),"dd/MM/yyyy");
        logger.info("From Date is "+fromDateSelect);
        //  String fromDateSelect = fileReader.accStatementTestData.get("transactionFromDate");
        String[] splitDate = fromDateSelect.split("/");
        String fromDate = splitDate[0];
        String fromMonth = String.valueOf(Integer.parseInt(splitDate[1]) - 1);
        String fromYear = splitDate[2];
        //waitTillInvisibilityOfLoader(driver);
        waitTillVisibilityElement(driver, accountStatementPage.yearClick);
        accountStatementPage.yearClick.click();
        selectDDByValue(accountStatementPage.yearSelect, fromYear);
        // waitTillInvisibilityOfLoader(driver);
        accountStatementPage.monthClick.click();
        selectDDByValue(accountStatementPage.monthSelect, fromMonth);
        accountStatementPage.getDateFromDD(fromDate).click();
        String toSelect=getFormatedDate(getPastDate(1),"dd/MM/yyyy");
        logger.info("To Date is "+toSelect);
      //  String toSelect = fileReader.accStatementTestData.get("transactionToDate");
        String[] splitToDate = toSelect.split("/");
        String toDate = splitToDate[0];
        String toMonth = String.valueOf(Integer.valueOf(splitToDate[1]) - 1);
        String toYear = splitToDate[2];
        waitForPageLoad(driver);
        accountStatementPage.yearClick.click();
        selectDDByValue(accountStatementPage.yearSelect, toYear);
        waitForPageLoad(driver);
        accountStatementPage.monthClick.click();
        selectDDByValue(accountStatementPage.monthSelect, toMonth);
        waitForPageLoad(driver);
        accountStatementPage.getDateFromDD(toDate).click();
        waitForPageLoad(driver);
        accountStatementPage.applyButton.click();
        //   waitTillInvisibilityOfLoader(driver);
    }

    @Then("User can verify the statement details as per the filter")
    public void userCanVerifyTheStatementDetailsAsPerTheFilter() {
        softAssert = new SoftAssert();
        String[] range = accountStatementPage.rangeFilterVerify.getText().split("-");
        String minRange = range[0].replace("₹", "").replaceAll(",", "").trim();
        String maxRange = range[1].replace("₹", "").replaceAll(",", "").trim();
        softAssert.assertTrue(minRange.contains(fileReader.accStatementTestData.get("amountRangeFrom")), "from amount not matched with entered amount");
        softAssert.assertTrue(maxRange.contains(fileReader.accStatementTestData.get("amountRangeTo")), "to amount not matched with entered amount");
        String dateSplit = accountStatementPage.dateFilterText.getText();
        String[] fromDate = dateSplit.split("-");
        String startDate = fromDate[0];
        String endDate = fromDate[1];
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");

        // LocalDateTime localDateTime=LocalDateTime.of("dd MMM,yyyy")
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

        Date dateToValidate = null;
        Date starts;
        Date end;
        try {
            starts = sdf.parse(startDate);
            end = sdf.parse(endDate);
            for (WebElement transactionLists : accountStatementPage.transactionList) {
                dateToValidate = sdf.parse(transactionLists.getText());
                //softAssert.assertTrue(dateToValidate.after(starts) && dateToValidate.before(end), "Date is lie between int the filter date");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        for (WebElement amountTransactionList : accountStatementPage.amountList) {
            String amountsLists = amountTransactionList.getText().replaceAll("[^\\d.]", "").trim();
            double amountNum = Double.parseDouble(amountsLists);
            String rangeFrom = String.valueOf(fileReader.accStatementTestData.get("amountRangeFrom"));
            Double rangeFromNum = Double.parseDouble(rangeFrom);
            String rangeTo = String.valueOf(fileReader.accStatementTestData.get("amountRangeTo"));
            Double rangeToNum = Double.parseDouble(rangeTo);
            softAssert.assertTrue(amountNum >= rangeFromNum && amountNum <= rangeToNum, "Amount in the  statement transaction not sort by filter ");
        }
        if (fileReader.accStatementTestData.get("filterTransactionType").equalsIgnoreCase("C")) {
            for (WebElement amount : accountStatementPage.amountList) {
                softAssert.assertTrue((amount.getText().contains("+")), "Credit Account type not sort by filter");
            }

        } else if (fileReader.accStatementTestData.get("filterTransactionType").equalsIgnoreCase("D")) {
            for (WebElement amount : accountStatementPage.amountList) {
                softAssert.assertTrue((amount.getText().contains("-")), "Debit Account type not sort by filter");
            }
        }
        try {
            if (accountStatementPage.transactionDate.getText().length() != 0) {
                softAssert.assertTrue(dateToValidate.compareTo(starts) >= 0 && dateToValidate.compareTo(end) <= 0, "Date is lie between int the filter date");
                waitTillInvisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("After applying filter the transaction section not displayed");
        }

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User remove all applied filter in account statement")
    public void userRemoveAllAppliedFilterInAccountStatement() {
        if (fileReader.accStatementTestData.get("filterTransactionType").equalsIgnoreCase("C")) {
            accountStatementPage.accTypeFilterRemoveCredit.click();
            staticWait(2000);
        } else if (fileReader.accStatementTestData.get("filterTransactionType").equalsIgnoreCase("D")) {
            accountStatementPage.accTypeFilterRemoveDebit.click();
            staticWait(2000);
        }
        staticWait(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(accountStatementPage.rangeFilterRemove));
        accountStatementPage.rangeFilterRemove.click();
        waitTillInvisibilityOfLoader(driver);
        accountStatementPage.dateFilterRemove.click();
        waitTillInvisibilityOfLoader(driver);
    }


    @When("User click transaction date sorting functionality in transaction section")
    public void userClickTransactionDateSortingFunctionalityInTransactionSection() {
        accountStatementPage.transactionDateSort.click();
        waitForPageLoad(driver);
    }

    @Then("User validate the transaction date sorting functionality in transaction section")
    public void userValidateTheTransactionDateSortingFunctionalityInTransactionSection() {
        //Ascending
        softAssert = new SoftAssert();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy hh:mm a");
        List<Date> dateList = new ArrayList<>();
        for (WebElement element : accountStatementPage.transactionTimeList) {
            String dateElements = element.getText().replaceAll("\n", " ");
            try {
                Date dateTime = dateFormat.parse(dateElements);
                dateList.add(dateTime);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        Date currentDate = null;
        Date nextDate = null;
        for (int i = 0; i < dateList.size() - 1; i++) {
            currentDate = dateList.get(i);
            nextDate = dateList.get(i + 1);
            //softAssert.assertTrue(currentDate.before(nextDate), "Transaction date not sorted in Acsending");
            softAssert.assertTrue(currentDate.compareTo(nextDate) <= 0, "Transaction date not sorted in Ascending");
            logger.info("Current Date :" + currentDate);
            logger.info("Next Date :" + nextDate);

        }
        //Descending
        waitTillInvisibilityOfLoader(driver);
        accountStatementPage.transactionDateSort.click();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd MMM, yyyy hh:mm a");
        //SimpleDateFormat dateFormatDsc = new SimpleDateFormat("dd MMM, yyyy hh:mm a");
        List<LocalDateTime> dateListDsc = new ArrayList<>();
        for (WebElement elementDsc : accountStatementPage.transactionTimeList) {
            String dateElementsDsc = elementDsc.getText().replaceAll("\n", " ");
            LocalDateTime dateTimeDsc = LocalDateTime.parse(dateElementsDsc, dateTimeFormat);
            dateListDsc.add(dateTimeDsc);
        }
        LocalDateTime currentDateDsc = null;
        LocalDateTime nextDateDsc = null;
        for (int j = 0; j < dateListDsc.size() - 1; j++) {
            currentDateDsc = dateListDsc.get(j);
            nextDateDsc = dateListDsc.get(j + 1);
        }

        softAssert.assertTrue(currentDateDsc.compareTo(nextDateDsc) >= 0, "Transaction date not sorted in Descending");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User validate the description sorting functionality in transaction list")
    public void userValidateTheDescriptionSortingFunctionalityInTransactionList() {
        try {
            waitTillElementToBeClickable(driver, accountStatementPage.descriptionSort);
            accountStatementPage.descriptionSort.click();
        } catch (Exception e) {
            attachScreenshot(driver, scenario);
            softAssert.fail(e.toString());
        }
        softAssert = new SoftAssert();
        String firstRowTransaction = null;
        String descriptionList = null;
        String descriptionListDsc = null;
        String firstRowTransactionDsc = null;
        for (WebElement description : accountStatementPage.transactionDescriptionList) {
            descriptionList = description.getText();
            firstRowTransaction = accountStatementPage.transactionDescription.getText();
        }
        accountStatementPage.descriptionSort.click();
        for (WebElement descriptionDsc : accountStatementPage.transactionDescriptionList) {
            descriptionListDsc = descriptionDsc.getText();
            firstRowTransactionDsc = accountStatementPage.transactionDescription.getText();
        }
        softAssert.assertNotEquals(firstRowTransaction, descriptionList, "Transaction Sorted Failed");
        softAssert.assertNotEquals(firstRowTransactionDsc, descriptionListDsc, "Transaction Sorted Failed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User validate the amount sorting functionality in transaction list")
    public void userValidateTheAmountSortingFunctionalityInTransactionList() {
        accountStatementPage.amountSort.click();
        waitTillInvisibilityOfLoader(driver);
        softAssert = new SoftAssert();
        List<Double> amountListNum = new ArrayList<>();
        for (WebElement amountList : accountStatementPage.amountList) {
            double amountListDetails = Double.parseDouble(amountList.getText().replaceAll("[^\\d.]", "").replaceAll("\\n", "").trim());
            amountListNum.add(amountListDetails);
        }
        for (int i = 0; i < amountListNum.size() - 1; i++) {
            Double currentAmount = amountListNum.get(i);
            Double nextAmount = amountListNum.get(i + 1);
            softAssert.assertTrue(currentAmount <= nextAmount, "Amount ascending sorting functionality failed");
        }
        waitForPageLoad(driver);
        //Descending
        accountStatementPage.amountSort.click();
        List<Double> amountListNumDsc = new ArrayList<>();
        for (WebElement amountListDsc : accountStatementPage.amountList) {
            double amountListDetailsDsc = Double.parseDouble(amountListDsc.getText().replaceAll("[^\\d.]", "").replaceAll("\\n", "").trim());
            amountListNum.add(amountListDetailsDsc);
        }
        for (int i = 0; i < amountListNumDsc.size() - 1; i++) {
            Double currentAmountDsc = amountListNumDsc.get(i);
            Double nextAmountDsc = amountListNumDsc.get(i + 1);
            softAssert.assertTrue(currentAmountDsc >= nextAmountDsc, "Amount descending sorting functionality failed");
        }
        //softAssert.assertTrue(currentFloatInt <= nextFloatInt, "Transaction amount not sorted in ascending order");
        //softAssert.assertTrue(currentFloatIntDsc >= nextFloatIntDsc, "Transaction amount not sorted in descending");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User click balance sorting functionality in transaction section")
    public void userClickBalanceSortingFunctionalityInTransactionSection() {
        try {
            accountStatementPage.balanceSort.click();
        } catch (ElementNotInteractableException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Balance Sorting icon not available please check the screenShot");
        }
    }

    @Then("User validate the balance sorting functionality in transaction section")
    public void userValidateTheBalanceSortingFunctionalityInTransactionSection() {
        softAssert = new SoftAssert();
        List<String> balanceString = new ArrayList<>();
        for (WebElement balanceElements : accountStatementPage.balanceList) {
            String balanceList = balanceElements.getText().replaceAll("₹", "").replaceAll(",", "");
            balanceString.add(balanceList);
        }
        double currentBalanceDouble = 0;
        double nextBalanceDouble = 0;
        for (int i = 0; i < balanceString.size() - 1; i++) {
            String currentBalance = balanceString.get(i);
            String nextBalance = balanceString.get(i + 1);

            currentBalanceDouble = Double.parseDouble(currentBalance);
            nextBalanceDouble = Double.parseDouble(nextBalance);
            // softAssert.assertTrue(currentBalanceDouble<=nextBalanceDouble,"Transaction section balance not sorted in ascending");
        }
        //Descending
        try {
            accountStatementPage.balanceSort.click();
        } catch (ElementNotInteractableException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Balance Sorting icon not available please check the screenShot");
        }
        List<String> balanceStringDsc = new ArrayList<>();
        for (WebElement balanceElementsDsc : accountStatementPage.balanceList) {
            String balanceListDsc = balanceElementsDsc.getText().replaceAll("₹", "").replaceAll(",", "");
            balanceStringDsc.add(balanceListDsc);
        }
        double currentBalanceDoubleDsc = 0;
        double nextBalanceDoubleDsc = 0;
        for (int i = 0; i < balanceStringDsc.size() - 1; i++) {
            String currentBalanaceDsc = balanceStringDsc.get(i);
            String nextBalanceDsc = balanceStringDsc.get(i + 1);

            currentBalanceDoubleDsc = Double.parseDouble(currentBalanaceDsc);
            nextBalanceDoubleDsc = Double.parseDouble(nextBalanceDsc);
            // softAssert.assertTrue(currentBalanceDoubleDsc >= nextBalanceDoubleDsc, "Transaction section balance not sorted in descending");
        }
        softAssert.assertTrue(currentBalanceDouble <= nextBalanceDouble, "Transaction section balance not sorted in ascending");
        softAssert.assertTrue(currentBalanceDoubleDsc >= nextBalanceDoubleDsc, "Transaction section balance not sorted in descending");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User validate the downloadStatement")
    public void userValidateTheDownloadStatement() {
        softAssert = new SoftAssert();
        try {
            if (accountStatementPage.downloadFailed.isDisplayed()) {
                attachScreenshot(driver, scenario);
                accountStatementPage.downloadFailedBackButton.click();
            }

        } catch (NoSuchElementException e) {
            logger.info(e.getMessage());
        }
        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("pdf")) {
            accountStatementPage.downloadPdfButton.click();
            WebDriverWait waitInvisibleElementMail = new WebDriverWait(driver, Duration.ofSeconds(60));
            waitInvisibleElementMail.until(ExpectedConditions.invisibilityOf(accountStatementPage.downloadingPopUp));
            staticWait(3000);
            File downloadedPdfFile = new File("C:/Users/987993/Downloads/Account_Statement.pdf");
            staticWait(4000);
            //   waitTillInvisibilityOfLoader(driver);
            softAssert.assertTrue(downloadedPdfFile.exists(), "AccountStatement download failed");
            waitTillInvisibilityOfLoader(driver);
            if (downloadedPdfFile.exists()) {
                downloadedPdfFile.delete();
            }
        }
        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("csv")) {
            accountStatementPage.downloadCsvButton.click();
            WebDriverWait waitInvisibleElementMail = new WebDriverWait(driver, Duration.ofSeconds(40));
            waitInvisibleElementMail.until(ExpectedConditions.invisibilityOf(accountStatementPage.downloadingPopUp));
            File downloadedCsvFile = new File("C:/Users/987993/Downloads/Account_Statement.csv");
            staticWait(4000);
            softAssert.assertTrue(downloadedCsvFile.exists(), "AccountStatement File download failed");
            waitTillInvisibilityOfLoader(driver);
            if (downloadedCsvFile.exists()) {
                downloadedCsvFile.delete();
            }
        }
        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("xls")) {
            accountStatementPage.downloadXlsButton.click();
            WebDriverWait waitInvisibleElementMail = new WebDriverWait(driver, Duration.ofSeconds(40));
            waitInvisibleElementMail.until(ExpectedConditions.invisibilityOf(accountStatementPage.downloadingPopUp));

            File downloadedXlsFile = new File("C:/Users/987993/Downloads/Account_Statement.xls");
            staticWait(4000);
            softAssert.assertTrue(downloadedXlsFile.exists(), "AccountStatement File download failed");
            waitTillInvisibilityOfLoader(driver);
            if (downloadedXlsFile.exists()) {
                downloadedXlsFile.delete();
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

    @And("User verify the quick download section")
    public void userVerifyTheQuickDownloadSection() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(accountStatementPage.currentFinancialDownloadButton.isDisplayed(), "current financial year download button not displayed");
        softAssert.assertTrue(accountStatementPage.currentFinancialEmailButton.isDisplayed(), "current financial year email button not displayed");
        softAssert.assertTrue(accountStatementPage.previousFinancialDownloadButton.isDisplayed(), "previous financial year download button not displayed");
        softAssert.assertTrue(accountStatementPage.previousFinancialEmailButton.isDisplayed(), "previous financial year email button not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User validate quick download for current financial year statement")
    public void userValidateQuickDownloadForCurrentFinancialYearStatement() {
        softAssert = new SoftAssert();
        waitTillElementToBeClickable(driver, accountStatementPage.currentFinancialDownloadButton);
        String currentFinancialYearDuration[] = accountStatementPage.currentFinancialYearPeriodChecking.getText().split("-");
        String currentFinancialYearFrom = currentFinancialYearDuration[0].trim();
        String currentFinancialYearTo = currentFinancialYearDuration[1].trim();
        accountStatementPage.currentFinancialDownloadButton.click();
        String currentFinancialYearPopUpFrom[] = accountStatementPage.financialYearPeriodInPopUp.getText().split("to");
        String currentFinancialPopUpYearFrom = currentFinancialYearPopUpFrom[0].trim();
        String currentFinancialPopUpYearTo = currentFinancialYearPopUpFrom[1].trim();
        softAssert.assertEquals("for " + currentFinancialYearFrom, currentFinancialPopUpYearFrom, "current financial year pop up from duration not matched");
        softAssert.assertEquals(currentFinancialYearTo, currentFinancialPopUpYearTo, "current financial year pop up to duration not matched");
        accountStatementPage.downloadFileType(fileReader.accStatementTestData.get("downloadType")).click();
        accountStatementPage.currentFinancialPopUpDownloadButton.click();
        WebDriverWait waitInvisibleElement = new WebDriverWait(driver, Duration.ofSeconds(80));
        waitInvisibleElement.until(ExpectedConditions.invisibilityOf(accountStatementPage.downloadingPopUp));

        try {
            if (accountStatementPage.anyErrorWhileDownloadOrEmail.isDisplayed()) {
                if (accountStatementPage.errorButtonFailedMessage.getText().contains("Back")) {
                    attachScreenshot(driver, scenario);
                    softAssert.fail("Current Financial year download statement failed");
                    accountStatementPage.downloadFailedBackButton.click();
                } else if (accountStatementPage.errorButtonFailedMessage.getText().contains("OK")) {
                    attachScreenshot(driver, scenario);
                    logger.error("Current Financial year download statement failed,No data's available");
                    accountStatementPage.noRecordFoundOkButton.click();
                }
            }
        } catch (NoSuchElementException e) {
        }

        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("pdf")) {
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.pdf");
            staticWait(4000);
            softAssert.assertTrue(downloadedFile.exists(), "current financial year File download failed");
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("xls")) {
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.xls");
            staticWait(4000);
            softAssert.assertTrue(downloadedFile.exists(), "current financial year File download failed");
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("csv")) {
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.csv");
            staticWait(4000);
            softAssert.assertTrue(downloadedFile.exists(), "current financial year File download failed");
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
        waitTillElementToBeClickable(driver, accountStatementPage.currentFinancialEmailButton);
        accountStatementPage.currentFinancialEmailButton.click();
        accountStatementPage.emailAddressEnter.sendKeys(Keys.chord(Keys.chord(Keys.CONTROL, "a")), fileReader.accStatementTestData.get("emailId"));
        staticWait(2000);
        accountStatementPage.emailSentStatementType(fileReader.accStatementTestData.get("downloadType")).click();
        accountStatementPage.quickEmailSent.click();
        softAssert.assertTrue(accountStatementPage.emailingPopUp.isDisplayed(), "Emailing pop up not displayed");
        try {
            if (accountStatementPage.anyErrorWhileDownloadOrEmail.isDisplayed()) {
                if (accountStatementPage.errorButtonFailedMessage.getText().contains("Back") || accountStatementPage.errorButtonFailedMessage.getText().contains("Okay")) {
                    attachScreenshot(driver, scenario);
                    clickOnButton(accountStatementPage.errorButtonFailedMessage);
                    softAssert.fail("Current Financial year sent email statement failed");
                } else if (accountStatementPage.errorButtonFailedMessage.getText().contains("OK")) {
                    attachScreenshot(driver, scenario);
                    logger.error("Current Financial year sent email statement failed,No data's available");
                    accountStatementPage.noRecordFoundOkButton.click();
                } else {
                    waitTillVisibilityElement(driver, accountStatementPage.toastMessage);
                    softAssert.assertTrue(accountStatementPage.toastMessage.getText().contains("uccessfully"), "Email sending failed");
                }
            }
        } catch (NoSuchElementException e) {
            logger.info(e.toString());
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User validate quick download for previous financial year statement")
    public void userValidateQuickDownloadForPreviousFinancialYearStatement() {
        softAssert = new SoftAssert();
        String popupMessage = null;
        String previousFinancialYearDuration[] = accountStatementPage.previousFinancialYearPeriodChecking.getText().split("-");
        String previousFinancialYearFrom = previousFinancialYearDuration[0].trim();
        String previousFinancialYearTo = previousFinancialYearDuration[1].trim();
        javaScriptExecutorClickElement(driver, accountStatementPage.previousFinancialDownloadButton);
        // accountStatementPage.previousFinancialDownloadButton.click();
        popupMessage = accountStatementPage.popUpMessage.getText();
        String previousFinancialYearPopUpFrom[] = accountStatementPage.financialYearPeriodInPopUp.getText().split("to");
        String previousFinancialPopUpYearFrom = previousFinancialYearPopUpFrom[0].trim();
        String previousFinancialPopUpYearTo = previousFinancialYearPopUpFrom[1].trim();
        softAssert.assertEquals(previousFinancialPopUpYearFrom, "for " + previousFinancialYearFrom, "previous financial year pop up from duration not matched");
        softAssert.assertEquals(previousFinancialPopUpYearTo, previousFinancialYearTo, "previous financial year pop up to duration not matched");

        accountStatementPage.downloadFileType(fileReader.accStatementTestData.get("downloadType")).click();
        accountStatementPage.previousFinancialFileDownloadButton.click();
        WebDriverWait waitInvisibleElement = new WebDriverWait(driver, Duration.ofSeconds(80));
        waitInvisibleElement.until(ExpectedConditions.invisibilityOf(accountStatementPage.downloadingPopUp));
        try {
            if (accountStatementPage.popUpMessage.getText().contains("No record found")) {
                attachScreenshot(driver, scenario);
                logger.error("Previous Financial year sent download statement failed,No data's available");
                accountStatementPage.errorButtonFailedMessage.click();
                boolean notDataFound = accountStatementPage.errorButtonFailedMessage.getText().contains("OK");
            } else {
                attachScreenshot(driver, scenario);
                softAssert.fail("Previous Financial year download statement failed");
            }

        } catch (NoSuchElementException exception) {
        }
        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("pdf")) {
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.pdf");
            staticWait(4000);
            softAssert.assertTrue(downloadedFile.exists(), "previous financial year File download failed");
            // waitTillInvisibilityOfLoader(driver);
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("xls")) {
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.xls");
            staticWait(4000);
            softAssert.assertTrue(downloadedFile.exists(), "previous financial year File download failed");
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
        if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("csv")) {
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.csv");
            staticWait(4000);
            softAssert.assertTrue(downloadedFile.exists(), "previous financial year File download failed");
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
        javaScriptExecutorClickElement(driver, accountStatementPage.previousFinancialEmailButton);
        accountStatementPage.emailAddressEnter.clear();
        accountStatementPage.emailAddressEnter.sendKeys(Keys.chord(Keys.chord(Keys.CONTROL, "a")), fileReader.accStatementTestData.get("emailId"));
        staticWait(3000);
        javaScriptExecutorClickElement(driver, accountStatementPage.emailSentStatementType(fileReader.accStatementTestData.get("downloadType")));
        accountStatementPage.quickEmailSent.click();
        softAssert.assertTrue(accountStatementPage.emailingPopUp.isDisplayed(), "Emailing pop up not displayed");
        waitTillInvisibilityElement(driver, accountStatementPage.emailingPopUp);
        try {
            if (accountStatementPage.anyErrorWhileDownloadOrEmail.isDisplayed()) {
                if (accountStatementPage.errorButtonFailedMessage.getText().contains("Back") || accountStatementPage.errorButtonFailedMessage.getText().contains("Okay")) {
                    attachScreenshot(driver, scenario);
                    clickOnButton(accountStatementPage.errorButtonFailedMessage);
                    softAssert.fail("Previous Financial year sent email statement failed");

                } else if (accountStatementPage.errorButtonFailedMessage.getText().contains("OK")) {
                    attachScreenshot(driver, scenario);
                    logger.error("Previous Financial year sent email statement failed,No data's available");
                    accountStatementPage.noRecordFoundOkButton.click();
                } else {
                    waitTillVisibilityElement(driver, accountStatementPage.toastMessage);
                    softAssert.assertTrue(accountStatementPage.toastMessage.getText().contains("uccessfully"), "Email sending failed");
                }
            }
        } catch (NoSuchElementException e) {
            logger.info(e.toString());
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            //  attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify transaction section page shows label")
    public void userVerifyTransactionSectionPageShowsLabel() {
        softAssert = new SoftAssert();
        int currentPageNo = 0;
        int totalPageNo = 0;
        int currentPageNoAfterNav = 0;
        if (accountStatementPage.noResultFound.getText().toLowerCase().contains("no result found")) {
            attachScreenshot(driver, scenario);
            accountStatementPage.dateFilter.click();
            accountStatementPage.lastOneYearFilter.click();
            waitTillInvisibilityOfLoader(driver);
            accountStatementPage.applyButton.click();
            waitTillInvisibilityOfLoader(driver);
            //attachScreenshot(driver,scenario);
        }
        String extractPageNumbers = accountStatementPage.pageSectionShowLabel.getText();
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(extractPageNumbers);
        int count = 0;
        while (matcher.find()) {
            if (count == 0) {
                currentPageNo = Integer.parseInt(matcher.group());
                logger.info("Label Show from " + matcher.group());
                count++;
            } else if (count == 1) {
                totalPageNo = Integer.parseInt(matcher.group());
                logger.info("Label Last page number " + matcher.group());
            }
        }
        if (totalPageNo > 1) {
            /**Verify the sorting button after click label button **/
            beforeSortingButtonPositionViewBox = accountStatementPage.balanceSortingButtonPosition.getAttribute("innerHTML");
            accountStatementPage.balanceSort.click();
            logger.info("Total no page available in label is " + totalPageNo);
            waitTillElementToBeClickable(driver, accountStatementPage.pageSectionGetNextButton);
            javaScriptExecutorClickElement(driver, accountStatementPage.pageSectionGetNextButton);
            String afterSortingViewBoxAfterClickLabelButton = accountStatementPage.balanceSortingButtonPosition.getAttribute("innerHTML");
            // clickOnButton(accountStatementPage.pageSectionGetNextButton);
            staticWait(2000);
            if (totalPageNo > 1) {
                accountStatementPage.pageSectionGetNextButton.click();
                String extractPageNumberChange[] = accountStatementPage.pageSectionShowLabel.getText().split(" ");
                int currentPageNoChange = Integer.parseInt(extractPageNumberChange[1].trim());
                softAssert.assertTrue(currentPageNoAfterNav != currentPageNo, "page navigation failed");
                softAssert.assertEquals(Integer.parseInt(accountStatementPage.pageSectionGetPageNumber.getText()), currentPageNoChange, "page navigation number is not to be same failed");
                softAssert.assertFalse(beforeSortingButtonPositionViewBox.contains(afterSortingViewBoxAfterClickLabelButton), "after click label button sorting function reset");
                logger.info("Current label page no is " + currentPageNo);
                logger.info("After navigate Current label page " + currentPageNoChange);
                logger.info(accountStatementPage.pageSectionGetPageNumber.getText());
                accountStatementPage.pageSectionGetPrevious.click();
            }
        } else {
            logger.info("One account statement page is available");
        }
        try {
            softAssert.assertAll();
        } catch (
                AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User select {string} & {string}apply the date filter")
    public void userSelectApplyTheDateFilter(String fromDates, String toDates) {
        accountStatementPage.dateFilter.click();
        String fromDateSelect = fromDates;
        String[] splitDate = fromDateSelect.split("/");
        String fromDate = splitDate[0];
        String fromMonth = String.valueOf(Integer.valueOf(splitDate[1]) - 1);
        String fromYear = splitDate[2];
        logger.info("Duration filter applied date from :" + fromDate + "-" + fromMonth + "-" + fromYear);
        staticWait(2000);
        accountStatementPage.yearClick.click();
        selectDDByValue(accountStatementPage.yearSelect, fromYear);
        staticWait(2000);
        accountStatementPage.monthClick.click();
        selectDDByValue(accountStatementPage.monthSelect, fromMonth);
        accountStatementPage.getDateFromDD(fromDate).click();
        String toSelect = toDates;
        String[] splitToDate = toSelect.split("/");
        String toDate = splitToDate[0];
        String toMonth = String.valueOf(Integer.valueOf(splitToDate[1]) - 1);
        String toYear = splitToDate[2];
        staticWait(2000);
        logger.info("Duration filter applied date to :" + toDate + "-" + toMonth + "-" + toYear);
        accountStatementPage.yearClick.click();
        selectDDByValue(accountStatementPage.yearSelect, toYear);
        staticWait(2000);
        accountStatementPage.monthClick.click();
        selectDDByValue(accountStatementPage.monthSelect, toMonth);
        staticWait(2000);
        accountStatementPage.getDateFromDD(toDate).click();
        accountStatementPage.applyButton.click();
        staticWait(3000);
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User can verify  the account statement page based {string}")
    public void userCanVerifyTheAccountStatementPageBased(String typeAcc) {
        softAssert = new SoftAssert();
        logger.info("Current scenario name is :" + typeAcc);
        boolean isVisible;
        String detailsScenarios = typeAcc;
        if (detailsScenarios.contains("Too many transaction to display")) {
            waitTillElementToBeClickable(driver, accountStatementPage.accountDD);
            accountStatementPage.accountDD.click();
            accountStatementPage.selectAccountFromDD(fileReader.accStatementTestData.get(">999PerticularPeriods"));
            waitTillInvisibilityOfLoader(driver);
            scrollIntoView(driver, accountStatementPage.tooManyTransactionMessage);
            softAssert.assertTrue(accountStatementPage.tooManyTransactionMessage.getText().contains(" You can get PDF statement on email"), "too many transaction message not displayed");
            softAssert.assertTrue(accountStatementPage.resetFilterButton.isDisplayed(), "reset filter button not displayed");
            softAssert.assertTrue(accountStatementPage.emailGetStatementButton.isDisplayed(), "email statement button not displayed");

        } else if (detailsScenarios.contains("Please visit nearest branch")) {
            try {
                scrollIntoView(driver, accountStatementPage.downloadStatement);
                softAssert.assertTrue(accountStatementPage.downloadStatement.isDisplayed(), "branch locator button is not displayed");
            } catch (NoSuchElementException e) {
                attachScreenshot(driver, scenario);
                softAssert.fail("In transaction section more than 999 transaction in a day option not displayed,Please verify the screen shot");
            }
            String currentWindow = driver.getWindowHandle();
            accountStatementPage.downloadStatement.click();
            waitTillInvisibilityOfLoader(driver);
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(currentWindow)) {
                    driver.switchTo().window(windowHandle);
                    softAssert.assertTrue(driver.getCurrentUrl().contains("branch-locator"), "branch-locator page not displayed");
                    break;
                }
            }
            driver.close();
            driver.switchTo().window(currentWindow);
            waitTillInvisibilityOfLoader(driver);
        } else if (detailsScenarios.contains("Current financial account")) {
            softAssert.assertTrue(accountStatementPage.quickDownloadList.size() == 1, "does not contain only current financial year details");
            softAssert.assertTrue(accountStatementPage.currentFinancialDownloadButton.isDisplayed(), "current financial year download button not enables");
            softAssert.assertTrue(accountStatementPage.currentFinancialEmailButton.isDisplayed(), "current financial year email button not displayed");
        }
        try {
            softAssert.assertAll();
        } catch (
                AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    /**
     * DOWNLOAD FUNCTIONALITY
     * Notes: Given in below codes all represent For download functionality feature It contains only Download particulars in Account statement
     **/

    @Then("User can verify  the account statement page based {string} for download function")
    public void userCanVerifyTheAccountStatementPageBasedForDownloadFunction(String typeAcc) {
        softAssert = new SoftAssert();
        boolean isVisible;
        String detailsScenarios = typeAcc;
        if (detailsScenarios.contains("You can either download")) {
            waitTillVisibilityElement(driver, accountStatementPage.downloadStatement);
            accountStatementPage.downloadStatement.click();
            waitTillElementToBeClickable(driver, accountStatementPage.downloadFileType(fileReader.accStatementTestData.get("downloadType")));
            accountStatementPage.downloadFileType(fileReader.accStatementTestData.get("downloadType")).click();
            accountStatementPage.previousFinancialFileDownloadButton.click();
            // waitTillInvisibilityElement(driver,accountStatementPage.downloadingPopUp);
            waitTillInvisibilityElement(driver, accountStatementPage.downloadingPopUp);
            try {
                if (accountStatementPage.downloadFailed.isDisplayed()) {
                    attachScreenshot(driver, scenario);
                    accountStatementPage.downloadFailed.click();
                }
            } catch (NoSuchElementException exception) {
                attachScreenshot(driver, scenario);
                logger.error("Statement File download failed");
            }
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.pdf");
            waitTillInvisibilityOfLoader(driver);
            softAssert.assertTrue(downloadedFile.exists(), "File download failed");
            // waitTillInvisibilityOfLoader(driver);
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
            accountStatementPage.emailGetStatementButton.click();
            accountStatementPage.emailAddressEnter.clear();
            accountStatementPage.emailAddressEnter.sendKeys(fileReader.accStatementTestData.get("emailId"));
            accountStatementPage.downloadFileType(fileReader.accStatementTestData.get("downloadType")).click();
            //staticWait(3000);
            accountStatementPage.quickEmailSent.click();
            WebDriverWait waitInvisibleElementMail = new WebDriverWait(driver, Duration.ofSeconds(80));
            waitInvisibleElementMail.until(ExpectedConditions.invisibilityOf(accountStatementPage.downloadingPopUp));
            try {
                isVisible = accountStatementPage.downloadFailed.isDisplayed();
            } catch (NoSuchElementException exception) {
            }
            if (isVisible = true) {
                attachScreenshot(driver, scenario);
                accountStatementPage.downloadFailed.click();
                softAssert.fail("Email sending failed");
            } else {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
                wait.until(ExpectedConditions.visibilityOf(accountStatementPage.emailSentSuccessfully));
                //softAssert.assertTrue(accountStatementPage.emailSentSuccessfully.isDisplayed(),"Email sending failed");
                softAssert.assertEquals(accountStatementPage.emailSentSuccessfully.getText(), "Email has been processed successfully", "Email sending failed");
            }
        } else if (detailsScenarios.contains("Current financial account")) {
            accountStatementPage.currentFinancialDownloadButton.click();
            accountStatementPage.downloadFileType(fileReader.accStatementTestData.get("downloadType")).click();
            accountStatementPage.currentFinancialDownloadButton.click();
            waitTillInvisibilityElement(driver, accountStatementPage.downloadingPopUp);
            staticWait(3000);
            try {
                if (accountStatementPage.downloadFailed.isDisplayed()) {
                    attachScreenshot(driver, scenario);
                    accountStatementPage.downloadFailed.click();
                    logger.error("Statement File download failed");
                }
            } catch (NoSuchElementException exception) {
            }
            if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("pdf")) {
                File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.pdf");
                staticWait(4000);
                softAssert.assertTrue(downloadedFile.exists(), "File download failed");
                //    waitTillInvisibilityOfLoader(driver);
                if (downloadedFile.exists()) {
                    downloadedFile.delete();
                }
            }
            if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("xls")) {
                File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.xls");
                staticWait(4000);
                softAssert.assertTrue(downloadedFile.exists(), "File download failed");
                //waitTillInvisibilityOfLoader(driver);
                if (downloadedFile.exists()) {
                    downloadedFile.delete();
                }
            }
            if (fileReader.accStatementTestData.get("downloadType").equalsIgnoreCase("csv")) {
                File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement.csv");
                staticWait(4000);
                softAssert.assertTrue(downloadedFile.exists(), "File download failed");
                if (downloadedFile.exists()) {
                    downloadedFile.delete();
                }
            }
            //waitTillInvisibilityOfLoader(driver);
            waitTillElementToBeClickable(driver, accountStatementPage.currentFinancialEmailButton);
            accountStatementPage.currentFinancialEmailButton.click();
            waitTillVisibilityElement(driver, accountStatementPage.currentFinancialEmailButton);
            accountStatementPage.addOtherMail.click();
            waitTillInvisibilityElement(driver, accountStatementPage.emailAddressEnter);
            accountStatementPage.emailAddressEnter.sendKeys(Keys.chord(Keys.chord(Keys.CONTROL, "a")), fileReader.accStatementTestData.get("emailId"));
            accountStatementPage.downloadFileType(fileReader.accStatementTestData.get("downloadType")).click();
            accountStatementPage.quickEmailSent.click();
            try {
                if (accountStatementPage.downloadFailedBackButton.isDisplayed()) {
                    attachScreenshot(driver, scenario);
                    accountStatementPage.downloadFailedBackButton.click();
                } else {
                    waitTillInvisibilityElement(driver, accountStatementPage.downloadingPopUp);
                    waitTillVisibilityElement(driver, accountStatementPage.emailSentSuccessfully);
                    softAssert.assertEquals(accountStatementPage.emailSentSuccessfully.getText(), "uccessfully", "Email sending failed");
                }
            } catch (NoSuchElementException e) {
                //  e.printStackTrace();
            } catch (TimeoutException e) {
                // throw new RuntimeException(e);
                //   e.printStackTrace();
            }
            staticWait(3000);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("Verify the download functions based on {string}")
    public void verifyTheDownloadFunctionsBasedOn(String typeAcc) {
        softAssert = new SoftAssert();
        String detailsScenarios = typeAcc;
        if (detailsScenarios.contains("Too many transaction to display")) {
            /***Download options is removed ,Screen was changed ***/

            /***Download options removed ***/
            accountStatementPage.emailGetStatementButton.click();
            softAssert.assertTrue(accountStatementPage.emailingPopUp.isDisplayed(), "Emailing pop up not displayed");
            waitTillInvisibilityElement(driver, accountStatementPage.emailingPopUp);
            try {
                if (accountStatementPage.toManyTransactionPopUp.isDisplayed()) {
                    accountStatementPage.toManyTransactionPopUp.click();
                }
            } catch (NoSuchElementException e) {
                logger.warn("Too many transaction po up displayed");
            }
            /***Reset Filter functions***/
            accountStatementPage.resetFilterButton.click();
            staticWait(2000);
            scrollIntoViewUp(driver, accountStatementPage.pageHeader);
            softAssert.assertNotEquals(accountStatementPage.validateRemovingTheFilter.getText(), "01, Apr 2023 - 10, Apr 2023", "reset filter options not working");

        } else if (detailsScenarios.contains("Please visit nearest branch")) {
            scrollIntoView(driver, accountStatementPage.downloadStatement);
            String currentWindow = driver.getWindowHandle();
            accountStatementPage.downloadStatement.click();
            waitTillInvisibilityOfLoader(driver);
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(currentWindow)) {
                    driver.switchTo().window(windowHandle);
                    softAssert.assertTrue(driver.getCurrentUrl().contains("branch-locator"), "branch-locator page not displayed");
                    break;
                }
            }
            driver.close();
            driver.switchTo().window(currentWindow);
            waitTillInvisibilityOfLoader(driver);
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            //  attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @Then("User verify the account statement page")
    public void userVerifyTheAccountStatementPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("statement"), "account statement page url not be the same");
        softAssert.assertTrue(accountStatementPage.pageHeader.getText().contains("Account Statements"), "account statement page header not displayed");
        softAssert.assertTrue(accountStatementPage.accountDD.isDisplayed(), "select account tab is displayed");
        softAssert.assertTrue(accountStatementPage.validateSelectAccShowingDefaultAccNo.getText().matches("\\d+"), "select account tab by default account number not displayed");
        softAssert.assertTrue(accountStatementPage.filterButton.isDisplayed(), "filter option not displayed");
        softAssert.assertTrue(accountStatementPage.dateFilter.isDisplayed(), "date filter option not displayed");
        softAssert.assertTrue(accountStatementPage.getStatementButton.isDisplayed(), "get statement button not displayed");
        //     softAssert.assertTrue(accountStatementPage.enterReferenceNo.isDisplayed(), "enter reference number not displayed");
        softAssert.assertTrue(accountStatementPage.currentFinancialDownloadButton.isDisplayed(), "current financial button not displayed");
        softAssert.assertTrue(accountStatementPage.currentFinancialEmailButton.isDisplayed(), "current financial email button not displayed");
        if (scenario.getName().contains("Validate more than 999 transaction in between the dates")) {

        } else if (scenario.getName().contains("Validate more than 999 transaction in a day")) {

        }


        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on maximize button in transaction section")
    public void userClicksOnMaximizeButtonInTransactionSection() {
//        if (accountStatementPage.noResultFound.getText().contains("No Result Found")) {
//            accountStatementPage.dateFilter.click();
//            accountStatementPage.lastOneYearFilter.click();
//            waitTillInvisibilityOfLoader(driver);
//            accountStatementPage.applyButton.click();
//            //attachScreenshot(driver,scenario);
//        }
        /** Verify the sorting button reset while maximize **/

        accountStatementPage.balanceSort.click();
        accountStatementPage.maximizeTransactionSection.click();
    }

    @Then("User verify the maximized transaction section")
    public void userVerifyTheMaximizedTransactionSection() {
        softAssert = new SoftAssert();
        try {
            String afterMaximizeBalanceSortingButtonPosition = accountStatementPage.balanceSortingButtonPosition.getAttribute("innerHTML");
            softAssert.assertTrue(accountStatementPage.valueDateSort.isDisplayed(), "value date sorting function not displayed");
            softAssert.assertTrue(accountStatementPage.referenceNumberOrChequeNo.isDisplayed(), "reference number column not displayed");
            softAssert.assertFalse(accountStatementPage.getStatementButton.isDisplayed(), "maximize functionality not displayed");
            softAssert.assertFalse(accountStatementPage.currentFinancialDownloadButton.isDisplayed(), "current financial statement not to be visible");
            softAssert.assertTrue(beforeSortingButtonPositionViewBox.contains(afterMaximizeBalanceSortingButtonPosition), "after maximize sorting button not reset");
            softAssert.assertAll();

        } catch (NoSuchElementException exception) {
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the value date sorting function in transaction section")
    public void userVerifyTheValueDateSortingFunctionInTransactionSection() {
        softAssert = new SoftAssert();
        waitTillInvisibilityOfLoader(driver);
        softAssert.assertTrue(accountStatementPage.dateFilterTextBegin.getText().contains(dateFilterTextDetails), "after apply and remove filter date filter text not same");
         logger.info(accountStatementPage.dateFilterTextBegin.getText());
         logger.info(dateFilterTextDetails);
        accountStatementPage.valueDateSort.click();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy hh:mm a");
        List<Date> dateList = new ArrayList<>();
        for (WebElement element : accountStatementPage.valueDateTimeList) {
            String dateElements = element.getText().replaceAll("\n", " ");
            try {
                Date dateTime = dateFormat.parse(dateElements);
                dateList.add(dateTime);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        Date currentDate = null;
        Date nextDate = null;
        for (int i = 0; i < dateList.size() - 1; i++) {
            currentDate = dateList.get(i);
            nextDate = dateList.get(i + 1);
            //softAssert.assertTrue(currentDate.before(nextDate), "Transaction date not sorted in Acsending");
            softAssert.assertTrue(currentDate.compareTo(nextDate) <= 0, "Transaction date not sorted in Ascending");
            logger.info("Current Date :" + currentDate);
            logger.info("Next Date :" + nextDate);

        }
        //Descending
        waitTillInvisibilityOfLoader(driver);
        accountStatementPage.valueDateSort.click();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd MMM, yyyy hh:mm a");
        //SimpleDateFormat dateFormatDsc = new SimpleDateFormat("dd MMM, yyyy hh:mm a");
        List<LocalDateTime> dateListDsc = new ArrayList<>();
        for (WebElement elementDsc : accountStatementPage.valueDateTimeList) {
            String dateElementsDsc = elementDsc.getText().replaceAll("\n", " ");
            LocalDateTime dateTimeDsc = LocalDateTime.parse(dateElementsDsc, dateTimeFormat);
            dateListDsc.add(dateTimeDsc);
        }
        LocalDateTime currentDateDsc = null;
        LocalDateTime nextDateDsc = null;
        for (int j = 0; j < dateListDsc.size() - 1; j++) {
            currentDateDsc = dateListDsc.get(j);
            nextDateDsc = dateListDsc.get(j + 1);
        }
        softAssert.assertTrue(currentDateDsc.compareTo(nextDateDsc) >= 0, "Transaction date not sorted in Descending");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the calendar details on duration filter")
    public void userVerifyTheCalendarDetailsOnDurationFilter() {
        softAssert = new SoftAssert();
        accountStatementPage.dateFilter.click();
        ListIterator<WebElement> listInCalendarDetails = accountStatementPage.calendarLeftSideDetails.listIterator();
        while (listInCalendarDetails.hasNext()) {
            WebElement calendarText = listInCalendarDetails.next();
            waitTillVisibilityElement(driver, calendarText);
            String calendarDetailText = calendarText.getText();
            logger.info("Iteration is checking " + calendarDetailText);
            softAssert.assertFalse(calendarDetailText.isEmpty(), "calendar left side lists are not displayed");

        }
        accountStatementPage.durationCancelButton.click();
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the reference or cheque number sorting function in transaction section")
    public void userVerifyTheReferenceOrChequeNumberSortingFunctionInTransactionSection() {
        softAssert = new SoftAssert();
        waitTillInvisibilityOfLoader(driver);
        List<WebElement> beforeSorting = accountStatementPage.referenceOrChequeNumberList;
        accountStatementPage.referenceOrChequeNumberSort.click();
        List<WebElement> ascendingList = accountStatementPage.referenceOrChequeNumberList;
        softAssert.assertTrue(!beforeSorting.equals(ascendingList), "ascending functionality for the reference no or cheque no failed");
        accountStatementPage.referenceOrChequeNumberSort.click();
        List<WebElement> descendingList = accountStatementPage.referenceOrChequeNumberList;
        softAssert.assertTrue(!ascendingList.equals(descendingList), "ascending functionality for the reference no or cheque no failed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User apply the date filter on account statement page")
    public void userApplyTheDateFilterOnAccountStatementPage() {
        String fromDateSelect = null;
        String toSelect = null;
        waitTillInvisibilityOfLoader(driver);
        accountStatementPage.dateFilter.click();
        if (scenario.getName().contains("Validate more than 999 transaction in between the dates")) {
            fromDateSelect = fileReader.accStatementTestData.get(">999TPPFromDate");
            toSelect = fileReader.accStatementTestData.get(">999TPPToDate");
        } else if (scenario.getName().contains("Validate more than 999 transaction in a day")) {
            fromDateSelect = fileReader.accStatementTestData.get(">999TPDDate");
            toSelect = fileReader.accStatementTestData.get(">999TPDDate");
        }
        String[] splitDate = fromDateSelect.split("/");
        String fromDate = splitDate[0].trim();
        String fromMonth = String.valueOf(Integer.valueOf(splitDate[1]) - 1).trim();
        String fromYear = splitDate[2].trim();
        logger.info("Duration filter applied date from :" + fromDate + "-" + splitDate[1] + "-" + fromYear);
        staticWait(2000);
        accountStatementPage.yearClick.click();
        selectDDByValue(accountStatementPage.yearSelect, fromYear);
        staticWait(2000);
        accountStatementPage.monthClick.click();
        selectDDByValue(accountStatementPage.monthSelect, fromMonth);
        //     selectDDByIndex(accountStatementPage.monthSelect, Integer.parseInt(fromMonth));
        accountStatementPage.getDateFromDD(fromDate).click();
        /***To Date Select***/

        String[] splitToDate = toSelect.split("/");
        String toDate = splitToDate[0].trim();
        String toMonth = String.valueOf(Integer.valueOf(splitToDate[1]) - 1).trim();
        String toYear = splitToDate[2].trim();
        staticWait(2000);
        logger.info("Duration filter applied date to :" + toDate + "-" + splitToDate[1] + "-" + toYear);
        accountStatementPage.yearClick.click();
        selectDDByValue(accountStatementPage.yearSelect, toYear);
        staticWait(2000);
        accountStatementPage.monthClick.click();
        selectDDByValue(accountStatementPage.monthSelect, toMonth);
        staticWait(2000);
        accountStatementPage.getDateFromDD(toDate).click();
        accountStatementPage.applyButton.click();
        staticWait(3000);
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the account transaction section")
    public void userVerifyTheAccountTransactionSection() {
        softAssert = new SoftAssert();
        if (scenario.getName().contains("Validate more than 999 transaction in between the dates")) {
            scrollIntoView(driver, accountStatementPage.tooManyTransactionMessage);
            softAssert.assertTrue(accountStatementPage.tooManyTransactionMessage.getText().contains(" You can get PDF statement on email"), "too many transaction message not displayed");
            softAssert.assertTrue(accountStatementPage.resetFilterButton.isDisplayed(), "reset filter button not displayed");
            softAssert.assertTrue(accountStatementPage.emailGetStatementButton.isDisplayed(), "email statement button not displayed");

        } else if (scenario.getName().contains("Validate more than 999 transaction in a day")) {
            try {
                scrollIntoView(driver, accountStatementPage.downloadStatement);
                softAssert.assertTrue(accountStatementPage.downloadStatement.isDisplayed(), "branch locator button is not displayed");
            } catch (NoSuchElementException e) {
                attachScreenshot(driver, scenario);
                softAssert.fail("In transaction section more than 999 transaction in a day option not displayed,Please verify the screen shot");
            }
            try {
                softAssert.assertAll();
            } catch (
                    AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        }
    }

    @And("User validate the functions in transaction section page")
    public void userValidateTheFunctionsInTransactionSectionPage() {
        softAssert = new SoftAssert();
        if (scenario.getName().contains("Validate more than 999 transaction in between the dates")) {
            /***Download options is removed ,Screen was changed ***/
            accountStatementPage.emailGetStatementButton.click();
            softAssert.assertTrue(accountStatementPage.emailingPopUp.isDisplayed(), "Emailing pop up not displayed");
            waitTillInvisibilityElement(driver, accountStatementPage.emailingPopUp);
            try {
                if (accountStatementPage.toManyTransactionPopUp.isDisplayed()) {
                    accountStatementPage.toManyTransactionPopUp.click();
                }
            } catch (NoSuchElementException e) {
                logger.warn("Too many transaction po up displayed");
            }
            /***Reset Filter functions***/
            accountStatementPage.resetFilterButton.click();
            staticWait(2000);
            scrollIntoViewUp(driver, accountStatementPage.pageHeader);
            softAssert.assertNotEquals(accountStatementPage.validateRemovingTheFilter.getText(), "01, Apr 2023 - 10, Apr 2023", "reset filter options not working");

        } else if (scenario.getName().contains("Validate more than 999 transaction in a day")) {
            String currentWindow = driver.getWindowHandle();
            try {
               scrollIntoView(driver, accountStatementPage.downloadStatement);
               accountStatementPage.downloadStatement.click();
           }
           catch(NoSuchElementException elementNotAbleToFind){
               attachScreenshot(driver,scenario);
               logger.error("Element not visible please look at the screen shot");
               softAssert.fail("Element not visible please look at the screen shot");
           }
            waitTillInvisibilityOfLoader(driver);
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(currentWindow)) {
                    driver.switchTo().window(windowHandle);
                    softAssert.assertTrue(driver.getCurrentUrl().contains("branch-locator"), "branch-locator page not displayed");
                    break;
                }
            }
            driver.close();
            driver.switchTo().window(currentWindow);
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

    @And("User validated the downloadStatement in {string} format")
    public void userValidatedTheDownloadStatementInFormat(String fileFormat) {
        softAssert = new SoftAssert();
        boolean isDisplayedErrorMessage = false;
        if (fileFormat.contains("pdf")) {
            accountStatementPage.downloadPdfButton.click();
        } else if (fileFormat.contains("csv")) {
            accountStatementPage.downloadCsvButton.click();
        } else if (fileFormat.contains("xlsx")) {
            accountStatementPage.downloadXlsButton.click();
        }
        try {
            if (accountStatementPage.downloadFailed.isDisplayed()) {
                attachScreenshot(driver, scenario);
                isDisplayedErrorMessage = true;
                accountStatementPage.downloadFailedBackButton.click();
                softAssert.fail(fileFormat+" deownload failed");
            }
        } catch (NoSuchElementException e) {
        }
        if (isDisplayedErrorMessage == false) {
            waitTillInvisibilityElement(driver, accountStatementPage.downloadingPopUp);
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement." + fileFormat + "");
            staticWait(3000);
            softAssert.assertTrue(downloadedFile.exists(), "AccountStatement File download failed");
            waitTillInvisibilityOfLoader(driver);
            logger.info("File name " + downloadedFile);
            if (downloadedFile.exists()) {
                logger.info("That file " + downloadedFile + " is deleted");
                downloadedFile.delete();
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

    @And("User clicks on {string} financial year download statement in {string} format")
    public void userClicksOnFinancialYearDownloadStatementInFormat(String financialYear, String fileFormat) {
        boolean isErrorMessageDownload = false;
        softAssert = new SoftAssert();
        waitTillElementToBeClickable(driver, accountStatementPage.currentFinancialDownloadButton);
        if (financialYear.contains("current")) {
            accountStatementPage.currentFinancialDownloadButton.click();
        } else if (financialYear.contains("previous")) {
            accountStatementPage.previousFinancialDownloadButton.click();
        }
        if (fileFormat.contains("pdf")) {
            accountStatementPage.downloadFileType("PDF").click();
        } else if (fileFormat.contains("xlsx")) {
            accountStatementPage.downloadFileType("XLS").click();
        } else if (fileFormat.contains("csv")) {
            accountStatementPage.downloadFileType("CSV").click();
        }
        accountStatementPage.financialPopUpDownloadButton.click();
        try {
            if (accountStatementPage.downloadFailed.isDisplayed()) {
                attachScreenshot(driver, scenario);
                isErrorMessageDownload = true;
                accountStatementPage.downloadFailed.click();
            }
        } catch (NoSuchElementException e) {
        }

        if (isErrorMessageDownload == false) {
            waitTillInvisibilityElement(driver, accountStatementPage.downloadingPopUp);
            File downloadedFile = new File("C:/Users/987993/Downloads/Account_Statement." + fileFormat + "");
            staticWait(2000);
            softAssert.assertTrue(downloadedFile.exists(), "AccountStatement File download failed");
            waitTillInvisibilityOfLoader(driver);
            logger.debug(fileFormat + "file downloaded successfully");
            if (downloadedFile.exists()) {
                downloadedFile.delete();
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

    @And("User verify the {string} financial year {string} pop up")
    public void userVerifyTheFinancialYearPopUp(String financialYear, String downloadOrEmail) {
        softAssert = new SoftAssert();
        if (financialYear.contains("current")) {
            String currentFinancialYearDuration[] = accountStatementPage.currentFinancialYearPeriodChecking.getText().split("-");
            String currentFinancialYearFrom = currentFinancialYearDuration[0].trim();
            String currentFinancialYearTo = currentFinancialYearDuration[1].trim();
            if (downloadOrEmail.contains("downloading")) {
                waitTillElementToBeClickable(driver, accountStatementPage.currentFinancialDownloadButton);
                accountStatementPage.currentFinancialDownloadButton.click();
            } else if (downloadOrEmail.contains("emailing")) {
                waitTillElementToBeClickable(driver, accountStatementPage.currentFinancialDownloadButton);
                accountStatementPage.currentFinancialDownloadButton.click();
            }
            String currentFinancialYearPopUpFrom[] = accountStatementPage.financialYearPeriodInPopUp.getText().split("to");
            String currentFinancialPopUpYearFrom = currentFinancialYearPopUpFrom[0].trim();
            String currentFinancialPopUpYearTo = currentFinancialYearPopUpFrom[1].trim();
            softAssert.assertEquals("for " + currentFinancialYearFrom, currentFinancialPopUpYearFrom, "current financial year pop up from duration not matched");
            logger.info("for " + currentFinancialYearFrom);
            logger.info(currentFinancialPopUpYearFrom);
            softAssert.assertEquals(currentFinancialYearTo, currentFinancialPopUpYearTo, "current financial year pop up to duration not matched");
        } else if (financialYear.contains("previous")) {
            String popupMessage = null;
            String previousFinancialYearDuration[] = accountStatementPage.previousFinancialYearPeriodChecking.getText().split("-");
            String previousFinancialYearFrom = previousFinancialYearDuration[0].trim();
            String previousFinancialYearTo = previousFinancialYearDuration[1].trim();
            javaScriptExecutorClickElement(driver, accountStatementPage.previousFinancialDownloadButton);
            // accountStatementPage.previousFinancialDownloadButton.click();
            popupMessage = accountStatementPage.popUpMessage.getText();
            String previousFinancialYearPopUpFrom[] = accountStatementPage.financialYearPeriodInPopUp.getText().split("to");
            String previousFinancialPopUpYearFrom = previousFinancialYearPopUpFrom[0].trim();
            String previousFinancialPopUpYearTo = previousFinancialYearPopUpFrom[1].trim();
            softAssert.assertEquals(previousFinancialPopUpYearFrom, "for " + previousFinancialYearFrom, "previous financial year pop up from duration not matched");
            softAssert.assertEquals(previousFinancialPopUpYearTo, previousFinancialYearTo, "previous financial year pop up to duration not matched");
        }

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
        accountStatementPage.cancelButton.click();

    }

    @And("User clicks on {string} financial year send email in {string} format")
    public void userClicksOnFinancialYearSendEmailInFormat(String financialYear, String fileFormat) {
        softAssert = new SoftAssert();
        if (financialYear.contains("current")) {
            accountStatementPage.currentFinancialEmailButton.click();
        } else if (financialYear.contains("previous")) {
            waitTillElementToBeClickable(driver, accountStatementPage.previousFinancialEmailButton);
            accountStatementPage.previousFinancialEmailButton.click();
        }
        accountStatementPage.emailAddressEnter.sendKeys(Keys.chord(Keys.chord(Keys.CONTROL, "a")), fileReader.accStatementTestData.get("emailId"));
        if (fileFormat.contains("pdf")) {
            accountStatementPage.emailSentStatementType("PDF").click();
        } else if (fileFormat.contains("xlsx")) {
            accountStatementPage.emailSentStatementType("XLS").click();
        } else if (fileFormat.contains("csv")) {
            accountStatementPage.emailSentStatementType("CSV").click();
        }
        accountStatementPage.quickEmailSent.click();
        softAssert.assertTrue(accountStatementPage.emailingPopUp.isDisplayed(), "Emailing pop up not displayed");
        waitTillVisibilityElement(driver, accountStatementPage.toastMessage);
        softAssert.assertTrue(accountStatementPage.toastMessage.getText().contains("uccessfully"), "Email sending in " + fileFormat + " file sending failed");
        logger.info(financialYear + " " + fileFormat + " file format Email sent successfully");
        try {
            if (accountStatementPage.anyErrorWhileDownloadOrEmail.isDisplayed()) {
                if (accountStatementPage.errorButtonFailedMessage.getText().contains("Back") || accountStatementPage.errorButtonFailedMessage.getText().contains("Okay")) {
                    attachScreenshot(driver, scenario);
                    clickOnButton(accountStatementPage.errorButtonFailedMessage);
                    softAssert.fail("Current Financial year sent email statement failed");
                } else if (accountStatementPage.errorButtonFailedMessage.getText().contains("OK")) {
                    attachScreenshot(driver, scenario);
                    logger.error("Current Financial year sent email statement failed,No data's available");
                    accountStatementPage.noRecordFoundOkButton.click();
                }
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

}










