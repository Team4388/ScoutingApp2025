package com.astatin3.scoutingapp2025.ui.TBA;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.RequestTask;
import com.astatin3.scoutingapp2025.databinding.FragmentTbaBinding;
import com.astatin3.scoutingapp2025.ui.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.function.Function;


public class TBAFragment extends Fragment {

//    private final String
    private final String TBAAddress = "https://www.thebluealliance.com/api/v3/";
    private final String TBAHeader = "X-TBA-Auth-Key: tjEKSZojAU2pgbs2mBt06SKyOakVhLutj3NwuxLTxPKQPLih11aCIwRIVFXKzY4e";

    private FragmentTbaBinding binding;
    private android.widget.ScrollView ScrollArea;
    private android.widget.TableLayout Table;

    private void alert(String title, String content) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(content);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTbaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}