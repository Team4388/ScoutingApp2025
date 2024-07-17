package com.astatin3.scoutingapp2025.types.data;

public class intType extends dataType {
    private static final int nulval = 255;

    public valueTypes getValueType() {
        return valueTypes.NUM;
    }

    public intType(String name, int value) {
        super(name);
        this.value = value;
    }

    public boolean isNull() {
        return ((int) value) == nulval;
    }
    public static Object getNullValue(){
        return nulval;
    }
}