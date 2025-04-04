package com.yape.ms.transaction.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, Date> {

    private static final String DATE_PATTERN = "ddMMyyyy";

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext context) {
        if (date == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setLenient(false);

        String formattedDate = sdf.format(date);

        try {
            Date parsedDate = sdf.parse(formattedDate);
            return formattedDate.equals(sdf.format(parsedDate));
        } catch (ParseException e) {
            return false;
        }
    }
}
