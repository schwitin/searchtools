package de.impl.orgatop;

import de.api.Part;
import de.impl.CsvImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Main {

    public static void main(final String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);
        final File inputFile = new File("input.csv");
        if (!inputFile.exists()) {
            logger.error("Datei {} existiert nicht.", inputFile.getAbsolutePath());
            return;
        }

        final File outputFile = new File("output.csv");
        if (outputFile.exists()) {
            logger.error("Datei {} existiert bereits.", outputFile.getAbsolutePath());
            return;
        }

        try {
            final CsvImporter csvImporter = new CsvImporter();
            final List<Part> parts = csvImporter.importCsv(inputFile);
            final SearchServiceImpl service = new SearchServiceImpl();
            service.authenticate();
            service.initParts(parts);
            service.printParts(new FileOutputStream(outputFile), parts);
        } catch (final Exception e) {
            logger.error("Fehler :( ", e);
        }
    }
}
