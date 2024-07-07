package com.astatin3.scoutingapp2025.ui.data;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.MainActivity;
import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentDataBinding;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.astatin3.scoutingapp2025.types.frcEvent;

public class dataFragment extends Fragment {

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
            binding.statusButton.setVisibility(View.GONE);
            binding.teamsButton.setVisibility(View.GONE);
            binding.compileButton.setVisibility(View.GONE);
            binding.fieldsButton.setVisibility(View.VISIBLE);


            binding.matchTable.setVisibility(View.GONE);
            return root;
        }

        frcEvent event = frcEvent.decode(fileEditor.readFile(evcode + ".eventdata"));

        binding.statusButton.setOnClickListener(v -> {
            binding.buttons.setVisibility(View.GONE);
            binding.statusView.setVisibility(View.VISIBLE);
            binding.statusView.start(binding, event);
            submenu = true;
        });

        binding.teamsButton.setOnClickListener(v -> {
            binding.buttons.setVisibility(View.GONE);
            binding.teamsView.setVisibility(View.VISIBLE);
            binding.teamsView.init(binding, event);
            submenu = true;
        });



        binding.compileButton.setOnClickListener(v -> {
//            binding.buttons.setVisibility(View.GONE);
//            binding.teamsView.setVisibility(View.VISIBLE);
//            binding.teamsView.init(binding, event);
//            submenu = true;
        });

        binding.fieldsButton.setOnClickListener(v -> {
            binding.buttons.setVisibility(View.GONE);
            binding.fieldsView.setVisibility(View.VISIBLE);
            binding.fieldsView.init(binding);
            submenu = true;
        });

        show_ui();

        return root;
    }

    public void show_ui(){
        binding.buttons.setVisibility(View.VISIBLE);
        binding.statusView.setVisibility(View.GONE);
        binding.teamsView.setVisibility(View.GONE);
        submenu = false;
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

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && submenu){
                    // handle back button's click listener

                    show_ui();

                    return true;
                }
                return false;
            }
        });
    }
}