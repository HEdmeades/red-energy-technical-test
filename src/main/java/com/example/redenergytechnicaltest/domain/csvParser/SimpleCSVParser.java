package com.example.redenergytechnicaltest.domain.csvParser;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public abstract class SimpleCSVParser {

    public List<String[]> readRecords(File simpleCSVFile) {
        try {
            return new CSVReader(new FileReader(simpleCSVFile)).readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

}
