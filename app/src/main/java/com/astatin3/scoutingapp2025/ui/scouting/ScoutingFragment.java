package com.astatin3.scoutingapp2025.ui.scouting;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import static com.astatin3.scoutingapp2025.utility.DataManager.event;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.ui.TeamSelectorFragment;
import com.astatin3.scoutingapp2025.utility.DataManager;

public class ScoutingFragment extends Fragment {

    private FragmentScoutingBinding binding;
    private boolean is_main_page = true;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentScoutingBinding.inflate(inflater, container, false);

        binding.buttons.setVisibility(View.VISIBLE);

        String evcode = latestSettings.settings.get_evcode();

        if(evcode.equals("unset")){
            binding.noEventError.setVisibility(View.VISIBLE);
            binding.buttons.setVisibility(View.GONE);
            is_main_page = false;
            return binding.getRoot();
        }

        DataManager.reload_event();

        if(event.matches.isEmpty())
            binding.matchScoutingButton.setVisibility(View.GONE);

        binding.matchScoutingButton.setOnClickListener(v -> {
            findNavController(this).navigate(R.id.action_navigation_scouting_to_navigation_match_scouting);
        });

        binding.pitScoutingButton.setOnClickListener(v -> {
            TeamSelectorFragment.setPits_mode(true);
            TeamSelectorFragment.setOnSelect(new TeamSelectorFragment.onTeamSelected() {
                @Override
                public void onSelect(TeamSelectorFragment self, frcTeam team) {
                    PitScoutingFragment.setTeam(team);
                    findNavController(self).navigate(R.id.action_navigation_team_selector_to_navigation_pit_scouting);
                }
            });
            findNavController(this).navigate(R.id.action_navigation_scouting_to_navigation_team_selector);
        });

        binding.statusButton.setOnClickListener(v -> {
            findNavController(this).navigate(R.id.action_navigation_scouting_to_navigation_scouting_status);
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK
                        && !is_main_page){

//                    binding.buttons.setVisibility(View.VISIBLE);
//                    binding.matchScoutingView.setVisibility(View.GONE);
//                    binding.pitScoutingView.setVisibility(View.GONE);
                    is_main_page = true;

                    return true;
                }
                return false;
            }
        });
    }

}