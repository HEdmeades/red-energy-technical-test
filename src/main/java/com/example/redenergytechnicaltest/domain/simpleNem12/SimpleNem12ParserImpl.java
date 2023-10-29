package com.example.redenergytechnicaltest.domain.simpleNem12;

import com.example.redenergytechnicaltest.domain.meter.MeterRead;
import com.example.redenergytechnicaltest.domain.meter.MeterVolume;
import com.example.redenergytechnicaltest.domain.csvParser.SimpleCSVParser;
import com.example.redenergytechnicaltest.enums.EnergyUnit;
import com.example.redenergytechnicaltest.enums.Quality;
import com.example.redenergytechnicaltest.enums.RecordType;
import com.example.redenergytechnicaltest.utils.BigDecimalUtils;
import com.example.redenergytechnicaltest.utils.DateUtils;
import javafx.util.Pair;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SimpleNem12ParserImpl extends SimpleCSVParser implements SimpleNem12Parser {

    @Override
    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {
        //I've broken this method into its on abstract class for future code extension.
        List<String[]> records = this.readRecords(simpleNem12File);

        try {
            return readSimpleNem12FileRecords(records);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<MeterRead> readSimpleNem12FileRecords(List<String[]> records) throws Exception {
        if (records.size() < 2) {
            //We expect at least a 100 record in the first row and 900 record at the end row
            throw new Exception("Invalid Nem12File");
        }

        //Remove the first line (we are expecting it to be "100")
        String[] firstRecord = records.remove(0);
        if (!RecordType.fromString(firstRecord[0]).equals(RecordType.ONE_HUNDRED)) {
            throw new Exception("Invalid Nem12File. Missing starting record 100");
        }

        //Remove the last line (we are expecting it to be "900")
        String[] lastRecord = records.remove(records.size() - 1);
        if (!RecordType.fromString(lastRecord[0]).equals(RecordType.NINE_HUNDRED)) {
            throw new Exception("Invalid Nem12File. Missing EOF record 900");
        }

        //Using a map so I can access the current meter read to append the meter volume children
        HashMap<String, MeterRead> map = new HashMap<>();

        String currentNmi = "";
        for (String[] lineItem : records) {
            switch (RecordType.fromString(lineItem[0])) {
                //Because we removed the first and last line - if we find a 100 or 900 record we can throw and error
                case ONE_HUNDRED:
                    throw new Exception("Invalid Nem12File. Record 100 must be on the first line");
                case NINE_HUNDRED:
                    throw new Exception("Invalid Nem12File. EOF record 900 must be on the last line");
                case TWO_HUNDRED:
                    MeterRead mr = mapToMeterRead(lineItem);

                    map.put(mr.getNmi(), mr);
                    currentNmi = mr.getNmi();
                    break;
                case THREE_HUNDRED:
                    if (map.containsKey(currentNmi)) {
                        Pair<LocalDate, MeterVolume> dateMeterVolumePair = mapToLocalDateMeterVolumePair(lineItem);

                        if(map.get(currentNmi).getVolumes().containsKey(dateMeterVolumePair.getKey())){
                            throw new Exception(String.format("Duplicate meter read volume for date %s", dateMeterVolumePair.getKey()));
                        }

                        map.get(currentNmi).appendVolume(dateMeterVolumePair.getKey(), dateMeterVolumePair.getValue());
                    } else {
                        throw new Exception("Invalid Nem12File. Meter volume found with no corresponding meter read");
                    }
                    break;
                default:
                    throw new Exception("Unknown RecordType");
            }
        }

        return map.values();
    }

    public MeterRead mapToMeterRead(String[] record) throws Exception {
        MeterRead meterRead = new MeterRead(record[1], EnergyUnit.fromString(record[2]));

        meterRead.validate();
        return meterRead;
    }

    public Pair<LocalDate, MeterVolume> mapToLocalDateMeterVolumePair(String[] record) throws Exception {
        MeterVolume mv = new MeterVolume(BigDecimalUtils.fromString(record[2]), Quality.fromString(record[3]));

        mv.validate();
        //I'm using a pair here to access the date for this record so we can easily append to our sorted map in MeterRead
        return new Pair<>(DateUtils.parseToLocalDate(record[1]), mv);
    }

}
