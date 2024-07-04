package com.astatin3.scoutingapp2025.ui.data;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.astatin3.scoutingapp2025.databinding.FragmentDataBinding;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.types.frcMatch;

import java.util.Arrays;

public class statusView extends ScrollView {
    public statusView(@NonNull Context context) {
        super(context);
    }
    public statusView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }
    public static int color_found = 0x7f00ff00;
    public static int color_not_found = 0x7f7f0000;
    private void addTableText(TableRow tr, String textStr){
        TextView text = new TextView(getContext());
        text.setTextSize(18);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); // Text align center
        text.setText(textStr);
        tr.addView(text);
    }
    public void start(FragmentDataBinding binding, frcEvent event) {
        binding.matchTable.removeAllViews();
        binding.matchTable.setStretchAllColumns(true);
        add_pit_scouting(binding, event);
        add_match_scouting(binding, event);
    }

    public void add_pit_scouting(FragmentDataBinding binding, frcEvent event){
        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText("Pit Scouting");
        tv.setTextSize(28);
        binding.matchTable.addView(tv);

        int[] teams = new int[event.teams.size()];

        for(int i = 0 ; i < event.teams.size(); i++){
            teams[i] = event.teams.get(i).teamNumber;
        }

        Arrays.sort(teams);

        TableRow tr = null;
        for(int i=0; i < event.teams.size(); i++){
//            frcTeam team = event.teams.get(i);
            int num = teams[i];

            if(i % 7 == 0){
                if(i != 0)
                    binding.matchTable.addView(tr);
                tr = new TableRow(getContext());
            }

            TextView text = new TextView(getContext());
            text.setTextSize(18);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            text.setText(String.valueOf(num));
            if(fileEditor.fileExist(event.eventCode + "-" + num + ".pitscoutdata")){
                text.setBackgroundColor(color_found);
            }else{
                text.setBackgroundColor(color_not_found);
            }
            tr.addView(text);
        }
        if(tr != null)
            binding.matchTable.addView(tr);
    }


    public void add_match_scouting(FragmentDataBinding binding, frcEvent event){

        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText("Match Scouting");
        tv.setTextSize(28);
        binding.matchTable.addView(tv);

        TableRow tr = new TableRow(getContext());
        addTableText(tr, "#");
        addTableText(tr, "Red-1");
        addTableText(tr, "Red-2");
        addTableText(tr, "Red-3");
        addTableText(tr, "Blue-1");
        addTableText(tr, "Blue-2");
        addTableText(tr, "Blue-3");
        binding.matchTable.addView(tr);

        for(frcMatch match : event.matches){

            tr = new TableRow(getContext());
            addTableText(tr, String.valueOf(match.matchIndex));
//
            for(int i=0;i<6;i++){
                TextView text = new TextView(getContext());
                text.setTextSize(18);
                text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                int team_num;
                String alliance_position;

                if(i < 3){
                    team_num = match.redAlliance[i];
                    alliance_position = "red-"+(i+1);
                }else{
                    team_num = match.blueAlliance[i-3];
                    alliance_position = "blue-"+(i-2);
                }

                text.setText(String.valueOf(team_num));
                if(fileEditor.fileExist(event.eventCode + "-" + match.matchIndex + "-" + alliance_position + "-" + team_num + ".matchscoutdata")){
                    text.setBackgroundColor(color_found);
                }else{
                    text.setBackgroundColor(color_not_found);
                }
                tr.addView(text);
            }

//            addTableText(tr, String.valueOf(match.matchIndex));
//            addTableText(tr, String.valueOf(match.blueAlliance[0]));
//            addTableText(tr, String.valueOf(match.blueAlliance[1]));
//            addTableText(tr, String.valueOf(match.blueAlliance[2]));
//            addTableText(tr, String.valueOf(match.redAlliance[0]));
//            addTableText(tr, String.valueOf(match.redAlliance[1]));
//            addTableText(tr, String.valueOf(match.redAlliance[2]));
//            if (toggle) {
//                tr.setBackgroundColor(0x30000000);
//            }
//
//            toggle = !toggle;
            binding.matchTable.addView(tr);
        }
    }
}
