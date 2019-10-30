package de.gaska.impl;

import de.gaska.api.Part;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class WorkerThreadTest {

    SearchServiceImpl service;
    WorkerThread workerThread;

    @Before
    public void setUp() throws Exception {
        service = new SearchServiceImpl("IMPEXTRADING", "Sergey Schwitin", "RYB1318");
        service.authenticate();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseSearchResult() throws IOException {
       CloseableHttpClient client =  service.getHttpclient();
       String partNr = "1003143.01";


        HttpGet request = new HttpGet("/towary/?szukajFrazy=" + partNr);
        CloseableHttpResponse response = client.execute(service.getTarget(), request);
		String responseStr = EntityUtils.toString(response.getEntity());


		//logger.info(responseStr);
        Part part = new Part("1", partNr);

        workerThread = new WorkerThread(client, service.getTarget(), part, partNr);
        workerThread.parseSearchResult(responseStr);
        //System.out.println(part);

    }
}