package com.astatin3.scoutingapp2025.ScoutingDataVersion;

public class ScoutingVersion {
    public enum inputTypes {
        USERNAME,
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
        public inputType(String name){
            this.name = name;
        }
    }

    public class usernameType extends inputType {
        public String defaultValue;
        public inputTypes getInputType(){return inputTypes.USERNAME;}
        public valueTypes getValueType(){return valueTypes.STRING;}
        public usernameType(String name, String defaultValue){
            super(name);
            this.defaultValue = defaultValue;
        }
    }

    public class sliderType extends inputType {
//        public int defaultValue;
        public int min;
        public int max;
        public inputTypes getInputType(){return inputTypes.SLIDER;}
        public valueTypes getValueType(){return valueTypes.NUM;}
        public sliderType(String name, int defaultValue, int min, int max){
            super(name);
            this.default_value = defaultValue;
            this.min = min;
            this.max = max;
        }
    }

    public class dropdownType extends inputType {
        public String[] text_options;
//        public int defaultSelIndex;
        public inputTypes getInputType(){return inputTypes.DROPDOWN;}
        public valueTypes getValueType(){return valueTypes.NUM;}
        public dropdownType(String name, String[] text_options, int defaultSelIndex){
            super(name);
            this.text_options = text_options;
            this.default_value = defaultSelIndex;
        }
    }

    public class notesType extends inputType {
//        public String default_text;
        public inputTypes getInputType(){return inputTypes.NOTES_INPUT;}
        public valueTypes getValueType(){return valueTypes.STRING;}
        public notesType(String name, String default_text){
            super(name);
            this.default_value = default_text;
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
        RENAME,
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

    public class renameTransferType extends transferType {
        public String new_name;
        public transferValue getType() {return transferValue.RENAME;}
        public renameTransferType(String name, String new_name){
            super(name);
            this.new_name = new_name;
        }
    }


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



//    public boolean save(String filename){
//        ByteBuilder bb = new ByteBuilder();
//        try {
//            bb.addInt(getVersion());
//            for(dataType data : getTypes()){
//                if(data.getType() == intType){
//                    bb.addInt((int)data.get());
//                } else if (data.getType() == stringType) {
//                    bb.addString((String)data.get());
//                }else{
//                    bb.addRaw(data.getType(), (byte[])data.get());
//                }
//            }
//            return fileEditor.writeFile(filename, bb.build());
//        } catch (ByteBuilder.buildingException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public void load(String filename){
//        byte[] bytes = fileEditor.readFile(filename);
//        BuiltByteParser bbp = new BuiltByteParser(bytes);
//        try {
//            ArrayList<BuiltByteParser.parsedObject> parsedObjects = bbp.parse();
//            parse((int)parsedObjects.get(0).get(), parsedObjects);
//        } catch (BuiltByteParser.byteParsingExeption e) {
//            e.printStackTrace();
////            throw new RuntimeException(e);
//        }
//    }
}
