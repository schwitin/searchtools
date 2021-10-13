package de.gaska.impl;

import de.gaska.api.Item;
import de.gaska.api.Part;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.prefs.Preferences;

public class SearchServiceImpl implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final String login;
    private final String password;
    private final String partner;

    private final WebDriver driver;

    public SearchServiceImpl() throws IOException {


        final String settingsFilePath = System.getProperty("settings");

        final Ini ini = new Ini(new File(settingsFilePath == null ? "settings.ini" : settingsFilePath));
        final Preferences prefs = new IniPreferences(ini);

        this.partner = prefs.node("account").get("partner", null);
        this.login = prefs.node("account").get("login", null);
        this.password = prefs.node("account").get("password", null);
        WebDriverManager.chromedriver().browserVersion(prefs.node("chrome").get("version", "90")).setup();

        final ChromeOptions chromeOptions = new ChromeOptions();

        if (prefs.node("chrome").get("headless", "true").equals("true")) {
            chromeOptions.addArguments("--headless");
        }
        driver = new ChromeDriver(chromeOptions);
    }

    public void initParts(final List<Part> parts) {
        final PartInitializer partInitializer = new PartInitializer(driver);

        int i = 0;
        for (final Part part : parts) {
            final String partNr = part.getPartNr();
            if ("?".equals(partNr)) {
                continue;
            }
            final String message = String.format("Verarbeite %s: %s/%s", part.getPartNr(), ++i, parts.size());
            logger.info(message);
            partInitializer.initPart(part);

        }


        logger.info("Erledigt");
    }


    public void printParts(final OutputStream outputStream, final List<Part> parts) {
        try (final PrintStream printStream = new PrintStream(outputStream)) {
            printStream.println(getHeader(parts));
            for (final Part part : parts) {
                printStream.println(part);
            }
        }
    }


    public void authenticate() {

        driver.get("https://www.b2b.gaska.com.pl/de/konto/login");
        final WebElement firma = driver.findElement(By.id("Akronim"));
        final WebElement userName = driver.findElement(By.id("Person"));
        final WebElement password = driver.findElement(By.id("Password"));
        final WebElement loginButton = driver.findElement(By.className("btn-primary"));

        firma.sendKeys(this.partner);
        userName.sendKeys(this.login);
        password.sendKeys(this.password);
        // Thread.sleep(10000);
        loginButton.click();
    }


    private String getHeader(final List<Part> parts) {
        return Item.getHeader();
    }

    @Override
    public void close() {
        driver.close();
        driver.quit();
    }
}
