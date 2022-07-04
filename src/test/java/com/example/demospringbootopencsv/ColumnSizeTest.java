package com.example.demospringbootopencsv;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class ColumnSizeTest {

    @Test
    public void checkColumSizeTest() {

        String pathFile = "src/test/resources/tmp.csv";

        File fileToCheck = new File(pathFile);
        Assertions.assertNotNull(fileToCheck);

        boolean result = OpenCsvTools.checkColumnSizeFromFile(pathFile);
        Assertions.assertFalse(result);
    }

}
