package com.example.demospringbootopencsv;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

}
