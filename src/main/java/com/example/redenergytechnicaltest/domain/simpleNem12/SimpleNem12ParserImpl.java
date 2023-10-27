package com.example.redenergytechnicaltest.domain.simpleNem12;

import com.example.redenergytechnicaltest.domain.MeterRead;
import com.example.redenergytechnicaltest.domain.MeterVolume;
import com.example.redenergytechnicaltest.enums.EnergyUnit;
import com.example.redenergytechnicaltest.enums.Quality;
import com.example.redenergytechnicaltest.enums.RecordType;
import com.example.redenergytechnicaltest.utils.DateUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class SimpleNem12ParserImpl implements SimpleNem12Parser {

    @Override
    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {
        List<String[]> records = this.readRecords(simpleNem12File);

        HashMap<String, MeterRead> map = new HashMap<>();

        String currentNmi = "";
        for (String[] lineItem : records) {
            switch (Objects.requireNonNull(RecordType.fromString(lineItem[0]))) {
                case ONE_HUNDRED:
                case NINE_HUNDRED:
                    break;
                case TWO_HUNDRED:
                    MeterRead mr = mapToMeterRead(lineItem);

                    map.put(mr.getNmi(), mr);
                    currentNmi = mr.getNmi();
                    break;
                case THREE_HUNDRED:
                    if(map.containsKey(currentNmi)){
                        AbstractMap.SimpleEntry<LocalDate, MeterVolume> dateMeterVolumePair = mapToMeterVolumeKeyValue(lineItem);
                        map.get(currentNmi).appendVolume(dateMeterVolumePair.getKey(), dateMeterVolumePair.getValue());
                    } else {
                        throw new RuntimeException("CSV parse file error. Meter volume found with no corresponding meter read");
                    }
                    break;
            }
        }

        return map.values();
    }

    public MeterRead mapToMeterRead(String[] record) {
        MeterRead meterRead = new MeterRead();
        meterRead.setNmi(record[1]);
        meterRead.setEnergyUnit(EnergyUnit.valueOf(record[2]));

        return meterRead;
    }

    public AbstractMap.SimpleEntry<LocalDate, MeterVolume> mapToMeterVolumeKeyValue(String[] record) {
        return new AbstractMap.SimpleEntry<LocalDate, MeterVolume>(DateUtils.parseToLocalDate(record[1]), new MeterVolume(new BigDecimal(record[2]), Quality.valueOf(record[3])));
    }

    private List<String[]> readRecords(File simpleNem12File) {
        CSVReader csvReader;
        try {
            csvReader = new CSVReader(new FileReader(simpleNem12File));
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
