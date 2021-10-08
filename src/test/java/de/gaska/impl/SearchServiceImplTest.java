package de.gaska.impl;

import de.gaska.api.Part;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class SearchServiceImplTest {
    SearchServiceImpl unitUnderTest;

    @Before
    public void setUp() throws Exception {
        System.setProperty("settings", "settings.ini");
        unitUnderTest = new SearchServiceImpl();
    }

    @After
    public void tearDown() throws Exception {
        unitUnderTest.close();
    }

    @Test
    public void initParts() {
        final List<Part> parts = new ArrayList<>();
        final Part part = new Part("myPartId", "1003143.01", "My_Name", "10");
        // Part part = new Part("myPartId", "85030");
        parts.add(part);
        unitUnderTest.authenticate();
        unitUnderTest.initParts(parts);
        assertNotNull(part);
        assertEquals(1, part.getOtherItems().size());
    }
}