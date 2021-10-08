package de.gaska;

import de.gaska.api.Part;
import de.gaska.impl.SearchServiceImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static String[] inputFiles = new String[]{"input_claas.csv", "input_andere.csv"};


    public static void main(final String[] args) throws Exception {
        final Logger logger = LoggerFactory.getLogger(Main.class);

        for (final String inputFileName : inputFiles) {
            final File inputFile = new File(inputFileName);
            if (false == inputFile.exists()) {
                System.err.println(String.format("Datei %s existiert nicht.", inputFile.getAbsolutePath()));
                return;
            }


            final File outputFile = new File(inputFileName.replace("input_", "output_"));
            if (true == outputFile.exists()) {
                System.err.println(String.format("Datei %s existiert bereits.", outputFile.getAbsolutePath()));
                //return;
            }

            final List<Part> parts = new ArrayList<Part>();
            final Reader in = new FileReader(inputFile);
            final Iterable<CSVRecord> records = CSVFormat.RFC4180
                    .builder()
                    .setDelimiter(";")
                    .build()
                    .parse(in);
            int id = 0;
            for (final CSVRecord record : records) {
                if (id == 0) {
                    id++;
                    continue;
                }
                String partNr = record.get(0);
                if (inputFileName.contains("claas")) {
                    partNr = partNr.substring(0, partNr.length() - 1);
                }
                final String partName = record.get(1);
                final String partBedarfsmaenge = record.get(2);
                parts.add(new Part("" + id++, partNr, partName, partBedarfsmaenge));
            }
            try {
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
