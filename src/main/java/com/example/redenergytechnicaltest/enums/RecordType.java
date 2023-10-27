package com.example.redenergytechnicaltest.enums;

public enum RecordType {
    ONE_HUNDRED(100),
    TWO_HUNDRED(200),
    THREE_HUNDRED(300),
    NINE_HUNDRED(900);

    public final int value;

    RecordType(int val) {
        this.value = val;
    }

    public static RecordType fromString(String val) {
        for (RecordType rt : RecordType.values()) {
            if (rt.value == Integer.parseInt(val)) {
                return rt;
            }
        }
        return null;
    }
}
