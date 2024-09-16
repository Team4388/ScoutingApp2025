package com.ridgebotics.ridgescout.utility;

import com.ridgebotics.ridgescout.SettingsVersionStack.latestSettings;
import com.ridgebotics.ridgescout.scoutingData.fields;
import com.ridgebotics.ridgescout.scoutingData.transfer.transferType;
import com.ridgebotics.ridgescout.types.frcEvent;
import com.ridgebotics.ridgescout.types.input.inputType;

public class DataManager {
    public static String evcode;
    public static frcEvent event;
    public static void reload_event(){
        evcode = getevcode();
        event = frcEvent.decode(fileEditor.readFile(evcode + ".eventdata"));
    }

    public static String getevcode() {
        return latestSettings.settings.get_evcode();
    }

    public static inputType[][] match_values;
    public static inputType[] match_latest_values;
    public static transferType[][] match_transferValues;
    public static void reload_match_fields(){
        match_values = fields.load(fields.matchFieldsFilename);
        match_latest_values = match_values[match_values.length-1];
        match_transferValues = transferType.get_transfer_values(match_values);
    }

    public static inputType[][] pit_values;
    public static inputType[] pit_latest_values;
    public static transferType[][] pit_transferValues;
    public static void reload_pit_fields(){
        pit_values = fields.load(fields.pitsFieldsFilename);
        pit_latest_values = pit_values[pit_values.length-1];
        pit_transferValues = transferType.get_transfer_values(pit_values);
    }
}
