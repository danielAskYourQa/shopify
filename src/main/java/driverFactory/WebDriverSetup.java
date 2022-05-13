package driverFactory;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static io.github.bonigarcia.wdm.WebDriverManager.edgedriver;
import static io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver;
import static io.github.bonigarcia.wdm.WebDriverManager.iedriver;
import static io.github.bonigarcia.wdm.WebDriverManager.operadriver;
import static io.github.bonigarcia.wdm.WebDriverManager.safaridriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverSetup {

  public static final Boolean IS_ON_WINDOWS = SystemUtils.IS_OS_WINDOWS;
  public static final Boolean IS_ON_LINUX = SystemUtils.IS_OS_LINUX;
  public static final Boolean IS_ON_MAC = SystemUtils.IS_OS_MAC;
  public static final String NEW_REPORT_DOWNLOAD_FOLDER = RandomStringUtils.randomAlphabetic(10);
  public static final String DOWNLOAD_REPORT_PATH_IOS_PATH =
      "/src/test/java/utils/reportDownloads/";
  public static final String DOWNLOAD_REPORT_PATH_LINUX_PATH =
      "/home/seluser/Downloads/";
  public static final String DOWNLOAD_REPORT_PATH_WINDOWS_PATH =
      "\\src\\test\\java\\utils\\reportDownloads\\";
  private static final Logger LOG = LoggerFactory.getLogger(WebDriverSetup.class);
  private static final String USER_DIR = System.getProperty("user.dir");
  private static final String DRIVER_PATH = "/src/main/utils/drivers/";
  private static final String BROWSER = System.getenv("BROWSER");
  private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
  private static WebDriver webDriver;

  public static WebDriver getDriver() {
    driver.set(retrieveDriver());
    return driver.get();
  }

  public static void cleanupDriver(WebDriver driver) {
    try {
      LOG.info("QUITTING DRIVER");
      if (driver != null) {
        driver.quit();
      }
    } catch (WebDriverException e) {
      System.err.println(e);
    }
  }

  @SneakyThrows
  public static WebDriver retrieveDriver() {
    switch (BROWSER) {
      case "Chrome":
        chromedriver().setup();
        webDriver = new ChromeDriver(getChromeOptions());
        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        return webDriver;
      case "Remote-Chrome":
        System.setProperty("webdriver.chrome.driver", USER_DIR + DRIVER_PATH + "nux_chromedriver");
        webDriver = new RemoteWebDriver(new URL(System.getenv("DOCKER_URL")), getChromeOptions());
        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        return webDriver;
      case "Firefox":
        firefoxdriver().setup();
        webDriver = new FirefoxDriver(getFirefoxOptions());
        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        return webDriver;
      case "Remote-Firefox":
        System.setProperty("webdriver.gecko.driver", USER_DIR + DRIVER_PATH + "nux_geckodriver");
        webDriver = new RemoteWebDriver(new URL(System.getenv("DOCKER_URL")), getFirefoxOptions());
        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        return webDriver;
      case "Edge":
        edgedriver().setup();
        webDriver = new EdgeDriver();
        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        return webDriver;
      case "IE":
        iedriver().setup();
        webDriver = new InternetExplorerDriver();
        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        return webDriver;
      case "Opera":
        operadriver().setup();
        webDriver = new OperaDriver();
        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        return webDriver;
      case "Safari":
        safaridriver().setup();
        webDriver = new SafariDriver(getSafariOptions());
        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        return webDriver;
      default:
        throw new RuntimeException(("Unsupported browser! Will not start any browser!"));
    }
  }

  private static ChromeOptions getChromeOptions() throws IOException {
    ChromeOptions options = new ChromeOptions()
        .addArguments("--ignore-certificate-errors")
        .addArguments("--disable-popup-blocking")
        .addArguments("--no-sandbox")
        //.addArguments("--headless")
        .addArguments("--incognito");
    if (IS_ON_WINDOWS) {
      String relativePath = DOWNLOAD_REPORT_PATH_WINDOWS_PATH + NEW_REPORT_DOWNLOAD_FOLDER;
      String path = USER_DIR + relativePath;
      Map<String, Object> preferences = new HashMap<>();
      preferences.put("plugins.always_open_pdf_externally", true);
      Path newDirectoryPath = Paths.get(path);
      Files.createDirectories(newDirectoryPath);
      if (BROWSER.equals("Chrome") || BROWSER.equals("Remote-Chrome")) {
        preferences.put("download.default_directory", path);
      }
      options.setExperimentalOption("prefs", preferences);
    } else if (IS_ON_MAC) {
      String relativePath = DOWNLOAD_REPORT_PATH_IOS_PATH + NEW_REPORT_DOWNLOAD_FOLDER;
      String path = USER_DIR + relativePath;
      Map<String, Object> preferences = new HashMap<>();
      preferences.put("plugins.always_open_pdf_externally", true);
      Path newDirectoryPath = Paths.get(path);
      Files.createDirectories(newDirectoryPath);
      File[] directories = new File(System.getProperty("user.dir") + DOWNLOAD_REPORT_PATH_IOS_PATH)
          .listFiles(File::isDirectory);
      if (directories.length == 0) {
        System.out.println("No new folder was created");
      } else {
        for (File folder : directories) {
          System.out.println(folder);
        }
      }
      if (BROWSER.equals("Chrome") || BROWSER.equals("Remote-Chrome")) {
        preferences.put("download.default_directory", path);
      }
      options.setExperimentalOption("prefs", preferences);
    } else {
      String path = DOWNLOAD_REPORT_PATH_LINUX_PATH;
      Map<String, Object> preferences = new HashMap<>();
      preferences.put("plugins.always_open_pdf_externally", true);
      preferences.put("safebrowsing.enabled", false);
      preferences.put("profile.default_content_settings.popups", 0);
      preferences.put("download.prompt_for_download", false);
      if (BROWSER.equals("Chrome") || BROWSER.equals("Remote-Chrome")) {
        preferences.put("download.default_directory", path);
      }
      options.setExperimentalOption("prefs", preferences);
      options.addArguments("start-maximized");
      options.addArguments("--safebrowsing-disable-download-protection");
      options.addArguments("safebrowsing-disable-extension-blacklist");
    }
    return options;
  }

  private static FirefoxOptions getFirefoxOptions() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setAcceptUntrustedCertificates(true);
    profile.setAssumeUntrustedCertificateIssuer(false);
    profile.setPreference("network.proxy.type", 0);
    FirefoxOptions options = new FirefoxOptions();
    options.setHeadless(true);
    options.setCapability("marionatte", "false");
    options.addArguments("--marionette-port");
    options.addArguments("2828");
    options.addPreference("devtools.selfxss.count", 100);
    options.setLogLevel(FirefoxDriverLogLevel.INFO);
    options.setProfile(profile);
    return options;
  }

  private static SafariOptions getSafariOptions() {
    SafariOptions safariOptions = new SafariOptions();
    safariOptions.setAutomaticInspection(true);
    safariOptions.setAutomaticProfiling(true);
    safariOptions.setUseTechnologyPreview(true);
    return safariOptions;
  }
}
