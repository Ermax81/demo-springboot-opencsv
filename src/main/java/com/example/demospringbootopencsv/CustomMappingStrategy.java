package com.example.demospringbootopencsv;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

/*Source: https://stackoverflow.com/questions/45203867/opencsv-how-to-create-csv-file-from-pojo-with-custom-column-headers-and-custom*/

/*Usefull to respect column order - write in csv file (OpenCSV)*/
public class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {

        super.setColumnMapping(new String[ getAnnotatedFields(bean)]);
        final int numColumns = getAnnotatedFields(bean);
        //final int totalFieldNum = findMaxFieldIndex();
        final int totalFieldNum = getFieldMap().values().size();

        //if (!isAnnotationDriven() || numColumns == -1) {
        if (numColumns == -1) {
            return super.generateHeader(bean);
        }

        String[] header = new String[numColumns];

        BeanField beanField;
        for (int i = 0; i <= totalFieldNum; i++) {
            beanField = findField(i);
            if (beanField!=null && isFieldAnnotated(beanField.getField())) {
                String columnHeaderName = extractHeaderName(beanField);
                header[i] = columnHeaderName;
            }
        }
        return header;
    }

    private int getAnnotatedFields(T bean) {
        return (int) Arrays.stream(FieldUtils.getAllFields(bean.getClass()))
                .filter(this::isFieldAnnotated)
                .count();
    }

    private boolean isFieldAnnotated(Field f) {
        return f.isAnnotationPresent(CsvBindByName.class) || f.isAnnotationPresent(CsvCustomBindByName.class) || f.isAnnotationPresent(CsvBindByPosition.class) ;
    }

    private String extractHeaderName(final BeanField beanField) {
        if (beanField == null || beanField.getField() == null) {
            return StringUtils.EMPTY;
        }

        Field field = beanField.getField();

        if (field.getDeclaredAnnotationsByType(CsvBindByName.class).length != 0) {
            final CsvBindByName bindByNameAnnotation = field.getDeclaredAnnotationsByType(CsvBindByName.class)[0];
            return bindByNameAnnotation.column();
        }

        if (field.getDeclaredAnnotationsByType(CsvCustomBindByName.class).length != 0) {
            final CsvCustomBindByName bindByNameAnnotation = field.getDeclaredAnnotationsByType(CsvCustomBindByName.class)[0];
            return bindByNameAnnotation.column();
        }

        return StringUtils.EMPTY;
    }

}
