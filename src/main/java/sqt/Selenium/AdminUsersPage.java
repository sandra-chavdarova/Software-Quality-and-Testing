package sqt.Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class AdminUsersPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By addUserButton = By.xpath("//button[normalize-space()='Add']");
    private final By userRoleDropdown = By.xpath("//label[text()='User Role']/following::div[contains(@class,'oxd-select-text-input')][1]");
    private final By statusDropdown = By.xpath("//label[text()='Status']/following::div[contains(@class,'oxd-select-text-input')][1]");

    private final By employeeNameField = By.xpath("//label[text()='Employee Name']/following::input[1]");
    private final By firstSuggestion = By.cssSelector(".oxd-autocomplete-dropdown div");

    private final By usernameField = By.xpath("//label[text()='Username']/following::input[1]");
    private final By passwordField = By.xpath("//label[text()='Password']/following::input[1]");
    private final By confirmPassword = By.xpath("//label[text()='Confirm Password']/following::input[1]");
    private final By saveButton = By.xpath("//button[@type='submit']");

    private final By successToast = By.cssSelector(".oxd-toast--success");
    private final By validationError = By.cssSelector(".oxd-input-field-error-message");

    // search
    private final By searchUsernameField = By.xpath("//label[text()='Username']/ancestor::div[contains(@class,'oxd-form-row')]//input");
    private final By searchButton = By.xpath("//button[normalize-space()='Search']");
    private final By tableRows = By.cssSelector(".oxd-table-card");
    private final By noRecordsFound = By.xpath("//*[contains(text(),'No Records Found')]");

    // "(x) Record(s) Found" span that appears after a search completes
    private final By recordsFoundSpan = By.cssSelector("span.oxd-text.oxd-text--span");

    // delete
    private final By deleteButton = By.xpath(".//button[.//*[contains(@class,'bi-trash')]]");
    private final By confirmDeleteButton = By.xpath("//button[contains(@class,'label-danger')]");

    public AdminUsersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateTo() {
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers");
        wait.until(ExpectedConditions.elementToBeClickable(addUserButton));
    }

    public void addUser(String role, String employeeHint, String username, String password)
            throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(addUserButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(usernameField));

        selectDropdownOption(userRoleDropdown, role);
        selectDropdownOption(statusDropdown, "Enabled");

        // employee name – type "a" + Arrow Down + Enter
        WebElement empField = wait.until(ExpectedConditions.elementToBeClickable(employeeNameField));
        empField.clear();
        empField.sendKeys(employeeHint);
        wait.until(ExpectedConditions.presenceOfElementLocated(firstSuggestion));

        // wait 1 second after typing
        Thread.sleep(1_000);

        new Actions(driver).click(empField)
                .sendKeys(org.openqa.selenium.Keys.ARROW_DOWN)
                .sendKeys(org.openqa.selenium.Keys.ENTER)
                .perform();

        wait.until(d -> {
            String val = d.findElement(employeeNameField).getAttribute("value");
            return val != null && !val.trim().isEmpty();
        });

        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(firstSuggestion));
        } catch (TimeoutException ignored) {
        }

        // username
        WebElement userField = wait.until(ExpectedConditions.elementToBeClickable(usernameField));
        userField.clear();
        userField.sendKeys(username);

        By usernameError = By.xpath("//label[text()='Username']/following::span[@class='oxd-input-field-error-message'][1]");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.numberOfElementsToBe(usernameError, 0),
                ExpectedConditions.presenceOfElementLocated(usernameError)
        ));
        List<WebElement> usernameErrors = driver.findElements(usernameError);
        if (!usernameErrors.isEmpty()) {
            throw new RuntimeException("Username validation failed: " + usernameErrors.get(0).getText());
        }

        // password and confirm password
        WebElement passField = wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        passField.clear();
        passField.sendKeys(password);

        WebElement confirmField = wait.until(ExpectedConditions.elementToBeClickable(confirmPassword));
        confirmField.clear();
        confirmField.sendKeys(password);

        // save
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(successToast),
                ExpectedConditions.visibilityOfElementLocated(validationError)
        ));

        List<WebElement> errors = driver.findElements(validationError);
        if (!errors.isEmpty()) {
            throw new RuntimeException("Form validation failed: " + errors.stream().map(WebElement::getText).collect(Collectors.joining(", ")));
        }
    }

    public void searchByUsername(String username) {
        navigateTo();
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(searchUsernameField));
        searchField.clear();
        searchField.sendKeys(username);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();

        // wait for the "(N) Record(s) Found" or "No Records Found" to appear
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(noRecordsFound),
                ExpectedConditions.visibilityOfElementLocated(recordsFoundSpan)
        ));
    }

    public int getResultCount() {
        List<WebElement> noRecords = driver.findElements(noRecordsFound);
        if (!noRecords.isEmpty() && noRecords.get(0).isDisplayed()) {
            return 0;
        }
        return driver.findElements(tableRows).size();
    }

    public boolean isNoRecordFound() {
        List<WebElement> noRecords = driver.findElements(noRecordsFound);
        if (!noRecords.isEmpty() && noRecords.get(0).isDisplayed()) {
            return true;
        }
        return driver.findElements(tableRows).isEmpty();
    }

    public void deleteFirstResult() {
        List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(tableRows));
        if (rows.isEmpty()) {
            throw new IllegalStateException("No row to delete");
        }
        rows.get(0).findElement(deleteButton).click();
        wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(successToast));
    }

    private void selectDropdownOption(By dropdownLocator, String optionText) {
        wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator)).click();
        By option = By.xpath("//div[@role='option']//span[text()='" + optionText + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }
}