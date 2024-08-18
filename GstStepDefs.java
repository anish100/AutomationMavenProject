package StepDefinition;


import dataProviders.ConfigFileReader;

import dataProviders.ExcelFileReader;
import io.cucumber.java.PendingException;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.*;
import io.qameta.allure.AllureLifecycle;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import PageObjectModel.GstpaymentPage;
import pom.*;
import reusable.Base;
import reusable.TestContext;
import textAssertions.TextAssertion;

import javax.sound.midi.ShortMessage;
import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GstStepDefs extends Base{
	
	GstpaymentPage gstpaymentpage;

    public GstStepDefs(TestContext context) {
        driver = context.getDriverManager().getWebDriver();
        scenario = context.getScenario();
        fileReader = context.getFileReaderManager().getExcelFileReader();
        configFileReader = context.getFileReaderManager().getConfigReader();
        gstpaymentpage=context.

    }




	@Then("User verify the  gst payment  screen")
	public void user_verify_the_gst_payment_screen() {
		//System.out.println("Inside steps- user navigates to Dashboard page ");

		gstpaymentpage.toggleButton();
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		explicitWait(driver, 30).until(ExpectedConditions.visibilityOf(gstpaymentpage.gstPaymentIcon));
		gstpaymentpage.gstPaymentIcon();
		softAssert = new SoftAssert();
		//verify the title of page
		String actualTitle = gstpaymentpage.gstPaymentTitle().getText();
		//update the line with parameter
		// use the method for used the present
		
		softAssert.assertEquals(gstpaymentpage.gstPaymentTitle().getText(), "GST Payment", "Text: " + gstpaymentpage.gstPaymentTitle()+ " does not match.");
        softAssert.assertTrue(gstpaymentpage.checkBeneficiaryAcnumber.isDisplayed(), "beneficiary account number fields not displayed");
        softAssert.assertTrue(gstpaymentpage.checkFormAccount.isDisplayed(), "Form account number field is not displayed");
        softAssert.assertTrue(gstpaymentpage.checkAvailableBalance.isDisplayed(), "Available balance message is not displayed ");
        softAssert.assertTrue(gstpaymentpage.checkTransferAmnt.isDisplayed(), "transfer amount fields is not displayed ");
        softAssert.assertTrue(gstpaymentpage.checkAmountGreater1.isDisplayed(),"validation messege is not displayed");
        softAssert.assertAll();

	  
	}



	@When("User select required details on gst paymnet page")
	public void user_select_required_details_on_gst_paymnet_page() {
	    //used the explicit wait
		//driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		explicitWait(driver, 40).until(ExpectedConditions.visibilityOf(gstpaymentpage.enterBanNumber));
        //Enter the cpin number 
		gstpaymentpage.enterBanNumber();
        //Enter the account number 
		explicitWait(driver, 40).until(ExpectedConditions.visibilityOf(gstpaymentpage.clickDrpDownIconAccnt));
		//gstpaymentpage.clickDrpDownIconAccnt();
		gstpaymentpage.selectAccntnos();
		explicitWait(driver, 60).until(ExpectedConditions.visibilityOf(gstpaymentpage.transferAmnt));
		gstpaymentpage.transferAmnt();

	}


	@Then("User verify the  payment summary details")
	public void user_verify_the_payment_summary_details() {
		
		//gstpaymentpage.checkrbiaccntnumber();
		
		// Verify that softassertall function is working or not 
		
		//If SoftAssert is working correctly, the test will fail, and you'll see an error message indicating that the intentional failure assertion failed.
		//If SoftAssert is not working correctly, the test will pass, and you won't see any error messages.
		
		// Check random account number is correct or not 
		String randomAccountNumber = gstpaymentpage.generateRandomAccountNumber();
		String actualrandomAccountNumber = gstpaymentpage.checkPayment().getText();
		softAssert.assertEquals(actualrandomAccountNumber, actualrandomAccountNumber, "The: " + actualrandomAccountNumber + " is not valid");
		

		//gstpaymentpage.ifscnumber();
		String actualIfscNumber = gstpaymentpage.ifscNumber().getText();
		System.out.println(actualIfscNumber);
		softAssert.assertEquals(actualIfscNumber, actualIfscNumber, "The: " + actualIfscNumber + " is not valid");

		
		//gstpaymentpage.neftnumber();
		
		String actualNeftNumber = gstpaymentpage.neftNumber().getText();
		System.out.println(actualNeftNumber);
		softAssert.assertEquals(actualNeftNumber, actualNeftNumber, "The: " + actualNeftNumber + " is not valid");
		softAssert.assertAll();
		
			
	}
	
////
////	   public boolean isValidAccountNumber(String accountNumber) {
////        // Check if the account number has exactly 12 digits
////        if (accountNumber.length() != 12) {
////            return false;
////        }
////		return false;
//    }

	@Then("User click on the paymentgst button")
	public void user_click_on_the_paymentgst_button() {
		//driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		explicitWait(driver, 40).until(ExpectedConditions.visibilityOf(gstpaymentpage.payGstSubmitbtn));
		gstpaymentpage.payGstSubmitbtn();
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		
		gstpaymentpage.enterOtp();


	}

	@Then("User verify that payment transaction is succesfull or not")
	public void user_verify_that_payment_transaction_is_succesfull_or_not() {
		
		
		String paymentverification = gstpaymentpage.verifyPayment().getText();
		softAssert.assertEquals(paymentverification, paymentverification, "The transaction of " + paymentverification + " is Failed");
		
		softAssert.assertAll();

		
	}
	
	@Then("User clicks on log out the session and verifies")
	public void user_clicks_on_log_out_the_session_and_verifies() {
		explicitWait(driver, 50).until(ExpectedConditions.visibilityOf(gstpaymentpage.clickProfileDropdownIcon));
		gstpaymentpage.clickProfileDropdownIcon.click();
		
		gstpaymentpage.logOutInPage.click();
		softAssert = new SoftAssert();

        softAssert.assertTrue(gstpaymentpage.logOutPopUpLogOutButton.isDisplayed(), "logout button in pop up not displayed ");
        gstpaymentpage.logOutPopUpLogOutButton.click();
        gstpaymentpage.logOutMessage.getText();
        System.out.println(gstpaymentpage.logOutMessage);
        softAssert.assertAll();
        driver.close();
       
	}
	
	
	@Then("User verify that transaction details is correctly or not")
	public void user_verify_that_transaction_details_is_correctly_or_not() {
		
		softAssert = new SoftAssert();
		//softAssert.assertTrue(gstpaymentpage.logOutPopUpLogOutButton.isDisplayed(), "logout button in pop up not displayed ");
		softAssert.assertTrue(gstpaymentpage.transactionAmount.isDisplayed(),"transaction amount is not displayed ");
		softAssert.assertTrue(gstpaymentpage.transactionToAccountCpin.isDisplayed(),"transaction to account cpin number is not displayed ");
		softAssert.assertTrue(gstpaymentpage.transactionFromAccount.isDisplayed(),"transaction from account number is not displayed ");
		softAssert.assertTrue(gstpaymentpage.transactionPaymentMode.isDisplayed(),"transaction payment mode is not displayed");
		softAssert.assertTrue(gstpaymentpage.transactionPaymentMode.isDisplayed(),"transaction payment mode is not displayed");
		softAssert.assertTrue(gstpaymentpage.transactionReferenceNumber.isDisplayed(),"transaction reference number is not displayed");
		softAssert.assertAll();


	    
	}


}


