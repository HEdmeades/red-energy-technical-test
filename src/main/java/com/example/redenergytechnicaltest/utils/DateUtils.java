package com.example.redenergytechnicaltest.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final String FILE_DATE_FORMAT = "yyyyMMdd";

    public static LocalDate parseToLocalDate(String s){
        if(StringUtils.isBlank(s)) {
            return null ;
        }

        return LocalDate.parse(StringUtils.trimToNull(s), DateTimeFormatter.ofPattern(FILE_DATE_FORMAT));
    }
}
