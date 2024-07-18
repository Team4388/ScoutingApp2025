package com.astatin3.scoutingapp2025.ui.data;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.databinding.FragmentDataBinding;
import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.types.input.inputType;

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

    inputType[][] values;

    public void init(FragmentDataBinding binding) {
        this.binding = binding;

        binding.fieldsSelectButtons.setVisibility(VISIBLE);
        binding.addButton.setVisibility(GONE);
        binding.fieldsArea.removeAllViews();
        binding.fieldsSelectButtons.bringToFront();

        binding.fieldsArea.setStretchAllColumns(true);

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

        for(int i = 0; i < values.length; i++){

            TableRow tr = new TableRow(getContext());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            rowParams.setMargins(20,20,20,20);
            tr.setLayoutParams(rowParams);
            tr.setPadding(20,20,20,20);
            binding.fieldsArea.addView(tr);

            tr.setBackgroundColor(background_color);

            TextView tv = new TextView(getContext());
            tv.setText("v" + i);
            tv.setTextSize(20);
            tr.addView(tv);

            tv = new TextView(getContext());
            tv.setText(values[i].length + " Fields");
            tv.setTextSize(16);
            tr.addView(tv);

//            frcTeam finalTeam = team;
            int fi = i;
            tr.setOnClickListener(v -> {
//                loadTeam(finalTeam, latestSettings.settings.get_compiled_mode());
                load_fields(values[fi]);
            });
        }
    }

    private void load_fields(inputType[] version_values) {

    }
}
