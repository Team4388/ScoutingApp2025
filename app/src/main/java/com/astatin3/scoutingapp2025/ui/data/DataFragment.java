package com.astatin3.scoutingapp2025.ui.data;


import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentDataBinding;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.ui.TeamSelectorFragment;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.astatin3.scoutingapp2025.types.frcEvent;

public class DataFragment extends Fragment {

    private FragmentDataBinding binding;

    private boolean submenu = false;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String evcode = latestSettings.settings.get_evcode();



        if(evcode.equals("unset")){
            binding.noEventError.setVisibility(View.VISIBLE);

            binding.buttons.setVisibility(View.VISIBLE);
            binding.teamsButton.setVisibility(View.GONE);
            binding.compareButton.setVisibility(View.GONE);
            binding.reportButton.setVisibility(View.GONE);
            binding.fieldsButton.setVisibility(View.VISIBLE);


            return root;
        }

        frcEvent event = frcEvent.decode(fileEditor.readFile(evcode + ".eventdata"));

        binding.teamsButton.setOnClickListener(v -> {
            TeamSelectorFragment.setPits_mode(false);
            TeamSelectorFragment.setOnSelect(new TeamSelectorFragment.onTeamSelected() {
                @Override
                public void onSelect(TeamSelectorFragment self, frcTeam team) {
                    TeamsFragment.setTeam(team);
                    findNavController(self).navigate(R.id.action_navigation_team_selector_to_navigation_data_teams);
                }
            });
            findNavController(this).navigate(R.id.action_navigation_data_to_navigation_team_selector);
        });

        binding.compareButton.setOnClickListener(v -> {
            findNavController(this).navigate(R.id.action_navigation_data_to_navigation_data_compare);
        });

        binding.reportButton.setOnClickListener(v -> {
            findNavController(this).navigate(R.id.action_navigation_data_to_navigation_data_report);
        });

        binding.fieldsButton.setOnClickListener(v -> {
            findNavController(this).navigate(R.id.action_navigation_data_to_navigation_data_fields_chooser);
        });

        return root;
    }
}