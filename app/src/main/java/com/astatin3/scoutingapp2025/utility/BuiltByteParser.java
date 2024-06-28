package com.astatin3.scoutingapp2025.utility;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BuiltByteParser {
    public static final Integer intType = 0;
    public static final Integer stringType = 1;
    public static final Integer intArrayType = 2;
    public static final Integer stringArrayType = 3;

    public class byteParsingExeption extends Exception {
        public byteParsingExeption() {}
        public byteParsingExeption(String message) {
            super(message);
        }
    }


    public abstract class parsedObject {
        public abstract Integer getType();
        public abstract Object get();
    }


    public class intObject extends parsedObject{
        int num;
        public Integer getType(){return intType;}
        public Object get(){return num;}
    }
    public class stringObject extends parsedObject{
        String str;
        public Integer getType(){return stringType;}
        public Object get(){return str;}
    }


    public class intArrayObject extends parsedObject{
        int[] arr;
        public Integer getType(){return intArrayType;}
        public Object get(){return arr;}
    }
    public class stringArrayObject extends parsedObject{
        String[] arr;
        public Integer getType(){return stringArrayType;}
        public Object get(){return arr;}
    }


    public class rawObject extends parsedObject {
        private int type;
        public rawObject(int type){this.type = type;}
        byte[] bytes;
        public Integer getType(){return type;}
        public Object get(){return bytes;}
    }

    byte[] bytes;
    ArrayList<parsedObject> objects = new ArrayList<>();
    public BuiltByteParser(byte[] bytes){
        this.bytes = bytes;
    }
    public ArrayList<parsedObject> parse() throws byteParsingExeption {
        if(bytes.length < 3){throw new byteParsingExeption("Invalid length");}
        int curIndex = 0;
        while(true){
//            Log.i("t", String.valueOf(curIndex));
            final int length = fileEditor.fromBytes(fileEditor.getByteBlock(bytes, curIndex, curIndex+2), 2);
            final int type = bytes[curIndex+2] & 0xFF;

            if(length == 0){
                curIndex += 3;
                continue;
            }

            final byte[] block;

            try {
                block = fileEditor.getByteBlock(bytes, curIndex + 3, curIndex + length + 3);
            } catch(Exception e){
                throw new byteParsingExeption("Array out of bounds");
            }

            switch(type){
                case 0:
                    intObject io = new intObject();
                    io.num = fileEditor.fromBytes(block, length);
                    objects.add(io);
                    break;
                case 1:
                    stringObject so = new stringObject();
                    so.str = new String(block, StandardCharsets.UTF_8);
                    objects.add(so);
                    break;
                case 2:
                    BuiltByteParser int_bbp = new BuiltByteParser(block);
                    ArrayList<parsedObject> intArrayObjects = int_bbp.parse();

                    int[] intArr = new int[intArrayObjects.size()];

                    for(int i = 0; i < intArrayObjects.size(); i ++){
                        intArr[i] = (int) intArrayObjects.get(i).get();
                    }

                    intArrayObject ia = new intArrayObject();
                    ia.arr = intArr;
                    objects.add(ia);
                    break;
                case 3:

                    BuiltByteParser str_bbp = new BuiltByteParser(block);
                    ArrayList<parsedObject> strArrayObjects = str_bbp.parse();

                    String[] StringArr = new String[strArrayObjects.size()];

                    for(int i = 0; i < strArrayObjects.size(); i ++){
                        StringArr[i] = (String) strArrayObjects.get(i).get();
                    }

                    stringArrayObject sa = new stringArrayObject();
                    sa.arr = StringArr;
                    objects.add(sa);
                    break;
                default:
                    rawObject ro = new rawObject(type);
                    ro.bytes = block;
                    objects.add(ro);
                    break;
            }

            curIndex += length + 3;

            if(curIndex == bytes.length){
                break;
            }else if(curIndex > bytes.length){
                throw new byteParsingExeption("Block length problem");
            }
        }

        return objects;
    }
}