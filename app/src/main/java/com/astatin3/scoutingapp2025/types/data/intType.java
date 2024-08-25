package com.astatin3.scoutingapp2025.types.data;

public class intType extends dataType {
    public static final int nullval = 0;
    public static final int unselectedval = 1;

    public valueTypes getValueType() {
        return valueTypes.NUM;
    }

//    public Object getNullValue(){
//        return nullval;
//    }
//    public Object getUnselectedValue(){
//        return unselectedval;
//    }

    public Object get(){
        return (int) forceGetValue()+2;
    }

    public void set(Object value){
        forceSetValue((int) value - 2);
    }

    public intType(String name, int value) {
        super(name);
        forceSetValue(value+2);
    }

    public static intType newNull(String name){
        final intType a = new intType(name, 0);
        a.forceSetValue(nullval);
        return a;
    }

    public static intType newUnselected(String name){
        final intType a = new intType(name, 0);
        a.forceSetValue(unselectedval);
        return a;
    }

    public static boolean isNull(int obj){
        return obj == nullval;
    }
    public boolean isNull() {
        return isNull((int) forceGetValue());
    }

    public static boolean isUnselected(int obj){
        return obj == unselectedval;
    }
    public boolean isUnselected() {
        return isUnselected((int) forceGetValue());
    }
}