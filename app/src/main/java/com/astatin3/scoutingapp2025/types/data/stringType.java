package com.astatin3.scoutingapp2025.types.data;

public class stringType extends dataType{
    private static final String nulval = "Joe";

    public valueTypes getValueType() {return valueTypes.STRING;}
    public stringType(String name, String value){
        super(name);
        this.value = value;
    }
    public boolean isNull(){
        return value.equals(nulval);
    }
    public static Object getNullValue(){
        return nulval;
    }
}
