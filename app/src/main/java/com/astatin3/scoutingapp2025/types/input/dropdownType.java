package com.astatin3.scoutingapp2025.types.input;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.types.data.dataType;
import com.astatin3.scoutingapp2025.types.data.intType;
import com.astatin3.scoutingapp2025.types.data.stringType;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.skydoves.powerspinner.SpinnerGravity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class dropdownType extends inputType {
    public String[] text_options;
    public int get_byte_id() {return dropdownType;}
    public inputTypes getInputType(){return inputTypes.DROPDOWN;}
    public dataType.valueTypes getValueType(){return dataType.valueTypes.NUM;}
    public Object get_fallback_value(){return 0;}
    public dropdownType(){};
    public dropdownType(String name, String[] text_options, int defaultSelIndex){
        super(name);
        this.text_options = text_options;
        this.default_value = defaultSelIndex;
    }
    public byte[] encode() throws ByteBuilder.buildingException {
        ByteBuilder bb = new ByteBuilder();
        bb.addString(name);
        bb.addInt((int)default_value);
        bb.addStringArray(text_options);
        return bb.build();
    }
    public void decode(byte[] bytes) throws BuiltByteParser.byteParsingExeption {
        BuiltByteParser bbp = new BuiltByteParser(bytes);
        ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();

        name          = (String)   objects.get(0).get();
        default_value =            objects.get(1).get();
        text_options  = (String[]) objects.get(2).get();
    }

    public PowerSpinnerView dropdown = null;

    public View createView(Context context, Function<dataType, Integer> onUpdate){
        dropdown = new PowerSpinnerView(context);

        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
        for(int i = 0; i < text_options.length; i++){
            iconSpinnerItems.add(new IconSpinnerItem(text_options[i]));
        }
        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(dropdown);
        dropdown.setSpinnerAdapter(iconSpinnerAdapter);
        dropdown.setItems(iconSpinnerItems);

        dropdown.selectItemByIndex((int) default_value);

        dropdown.setPadding(10,10,10,10);
        dropdown.setBackgroundColor(0xf0000000);
        dropdown.setTextColor(0xff00ff00);
        dropdown.setTextSize(15);
        dropdown.setArrowGravity(SpinnerGravity.END);
        dropdown.setArrowPadding(8);
        dropdown.setSpinnerItemHeight(46);
        dropdown.setSpinnerPopupElevation(14);


        dropdown.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<IconSpinnerItem>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable IconSpinnerItem oldItem, int newIndex,
                                       IconSpinnerItem newItem) {
                onUpdate.apply(getViewValue());
            }
        });

//            dropdown.setLifecycleOwner(context.life);
//            slider.addOnChangeListener(new Slider.OnChangeListener() {
//                @Override
//                public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
//                    onUpdate.apply(getViewValue());
//                }
//            });
        return dropdown;

    };
    public void setViewValue(Object value) {
        if(dropdown == null) return;
        if(value.equals(intType.nulval)){
            nullify();
            return;
        }

        isBlank = false;

        dropdown.setVisibility(View.VISIBLE);
        dropdown.selectItemByIndex((int) value);
    }
    public void nullify(){
        isBlank = true;
        dropdown.setVisibility(View.GONE);
    }
    public dataType getViewValue(){
        if(dropdown == null) return null;
        if(dropdown.getVisibility() == View.GONE) return new intType(name, intType.nulval);
        return new intType(name, dropdown.getSelectedIndex());
    }
    public void add_individual_view(LinearLayout parent, dataType data){
        if(data.isNull()) return;
        TextView tv = new TextView(parent.getContext());
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setPadding(20,20,20,20);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(text_options[(int) data.get()]);
        tv.setTextSize(18);
        parent.addView(tv);
    }
    private static int[] generateEquidistantColors(int N) {
        int[] colors = new int[N];
        float[] hsv = new float[3]; // Hue, Saturation, Value

        for (int i = 0; i < N; i++) {
            float hue = i * 1.0F / N;
            hsv[0] = hue * 360; // Convert hue to degrees (0 to 360)
            hsv[1] = 1; // Maximum saturation
            hsv[2] = 1; // Maximum brightness (value)

            colors[i] = Color.HSVToColor(hsv);
        }
        return colors;
    }

    public void add_compiled_view(LinearLayout parent, dataType[] data){
        PieChart chart = new PieChart(parent.getContext());
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layout.height = 350;
        chart.setLayoutParams(layout);
        chart.setBackgroundColor(0xff252025);
        parent.addView(chart);

        int[] data_2 = new int[text_options.length];
        for(int i = 0; i < data.length; i++)
            if(!data[i].isNull())
                data_2[(int) data[i].get()]++;

        List<PieEntry> entries = new ArrayList<>();
        for(int i = 0; i < data_2.length; i++) {
            PieEntry entry = new PieEntry((float) data_2[i], text_options[i]);
            entries.add(entry);
        }

        PieDataSet pieDataSet = new PieDataSet(entries, name);
        pieDataSet.setColors(generateEquidistantColors(text_options.length));
        PieData pieData = new PieData(pieDataSet);
        chart.setDrawHoleEnabled(false);
        chart.setData(pieData);
    }
}

