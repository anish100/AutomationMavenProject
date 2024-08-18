package stepDefs;

import dataProviders.ConfigFileReader;
import dataProviders.ExcelFileReader;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.mk_latn.No;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pom.AccountStatementPage;
import pom.AccountSummaryPage;
import pom.HomePage;
import pom.LoanPage;
import reusable.Base;
import reusable.TestContext;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LoanStepDef extends Base {

    private static final Logger logger = LogManager.getLogger(LoanStepDef.class);
    LoanPage loanPage;
    AccountSummaryPage accountSummaryPage;
    AccountStatementStepDef accountStatementStepDef;
    MoneyTransferStepDef moneyTransferStepDef;
    HomePageStepDef homePageStepDef;
    HomePage homePage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    String isLoanAppliedSuccessfully;
    String loanType;
    // String loanAccountNumber;
    String loanInterestRate;
    String loanOutstandingBalance;

    String overDueAmountLoanDetailsPage;
    String loanApplierName;

    private String maxAmountVerify;


    public LoanStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        loanPage = context.getPageObjectManager().getLoanPage();
        homePage = context.getPageObjectManager().getHomePage();
        accountSummaryPage = context.getPageObjectManager().getAccountSummaryPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        homePageStepDef = new HomePageStepDef(context);
        accountStatementStepDef = new AccountStatementStepDef(context);
        moneyTransferStepDef = new MoneyTransferStepDef(context);
    }

    @Then("User can verify the loan page")
    public void userCanVerifyTheLoanPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("loans"), "loan page not displayed");
        try {
            softAssert.assertTrue(loanPage.loanPageHeader.getText().contains("Apply for Loans"), "loan page header not displayed");
            softAssert.assertTrue(loanPage.loanPageSectionDetails.size() != 0, "loan page section not displayed");
            softAssert.assertTrue(loanPage.loanPageLoanApplyButton.isDisplayed(), "loan apply button not displayed");
            softAssert.assertTrue(loanPage.loanPageSectionViewButton.isDisplayed(), "loan page section view button not displayed");
            loanType = loanPage.loanTypeForLoanNumber(fileReader.loanTestData.get("loanAccountNumber")).getText();
            loanInterestRate = loanPage.loanInterestForLoanNumber(fileReader.loanTestData.get("loanAccountNumber")).getText();
            loanOutstandingBalance = loanPage.outStandingBalanceForLoanNumber(fileReader.loanTestData.get("loanAccountNumber")).getText().replace("₹", "").replaceAll(",", "").trim();
        } catch (NoSuchElementException exception) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Elements not displayed" + exception.toString());
        }
        logger.info("User has a " + loanType + " loan");
        logger.info("Loan outstanding balance is :" + loanOutstandingBalance);
        logger.info("Loan Account number is :" + loanPage.loanPageSectionLoanAccNumber.getText());
        setAccountNumber(loanPage.loanPageSectionLoanAccNumber.getText().trim());
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on one of the view button active loan account section")
    public void userClicksOnOneOfTheViewButtonActiveLoanAccountSection() {
        loanPage.viewButtonForSpecificLoan(fileReader.loanTestData.get("loanAccountNumber")).click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User will be navigated on the loan details page")
    public void userWillBeNavigatedOnTheLoanDetailsPage() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, loanPage.loanDetailsPageLoanTypeName);
        softAssert.assertTrue(loanPage.loanDetailsPageLoanTypeName.getText().contains(loanPage.loanDetailsPageLoanTypeName.getText()), "loan type not matched");
        softAssert.assertTrue(loanPage.loanDetailsPageRangeBar.isDisplayed(), "loan range bar not displayed ");
        softAssert.assertTrue(loanPage.loanDetailsPageLoanAccountNumber.getText().contains(loanPage.loanDetailsPageLoanAccountNumber.getText()), "loan account number not matched");
        softAssert.assertTrue(loanPage.loanDetailsPageActiveIcon.isDisplayed(), "loan active status icon not displayed");
        softAssert.assertTrue(loanPage.loanDetailsOutstandingBalance.getText().contains("₹"), "loan outstanding balance not displayed");
        softAssert.assertTrue(loanPage.loanDetailsPrincipalPaid.getText().contains("₹"), "loan principal paid amount not displayed");
        softAssert.assertTrue(loanPage.loanDetailsPageDisbursedAmount.getText().contains("₹"), "loan disbursement amount not displayed");
        softAssert.assertTrue(loanPage.loanDetailsPageNextInstalmentAmount.getText().contains("₹"), "loan next instalment amount not displayed");
        // softAssert.assertFalse(loanPage.loanDetailsPageNextInstalmentDate.getText().isEmpty(),"loan next instalment date not displayed");

        String loanOverDue = loanPage.loanDetailsPageOverdue.getText().replace("₹", "").replaceAll(",", "").trim();
        String loanOutstanding = loanPage.loanDetailsOutstandingBalance.getText().replace("₹", "").replaceAll(",", "").trim();

        double loanOverDueNumeric = Double.parseDouble(loanOverDue);
        double loanOutstandingNumeric = Double.parseDouble(loanOutstanding);
        double principlePaid = Double.parseDouble(loanPage.loanDetailsPrincipalPaid.getText().replace("₹", "").replaceAll(",", "").trim());
        double loanOutStandingInLoanDetails = loanOverDueNumeric + loanOutstandingNumeric;
        double disbursedAmount = Double.parseDouble(loanPage.loanDetailsPageDisbursedAmount.getText().replace("₹", "").replaceAll(",", "").trim());
        //  softAssert.assertEquals(loanOutStandingInLoanDetails,Double.parseDouble(loanOutstandingBalance.replace("₹", "").replaceAll(",", "").trim()), "Outstanding balance not matched");
        //  softAssert.assertEquals(disbursedAmount,principlePaid,"Outstanding balance not matched");

        logger.info("Loan Outstanding balance");


        try {
            if (loanPage.loanDetailsPagePayNowButton.isDisplayed()) {
                logger.info("The account holder has an overdue");
                //System.out.println("The Given CIF have CASA");
            } else {
                logger.info("The account holder doesn't have an overdue");
                // System.out.println("The Given CIF doesn't have CASA");
            }
        } catch (NoSuchElementException e) {
            logger.info("The user doesn't have pay now option");

        }
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User Verify the account statement in quick links")
    public void userVerifyTheAccountStatementInQuickLinks() {
        softAssert = new SoftAssert();
        loanPage.quickLinksStatementTab.click();
        waitTillInvisibilityOfLoader(driver);
        softAssert.assertTrue(driver.getCurrentUrl().contains("statement"), "statement page url not matched");
        softAssert.assertTrue(loanPage.pageHeader.isDisplayed(), "Account statement page header not displayed");
        softAssert.assertTrue((loanPage).selectAccTab.isDisplayed(), "select account number not displayed");
        driver.navigate().back();
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User Verify the money transfer in quick links")
    public void userVerifyTheMoneyTransferInQuickLinks() {
        softAssert = new SoftAssert();
        loanPage.loanMoneyTransferQuickLink.click();
        waitTillInvisibilityOfLoader(driver);
        softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "money transfer page not displayed");
        softAssert.assertTrue(loanPage.moneyTransferPageHeader.getText().contains("Money Transfer"), "money transfer page header not displayed");
        softAssert.assertTrue(loanPage.recentPayeeMoneyTransferHeader.getText().contains("Recent Payee"), "money transfer payee header not displayed");
        driver.navigate().back();
        waitTillInvisibilityOfLoader(driver);
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify more details section in loan details page")
    public void userVerifyMoreDetailsSectionInLoanDetailsPage() {
        softAssert = new SoftAssert();
        try {
            scrollIntoView(driver, loanPage.loanDetailsMoreDetailsLoanAccNumber);
            softAssert.assertTrue(loanPage.loanDetailsMoreDetailsLoanAccNumber.getText().contains(loanPage.loanDetailsPageLoanAccountNumber.getText()), "loan account number not matched");
            softAssert.assertTrue(loanPage.loanDetailsMoreDetailsDisbursedAmount.getText().contains(loanPage.loanDetailsPageDisbursedAmount.getText()), "loan disburse amount not matched");
            softAssert.assertTrue(loanPage.loanDetailsMoreDetailsDisbursalDate.getText().contains(loanPage.loanDetailsLoanStartDate.getText()), "loan disburse date not matched");
            softAssert.assertTrue(loanPage.loanDetailsMoreDetailsLoanMaturityDate.getText().contains(loanPage.loanDetailsLoanEndDate.getText()), "loan maturity date not matched");
            softAssert.assertTrue(loanPage.loanDetailsMoreDetailsRateOfInterest.getText().contains(loanInterestRate), "loan interest rate not matched");
            softAssert.assertTrue(loanPage.loanDetailsMoreDetailsPrincipalPaid.getText().replaceAll("[^\\d.]", "").contains(loanPage.loanDetailsPrincipalPaid.getText().replaceAll("[^\\d.]", "")), "loan principal paid not matched");
            softAssert.assertTrue(loanPage.loanDetailsMoreDetailsNextEmiDate.getText().contains(loanPage.loanDetailsPageNextInstalmentDate.getText()), "loan next instalment date not matched");
            softAssert.assertTrue(loanPage.loanDetailsMoreDetailsNextInstalment.getText().contains(loanPage.loanDetailsPageNextInstalmentAmount.getText()), "loan next instalment amount not matched");
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            logger.error("loan details section not displayed");
            softAssert.fail("loan details section not displayed");
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on recent activity section")
    public void userClicksOnRecentActivitySection() {
        try {
            scrollIntoView(driver, loanPage.loanDetailsRecentActivity);
            loanPage.loanDetailsRecentActivity.click();
            waitTillInvisibilityOfLoader(driver);
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Please verify the screen shot,Loan details page not displayed");
        }
    }

    @And("User verify recent activity section in loan details page")
    public void userVerifyRecentActivitySectionInLoanDetailsPage() {
        softAssert = new SoftAssert();
        try {
            scrollIntoView(driver, loanPage.loanDetailsRecentActivityTableDetails);
            softAssert.assertTrue(loanPage.loanDetailsRecentActivityTable.isDisplayed(), "Recent activity details not visible");
        } catch (NoSuchElementException e) {
            // attachScreenshot(driver, scenario);
            logger.error("recent activity section not displayed");
            softAssert.fail("recent activity section not displayed");
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on transaction recent activity section")
    public void userClicksOnTransactionRecentActivitySection() {
        try {
            loanPage.loanDetailsRecentActivityTableDetails.click();
            waitTillVisibilityElement(driver, loanPage.loanDetailsRecentActivityPopupAmount);
        } catch (NoSuchElementException e) {
            logger.error("Recent transaction section not showing");
        }
    }

    @Then("User verify the popup in recent activity")
    public void userVerifyThePopupInRecentActivity() {
        try {
            softAssert = new SoftAssert();
            softAssert.assertTrue(loanPage.loanDetailsRecentActivityPopupAmount.getText().trim().contains(loanPage.loanDetailsRecentActivityTableAmount.getText().trim()), "transaction amount in the section not matched");
            softAssert.assertEquals(loanPage.loanDetailsRecentActivityPopupDescription.getText().trim().replaceAll("\\s",""), loanPage.loanDetailsRecentActivityTableDescription.getText().trim().replaceAll("\\s",""), "description of loan not matched");
            logger.info(loanPage.loanDetailsRecentActivityPopupDescription.getText() + " " + loanPage.loanDetailsRecentActivityTableDescription.getText());
            softAssert.assertEquals(loanPage.loanDetailsRecentActivityPopupTransactionDate.getText().trim(), loanPage.loanDetailsRecentActivityTableTransactionDate.getText().trim(), "transaction date not matched");
        } catch (NoSuchElementException e) {
            logger.error("Recent transaction activity pop up not displayed");
        }
        try {
            softAssert.assertAll();

        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User verify the download in recent activity pop up")
    public void userVerifyTheDownloadInRecentActivityPopUp() {
        try {
            softAssert = new SoftAssert();
            waitTillElementToBeClickable(driver,loanPage.loanDetailsRecentActivityPopupDownloadButton);
            clickOnButton(loanPage.loanDetailsRecentActivityPopupDownloadButton);
            File transactionPdfFile = new File("C:/Users/987993/Downloads/" + "TxnRpt_" + getCurrentDateTime() + ".pdf");
            staticWait(5000);
            softAssert.assertTrue(transactionPdfFile.exists(), "Transaction loan Statement download failed");
            waitTillInvisibilityOfLoader(driver);
            if (transactionPdfFile.exists()) {
                transactionPdfFile.delete();
            }

            try {
                softAssert.assertAll();

            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        } catch (NoSuchElementException e) {
            logger.error("Recent transaction activity pop up not displayed");
        }


    }

    @And("User clicks cancel button in recent activity pop up")
    public void userClicksCancelButtonInRecentActivityPopUp() {
        try {
            loanPage.loanDetailsRecentActivityPopupCancelButton.click();
            waitTillInvisibilityOfLoader(driver);
        } catch (NoSuchElementException e) {
            logger.error("Recent transaction activity pop up not displayed");
        }
    }

    @And("User verify amortisation Table section in loan details page")
    public void userVerifyAmortisationTableSectionInLoanDetailsPage() {
        softAssert = new SoftAssert();
        boolean isDisplayedAmortizationSection;
        boolean isDisplayedAmortizationDownloadPopUp;
        loanPage.pageDetailsAmortisation.click();
        try {
            scrollIntoView(driver, loanPage.pageDetailsAmortisation);
            isDisplayedAmortizationSection = loanPage.pageDetailsAmortisationPDFDownload.isDisplayed();
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            logger.error("amortization section section not displayed");
            softAssert.fail("amortization section section not displayed");
        }
        if (isDisplayedAmortizationSection = true) {
            logger.info("Amortization section displayed status is :" + isDisplayedAmortizationSection);
            // System.out.println(isDisplayedAmortizationSection);
            softAssert.assertTrue(loanPage.pageDetailsAmortisationTable.size() != 0, "amortisation table details not visible");

        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks loan statement button")
    public void userClicksLoanStatementButton() {
        loanPage.loanStatementButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify loan statement page")
    public void userVerifyLoanStatementPage() {
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(driver.getCurrentUrl().contains("loanstatement"), "loan statement page url verify failed,showing " + loanPage.pageHeader + " page");
            softAssert.assertTrue(loanPage.loanStatementPageHeader.getText().contains("Loan Statement"), "loan statement page header not visible");
            softAssert.assertTrue(loanPage.loanStatementPageGetStatement.isDisplayed(), "get statement not displayed");
            softAssert.assertTrue(loanPage.loanStatementPageSelectAccountNumber.isDisplayed(), "select account number tab not displayed");
            softAssert.assertTrue(loanPage.validateSelectLoanAccShowingDefaultAccNo.getText().matches("\\d+"), "select account number tab by default loan acc number not displayed");
            softAssert.assertTrue(loanPage.loanStatementDateFilter.isDisplayed(), "loan date filter tab not displayed");
            softAssert.assertTrue(loanPage.loanStatementFilterButton.isDisplayed(), "loan range filter tab not displayed");
/**If Any error occurs in loan statement page **/

            if (loanPage.loanStatementPageFailed.getText().contains("Please try another search")) {
                softAssert.fail("Internal ServerError");
                attachScreenshot(driver, scenario);
                scrollIntoView(driver, loanPage.loanStatementPageFailed);
                logger.error("Due to the internal server error details not dispalyed");
                Assert.fail("Internal server Error");
            } else if (loanPage.loanStatementPageFailed.getText().contains("No transactions has been made in give time period")) {
                logger.debug("There is no transaction in between the time period");
                attachScreenshot(driver, scenario);
            } else {
//              if (loanPage.loanStatementPageTableSection.isDisplayed()) {
                softAssert.assertTrue(loanPage.loanStatementPageTableSection.isDisplayed(), "loan statement table section visible");
                softAssert.assertTrue(loanPage.loanStatementPageDownloadStatementButton.isDisplayed(), "loan statement download button not displayed");
            }


        } catch (NoSuchElementException e) {
            //    attachScreenshot(driver, scenario);
            logger.error(e.toString());
        }
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());

        }
    }

    @And("User select the account the account from loan statement page")
    public void userSelectTheAccountTheAccountFromLoanStatementPage() {
        loanPage.loanStatementPageSelectAccountNumber.click();
        waitForPageLoad(driver);
        loanPage.selectLoanAccountFromDD(fileReader.loanTestData.get("loanAccountNumber"));
        logger.info("Loan statement for selected loan account number is :" + fileReader.loanTestData.get("loanAccountNumber"));

    }

    @When("User apply filter on loan account statement section")
    public void userApplyFilterOnLoanAccountStatementSection() {
        loanPage.loanStatementFilterButton.click();
        loanPage.loanStatementFilterAccType.click();
        loanPage.loanStatementAccType(fileReader.loanTestData.get("filterTransactionType")).click();
        logger.info("Selected transaction type filter is" + fileReader.loanTestData.get("filterTransactionType"));
        waitForPageLoad(driver);
        loanPage.loanStatementFilterRangeFrom.sendKeys(Keys.chord(Keys.chord(Keys.CONTROL, "a"), fileReader.loanTestData.get("amountRangeFrom")));
        loanPage.loanStatementFilterRangeTo.sendKeys(Keys.chord(Keys.chord(Keys.CONTROL, "a"), fileReader.loanTestData.get("amountRangeTo")));
        logger.info("Amount filter from range is Rs." + fileReader.loanTestData.get("amountRangeFrom") + "&" + "Amount filter to range is Rs." + fileReader.loanTestData.get("amountRangeTo"));
        waitForPageLoad(driver);
        loanPage.loanStatementFilterApplyButton.click();
        //staticWait(3000);
        waitTillElementToBeClickable(driver, loanPage.loanStatementDateFilter);
        //Date Filter Apply
        loanPage.loanStatementDateFilter.click();
        String fromDateSelect = fileReader.loanTestData.get("transactionFromDate");
        String[] splitDate = fromDateSelect.split("/");
        String fromDate = splitDate[0];

        String fromMonth = String.valueOf(Integer.valueOf(splitDate[1]) - 1);
        String fromYear = splitDate[2];

        waitTillElementToBeClickable(driver, loanPage.loanStatementDateFilterYearClick);
        loanPage.loanStatementDateFilterYearClick.click();
        selectDDByValue(loanPage.loanStatementDateFilterYearSelect, fromYear);

        waitTillElementToBeClickable(driver, loanPage.loanStatementDateFilterMonthClick);
        loanPage.loanStatementDateFilterMonthClick.click();
        selectDDByValue(loanPage.loanStatementDateFilterMonthSelect, fromMonth);

        loanPage.loanStatementFilterDateFromDD(fromDate).click();
//To date
        String toDateSelect = fileReader.loanTestData.get("transactionToDate");
        String[] splitToDate = toDateSelect.split("/");
        String toDate = splitToDate[0];

        String toMonth = String.valueOf(Integer.valueOf(splitToDate[1]) - 1);
        String toYear = splitToDate[2];
        loanPage.loanStatementDateFilterYearClick.click();
        selectDDByValue(loanPage.loanStatementDateFilterYearSelect, toYear);
        loanPage.loanStatementDateFilterMonthClick.click();
        selectDDByValue(loanPage.loanStatementDateFilterMonthSelect, toMonth);
        loanPage.loanStatementFilterDateFromDD(toDate).click();
        logger.info("User apply duration filter the dates  from " + fromDate + "-" + fromMonth + "-" + fromYear + " to " + toDate + "-" + toMonth + "-" + toYear);
        loanPage.loanStatementCalendarFilterApplyButton.click();
        waitTillInvisibilityOfLoader(driver);

    }

    @Then("User verify loan account statement account section as per applied filter")
    public void userVerifyLoanAccountStatementAccountSectionAsPerAppliedFilter() {
        softAssert = new SoftAssert();
        for (WebElement loanAmountTransactionList : loanPage.loanStatementAmountList) {
            String loanAmountsLists = loanAmountTransactionList.getText().replaceAll("[^\\d.]", "").trim();
            double loanAmountNum = Double.parseDouble(loanAmountsLists);
            // System.out.println(loanAmountNum);
            String rangeFrom = String.valueOf(fileReader.loanTestData.get("amountRangeFrom"));
            Double rangeFromNum = Double.parseDouble(rangeFrom);
            //System.out.println("Filter Amount Range"+rangeFromNum);
            String rangeTo = String.valueOf(fileReader.loanTestData.get("amountRangeTo"));
            Double rangeToNum = Double.parseDouble(rangeTo);
            //System.out.println(rangeToNum);
            softAssert.assertTrue(loanAmountNum >= rangeFromNum && loanAmountNum <= rangeToNum, "Amount in the loan statement transaction not sort by filter ");
        }

        String dateSplit = loanPage.loanStatementBetweenDates.getText();
        String[] fromDateFilter = dateSplit.split("-");
        String startDate = fromDateFilter[0];
        String endDate = fromDateFilter[1];

//Transaction date validation
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy");
        Date dateToValidate = null;
        Date starts;
        Date end;

        try {
            starts = sdf.parse(startDate);
            end = sdf.parse(endDate);
            List<LocalDateTime> dateList = new ArrayList<>();
            for (WebElement transactionDate : loanPage.loanStatementTransactionDate) {
                dateToValidate = sdf.parse(transactionDate.getText());
                softAssert.assertTrue(dateToValidate.compareTo(starts) >= 0 && dateToValidate.compareTo(end) <= 0, "transaction date not lie between int the filter dates");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //After apply both filter
        for (WebElement loanAmountTransactionList : loanPage.loanStatementAmountList) {
            String loanAmountsLists = loanAmountTransactionList.getText().replaceAll("[^\\d.]", "").trim();
            double loanAmountNum = Double.parseDouble(loanAmountsLists);
            String rangeFrom = String.valueOf(fileReader.loanTestData.get("amountRangeFrom"));
            Double rangeFromNum = Double.parseDouble(rangeFrom);
            String rangeTo = String.valueOf(fileReader.loanTestData.get("amountRangeTo"));
            Double rangeToNum = Double.parseDouble(rangeTo);

            softAssert.assertTrue(loanAmountNum >= rangeFromNum && loanAmountNum <= rangeToNum, "Amount in the loan statement transaction not sort by filter ");
        }
        // loanPage.loanStatementRangeFilterRemove.click();
        if (fileReader.loanTestData.get("filterTransactionType").equalsIgnoreCase("C")) {
            for (WebElement amount : loanPage.loanStatementAmountList) {
                softAssert.assertTrue((amount.getText().contains("+")), "Credit Account type not sort by filter");
            }

        } else if (fileReader.loanTestData.get("filterTransactionType").equalsIgnoreCase("D")) {
            for (WebElement amount : loanPage.loanStatementAmountList) {
                softAssert.assertTrue((amount.getText().contains("-")), "Debit Account type not sort by filter");
            }
        }
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User remove all applied filter")
    public void userRemoveAllAppliedFilter() {

        if (fileReader.loanTestData.get("filterTransactionType").equalsIgnoreCase("C")) {
            loanPage.accTypeFilterRemoveCredit.click();
        } else if (fileReader.loanTestData.get("filterTransactionType").equalsIgnoreCase("D")) {
            loanPage.accTypeFilterRemoveDebit.click();
        }
        staticWait(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(loanPage.loanStatementRangeFilterRemove));
        try {
            loanPage.loanStatementRangeFilterRemove.click();
            loanPage.loanStatementDateFilterRemove.click();
        } catch (NoSuchElementException e) {
            logger.info(e.toString());
        }
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User verify transaction date sorting functionality in loan account statement page")
    public void userVerifyTransactionDateSortingFunctionalityInLoanAccountStatementPage() {
        //Ascending
        softAssert = new SoftAssert();
        try {
            loanPage.loanStatementtransactionDateSort.click();
        } catch (NoSuchElementException elementNotFound) {
            attachScreenshot(driver, scenario);
        }
        //    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd, MMM yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        List<Date> dateListAsc = new ArrayList<>();
        for (WebElement dateElementAsc : loanPage.loanStatementTransactionDate) {
            String dateElementsAsc = dateElementAsc.getText();
            try {
                Date dateTime = dateFormat.parse(dateElementsAsc);
                dateListAsc.add(dateTime);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < dateListAsc.size() - 1; i++) {
            Date currentDateAsc = dateListAsc.get(i);
            Date nextDateAsc = dateListAsc.get(i + 1);
            softAssert.assertTrue(currentDateAsc.before(nextDateAsc) || currentDateAsc.equals(nextDateAsc), "Transaction date not sorted in Ascending");
        }
        waitForPageLoad(driver);
        //Descending Sort

        loanPage.loanStatementtransactionDateSort.click();
        DateTimeFormatter dateFormatDsc = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        List<Date> dateListDsc = new ArrayList<>();
        for (WebElement dateElementDsc : loanPage.loanStatementTransactionDate) {
            String dateElementsDsc = dateElementDsc.getText();
            try {
                Date dateTime = dateFormat.parse(dateElementsDsc);
                dateListAsc.add(dateTime);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());

        }

    }

    @And("User verify description sorting functionality in loan account statement page")
    public void userVerifyDescriptionSortingFunctionalityInLoanAccountStatementPage() {
        softAssert = new SoftAssert();
        try {
            loanPage.loanStatementDescriptionSort.click();
        } catch (ElementNotInteractableException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail(e.toString());
        }
        List<String> sortDescriptionAsc = new ArrayList<>();
        for (WebElement sortAsc : loanPage.loanStatementTransactionDescription) {
            String descriptionElement = sortAsc.getText();
            sortDescriptionAsc.add(descriptionElement);
        }
        //Descending
        try {
            loanPage.loanStatementDescriptionSort.click();
        } catch (ElementNotInteractableException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail(e.toString());
        }
        List<String> sortDescriptionDsc = new ArrayList<>();
        for (WebElement sortDsc : loanPage.loanStatementTransactionDescription) {
            String descriptionElementDsc = sortDsc.getText();
            sortDescriptionDsc.add(descriptionElementDsc);
        }
        softAssert.assertFalse(sortDescriptionAsc.equals(sortDescriptionDsc), "Description sorting failed");

        try {
            softAssert.assertAll();
            //attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());

        }

    }

    @And("User verify amount sorting functionality in loan account statement page")
    public void userVerifyAmountSortingFunctionalityInLoanAccountStatementPage() {

        loanPage.loanStatementAmountSort.click();
        softAssert = new SoftAssert();
        List<Double> amountListNum = new ArrayList<>();
        for (WebElement amountList : loanPage.loanStatementAmountList) {
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
        loanPage.loanStatementAmountSort.click();
        List<Double> amountListNumDsc = new ArrayList<>();
        for (WebElement amountListDsc : loanPage.loanStatementAmountList) {
            double amountListDetailsDsc = Double.parseDouble(amountListDsc.getText().replaceAll("[^\\d.]", "").replaceAll("\\n", "").trim());
            amountListNum.add(amountListDetailsDsc);
        }
        for (int i = 0; i < amountListNumDsc.size() - 1; i++) {
            Double currentAmountDsc = amountListNumDsc.get(i);
            Double nextAmountDsc = amountListNumDsc.get(i + 1);
            softAssert.assertTrue(currentAmountDsc >= nextAmountDsc, "Amount descending sorting functionality failed");
        }
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify balance sorting functionality in loan account statement page")
    public void userVerifyBalanceSortingFunctionalityInLoanAccountStatementPage() {
        softAssert = new SoftAssert();
        //Ascending
        loanPage.loanStatementBalanceSort.click();
        List<Double> balanceList = new ArrayList<>();
        for (WebElement balanceSort : loanPage.loanStatementBalanceList) {
            // double balanceNum=Double.parseDouble(balanceSort.getText().replaceAll("[^\\d.]", "").replaceAll("\\n", "").trim());
            double balanceNum = Double.parseDouble(balanceSort.getText().replaceAll("₹", "").replaceAll(",", "").trim());
            balanceList.add(balanceNum);

        }
        for (int i = 0; i < balanceList.size() - 1; i++) {
            Double currentNum = balanceList.get(i);
            Double nextNum = balanceList.get(i + 1);
            softAssert.assertTrue(currentNum <= nextNum, "Balance ascending functionality failed");
        }
        //Descending
        loanPage.loanStatementBalanceSort.click();
        List<Double> balanceListDsc = new ArrayList<>();
        for (WebElement balanceSortDsc : loanPage.loanStatementBalanceList) {
            //double balanceNumDsc=Double.parseDouble(balanceSortDsc.getText().replaceAll("[^\\d.]", "").replaceAll("\\n", "").trim());
            double balanceNumDsc = Double.parseDouble(balanceSortDsc.getText().replaceAll("₹", "").replaceAll(",", "").trim());
            balanceList.add(balanceNumDsc);
        }
        for (int i = 0; i < balanceListDsc.size() - 1; i++) {
            Double currentNumDsc = balanceListDsc.get(i);
            Double nextNumDsc = balanceListDsc.get(i + 1);
            softAssert.assertTrue(currentNumDsc >= nextNumDsc, "Balance descending functionality failed");
        }
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify transaction section page shows label in loan statement")
    public void userVerifyTransactionSectionPageShowsLabelInLoanStatement() {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, loanPage.pageSectionShowLabel);
        String extractPageNumber[] = loanPage.pageSectionShowLabel.getAttribute("innerHTML").split(" ");
        int currentPageNo = Integer.parseInt(extractPageNumber[1].trim());
        int totalPageNo = Integer.parseInt(extractPageNumber[3].trim());
        if (totalPageNo < 1) {
            loanPage.pageSectionGetNextButton.click();
            String extractPageNumberChange[] = loanPage.pageSectionShowLabel.getText().split(" ");
            int currentPageNoChange = Integer.parseInt(extractPageNumberChange[1].trim());
            softAssert.assertTrue(loanPage.pageSectionGetPageNumber.getText().equals(currentPageNoChange), "current page not matched");
            System.out.println(currentPageNo);
            System.out.println(currentPageNoChange);
            loanPage.pageSectionGetPrevious.click();
        }
    }

    @And("User verify maximize functionality of loan account statement section")
    public void userVerifyMaximizeFunctionalityOfLoanAccountStatementSection() {
        softAssert = new SoftAssert();
        loanPage.loanStatementTableMaximize.click();
        // softAssert.assertFalse(loanPage.loanStatementPageGetStatement.isDisplayed(),"loan statement section maximize failed ");
        softAssert.assertTrue(loanPage.loanStatementTableMaximizeToolTip.getAttribute("innerHTML").contains("Minimize"), "Maximize failed");
        loanPage.loanStatementTableMaximize.click();
        //softAssert.assertTrue(loanPage.loanStatementTableMaximizeToolTip.getAttribute("innerHTML").contains("Maximize"),"Min");

        try {
            softAssert.assertAll();
            //  attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on one of the loan account statement list")
    public void userClicksOnOneOfTheLoanAccountStatementList() {

        try {
            waitTillVisibilityElement(driver, loanPage.loanStatementPageTableSectionTableRow);
            loanPage.loanStatementPageTableSectionTableRow.click();
            waitForPageLoad(driver);

        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            softAssert.fail("Loan statement transaction details not displayed");
        }
    }

    @Then("User verify the popup in loan statement section")
    public void userVerifyThePopupInLoanStatementSection() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(loanPage.loanStatementPopupTransactionAmount.getText().trim().replaceAll("\\n", "").contains(loanPage.loanStatementTableBalanceRow.getText().trim().replaceAll("\\n", "").replace("-", "").replace("+", "")), "transaction amount in pop up not matched");
        softAssert.assertEquals(loanPage.loanStatementPopupTransactionDate.getText(), loanPage.loanStatementTableDateRow.getText(), "transaction date in pop up not matched");
        softAssert.assertTrue(loanPage.loanStatementPopupDescription.getText().contains(loanPage.loanStatementTableDescriptionRow.getText()), "transaction description in pop up not matched");
        softAssert.assertTrue(loanPage.loanStatementPopupDownloadButton.isDisplayed(), "pop up download button not displayed");
        softAssert.assertTrue(loanPage.loanStatementPopupCancelButton.isDisplayed(), "pop up cancel button not displayed");

        try {
            softAssert.assertAll();
            //attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the download transaction in popup")
    public void userVerifyTheDownloadTransactionInPopup() {
        softAssert = new SoftAssert();
        try {
            File currentTransactionPdfFile = new File("C:/Users/987993/Downloads/" + "TxnRpt_" + getCurrentDateTime() + ".pdf");
            loanPage.loanStatementPopupDownloadButton.click();
            File transactionPdfFile = new File("C:/Users/987993/Downloads/" + "TxnRpt_" + getCurrentDateTime() + ".pdf");
            staticWait(2000);
            softAssert.assertTrue(transactionPdfFile.exists(), "pop up transaction loan Statement download failed");
            waitTillInvisibilityOfLoader(driver);
            if (transactionPdfFile.exists() || currentTransactionPdfFile.exists()) {
                transactionPdfFile.delete();
            }
        } catch (NoSuchElementException e) {
            softAssert.fail("Pop up download button not displayed");
        }
        try {
            softAssert.assertAll();
            //attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User clicks on cancel button in transaction pop up")
    public void userClicksOnCancelButtonInTransactionPopUp() {
        loanPage.loanStatementPopupCancelButton.click();
        waitTillInvisibilityElement(driver, loanPage.loanStatementPopupCancelButton);
    }

    @And("User verify the loan statement section by enter description")
    public void userVerifyTheLoanStatementSectionByEnterDescription() {
        String description = loanPage.loanStatementTableDescriptionRow.getText();
        try {
            loanPage.loanStatementEnterDescription.sendKeys(description);
            softAssert = new SoftAssert();
            softAssert.assertTrue(loanPage.loanStatementTableDescriptionRow.getText().contains(description), "search description loan details not matched");
            loanPage.loanStatementEnterDescription.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            softAssert.assertAll();
        } catch (Exception e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify downloadStatement in loan statement page")
    public void userVerifyDownloadStatementInLoanStatementPage() {
        softAssert = new SoftAssert();
        File currentTimeFileName = new File("C:/Users/987993/Downloads/" + "TxnRpt_" + getCurrentDateTime() + ".pdf");
        loanPage.loanStatementPageDownloadStatementButton.click();
        waitTillInvisibilityElement(driver, loanPage.downloadInitiate);
        staticWait(3000);
        File transactionPdfFile = new File("C:/Users/987993/Downloads/" + "TxnRpt_" + getCurrentDateTime() + ".pdf");
        System.out.println(transactionPdfFile);
        softAssert.assertTrue(transactionPdfFile.exists() || currentTimeFileName.exists(), " loan Statement transaction download failed");
        if (transactionPdfFile.exists()) {
            transactionPdfFile.delete();
        }
        waitForPageLoad(driver);
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks pay now button in loan details page")
    public void userClicksPayNowButtonInLoanDetailsPage() {
        try {
            if (loanPage.loanDetailsPagePayNowButton.isDisplayed()) {
                scrollIntoView(driver, loanPage.loanDetailsPagePayNowButton);
                overDueAmountLoanDetailsPage = loanPage.loanDetailsPageOverdue.getText();
                waitTillElementToBeClickable(driver, loanPage.loanDetailsPagePayNowButton);
                logger.info("This account has an overdue amount,the amount is : Rs." + overDueAmountLoanDetailsPage);
                loanPage.loanDetailsPagePayNowButton.click();
                waitTillInvisibilityOfLoader(driver);
            } else {
                //attachScreenshot(driver,scenario);
                logger.info("This account doesn't have an overdue amount");
                // System.out.println("The Given CIF doesn't have CASA");
            }
        } catch (NoSuchElementException e) {
            scrollIntoView(driver, loanPage.loanDetailsPayNowVisibilityScreenShot);
            // attachScreenshot(driver, scenario);
        }
    }

    @Then("User verify pay over due page details")
    public void userVerifyPayOverDuePageDetails() {
        // try {
        softAssert = new SoftAssert();
        waitTillVisibilityElement(driver, loanPage.payOverduePageHeader);
        softAssert.assertTrue(loanPage.payOverduePageHeader.getText().contains("Pay Overdue Amount"), "pay over due page header not visible");
        softAssert.assertTrue(loanPage.payOverduePageAccNumber.getText().contains(fileReader.loanTestData.get("loanAccountNumber").replaceAll("[^\\d.]", "").trim()), "loan account number not matched");
        softAssert.assertTrue(loanPage.payOverduePageAccType.getText().contains(loanType), "type of loan not matched");
        softAssert.assertTrue(loanPage.payOverduePageAmount.getText().contains(overDueAmountLoanDetailsPage), "over due amount not matched");

        if (loanPage.maxAmount.getAttribute("class").contains("icon-error")) {
            logger.info("Entered amount is more than available balance");
        } else {
            String maxAmount[] = loanPage.maxAmount.getText().split(":");
            String maxAmountVerify = maxAmount[1].trim();
            softAssert.assertEquals(maxAmountVerify, loanPage.payOverduePageAmount.getText(), "maxAmount not matched with over due amount");
        }
        /*********************/
        //  Quick Links Verify in pay over due details page
        homePageStepDef.userClicksOnTheDetailedStatementInQuickLinks();
        userVerifyLoanStatementPage();
        homePageStepDef.userNavigatesToThe("pay overdue page");
        homePageStepDef.userClicksOnTheMoneyTransferInQuickLinks();
        moneyTransferStepDef.userVerifyMoneyTransferHomePage();
        homePageStepDef.userNavigatesToThe("pay overdue page");
        waitTillElementToBeClickable(driver, loanPage.payOverduePageFromAccNumber);
        /**********/

        loanPage.payOverduePageFromAccNumber.click();
        loanPage.selectAccountFromDDOverDuePage(fileReader.loanTestData.get("fromAccountNumber"));
        logger.info("The over due amount pay from account number is :" + fileReader.loanTestData.get("fromAccountNumber"));
        waitTillLoading(driver);
        loanPage.payOverduePageAmountEnterToPay.sendKeys(Keys.chord(Keys.CONTROL, "a"), fileReader.loanTestData.get("overDueAmount"));
        logger.info("The overdue amount paid by the user is Rs." + fileReader.loanTestData.get("overDueAmount"));
        softAssert.assertEquals(loanPage.payOverduePageFromAccNumber.getText().trim(), fileReader.loanTestData.get("fromAccountNumber").trim(), "selected account number is not matched");
        if (loanPage.maxAmount.getAttribute("class").contains("icon-error")) {
            logger.info("Entered amount is more than available balance");
        } else {
            loanPage.payOverduePageMakePayementButton.click();
            waitTillInvisibilityOfLoader(driver);
        }
//        } catch (NoSuchElementException e) {
//               logger.info(e.toString());
//        }

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());

        }

    }

    @Then("User verify the pay over due payment status page")
    public void userVerifyThePayOverDuePaymentStatusPage() {
        softAssert = new SoftAssert();
        try {
            waitTillVisibilityElement(driver, loanPage.payOverduePageTransactionDetails);
            if (driver.getCurrentUrl().contains("loans/loan-details/pay-overdue/payment-status")) {
                waitTillVisibilityElement(driver, loanPage.payOverduePageTransactionDetails);
                softAssert.assertTrue(loanPage.payOverduePageTransactionDetails.isDisplayed(), "transaction details page header not displayed");
                softAssert.assertTrue(loanPage.payOverduePageSuccessfulPayment.getText().contains("Successfully"), "overdue payment failed");
                logger.info("Over due payment status is :" + loanPage.payOverduePageSuccessfulPayment.getText());
                softAssert.assertTrue(loanPage.payOverduePageSuccessfulPaymentLoanAccNo.getText().contains(fileReader.loanTestData.get("loanAccountNumber").replaceAll("[^\\d.]", "").trim()), "loan account number not matched");
                softAssert.assertTrue(loanPage.payOverduePageSuccessfulPaymentLoanAccType.getText().contains(loanType), "loan type not matched");
                clickOnButton(loanPage.backButton);
                waitTillInvisibilityOfLoader(driver);
            } else if (loanPage.memoFailed.getText().contains("Loan Overdue Payment Failed!")) {
                softAssert.assertTrue(loanPage.memoAccountMessage.isDisplayed(), "memo message is displayed");
                logger.info("Payment failed,due to the account having a memo");
                loanPage.backToLoanSummaryButton.click();
            } else if (loanPage.cancelButton.isDisplayed()) {
                attachScreenshot(driver, scenario);
                softAssert.fail("Please verify the screen shot");
                loanPage.cancelButton.click();
                staticWait(1000);
                loanPage.backButton.click();
                try {
                    if (loanPage.leaveButton.isDisplayed()) {
                        loanPage.leaveButton.click();
                        waitTillInvisibilityOfLoader(driver);
                    }
                    homePage.loanModuleButton.click();
                    waitTillInvisibilityOfLoader(driver);
                } catch (NoSuchElementException e) {
                }


            } else if (loanPage.maxAmount.getAttribute("class").contains("icon-error")) {
                logger.info("Entered amount is more than available balance");
                loanPage.backButton.click();
                loanPage.backButton.click();
            }
        } catch (NoSuchElementException e) {
            logger.info("Message " + e);
            setErrorsInList(e.toString());
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());

        }


    }

    @When("User clicks apply for loan in loan page")
    public void userClicksApplyForLoanInLoanPage() {
        loanPage.loanPageLoanApplyButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify apply for loan page")
    public void userVerifyApplyForLoanPage() {
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(loanPage.applyForLoanPageHeader.getText().contains("Apply for Loans"), "apply for loan page header not displayed");
            softAssert.assertTrue(loanPage.applyForLoanPageTextVerify.getText().contains("Our Funding"), "apply for loan page verify failed");
            softAssert.assertTrue(loanPage.quickLinks.isDisplayed(), "quick links not displayed");
            softAssert.assertTrue(loanPage.quickLinksStatementTab.isDisplayed(), "quick links statement tab not displayed");
            softAssert.assertTrue(loanPage.quickLinksMoneyTransferTab.isDisplayed(), "quick links money transfer tab not displayed");
//            loanPage.loanStatementQuickLink.click();
//
//            loanPage.loanMoneyTransferQuickLink.click();
//            softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "money transfer page not displayed");
//            softAssert.assertTrue(loanPage.moneyTransferPageHeader.getText().contains("Money Transfer"), "money transfer page header not displayed");
//            softAssert.assertTrue(loanPage.recentPayeeMoneyTransferHeader.getText().contains("Recent Payee"), "money transfer payee header not displayed");
//            driver.navigate().back();
//            waitTillInvisibilityOfLoader(driver);
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
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

    @When("User select loan type")
    public void userSelectLoanType() {
        logger.info("User select to apply for loan is :" + fileReader.loanTestData.get("typeOfLoanApply") + " Loan");
        clickOnButton(loanPage.chooseLoanType(fileReader.loanTestData.get("typeOfLoanApply")));
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the selected loan page")
    public void userVerifyTheSelectedLoanPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(driver.getCurrentUrl().contains("HomeLoan"), "selected loan apply page not displayed");
        softAssert.assertTrue(loanPage.pageHeader.getAttribute("innerHTML").contains("Home Loan"), "header is not displayed");
        softAssert.assertTrue(loanPage.quickLinks.isDisplayed(), "quick links not displayed in selected loan page");
        softAssert.assertTrue(loanPage.quickLinksStatementTab.isDisplayed(), "quick links statement tab not displayed in selected loan page");
        softAssert.assertTrue(loanPage.quickLinksMoneyTransferTab.isDisplayed(), "quick links money transfer tab not displayed in selected loan page");
        softAssert.assertTrue(loanPage.nameTab.isDisplayed(), "name tab is not displayed");
        softAssert.assertTrue(loanPage.mobileNumberTab.isDisplayed(), "mobile number tab is not displayed");
        softAssert.assertTrue(loanPage.mailIdTab.isDisplayed(), "mail id tab is not displayed");
        softAssert.assertTrue(loanPage.proceedButton.isEnabled(), "proceed button id not enabled");
        loanApplierName = loanPage.nameTab.getText();
        /**** CHANGE THE MOBILE NUMBER *****/
        staticWait(1000);
        loanPage.mobileNumberTab.sendKeys(Keys.chord(Keys.CONTROL, "a"), "8148992911");
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

    @When("User clicks proceed button in selected loan page")
    public void userClicksProceedButtonInSelectedLoanPage() {
        loanPage.proceedButton.click();
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify apply loan success page")
    public void userVerifyApplyLoanSuccessPage() {
        softAssert = new SoftAssert();
        try {
            staticWait(3000);
            softAssert.assertTrue(loanPage.loanPageHeader.getText().contains("Home Loan"), "page header not displayed");

            softAssert.assertTrue(loanPage.appliedSuccessfully.getText().contains("uccessfully"), "applied loan successful status not displayed");
            softAssert.assertTrue(loanPage.appliedStatusName.getText().contains(loanApplierName), "loan applied page name not matched");
            softAssert.assertTrue(loanPage.appliedStatusMobileNumber.isDisplayed(), "loan applied page mobile number not displayed");
            softAssert.assertTrue(loanPage.appliedStatusMailId.isDisplayed(), "loan applied page email id not matched");
            softAssert.assertTrue(loanPage.referenceCopiedButton.isDisplayed(), "loan applied page reference copy button not displayed");
            loanPage.referenceCopiedButton.click();
            waitTillVisibilityElement(driver, loanPage.referenceCopiedMessage);
            softAssert.assertTrue(loanPage.referenceCopiedMessage.isDisplayed(), "loan applied page reference number copied message not displayed");
            String copiedValue = getCopiedValue().trim();
            softAssert.assertEquals(loanPage.appliedStatusReferenceId.getText().trim(), copiedValue, "copied reference id not be the same");
            loanPage.backToLoanButton.click();
            isLoanAppliedSuccessfully = loanPage.appliedSuccessfully.getText();
            waitTillInvisibilityOfLoader(driver);
        } catch (NoSuchElementException e) {
            attachScreenshot(driver, scenario);
            logger.error(e.toString());
            loanPage.cancelButton.click();
            staticWait(2000);
            loanPage.backButton.click();
            logger.error("Loan applied by the user is failed");
            softAssert.fail("Loan applied failed");
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


    @And("User verify the navigation of loan type")
    public void userVerifyTheNavigationOfLoanType() {
        String applyLoanPageWindow = null;
        softAssert = new SoftAssert();
        // for (int i = loanPage.loanTypeList.size() - 1; i >= 0; i--) {
        for (int i = 0; i < loanPage.loanTypeList.size() - 1; i++) {
            WebElement clickButton = loanPage.loanTypeList.get(i);
            scrollIntoView(driver, clickButton);
            String currentLoan = clickButton.getText();
            if (clickButton.getText().contains("Personal Loan")) {
                clickButton.click();
                try {
                    if(homePage.toastMessage.isDisplayed()){
                        attachScreenshot(driver,scenario);
                        softAssert.fail(homePage.toastMessage.getText() +" message appeared,Please check the attached screen shot");
                    }
                    softAssert.assertTrue(loanPage.redirectingPopUp.isDisplayed(), "personal loan redirecting message not displayed");
                    softAssert.assertTrue(loanPage.cancelButton.isDisplayed(), "cancel button not displayed");
                    applyLoanPageWindow = driver.getWindowHandle();
                    loanPage.continueButton.click();
                    waitTillANewWindowOpens(driver);
                    for (String personalLoanWindow : driver.getWindowHandles()) {
                        if (!personalLoanWindow.equals(applyLoanPageWindow)) {
                            driver.switchTo().window(personalLoanWindow);
                            softAssert.assertTrue(driver.getCurrentUrl().contains("https://cvuat.aubankuat.in/au_lender_prequal_rejected"), "personal loan page not be the same");
                        }
                    }
                    driver.close();
                    driver.switchTo().window(applyLoanPageWindow);
                } catch (NoSuchElementException e) {
                    attachScreenshot(driver, scenario);
                    softAssert.fail("Personal lon redirection page failed");
                }

            } else if (clickButton.getText().contains("Car Loan")) {
                clickButton.click();
                waitTillInvisibilityOfLoader(driver);
                scrollIntoViewUp(driver, loanPage.pageHeader);
                logger.info("Current loan page header is :" + loanPage.loanPageHeader.getText());
                loanPage.carLoanUsedCar.click();
                waitTillInvisibilityOfLoader(driver);
                String usedCarLoan = loanPage.carLoanUsedCar.getText();
                softAssert.assertTrue(loanPage.backButtonApplyLoanPage.isDisplayed(), "back button not displayed");
                softAssert.assertTrue(loanPage.applyLoanPageHeader.getText().contains(usedCarLoan), " " + usedCarLoan + "page header not displayed");
                logger.info("User verified the " + loanPage.applyLoanPageHeader.getText() + " page");
/*******We didnt verify the quick links because naviate back option not given
 * *******/
                //                /** Quick Link Section  **/
//                waitTillElementToBeClickable(driver, accountSummaryPage.detailsStatementQuickLink);
//                javaScriptExecutorClickElement(driver, accountSummaryPage.detailsStatementQuickLink);
//                // accountSummaryPage.detailsStatementQuickLink.click();
//                waitTillVisibilityElement(driver, accountSummaryPage.accountStatementPageHeader);
//                try {
//                    softAssert.assertTrue(driver.getCurrentUrl().contains("statement"), "account statement page url not be the same");
//                    softAssert.assertTrue(accountSummaryPage.accountStatementPageHeader.isDisplayed(), "account statement page header not displayed");
//
//                } catch (NoSuchElementException e) {
//                    attachScreenshot(driver, scenario);
//                    softAssert.fail("Account statement header not displayed");
//                }
//                loanPage.backButton.click();
//                waitTillElementToBeClickable(driver, accountSummaryPage.moneyTransferQuickLink);
//                javaScriptExecutorClickElement(driver, accountSummaryPage.moneyTransferQuickLink);
//                waitTillInvisibilityOfLoader(driver);
//                try {
//                    softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "navigates to money transfer failed");
//                    softAssert.assertTrue(accountSummaryPage.moneyTransferPageHeader.isDisplayed(), "money transfer header not displayed");
//                    // attachScreenshot(driver,scenario);
//                } catch (NoSuchElementException e) {
//                    attachScreenshot(driver, scenario);
//                    softAssert.fail("Money Transfer header no displayed");
//                }
//                loanPage.backButton.click();
//                waitTillLoading(driver);
                loanPage.backButtonApplyLoanPage.click();
                waitTillLoading(driver);
                loanPage.carLoanButton.click();
                String newCarLoan = loanPage.carLoanNewCar.getText();
                loanPage.carLoanNewCar.click();
                waitTillInvisibilityOfLoader(driver);
                logger.info("Current loan page header is :" + loanPage.loanPageHeader.getText());
                softAssert.assertTrue(loanPage.backButtonApplyLoanPage.isDisplayed(), "back button not displayed");
                softAssert.assertTrue(loanPage.applyLoanPageHeader.getText().contains(newCarLoan), "" + newCarLoan + "page header not displayed");
                logger.info("User verified the " + loanPage.applyLoanPageHeader.getText() + " page");
//                /** Quick Link Section Verify**/
//
//                waitTillElementToBeClickable(driver, accountSummaryPage.detailsStatementQuickLink);
//                javaScriptExecutorClickElement(driver, accountSummaryPage.detailsStatementQuickLink);
//                // accountSummaryPage.detailsStatementQuickLink.click();
//                waitTillVisibilityElement(driver, accountSummaryPage.accountStatementPageHeader);
//                try {
//                    softAssert.assertTrue(driver.getCurrentUrl().contains("statement"), "account statement page url not be the same");
//                    softAssert.assertTrue(accountSummaryPage.accountStatementPageHeader.isDisplayed(), "account statement page header not displayed");
//
//                } catch (NoSuchElementException e) {
//                    attachScreenshot(driver, scenario);
//                    softAssert.fail("Account statement header not displayed");
//                }
//                loanPage.backButton.click();
//                waitTillElementToBeClickable(driver, accountSummaryPage.moneyTransferQuickLink);
//                javaScriptExecutorClickElement(driver, accountSummaryPage.moneyTransferQuickLink);
//                waitTillInvisibilityOfLoader(driver);
//                try {
//                    softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "navigates to money transfer failed");
//                    softAssert.assertTrue(accountSummaryPage.moneyTransferPageHeader.isDisplayed(), "money transfer header not displayed");
//                    // attachScreenshot(driver,scenario);
//                } catch (NoSuchElementException e) {
//                    attachScreenshot(driver, scenario);
//                    softAssert.fail("Money Transfer header no displayed");
////                }
//                loanPage.backButton.click();
//                waitTillInvisibilityOfLoader(driver);
                /*****/
                loanPage.backButtonApplyLoanPage.click();
                waitTillInvisibilityOfLoader(driver);
            } else {
                scrollIntoView(driver, clickButton);
                clickButton.click();
                waitTillInvisibilityOfLoader(driver);
                scrollIntoViewUp(driver, loanPage.pageHeader);
                staticWait(2000);
                logger.info("Current loan page header is :" + loanPage.loanPageHeader.getText());
                softAssert.assertTrue(loanPage.backButtonApplyLoanPage.isDisplayed(), "back button not displayed");
                softAssert.assertTrue(loanPage.applyLoanPageHeader.getText().contains(currentLoan), "" + currentLoan + "page header not displayed");
                logger.info("User verified the " + loanPage.applyLoanPageHeader.getText() + " page");
//                /***Quick Link Section Verify Each Loan Page ***/
//
//                waitTillElementToBeClickable(driver, accountSummaryPage.detailsStatementQuickLink);
//                javaScriptExecutorClickElement(driver, accountSummaryPage.detailsStatementQuickLink);
//                // accountSummaryPage.detailsStatementQuickLink.click();
//                waitTillVisibilityElement(driver, accountSummaryPage.accountStatementPageHeader);
//                try {
//                    softAssert.assertTrue(driver.getCurrentUrl().contains("statement"), "account statement page url not be the same");
//                    softAssert.assertTrue(accountSummaryPage.accountStatementPageHeader.isDisplayed(), "account statement page header not displayed");
//
//                } catch (NoSuchElementException e) {
//                    attachScreenshot(driver, scenario);
//                    softAssert.fail("Account statement header not displayed");
//                }
//                loanPage.backButton.click();
//                waitTillElementToBeClickable(driver, accountSummaryPage.moneyTransferQuickLink);
//                javaScriptExecutorClickElement(driver, accountSummaryPage.moneyTransferQuickLink);
//                waitTillInvisibilityOfLoader(driver);
//                try {
//                    softAssert.assertTrue(driver.getCurrentUrl().contains("moneytransfer"), "navigates to money transfer failed");
//                    softAssert.assertTrue(accountSummaryPage.moneyTransferPageHeader.isDisplayed(), "money transfer header not displayed");
//                    // attachScreenshot(driver,scenario);
//                } catch (NoSuchElementException e) {
//                    attachScreenshot(driver, scenario);
//                    softAssert.fail("Money Transfer header no displayed");
////                }
//                loanPage.backButton.click();
//                waitTillInvisibilityOfLoader(driver);


                /*** ***/
                loanPage.backButton.click();
                //loanPage.backButtonApplyLoanPage.click();
                waitTillInvisibilityOfLoader(driver);

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

    @And("User clicks back button navigates to loan home page")
    public void userClicksBackButtonNavigatesToLoanHomePage() {
        // loanPage.backButtonApplyLoanPage.click();
        /**Back button isssue**/
//        scrollIntoViewUp(driver, loanPage.backButton);
//        waitTillElementToBeClickable(driver, loanPage.backButton);
//        loanPage.backButton.click();
//        waitTillInvisibilityOfLoader(driver);
        clickOnButton(homePage.loanModuleButton);
        waitTillInvisibilityOfLoader(driver);

    }


    /**
     * Given below codes referred to Download function
     ****/


    @And("User verify amortisation statement download")
    public void userVerifyAmortisationStatementDownload() {
        softAssert = new SoftAssert();
        loanPage.pageDetailsAmortisation.click();
        try {
            waitTillVisibilityElement(driver, loanPage.pageDetailsAmortisationPDFDownload);
            loanPage.pageDetailsAmortisationPDFDownload.click();
            File amortizationPdfFile = new File("C:/Users/987993/Downloads/Amortization Statement.pdf");
            staticWait(5000);
            softAssert.assertTrue(amortizationPdfFile.exists(), "Amortisation Table Statement download failed");
            waitTillInvisibilityOfLoader(driver);
            if (amortizationPdfFile.exists()) {
                amortizationPdfFile.delete();
            }
        } catch (NoSuchElementException e) {
            logger.error("Amortization download button not displayed");
            softAssert.fail("Amortization download button not showing");

        }
    }


    @And("User clicks on back button and navigate to loan details page")
    public void userClicksOnBackButtonAndNavigateToLoanDetailsPage() {
        if (driver.getCurrentUrl().contains("pay-overdue")) {
            loanPage.backButton.click();
            waitTillInvisibilityOfLoader(driver);
        }
    }

    @When("User clicks on maximize button in loan transaction section")
    public void userClicksOnMaximizeButtonInLoanTransactionSection() {
        waitTillElementToBeClickable(driver, loanPage.loanStatementTableMaximize);
        loanPage.loanStatementTableMaximize.click();
        staticWait(2000);


    }

    @And("User verify the value date sorting function in loan transaction section")
    public void userVerifyTheValueDateSortingFunctionInLoanTransactionSection() {
        softAssert = new SoftAssert();
        loanPage.loanStatementValueDateSort.click();
        //    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd, MMM yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        List<Date> dateListAsc = new ArrayList<>();
        for (WebElement dateElementAsc : loanPage.loanStatementTransactionValueDate) {
            String dateElementsAsc = dateElementAsc.getText();
            try {
                Date dateTime = dateFormat.parse(dateElementsAsc);
                dateListAsc.add(dateTime);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < dateListAsc.size() - 1; i++) {
            Date currentDateAsc = dateListAsc.get(i);
            Date nextDateAsc = dateListAsc.get(i + 1);
            softAssert.assertTrue(currentDateAsc.before(nextDateAsc) || currentDateAsc.equals(nextDateAsc), "Transaction date not sorted in Ascending");
        }
        waitForPageLoad(driver);
        //Descending Sort

        loanPage.loanStatementValueDateSort.click();
        DateTimeFormatter dateFormatDsc = DateTimeFormatter.ofPattern("dd, MMM yyyy");
        List<Date> dateListDsc = new ArrayList<>();
        for (WebElement dateElementDsc : loanPage.loanStatementTransactionValueDate) {
            String dateElementsDsc = dateElementDsc.getText();
            try {
                Date dateTime = dateFormat.parse(dateElementsDsc);
                dateListAsc.add(dateTime);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < dateListDsc.size() - 1; i++) {
            Date currentDateDsc = dateListDsc.get(i);
            Date nextDateDsc = dateListDsc.get(i + 1);
            softAssert.assertTrue(currentDateDsc.after(nextDateDsc) || currentDateDsc.equals(nextDateDsc), "Value date not sorted in Descending");
        }
        try {
            softAssert.assertAll();
            // attachScreenshot(driver, scenario);
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());

        }

    }

    @And("User navigates to the apply loan home page")
    public void userNavigatesToTheApplyLoanHomePage() {
        /** Previous Steps its navigates to dash board page...So steps added to navigates **/
        if (driver.getCurrentUrl().contains("apply-for-loans")) {
            logger.info("Apply loan is failed so no need to navigates loan home page to apply loan page");
        } else {
            loanPage.loanPageLoanApplyButton.click();
            waitTillInvisibilityOfLoader(driver);
            logger.info("Navigates to apply loan page");
        }


    }

    @And("User select transaction period from duration filter")
    public void userSelectTransactionPeriodFromDurationFilter() {
        try {
            if (!loanPage.transactionTableHeader.isDisplayed()) {
                loanPage.durationSelect.click();
                loanPage.last6monthInDuration.click();
                waitTillInvisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException exception) {
        }
    }

}

