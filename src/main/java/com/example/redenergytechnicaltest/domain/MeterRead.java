// Copyright Red Energy Limited 2017

package com.example.redenergytechnicaltest.domain;

import com.example.redenergytechnicaltest.enums.EnergyUnit;
import com.opencsv.bean.AbstractBeanField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a meter read, corresponds to RecordType 200 in SimpleNem12
 *
 * The volumes property is a map that holds the date and associated <code>MeterVolume</code> on that date, values come from RecordType 300.
 */
@Getter
@Setter
@NoArgsConstructor
public class MeterRead extends DomainObject {

  @NotBlank
  private String nmi;

  @NotNull
  private EnergyUnit energyUnit;

  private SortedMap<LocalDate, MeterVolume> volumes = new TreeMap<>();

  public MeterRead(String nmi, EnergyUnit energyUnit) {
    this.nmi = nmi;
    this.energyUnit = energyUnit;
  }

  MeterVolume getMeterVolume(LocalDate localDate) {
    return volumes.get(localDate);
  }

  public void appendVolume(LocalDate localDate, MeterVolume meterVolume) {
    volumes.put(localDate, meterVolume);
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MeterRead meterRead = (MeterRead) o;
    return Objects.equals(getNmi(), meterRead.getNmi());
  }

  public int hashCode() {
    return Objects.hash(getNmi());
  }

  public BigDecimal getTotalVolume() {
    return volumes.values().stream()
      .map(mr -> mr.getVolume())
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public static class EnergyUnitConverter extends AbstractBeanField<String, EnergyUnit> {
    @Override
    protected EnergyUnit convert(String s) {
      return EnergyUnit.valueOf(StringUtils.trimToNull(s));
    }
  }



}
