package com.kelab.experiment.constant.enums;

public enum HomeWorkType {
    PERSON(1), GROUP(2);

    private int value;

    HomeWorkType(int value) {
        this.value = value;
    }

    public static HomeWorkType valueOf(int value) {
        switch (value) {
            case 1:
                return PERSON;
            case 2:
                return GROUP;
        }
        throw new RuntimeException("HomeWorkType parse wrong");
    }

    public Integer value() {
        return this.value;
    }
}
