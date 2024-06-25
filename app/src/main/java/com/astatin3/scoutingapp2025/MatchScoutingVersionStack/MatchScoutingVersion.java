package com.astatin3.scoutingapp2025.MatchScoutingVersionStack;

import com.astatin3.scoutingapp2025.BuiltByteParser;
import com.astatin3.scoutingapp2025.ByteBuilder;
import com.astatin3.scoutingapp2025.fileEditor;

import java.util.ArrayList;

public abstract class MatchScoutingVersion {
    public abstract int getVersion();
    public abstract dataType[] getTypes();
//    public abstract void ();
//    public abstract MatchScoutingVersion update(MatchScoutingVersion prev);


    public dataType[] test(){return getTypes();}

    public static final Integer intType = 0;
    public static final Integer stringType = 1;

//    public static final Integer update_direct_transfer = 0;
//    public static final Integer update_reset_value = 1;
//    public static final Integer update_delete_value = 2;

    public abstract class dataType {
        public String name;
        public dataType(String name){
            this.name = name;
        }
        public abstract Integer getType();
        public abstract Object get();
        public abstract void set(Object obj);
    }
    public class intData extends MatchScoutingVersion.dataType {
        int num;
        public intData(String name, int defaultValue){
            super(name);
            this.num = defaultValue;
        }
        public Integer getType(){return intType;}
        public Object get(){return num;}
        public void set(Object obj){
            this.num = (int) obj;
        };
    }
    public class stringData extends MatchScoutingVersion.dataType {
        public String str;
        public stringData(String name, String defaultValue){
            super(name);
            this.str = defaultValue;
        }
        public Integer getType(){return stringType;}
        public Object get(){return str;}
        public void set(Object obj){
            this.str = (String) obj;
        };
    }
    public class rawData extends MatchScoutingVersion.dataType {
        private int type;
        public byte[] bytes;
        public rawData(String name, int type, byte[] defaultValue){
            super(name);
            this.type = type;
            this.bytes = defaultValue;
        }
        public Integer getType(){return type;}
        public Object get(){return bytes;}
        public void set(Object obj){
            this.bytes = (byte[]) obj;
        };
    }


    public dataType get_value(String name){
        for(dataType data : getTypes()){
            if(data.name.equals(name))
                return data;
        }
        return null;
    }

    public intData get_int_value(String name){
        dataType data = get_value(name);
        if(data.getType() != intType)
            return null;
        return (intData) data;
    }

    public stringData get_string_value(String name){
        dataType data = get_value(name);
        if(data.getType() != stringType)
            return null;
        return (stringData) data;
    }

    public rawData get_raw_value(String name, int rawType){
        dataType data = get_value(name);
        if(!data.getType().equals(rawType))
            return null;
        return (rawData) data;
    }



    public boolean save(String filename){
        ByteBuilder bb = new ByteBuilder();
        try {
            bb.addInt(getVersion());
            for(dataType data : getTypes()){
                if(data.getType() == intType){
                    bb.addInt((int)data.get());
                } else if (data.getType() == stringType) {
                    bb.addString((String)data.get());
                }else{
                    bb.addRaw(data.getType(), (byte[])data.get());
                }
            }
            return fileEditor.writeFile(filename, bb.build());
        } catch (ByteBuilder.buildingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean load(String filename){
        byte[] bytes = fileEditor.readFile(filename);
        BuiltByteParser bbp = new BuiltByteParser(bytes);
        try {
            ArrayList<BuiltByteParser.parsedObject> parsedObjects = bbp.parse();
            return true;
        } catch (BuiltByteParser.byteParsingExeption e) {
            e.printStackTrace();
            return false;
//            throw new RuntimeException(e);
        }
    }
}
