package de.gaska.impl;

import de.gaska.api.Item;
import de.gaska.api.Part;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchServiceImpl {

	Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
	CloseableHttpClient httpclient;
	HttpHost target = new HttpHost("www.gaska.com.pl", 80, "http");
	String login;
	String password;
	String partner;

	public SearchServiceImpl(String partner, String login, String password) {
		this.login = login;
		this.password = password;
		this.partner = partner;

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setDefaultMaxPerRoute(10);
		cm.setMaxPerRoute(new HttpRoute(target), 10);
		cm.setMaxTotal(10);
		this.httpclient = HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setConnectionManager(cm).build();
	}

	public void initParts(List<Part> parts) throws Exception {

		int i = 0;
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (Part part : parts) {
			String partNr = part.getPartNr();
			if ("?".equals(partNr)) {
				continue;
			}
			String message = String.format("Verarbeite %s: %s/%s", part.getPartNr(), ++i, parts.size());
			Runnable worker = new WorkerThread(httpclient, this.target, part, message);
			//worker.run();
			executor.execute(worker);
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
			Thread.sleep(10);
		}

		logger.info("Finished all threads");
	}
	
	public void printParts(OutputStream outputStream, List<Part> parts){
		try (PrintStream printStream = new PrintStream(outputStream)) {
			printStream.println(getHeader(parts));
			for (Part part : parts) {
				printStream.println(part);
			}
		}
	}

	
	public CloseableHttpClient getHttpclient() {
		return httpclient;
	}

	public HttpHost getTarget() { return  target; }

	public void authenticate() throws Exception {

		// Go to the login page
		HttpGet request = new HttpGet("/zaloguj/partner");
		CloseableHttpResponse response = httpclient.execute(target, request);
		String responseStr = EntityUtils.toString(response.getEntity());
		//logger.info(responseStr);

		// Send a login request
		// The parameters and values are copied from browser developer tools
		HttpEntity entity = MultipartEntityBuilder
				.create()
				.addTextBody("__EVENTTARGET", "")
				.addTextBody("__EVENTARGUMENT", "")
				.addTextBody("__VIEWSTATE", "/wEPDwUJOTA3MDgyMTA1D2QWAmYPZBYCAgMPFgIeB2VuY3R5cGUFE211bHRpcGFydC9mb3JtLWRhdGEWDAICDw8WAh4HVmlzaWJsZWhkZAIDD2QWBGYPDxYCHwFoZGQCAQ8PFgIfAWhkZAILD2QWDmYPZBYGAgMPZBYCAgIPZBYCAgEPZBYCZg9kFgJmD2QWAmYPZBYCAgMPZBYEAgEPFgIfAWhkAgMPZBYEAgMPDxYCHgRUZXh0BQEwZGQCBw8PFgIfAgUIMCwwMCBQTE5kZAINDw8WAh8BaGRkAg8PDxYCHwFoZGQCAg9kFgICAg9kFgICAQ9kFgJmD2QWAmYPZBYCZg9kFgICAw9kFgQCAQ8WAh8BaGQCAw9kFgQCAw8PFgIfAgUBMGRkAgcPDxYCHwIFCDAsMDAgUExOZGQCBA8PFgIfAWhkZAIFDw8WAh8BaGRkAgYPDxYCHwFoZGQCCA9kFgICBQ8WBB4IZGF0YS1zcmMFDC9uYXN6ZS1tYXJraR4KZGF0YS1kZWxheQUFMTUwMDBkAgkPDxYCHwFoZGQCDA8WAh4JaW5uZXJodG1sBQlMb2dvd2FuaWVkAg0PZBYCAgEPZBYCAgcPZBYCZg9kFgJmD2QWAmYPZBYCAgEPZBYGAgEPZBYGAgEPZBYCZg9kFgICAQ9kFgQCDw8QDxYCHgdDaGVja2VkaGRkZGQCGQ88KwAEAQAPFgIeBVZhbHVlBQwvcmVqZXN0cmFjamFkZAIDD2QWAmYPDxYCHwFnZGQCBQ9kFgJmDw8WAh8BZ2RkAgMPZBYEAgEPPCsABAEADxYCHwFoZGQCAw8WAh8FBZ4KPHN0cm9uZz48c3BhbiBzdHlsZT0iZm9udC1zaXplOiAxMHB0OyI+SmXFm2xpIFBhxYRzdHdhIGZpcm1hIG5pZSBwb3NpYWRhIGRhbnljaCBkb3N0xJlwb3d5Y2ggZG8gQjJCLCBwcm9zaW15IG8ga29udGFrdCB6IG5hc3p5bSBkemlhxYJlbSBoYW5kbG93eW0gdyBjZWx1IHV0d29yemVuaWEga29udGEuIFphcHJhc3phbXkgZG8gd3Nww7PFgnByYWN5Ljwvc3Bhbj48L3N0cm9uZz48ZGl2IHN0eWxlPSJ0ZXh0LWFsaWduOiBsZWZ0OyI+PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogMTBwdDsiPjxzdHJvbmc+PGJyLz48L3N0cm9uZz48L3NwYW4+PC9kaXY+PGRpdiBzdHlsZT0idGV4dC1hbGlnbjogbGVmdDsiPjxzcGFuIHN0eWxlPSJmb250LXNpemU6IDEwcHQ7Ij5JbmZvcm11amVteSwgacW8IG51bWVyeSBvcnlnaW5hbG5lIHXFvHl0byBqZWR5bmllIHcgY2VsYWNoIGlkZW50eWZpa2FjeWpueWNoIHRvd2FydSBpIG5pZSBzxIUgd3NrYXphbmllbSBwcm9kdWNlbnRhLiBPZmVyb3dhbmUgdG93YXJ5IHPEhSB6YW1pZW5uaWthbWkuIEZpcm1hIEfEhXNrYSBvZmVydWplIHRha8W8ZSBvcnlnaW5hbG5lIGN6xJnFm2NpIG9kIHd5YnJhbnljaCBwcm9kdWNlbnTDs3cgcG9kIHphbcOzd2llbmllIGx1YiB6IG1hZ2F6eW51LiBDesSZxZtjaSB0ZSBzxIUgd3lyYcW6bmllIG9waXNhbmUgamFrbyBjesSZxZvEhyBvcnlnaW5hbG5hIOKAnk9yaWdpbmFs4oCdLjxzdHJvbmc+PGJyLz48L3N0cm9uZz48L3NwYW4+PC9kaXY+PGRpdiBzdHlsZT0idGV4dC1hbGlnbjogY2VudGVyOyI+PGJyLz48L2Rpdj48ZGl2IHN0eWxlPSJ0ZXh0LWFsaWduOiBjZW50ZXI7Ij48aW1nIHNyYz0iL2ltZy9jbXMvdG0ybGpzZmEuanBnIiBzdHlsZT0id2lkdGg6IDM2MHB4OyBoZWlnaHQ6IDIwMHB4OyIvPjxici8+PC9kaXY+Cgo8c2NyaXB0PgogIChmdW5jdGlvbihpLHMsbyxnLHIsYSxtKXtpWydHb29nbGVBbmFseXRpY3NPYmplY3QnXT1yO2lbcl09aVtyXXx8ZnVuY3Rpb24oKXsKICAoaVtyXS5xPWlbcl0ucXx8W10pLnB1c2goYXJndW1lbnRzKX0saVtyXS5sPTEqbmV3IERhdGUoKTthPXMuY3JlYXRlRWxlbWVudChvKSwKICBtPXMuZ2V0RWxlbWVudHNCeVRhZ05hbWUobylbMF07YS5hc3luYz0xO2Euc3JjPWc7bS5wYXJlbnROb2RlLmluc2VydEJlZm9yZShhLG0pCiAgfSkod2luZG93LGRvY3VtZW50LCdzY3JpcHQnLCcvL3d3dy5nb29nbGUtYW5hbHl0aWNzLmNvbS9hbmFseXRpY3MuanMnLCdnYScpOwoKICBnYSgnY3JlYXRlJywgJ1VBLTExNTA2OTk4LTEnLCAnYXV0bycpOwogIGdhKCdzZW5kJywgJ3BhZ2V2aWV3Jyk7Cgo8L3NjcmlwdD5kAgUPZBYEAgEPPCsABAEADxYCHwFoZGQCAw8WAh8FBaUKPGRpdiBzdHlsZT0idGV4dC1hbGlnbjogbGVmdDsiPjxzcGFuIHN0eWxlPSJmb250LXNpemU6IDEwcHQ7Ij48c3Ryb25nPk9iZWNuYSBzdHJvbmEgbG9nb3dhbmlhIGplc3QgcHJ6ZXpuYWN6b25hIHR5bGtvIGRsYSBrbGllbnTDs3cgZGV0YWxpY3pueWNoLiBKZcWbbGkgamVzdGXFmyBrbGllbnRlbSBodXJ0b3d5bSBwb3dpbmllbmXFmyBwcnplxYLEhWN6ecSHIHNpxJkgbmEgemFrxYJhZGvEmTogIkh1cnRvd2EgcGxhdGZvcm1hIEIyQiIgem5hamR1asSFY8SFIHNpxJkgcG93ecW8ZWo8L3N0cm9uZz48L3NwYW4+PC9kaXY+PGRpdiBzdHlsZT0idGV4dC1hbGlnbjogbGVmdDsiPjxzcGFuIHN0eWxlPSJmb250LXNpemU6IDEwcHQ7Ij48YnIvPjwvc3Bhbj48L2Rpdj48ZGl2IHN0eWxlPSJ0ZXh0LWFsaWduOiBsZWZ0OyI+PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogMTBwdDsiPkluZm9ybXVqZW15LCBpxbwgbnVtZXJ5IG9yeWdpbmFsbmUgdcW8eXRvIGplZHluaWUgdyBjZWxhY2ggaWRlbnR5ZmlrYWN5am55Y2ggdG93YXJ1IGkgbmllIHPEhSB3c2themFuaWVtIHByb2R1Y2VudGEuIE9mZXJvd2FuZSB0b3dhcnkgc8SFIHphbWllbm5pa2FtaS4gRmlybWEgR8SFc2thIG9mZXJ1amUgdGFrxbxlIG9yeWdpbmFsbmUgY3rEmcWbY2kgb2Qgd3licmFueWNoIHByb2R1Y2VudMOzdyBwb2QgemFtw7N3aWVuaWUgbHViIHogbWFnYXp5bnUuIEN6xJnFm2NpIHRlIHPEhSB3eXJhxbpuaWUgb3Bpc2FuZSBqYWtvIGN6xJnFm8SHIG9yeWdpbmFsbmEg4oCeT3JpZ2luYWzigJ08YnIvPjwvc3Bhbj48L2Rpdj48ZGl2IHN0eWxlPSJ0ZXh0LWFsaWduOiBsZWZ0OyI+PGJyLz48L2Rpdj48ZGl2IHN0eWxlPSJ0ZXh0LWFsaWduOiBsZWZ0OyI+PGltZyBzcmM9Ii9pbWcvY21zLzNyMm5qYTFiLmpwZyIvPjxici8+PC9kaXY+CgoKPHNjcmlwdD4KICAoZnVuY3Rpb24oaSxzLG8sZyxyLGEsbSl7aVsnR29vZ2xlQW5hbHl0aWNzT2JqZWN0J109cjtpW3JdPWlbcl18fGZ1bmN0aW9uKCl7CiAgKGlbcl0ucT1pW3JdLnF8fFtdKS5wdXNoKGFyZ3VtZW50cyl9LGlbcl0ubD0xKm5ldyBEYXRlKCk7YT1zLmNyZWF0ZUVsZW1lbnQobyksCiAgbT1zLmdldEVsZW1lbnRzQnlUYWdOYW1lKG8pWzBdO2EuYXN5bmM9MTthLnNyYz1nO20ucGFyZW50Tm9kZS5pbnNlcnRCZWZvcmUoYSxtKQogIH0pKHdpbmRvdyxkb2N1bWVudCwnc2NyaXB0JywnLy93d3cuZ29vZ2xlLWFuYWx5dGljcy5jb20vYW5hbHl0aWNzLmpzJywnZ2EnKTsKCiAgZ2EoJ2NyZWF0ZScsICdVQS0xMTUwNjk5OC0xJywgJ2F1dG8nKTsKICBnYSgnc2VuZCcsICdwYWdldmlldycpOwoKPC9zY3JpcHQ+ZAISDxYCHgdvbmNsaWNrBR5qYXZhc2NyaXB0OndpbmRvdy5sb2NhdGlvbj0nLydkGAEFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYVBR9jdGwwMCRjdGwwNiRsc0xvZ2luU3RhdHVzJGN0bDAxBR9jdGwwMCRjdGwwNiRsc0xvZ2luU3RhdHVzJGN0bDAzBRVjdGwwMCRjdGwwNyRqZXotYnRuLTIFFWN0bDAwJGN0bDA3JGplei1idG4tMwUVY3RsMDAkY3RsMDckamV6LWJ0bi00BRVjdGwwMCRjdGwwNyRqZXotYnRuLTUFFWN0bDAwJGN0bDA3JGplei1idG4tNgUVY3RsMDAkY3RsMDckamV6LWJ0bi03BRVjdGwwMCRjdGwwNyRqZXotYnRuLTgFF2N0bDAwJGN0bDA5JGN0bDAwJG1NZW51BRpjdGwwMCRjdGwwOSRUb3BNZW51MSRtTWVudQUkY3RsMDAkc3p1a2FqUG9GcmF6aWUkYlN6dWthalBvRnJhemllBStjdGwwMCRzenVrYWpQb0ZyYXppZSRiU3p1a2FqUG9GcmF6aWVXeWN6eXNjBSxjdGwwMCRjdGwxMCRVc2VyQ29udHJvbDEkbHNMb2dpblN0YXR1cyRjdGwwMQUsY3RsMDAkY3RsMTAkVXNlckNvbnRyb2wxJGxzTG9naW5TdGF0dXMkY3RsMDMFGWN0bDAwJGN0bDEwJGN0bDA5JHBjVXdhZ2kFKGN0bDAwJENvbnRlbnRQbGFjZUhvbGRlcjEkY3RsMDAkcmJLbGllbnQFKWN0bDAwJENvbnRlbnRQbGFjZUhvbGRlcjEkY3RsMDAkcmJQYXJ0bmVyBTljdGwwMCRDb250ZW50UGxhY2VIb2xkZXIxJGN0bDAwJGN0bDAwJGxMb2dpbiRMb2dpbkJ1dHRvbjIFUWN0bDAwJENvbnRlbnRQbGFjZUhvbGRlcjEkY3RsMDAkY3RsMDAkcHJQYXJ0bmVyJFVzZXJOYW1lQ29udGFpbmVySUQkYkhhc2xvUGFydG5lcgVPY3RsMDAkQ29udGVudFBsYWNlSG9sZGVyMSRjdGwwMCRjdGwwMCRwcktsaWVudCRVc2VyTmFtZUNvbnRhaW5lcklEJGJIYXNsb0tsaWVudBdg4pb13xUl9hn46H2q0+rbstDisoAhMQw6+xBfbTmy")
				.addTextBody("__VIEWSTATEGENERATOR", "1806D926")
				.addTextBody("__EVENTVALIDATION", "/wEdAAtjFcHkkAS2lOJImJkpA5QEq6BjglSX20JNkeypyKqJlaRS749lkD/GBkaugrqWgnwTNF4ilk8Da4vXR04WT+HRaTpQFLkSqh4zbYQIIJoR8XQgaGNMJFyiCGM8WzUkXwUVxHNpDDXvolG7/7h27tMSHzs6Bvy7vBTJidVfpxCnoNHwvYjNTxa0QlViPSLoN3uOPS0cRWHQIfNuHSks9nyc2HZ6zHekaQoOFRRq9slcBpP3UV62LB22hPAzjKTaGizsLsg5nJKUfVTSqc/LHQt1")
				.addTextBody("szukajPoFrazie_tbSzukajPoFrazie_Raw", "")
				.addTextBody("ctl00$szukajPoFrazie$tbSzukajPoFrazie", "Szukaj towaru...")
				.addTextBody("ctl00$szukajPoFrazie$ctl01", "I")
				.addTextBody("ctl00$szukajPoFrazie$stszukajPoFrazie", "")
				.addTextBody("rbKlient_CH", "0")
				.addTextBody("rbPartner_CH", "1")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$lLogin$Akronim", partner)
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$lLogin$Akronim$CVS", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$lLogin$UserName", login)
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$lLogin$UserName$CVS", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$lLogin$Password", password)
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$lLogin$Password$CVS", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$lLogin$LoginButton2", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$prPartner$UserNameContainerID$tbAkronimPartnerPR", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$prPartner$UserNameContainerID$tbAkronimPartnerPR$CVS", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$prPartner$UserNameContainerID$tbUzytkwnikPartnerPR", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$prPartner$UserNameContainerID$tbUzytkwnikPartnerPR$CVS", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$prKlient$UserNameContainerID$tbUserKlientPR", "")
				.addTextBody("ctl00$ContentPlaceHolder1$ctl00$ctl00$prKlient$UserNameContainerID$tbUserKlientPR$CVS", "")
				.addTextBody("DXScript", "1_144,1_80,1_136,1_129,1_134,1_120,1_98,1_105,1_94,1_91,1_79,1_77,1_127,1_104,1_119,1_111")
				.build();

		HttpPost pRequest = new HttpPost("/zaloguj/partner");
		pRequest.setEntity(entity);
		response = httpclient.execute(target, pRequest);
		//responseStr = EntityUtils.toString(response.getEntity());
		//logger.info(responseStr);
	}


	private String getHeader(List<Part> parts){

		return Item.getHeader();
		/*
		
		int otherPartsSizeMax = 0;
		for (Part part : parts) {
	        int size = part.getOtherItems().size();
	        if (size >  otherPartsSizeMax){
	        	otherPartsSizeMax = size;
	        }
	    }
		
		StringBuilder header = new StringBuilder();
		int headersCount  = otherPartsSizeMax +2;
		for (int i = 0; i < headersCount; i++) {
	        header.append(Item.getHeader());
	        header.append(";");
	    }
		
		return header.toString();
		*/
	}

}
