package com.astatin3.scoutingapp2025.ui.scouting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.scoutingData.ScoutingDataWriter;
import com.astatin3.scoutingapp2025.scoutingData.ScoutingVersion;
import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.types.frcMatch;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.utility.AutoSaveManager;
import com.google.android.material.slider.Slider;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.function.Function;

public class matchScoutingView extends ConstraintLayout {
    public matchScoutingView(Context context) {
        super(context);
    }
    public matchScoutingView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    private static final int unsaved_color = 0x60ff0000;
    private static final int saved_color = 0x6000ff00;

    FragmentScoutingBinding binding;
    String alliance_position;
    String evcode;
    int cur_match_num;
    frcEvent event;
    String filename;
    String username;

    boolean edited = false;

    ScoutingVersion.inputType[][] values;
    ScoutingVersion.inputType[] latest_values;
    ScoutingVersion.transferType[][] transferValues;

    AutoSaveManager asm = new AutoSaveManager(this::save);

    ArrayList<ScoutingVersion.dataType> dataTypes;



    public void save(){
        System.out.println("Saved!");
        edited = false;
        set_indicator_color(saved_color);
//        fileEditor.createFile(filename);
        save_fields();
    }

    public void set_indicator_color(int color){
        binding.fileIndicator.setBackgroundColor(color);
    }

    public void update_asm(){
//        v.getBackground().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.DARKEN);
        edited = true;
        set_indicator_color(unsaved_color);
        asm.update();
    }




    public void init(FragmentScoutingBinding tmp_binding){
        binding = tmp_binding;

        alliance_position = latestSettings.settings.get_alliance_pos();
        evcode = latestSettings.settings.get_evcode();
        event = frcEvent.decode(fileEditor.readFile(evcode + ".eventdata"));
        username = latestSettings.settings.get_username();

        binding.eventcode.setText(evcode);
        binding.alliancePosText.setText(alliance_position);

        values = fields.load(fields.matchFieldsFilename);

        if(values == null || values.length == 0){
            TextView tv = new TextView(getContext());
            tv.setText("Failed to load fields.\nTry to either download or create match scouting fields.");
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            binding.MatchScoutArea.addView(tv);
            return;
        }

        cur_match_num = latestSettings.settings.get_match_num();
        update_match_num();

        binding.nextButton.setOnClickListener(v -> {
            if(edited) save();
            latestSettings.settings.set_match_num(cur_match_num+1);
            cur_match_num += 1;
            update_match_num();
            update_scouting_data();
        });

        binding.backButton.setOnClickListener(v -> {
            if(edited) save();
            latestSettings.settings.set_match_num(cur_match_num-1);
            cur_match_num -= 1;
            update_match_num();
            update_scouting_data();
        });

//        binding.middleButton.setOnClickListener(v -> {
//            if(edited) save();
//        });

        latest_values = values[values.length-1];
        transferValues = fields.sv.get_transfer_values(values);

        create_fields();
        update_scouting_data();
    }




    private void create_fields(){
        if(asm.isRunning){
            asm.stop();
        }

        for(int i = 0 ; i < latest_values.length; i++) {
            TextView tv = new TextView(getContext());
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setText(latest_values[i].name);
            tv.setTextSize(24);
            binding.MatchScoutArea.addView(tv);

            View v = latest_values[i].createView(getContext(), new Function<ScoutingVersion.dataType, Integer>() {
                @Override
                public Integer apply(ScoutingVersion.dataType dataType) {
//                    edited = true;
                    if(asm.isRunning)
                        update_asm();
                    return 0;
                }
            });

            binding.MatchScoutArea.addView(v);
        }
    }




    private void update_match_num(){
//        cur_match_num = latestSettings.settings.get_match_num();

        edited = false;

        binding.matchnum.setText(String.valueOf(cur_match_num+1));

        if(cur_match_num <= 0){
            binding.backButton.setVisibility(View.GONE);
        }else{
            binding.backButton.setVisibility(View.VISIBLE);
        }

        if(cur_match_num >= event.matches.size()-1){
            binding.nextButton.setVisibility(View.GONE);
        }else{
            binding.nextButton.setVisibility(View.VISIBLE);
        }
    }




    private frcTeam get_team(frcMatch match){

        // Get team number
        String[] split = alliance_position.split("-");
        Integer team_num = null;

        switch (split[0]){
            case "red":
                team_num = match.redAlliance[Integer.parseInt(split[1])-1];
                break;
            case "blue":
                team_num = match.blueAlliance[Integer.parseInt(split[1])-1];
                break;
        }

        binding.barTeamNum.setText(String.valueOf(team_num));

        frcTeam team = null;
        for(int i=0; i < event.teams.size(); i++){
            frcTeam tmpteam = event.teams.get(i);
            if(tmpteam.teamNumber == team_num){
                team = tmpteam;
                break;
            }
        }

        filename = evcode + "-" + (cur_match_num+1) + "-" + alliance_position + "-" + team_num + ".matchscoutdata";

        return team;
    }




    public void update_scouting_data(){

        frcMatch match = event.matches.get(cur_match_num);
        frcTeam team = get_team(match);

        binding.teamName.setText(team.teamName);
        binding.teamDescription.setText(team.getDescription());

        boolean new_file = !fileEditor.fileExist(filename);

        if(asm.isRunning){
            asm.stop();
        }

        if(new_file){
            default_fields();
            set_indicator_color(unsaved_color);
        }else{
            get_fields();
            set_indicator_color(saved_color);
        }

        asm.start();

    }



    public void default_fields(){
        for(int i = 0; i < latest_values.length; i++){
            ScoutingVersion.inputType input = latest_values[i];
            input.setViewValue(input.default_value);
        }
    }



    public void get_fields(){

        ScoutingDataWriter.ParsedScoutingDataResult psdr = ScoutingDataWriter.load(filename, values, transferValues);
        ScoutingVersion.dataType[] types = psdr.data.array;

        for(int i = 0; i < latest_values.length; i++){
//            types[i] = latest_values[i].getViewValue();
            latest_values[i].setViewValue(types[i]);
        }
    }



    public void save_fields(){

        ScoutingVersion.dataType[] types = new ScoutingVersion.dataType[latest_values.length];

        for(int i = 0; i < latest_values.length; i++){
            types[i] = latest_values[i].getViewValue();
        }

        System.out.println(ScoutingDataWriter.save(values.length-1, username, filename, types));
    }
}
