package com.astatin3.scoutingapp2025.types.data;

public abstract class dataType {
    public enum valueTypes {
        NUM,
        STRING
    }
    public String name;
    public Object value;
    public abstract valueTypes getValueType();
    public Object get(){
        return value;
    }
    public void set(Object value){
        this.value = value;
    }
    public dataType(String name){
        this.name = name;
    }
}