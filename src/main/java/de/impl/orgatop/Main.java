package de.impl.orgatop;

import de.api.Part;
import de.impl.CsvImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(final String[] args) throws IOException {
        final Logger logger = LoggerFactory.getLogger(Main.class);
        final File inputFile = new File("input.csv");
        if (!inputFile.exists()) {
            logger.error("Datei {} existiert nicht.", inputFile.getAbsolutePath());
            waitForEnterBeforeExit();
            return;
        }

        final File outputFile = new File("output.csv");

        try {
            final CsvImporter csvImporter = new CsvImporter();
            final List<Part> parts = csvImporter.importCsv(inputFile);
            final SearchServiceImpl service = new SearchServiceImpl();
            service.authenticate();
            service.initParts(parts);
            service.printParts(new FileOutputStream(outputFile), parts);
        } catch (final Exception e) {
            logger.error("Fehler :( ", e);
        } finally {
            waitForEnterBeforeExit();
        }

    }

    private static void waitForEnterBeforeExit() throws IOException {
        System.out.println("Bitte 2 Mal ENTER eingeben um das Programm zu beenden.");
        System.in.read();
    }

}
