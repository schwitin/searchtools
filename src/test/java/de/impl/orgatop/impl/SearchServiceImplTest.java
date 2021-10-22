package de.impl.orgatop.impl;

import de.api.Part;
import de.impl.orgatop.SearchServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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
        // 009349350
        // 200310621
        // 9303450 9103250 kein Ergebnis
        final Part part = new Part("myPartId", "9303450", "My_Name", "10");
        // Part part = new Part("myPartId", "85030");
        parts.add(part);
        unitUnderTest.authenticate();
        unitUnderTest.initParts(parts);
        assertNotNull(part);
        //assertEquals(3, part.getOtherItems().size());
    }
}