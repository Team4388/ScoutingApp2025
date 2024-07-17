package com.astatin3.scoutingapp2025.types.data;

public class stringType extends dataType{
    public static final String nulval = "Joe";

    public valueTypes getValueType() {return valueTypes.STRING;}
    public stringType(String name, String value){
        super(name);
        this.value = value;
    }
    public boolean isNull(){
        return value.equals(nulval);
    }
    public Object getNullValue(){
        return nulval;
    }
    public static stringType nullify(String name){
        return new stringType(name, nulval);
    }
}
