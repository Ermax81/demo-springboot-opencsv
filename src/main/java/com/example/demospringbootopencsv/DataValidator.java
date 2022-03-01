package com.example.demospringbootopencsv;

import com.opencsv.bean.BeanField;
import com.opencsv.bean.validators.MustMatchRegexExpression;
import com.opencsv.exceptions.CsvValidationException;

public class DataValidator extends MustMatchRegexExpression {

    public DataValidator() {}

    public void validate(String value, BeanField field) throws CsvValidationException {
        //cf Resource Bundle 'mustMatchRegex' (package com.opencsv:opencsv fichier mustMatchRegex_fr.properties)

        //Modify CsvValidationException message
        String message = "Field value of "+ field.getField().getName() +" is not adequate.";
        if (!isValid(value)) {
            throw new CsvValidationException(message);
        }
    }
}
