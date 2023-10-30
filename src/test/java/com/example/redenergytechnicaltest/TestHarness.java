// Copyright Red Energy Limited 2017

package com.example.redenergytechnicaltest;

import com.example.redenergytechnicaltest.domain.meter.MeterRead;
import com.example.redenergytechnicaltest.domain.simpleNem12.SimpleNem12ParserImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple test harness for trying out SimpleNem12Parser implementation
 */
@SpringBootTest
public class TestHarness {

  static File simpleNem12File;

  @Test
  public void testFileLoad() {
    ClassLoader classLoader = TestHarness.class.getClassLoader();

    simpleNem12File = new File(classLoader.getResource("testFiles/SimpleNem12.csv").getFile());

    // Uncomment below to try out test harness.
    Collection<MeterRead> meterReads = new SimpleNem12ParserImpl().parseSimpleNem12(simpleNem12File);

    MeterRead read6123456789 = meterReads.stream().filter(mr -> mr.getNmi().equals("6123456789")).findFirst().get();
    System.out.println(String.format("Total volume for NMI 6123456789 is %f", read6123456789.getTotalVolume()));  // Should be -36.84
    assert(StringUtils.equals(read6123456789.getTotalVolume().toPlainString(), "-36.84"));

    MeterRead read6987654321 = meterReads.stream().filter(mr -> mr.getNmi().equals("6987654321")).findFirst().get();
    System.out.println(String.format("Total volume for NMI 6987654321 is %f", read6987654321.getTotalVolume()));  // Should be 14.33
    assert(StringUtils.equals(read6987654321.getTotalVolume().toPlainString(), "14.33"));
  }

  @Test
  public void testFileLoadExtra200Row() {
    ClassLoader classLoader = TestHarness.class.getClassLoader();

    simpleNem12File = new File(classLoader.getResource("testFiles/SimpleNem12-test-extra-200-row.csv").getFile());
    Collection<MeterRead> meterReads = new SimpleNem12ParserImpl().parseSimpleNem12(simpleNem12File);

    MeterRead read6123456789 = meterReads.stream().filter(mr -> mr.getNmi().equals("6123456789")).findFirst().get();
    validateMeterRead(read6123456789, new BigDecimal("-36.84"));

    MeterRead read6987654321 = meterReads.stream().filter(mr -> mr.getNmi().equals("6987654321")).findFirst().get();
    validateMeterRead(read6987654321, new BigDecimal("14.33"));

    MeterRead read123456 = meterReads.stream().filter(mr -> mr.getNmi().equals("123456")).findFirst().get();
    validateMeterRead(read123456, new BigDecimal("0"));
  }

  private void validateMeterRead(MeterRead mr, BigDecimal expectedValue){
    System.out.printf("Total volume for NMI %s is %f%n", mr.getNmi(), mr.getTotalVolume());
    assert(StringUtils.equals(mr.getTotalVolume().toPlainString(), expectedValue.toPlainString()));
  }

  @Test
  public void testNullValueInMeterVolumeData() {
    ClassLoader classLoader = TestHarness.class.getClassLoader();

    simpleNem12File = new File(classLoader.getResource("testFiles/SimpleNem12-invalid-file.csv").getFile());

    Exception re = Assertions.assertThrows(Exception.class, () -> new SimpleNem12ParserImpl().parseSimpleNem12(simpleNem12File)) ;
    assert(StringUtils.equals(re.getMessage(), "java.lang.Exception: MeterVolume: volume must not be null"));
  }

  @Test
  public void testInvalidFile900RecordMidWayThroughFile() {
    ClassLoader classLoader = TestHarness.class.getClassLoader();

    simpleNem12File = new File(classLoader.getResource("testFiles/SimpleNem12-invalid-eof-record.csv").getFile());

    Exception re = Assertions.assertThrows(Exception.class, () -> new SimpleNem12ParserImpl().parseSimpleNem12(simpleNem12File)) ;
    assert(StringUtils.equals(re.getMessage(), "java.lang.Exception: Invalid Nem12File. EOF record 900 must be on the last line"));
  }

  @Test
  public void testReadNem12Records() throws Exception {
    ArrayList<String[]> records = new ArrayList<String[]>() {{
      add(new String[]{"100"});
      add(new String[]{"200","6123456789","KWH"});
      add(new String[]{"300","20161113","-50.8","A"});
      add(new String[]{"900"});
    }};

    Collection<MeterRead> meterReads = new SimpleNem12ParserImpl().readSimpleNem12FileRecords(records);
    MeterRead read6123456789 = meterReads.stream().filter(mr -> mr.getNmi().equals("6123456789")).findFirst().get();
    validateMeterRead(read6123456789, new BigDecimal("-50.8"));
  }

  @Test
  public void testReadNem12RecordsFailingTestMissing900EOFRecord() {
    ArrayList<String[]> records = new ArrayList<String[]>() {{
      add(new String[]{"100"});
      add(new String[]{"200","6123456789","KWH"});
      add(new String[]{"300","20161113","-50.8","A"});
    }};

    Exception re = Assertions.assertThrows(Exception.class, () -> new SimpleNem12ParserImpl().readSimpleNem12FileRecords(records));
    assert(StringUtils.equals(re.getMessage(), "Invalid Nem12File. Missing EOF record 900"));
  }

  @Test
  public void testReadNem12RecordsFailingTestMissingStarting100EOFRecord() {
    ArrayList<String[]> records = new ArrayList<String[]>() {{
      add(new String[]{"200","6123456789","KWH"});
      add(new String[]{"300","20161113","-50.8","A"});
      add(new String[]{"900"});
    }};

    Exception re = Assertions.assertThrows(Exception.class, () -> new SimpleNem12ParserImpl().readSimpleNem12FileRecords(records));
    assert(StringUtils.equals(re.getMessage(), "Invalid Nem12File. Missing starting record 100"));
  }

  @Test
  public void testReadNem12RecordsFailingDuplicateMeterReadVolumeDate() {
    ArrayList<String[]> records = new ArrayList<String[]>() {{
      add(new String[]{"100"});
      add(new String[]{"200","6123456789","KWH"});
      add(new String[]{"300","20161113","-50.8","A"});
      add(new String[]{"300","20161113","100","A"});
      add(new String[]{"900"});
    }};

    Exception re = Assertions.assertThrows(Exception.class, () -> new SimpleNem12ParserImpl().readSimpleNem12FileRecords(records));
    assert(StringUtils.equals(re.getMessage(), "Duplicate meter read volume for date 2016-11-13"));
  }

}
