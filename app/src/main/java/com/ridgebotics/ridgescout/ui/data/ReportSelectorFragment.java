package com.ridgebotics.ridgescout.ui.data;

import static androidx.navigation.fragment.FragmentKt.findNavController;
import static com.ridgebotics.ridgescout.utility.DataManager.evcode;
import static com.ridgebotics.ridgescout.utility.DataManager.event;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ridgebotics.ridgescout.R;
import com.ridgebotics.ridgescout.databinding.FragmentDataReportSelectorBinding;
import com.ridgebotics.ridgescout.types.frcMatch;
import com.ridgebotics.ridgescout.utility.AlertManager;
import com.ridgebotics.ridgescout.utility.DataManager;
import com.ridgebotics.ridgescout.utility.settingsManager;

public class ReportSelectorFragment extends Fragment {
    FragmentDataReportSelectorBinding binding;

    private final int teamNum = settingsManager.getTeamNum();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDataReportSelectorBinding.inflate(inflater, container, false);
        binding.matchTable.setStretchAllColumns(true);

        DataManager.reload_event();
        frcMatch[] teamMatches = event.getTeamMatches(teamNum);

        if(teamMatches.length == 0){
            AlertManager.error("Team number " + teamNum + " could not be found in event " + evcode);
            findNavController(this).navigate(R.id.action_navigation_data_report_selector_to_navigation_data);
        }

        for(int i = 0; i < teamMatches.length; i++){
            addTableRow(teamMatches[i]);
        }


        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void addTableRow(frcMatch match){
        TableRow tr = new TableRow(getContext());
        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(20,20,20,20);
        tr.setLayoutParams(rowParams);
        tr.setPadding(20,20,20,20);
        tr.setBackgroundColor(0x5000ff00);
        binding.matchTable.addView(tr);

        TextView tv = new TextView(getContext());
        tv.setText("Match " + match.matchIndex);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tr.addView(tv);

        tv = new TextView(getContext());
        tv.setText("Pos " + match.getTeamAlliance(teamNum));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tr.addView(tv);

        tr.setOnClickListener(v -> {
            ReportFragment.setMatch(match);
            findNavController(this).navigate(R.id.action_navigation_data_report_selector_to_navigation_data_report);
        });
    }
}
