package de.gaska.impl;

import de.gaska.api.Item;
import de.gaska.api.Part;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartInitializer {

	private Logger logger = LoggerFactory.getLogger(PartInitializer.class);

	private WebDriver driver;
	//private Part part;

	public PartInitializer(WebDriver driver) {
		//this.part = part;
		this.driver = driver;
	}


	public void initPart(Part part) {

		logger.info("Suche: " + part.getPartNr());

		String targetStr = String.format("https://www.gaska.com.pl/towary/?szukajFrazy=%s", part.getPartNr());
		driver.get(targetStr);
		parseSearchResult(part, driver.getPageSource());
	}

	private void parseSearchResult(Part part, String responseStr) {
		logger.info("Parse: " + part.getPartNr());

		Document doc = Jsoup.parse(responseStr);
		logger.debug(doc.toString());
		for (int i = 0; i <= 9; i++){
			Elements rows = doc.select(String.format("tr[id=ContentPlaceHolder1_tTowary_gvTowary_DXDataRow%s]", i));
			if(rows.size() < 1) {
				break;
			} else{
				Element row = rows.first();
				Document produktdetails = getProductDetails(row);
				String partNr = getPartNr(row);

				Item otherItem = new Item(partNr);
				otherItem.setName(getPartName(row));
				otherItem.setPriceNetto(getPreisNetto(produktdetails));
				otherItem.setVerfuegbarkeit(getVerfuegbarkeit(produktdetails));
				otherItem.setOemNummern(getOemNummer(part, produktdetails));
				part.addOtherItem(otherItem);
			}
		}
		logger.info(part.toString());
	}

	private Document getProductDetails(Element row){
		Elements cols = row.select("td:nth-child(2)");
		String url = cols.first().child(0).attr("href").replace("..", "");
		driver.get("https://www.gaska.com.pl" + url);
		String responseStr = driver.getPageSource();
		return Jsoup.parse(responseStr);
	}

	private String getPartNr(Element row) {
		Elements cols = row.select("td:nth-child(3)");
		return cols.first().text();
	}

	private String getPartName(Element row) {
		Elements cols = row.select("td:nth-child(2)");
		return cols.first().text();
	}

	private String getPreisNetto(Document productDetails){
		Elements rows =  productDetails.select("div[id=ContentPlaceHolder1_pProdukt_divCenaNetto]")
				.select("span[id=ContentPlaceHolder1_pProdukt_lCena]");
		String preisNetto = rows.first().text();
		preisNetto = preisNetto.substring(0,preisNetto.indexOf(" "));
		return preisNetto;
	}

	private String getOemNummer(Part part, Document productDetails){
		StringBuilder ret = new StringBuilder();
		String oemNummern = "";
		Elements rows =  productDetails.select("span[id=ContentPlaceHolder1_pProdukt_lNumeryZamienne]");
		if(rows.size() > 0) oemNummern = rows.first().text();
		String[] splitted = oemNummern.split("(,|\\s)");

		for (String oemNummer : splitted){
			if (oemNummer.contains(part.getPartNr())){
				ret.append(oemNummer.trim());
				ret.append(",");
			}
		}
		return ret.toString();
	}

	private String getVerfuegbarkeit(Document productDetails){
		Elements rows = productDetails.select("span[id=ContentPlaceHolder1_pProdukt_lDostepnosc]");
		String verfuegbarkeit = rows.first().text();
		if("vorhanden".equals(verfuegbarkeit)){
			verfuegbarkeit = "+";
		}
		return verfuegbarkeit;
	}
}
