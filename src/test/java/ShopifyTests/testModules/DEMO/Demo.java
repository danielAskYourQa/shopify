package ShopifyTests.testModules.DEMO;

import static utils.DriverUtilities.shopify;

import driverFactory.WebBasePage;
import driverFactory.WebDriverSetup;
import ShopifyTests.dataProviders.demoDataProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.RetryAnalyzer;


@Listeners({RetryAnalyzer.class})
public class Demo extends WebBasePage implements ITest {

  @Test(description = "Testing discounts of various products from various pages", dataProvider = "procentage", dataProviderClass = demoDataProvider.class)
  public void testDiscountProcentage(String testName, String url, String itemName, Double discountProcentageData)
      throws InterruptedException, IOException {
    driver.get(url);
    WebElement shopItem = shopify.getDesiredProduct(itemName);
    WebElement originalPriceObj = shopify.getOriginalPriceObj(shopItem);
    Double originalPrice = shopify.returnAllDoubles(originalPriceObj.getText()).get(0);
    Double reducedPrice = shopify.getReducedPriceValue(shopItem, originalPriceObj.getText());
    Double discountProcentage = shopify.calculateProcentage(originalPrice, reducedPrice);
    Assert.assertEquals(Math.round(100 * discountProcentage), Math.round(100 * discountProcentageData));
  }

  @Test(description = "Testing procentage discount", dataProvider = "checkoutItems", dataProviderClass = demoDataProvider.class)
  public void testCheckout(String testName, String url, String itemName, Double discountProcentageData)
      throws InterruptedException, IOException {
    driver.get(url);
    Thread.sleep(3000);
    if (url.contains("uppercasemagazine")) {
      shopify.closePopUpsNonJava();
    }
    Thread.sleep(2000);
    Double originalPrice = shopify.returnAllDoubles(shopify.getDesiredProduct(itemName).getText()).get(0);
    shopify.getDesiredProduct(itemName).click();
    if (url.contains("manitobah")) {
      Thread.sleep(2000);
      shopify.acceptCookies();
    }
    shopify.pressAddToCart();
    Thread.sleep(2000);
    shopify.increaseAmount(url,"2");
    if (url.contains("uppercasemagazine")) {
      shopify.updateCart();
    }
    Thread.sleep(3000);
    shopify.clickCheckOut();
    Thread.sleep(2000);
    shopify.validatePriceIsCalculatedCorrectly(url, originalPrice, true);
  }


  @BeforeMethod(alwaysRun = true)
  public void setTestName(Method method, Object[] row) {
    testName.set(row[0] + "-----------------------------------------------");
  }

  @Override
  public String getTestName() {
    return testName.get();
  }

  @AfterMethod
  public void afterMethod() {
    WebDriverSetup.cleanupDriver(driver);
    File file = new File(driverUtilities.pathNewFolder);
    WebDriverSetup.cleanupDriver(driver);
  }

  @AfterClass
  public void afterClass() {
    File file = new File(driverUtilities.relativePath);
    driverUtilities.deleteDirectory(file);
  }
}
