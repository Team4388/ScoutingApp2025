package com.astatin3.scoutingapp2025.MatchScoutingVersionStack;

import com.astatin3.scoutingapp2025.BuiltByteParser;

import java.util.ArrayList;

public class msv0 extends MatchScoutingVersion{
    public static int version_num = 0;
    @Override
    public int getVersion(){return version_num;}
    public dataType[] types = {
            new stringData("name", "Unset-Username"),
            new intData("team", 0)
    };
    @Override
    public dataType[] getTypes(){
        return types;
    }
//    public MatchScoutingVersion update(MatchScoutingVersion prev) {
//        return prev;
//    }

    public static msv0 parse(int version, ArrayList<BuiltByteParser.parsedObject> parsedObjects) {
        msv0 msdata = new msv0();
        dataType[] types = msdata.getTypes();

        ((stringData)types[0]).str = (String)(parsedObjects.get(0)).get();
        ((intData)types[1]).num = (int)(parsedObjects.get(0)).get();

        return msdata;
    }
}
