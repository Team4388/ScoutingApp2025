package com.astatin3.scoutingapp2025.ui.scouting;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;

public class scoutingFragment extends Fragment {

    private FragmentScoutingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentScoutingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttons.setVisibility(View.VISIBLE);
        binding.matchScoutingView.setVisibility(View.GONE);
        binding.pitScoutingView.setVisibility(View.GONE);

        if(latestSettings.settings.get_evcode().equals("unset")){
            binding.noEventError.setVisibility(View.VISIBLE);
            binding.buttons.setVisibility(View.GONE);
        }

        binding.matchScoutingButton.setOnClickListener(v -> {
            binding.buttons.setVisibility(View.GONE);
            binding.matchScoutingView.setVisibility(View.VISIBLE);
            binding.matchScoutingView.init(binding);
        });

        binding.pitScoutingButton.setOnClickListener(v -> {
            binding.pitScoutingView.setVisibility(View.VISIBLE);
            binding.buttons.setVisibility(View.GONE);
            //            binding.pitScoutArea.setVisibility(View.VISIBLE);
            binding.pitScoutingView.init(binding);
        });

        return root;
    }
}