package com.astatin3.scoutingapp2025.scoutingData;

import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;

import java.util.ArrayList;

public class ScoutingVersion {
    public static final int slider_type_id = 255;
    public static final int dropdownType = 254;
    public static final int notesType = 253;

    public enum inputTypes {
//        USERNAME,
        SLIDER,
        DROPDOWN,
        NOTES_INPUT
    }

    public enum valueTypes {
        NUM,
        STRING
    }

    public abstract class inputType {
        public String name;
        public Object default_value;
        public abstract inputTypes getInputType();
        public abstract valueTypes getValueType();
        public abstract int get_byte_id();
        public inputType(){}
        public inputType(String name){
            this.name = name;
        }
        public abstract byte[] encode() throws ByteBuilder.buildingException;
        public abstract void decode(byte[] bytes) throws BuiltByteParser.byteParsingExeption;
    }

//    public class usernameType extends inputType {
//        public String defaultValue;
//        public inputTypes getInputType(){return inputTypes.USERNAME;}
//        public valueTypes getValueType(){return valueTypes.STRING;}
//        public usernameType(String name, String defaultValue){
//            super(name);
//            this.defaultValue = defaultValue;
//        }
//    }

    public class sliderType extends inputType {
//        public int defaultValue;
        public int min;
        public int max;
        public int get_byte_id() {return slider_type_id;}
        public inputTypes getInputType(){return inputTypes.SLIDER;}
        public valueTypes getValueType(){return valueTypes.NUM;}
        public sliderType(){};
        public sliderType(String name, int defaultValue, int min, int max){
            super(name);
            this.default_value = defaultValue;
            this.min = min;
            this.max = max;
        }
        public byte[] encode() throws ByteBuilder.buildingException {
            ByteBuilder bb = new ByteBuilder();
            bb.addString(name);
            bb.addInt((int)default_value);
            bb.addInt(min);
            bb.addInt(max);
            return bb.build();
        }
        public void decode(byte[] bytes) throws BuiltByteParser.byteParsingExeption {
            BuiltByteParser bbp = new BuiltByteParser(bytes);
            ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();

            name          = (String) objects.get(0).get();
            default_value =          objects.get(1).get();
            min           = (int)    objects.get(2).get();
            max           = (int)    objects.get(3).get();
        }
    }

    public class dropdownType extends inputType {
        public String[] text_options;
        public int get_byte_id() {return dropdownType;}
        public inputTypes getInputType(){return inputTypes.DROPDOWN;}
        public valueTypes getValueType(){return valueTypes.NUM;}
        public dropdownType(){};
        public dropdownType(String name, String[] text_options, int defaultSelIndex){
            super(name);
            this.text_options = text_options;
            this.default_value = defaultSelIndex;
        }
        public byte[] encode() throws ByteBuilder.buildingException {
            ByteBuilder bb = new ByteBuilder();
            bb.addString(name);
            bb.addInt((int)default_value);
            bb.addStringArray(text_options);
            return bb.build();
        }
        public void decode(byte[] bytes) throws BuiltByteParser.byteParsingExeption {
            BuiltByteParser bbp = new BuiltByteParser(bytes);
            ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();

            name          = (String)   objects.get(0).get();
            default_value =            objects.get(1).get();
            text_options  = (String[]) objects.get(2).get();
        }
    }

    public class notesType extends inputType {
        public int get_byte_id() {return notesType;}
        public inputTypes getInputType(){return inputTypes.NOTES_INPUT;}
        public valueTypes getValueType(){return valueTypes.STRING;}
        public notesType(){};
        public notesType(String name, String default_text){
            super(name);
            this.default_value = default_text;
        }
        public byte[] encode() throws ByteBuilder.buildingException {
            ByteBuilder bb = new ByteBuilder();
            bb.addString(name);
            bb.addString((String) default_value);
            return bb.build();
        }
        public void decode(byte[] bytes) throws BuiltByteParser.byteParsingExeption {
            BuiltByteParser bbp = new BuiltByteParser(bytes);
            ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();

            name          = (String) objects.get(0).get();
            default_value =          objects.get(1).get();
        }
    }




    public abstract class dataType {
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

    public class intType extends dataType{
        public valueTypes getValueType() {return valueTypes.NUM;}
        public intType(String name, int value){
            super(name);
            this.value = value;
        }
    }

