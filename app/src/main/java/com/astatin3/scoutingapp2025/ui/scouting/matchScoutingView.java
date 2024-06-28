package com.astatin3.scoutingapp2025.ui.scouting;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.scoutingData.ScoutingVersion;
import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.fileEditor;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.types.frcMatch;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.utility.AutoSaveManager;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class matchScoutingView extends ConstraintLayout {
    public matchScoutingView(Context context) {
        super(context);
    }
    public matchScoutingView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    FragmentScoutingBinding binding;
    String alliance_position;
    String evcode;
    int cur_match_num;
    frcEvent event;
    AutoSaveManager asm = new AutoSaveManager(this::save);

    public void save(){
        System.out.println("Saved!");
    }

    public void init(FragmentScoutingBinding tmp_binding){
        binding = tmp_binding;

        alliance_position = latestSettings.settings.get_alliance_pos();
        evcode = latestSettings.settings.get_evcode();
        event = frcEvent.decode(fileEditor.readFile(evcode + ".eventdata"));

        binding.eventcode.setText(evcode);
        binding.alliancePosText.setText(alliance_position);

        cur_match_num = latestSettings.settings.get_match_num();
        update_match_num();

        binding.nextButton.setOnClickListener(v -> {
            latestSettings.settings.set_match_num(cur_match_num+1);
            cur_match_num += 1;
            update_match_num();
        });

        binding.backButton.setOnClickListener(v -> {
            latestSettings.settings.set_match_num(cur_match_num-1);
            cur_match_num -= 1;
            update_match_num();
        });
    }

    private void update_match_num(){
//        cur_match_num = latestSettings.settings.get_match_num();

        binding.matchnum.setText(String.valueOf(cur_match_num+1));

        System.out.println(cur_match_num);

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

        update_scouting_data();
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

        return team;
    }

    ArrayList<View> views = new ArrayList<>();

    public void update_scouting_data(){

        asm.start();

        frcMatch match = event.matches.get(cur_match_num);
        frcTeam team = get_team(match);

        binding.teamName.setText(team.teamName);
        binding.teamDescription.setText(team.getDescription());

        for(int i = 0; i < views.size(); i++){
            binding.MatchScoutArea.removeView(views.get(i));
        }

        views = new ArrayList<>();

        boolean success = fields.load();

        if(!success){
            TextView tv = new TextView(getContext());
            tv.setText("Failed to load fields.\nTry to either download or create match scouting fields.");
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            binding.MatchScoutArea.addView(tv);
            views.add(tv);
            return;
        }

        if(fields.values.length == 0){
            return;
        }

        ScoutingVersion.inputType[] values = fields.values[fields.values.length-1];

//        int prev_text = binding.teamDescription.getId();

        for(int i = 0 ; i < values.length; i++){
            TextView tv = new TextView(getContext());
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextSize(24);
            binding.MatchScoutArea.addView(tv);
            views.add(tv);

            switch (values[i].getInputType()){
                case SLIDER:
                    ScoutingVersion.sliderType sliderType = (ScoutingVersion.sliderType) values[i];
                    tv.setText(sliderType.name);

                    Slider slider = new Slider(getContext());

                    slider.setStepSize((float) 1 / sliderType.max);
                    slider.setValue((int) sliderType.default_value / (float) sliderType.max);

                    slider.addOnChangeListener(new Slider.OnChangeListener() {
                        @Override
                        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                            System.out.println(value * sliderType.max);
                            asm.update();
                        }
                    });

                    binding.MatchScoutArea.addView(slider);
                    views.add(slider);
                    break;
                case DROPDOWN:
                    ScoutingVersion.dropdownType dropdownType = (ScoutingVersion.dropdownType) values[i];
                    tv.setText(dropdownType.name);
                    break;
                case NOTES_INPUT:
                    ScoutingVersion.notesType notesType = (ScoutingVersion.notesType) values[i];
                    tv.setText(notesType.name);
                    break;
            }

//            prev_text = tv.getId();
        }
    }
}
