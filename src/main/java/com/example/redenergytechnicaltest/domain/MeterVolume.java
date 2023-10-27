// Copyright Red Energy Limited 2017

package com.example.redenergytechnicaltest.domain;

import com.example.redenergytechnicaltest.enums.Quality;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a meter volume with quality, values should come from RecordType 300
 */
@Getter
@Setter
public class MeterVolume {

  private BigDecimal volume;
  private Quality quality;

  public MeterVolume(BigDecimal volume, Quality quality) {
    this.volume = volume;
    this.quality = quality;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MeterVolume that = (MeterVolume) o;
    return Objects.equals(getVolume(), that.getVolume()) &&
      getQuality() == that.getQuality();
  }

  public int hashCode() {
    return Objects.hash(getVolume(), getQuality());
  }
}
