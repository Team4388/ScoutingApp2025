package com.astatin3.scoutingapp2025.ui.transfer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.RequestTask;
import com.astatin3.scoutingapp2025.databinding.FragmentTbaBinding;
import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.ui.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Function;

public class TBAView extends ScrollView {

    //    private final String
    private final String TBAAddress = "https://www.thebluealliance.com/api/v3/";
    private final String TBAHeader = "X-TBA-Auth-Key: tjEKSZojAU2pgbs2mBt06SKyOakVhLutj3NwuxLTxPKQPLih11aCIwRIVFXKzY4e";

    private android.widget.TableLayout Table;

    public TBAView(Context context) {
        super(context);
    }

    public TBAView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    private void alert(String title, String content) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(content);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }


    public void start(FragmentTransferBinding binding, String yearStr) {

        Table = binding.matchTable;

        final RequestTask rq = new RequestTask();
        rq.onResult(new Function<String, String>() {
            @Override
            public String apply(String s) {
                eventTable(s);
                return null;
            }
        });
        rq.execute(TBAAddress + "events/"+yearStr, TBAHeader);

    }

    private void addTableText(TableRow tr, String textStr){
        TextView text = new TextView(getContext());
        text.setTextSize(18);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); // Text align center
        text.setText(textStr);
        tr.addView(text);
    }

    public void eventTable(String dataString){
        Date currentTime = Calendar.getInstance().getTime();

        try {
            JSONArray data = new JSONArray(dataString);

            Table.setStretchAllColumns(true);
            Table.bringToFront();

            boolean toggle = false;

//            TableRow tr = new TableRow(getContext());
//            addTableText(tr, "Key");
//            addTableText(tr, "Title");
//            addTableText(tr, "Type");
//
//            Table.addView(tr);

            for(int i=0;i<data.length();i++){
                TableRow tr = new TableRow(getContext());
                TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );
                rowParams.setMargins(20,20,20,20);
                tr.setLayoutParams(rowParams);
                tr.setPadding(20,20,20,20);
                tr.setBackgroundColor(0x30000000);


                JSONObject j = data.getJSONObject(i);

                String matchKey = j.getString("key");
                String name = j.getString("short_name");

                // Sometimes, a short name is not present on TBA Events
                if(name.isEmpty()){
                    name = j.getString("name");
                }

                TextView tv = new TextView(getContext());
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextSize(12);
                tv.setText(j.getString("key"));
                tr.addView(tv);

                tv = new TextView(getContext());
                tv.setTextSize(18);
                tv.setText(name);
                tr.addView(tv);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date startDate = format.parse(j.getString("start_date"));
                    Date endDate = format.parse(j.getString("end_date"));
                    if(currentTime.after(endDate)){
                        tr.setBackgroundColor(0x30FF0000);
                    }else if(currentTime.before(startDate)){
                        tr.setBackgroundColor(0x3000FF00);
                    }else if(currentTime.after(startDate) && currentTime.before(endDate)){
                        tr.setBackgroundColor(0x30FFFF00);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                tr.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final RequestTask rq = new RequestTask();
                        rq.onResult(new Function<String, String>() {
                            @Override
                            public String apply(String s) {
                                matchTable(s);
                                return null;
                            }
                        });
                        rq.execute((TBAAddress + "event/" + matchKey + "/matches"), TBAHeader);
                    }
                });

//                tr.addView(cl);
                Table.addView(tr, rowParams);

                toggle = !toggle;
            }
        }catch (JSONException j){
            alert("Error", "Invalid JSON");
        }
    }

    class matchComparator implements Comparator<JSONObject>
    {

        public int compare(JSONObject a, JSONObject b)
        {
            try {
                return a.getInt("match_number") - b.getInt("match_number");
            }catch (JSONException j){
                return 0;
            }
        }
    }


    public void matchTable(String dataString){
        try {
            JSONArray data = new JSONArray(dataString);

            Table.removeAllViews();
            Table.setStretchAllColumns(true);
            Table.bringToFront();



            if(data.length() == 0){
                TableRow tr = new TableRow(getContext());
                addTableText(tr, "This event has no matches released yet...");
                Table.addView(tr);
                tr = new TableRow(getContext());
                addTableText(tr, "Try manually adding practice matches.");
                Table.addView(tr);
                return;
            }

            TableRow tr = new TableRow(getContext());
            Button btn = new Button(getContext());
            btn.setText("Save");
            btn.setTextSize(18);
            btn.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
//            addTableText(tr, "test");
            tr.addView(btn);
            Table.addView(tr);

            tr = new TableRow(getContext());
            addTableText(tr, "#");
            addTableText(tr, "Red-1");
            addTableText(tr, "Red-2");
            addTableText(tr, "Red-3");
            addTableText(tr, "Blue-1");
            addTableText(tr, "Blue-2");
            addTableText(tr, "Blue-3");
            Table.addView(tr);


            data = JSONUtil.sort(data, new Comparator(){
                public int compare(Object a, Object b){
                    JSONObject    ja = (JSONObject)a;
                    JSONObject    jb = (JSONObject)b;
                    try {
                        return ja.getInt("match_number") - jb.getInt("match_number");
                    }catch (JSONException j){
                        return 0;
                    }
                }
            });


            boolean toggle = false;
            int matchCount = 1;

            for(int a=0;a<data.length();a++){
                final JSONObject match = data.getJSONObject(a);

                if(!match.getString("comp_level").equals("qm")){
                    continue;
                }

                final JSONObject alliances = match.getJSONObject("alliances");
                final JSONArray redAlliance = alliances.getJSONObject("red").getJSONArray("team_keys");
                final JSONArray blueAlliance = alliances.getJSONObject("blue").getJSONArray("team_keys");

                tr = new TableRow(getContext());

                if (toggle) {
                    tr.setBackgroundColor(0x30000000);
                }

                addTableText(tr, String.valueOf(matchCount));
//                addTableText(tr, match.getString("key"));

                for(int b=0;b<6;b++){
                    TextView text = new TextView(getContext());
                    text.setTextSize(18);
                    text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); // Text align center
                    tr.addView(text);

                    if(b < 3){
                        text.setText(redAlliance.getString(b).substring(3));
                        text.setBackgroundColor(0x50ff0000);
                    }else{
                        text.setText(blueAlliance.getString(b-3).substring(3));
                        text.setBackgroundColor(0x500000ff);
                    }
                }

                Table.addView(tr);

                matchCount += 1;
                toggle = !toggle;
            }



        }catch (JSONException j){
            alert("Error", "Invalid JSON");
        }
    }
}
