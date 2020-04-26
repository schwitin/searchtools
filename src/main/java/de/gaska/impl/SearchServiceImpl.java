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

	private Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
	private String login;
	private String password;
	private String partner;

	private WebDriver driver;

	public SearchServiceImpl() throws IOException {


		String settingsFilePath = System.getProperty("settings");

		Ini ini = new Ini(new File(settingsFilePath));
		Preferences prefs = new IniPreferences(ini);

		this.partner = prefs.node("account").get("partner", null);
		this.login = prefs.node("account").get("login", null);
		this.password = prefs.node("account").get("password", null);
		WebDriverManager.chromedriver().version(prefs.node("chromedriver").get("version", "80.0.3987.106")).setup();


		ChromeOptions chromeOptions = new ChromeOptions();

		if(prefs.node("chromedriver").get("headless", "true").equals("true")){
			chromeOptions.addArguments("--headless");
		}
		driver = new ChromeDriver(chromeOptions);
	}

	public void initParts(List<Part> parts) {
		PartInitializer partInitializer = new PartInitializer(driver);

		int i = 0;
		for (Part part : parts) {
			String partNr = part.getPartNr();
			if ("?".equals(partNr)) {
				continue;
			}
			String message = String.format("Verarbeite %s: %s/%s", part.getPartNr(), ++i, parts.size());
			logger.info(message);
			partInitializer.initPart(part);

		}


		logger.info("Erledigt");
	}


	public void printParts(OutputStream outputStream, List<Part> parts){
		try (PrintStream printStream = new PrintStream(outputStream)) {
			printStream.println(getHeader(parts));
			for (Part part : parts) {
				printStream.println(part);
			}
		}
	}

	


	public void authenticate()  {

		driver.get("https://www.gaska.com.pl/zaloguj/partner");
		WebElement firma = driver.findElement(By.id("Akronim_I"));
		WebElement userName = driver.findElement(By.id("UserName_I"));
		WebElement password = driver.findElement(By.id("Password_I"));
		WebElement loginButton = driver.findElement(By.id("LoginButton2_CD"));

		firma.sendKeys(this.partner);
		userName.sendKeys(this.login);
		password.sendKeys(this.password);
		// Thread.sleep(10000);
		loginButton.click();
	}


	private String getHeader(List<Part> parts){
		return Item.getHeader();
	}

	@Override
	public void close() throws IOException {
		driver.close();
	}
}
