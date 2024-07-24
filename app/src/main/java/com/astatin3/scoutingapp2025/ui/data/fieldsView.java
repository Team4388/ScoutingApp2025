package com.astatin3.scoutingapp2025.ui.data;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.astatin3.scoutingapp2025.databinding.FragmentDataBinding;
import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.types.input.inputType;

import java.util.ArrayList;
import java.util.List;

public class fieldsView extends ConstraintLayout {
    public fieldsView(@NonNull Context context) {
        super(context);
    }
    public fieldsView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }
    FragmentDataBinding binding;
    String filename;
    private static final int background_color = 0x5000ff00;
    private static final int unfocused_background_color = 0x2000ff00;

    inputType[][] values;

    public void init(FragmentDataBinding binding) {
        this.binding = binding;

        binding.fieldsSelectButtons.setVisibility(VISIBLE);
        binding.addButton.setVisibility(GONE);
        binding.fieldsArea.setReorderingEnabled(false);
        binding.fieldsArea.removeAllViews();
        binding.fieldsArea.setStretchAllColumns(true);
        binding.fieldsSelectButtons.bringToFront();

//        binding.fieldsArea.setStretchAllColumns(true);

        binding.matchScoutingButton.setOnClickListener(v -> {
            binding.fieldsSelectButtons.setVisibility(GONE);
            binding.addButton.setVisibility(VISIBLE);
            filename = fields.matchFieldsFilename;
            load_field_menu();
        });

        binding.pitScoutingButton.setOnClickListener(v -> {
            binding.fieldsSelectButtons.setVisibility(GONE);
            binding.addButton.setVisibility(VISIBLE);
            filename = fields.pitsFieldsFilename;
            load_field_menu();
        });

    }
    private void load_field_menu() {
        values = fields.load(filename);
        binding.fieldsArea.bringToFront();
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
            tr.setBackgroundColor(background_color);

            TextView tv = new TextView(getContext());
            tv.setText("v" + i + "\n eee");
            tv.setTextSize(20);
            tr.addView(tv);

            tv = new TextView(getContext());
            tv.setText(values[i].length + " Fields");
            tv.setTextSize(16);
            tr.addView(tv);

            binding.fieldsArea.addView(tr);

            int fi = i;
            tr.setOnClickListener(v -> {
                display_fields(values[fi]);
            });
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

                trHighlight(tr);
            });
        }
    }

    private void trHighlight(TableRow self){
        for(int i = 0; i < binding.teamsArea.getChildCount(); i++){
            TableRow child = (TableRow) binding.teamsArea.getChildAt(i);
            child.setBackgroundColor(unfocused_background_color);
        }

        self.setBackgroundColor(background_color);

    }
}
