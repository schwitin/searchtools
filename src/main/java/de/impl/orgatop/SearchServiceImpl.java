package de.impl.orgatop;

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
import java.util.prefs.Preferences;

public class SearchServiceImpl extends SearchServiceBase {
    Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);


    public SearchServiceImpl() throws IOException {
        super();
    }


    @Override
    public void authenticate() throws IOException {

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
    }

    @Override
    public PartInitializerBase getPartInitializer() {
        return new PartInitializer(driver, wait);
    }

    @Override
    public String getHeader() {
        return super.getHeader().replace("Gaska", "Orgatop");
    }
}
