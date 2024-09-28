package com.ridgebotics.ridgescout.ui.data;

import static com.ridgebotics.ridgescout.utility.DataManager.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ridgebotics.ridgescout.databinding.FragmentDataReportBinding;
import com.ridgebotics.ridgescout.types.frcMatch;
import com.ridgebotics.ridgescout.utility.DataManager;
import com.ridgebotics.ridgescout.utility.ollama.OllamaClient;
import com.ridgebotics.ridgescout.utility.ollama.PromptCreator;
import com.ridgebotics.ridgescout.utility.settingsManager;

public class ReportFragment extends Fragment {
    FragmentDataReportBinding binding;

    private static frcMatch match;
    public static void setMatch(frcMatch m){
        match = m;
    }

    private final int ourTeamNum = settingsManager.getTeamNum();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDataReportBinding.inflate(inflater, container, false);

        binding.teamNumber.setText(String.valueOf(ourTeamNum));

        binding.AyEyeBox.setVisibility(View.VISIBLE);
        binding.AyEyeButton.setVisibility(View.GONE);

        DataManager.reload_event();
        DataManager.reload_pit_fields();
        DataManager.reload_match_fields();

        binding.AyEyeBox.setText("TBD!\n This is meant to be a tool that lets scouters more easily write reports to the drive team before matches. There are some plans for LLM integration into this menu ");

//        binding.AyEyeButton.setText("Create Prompt");
//        binding.AyEyeButton.setOnClickListener(a ->{
//            getPrompt();
//            binding.AyEyeButton.setText("Generate Overview");
//            binding.AyEyeButton.setOnClickListener(b ->{
//                AIDataOverview();
//                binding.AyEyeButton.setVisibility(View.GONE);
//            });
//        });

        return binding.getRoot();
    }

    private void getPrompt(){
        binding.AyEyeBox.setVisibility(View.VISIBLE);
        String prompt = PromptCreator.genMatchPrompt(0);
        binding.AyEyeBox.setText(prompt);
    }

    private void AIDataOverview(){
        String prompt = binding.AyEyeBox.getText().toString();
        binding.AyEyeBox.setText("");
        OllamaClient.run(prompt, new OllamaClient.ollamaListener() {
            @Override
            public void onResponse(String response) {
//                System.out.println(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.AyEyeBox.setText(binding.AyEyeBox.getText()+response);
                    }
                });
            }

            @Override
            public void onComplete() {
                System.out.println(binding.AyEyeBox.getText());
            }
        });
    }
}
