package utils;


import lombok.SneakyThrows;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import pages.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static driverFactory.WebDriverSetup.*;
import static java.lang.String.valueOf;
import static java.lang.Thread.sleep;
import static org.openqa.selenium.Keys.ENTER;
import static org.openqa.selenium.Keys.ESCAPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class DriverUtilities {

  private static final Logger logger = LoggerFactory.getLogger(DriverUtilities.class);
  public final By pentahoLoader = By.cssSelector(".img.blockUIDefaultImg");
  protected final By spinnerLoader = By.cssSelector("[data-test-spinner]");
  public String relativePath;
  public static String pathNewFolder;
  protected static WebDriver driver;
  public static Shopify shopify;
  protected WebDriverWait explicitWait;
  protected Wait<WebDriver> wait;
  protected Actions actions;
  protected int counter = 0;

  public static String getTomorrowDate() {
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    Date currentDate = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(currentDate);
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    Date tomorrowDate = calendar.getTime();
    return date.format(tomorrowDate);
  }

  public static String getTodayDate() {
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    return date.format((new Date()));
  }

  public static List<LocalDateTime> getPreviousWeek(LocalDateTime date) {
    final int dayOfWeek = date.getDayOfWeek().getValue();
    final List<LocalDateTime> daysOfLastWeek = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      daysOfLastWeek.add(date.minusDays(dayOfWeek + i));
    }
    return daysOfLastWeek;
  }

  public void setPathNewFolder(String pathNewFolder) {
    this.pathNewFolder = pathNewFolder;
  }

  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }

  public void setDriver(WebDriver driver) {
    this.driver = driver;
  }

  public void setExplicitWait(WebDriverWait explicitWait) {
    this.explicitWait = explicitWait;
  }

  public void initializePages() {
    shopify = new Shopify();
    shopify.setDriver(driver);
    shopify.setExplicitWait(explicitWait);
  }


  @SneakyThrows
  public void waitForDataToLoad(int millis) {
    sleep(millis);
  }


  protected void waitForVisibility(By elementBy) {
    explicitWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(elementBy));
  }

  protected void waitForPresenceOfElementLocated(By elementBy) {
    explicitWait
        .withMessage("Element: [" + elementBy + "] not present.")
        .pollingEvery(Duration.ofMillis(50))
        .ignoring(StaleElementReferenceException.class)
        .until(ExpectedConditions.presenceOfElementLocated(elementBy));
  }


  protected List<WebElement> findElements(By elementBy) {
    explicitWait
        .withMessage("No visibility for all elements located as: [" + elementBy + "]")
        .pollingEvery(Duration.ofMillis(50))
        .ignoring(StaleElementReferenceException.class)
        .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(elementBy));
    return driver.findElements(elementBy);
  }


  protected List<WebElement> findElementsWithoutWaiting(By elementBy) {
    try {
      return driver.findElements(elementBy);
    } catch (Exception e) {
      throw new NoSuchElementException("No elements are visible!");
    }
  }

  protected WebElement findElement(By elementBy) {
    explicitWait
        .withMessage("No visibility for element located as: [" + elementBy + "]")
        .pollingEvery(Duration.ofMillis(50))
        .ignoring(StaleElementReferenceException.class)
        .until(ExpectedConditions.presenceOfElementLocated(elementBy));
    return driver.findElement(elementBy);
  }

  protected List<WebElement> findElementsNoVisibility(By elementBy) {
    return driver.findElements(elementBy);
  }

  public void click(By elementBy) {
    waitForPresenceOfElementLocated(elementBy);
    explicitWait
        .withMessage("Element: [" + elementBy + "] not clickable.")
        .pollingEvery(Duration.ofMillis(150))
        .ignoring(StaleElementReferenceException.class)
        .until(ExpectedConditions.presenceOfElementLocated(elementBy));
    driver.findElement(elementBy).click();
  }


  public void mouseOver(By elementBy) {
    explicitWait
        .withMessage("Element: [" + elementBy + "] cannot be hovered.")
        .pollingEvery(Duration.ofMillis(50))
        .ignoring(StaleElementReferenceException.class)
        .until(ExpectedConditions.presenceOfElementLocated(elementBy));
    actions = new Actions(driver);
    actions
        .moveToElement(driver.findElement(elementBy))
        .build()
        .perform();
  }

  public void mouseOver(WebElement element) {
    explicitWait
        .withMessage("Element: [" + element + "] cannot be hovered.")
        .pollingEvery(Duration.ofMillis(50))
        .ignoring(StaleElementReferenceException.class)
        .until(ExpectedConditions.elementToBeClickable(element));
    actions = new Actions(driver);
    actions
        .moveToElement(element)
        .build()
        .perform();
  }

  protected void sendKeysWithClear(By elementBy, String text) {
    waitForPresenceOfElementLocated(elementBy);
    driver.findElement(elementBy).clear();
    driver.findElement(elementBy).sendKeys(text);
  }

  protected void sendKeys(By elementBy, String text) {
    driver.findElement(elementBy).sendKeys(text);
  }

  protected void senKeysWithoutClear(By elementBy, String text) {
    waitForPresenceOfElementLocated(elementBy);
    driver.findElement(elementBy).sendKeys(text);
  }

  protected String getText(By element) {
    waitForPresenceOfElementLocated(element);
    return findElement(element).getText();
  }

  public Boolean textContainsValue(By element, String value) {
    waitForPresenceOfElementLocated(element);
    return findElementsWithoutWaiting(element)
        .stream()
        .anyMatch(text -> text
            .getText()
            .contains(value));
  }

  public Boolean textEqualsValue(By element, String value) {
    waitForPresenceOfElementLocated(element);
    return findElementsWithoutWaiting(element)
        .stream()
        .anyMatch(text -> text
            .getText()
            .equals(value));
  }

  public void pressEnterFromKeyboard() {
    new Actions(driver)
        .sendKeys(ENTER)
        .build()
        .perform();
  }

  public void pressEscFromKeyboard() {
    new Actions(driver)
        .sendKeys(ESCAPE)
        .build()
        .perform();
  }

  protected void assertElementContainsText(By elementBy, String expectedText) {
    waitForPresenceOfElementLocated(elementBy);
    assertTrue(getText(elementBy).toLowerCase().trim().contains(expectedText.toLowerCase().trim()),
        String.format("The element doesn't contain the expected text!\nExpected: %s , \n  Actual: %s\n",
            expectedText, getText(elementBy)));
  }


  protected void assertIfElementIsNotPresent(By elementBy) {
    try {
      explicitWait.until(ExpectedConditions.invisibilityOfElementLocated(elementBy));
    } catch (Exception e) {
      e.getStackTrace();
    }
  }

  protected void refreshThePageAndWaitToLoad() {
    driver.navigate().refresh();
    waitForDataToLoad(2500);
  }

  protected void switchToFrame(By elementBy) {
    explicitWait
        .withMessage("Element: [" + elementBy + "] not available.")
        .pollingEvery(Duration.ofMillis(50))
        .ignoring(StaleElementReferenceException.class)
        .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(elementBy));
  }

  protected void switchToDefaultContent() {
    driver.switchTo().defaultContent();
  }

  protected void waitForJStoLoad() {
    explicitWait.until((ExpectedCondition<Boolean>) wd ->
        ((JavascriptExecutor) Objects.requireNonNull(wd)).executeScript("return document.readyState")
            .equals("complete"));
  }

  public void clickFieldAndSelectValueFromDropDown(String value, By dropDownElement, By field) {
    List<WebElement> listOfElements = findElements(dropDownElement);
    click(field);
    waitForPresenceOfElementLocated(dropDownElement);
    for (WebElement webElement : listOfElements) {
      if (webElement.getText().contains(value)) {
        webElement.click();
        break;
      }
    }
  }

  /////////////////////////////////////////////

  public void switchIframe() {
    WebElement iFrame = driver.findElement(By.tagName("iframe"));
    driver.switchTo().frame(iFrame);
  }

  public void cleanDownloads() {
    File file = new File(pathNewFolder);
    File[] files = file.listFiles();
    if (files != null) {
      for (File f : files) {
        logger.debug("*******" + f.getName() + "*********");
        if (f.isFile() && f.exists()) {
          if (!f.getName().equals("placeHolder.java")) {
            f.delete();
          }
        }
      }
    }
  }

  public void cleanDownloads(File file) {
    File[] files = file.listFiles();
    for (File f : files) {
      logger.debug("*******" + f.getName() + "*********");
      if (f.isFile() && f.exists()) {
        if (!f.getName().equals("placeHolder.java")) {
          f.delete();
        }
      }
    }
  }


  public void waitForAnyFileToDownload() throws InterruptedException {
    Integer counter = 0;
    File file = new File(pathNewFolder);
    System.out.println("Searching for file in path " + pathNewFolder);
    File[] files = file.listFiles();
    Integer initialLenght = files.length;

    while (files.length <= initialLenght && counter < 1000) {
      files = file.listFiles();
      Thread.sleep(1000);
      counter++;
    }

    for (File item : Objects.requireNonNull(file.listFiles())) {
      logger.debug("*******" + item.getName() + "*********");
    }
    Thread.sleep(2000);

    Boolean crdownload = true;
    files = file.listFiles();
    if (files.length > 0) {
      while (counter < 100 && crdownload) {
        file = new File(pathNewFolder);
        files = file.listFiles();
        crdownload = files[0].getName().contains("crDownload");
        Thread.sleep(1000);
      }
    }

    Assert.assertTrue(file.listFiles().length > initialLenght,
        "No new report was downloaded even after 16.6666667 minutes");
  }

  public void waitVisibilityWithTimeout(By elementBy, long timeout) {
    WebDriverWait wait = new WebDriverWait(driver, timeout);
    wait.until(ExpectedConditions.presenceOfElementLocated(elementBy));
  }

  public void deleteDirectory(File file) {
    if (file != null && !IS_ON_LINUX) {
      for (File subfile : file.listFiles()) {
        if (subfile.isDirectory()) {
          deleteDirectory(subfile);
        }
        if (!subfile.getName().equals("placeHolder.java")) {
          subfile.delete();
        }
      }
    }
  }
}
