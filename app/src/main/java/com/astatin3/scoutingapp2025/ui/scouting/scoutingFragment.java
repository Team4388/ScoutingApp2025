package com.astatin3.scoutingapp2025.ui.scouting;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.BuiltByteParser;
import com.astatin3.scoutingapp2025.ByteBuilder;
import com.astatin3.scoutingapp2025.ScoutingDataVersion.fields;
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
//            byte[] bytes = fields.save();
//            System.out.println(bytes.length);
//            System.out.println(fields.load(bytes)[0].length);

            System.out.println(fields.load());

            fields.test();

//            fields.test();

//            ByteBuilder bb = new ByteBuilder();
//            try {
//                bb.addInt(1243);
//                bb.addStringArray(new String[]{"Test", "Test2", "Tljewhgr"});
//                bb.addIntArray(new int[]{4, 3, 8, 8});
//                byte[] bytes = bb.build();
//
//                BuiltByteParser bbp = new BuiltByteParser(bytes);
//                BuiltByteParser.parsedObject[] objects = bbp.parse().toArray(new BuiltByteParser.parsedObject[0]);
//
//                for(BuiltByteParser.parsedObject object : objects){
//                    switch (object.getType()){
//                        case 0:
//                            System.out.println((int) object.get());
//                            break;
//                        case 1:
//                            System.out.println((String) object.get());
//                            break;
//                        case 2:
//                            System.out.println(Arrays.toString((int[]) object.get()));
//                            break;
//                        case 3:
//                            System.out.println(Arrays.toString((String[]) object.get()));
//                            break;
//                    }
//                }
//
//
//            } catch (ByteBuilder.buildingException e) {
//                throw new RuntimeException(e);
//            } catch (BuiltByteParser.byteParsingExeption e) {
//                throw new RuntimeException(e);
//            }

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