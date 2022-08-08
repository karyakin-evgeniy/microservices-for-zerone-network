package org.proteam24.zeroneapplication.page;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

@DisplayName("Тест в браузере")
public class MainTest {
    private WebDriver driver;

    @BeforeAll
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("--disable-gpu");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("window-size=1200x600");
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    @DisplayName("Тест login/logout")
    void authenticationTest() {
        driver.get("http://zeronenetwork.design/login");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        String loginFormTitle = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/h2")).getText();
        Assertions.assertEquals(loginFormTitle, "Войдите в аккаунт");

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement loginEmail = driver.findElement(By.id("login-email"));
        WebElement loginPassword = driver.findElement(By.id("login-password"));
        WebElement loginButton = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/form/div[3]/button"));
        loginEmail.sendKeys("akrafit@gmail.com");
        loginPassword.sendKeys("Xs3jd4nK");
        loginButton.click();

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        String userName = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/header/a/span")).getText();
        Assertions.assertEquals(userName, "Rafit Akchurin");
        WebElement logoutButton = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[1]/a[2]"));
        logoutButton.click();

        String loginFormTitleAfter = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/h2")).getText();
        Assertions.assertEquals(loginFormTitleAfter, "Войдите в аккаунт");
    }
}
