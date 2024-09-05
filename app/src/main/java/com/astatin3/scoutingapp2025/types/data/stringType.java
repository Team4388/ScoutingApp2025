package com.astatin3.scoutingapp2025.types.data;

public class stringType extends dataType{
    public static final String nullval = "ƒ";
    public static final String unselectedval = "■";

    public valueTypes getValueType() {
        return valueTypes.STRING;
    }

//    public Object getNullValue(){
//        return nullval;
//    }
//    public Object getUnselectedValue(){
//        return unselectedval;
//    }

    public Object get(){
        return forceGetValue();
    }

    public void set(Object value){
        forceSetValue(value);
    }

    public stringType(String name, String value) {
        super(name);
        forceSetValue(value);
    }

    public static stringType newNull(String name){
        final stringType a = new stringType(name, "");
        a.forceSetValue(nullval);
        return a;
    }

    public static stringType newUnselected(String name){
        final stringType a = new stringType(name, "");
        a.forceSetValue(unselectedval);
        return a;
    }

    public static boolean isNull(String obj){
        return obj.equals(nullval);
    }
    public boolean isNull() {
        return isNull((String) forceGetValue());
    }


    public static boolean isUnselected(String obj){
        return obj.equals(unselectedval);
    }
    public boolean isUnselected() {
        return isUnselected((String) forceGetValue());
    }
}
