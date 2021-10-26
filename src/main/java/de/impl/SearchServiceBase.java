package de.impl;

import de.api.Item;
import de.api.Part;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.prefs.Preferences;

public abstract class SearchServiceBase implements Closeable, de.api.SearchService {

    private final Logger logger = LoggerFactory.getLogger(SearchServiceBase.class);
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public SearchServiceBase() throws IOException {

        final String settingsFilePath = System.getProperty("settings");
        final Ini ini = new Ini(new File(settingsFilePath == null ? "settings.ini" : settingsFilePath));
        final Preferences prefs = new IniPreferences(ini);
        WebDriverManager.chromedriver().browserVersion(prefs.node("chrome").get("version", "90")).setup();

        final ChromeOptions chromeOptions = new ChromeOptions();

        if (prefs.node("chrome").get("headless", "true").equals("true")) {
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.addArguments("--disable-extensions");
            chromeOptions.addArguments("--start-maximized");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--ignore-certificate-errors");
        }
        driver = new ChromeDriver(chromeOptions);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void initParts(final List<Part> parts) {
        final PartInitializerBase partInitializer = getPartInitializer();
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
            printStream.println(getHeader());
            for (final Part part : parts) {
                printStream.println(part);
            }
        }
    }


    public abstract void authenticate() throws IOException;


    public String getHeader() {
        return Item.getHeader();
    }

    @Override
    public void close() {
        driver.close();
        driver.quit();
    }

    public abstract PartInitializerBase getPartInitializer();
}
