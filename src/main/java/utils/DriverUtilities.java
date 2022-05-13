package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
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
    public void waitForLoaderInvisibility() {
        try {
            do {
                sleep(500);
            } while (findElementNoVisibility(pentahoLoader).isDisplayed());
        } catch (NoSuchElementException ignore) {
        }
    }

    public void waitForInvisibilityOfSpinnerLoader() {
        explicitWait.until(ExpectedConditions.invisibilityOfElementLocated(spinnerLoader));
    }

    @SneakyThrows
    public void waitForDataToLoad(int millis) {
        sleep(millis);
    }

    public void waitForDataToLoad() {
        waitForDataToLoad(4000);
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

    public void waitForPageToLoad() {
        waitForDataToLoad(4000);

    }

    protected void waitInvisibilityWithTimeout(By elementBy) {
        wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(elementBy));
    }

    protected void isElementInvisible(By elementBy) {
        try {
            explicitWait.until(ExpectedConditions.invisibilityOfElementLocated(elementBy));
        } catch (Exception ignore) {
        }
    }

    protected List<WebElement> findElements(By elementBy) {
        explicitWait
            .withMessage("No visibility for all elements located as: [" + elementBy + "]")
            .pollingEvery(Duration.ofMillis(50))
            .ignoring(StaleElementReferenceException.class)
            .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(elementBy));
        return driver.findElements(elementBy);
    }

    protected void clickFirstFoodItem() {
        pressEnterFromKeyboard();
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

    protected WebElement findElementNoVisibility(By elementBy) {
        return driver.findElement(elementBy);
    }

    protected WebElement findElementWithoutWaiting(By elementBy) {
        try {
            return driver.findElement(elementBy);
        } catch (Exception ex) {
            throw new NoSuchElementException("No such element!!!");
        }
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

    public void clickWithoutClickableWait(By elementBy) {
        waitForPresenceOfElementLocated(elementBy);
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

    protected void assertUiTextAgainstApiText(By elementBy, String expectedText) {
        waitForPresenceOfElementLocated(elementBy);
        assertTrue(expectedText.toLowerCase().trim().contains(getText(elementBy).toLowerCase().trim()),
            String.format("The element doesn't contain the expected text!\nExpected: %s , \n  Actual: %s\n",
                expectedText, getText(elementBy)));
    }

    public void assertIfElementPresent(By elementBy) {
        explicitWait
            .withMessage("Element: [" + elementBy + "] not present.")
            .pollingEvery(Duration.ofMillis(50))
            .ignoring(StaleElementReferenceException.class)
            .until(ExpectedConditions.visibilityOfElementLocated(elementBy));
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

    public void clickOnChildOfAnElement(By listOfElements, String name, By childElement) {
        waitForPresenceOfElementLocated(listOfElements);
        List<WebElement> elements = findElements(listOfElements);
        for (WebElement element : elements) {
            if (element.getText().contains(name)) {
                element.findElement(childElement).click();
                waitForDataToLoad(4000);
                break;
            }
        }
    }

    public void clickElementFromListIfEqualsValue(String value, By list) {
        waitForPresenceOfElementLocated(list);
        List<WebElement> listOfWebElements = findElements(list);
        for (WebElement webElement : listOfWebElements) {
            if (webElement.getText().equals(value)) {
                webElement.click();
                break;
            }
        }
    }

    public void clickElementFromListIfContainsValue(String value, By list) {
        waitForPresenceOfElementLocated(list);
        List<WebElement> listOfWebElements = findElements(list);
        for (WebElement webElement : listOfWebElements) {
            if (webElement.getText().contains(value)) {
                webElement.click();
                break;
            }
        }
    }

    public void assertElementFromListContainsValue(String value, By list) {
        waitForPresenceOfElementLocated(list);
        for (WebElement webElement : findElements(list)) {
            if (webElement.getText().contains(value)) {
                assertTrue(webElement.getText().contains(value));
                break;
            }
        }
    }

    public void assertElementFromListEqualsTheValue(String value, By list) {
        List<WebElement> listOfWebElements = findElements(list);
        waitForPresenceOfElementLocated(list);
        for (WebElement webElement : listOfWebElements) {
            if (webElement.getText().contains(value)) {
                assertEquals(value, webElement.getText());
                break;
            }
        }
    }

    public void assertElementDisplayedInTheList(By list) {
        waitForPresenceOfElementLocated(list);
        List<WebElement> listOfWebElements = findElements(list);
        for (WebElement element : listOfWebElements) {
            assertTrue(element.isDisplayed());
        }
    }

    protected String monthAndYearFormatter() {
        SimpleDateFormat date = new SimpleDateFormat("MMMM yyyy");
        return date.format((new Date()));
    }

    public String dayMonthAndYearFormatter() {
        SimpleDateFormat date = new SimpleDateFormat("d MMM yyyy");
        return date.format((new Date()));
    }

    protected String formatDateAsYearMonthAndDate() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        return date.format((new Date()));
    }

    public void navigateToWindow(int tabNumber) {
        //create a list of tabs from whom to select
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabNumber));
    }

    public void goToUrl(String url) {
        driver.get(url);
        waitForDataToLoad(4000);
    }

    public void scrollDown() {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("window.scrollBy(0,1000)");
    }

    public void softScrollDown() {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("window.scrollBy(0,150)");
    }

    public void scrollUp() {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("window.scrollBy(0,-800)");
    }

    public void scrollIntoView(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView();", element);
    }

    public void openNewTab() {
        ((JavascriptExecutor) driver).executeScript("window.open()");
    }

    public boolean getAttribute(By element, String attribute, String contains) {
        waitForPresenceOfElementLocated(element);
        assertTrue(findElements(element)
            .stream()
            .findAny().isPresent(), "Element: [" + element + "] not present.");
        return findElements(element)
            .stream()
            .anyMatch(webElement -> webElement.getAttribute(attribute).equals(contains));
    }

    public Boolean isDisplayed(By element) {
        waitForPresenceOfElementLocated(element);
        return findElementsNoVisibility(element).stream().anyMatch(WebElement::isDisplayed);
    }

    public Boolean isNotDisplayed(By element) {
        return findElementsNoVisibility(element).stream().noneMatch(WebElement::isDisplayed);
    }

    public Boolean exists(By element, String value) {
        return findElements(element)
            .stream()
            .anyMatch(searchResults -> searchResults.getText().contains(value));
    }

    public Boolean doesNotExists(By element, String value) {
        return findElements(element)
            .stream()
            .noneMatch(searchResults -> searchResults.getText().contains(value));
    }

    public Boolean isEnabled(By element) {
        waitForPresenceOfElementLocated(element);
        return findElements(element)
            .stream()
            .findFirst()
            .get()
            .isEnabled();
    }

    public List<String> collectToList(By element, Integer skip) {
        waitForPresenceOfElementLocated(element);
        return findElements(element)
            .stream()
            .skip(skip)
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }

    public List<String> collectToList(By element) {
        waitForPresenceOfElementLocated(element);
        return findElements(element)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }

    public void clearFieldByPressingBackspace(By field) {
        waitForPresenceOfElementLocated(field);
        click(field);
        for (int i = 0; i < 50; i++) {
            senKeysWithoutClear(field, valueOf(Keys.BACK_SPACE));
        }
    }

    public void mouseOverAndSelectFromList(By listOfElements, String name, By childElement) {
        waitForPresenceOfElementLocated(listOfElements);
        for (WebElement element : findElements(listOfElements)) {
            if (element.getText().contains(name)) {
                mouseOver(element);
                element.findElement(childElement).click();
                break;
            }
        }
    }

    public void mouseOverOnElementFromList(By listOfElements, String name, By childElement) {
        waitForPresenceOfElementLocated(listOfElements);
        for (WebElement element : findElements(listOfElements)) {
            if (element.getText().contains(name)) {
                mouseOver(element.findElement(childElement));
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

    public void waitForDownloadToComplete(String fileName) throws InterruptedException {
        boolean fileFound = false;
        File file = new File(pathNewFolder);

        if (file.listFiles().length == 0 && counter < 300){
            try {
                driver.findElement(By.id("download")).click();
            }catch (Exception ex){
                logger.debug("Download button was not found");
            }
            Thread.sleep(1000);
            counter++;
            waitForDownloadToComplete(fileName);
        }

        assertTrue(file.listFiles().length > 0, "No document was downloaded");

        counter = 0;
        if (!file.listFiles()[0].getName().equals(fileName)) {
            while (!file.listFiles()[0].getName().equals(fileName) && counter < 30) {
                Thread.sleep(1000);
                counter++;
            }
            if (file.listFiles()[0].getName().equals(fileName)){
                fileFound = true;
            }
        } else {
            fileFound = true;
        }
        Assert.assertTrue(fileFound, "No new report was downloaded");
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

    public boolean checkEntryInList(String entry, List<String> list) {
        for (String item : list) {
            if (item.contains(entry)) {
                return true;
            }
        }
        return false;
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
