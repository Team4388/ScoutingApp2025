package com.astatin3.scoutingapp2025.types.data;

public class intType extends dataType{
    public valueTypes getValueType() {return valueTypes.NUM;}
    public intType(String name, int value){
        super(name);
        this.value = value;
    }
}
