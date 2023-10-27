package com.example.redenergytechnicaltest.domain.simpleNem12;

import com.example.redenergytechnicaltest.domain.MeterRead;
import com.example.redenergytechnicaltest.domain.MeterVolume;
import com.example.redenergytechnicaltest.domain.csvParser.SimpleCSVParser;
import com.example.redenergytechnicaltest.enums.EnergyUnit;
import com.example.redenergytechnicaltest.enums.Quality;
import com.example.redenergytechnicaltest.enums.RecordType;
import com.example.redenergytechnicaltest.utils.BigDecimalUtils;
import com.example.redenergytechnicaltest.utils.DateUtils;
import javafx.util.Pair;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SimpleNem12ParserImpl extends SimpleCSVParser implements SimpleNem12Parser {

    @Override
    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {
        List<String[]> records = this.readRecords(simpleNem12File);

        if(records.size() < 2){
            //We expect at least a 100 record in the first row and 900 record at the end
            throw new RuntimeException("Invalid Nem12File");
        }

        String[] firstRecord = records.remove(0);
        if(!RecordType.fromString(firstRecord[0]).equals(RecordType.ONE_HUNDRED)){
            throw new RuntimeException("Invalid Nem12File. Missing starting record 100");
        }

        String[] lastRecord = records.remove(records.size() - 1);
        if(!RecordType.fromString(lastRecord[0]).equals(RecordType.NINE_HUNDRED)){
            throw new RuntimeException("Invalid Nem12File. Missing starting EOF record 900");
        }

        HashMap<String, MeterRead> map = new HashMap<>();

        try {
            String currentNmi = "";
            for (String[] lineItem : records) {
                switch (RecordType.fromString(lineItem[0])) {
                    case ONE_HUNDRED:
                        throw new RuntimeException("Invalid Nem12File. EOF record 100 is not on the first line");
                    case NINE_HUNDRED:
                        throw new RuntimeException("Invalid Nem12File. EOF record 900 is not on the last line");
                    case TWO_HUNDRED:
                        MeterRead mr = mapToMeterRead(lineItem);

                        map.put(mr.getNmi(), mr);
                        currentNmi = mr.getNmi();
                        break;
                    case THREE_HUNDRED:
                        if(map.containsKey(currentNmi)){
                            Pair<LocalDate, MeterVolume> dateMeterVolumePair = mapToLocalDateMeterVolumePair(lineItem);
                            map.get(currentNmi).appendVolume(dateMeterVolumePair.getKey(), dateMeterVolumePair.getValue());
                        } else {
                            throw new RuntimeException("CSV parse file error. Meter volume found with no corresponding meter read");
                        }
                        break;
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return map.values();
    }

    public MeterRead mapToMeterRead(String[] record) throws Exception {
        MeterRead meterRead = new MeterRead();
        meterRead.setNmi(ObjectUtils.defaultIfNull(record[1], null));
        meterRead.setEnergyUnit(EnergyUnit.fromString(record[2]));

        meterRead.validate();

        return meterRead;
    }

    public Pair<LocalDate, MeterVolume> mapToLocalDateMeterVolumePair(String[] record) throws Exception {
        MeterVolume mv = new MeterVolume(BigDecimalUtils.fromString(record[2]), Quality.fromString(record[3]));

        mv.validate();

        return new Pair<>(DateUtils.parseToLocalDate(record[1]), mv);
    }

}
