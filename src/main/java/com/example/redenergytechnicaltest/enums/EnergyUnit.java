// Copyright Red Energy Limited 2017

package com.example.redenergytechnicaltest.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents the KWH energy unit in SimpleNem12
 */
public enum EnergyUnit {

    KWH;

    public static EnergyUnit fromString(String val) {
        if (val == null) {
            throw new RuntimeException("Unable to parse EnergyUnit of null");
        }

        for (EnergyUnit eu : EnergyUnit.values()) {
            if (StringUtils.equals(eu.name(), val)) {
                return eu;
            }
        }
        throw new RuntimeException(String.format("Unable to parse EnergyUnit %s", val));
    }

}
