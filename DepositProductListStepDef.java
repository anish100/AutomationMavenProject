package stepDefs;

import io.cucumber.java.en.Then;
import pom.DepositsDashboardPage;

public class DepositProductListStepDef {
    DepositsDashboardPage depositsDashboardPage;

    @Then("User verify the select deposit account type page")
    public void userVerifyTheSelectDepositAccountTypePage(){
    depositsDashboardPage.fdDepositHeader.getText();
    }
}
