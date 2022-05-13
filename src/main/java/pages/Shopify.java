package pages;

import static org.testng.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverUtilities;

public class Shopify extends DriverUtilities {

  protected final By homeButton = By.cssSelector("a[data-test-nav='home']");
  protected final By itemContainer = By.className("grid-product__content");
  protected final By originalPrice = By.className("grid-product__price--original");
  protected final By cartIcon = By.className("icon-cart");


  public void setDriver(WebDriver driver) {
    this.driver = driver;
  }

  public void setExplicitWait(WebDriverWait explicitWait) {
    this.explicitWait = explicitWait;
  }

  public WebElement getDesiredProduct(String productName) {
    List<WebElement> allProducts = driver.findElements(itemContainer);
    for (WebElement element : allProducts) {
      if (element.getText().contains(productName)) {
        return element;
      }
    }
    return null;
  }

  public WebElement getOriginalPriceObj(WebElement container) {
    return container.findElement(originalPrice);
  }

  public Double getReducedPriceValue(WebElement container, String originalPrice) {
    Boolean foundElement = false;
    double nonDiscountPrice = Double.parseDouble(originalPrice.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
    for (WebElement webElement : container.findElements(By.tagName("span"))) {
      if (webElement.getText().matches(".*\\d.*")) {
        double discountPrice = Double.valueOf(webElement.getText().replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
        if (discountPrice < nonDiscountPrice) {
          foundElement = true;
          return discountPrice;
        }
      }
    }
    if (foundElement.equals(false)) {
      ArrayList<Double> doubles = returnAllDoubles(container.getText());
      for (Double price : returnAllDoubles(container.getText())) {
        if (price < nonDiscountPrice) {
          return price;
        }
      }

    }
    return null;
  }

  public ArrayList<Double> returnAllDoubles(String input) {
    ArrayList<Double> myDoubles = new ArrayList<Double>();
    Matcher matcher = Pattern.compile("[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?").matcher(input);

    while (matcher.find()) {
      double element = Double.parseDouble(matcher.group());
      myDoubles.add(element);
    }

    for (double element : myDoubles) {
      System.out.println(element);
    }
    return myDoubles;
  }

  public Double calculateProcentage(Double originalPrice, Double discountPrice) {
    Double procentage = (discountPrice * 100) / originalPrice;
    Double discountProcentage = 100 - procentage;
    return discountProcentage;
  }

  public void cleanBag() throws InterruptedException {
    Thread.sleep(2000);
    try {
      driver.findElement(cartIcon).click();
    } catch (Exception e) {
      closePopUpsNonJava();
      Thread.sleep(2000);
      driver.findElement(cartIcon).click();
    }
  }

  public void closePopUps() {
    String parent = driver.getWindowHandle();
    Set<String> pops = driver.getWindowHandles();
    {
      Iterator<String> it = pops.iterator();
      while (it.hasNext()) {

        String popupHandle = it.next().toString();
        if (!popupHandle.contains(parent)) {
          driver.switchTo().window(popupHandle);
          System.out.println("Popu Up Title: " + driver.switchTo().window(popupHandle).getTitle());
          driver.close();
        }
      }
    }
  }

  public void closePopUpsNonJava() {
    for (WebElement element : driver.findElements(By.tagName("button"))) {
      if (element.getText().equals("Shop now")) {
        element.click();
        break;
      }
    }
  }

  public void pressAddToCart() {
    for (WebElement element : driver.findElements(By.tagName("span"))) {
      if (element.getText().toLowerCase().strip().contains("add to cart")) {
        element.click();
        break;
      }
    }
  }

  public void increaseAmount(String url, String amount) throws InterruptedException {
    for (WebElement element : driver.findElements(By.tagName("input"))) {
      if (element.getAttribute("name").equals("updates[]")) {
        if (url.contains("manitobah")) {
          element.click();
          String s = Keys.chord(Keys.COMMAND, "a");
          element.sendKeys(Keys.BACK_SPACE);
          element.sendKeys(amount);
        } else {
          element.clear();
          element.sendKeys(amount);
        }
        break;
      }
    }
  }

  public void updateCart() {
    for (WebElement element : driver.findElements(By.tagName("button"))) {
      if (element.getAttribute("name").equals("update")) {
        element.click();
        break;
      }
    }
  }

  public void clickCheckOut() {
    for (WebElement element : driver.findElements(By.tagName("button"))) {
      if (element.getAttribute("name").equals("checkout")) {
        element.click();
        break;
      }
    }
  }

  public void acceptCookies() {
    driver.findElement(By.id("shopify-privacy-banner-accept-button")).click();
  }

  public void validatePriceIsCalculatedCorrectly(String url, Double originalPrice, Boolean shippingCosts)
      throws InterruptedException {
    int startingIndex = 0;
    Thread.sleep(3000);
    String allText = (url.contains("uppercasemagazine")) ? driver.findElement(By.id("app")).getText()
        : driver.findElement(By.className("order-summary__section--total-lines")).getText();
    String[] listOfPrices = allText.split("\\$");
    ArrayList<Double> myDoubles = new ArrayList<Double>();
    for (String itemString : listOfPrices) {
      if (returnAllDoubles(itemString).size() > 0) {
        myDoubles.add(returnAllDoubles(itemString).get(0));
      }
    }
    if (url.contains("uppercasemagazine")) {
      myDoubles = (ArrayList<Double>) myDoubles.stream()
          .distinct()
          .collect(Collectors.toList());
      startingIndex = 1;
    }


    Double counter = 0.00;
    for (int index = startingIndex; index < myDoubles.size() - 1; index++) {
      counter = counter + myDoubles.get(index);
    }
    assertEquals(counter, myDoubles.get(myDoubles.size() - 1));
  }
}
