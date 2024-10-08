package de.impl.orgatop;

import de.api.Item;
import de.api.Part;
import de.impl.PartInitializerBase;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PartInitializer extends PartInitializerBase {

    public static final int MAX_ITEMS_TO_INITIALIZE = 10;
    public static final String STR_MANUEL_PRUEFEN = "Manuel prüfen";
    private final Logger logger = LoggerFactory.getLogger(PartInitializer.class);

    public PartInitializer(final WebDriver driver, final WebDriverWait wait) {
        super(driver, wait);
    }

    @Override
    public void initPart(final Part part) {
        search(part);
        if (isSearchResultsAvailable()) {
            parseSearchResult(part);
        } else if (isOnDetails()) {
            final Item i = new Item("", null);
            i.setPart(part);
            initializeDetailsForItem(i);
            part.getOtherItems().add(i);
        } else {
            logger.warn("Keine Ergebnisse zu {}", part.getPartNr());
        }
    }

    private void search(final Part part) {
        final String partNr = part.getPartNr();
        logger.info("Suche: {}", partNr);
        final WebElement searchField = driver.findElement(By.id("solrSearchTerm"));
        searchField.sendKeys(Keys.DELETE);
        searchField.sendKeys(partNr);
        searchField.sendKeys(Keys.RETURN);
    }

    private boolean isSearchResultsAvailable() {
        try {
            wait.until(d -> !driver.findElement(By.id("searchResultTbl")).findElement(By.tagName("tbody")).findElements(By.className("odd")).isEmpty());
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    // Falls nur ein Orgatop-Ergebnis existiert so wird automatisch auf Details navigiert.
    // Hier wird Geprüft ob das der Fall ist.
    private boolean isOnDetails() {
        return !driver.findElements(By.id("properties")).isEmpty();
    }

    private void parseSearchResult(final Part part) {
        this.initializeItems(part);
    }

    private void initializeItems(final Part part) {
        final List<WebElement> rows = driver.findElement(By.id("searchResultTbl")).findElement(By.tagName("tbody")).findElements(By.xpath("tr"));

        for (final WebElement row : rows) {
            final List<WebElement> columns = row.findElements(By.xpath("td"));
            if (columns.size() != 7) {
                logger.error("Unerwartete Anzahl der Spalten in der Ergebnistabelle {}", columns.size());
                continue;
            }
            initializeItemFromResultRow(part, columns);
            if (part.getOtherItems().size() >= MAX_ITEMS_TO_INITIALIZE) {
                break;
            }
        }
        initializeDetails(part);
    }

    private void initializeItemFromResultRow(final Part part, final List<WebElement> columns) {
        final String partNummer = columns.get(2).getText().trim();
        final Item item = new Item(partNummer, null);
        item.setPart(part);
        part.getOtherItems().add(item);

        final String partName = columns.get(3).getText().trim();
        item.setName(partName);
        item.setPriceNetto(getPriceNetto(columns.get(6)));
        item.setUrlToDetails(getLinkToDetails(columns.get(2)));
        item.setVerpackungseinheit(getVerpackungseiheit(columns.get(5)));
        item.setVerfuegbarkeit(getVerfuegbarkeit(part, columns.get(5)));

        logger.info("------------------");
        logger.info("Artikelnummer: {}", item.getItemNumber());
        logger.info("Bezeichnung: {}", item.getName());
        logger.info("Preis Netto: {}", item.getPriceNetto());
        logger.info("Verpackungseinheit: {}", item.getVerpackungseinheit());
        logger.info("Verfügbarkeit: {}", item.getVerfuegbarkeit());
    }

    private void initializeDetails(final Part part) {
        part.getOtherItems()
                .stream()
                .filter(it -> it.getUrlToDetails() != null).
                forEach(this::navigateToDetailsAndInitializeDetailsForItem);

    }

    private void navigateToDetailsAndInitializeDetailsForItem(final Item item) {
        if (item.getUrlToDetails() == null) {
            return;
        }
        try {
            navigateToDetails(item.getUrlToDetails());
            initializeDetailsForItem(item);
            navigateBackToSearchResults();
        } catch (final Exception e) {
            logger.warn("Details für {} konnten nicht ermittelt werden", item.getItemNumber(), e);
        }
    }

    private void initializeDetailsForItem(final Item item) {
        item.setPriceNetto(getPreisNettoFromDetails());
        item.setPriceBrutto(getPreisBruttoFromDetails());
        item.setVerpackungseinheit(getVerpackungseinheitFromDetails());
        item.setItemNumber(getArtikelnummerFromDetails());
        item.setOemNummern(getOrgatopNummerFromDetails());
        item.setName(getBezeichnungFromDetails());
        item.setGewicht(getGewichtFromDetails());

        if (item.getVerfuegbarkeit() == null || item.getVerfuegbarkeit().isBlank() || item.getVerfuegbarkeit().equals(STR_MANUEL_PRUEFEN)){
            item.setVerfuegbarkeit(getVerfuegbarkeitFromDetails(item));
        }

        logger.info("----------Details zu {} -------------", item.getItemNumber());
        logger.info("Bezeichnung: {}", item.getName());
        logger.info("Preis Netto: {}", item.getPriceNetto());
        logger.info("Preis Brutto: {}", item.getPriceBrutto());
        logger.info("Gewicht: {}", item.getGewicht());
        logger.info("Verpackungseinheit: {}", item.getVerpackungseinheit());
        logger.info("Verfügbarkeit: {}", item.getVerfuegbarkeit());
    }

    private void navigateToDetails(final String urlToDetails) {
        final String xpath = String.format("//*[@*=\"%s\"]", urlToDetails);
        driver.findElement(By.xpath(xpath)).click();
        wait.until(d -> d.findElement(By.id("properties")));
    }

    private void navigateBackToSearchResults() {
        driver.navigate().back();
        driver.switchTo().defaultContent().switchTo().frame("innerFrame").switchTo().frame("catalog").switchTo().frame("content");
        wait.until(d -> !driver.findElement(By.id("searchResultTbl")).findElement(By.tagName("tbody")).findElements(By.className("odd")).isEmpty());
    }

    private String getPriceNetto(final WebElement columnWithPrice) {
        try {
            final boolean isPreisanfrage = !columnWithPrice.findElements(By.linkText("Preisanfrage")).isEmpty();
            return isPreisanfrage ? "Preisanfrage" : columnWithPrice.findElement(By.tagName("span")).findElement(By.tagName("b")).getText();
        } catch (final Exception e) {
            logger.warn("Preis Netto konnte nicht ermittelt werden (Tabelle)");
            return "";
        }
    }

    private String getLinkToDetails(final WebElement columnWithLink) {
        try {
            final boolean isLinkAvailable = columnWithLink.findElements(By.tagName("a")).size() == 1;
            return isLinkAvailable ? columnWithLink.findElement(By.tagName("a")).getAttribute("href") : null;
        } catch (final Exception e) {
            logger.warn("Link zu Details konnte nicht ermittelt werden (Tabelle)");
            return null;
        }
    }

    private String getVerpackungseiheit(final WebElement columnWithVerpackungseinheit) {
        try {
            final boolean isVerpackungseinheitAvailable = columnWithVerpackungseinheit.findElements(By.name("quantity")).size() == 1;
            return isVerpackungseinheitAvailable ? columnWithVerpackungseinheit.findElement(By.name("quantity")).getAttribute("value") : "";
        } catch (final Exception e) {
            logger.warn("Verpackungseinheit konnte nicht ermittelt werden (Tabelle)");
            return "";
        }
    }

    private String getVerfuegbarkeit(Part part, WebElement columnQuickBoard) {
        String bedarfsmaenge = part.getPartBedarfsmaenge();
        if (bedarfsmaenge == null || bedarfsmaenge.isBlank()) {
            bedarfsmaenge = "1";
        }
        try {
            final WebElement mengeInput = wait.until(d -> columnQuickBoard.findElements(By.tagName("input")))
                    .stream()
                    .filter(el -> el.getAttribute("name").equals("quantity"))
                    .findFirst().orElseThrow();
            mengeInput.sendKeys("\u0008\u0008\u0008");
            mengeInput.sendKeys(bedarfsmaenge);

            wait.until(d -> columnQuickBoard.findElement(By.className("avalButton"))).click();

            return wait.until(d -> columnQuickBoard.findElement(By.className("avalButton"))
                    .findElement(By.xpath("//div[@title]"))).getAttribute("title");

        } catch (Exception e) {
            logger.warn("Verfügbarkeit konnte nicht ermittelt werden");
            return STR_MANUEL_PRUEFEN;
        }
    }

    private String getVerfuegbarkeitFromDetails(final Item item) {
        String bedarfsmaenge = item.getPart().getPartBedarfsmaenge();
        if (bedarfsmaenge == null || bedarfsmaenge.isBlank()) {
            bedarfsmaenge = "1";
        }
        try {
            final WebElement mengeInput = driver.findElement(By.id("properties")).findElement(By.name("quantity"));
            mengeInput.sendKeys("\u0008\u0008\u0008");
            mengeInput.sendKeys(bedarfsmaenge);

            wait.until(d -> d.findElement(By.id("properties")).findElement(By.cssSelector("[alt=\"Verfügbarkeit\"]"))).click();
            final String xpath = "//div[contains(@style,'availabilityGreen.png')] | //div[contains(@style,'availabilityYellow.png')] | //div[contains(@style,'availabilityGreenYellow.png')] | //div[contains(@style,'availabilityRed.png')]";
            return wait.until(d -> d.findElement(By.id("properties")).findElement(By.xpath(xpath))).getAttribute("title");
        } catch (final Exception e) {
            logger.warn("Verfügbarkeit konnte nicht ermittelt werden");
            return STR_MANUEL_PRUEFEN;
        }
    }

    private String getPreisNettoFromDetails() {
        try {
            return driver.findElement(By.id("properties")).findElement(By.className("nettoPrice")).findElement(By.tagName("b")).getText();
        } catch (final Exception e) {
            logger.warn("Netto Preis konnte nicht ermittelt werden");
            return "";
        }
    }

    private String getPreisBruttoFromDetails() {
        try {
            return driver.findElement(By.id("properties")).findElement(By.className("ListDimension")).getText();
        } catch (final Exception e) {
            logger.warn("Brutto Preis konnte nicht ermittelt werden");
            return "";
        }
    }

    private String getVerpackungseinheitFromDetails() {
        try {
            final String xpath = "//*[contains(@class, 'ListLabel') and normalize-space(text())=\"Verpackungseinheit\"]/parent::tr";
            return driver.findElement(By.id("properties")).findElement(By.xpath(xpath)).findElement(By.className("ListText")).getText();
        } catch (final Exception e) {
            logger.warn("Verpackungseinheit konnte nicht ermittelt werden");
            return "";
        }
    }

    private String getGewichtFromDetails() {
        try {
            final String xpath = "//*[contains(@class, 'ListLabel') and normalize-space(text())=\"Gewicht\"]/parent::tr";
            return driver.findElement(By.id("properties")).findElement(By.xpath(xpath)).findElement(By.className("ListDimension")).getText();
        } catch (final Exception e) {
            logger.warn("Gewicht konnte nicht ermittelt werden");
            return "";
        }
    }

    private String getArtikelnummerFromDetails() {
        try {
            final String xpath = "//*[contains(@class, 'ListLabel') and normalize-space(text())=\"Id.-Art.Nr.\"]/parent::tr";
            return driver.findElement(By.id("properties")).findElement(By.xpath(xpath)).findElement(By.className("Text")).getText();
        } catch (final Exception e) {
            logger.warn("Artikelnummer konnte nicht ermittelt werden");
            return "";
        }
    }

    private String getOrgatopNummerFromDetails() {
        try {
            final String xpath = "//*[contains(@class, 'ListLabel') and normalize-space(text())=\"ORGATOP\"]/parent::tr";
            return driver.findElement(By.id("properties")).findElement(By.xpath(xpath)).findElement(By.className("ListText")).getText();
        } catch (final Exception e) {
            logger.warn("Orgatop Nummer konnte nicht ermittelt werden");
            return "";
        }
    }

    private String getBezeichnungFromDetails() {
        try {
            final String xpath = "//*[contains(@class, 'ListLabel') and normalize-space(text())=\"Artikelbezeichnung\"]/parent::tr";
            return driver.findElement(By.id("properties")).findElement(By.xpath(xpath)).findElement(By.className("Text")).getText();
        } catch (final Exception e) {
            logger.warn("Bezeichnung konnte nicht ermittelt werden");
            return "";
        }
    }
}