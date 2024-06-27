package com.astatin3.scoutingapp2025.ui.scouting;

import static com.astatin3.scoutingapp2025.ScoutingDataVersion.fields.sv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.ScoutingDataVersion.ScoutingVersion;
import com.astatin3.scoutingapp2025.ScoutingDataVersion.fields;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.fileEditor;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.types.frcMatch;
import com.astatin3.scoutingapp2025.types.frcTeam;

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

    public void update_scouting_data(){
        frcMatch match = event.matches.get(cur_match_num);

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

        binding.teamName.setText(team.teamName);
        binding.teamDescription.setText(team.getDescription());

        fields.load();
        ScoutingVersion.inputType[] values = fields.values[fields.values.length-1];

        int prev_text = binding.teamDescription.getId();

        for(int i = 0 ; i < values.length; i++){
            TextView tv = new TextView(getContext());

//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
////            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//            params.addRule(RelativeLayout.ALIGN_LEFT, prev_text);
//            tv.setLayoutParams(params);


            String text = "Fallback";
            switch (values[i].getInputType()){
                case SLIDER:
                    text = "Slider";
                    break;
                case DROPDOWN:
                    text = "Dropdown";
                    break;
                case NOTES_INPUT:
                    text = "Notes";
                    break;
            }
            System.out.println(text);
            tv.setText(text);

            binding.MatchScoutArea.addView(tv);
            prev_text = tv.getId();
        }
    }
}
