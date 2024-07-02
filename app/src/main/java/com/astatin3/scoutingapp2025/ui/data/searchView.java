package com.astatin3.scoutingapp2025.ui.data;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.databinding.FragmentDataBinding;
import com.astatin3.scoutingapp2025.types.frcEvent;

public class searchView extends ConstraintLayout {
    public searchView(@NonNull Context context) {
        super(context);
    }
    public searchView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    FragmentDataBinding binding;

    public void init(FragmentDataBinding binding, frcEvent event){
        this.binding = binding;

    }
}
