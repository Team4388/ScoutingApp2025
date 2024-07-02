package com.astatin3.scoutingapp2025.ui.scouting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.scoutingData.ScoutingDataWriter;
import com.astatin3.scoutingapp2025.scoutingData.ScoutingVersion;
import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.types.frcMatch;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.utility.AutoSaveManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class pitScoutingView extends ConstraintLayout {
    public pitScoutingView(Context context) {
        super(context);
    }
    public pitScoutingView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }


    private static final int unsaved_color = 0x60ff0000;
    private static final int saved_color = 0x6000ff00;

    boolean edited = false;

    FragmentScoutingBinding binding;
    String alliance_position;
    String evcode;
    frcEvent event;
    String filename;
    String username;

    ScoutingVersion.inputType[][] values;
    ScoutingVersion.inputType[] latest_values;
    ScoutingVersion.transferType[][] transferValues;

    AutoSaveManager asm = new AutoSaveManager(this::save);

    ArrayList<ScoutingVersion.dataType> dataTypes;

    public void save(){
        System.out.println("Saved!");
        edited = false;
        set_indicator_color(saved_color);

        ScoutingVersion.dataType[] types = new ScoutingVersion.dataType[latest_values.length];

        for(int i = 0; i < latest_values.length; i++){
            types[i] = latest_values[i].getViewValue();
        }

        System.out.println(ScoutingDataWriter.save(values.length-1, username, filename, types));
    }

    public void set_indicator_color(int color){
        binding.pitFileIndicator.setBackgroundColor(color);
    }

    public void update_asm(){
//        v.getBackground().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.DARKEN);
        edited = true;
        set_indicator_color(unsaved_color);
        asm.update();
    }


    public void init(FragmentScoutingBinding tmp_binding, frcEvent event){
        binding = tmp_binding;

        evcode = event.eventCode;
        this.event = event;
        username = latestSettings.settings.get_username();

        binding.eventcode.setText(evcode);

        binding.pitBackButton.setOnClickListener(view -> {
            if(edited) save();
            binding.pitTeamName.setVisibility(View.GONE);
            binding.pitTeamDescription.setVisibility(View.GONE);
            clear_fields();
            load_teams();
        });

        values = fields.load(fields.pitsFieldsFilename);

        if(values == null || values.length == 0){
            TextView tv = new TextView(getContext());
            tv.setText("Failed to load fields.\nTry to either download or create pit scouting fields.");
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            binding.pitScoutArea.addView(tv);
            return;
        }

        latest_values = values[values.length-1];
        transferValues = fields.sv.get_transfer_values(values);

//        create_fields();
//        update_scouting_data();

        load_teams();
    }

    public void clear_fields(){
        int childCount = binding.pitScoutArea.getChildCount();
        View[] views = new View[childCount];

        for(int i = 0; i < childCount; i++){
            views[i] = binding.pitScoutArea.getChildAt(i);
        }

        for(int i = 0; i < childCount; i++){
            if(!views[i].isShown()) continue;
            binding.pitScoutArea.removeView(views[i]);
        }
    }

    public void load_teams(){
        binding.pitFileIndicator.setVisibility(View.GONE);
        binding.pitTeamName.setVisibility(View.GONE);
        binding.pitTeamDescription.setVisibility(View.GONE);

        clear_fields();


        int[] teamNums = new int[event.teams.size()];

        for(int i = 0 ; i < event.teams.size(); i++){
            teamNums[i] = event.teams.get(i).teamNumber;
        }

        Arrays.sort(teamNums);

        TableLayout table = new TableLayout(getContext());
        table.setStretchAllColumns(true);
        binding.pitScoutArea.addView(table);


        for(int i = 0; i < event.teams.size(); i++){
            frcTeam team = null;
            for(int a = 0 ; a < event.teams.size(); a++){
                if(event.teams.get(a).teamNumber == teamNums[i]){
                    team = event.teams.get(a);
                    break;
                }
            }

            TableRow tr = new TableRow(getContext());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(20,20,20,20);
            tr.setLayoutParams(rowParams);
            tr.setPadding(20,20,20,20);
            table.addView(tr);

            if(fileEditor.fileExist(evcode + "-" + team.teamNumber + ".pitscoutdata")){
                tr.setBackgroundColor(0x3000FF00);
            }else{
                tr.setBackgroundColor(0x30FF0000);
            }

            TextView tv = new TextView(getContext());
            tv.setText(String.valueOf(team.teamNumber));
            tv.setTextSize(20);
            tr.addView(tv);

            tv = new TextView(getContext());
            tv.setText(team.teamName);
            tv.setTextSize(16);
            tr.addView(tv);

            frcTeam finalTeam = team;
            tr.setOnClickListener(v -> {
//                binding.pitScoutArea.removeView(table);
                loadTeam(finalTeam);
            });
        }
    }

    public void loadTeam(frcTeam team){
        clear_fields();

        binding.pitFileIndicator.setVisibility(View.VISIBLE);
        binding.pitTeamName.setVisibility(View.VISIBLE);
        binding.pitTeamDescription.setVisibility(View.VISIBLE);

        binding.pitTeamName.setText(team.teamName);
        binding.pitTeamDescription.setText(team.getDescription());
        binding.pitBarTeamNum.setText(String.valueOf(team.teamNumber));

        binding.teamName.setText(team.teamName);
        binding.teamDescription.setText(team.getDescription());

        filename = evcode + "-" + team.teamNumber + ".pitscoutdata";

        boolean new_file = !fileEditor.fileExist(filename);

        if(asm.isRunning){
            asm.stop();
        }

        create_fields();

        if(new_file){
            default_fields();
            set_indicator_color(unsaved_color);
        }else{
            get_fields();
            set_indicator_color(saved_color);
        }

        asm.start();

    }

    private void create_fields() {
        if(asm.isRunning){
            asm.stop();
        }

        for(int i = 0 ; i < latest_values.length; i++) {
            TextView tv = new TextView(getContext());
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setText(latest_values[i].name);
            tv.setTextSize(24);
            binding.pitScoutArea.addView(tv);

            View v = latest_values[i].createView(getContext(), new Function<ScoutingVersion.dataType, Integer>() {
                @Override
                public Integer apply(ScoutingVersion.dataType dataType) {
//                    edited = true;
                    if(asm.isRunning)
                        update_asm();
                    return 0;
                }
            });

            binding.pitScoutArea.addView(v);
        }
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
}
