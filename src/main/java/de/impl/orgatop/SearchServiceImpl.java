package de.impl.orgatop;

import de.api.Item;
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
    private static final String HEADER = "Pos.Nr.;Gesucht Art.Nr.;Menke Art.Nr.;Bezeichnung IMPEX;Bezeichnung Menke;Preis Netto;VE (Verpackungseinheit);Gewicht;Bedarfsmenge IMPEX;Verfuegbarkeit, St;Menke Art.Nr.;Bezeichnung IMPEX;Bezeichnung Menke;Preis Netto;VE (Verpackungseinheit);Gewicht;Bedarfsmenge IMPEX;Verfuegbarkeit, St;";
    final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    public SearchServiceImpl() throws IOException {
        super();
    }

    @Override
    public void authenticate() throws IOException {
        logger.info(">>> authenticate");

        final String settingsFilePath = System.getProperty("settings");

        final Ini ini = new Ini(new File(settingsFilePath == null ? "settings.ini" : settingsFilePath));
        final Preferences prefs = new IniPreferences(ini);

        final String login = prefs.node("account_orgatop").get("login", null);
        final String password = prefs.node("account_orgatop").get("password", null);

        driver.get("https://shop.orgatop.de/");
        driver.switchTo().defaultContent().switchTo().frame("innerFrame").switchTo().frame("menu").switchTo().frame("login");
        final WebElement loginElement = driver.findElement(By.name("login"));
        final WebElement passwordElement = driver.findElement(By.name("pwd"));
        final WebElement loginButton = driver.findElement(By.linkText("Anmelden"));

        loginElement.sendKeys(login);
        passwordElement.sendKeys(password);
        loginButton.click();
        driver.switchTo().defaultContent().switchTo().frame("innerFrame").switchTo().frame("catalog").switchTo().frame("content");
        logger.info("<<< authenticate");
    }

    @Override
    public PartInitializerBase getPartInitializer() {
        return new PartInitializer(driver, wait);
    }

    @Override
    public void render(final OutputStream outputStream, final List<Part> parts) {
        logger.info(">>> render");
        try (final PrintStream printStream = new PrintStream(outputStream)) {
            printStream.println(HEADER);
            parts.stream().map(this::mapToCsvRow).forEach(printStream::println);
        }
        logger.info("<<< render");
    }

    private String mapToCsvRow(Part part) {
        Item original = part.getOtherItems().stream().filter(item -> item.getItemNumber().equals(part.getPartNr())).findFirst().orElse(null);
        Item compatible = part.getOtherItems().stream().filter(item -> item.getItemNumber().equals(part.getPartNr() + "M")).findFirst().orElse(null);
        return mapToCsvColumns(part, original, compatible);
    }

    private String mapToCsvColumns(Part part, Item original, Item compatible) {
        return part.getId() + // Pos.Nr.
                ";" +
                part.getPartNr() + // Gesucht Art. Nr.
                ";" + mapToCsvColumns(part, original) + mapToCsvColumns(part, compatible);


    }

    private String mapToCsvColumns(Part part, Item item){
        return (item == null ? "" : item.getItemNumber()) + // Menke Art.Nr.
                ";" +
                part.getPartName() + // Bezeichnung IMPEX
                ";" +
                (item == null ? "" : item.getName()) + // Bezeichnung Menke
                ";" +
                (item == null ? "" : item.getPriceNetto()) +
                ";" +
                (item == null ? "" : item.getVerpackungseinheit()) +
                ";" +
                (item == null ? "" : item.getGewicht()) +
                ";" +
                part.getPartBedarfsmaenge() +
                ";" +
                (item == null ? "" : item.getVerfuegbarkeit()) +
                ";";
    }
}
