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
import org.testng.asserts.SoftAssert;
import pom.*;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CollectionsStepDef extends Base {
    private static final Logger logger = LogManager.getLogger(CollectionsStepDef.class);

    CollectionsPage collectionsPage;
    DashboardPage dashboardPage;
    AccountStatementPage accountStatementPage;
    WebDriver driver;
    Scenario scenario;
    ExcelFileReader fileReader;
    ConfigFileReader configFileReader;
    SoftAssert softAssert;
    List<Date> actualDateList;
    SimpleDateFormat dateFormat;
    List<Double> actualAmountList;
    String searchByName;
    String searchByStatus;

    public CollectionsStepDef(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        collectionsPage = context.getPageObjectManager().getCollectionsPage();
        accountStatementPage = context.getPageObjectManager().getAccountStatementPage();
        dashboardPage = context.getPageObjectManager().getDashboardPage();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
    }

    @And("User verify the QR code details on store dashboard page")
    public void userVerifyTheQRCodeDetailsOnStoreDashboardPage() {
        softAssert = new SoftAssert();
        File qrImage = collectionsPage.qrCode.getScreenshotAs(OutputType.FILE);

        try {
            String qrCodeTextDefault = qrCodeScanner(qrImage);
            String extractVPAIdDefault[] = qrCodeTextDefault.split("=");
            String qrCodeVAPTextExtract = extractVPAIdDefault[1].trim();
            String extractVpaIdArr[]=qrCodeVAPTextExtract.split("&");
            String qrCodeVAPTextDefault=extractVpaIdArr[0].trim();
            softAssert.assertEquals(qrCodeVAPTextDefault, collectionsPage.vpaTAbBelowQrCode.getText().trim(), "vpa id not matched with displayed value");
            collectionsPage.vpaDropDownBelowQrCode.click();
            softAssert.assertTrue(!collectionsPage.vpaListsOnDropDownBelowQrCode.isEmpty(), "vpa lists in drop down is empty");
            collectionsPage.selectVPAByIndex.click();
            collectionsPage.copyButton.click();
            waitTillVisibilityElement(driver, collectionsPage.successfullyText);
            softAssert.assertTrue(collectionsPage.successfullyText.isDisplayed(), "copied successfully message is not displayed");
            String copiedContent = getCopiedValue().trim();
            softAssert.assertTrue(copiedContent.matches(collectionsPage.vpaTAbBelowQrCode.getText().trim()), "copied VPA different from QR code text");
            logger.info("Copied Value " + copiedContent);
        } catch (RuntimeException e) {
            logger.error("QR code is not readable format");
            //  softAssert.fail("QR code is not readable format");
            // As per developer said this is nt real data qr so,removed on 4/4/2024

        }
        logger.info("VPA ID" + collectionsPage.vpaTAbBelowQrCode.getText());
        setVPAId(collectionsPage.vpaTAbBelowQrCode.getText());

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks on download QR button")
    public void userClicksOnDownloadQRButton() {
        collectionsPage.downloadButton.click();
        staticWait(2000);
    }

    @Then("User verify the downloaded QR details")
    public void userVerifyTheDownloadedQRDetails() {
        softAssert = new SoftAssert();
        File qrImage = collectionsPage.qrCode.getScreenshotAs(OutputType.FILE);
        try {
            String qrCodeText = qrCodeScanner(qrImage);
            String extractVPAId[] = qrCodeText.split("=");
            String qrCodeVAPText = extractVPAId[2].trim();
            logger.info("QR CODE TEXT " + qrCodeVAPText);
            logger.info("VPA ID " + collectionsPage.vpaTAbBelowQrCode.getText());
            setVPAId(collectionsPage.vpaTAbBelowQrCode.getText().trim());
            File downloadedQR = new File("C:/Users/987993/Downloads/qr_code.png");
            softAssert.assertTrue(downloadedQR.exists(), "QR image not downloaded");
            waitTillInvisibilityOfLoader(driver);
            String qrText = qrCodeScanner(downloadedQR);
            logger.info("Downloaded QR text " + qrText);
            softAssert.assertTrue(qrText.contains(qrCodeText), "QR code information not be the same");
            if (downloadedQR.exists()) {
                downloadedQR.delete();
            }
        } catch (RuntimeException e) {
            logger.error("QR code is not readable format");
            //   softAssert.fail("QR code is not readable format");

        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @When("User clicks on generate qr button")
    public void userClicksOnGenerateQrButton() {
        // Enter the Amount and Remarks
        scrollIntoView(driver, collectionsPage.dynamicQrEnterAmount);
        waitTillElementToBeClickable(driver, collectionsPage.dynamicQrEnterAmount);
        collectionsPage.dynamicQrEnterAmount.sendKeys(fileReader.collectionsTestData.get("dynamicQRAmount"));
        collectionsPage.dynamicQrRemarks.sendKeys("Purchasing the car");
        collectionsPage.generateQRButton.click();
        waitForPageLoad(driver);
    }

    @Then("User verify the dynamic QR pop up")
    public void userVerifyTheDynamicQRPopUp() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, collectionsPage.dynamicQRPopupVpaId);
        softAssert.assertTrue(collectionsPage.dynamicQRPopupVpaId.isDisplayed(), "VPA details not visible in dynamic QR Popup");
        softAssert.assertTrue(collectionsPage.dynamicQROnPopup.isDisplayed(), "dynamic QR is not displayed in pop up");
        softAssert.assertTrue(collectionsPage.dynamicQRDetailsOnPopup.isDisplayed(), "dynamic QR details not displayed");
        softAssert.assertTrue(collectionsPage.cancelButton.isDisplayed(), "dynamic QR pop up cancel button is not displayed");
        softAssert.assertTrue(collectionsPage.dynamicQRDownloadButton.isDisplayed(), "dynamic QR pop up download button is not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @And("User verify the generated dynamic QR details")
    public void userVerifyTheGeneratedDynamicQRDetails() {
        softAssert = new SoftAssert();
        File dynamicQRPath = null;
        File dynamicQR = collectionsPage.dynamicQROnPopup.getScreenshotAs(OutputType.FILE);
        collectionsPage.dynamicQRDownloadButton.click();
        staticWait(2000);
        dynamicQRPath = new File("C:/Users/987993/Downloads/QR_Code.png");
        try {
            String dynamicQRText = qrCodeScanner(dynamicQR);
            logger.info("Dynamic QR Text " + dynamicQRText);
            softAssert.assertTrue(dynamicQRPath.exists(), "QR image not downloaded");
            waitTillInvisibilityOfLoader(driver);
            String downloadedDynamicQRText = qrCodeScanner(dynamicQRPath);
            logger.info("Downloaded QR text " + downloadedDynamicQRText);
            softAssert.assertTrue(downloadedDynamicQRText.contains(dynamicQRText), "dynamic QR not same");
        } catch (RuntimeException e) {
            //     softAssert.fail("QR code is not readable format");
        }
        collectionsPage.cancelButton.click();
        if (dynamicQRPath.exists()) {
            dynamicQRPath.delete();
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @And("User clicks on collections on store dashboard page")
    public void userClicksOnCollectionsOnStoreDashboardPage() {
        collectionsPage.collectionsMenu.click();
        waitTillInvisibilityOfLoader(driver);
    }


    @And("User verify collection container on store dashboard page")
    public void userVerifyCollectionContainerOnStoreDashboardPage() {
        softAssert = new SoftAssert();
        softAssert.assertTrue(collectionsPage.vpaTab.getText().contains("All VPAs"), "all vpa text not displayed");
        collectionsPage.vpaDropDown.click();
        collectionsPage.selectVPAOnCollectionContainerIndex.click();
        softAssert.assertTrue(!collectionsPage.vpaTab.getText().isEmpty(), "VPA tab id is not displayed");
        softAssert.assertTrue(collectionsPage.receivedAmountOnCollectionContainerIndex.getText().matches(".*\\d.*"), "received amount not displayed");
        softAssert.assertTrue(collectionsPage.noOfPaymentOnCollectionContainer.getText().matches(".*\\d.*"), "no of payments not displayed");

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }

    }

    @Then("User verify the QR transaction page")
    public void userVerifyTheQRTransactionPage() {
        softAssert = new SoftAssert();
        scrollIntoViewUp(driver, collectionsPage.pageHeader);
        softAssert.assertTrue(driver.getCurrentUrl().contains(TextAssertion.collectionsPageUrlPath), "collections page url not matched");
        softAssert.assertTrue(collectionsPage.pageHeader.getText().contains(TextAssertion.collectionsPageHeader), "collection page header name not matched");
        logger.info(collectionsPage.pageHeader.getText());
        softAssert.assertTrue(collectionsPage.backButton.isDisplayed(), "back button not displayed");
        softAssert.assertTrue(collectionsPage.selectVPATabQRTransactionPage.isDisplayed(), "select vpa tab not displayed");
      //  softAssert.assertTrue(collectionsPage.searchTabQRTransactionPage.isDisplayed(), "search tab not displayed");
      //  softAssert.assertTrue(collectionsPage.qrTransactionPageCalendarFilter.isDisplayed(), "calendar filter is not displayed");
      //  softAssert.assertTrue(collectionsPage.qrTransactionPageAmountFilter.isDisplayed(), "amount filter is not displayed");
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User select the vpa id from drop down")
    public void userSelectTheVpaIdFromDropDown() {
        collectionsPage.selectVPATabDropDownQRTransactionPage.click();
        collectionsPage.selectVPAFromDropDown(getVPAId()).click();
        waitTillInvisibilityOfLoader(driver);
    }

    @And("User apply the calendar filter on QR transaction page")
    public void userApplyTheCalendarFilterOnQRTransactionPage() {
        scrollIntoViewUp(driver, collectionsPage.pageHeader);
        collectionsPage.qrTransactionPageCalendarFilter.click();
        collectionsPage.durationSelect("Last 6 Months");
        String fromDateSelect = fileReader.collectionsTestData.get("fromDate");
        String toSelect = fileReader.collectionsTestData.get("toDate");

//From Date Extract
        String[] splitDate = fromDateSelect.split("/");
        String fromDate = splitDate[0];
        String fromMonth = String.valueOf(Integer.valueOf(splitDate[1]) - 1);
        String fromYear = splitDate[2];
        waitTillVisibilityElement(driver, accountStatementPage.yearClick);
        accountStatementPage.yearClick.click();
        selectDDByValue(accountStatementPage.yearSelect, fromYear);
        accountStatementPage.monthClick.click();
        selectDDByValue(accountStatementPage.monthSelect, fromMonth);
        accountStatementPage.getDateFromDD(fromDate).click();

//To date Extract
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
        waitTillInvisibilityOfLoader(driver);
    }

    @Then("User verify the transaction section based on calendar filter")
    public void userVerifyTheTransactionSectionBasedOnCalendarFilter() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No results Found In Table Section");
        } else {
            softAssert = new SoftAssert();
            String dateSplit = accountStatementPage.dateFilterText.getText();
            String[] fromDate = dateSplit.split("-");
            String startDate = fromDate[0];
            String endDate = fromDate[1];
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

            Date dateToValidate = null;
            Date starts;
            Date end;
            try {
                starts = sdf.parse(startDate);
                end = sdf.parse(endDate);
                for (WebElement transactionLists : collectionsPage.transactionList) {
                    dateToValidate = sdf.parse(transactionLists.getText());
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
            softAssert.assertTrue(dateToValidate.compareTo(starts) >= 0 && dateToValidate.compareTo(end) <= 0, "Date is lie between int the filter date");
            waitTillInvisibilityOfLoader(driver);

            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        }
    }

    @When("User clicks on transaction date sorting button")
    public void userClicksOnTransactionDateSortingButton() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            waitTillElementToBeClickable(driver, accountStatementPage.transactionDateSort);
            dateFormat = new SimpleDateFormat("dd MMM, yyyy");
            actualDateList = new ArrayList<>();
            /*******Sorting By Collections method *******/
            for (WebElement element : accountStatementPage.transactionTimeList) {
                String dateElements = element.getText().replaceAll("\n", " ");
                try {
                    Date dateTime = dateFormat.parse(dateElements);
                    actualDateList.add(dateTime);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
            scrollIntoView(driver, accountStatementPage.transactionDateSort);
            accountStatementPage.transactionDateSort.click();
            waitForPageLoad(driver);

        }
    }

    @Then("User validate the transaction date ascending sorting function")
    public void userValidateTheTransactionDateAscendingSortingFunction() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            /****Note : 19_03_2024 sorting date functions considered only date after discussion ***/
            softAssert = new SoftAssert();
            List<Date> afterAscendingdateList = new ArrayList<>();
            /*******Sorting By Collections method *******/
            for (WebElement element : collectionsPage.transactionDateList) {
                String dateElements = element.getText().replaceAll("\n", " ");
                try {
                    Date dateTime = dateFormat.parse(dateElements);
                    afterAscendingdateList.add(dateTime);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            Collections.sort(actualDateList);
            softAssert.assertEquals(afterAscendingdateList, actualDateList);
            logger.info(actualDateList);
            logger.info(afterAscendingdateList);
            waitTillInvisibilityOfLoader(driver);
            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                logger.error("Ascending list Expect " + actualDateList + "Actual List " + afterAscendingdateList);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        }
    }

    @Then("User validate the transaction date descending sorting function")
    public void userValidateTheTransactionDateDescendingSortingFunction() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            softAssert = new SoftAssert();
            List<Date> dateListDsc = new ArrayList<>();
            if (collectionsPage.transactionDateList.size() > 1) {
                for (WebElement elementDsc : collectionsPage.transactionDateList) {
                    String dateElements = elementDsc.getText().replaceAll("\n", " ");
                    try {
                        Date dateTime = dateFormat.parse(dateElements);
                        dateListDsc.add(dateTime);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                Collections.sort(actualDateList, Collections.reverseOrder());
                softAssert.assertEquals(dateListDsc, actualDateList, "descending sorting not matched");
                logger.info(actualDateList);
                try {
                    softAssert.assertAll();
                } catch (AssertionError e) {
                    attachScreenshot(driver, scenario);
                    logger.error("Ascending list Expect " + actualDateList + "Actual List " + dateListDsc);
                    scenario.log(e.toString());
                    setErrorsInList(e.toString());
                }
            }
        }
    }

    @When("User clicks on amount sorting button")
    public void userClicksOnAmountSortingButton() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            actualAmountList = new ArrayList<>();
            for (WebElement amountList : collectionsPage.amountList) {
                double amountListDetails = Double.parseDouble(amountList.getText().replaceAll("[^\\d.]", "").replaceAll("\\n", "").trim());
                actualAmountList.add(amountListDetails);
            }
            scrollIntoViewUp(driver, accountStatementPage.amountSort);
            accountStatementPage.amountSort.click();
            waitTillInvisibilityOfLoader(driver);
        }
    }

    @Then("User validate the amount ascending sorting function")
    public void userValidateTheAmountAscendingSortingFunction() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            softAssert = new SoftAssert();
            List<Double> ascendingAmountList = new ArrayList<>();
            for (WebElement amountList : collectionsPage.amountList) {
                double amountListDetails = Double.parseDouble(amountList.getText().replaceAll("[^\\d.]", "").replaceAll("\\n", "").trim());
                ascendingAmountList.add(amountListDetails);
            }
            Collections.sort(actualAmountList);
            softAssert.assertEquals(ascendingAmountList, actualAmountList, "amount list are not in ascending order");
            logger.info("Ascending Order Amount " + actualAmountList + "Actual " + ascendingAmountList);

            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        }
    }


    @Then("User validate the amount descending sorting function")
    public void userValidateTheAmountDescendingSortingFunction() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {

            softAssert = new SoftAssert();
            List<Double> amountListNumDsc = new ArrayList<>();
            for (WebElement amountListDsc : collectionsPage.amountList) {
                double amountListDetailsDsc = Double.parseDouble(amountListDsc.getText().replaceAll("[^\\d.]", "").replaceAll("\\n", "").trim());
                amountListNumDsc.add(amountListDetailsDsc);
            }
            Collections.sort(actualAmountList, Collections.reverseOrder());
            softAssert.assertEquals(amountListNumDsc, actualAmountList, "amount sorted descending failed");
            logger.info("Expected List " + actualAmountList + "Actual List " + amountListNumDsc);

            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
        }
    }

    @When("User clicks on amount filter on QR transaction page")
    public void userClicksOnAmountFilterOnQRTransactionPage() {
        waitTillElementToBeClickable(driver, collectionsPage.qrTransactionPageAmountFilter);
        collectionsPage.qrTransactionPageAmountFilter.click();
        waitTillInvisibilityOfLoader(driver);
        accountStatementPage.amountRangeFrom.sendKeys(fileReader.collectionsTestData.get("amountFrom"));
        accountStatementPage.amountRangeTo.click();
        accountStatementPage.amountRangeTo.sendKeys(fileReader.collectionsTestData.get("amountTo"));
        waitForPageLoad(driver);
        accountStatementPage.applyButton.click();
        waitTillInvisibilityOfLoader(driver);

    }

    @Then("User verify the transaction section based on amount filter")
    public void userVerifyTheTransactionSectionBasedOnAmountFilter() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            softAssert = new SoftAssert();
            for (WebElement amountTransactionList : collectionsPage.amountList) {
                String amountsLists = amountTransactionList.getText().replaceAll("[^\\d.]", "").trim();
                double amountNum = Double.parseDouble(amountsLists);
                String rangeFrom = String.valueOf(fileReader.collectionsTestData.get("amountFrom"));
                Double rangeFromNum = Double.parseDouble(rangeFrom);
                String rangeTo = String.valueOf(fileReader.collectionsTestData.get("amountTo"));
                Double rangeToNum = Double.parseDouble(rangeTo);
                softAssert.assertTrue(amountNum >= rangeFromNum && amountNum <= rangeToNum, "Amount in the  statement transaction not sort by filter ");
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

    @And("User remove the applied filters")
    public void userRemoveTheAppliedFilters() {
        scrollIntoViewUp(driver, collectionsPage.pageHeader);
        try {
            if (collectionsPage.qrTransactionPageCalendarFilterCloseButton.isDisplayed()) {
                collectionsPage.qrTransactionPageCalendarFilterCloseButton.click();
                waitTillInvisibilityOfLoader(driver);
            }
            if (accountStatementPage.rangeFilterRemove.isDisplayed()) {
                accountStatementPage.rangeFilterRemove.click();
                waitTillInvisibilityOfLoader(driver);
            }
        } catch (NoSuchElementException exception) {
        }
    }

    @And("User verify the store dashboard page screen when QR code available")
    public void userVerifyTheStoreDashboardPageScreenWhenQRCodeAvailable() {
        softAssert = new SoftAssert();
        try {
            softAssert.assertTrue(collectionsPage.qrCode.isDisplayed(), "qr code not displayed");
            softAssert.assertTrue(collectionsPage.vpaDropDown.isDisplayed(), "vpa bat is not displayed");
            softAssert.assertTrue(collectionsPage.copyButton.isDisplayed(), "qr code copy button not displayed");
            softAssert.assertTrue(collectionsPage.downloadButton.isDisplayed(), "qr download button is not displayed");
            softAssert.assertTrue(collectionsPage.dynamicQrEnterAmount.isDisplayed(), "enter amount tab for dynamic qr code is not displayed");
            softAssert.assertTrue(collectionsPage.dynamicQrRemarks.isDisplayed(), "remarks tab for dynamic qr is not displayed");
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());

        }
    }

    @When("User clicks on download statement")
    public void userClicksOnDownloadStatement() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            collectionsPage.downloadStatementButton.click();
            staticWait(3000);
        }
    }

    @Then("User verify the downloaded statement file")
    public void userVerifyTheDownloadedStatementFile() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            softAssert = new SoftAssert();
            File downloadedStatement = new File("C:/Users/987993/Downloads/Qr-collection-statement.csv");
            softAssert.assertTrue(downloadedStatement.exists(), "statement downloaded failed");
            waitTillInvisibilityOfLoader(driver);
            if (downloadedStatement.exists()) {
                downloadedStatement.delete();
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

    @When("User enters the {string} in the search box")
    public void userEntersTheInTheSearchBox(String searchType) {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
             searchByName=collectionsPage.customerNameOnFirstRow.getText().trim();
            searchByStatus="Settled";
                    switch (searchType) {
                case "customer name":
                    collectionsPage.searchTabQRTransactionPage.sendKeys(Keys.chord(Keys.CONTROL, "a"),searchByName);
                case "status":
                    collectionsPage.searchTabQRTransactionPage.sendKeys(Keys.chord(Keys.CONTROL, "a"), searchByStatus);
            }
            staticWait(3000);
        }
    }

    @And("User verifies the transaction section by {string}")
    public void userVerifiesTheTransactionSectionBy(String searchType) {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            switch (searchType) {
                case "customer name":
                    ListIterator<WebElement> customerNameList = collectionsPage.customerNameListOnTransactionSection.listIterator();
                    while (customerNameList.hasNext()) {
                        WebElement customerNameElement = customerNameList.next();
                        String customerName = customerNameElement.getText();
                        softAssert.assertTrue(searchByName.contains(customerName.trim()), "status section not in searching order");
                        logger.info(searchByName + "|" + customerName.trim());
                    }
                    break;
                case "status":
                    ListIterator<WebElement> statusList = collectionsPage.statusListOnTransactionSection.listIterator();
                    while (statusList.hasNext()) {
                        WebElement statusListOnTransaction = statusList.next();
                        String statusText = statusListOnTransaction.getText();
                        softAssert.assertTrue(searchByStatus.contains(statusText.trim()), "status not in search order");
                        logger.info(searchByStatus + "|" + statusText.trim());
                    }
                    break;
            }
            try {
                softAssert.assertAll();
            } catch (AssertionError e) {
                attachScreenshot(driver, scenario);
                scenario.log(e.toString());
                setErrorsInList(e.toString());
            }
            collectionsPage.searchTabQRTransactionPage.clear();
        }
    }


    @When("User clicks on any one transaction on transaction section")
    public void userClicksOnAnyOneTransactionOnTransactionSection() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table");
        } else {
            waitTillVisibilityElement(driver, collectionsPage.firstRowTransactionSection);
            clickOnButton(collectionsPage.firstRowTransactionSection);

        }
    }

    @Then("User verify the transaction pop up on transaction section")
    public void userVerifyTheTransactionPopUpOnTransactionSection() {
        softAssert = new SoftAssert();
        boolean isCollectionPagePopUpDisplayed = false;
        try {
            collectionsPage.collectionPagePopUp.isDisplayed();
            isCollectionPagePopUpDisplayed = true;
            waitTillVisibilityElement(driver, collectionsPage.collectionPagePopUp);
        } catch (NoSuchElementException popUpNotDisplayed) {
            softAssert.fail("Collection page transaction section pop up not displayed");
        }
        if (isCollectionPagePopUpDisplayed == true) {
            softAssert.assertTrue(collectionsPage.collectionPagePopUpAmount.getText().trim().contains(collectionsPage.transactionAmountOnFirstRow.getText().trim()), "transaction amount on pop up  not be the same");
            String dateTimeMatch = "on " + collectionsPage.transactionDateOnFirstRow.getText() + " at " + collectionsPage.transactionDateOnFirstRow.getText();
            logger.info(dateTimeMatch);
            // Date extraction
            String dateTimePopUpText = collectionsPage.collectionPagePopUpDateTime.getText();
            SimpleDateFormat formatter = new SimpleDateFormat("'on' dd MMM, yyyy 'at' hh:mm a");
            Date dateAndTime;
            try {
                dateAndTime = formatter.parse(dateTimePopUpText);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            String extractedDate = dateFormat.format(dateAndTime);
            String extractedTime = timeFormat.format(dateAndTime);
            logger.info("Extracted Date " + extractedDate);
            logger.info("Extracted Time " + extractedTime);
            softAssert.assertTrue(extractedDate.contains(collectionsPage.transactionDateOnFirstRow.getText().trim()), "transaction date on pop up not be the same");
            softAssert.assertTrue(extractedTime.contains(collectionsPage.transactionTimeOnFirstRow.getText().trim()), "transaction time on pop up not be the same");
            softAssert.assertTrue(!collectionsPage.collectionPagePopUpTo.getText().isEmpty(), "to details on pop up not displayed");
            softAssert.assertTrue(collectionsPage.collectionPagePopUpToVpaId.getText().contains(getVPAId()), "receiver vpa details not be the same");
            softAssert.assertTrue(!collectionsPage.collectionPagePopUpFrom.getText().isEmpty(), "from details on pop up not be the same");
            softAssert.assertTrue(!collectionsPage.collectionPagePopUpFromVpaId.getText().isEmpty(), "sender details vpa on pop up not displayed");
            softAssert.assertTrue(!collectionsPage.collectionPagePopUpMobileNumber.getText().isEmpty(), "mobile number on pop up not displayed");
            softAssert.assertTrue(!collectionsPage.collectionPagePopUpReferenceNumber.getText().isEmpty(), "reference number not displayed");
            collectionsPage.collectionPagePopUpCopyButton.click();
            softAssert.assertTrue(collectionsPage.toastMessage.getText().contains("opied"), "copied successfully message not displayed");
            softAssert.assertTrue(collectionsPage.collectionPagePopUpReferenceNumber.getText().trim().contains(getCopiedValue().trim()), "copied value not be the same");
        }
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            attachScreenshot(driver, scenario);
            scenario.log(e.toString());
            setErrorsInList(e.toString());
        }
    }

    @When("User clicks on download button collection page pop up")
    public void userClicksOnDownloadButtonCollectionPagePopUp() {
        waitTillElementToBeClickable(driver, collectionsPage.downloadButton);
        clickOnButton(collectionsPage.downloadButton);
    }

    @When("there are no transactions, the user chooses a period for transactions")
    public void thereAreNoTransactionsTheUserChoosesAPeriodForTransactions() {
        if (collectionsPage.tableSectionVerify.getText().contains("No Result Found")) {
            logger.info("No transactions available in the table ,So choose last week from duration");
            collectionsPage.qrTransactionPageCalendarFilter.click();
            waitTillVisibilityElement(driver, collectionsPage.calendarScreen);
            collectionsPage.durationSelect("Last 6 Months");
            collectionsPage.applyButton.click();
            waitTillInvisibilityOfLoader(driver);
        }
    }
}

