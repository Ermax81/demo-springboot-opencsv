package com.example.demospringbootopencsv;

import com.opencsv.*;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.opencsv.ICSVWriter.*;
import static com.opencsv.ICSVWriter.DEFAULT_LINE_END;

@Slf4j
public class OpenCsvTools {

    public static <T> File exportToFile(List<T> items, String fileName, Class classExport) {
        File outputFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1);
            CSVWriter writer = new CSVWriter(osw, ';', NO_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
            if (!items.isEmpty()) {
                prepareExportAndWrite(new StatefulBeanToCsvBuilder<T>(writer), items);
            } else {
                List<String[]> headerLst = new ArrayList<>();
                headerLst.add(getHeaderForFile(classExport));
                writer.writeAll(headerLst);
            }
            writer.flush(); // mandatory to write in the file
        } catch (FileNotFoundException e) { // FileOutputStream
            log.error("OpenCsvTools exportToFile FileNotFoundException for FileOutputStream: " + e.getMessage());
        } catch (IOException e) { // flush
            log.error("OpenCsvTools exportToFile IOException for flush: " + e.getMessage());
        }
        return outputFile;
    }

    public static <T> void prepareExportAndWrite(StatefulBeanToCsvBuilder<T> builder, List<T> items) {

        Class<T> theClass = (Class<T>) items.get(0).getClass();

        try {
            CustomMappingStrategy<T> strategy = new CustomMappingStrategy<>();
            strategy.setType(theClass);
            StatefulBeanToCsv<T> beanToCsv = builder.withQuotechar(NO_QUOTE_CHARACTER)
                    .withEscapechar(DEFAULT_ESCAPE_CHARACTER).withSeparator(';').withLineEnd(DEFAULT_LINE_END)
                    // .withOrderedResults(true)
                    // .withMappingStrategy(new AnnotationStrategy<>(User.class))
                    .withMappingStrategy(strategy).build();

            beanToCsv.write(items);
        } catch (CsvDataTypeMismatchException e) {
            log.error("OpenCsvTools prepareExportAndWrite CsvDataTypeMismatchException: " + e.getMessage());
        } catch (CsvRequiredFieldEmptyException e) {
            log.error("OpenCsvTools prepareExportAndWrite CsvRequiredFieldEmptyException: " + e.getMessage());
        }
    }

    public static <T> String[] getHeaderForFile(Class classExport) {
        CustomMappingStrategy<T> strategy = new CustomMappingStrategy<>();
        strategy.setType(classExport);
        try {
            return (strategy.generateHeader((T) classExport.getDeclaredConstructor().newInstance()));
        } catch (CsvRequiredFieldEmptyException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("OpenCsvTools getHeaderForFile: "+e.getMessage());
        }
        return new String[0];
    }

    public static CSVParser getParser(char separator) {
        return new CSVParserBuilder()
                .withSeparator(separator)
                .withEscapeChar(DEFAULT_ESCAPE_CHARACTER)
                .withQuoteChar(NO_QUOTE_CHARACTER)
                .build();
    }

    public static CSVParser getParser() {
        return getParser(';');
    }

    // Check if column size for each row is equal to column size of header
    // TODO create output file with bad content
    public static boolean checkColumnSizeFromFile(String pathFile) {
        boolean output = true;

        File fileToCheck = new File(pathFile);

        HashMap<Integer,LineInFile> contentFile = new HashMap<>();
        HashMap<Integer,LineInFile> badContentFile = new HashMap<>();
        int nbLine = 0;
        int nbColumn = 0;
        String[] line;

        CSVParser parser = OpenCsvTools.getParser();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileToCheck))
                .withCSVParser(parser)
                .build()
        ) {

            while ( (line = reader.readNext()) != null ) {
                if (nbLine == 0) { // 1ere ligne
                    nbColumn = line.length; // on prend le nombre de colonne de l'entête
                }
                if (nbColumn != line.length) { //bad content
                    badContentFile.put(nbLine, new LineInFile(line, line.length));
                    output = false;
                } else { // nbColumn ok
                    contentFile.put(nbLine, new LineInFile(line, line.length));
                }
                nbLine++;
            }

        } catch (FileNotFoundException e) { //FileReader
            throw new RuntimeException(e);
        } catch (IOException e) { //FileReader
            throw new RuntimeException(e);
        } catch (CsvValidationException e) { //readNext()
            throw new RuntimeException(e);
        }

        log.info("checkColumnSizeFromFile - Nb_lines:"+nbLine);
        log.info("checkColumnSizeFromFile - Nb_column:"+nbColumn);
        log.info("checkColumnSizeFromFile - Size_content:"+contentFile.size()+" (must be equal to Nb_lines if no error)");
        log.info("checkColumnSizeFromFile - Size_error:"+badContentFile.size());

        return output;
    }

}
