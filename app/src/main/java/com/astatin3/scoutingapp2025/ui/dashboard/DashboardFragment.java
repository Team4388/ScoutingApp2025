package com.astatin3.scoutingapp2025.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private TableLayout Table;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Table = binding.matchTable;

        warnPopup();

        return root;
    }

    public void warnPopup(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("This is an alert with no consequence");
        alert.setTitle("App Title");
        alert.setPositiveButton("OK", null);
        alert.setCancelable(false);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showTableLayout();
            }
        });

        alert.create().show();
    }

    public void showTableLayout(){
//        Date date = new Date();
        int rows = 200;
        int columns  = 10;
        Table.setStretchAllColumns(true);
        Table.bringToFront();

        for(int i = 0; i < rows; i++){

            TableRow tr =  new TableRow(getContext());

            Button button = new Button(getContext());
            button.setText("test");
            tr.addView(button);

            TextView text = new TextView(getContext());
            text.setTextSize(18);
            text.setText("eee");
            tr.addView(text);

            Table.addView(tr);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}