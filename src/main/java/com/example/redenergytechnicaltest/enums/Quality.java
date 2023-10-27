// Copyright Red Energy Limited 2017

package com.example.redenergytechnicaltest.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents meter read quality in SimpleNem12
 */
public enum Quality {

  A,
  E;

  public static Quality fromString(String val) {
    if (val == null) {
      throw new RuntimeException("Unable to parse Quality of null");
    }

    for (Quality q : Quality.values()) {
      if (StringUtils.equals(q.name(), val)) {
        return q;
      }
    }
    throw new RuntimeException(String.format("Unable to parse Quality %s", val));
  }
  
}
