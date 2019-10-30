package de.gaska.impl;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gaska.api.Item;
import de.gaska.api.Part;

public class WorkerThread implements Runnable {

	Logger logger = LoggerFactory.getLogger(WorkerThread.class);

	CloseableHttpClient httpclient;
	HttpContext context;

	HttpHost target ;

	String message = "";
	Part part;

	public WorkerThread(CloseableHttpClient httpclient, HttpHost target, Part part, String message) {
		this.message = message;
		this.part = part;
		this.httpclient = httpclient;
		this.target = target;
		this.context = HttpClientContext.create();
	}

	@Override
	public void run() {
		logInfo("Start processing " + part.getPartNr() + " " + message);
		initPart();
		logInfo("End  processing " + part.getPartNr());
	}

	void initPart() {

		logInfo("Suche: " + this.part.getPartNr());

		String targetStr = String.format("/towary/?szukajFrazy=%s", this.part.getPartNr());

		HttpGet request = new HttpGet(targetStr);
		try (CloseableHttpResponse response = httpclient.execute(target, request, context)) {
			String responseStr = EntityUtils.toString(response.getEntity());
			parseSearchResult(responseStr);
		} catch (ParseException | IOException e) {
	        logError("Fehler beim initialisieren von " + this.part.getPartNr(), e);
        } 
	}

	Part parseSearchResult(String responseStr) {
		logInfo("Parse: " + this.part.getPartNr());

		Document doc = Jsoup.parse(responseStr);
		logDebug(doc.toString());
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
				otherItem.setOemNummern(getOemNummer(produktdetails));
				part.addOtherItem(otherItem);
			}
		}
		logInfo(part.toString());
		return part;
	}

	private Document getProductDetails(Element row){
		Document doc = null;
		Elements cols = row.select("td:nth-child(2)");
		String url = cols.first().child(0).attr("href").replace("..", "");

		HttpGet request = new HttpGet(url);
		try (CloseableHttpResponse response = httpclient.execute(target, request)) {
			String responseStr = EntityUtils.toString(response.getEntity());
			doc =  Jsoup.parse(responseStr);
		} catch (ParseException | IOException e) {
			logError("Fehler beim Abfragen der Produktdetails", e);
		}
		return doc;
	}

	private String getPartNr(Element row) {
		Elements cols = row.select("td:nth-child(3)");
		String text = cols.first().text();
		return text;
	}

	private String getPartName(Element row) {
		Elements cols = row.select("td:nth-child(2)");
		String text = cols.first().text();
		return text;
	}

	private String getPreisNetto(Document productDetails){
		Elements rows =  productDetails.select("div[id=ContentPlaceHolder1_pProdukt_divCenaNetto]")
				.select("span[id=ContentPlaceHolder1_pProdukt_lCena]");
		String preisNetto = rows.first().text();
		preisNetto = preisNetto.substring(0,preisNetto.indexOf(" "));
		return preisNetto;
	}

	private String getOemNummer(Document productDetails){
		StringBuffer ret = new StringBuffer();
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

	void logInfo(String message) {
		logger.info(Thread.currentThread().getName() + " " + message);
	}

	void logDebug(String message) {
		logger.debug(Thread.currentThread().getName() + " " + message);
	}

	void logError(String message, Exception e) {
		logger.error(Thread.currentThread().getName() + " " + message, e);
	}

	@Override
	public String toString() {
		return this.part.getPartNr();
	}
}
