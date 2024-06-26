package com.astatin3.scoutingapp2025.ui.scouting;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.ScoutingDataVersion.MatchScouting;
import com.astatin3.scoutingapp2025.ScoutingDataVersion.ScoutingVersion;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;

import java.util.Arrays;

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
            MatchScouting.MatchScoutingArray msa = MatchScouting.ms.new MatchScoutingArray(0, new ScoutingVersion.dataType[]{
                MatchScouting.msv.new stringType("name", "test-username"),
                MatchScouting.msv.new intType("How good is robot", 12)
            });
//            System.out.println(Arrays.toString(msa.array));

            msa.update();

            for(ScoutingVersion.dataType dt : msa.array){
                if(dt == null) continue;
                switch (dt.getValueType()){
                    case NUM:
                        System.out.println(dt.name + " " + (int) dt.get());
                        break;
                    case STRING:
                        System.out.println(dt.name + " " + (String) dt.get());
                        break;
                }
//                case ScoutingVersion.valueTypes.NUM:

            }
        });
//
//        binding.pitScoutingButton.setOnClickListener(v -> {
//            msv1 msdata = new msv1();
////            msdata.types = MatchScoutingVersion.dataType[]5{};
//            System.out.println(msdata.load("test.matchscoutdata"));
//            for(MatchScoutingVersion.dataType data : msdata.getTypes().fields){
////                System.out.println(data.getType());
//                switch (data.getType()){
//                    case 0:
//                        System.out.println((int)data.get());
//                        break;
//                    case 1:
//                        System.out.println((String) data.get());
//                        break;
//                }
//            }
//        });

//        msdata.set

        return root;
    }
}