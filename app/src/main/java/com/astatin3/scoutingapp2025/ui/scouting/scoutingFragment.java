package com.astatin3.scoutingapp2025.ui.scouting;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.MainActivity;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.utility.fileEditor;

public class scoutingFragment extends Fragment {

    private FragmentScoutingBinding binding;
    private boolean is_main_page = true;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentScoutingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttons.setVisibility(View.VISIBLE);
        binding.matchScoutingView.setVisibility(View.GONE);
        binding.pitScoutingView.setVisibility(View.GONE);


        String evcode = latestSettings.settings.get_evcode();

        if(evcode.equals("unset")){
            binding.noEventError.setVisibility(View.VISIBLE);
            binding.buttons.setVisibility(View.GONE);
            is_main_page = false;
            return root;
        }

        frcEvent event = frcEvent.decode(fileEditor.readFile(evcode + ".eventdata"));

        if(event.matches.isEmpty())
            binding.matchScoutingButton.setVisibility(View.GONE);

        binding.matchScoutingButton.setOnClickListener(v -> {
            binding.buttons.setVisibility(View.GONE);
            binding.matchScoutingView.setVisibility(View.VISIBLE);
            binding.matchScoutingView.init(binding, event);
            is_main_page = false;
        });

        binding.pitScoutingButton.setOnClickListener(v -> {
            binding.pitScoutingView.setVisibility(View.VISIBLE);
            binding.buttons.setVisibility(View.GONE);
            //            binding.pitScoutArea.setVisibility(View.VISIBLE);
            binding.pitScoutingView.init(binding, event);
            is_main_page = false;
        });

        return root;
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

                    binding.buttons.setVisibility(View.VISIBLE);
                    binding.matchScoutingView.setVisibility(View.GONE);
                    binding.pitScoutingView.setVisibility(View.GONE);
                    is_main_page = true;

                    return true;
                }
                return false;
            }
        });
    }

}