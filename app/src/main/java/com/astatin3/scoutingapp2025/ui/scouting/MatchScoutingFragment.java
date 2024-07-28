package com.astatin3.scoutingapp2025.ui.scouting;

import static com.astatin3.scoutingapp2025.utility.DataManager.evcode;
import static com.astatin3.scoutingapp2025.utility.DataManager.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentMatchScoutingBinding;
import com.astatin3.scoutingapp2025.scoutingData.ScoutingDataWriter;
import com.astatin3.scoutingapp2025.types.data.dataType;
import com.astatin3.scoutingapp2025.types.frcMatch;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.types.input.inputType;
import com.astatin3.scoutingapp2025.utility.AutoSaveManager;
import com.astatin3.scoutingapp2025.utility.DataManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.util.ArrayList;
import java.util.function.Function;

public class MatchScoutingFragment extends Fragment {

    private FragmentMatchScoutingBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMatchScoutingBinding.inflate(inflater, container, false);

        DataManager.reload_match_fields();

        alliance_position = latestSettings.settings.get_alliance_pos();
        username = latestSettings.settings.get_username();

        binding.eventcode.setText(evcode);
        binding.alliancePosText.setText(alliance_position);

        binding.teamDescription.setVisibility(View.GONE);
        binding.teamName.setVisibility(View.GONE);
        clear_fields();
        binding.teamDescription.setVisibility(View.VISIBLE);
        binding.teamName.setVisibility(View.VISIBLE);

        if(DataManager.match_values == null || DataManager.match_values.length == 0){
            TextView tv = new TextView(getContext());
            tv.setText("Failed to load fields.\nTry to either download or create match scouting fields.");
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            binding.MatchScoutArea.addView(tv);
            return binding.getRoot();
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

        create_fields();
        update_scouting_data();

        return binding.getRoot();
    }


    private static final int unsaved_color = 0x60ff0000;
    private static final int saved_color = 0x6000ff00;

    String alliance_position;
    int cur_match_num;
    String username;
    String filename;

    boolean edited = false;

    TextView[] titles;

    AutoSaveManager asm = new AutoSaveManager(this::save);

    ArrayList<dataType> dataTypes;



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


    public void clear_fields(){
        int childCount = binding.MatchScoutArea.getChildCount();
        View[] views = new View[childCount];

        for(int i = 0; i < childCount; i++){
            views[i] = binding.MatchScoutArea.getChildAt(i);
        }

        for(int i = 0; i < childCount; i++){
            if(!views[i].isShown()) continue;
            binding.MatchScoutArea.removeView(views[i]);
        }
    }

    private int default_text_color = 0;

    private void create_fields(){
        if(asm.isRunning){
            asm.stop();
        }

        titles = new TextView[DataManager.match_latest_values.length];

        for(int i = 0 ; i < DataManager.match_latest_values.length; i++) {
            final TextView tv = new TextView(getContext());
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setText(DataManager.match_latest_values[i].name);
            tv.setPadding(8,8,8,8);
            tv.setTextSize(24);
            titles[i] = tv;

            default_text_color = tv.getCurrentTextColor();

            final View v = DataManager.match_latest_values[i].createView(getContext(), new Function<dataType, Integer>() {
                @Override
                public Integer apply(dataType dataType) {
//                    edited = true;
                    if(asm.isRunning)
                        update_asm();
                    return 0;
                }
            });

            binding.MatchScoutArea.addView(tv);
            int fi = i;
            tv.setOnClickListener(p -> {
//                boolean blank = !latest_values[fi].getViewValue().isNull();

//                System.out.println(blank);

                asm.update();

                if(!DataManager.match_latest_values[fi].isBlank){
                    tv.setBackgroundColor(0xffff0000);
                    tv.setTextColor(0xff000000);
                    DataManager.match_latest_values[fi].nullify();
                }else{
                    tv.setBackgroundColor(0x00000000);
                    tv.setTextColor(default_text_color);
                    DataManager.match_latest_values[fi].setViewValue(DataManager.match_latest_values[fi].default_value);
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
        for(int i = 0; i < DataManager.match_latest_values.length; i++){
            inputType input = DataManager.match_latest_values[i];
            input.setViewValue(input.default_value);

            titles[i].setBackgroundColor(0x00000000);
            titles[i].setTextColor(default_text_color);
        }
    }



    public void get_fields(){

        ScoutingDataWriter.ParsedScoutingDataResult psdr = ScoutingDataWriter.load(filename, DataManager.match_values, DataManager.match_transferValues);
        dataType[] types = psdr.data.array;

        for(int i = 0; i < DataManager.match_latest_values.length; i++){
//            types[i] = latest_values[i].getViewValue();
            DataManager.match_latest_values[i].setViewValue(types[i]);

            if(DataManager.match_latest_values[i].isBlank){
                titles[i].setBackgroundColor(0xffff0000);
                titles[i].setTextColor(0xff000000);
            }else{
                titles[i].setBackgroundColor(0x00000000);
                titles[i].setTextColor(default_text_color);
            }
        }
    }



    public void save_fields(){

        dataType[] types = new dataType[DataManager.match_latest_values.length];

        for(int i = 0; i < DataManager.match_latest_values.length; i++){
            types[i] = DataManager.match_latest_values[i].getViewValue();
        }

        if(ScoutingDataWriter.save(DataManager.match_values.length-1, username, filename, types))
            System.out.println("Saved!");
        else
            System.out.println("Error saving");
    }
}
