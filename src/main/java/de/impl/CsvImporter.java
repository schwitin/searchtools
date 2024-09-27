package de.impl;

import de.api.Part;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvImporter {

    public List<Part> importCsv(final File inputFile) throws IOException {
        final List<Part> parts = new ArrayList<>();
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
            final String partNr = record.get(0).trim();
            final String partName = record.get(1).trim();
            final String partBedarfsmaenge = record.get(2).trim();
            final de.api.Part part = new Part("" + id++, partNr, partName, partBedarfsmaenge);

            parts.add(part);
        }
        return parts;
    }
}
