package de.impl.gaska.impl;

import de.api.Part;
import de.impl.gaska.SearchServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
    public void tearDown() {
        unitUnderTest.close();
    }

    @Test
    public void initParts() throws IOException {
        final List<Part> parts = new ArrayList<>();
        final Part part = new Part("myPartId", "2118260", "My_Name", "10");
        // Part part = new Part("myPartId", "85030");
        parts.add(part);
        unitUnderTest.authenticate();
        unitUnderTest.initParts(parts);
        assertNotNull(part);
        assertEquals(2, part.getOtherItems().size());
    }
}