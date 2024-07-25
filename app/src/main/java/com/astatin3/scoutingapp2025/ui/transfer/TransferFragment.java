package com.astatin3.scoutingapp2025.ui.transfer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
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

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.types.file;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransferFragment extends Fragment {
    private FragmentTransferBinding binding;

    private boolean submenu = false;

    private enum TransferTypes {
        CAMERA,
        BLUETOOTH,
        LOCAL_WIFI,
        SCOUTING_SERVER
    }

    String evcode;

    private static final int background_color = 0x5000ff00;
    private static final int unselected_background_color = 0x2000ff00;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentTransferBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        show_ui();

        evcode = latestSettings.settings.get_evcode();

        binding.downloadButton.setOnClickListener(v -> {
            start_download();
            submenu = true;
        });

        binding.TBAButton.setOnClickListener(v -> {
            binding.noEventError.setVisibility(View.GONE);
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Warning");
            alert.setMessage("This action requires internet.");
            alert.setCancelable(true);

            alert.setPositiveButton("Ok", (dialog, which) -> {
                binding.mainSelectLayout.setVisibility(View.GONE);
                binding.TBAView.setVisibility(View.VISIBLE);
                binding.TBAView.start(binding, "2024");
                submenu = true;
            });

            alert.setNegativeButton("Cancel", null);
            alert.create().show();
        });

        if(evcode.equals("unset")){
            binding.noEventError.setVisibility(View.VISIBLE);
            binding.uploadButton.setVisibility(View.GONE);
            binding.downloadButton.setVisibility(View.VISIBLE);
            return root;
        }

        binding.uploadButton.setOnClickListener(v -> {
            filePicker();
            submenu = true;
        });

        if(!latestSettings.settings.get_wifi_mode())
            binding.TBAButton.setVisibility(View.GONE);

        return root;
    }

    public void show_ui(){
        binding.mainSelectLayout.setVisibility(View.VISIBLE);
        binding.noEventError.setVisibility(View.GONE);
        binding.loadSelectLayout.setVisibility(View.GONE);

        binding.bluetoothSenderView.setVisibility(View.GONE);
        binding.bluetoothReceiverView.setVisibility(View.GONE);

        binding.generatorLayout.setVisibility(View.GONE);
        binding.scannerLayout.setVisibility(View.GONE);

        binding.fileSelectorLayout.setVisibility(View.GONE);
        binding.TBAView.setVisibility(View.GONE);
    }



    private void filePicker(){
        binding.mainSelectLayout.setVisibility(View.GONE);
        binding.fileSelectorLayout.setVisibility(View.VISIBLE);
        binding.fileSelectorTable.removeAllViews();
        binding.fileSelectorSearchbar.bringToFront();

        meta_string_array = new String[]{
                "matches.fields",
                "pits.fields",
                evcode+".eventdata"
        };

        String[] files = fileEditor.getEventFiles(evcode);

        Boolean[] selected_arr = new Boolean[files.length];
        Arrays.fill(selected_arr, Boolean.TRUE);

        for(int i = 0; i < files.length; i++){
            TableRow tr = new TableRow(getContext());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(20,20,20,20);
            tr.setLayoutParams(rowParams);
            tr.setPadding(20,20,20,20);
            binding.fileSelectorTable.addView(tr);

            tr.setBackgroundColor(background_color);

            TextView tv = new TextView(getContext());
            tv.setText(String.valueOf(files[i]));
            tv.setTextSize(20);
            tr.addView(tv);

            final int fi = i;
            tr.setOnClickListener(view -> {
                boolean sel = !selected_arr[fi];
                selected_arr[fi] = sel;

                tr.setBackgroundColor(sel ? background_color : unselected_background_color);
            });
        }

        binding.fileSelectorSearchbar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int a, KeyEvent keyEvent) {

                String search_param = binding.fileSelectorSearchbar.getText().toString();
                List<Integer> match_num_nums = get_matches_from_search_params(search_param);

                Arrays.fill(selected_arr, Boolean.TRUE);

                for(int i = 0; i < files.length; i++){
                    View child = binding.fileSelectorTable.getChildAt(i);
                    child.setBackgroundColor(background_color);
                    child.setVisibility(is_in_search_param(files[i], search_param, match_num_nums) ? View.VISIBLE : View.GONE);
                }

                return false;
            }
        });

        binding.fileSelectorButton.setText("Send");
        binding.fileSelectorButton.setOnClickListener(view -> {
            List<String> filenames = new ArrayList<>();
            for(int i = 0; i < files.length; i++){
                View child = binding.fileSelectorTable.getChildAt(i);
                if(child.getVisibility() == View.VISIBLE && selected_arr[i])
                    filenames.add(files[i]);
            }
            start_upload(get_bytes_of_filenames(filenames));
        });
    }



    private static String[] meta_string_array;

    private boolean is_in_search_param(String filename, String search_param, List<Integer> nums){
        return
        ("meta".contains(search_param) && Arrays.asList(meta_string_array).contains(filename)) ||
        filename.contains(search_param) ||
        match_file_is_match_num(filename, nums);
    }


    private boolean match_file_is_match_num(String filename, List<Integer> nums){
        if(!filename.endsWith(".matchscoutdata")) return false;
        String[] dash_split = filename.split("-");
        if(dash_split.length != 5) return false;
        String s = dash_split[1];
        if(!is_int(s)) return false;
        int n = Integer.parseInt(s);
        return nums.contains(n);
    }

    private List<Integer> get_matches_from_search_params(String search_param){
        List<Integer> nums = new ArrayList<>();
        String[] comma_split = search_param.split(",");

        for(int i = 0; i < comma_split.length; i++){
            if(comma_split[i].contains("-")){

                String[] dash_split = comma_split[i].split("-");
                if(dash_split.length != 2) continue;
                String stra = dash_split[0];
                String strb = dash_split[1];

                if(!(is_int(stra) && is_int(strb))) continue;

                int a = Integer.parseUnsignedInt(stra);
                int b = Integer.parseUnsignedInt(strb);

                for(int x = a; x <= b; x++)
                    nums.add(x);
            } else if(is_int(comma_split[i]))
                nums.add(Integer.parseUnsignedInt(comma_split[i]));
        }

        return nums;
    }

    private boolean is_int(String num){
        try {
            Integer.parseUnsignedInt(num);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }



    private static byte[] get_bytes_of_filenames(List<String> filenames){
        try {
            ByteBuilder b = new ByteBuilder();

            for(int i = 0; i < filenames.size(); i++){
                file f = new file(filenames.get(i));
                b.addRaw(file.typecode, f.encode());
            }

            return b.build();
        } catch (ByteBuilder.buildingException e){
            e.printStackTrace();
            return null;
        }
    }



    private void start_upload(byte[] data){
        binding.mainSelectLayout.setVisibility(View.GONE);
        binding.fileSelectorLayout.setVisibility(View.GONE);
        binding.loadSelectLayout.setVisibility(View.VISIBLE);

        binding.cameraButton.setOnClickListener(view -> {
            start_upload_codes(data);
        });

        binding.bluetoothButton.setOnClickListener(view -> {
            start_upload_bluetooth(data);
        });
    }

    private void start_download(){
        binding.mainSelectLayout.setVisibility(View.GONE);
        binding.fileSelectorLayout.setVisibility(View.GONE);
        binding.loadSelectLayout.setVisibility(View.VISIBLE);

        binding.cameraButton.setOnClickListener(view -> {
            start_download_codes();
        });

        binding.bluetoothButton.setOnClickListener(view -> {
            start_download_bluetooth();
        });
    }



    private void start_upload_codes(byte[] data){
        binding.loadSelectLayout.setVisibility(View.GONE);
        binding.generatorLayout.setVisibility(View.VISIBLE);
        binding.generatorLayout.start(binding, data);
    }

    private void start_download_codes(){
        binding.loadSelectLayout.setVisibility(View.GONE);
        binding.scannerLayout.setVisibility(View.VISIBLE);
        binding.scannerLayout.start(binding, getViewLifecycleOwner());
    }


    private void start_upload_bluetooth(byte[] data){
        binding.loadSelectLayout.setVisibility(View.GONE);
        binding.bluetoothSenderView.setVisibility(View.VISIBLE);
        binding.bluetoothSenderView.init();
        binding.bluetoothSenderView.set_data(data);


    }

    private void start_download_bluetooth(){
        binding.loadSelectLayout.setVisibility(View.GONE);
        binding.bluetoothReceiverView.setVisibility(View.VISIBLE);
        binding.bluetoothReceiverView.init();
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.bluetoothSenderView.onDestroy();
        binding.bluetoothReceiverView.onDestroy();
    }
}