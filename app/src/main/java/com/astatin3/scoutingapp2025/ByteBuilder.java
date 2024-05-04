package com.astatin3.scoutingapp2025;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ByteBuilder {
    ArrayList<byteType> bytesToBuild = new ArrayList<>();

    public class buildingException extends Exception {
        public buildingException() {}
        public buildingException(String message) {
            super(message);
        }
    }

    private abstract static class byteType {
        public abstract byte getType();
        public abstract int length();
        public abstract byte[] build();
    }

    private class intType extends byteType {
        public int precision;
        public int num;
        public byte getType(){return 0;}
        public int length(){return precision;}
        public byte[] build(){
            return fileEditor.toBytes(num, precision);
        }
    }
    private int getLeastBytePrecision(int num){
        if(num <= 1){return 1;}
        return (int) Math.ceil(Math.log(Math.abs(num))/Math.log(8));
    }
    public ByteBuilder addInt(int num) throws buildingException {
        // Get closest number of bytes
        int precision = getLeastBytePrecision(num);
        return addInt(num, precision);
    }
    public ByteBuilder addInt(int num, int precision) throws buildingException {
        if(precision <= 0){throw new buildingException("Invalid precision: " + precision);}

        if(precision > 65536){throw new buildingException("Precision too large (greter than 65536)");}
        if(precision < getLeastBytePrecision(num)){throw new buildingException("Precision too small");}

        if(num > Integer.MAX_VALUE){throw new buildingException("Integer overflow");}
        if(num < Integer.MIN_VALUE){throw new buildingException("Integer overflow");}

        intType intType = new intType();
        intType.num = num;
        intType.precision = precision;

        bytesToBuild.add(intType);
        return this;
    }

    private class stringType extends byteType {
        public byte[] bytes;
        public byte getType(){return 1;}
        public int length(){return bytes.length;}
        public byte[] build(){
            return bytes;
//            return str.getBytes(charset);
        }
    }
    public ByteBuilder addString(String str) throws buildingException {
        if(str.length() > 65536){throw new buildingException("String too long (greater than 65536)");}

        stringType stringType = new stringType();
        // To get the length correctly, the string bytes need to be precalculated
        stringType.bytes = str.getBytes(StandardCharsets.UTF_8);

        bytesToBuild.add(stringType);
        return this;
    }

    private class rawType extends byteType {
        public int type;
        public byte[] bytes;
        public byte getType(){return (byte) type;}
        public int length(){return bytes.length;}
        public byte[] build(){
            return bytes;
        }
    }

    public ByteBuilder addRaw(int type, byte[] bytes) throws buildingException {
        if(bytes.length > 65536){throw new buildingException("Byte array length to long (greater than 65536)");}

        rawType rawType = new rawType();
        rawType.type = type;
        rawType.bytes = bytes;
        bytesToBuild.add(rawType);

        return this;
    }

    public byte[] build() throws buildingException {
        if(bytesToBuild.size() == 0){throw new buildingException("Cannot build null data");}

        int length = bytesToBuild.size() * 3;
        for(byteType bt : bytesToBuild){
            length += bt.length();
        }

        byte[] bytes = new byte[length];
        int bytesFilled = 0;

        for(byteType bt : bytesToBuild){

            byte[] blockLength = fileEditor.toBytes(bt.length(), 2);

            bytes[bytesFilled] = blockLength[0];
            bytesFilled += 1;
            bytes[bytesFilled] = blockLength[1];
            bytesFilled += 1;
            bytes[bytesFilled] = bt.getType();
            bytesFilled += 1;

//            if(bt.length == 0)
//                continue;

            int i = 1;
            byte[] newBytes = bt.build();
            for(byte b : newBytes){
//                Log.i("i", (bytesFilled+1) + "/" + length + " (" + i + "/" + bt.length() + ")");
                bytes[bytesFilled] = b;
                bytesFilled += 1;
                i++;
            }
        }

        return bytes;
    }
}
