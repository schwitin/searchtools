package de.impl.orgatop;

import de.api.Part;
import de.impl.CsvImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(final String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);
        final File inputFile = new File("input.csv");
        if (!inputFile.exists()) {
            logger.error("Datei {} existiert nicht.", inputFile.getAbsolutePath());
            waitForEnterBeforeExit();
            return;
        }

        final File outputFile = new File("output.csv");

        try (final SearchServiceImpl service = new SearchServiceImpl()) {
            final CsvImporter csvImporter = new CsvImporter();
            final List<Part> parts = csvImporter.importCsv(inputFile);
            service.authenticate();
            service.initParts(parts);
            service.render(Files.newOutputStream(outputFile.toPath()), parts);
        } catch (final Exception e) {
            logger.error("Fehler :( ", e);
        } finally {
            waitForEnterBeforeExit();
        }

    }

    private static void waitForEnterBeforeExit() {
        Scanner s = new Scanner(System.in);
        System.out.println("Bitte 2 Mal ENTER eingeben um das Programm zu beenden.");
        s.nextLine();
    }

}
