package com.astatin3.scoutingapp2025.ScoutingDataVersion;

import android.renderscript.Element;

import com.astatin3.scoutingapp2025.ScoutingDataVersion.ScoutingVersion.*;

public class MatchScouting {
    public static int latest_version_num = 4;

    public static ScoutingVersion msv = new ScoutingVersion();
    public static MatchScouting ms = new MatchScouting();

    public static inputType[][] values = new inputType[][] {
        {
            msv.new usernameType("name", "Unset-Username"),
            msv.new sliderType("How good is robot", 5, 0, 10)
        }, {
            msv.new usernameType("name", "Unset-Username"),
            msv.new sliderType("How good is robot", 5, 0, 10),
            msv.new notesType("notes", "No-Notes")
        },{
            msv.new usernameType("name", "Unset-Username"),
//            msv.new sliderType("How good is robot", 5, 0, 10),
            msv.new notesType("notes", "No-Notes")
        },{
            msv.new usernameType("name", "Unset-Username"),
            msv.new sliderType("team_number", 4388, 0, 9999),
            msv.new notesType("notes", "No-Notes")
        }
    };
    public static transferType[][] transfer_values = new transferType[][] {
        {
            msv.new directTransferType("name"),
            msv.new directTransferType("How good is robot"),
            msv.new createTransferType("notes")
        },{
            msv.new directTransferType("name"),
            msv.new renameTransferType("How good is robot", "robot_preformance"),
            msv.new directTransferType("notes")
        },{
            msv.new directTransferType("name"),
            msv.new directTransferType("notes")
        },{
            msv.new directTransferType("name"),
            msv.new createTransferType("team_number"),
            msv.new directTransferType("notes")
        }
    };


    public class MatchScoutingArray {
        public int version;
        public dataType[] array;
        public MatchScoutingArray(int version, dataType[] array){
            this.version = version;
            this.array = array;
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
                        case RENAME:
                            new_values[i] = rename_transfer((renameTransferType) tv);
                            continue;
                        case CREATE:
                            new_values[i] = create_transfer((createTransferType) tv, i);
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
            System.out.println(0);
            return null;
        }

        private dataType get_data_type_by_name(String name){
            for(dataType dt : array){
                if(dt.name.equals(name)){
                    return dt;
                }
            }
            System.out.println(1);
            return null;
        }

        private dataType direct_transfer(directTransferType tv){
            return get_data_type_by_name(tv.name);
        }

        private dataType rename_transfer(renameTransferType tv){
            dataType dt = get_data_type_by_name(tv.name);
            dt.name = tv.new_name;
            return dt;
        }

        private dataType create_transfer(createTransferType tv, int index){
            inputType it = get_input_type_by_name(version+1, tv.name);
            System.out.println(tv.name);
            switch (it.getValueType()){
                case NUM:
                    return msv.new intType(it.name, (int) it.default_value);
                case STRING:
                    return msv.new stringType(it.name, (String) it.default_value);
            }
            System.out.println(2);
            return null;
        }
    }
}
