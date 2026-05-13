package sqt.Selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.Map;

public abstract class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;
    protected AdminUsersPage adminUsersPage;

    protected static final String ADMIN_USER = "Admin";
    protected static final String ADMIN_PASSWORD = "admin123";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-web-security");
        options.addArguments("--no-sandbox");
        options.addArguments("--lang=en-US");
        options.setExperimentalOption("prefs", Map.of("intl.accept_languages", "en-US,en"));

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        loginPage = new LoginPage(driver);
        adminUsersPage = new AdminUsersPage(driver);

        loginPage.open();
        loginPage.loginAs(ADMIN_USER, ADMIN_PASSWORD);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}