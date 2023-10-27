package com.example.redenergytechnicaltest.domain.simpleNem12;

import com.opencsv.bean.AbstractBeanField;
import org.apache.commons.lang3.StringUtils;

public class SimpleCSVConverter {

    public static class StringConverter extends AbstractBeanField<String, String> {
        @Override
        protected String convert(String s) {
            return StringUtils.trimToNull(s);
        }
    }

    public static class IntegerConverter extends AbstractBeanField<String, Integer> {
        @Override
        protected Integer convert(String s) {
            return Integer.valueOf(StringUtils.trimToNull(s));
        }
    }

}
