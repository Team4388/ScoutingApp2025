package com.astatin3.scoutingapp2025.ui.data;

import android.os.Bundle;
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

import com.astatin3.scoutingapp2025.databinding.FragmentDataFieldsBinding;
import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.types.input.inputType;

public class FieldsFragment extends Fragment {
    FragmentDataFieldsBinding binding;

    private static String filename;
    public static void set_filename(String tmpfilename){
        filename = tmpfilename;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDataFieldsBinding.inflate(inflater, container, false);

        load_field_menu();

        return binding.getRoot();
    }

    private static final int background_color = 0x5000ff00;
    private static final int unfocused_background_color = 0x2000ff00;

    inputType[][] values;

    private void load_field_menu() {

        values = fields.load(filename);

        binding.fieldsArea.bringToFront();
        binding.fieldsArea.setStretchAllColumns(true);
        binding.fieldsArea.removeAllViews();
        binding.fieldsArea.setReorderingEnabled(false);

        if(values == null) return;

        for(int i = 0; i < values.length; i++){

            TableRow tr = new TableRow(getContext());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            rowParams.setMargins(20,20,20,20);
            tr.setLayoutParams(rowParams);
            tr.setPadding(20,20,20,20);

            TextView tv = new TextView(getContext());
            tv.setText("v" + i);
            tv.setTextSize(20);
            tr.addView(tv);

            tv = new TextView(getContext());
            tv.setText(values[i].length + " Fields");
            tv.setTextSize(16);
            tr.addView(tv);

            binding.fieldsArea.addView(tr);

            if(i == values.length-1) {
                tr.setBackgroundColor(background_color);
                int fi = i;
                tr.setOnClickListener(v -> {
                    display_fields(values[fi]);
                });
            }else{
                tr.setBackgroundColor(unfocused_background_color);
            }
        }
    }

    private void display_fields(inputType[] version_values) {
        binding.fieldsArea.removeAllViews();
        binding.fieldsArea.setReorderingEnabled(true);

        TextView e = null;
        binding.addButton.setOnClickListener(view -> {
            e.setText("123");
        });

        for(int i = 0; i < version_values.length; i++){
            TableRow tr = new TableRow(getContext());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            rowParams.setMargins(20,20,20,20);
            tr.setLayoutParams(rowParams);
            tr.setPadding(20,20,20,20);
            tr.setBackgroundColor(unfocused_background_color);

            TextView tv = new TextView(getContext());
            tv.setText(version_values[i].get_type_name());
            tv.setTextSize(12);
            tr.addView(tv);

            tv = new TextView(getContext());
            tv.setText(version_values[i].name);
            tv.setTextSize(20);
            tr.addView(tv);

            binding.fieldsArea.addView(tr);

            tr.setOnClickListener(v -> {
                trOnClick(version_values, tr);
            });
        }
    }

    private void trOnClick(inputType[] version_values, TableRow tr){
        int index = -1;
        for(int i = 0; i < binding.fieldsArea.getChildCount(); i++){
            View v = binding.fieldsArea.getChildAt(i);

            if(v.equals(tr)) {
                tr.setBackgroundColor(background_color);
                index = i;
            } else
                binding.fieldsArea.getChildAt(i).setBackgroundColor(unfocused_background_color);
        }

        onFieldSelect(version_values[binding.fieldsArea.getReorderedIndexes().get(index)]);
    }

    private void onFieldSelect(inputType field){
        System.out.println(field.name);

    }
}
