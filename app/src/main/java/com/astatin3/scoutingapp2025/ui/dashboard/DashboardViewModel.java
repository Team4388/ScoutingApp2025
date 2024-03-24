package com.astatin3.scoutingapp2025.ui.dashboard;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

//    private class row {
//        public row() {
//
//        }
//    }

    private final MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
//
//        final TextView row = new TextView(getContext());
////        setContentView()
//
//        row.setLayoutParams(new
//                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
//                TableRow.LayoutParams.WRAP_CONTENT));
//
////        TableLayout list = (TableLayout) this.findViewById(R.id.matchTable);
////        View list = inflater.inflate(inflater, container, findViewById(R.id.toastViewGroup));
//
//
//        row.setGravity(Gravity.END);
//        row.setTextSize(TypedValue.COMPLEX_UNIT_PX, 10);
//        row.setPadding(5, 1, 0, 5);
//        row.setTextColor(Color.parseColor("#aaaaaa"));
//        row.setBackgroundColor(Color.parseColor("#f8f8f8"));
//        row.setText("testing!!");
//        .addView(row);
    }

    public LiveData<String> getText() {
        return mText;
    }
}