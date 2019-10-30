package de.gaska.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SearchServiceImplTest {
    SearchServiceImpl service;

    @Before
    public void setUp() throws Exception {
        service = new SearchServiceImpl("IMPEXTRADING", "Sergey Schwitin", "RYB1318");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void initParts() {
    }

    @Test
    public void printParts() {
    }

    @Test
    public void getHttpclient() {
    }

    @Test
    public void authenticate() throws Exception{
        service.authenticate();
    }
}