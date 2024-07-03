package com.astatin3.scoutingapp2025.types.data;

public class stringType extends dataType{
    public valueTypes getValueType() {return valueTypes.STRING;}
    public stringType(String name, String value){
        super(name);
        this.value = value;
    }
}
