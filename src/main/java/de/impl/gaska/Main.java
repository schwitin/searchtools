package de.impl.gaska;

import de.api.Part;
import de.impl.CsvImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Main {

    static String[] inputFiles = new String[]{"input_claas.csv", "input_andere.csv"};


    public static void main(final String[] args) throws Exception {
        final Logger logger = LoggerFactory.getLogger(Main.class);

        for (final String inputFileName : inputFiles) {
            final File inputFile = new File(inputFileName);
            if (!inputFile.exists()) {
                logger.error("Datei {} existiert nicht.", inputFile.getAbsolutePath());
                return;
            }

            final File outputFile = new File(inputFileName.replace("input_", "output_"));
            if (outputFile.exists()) {
                logger.error("Datei {} existiert bereits.%n", outputFile.getAbsolutePath());
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
}
