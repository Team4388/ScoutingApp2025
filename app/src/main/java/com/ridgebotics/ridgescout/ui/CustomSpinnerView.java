package com.ridgebotics.ridgescout.ui;

import static android.app.PendingIntent.getActivity;
import static com.ridgebotics.ridgescout.utility.settingsManager.getEditor;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.ridgebotics.ridgescout.R;
import com.ridgebotics.ridgescout.databinding.ViewCustomSpinnerBinding;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerView extends LinearLayout {

    public interface onClickListener {
        void onClick(String item, int index);
    }

    public CustomSpinnerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomSpinnerView(Context context) {
        super(context);
        init(context);
    }

    private ViewCustomSpinnerBinding binding;

    private List<String> options;
    private CustomSpinnerPopup dialog;
    private onClickListener onClickListener;

    private TextView title;
    private TextView item;

    private int index = -1;

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_custom_spinner, this, true);

        title = findViewById(R.id.title);
        item = findViewById(R.id.item);
    }

    public void setOnClickListener(onClickListener listener){
        this.onClickListener = listener;
    }

    public void setOptions(List<String> options){
        this.options = options;
//        dialog = CustomSpinnerPopup.newInstance(options);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("OK", (dialog, which) -> {});
        CustomSpinnerPopup popup = new CustomSpinnerPopup(getContext()).init(options, option -> {
//            dialog.();
            if(!isEnabled()) return;
            item.setText(option);
            if(onClickListener != null) {
                onClickListener.onClick(option, options.indexOf(option));
                index = options.indexOf(option);
            }
        });
        popup.setLayoutDirection(0);
        builder.setView(popup);
        AlertDialog dialog = builder.create();


//        popup.setOnOptionSelectedListener();

        this.setOnClickListener(v -> {
            if(!isEnabled()) return;
            dialog.show();
        });
    }

    public void setTitle(String text){
        title.setText(text);
    }

    public void setOption(String option) {
        item.setText(option);
    }

    public void setOption(int index) {
        setOption(options.get(index));
    }

    public int getIndex(){
        return index;
    }
}