    public class stringType extends dataType{
        public valueTypes getValueType() {return valueTypes.STRING;}
        public stringType(String name, String value){
            super(name);
            this.value = value;
        }
    }



    public enum transferValue {
        DIRECT,
//        RENAME,
        CREATE
//        DELETE
//        UP_TO_DATE
    }

    public abstract class transferType {
        public String name;
        public abstract transferValue getType();
        public transferType(String name){
            this.name = name;
        }
    }

    public class directTransferType extends transferType {
        public transferValue getType() {return transferValue.DIRECT;}
        public directTransferType(String name){
            super(name);
        }
    }

//    public class renameTransferType extends transferType {
//        public String new_name;
//        public transferValue getType() {return transferValue.RENAME;}
//        public renameTransferType(String name, String new_name){
//            super(name);
//            this.new_name = new_name;
//        }
//    }


    public class createTransferType extends transferType {
        public transferValue getType() {return transferValue.CREATE;}
        public createTransferType(String name){
            super(name);
        }
    }

//    public class deleteTransferType extends transferType {
//        public transferValue getType() {return transferValue.DELETE;}
//        public deleteTransferType(String name){
//            super(name);
//        }
//    }

//    public class uptodateTransferType extends transferType {
//        public String name;
//        public transferValue getType() {return transferValue.UP_TO_DATE;}
//        public uptodateTransferType(String name){
//            super(name);
//        }
//    }



    public class ScoutingArray {
        public int version;
        public dataType[] array;
        public ScoutingVersion.inputType[][] values;
        public int latest_version_num;
        public transferType[][] transfer_values;

        public ScoutingArray(int version, dataType[] array, ScoutingVersion.inputType[][] values, transferType[][] transfer_values){
            this.version = version;
            this.array = array;
            this.values = values;
            this.latest_version_num = values.length-1;
            this.transfer_values = transfer_values;
        }

        public ScoutingArray(int version, dataType[] array, ScoutingVersion.inputType[][] values){
            this(version, array, values, get_transfer_values(values));
        }

        public void update(){
            while(version<latest_version_num){
                dataType[] new_values = new dataType[transfer_values[version].length];
                for(int i = 0; i < transfer_values[version].length; i++){
                    transferType tv = transfer_values[version][i];
                    switch (tv.getType()){
                        case DIRECT:
                            new_values[i] = direct_transfer((directTransferType) tv);
                            continue;
//                        case RENAME:
//                            new_values[i] = rename_transfer((renameTransferType) tv);
//                            continue;
                        case CREATE:
                            new_values[i] = create_transfer((createTransferType) tv);
                            continue;
                    }
                    System.out.println(new_values[i]);
                }
                this.array = new_values;
                version++;
                System.out.println("Updated to " + version);
            }
        }

        private inputType get_input_type_by_name(int version, String name){
            for(inputType it : values[version]){
                if(it.name.equals(name)){
                    return it;
                }
            }
            return null;
        }

        private dataType get_data_type_by_name(String name){
            for(dataType dt : array){
                if(dt.name.equals(name)){
                    return dt;
                }
            }
            return null;
        }

        private dataType direct_transfer(directTransferType tv){
            return get_data_type_by_name(tv.name);
        }

//        private dataType rename_transfer(renameTransferType tv){
//            dataType dt = get_data_type_by_name(tv.name);
//            dt.name = tv.new_name;
//            return dt;
//        }

        private dataType create_transfer(createTransferType tv){
            inputType it = get_input_type_by_name(version+1, tv.name);
//            System.out.println(tv.name);
            switch (it.getValueType()){
                case NUM:
                    return new intType(it.name, (int) it.default_value);
                case STRING:
                    return new stringType(it.name, (String) it.default_value);
            }
            System.out.println(2);
            return null;
        }

    }

    private inputType get_input_type_by_name(inputType[] values, String name){
        for(inputType it : values){
            if(it.name.equals(name)){
                return it;
            }
        }
        return null;
    }

    public transferType[][] get_transfer_values(inputType[][] values) {
        transferType[][] output = new transferType[values.length][];
        for(int a = 1; a < values.length; a++){
            transferType[] v = new transferType[values[a].length];
            for(int b = 0; b < values[a].length; b++){
                String name = values[a][b].name;
                if(get_input_type_by_name(values[a-1], name) != null){
                    v[b] = new directTransferType(name);
                }else{
                    v[b] = new createTransferType(name);
                }
            }
            output[a-1] = v;
        }
        return output;
    }
}
