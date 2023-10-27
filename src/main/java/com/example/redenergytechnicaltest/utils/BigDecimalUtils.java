package com.example.redenergytechnicaltest.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BigDecimalUtils {

    public static BigDecimal fromString(String s){
        if(StringUtils.isBlank(s)) {
            return null ;
        }

        try {
            return new BigDecimal(s);
        } catch (NumberFormatException nfe){
            throw new RuntimeException(String.format("Failed parsing string to BigDecimal of string %s", s));
        }
    }
}
