package com.example.demospringbootopencsv;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static com.opencsv.ICSVWriter.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.ICSVWriter.NO_QUOTE_CHARACTER;

@Slf4j
public class ParseStringTest {

    @Test
    void parseString() {

        //String line = "1;john;doe;";
        //String line = "1;john;doe;john.doe";
        String line = "1;john;doe;john.doe@nomail.com";

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .withEscapeChar(DEFAULT_ESCAPE_CHARACTER)
                .withQuoteChar(NO_QUOTE_CHARACTER)
                .build();

        try {
            String[] items = parser.parseLine(line);
            Assertions.assertNotNull(items);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ColumnPositionMappingStrategy<User> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(User.class);

        //Not usefull
//        String[] columns = new String[]{"id","firstName","lastName","email"};
//        strategy.setColumnMapping(columns);

        try (CSVReader reader = new CSVReaderBuilder(new StringReader(line))
             .withCSVParser(parser)
             .build()
        ) {
            CsvToBean csvToBean = new CsvToBeanBuilder<User>(reader)
                    .withSeparator(';')
                    .withOrderedResults(true)
                    .withMappingStrategy(strategy)
                    .withThrowExceptions(false) //retrieve Errors through  List<CsvExceptions>
                    .build();

            List<User> beans = csvToBean.parse();
            Assertions.assertNotNull(beans);
            List<CsvException> errors = csvToBean.getCapturedExceptions();
            Assertions.assertNotNull(errors);
            //errors.get(0).getLineNumber()
            //log.error(errors.get(0).getMessage());

//        } catch (RuntimeException e) { //avec .withThrowExceptions(true)
//
//            StringBuilder errorMessage = new StringBuilder();
//            errorMessage.append(e.getMessage()); //Error parsing CSV line: 1. [1,john,doe,]
//            errorMessage.append(" ");
//            errorMessage.append(e.getCause().getMessage()); //Field 'email' is mandatory but no value was provided.
//            log.error(errorMessage.toString());

        } catch (IOException e) {
            //e.printStackTrace();
        }


    }

}
