package de.impl.gaska;

import de.api.Item;
import de.api.Part;
import de.impl.PartInitializerBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PartInitializer extends PartInitializerBase {

    private final Logger logger = LoggerFactory.getLogger(PartInitializer.class);

    public PartInitializer(final WebDriver driver, final WebDriverWait wait) {
        super(driver, wait);
    }

    @Override
    public void initPart(final Part part) {
        logger.info("Suche: {}", part.getPartNr());
        final String partNr = part.getPartNr();
        final String queryString = partNr.substring(0, partNr.length() - part.getSubstringLastCharsFromPartNrWhileSearch());
        final String targetStr = String.format("https://www.b2b.gaska.com.pl/de/k/alle-produkte-0?q=%s", queryString);
        driver.get(targetStr);
        parseSearchResult(part);
    }

    private void parseSearchResult(final Part part) {
        part.setOtherItems(this.initializeItems());

        for (final Item item : part.getOtherItems()) {

            logger.info("-----------------------");

            logger.info("Part Nr: {}", item.getItemNumber());
            logger.info("Verpackungseinheit: {}", item.getVerpackungseinheit());
            driver.get(item.getUrlToDetails());
            final String partName = this.getPartName();
            final String preisNetto = this.getPreisNetto();
            final String verfuegbarkeit = this.getVerfuegbarkeit();
            final String oemNummern = this.getOemNummer(part);
            item.setName(partName);
            item.setPriceNetto(preisNetto);
            item.setVerfuegbarkeit(verfuegbarkeit);
            item.setOemNummern(oemNummern);
        }

    }

    private List<Item> initializeItems() {
        final List<WebElement> rows = driver.findElements(By.className("prd-row"));
        final List<Item> items = new ArrayList<>();
        int i = 0;
        for (final WebElement row : rows) {
            final WebElement linkToDetails = row.findElement(By.className("stretched-link"));
            final String urlToDetails = linkToDetails.getAttribute("href");
            final String partNr = row.getAttribute("data-code");
            final String verpackungseinheit = row.findElement(By.className("t-package")).findElement(By.className("text-sm-center")).getText();
            final Item item = new Item(partNr, urlToDetails);
            item.setVerpackungseinheit(verpackungseinheit);
            items.add(item);
            i++;
            if (i >= 10) {
                break;
            }
        }
        return items;
    }

    private String getPartName() {
        try {
            final String partName = driver.findElement(By.className("prd-v")).findElement(By.cssSelector("h1.name")).getText();
            logger.info("Bezeichnung: {}", partName);
            return partName;
        } catch (final Exception e) {
            logger.warn("Bezeichnung konnte nicht ermittelt werden.");
            logger.debug(driver.getPageSource());
            return "";
        }

    }

    private String getPreisNetto() {
        try {
            wait.until(d -> d.findElement(By.cssSelector("img.price-img")));
            driver.findElement(By.cssSelector("img.price-img")).click(); // Preis anzeigen
            wait.until(d -> d.findElement(By.cssSelector("div.price-hurt")).findElement(By.tagName("strong")).isDisplayed());
            final String preisNetto = driver.findElement(By.cssSelector("div.price-hurt")).findElement(By.tagName("strong")).getText();
            logger.info("Preis Netto: {}", preisNetto);
            return preisNetto;
        } catch (final Exception e) {
            logger.warn("Preis Netto konnte nicht ermittelt werden.");
            logger.debug(driver.getPageSource());
            return "";
        }
    }

    private String getOemNummer(final Part part) {
        try {
            wait.until(d -> d.findElement(By.cssSelector("button[data-target=\"#numeryZamienneInfo\"]")));
            driver.findElement(By.cssSelector("button[data-target=\"#numeryZamienneInfo\"]")).click(); // oemNummern anzeigen
            wait.until(d -> d.findElement(By.cssSelector("button[data-target=\"#numeryZamienneInfo\"]")));
            final String oemNummern = driver.findElement(By.id("numeryZamienneInfo")).findElement(By.className("card-body")).getText();

            final String[] splitted = oemNummern.split("(,|\\s)");

            final StringBuilder ret = new StringBuilder();
            for (final String oemNummer : splitted) {
                if (oemNummer.contains(part.getPartNr())) {
                    ret.append(oemNummer.trim());
                    ret.append(",");
                }
            }
            logger.info("OEM Nummern: {}", ret);
            return ret.toString();
        } catch (final Exception e) {
            logger.warn("OEM Nummern konnten nicht ermittelt werden.");
            logger.debug(driver.getPageSource());
            return "";
        }
    }

    private String getVerfuegbarkeit() {
        try {
            wait.until(d -> d.findElement(By.cssSelector("img.stock-img")));
            driver.findElement(By.cssSelector("img.stock-img")).click(); // Verfügbarkeit anzeigen
            wait.until(d -> d.findElement(By.cssSelector("div.stock-cont")).findElement(By.tagName("span")));
            String verfuegbarkeit = driver.findElement(By.cssSelector("div.stock-cont")).findElement(By.tagName("span")).getText();
            verfuegbarkeit = this.shouldAddPlusToVerfuegbarkeit() ? "+" + verfuegbarkeit : verfuegbarkeit;
            logger.info("Verfuegbarkeit: {}", verfuegbarkeit);
            return verfuegbarkeit;
        } catch (final Exception e) {
            logger.warn("Verfügbarkeit konnte nicht ermittelt werden.");
            logger.debug(driver.getPageSource());
            return "";
        }
    }

    private boolean shouldAddPlusToVerfuegbarkeit() {
        try {
            driver.findElement(By.cssSelector("div.stock-cont")).findElement(By.cssSelector("span.fa-plus")).getText();
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
}
