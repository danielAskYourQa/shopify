package driverFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.DriverUtilities;

import java.io.File;

import static driverFactory.WebDriverSetup.*;
import static java.lang.Long.parseLong;


public class WebBasePage {

    public WebDriverWait explicitWait;
    protected static WebDriver driver;
    public DriverUtilities driverUtilities = new DriverUtilities();
    public String relativePath;
    public String pathNewFolder;
    protected ThreadLocal<String> testName = new ThreadLocal<>();

    public WebBasePage() {
        relativePath =
            (IS_ON_WINDOWS) ? System.getProperty("user.dir") + WebDriverSetup.DOWNLOAD_REPORT_PATH_WINDOWS_PATH
                : ((IS_ON_MAC) ? System.getProperty("user.dir") + DOWNLOAD_REPORT_PATH_IOS_PATH
                    : DOWNLOAD_REPORT_PATH_LINUX_PATH);
        pathNewFolder = (IS_ON_WINDOWS) ? relativePath + WebDriverSetup.NEW_REPORT_DOWNLOAD_FOLDER + "\\"
            : ((IS_ON_MAC) ? relativePath + WebDriverSetup.NEW_REPORT_DOWNLOAD_FOLDER
                + "/" : DOWNLOAD_REPORT_PATH_LINUX_PATH);

        driverUtilities.setPathNewFolder(pathNewFolder);
        driverUtilities.setRelativePath(relativePath);
    }

    public static WebDriver getDriver(){
        return WebDriverSetup.getDriver();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        driver = WebDriverSetup.getDriver();
        explicitWait = new WebDriverWait(driver, parseLong(System.getenv("TIMEOUT")));
        driverUtilities.setDriver(driver);
        driverUtilities.setExplicitWait(explicitWait);
        driverUtilities.initializePages();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        WebDriverSetup.cleanupDriver(driver);
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        File file = new File(driverUtilities.relativePath);
        driverUtilities.deleteDirectory(file);
    }
}
