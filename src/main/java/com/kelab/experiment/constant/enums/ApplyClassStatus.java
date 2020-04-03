package com.kelab.experiment.constant.enums;

public enum ApplyClassStatus {
    ALLOWED(1), REJECTED(2), PADDING(3);

    private int value;

    ApplyClassStatus(int value) {
        this.value = value;
    }

    public static ApplyClassStatus valueOf(int value) {
        switch (value) {
            case 1:
                return ALLOWED;
            case 2:
                return REJECTED;
            case 3:
                return PADDING;
        }
        throw new RuntimeException("ApplyClassStatus parse wrong");
    }

    public Integer value() {
        return this.value;
    }
}
