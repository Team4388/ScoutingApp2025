package com.astatin3.scoutingapp2025.ui.scouting;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.MatchScoutingVersionStack.MatchScoutingVersion;
import com.astatin3.scoutingapp2025.MatchScoutingVersionStack.msv1;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.fileEditor;

public class scoutingFragment extends Fragment {

    private FragmentScoutingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentScoutingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(latestSettings.settings.get_evcode().equals("unset")){
            binding.noEventError.setVisibility(View.VISIBLE);
            binding.buttons.setVisibility(View.GONE);
        }

        binding.matchScoutingButton.setOnClickListener(v -> {
            msv1 msdata = new msv1();
            System.out.println(msdata.save("test.matchscoutdata"));
//            System.out.println(msdata.test().length);
        });

        binding.pitScoutingButton.setOnClickListener(v -> {
            msv1 msdata = new msv1();
            System.out.println(msdata.load("test.matchscoutdata"));
            for(MatchScoutingVersion.dataType data : msdata.getTypes()){
//                System.out.println(data.getType());
                switch ((int)data.getType()){
                    case 0:
                        System.out.println((int)data.get());
                        break;
                    case 1:
                        System.out.println((String) data.get());
                        break;
                }
            }
        });

//        msdata.set

        return root;
    }
}