package de.impl.gaska;

import de.api.Part;
import de.impl.PartInitializerBase;
import de.impl.SearchServiceBase;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.prefs.Preferences;

public class SearchServiceImpl extends SearchServiceBase {

    private static final String HEADER = "Pos. Nr.;Gesucht Art.Nr.;Gaska Art.Nr.;Bezeichnung IMPEX;Bezeichnung Gaska;Bedarfsmenge IMPEX;Verfuegbarkeit, St;Grosshandelspreis, Netto;Verpackungseinheit;OEM Nummern (Ersatz-Nr.)";
    private final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    public SearchServiceImpl() throws IOException {
        super();
    }


    @Override
    public void authenticate() throws IOException {
        logger.info(">>> authenticate");
        final String settingsFilePath = System.getProperty("settings");

        final Ini ini = new Ini(new File(settingsFilePath == null ? "settings.ini" : settingsFilePath));
        final Preferences prefs = new IniPreferences(ini);

        final String partner = prefs.node("account_gaska").get("partner", null);
        final String login = prefs.node("account_gaska").get("login", null);
        final String password = prefs.node("account_gaska").get("password", null);

        driver.get("https://www.b2b.gaska.com.pl/de/konto/login");
        final WebElement firma = driver.findElement(By.id("Akronim"));
        final WebElement userName = driver.findElement(By.id("Person"));
        final WebElement passwordElement = driver.findElement(By.id("Password"));
        final WebElement loginButton = driver.findElement(By.className("btn-primary"));

        firma.sendKeys(partner);
        userName.sendKeys(login);
        passwordElement.sendKeys(password);
        // Thread.sleep(10000);
        loginButton.click();
        logger.info("<<< authenticate");
    }

    @Override
    public void render(final OutputStream outputStream, final List<Part> parts){
        logger.info(">>> render");
        try (final PrintStream printStream = new PrintStream(outputStream)) {
            printStream.println(HEADER);
            for (final Part part : parts) {
                printStream.println(part);
            }
        }
        logger.info("<<< render");
    }

    @Override
    public PartInitializerBase getPartInitializer() {
        return new PartInitializer(driver, wait);
    }
}
