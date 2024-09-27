package de.impl.gaska;

import de.api.Part;
import de.impl.CsvImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Scanner;

public class Main {

    final static String[] inputFiles = new String[]{"input_claas.csv", "input_andere.csv"};


    public static void main(final String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);

        for (final String inputFileName : inputFiles) {
            final File inputFile = new File(inputFileName);
            if (!inputFile.exists()) {
                logger.error("Datei {} existiert nicht.", inputFile.getAbsolutePath());
                waitForEnterBeforeExit();
                return;
            }

            final File outputFile = new File(inputFileName.replace("input_", "output_"));

            try (final SearchServiceImpl service = new SearchServiceImpl()) {
                final CsvImporter csvImporter = new CsvImporter();
                final List<Part> parts = csvImporter.importCsv(inputFile);
                service.authenticate();
                service.initParts(parts);
                service.render(new FileOutputStream(outputFile), parts);
            } catch (final Exception e) {
                logger.error("Fehler :( ", e);
            }
        }
        waitForEnterBeforeExit();
    }

    private static void waitForEnterBeforeExit() {
        Scanner s = new Scanner(System.in);
        System.out.println("Bitte 2 Mal ENTER eingeben um das Programm zu beenden.");
        s.nextLine();
    }
}
