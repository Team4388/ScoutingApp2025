package com.astatin3.scoutingapp2025.types.input;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.astatin3.scoutingapp2025.types.data.dataType;
import com.astatin3.scoutingapp2025.types.data.intType;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class sliderType extends inputType {
    //        public int defaultValue;
    public int min;
    public int max;
    public int get_byte_id() {return slider_type_id;}
    public inputTypes getInputType(){return inputTypes.SLIDER;}
    public dataType.valueTypes getValueType(){return dataType.valueTypes.NUM;}
    public Object get_fallback_value(){return 0;}
    public sliderType(){};
    public sliderType(String name, int defaultValue, int min, int max){
        super(name);
        this.default_value = defaultValue;
        this.min = min;
        this.max = max;
    }
    public byte[] encode() throws ByteBuilder.buildingException {
        ByteBuilder bb = new ByteBuilder();
        bb.addString(name);
        bb.addInt((int)default_value);
        bb.addInt(min);
        bb.addInt(max);
        return bb.build();
    }
    public void decode(byte[] bytes) throws BuiltByteParser.byteParsingExeption {
        BuiltByteParser bbp = new BuiltByteParser(bytes);
        ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();

        name          = (String) objects.get(0).get();
        default_value =          objects.get(1).get();
        min           = (int)    objects.get(2).get();
        max           = (int)    objects.get(3).get();
    }

    public Slider slider = null;

    public View createView(Context context, Function<dataType, Integer> onUpdate){
        slider = new Slider(context);
        setViewValue(default_value);
        slider.setStepSize((float) 1 / max);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                onUpdate.apply(getViewValue());
            }
        });
        return slider;

    }
    public void setViewValue(Object value) {
        if(slider == null) return;
        float slider_position = (float) ((int) value-min) / (max-min);
        float step_size = (float) 1/max;
        int round_position = Math.round(slider_position / step_size);
        slider.setValue(round_position*step_size);
    }
    public dataType getViewValue(){
        if(slider == null) return null;
        return new intType(name,  min + (int) (slider.getValue() * (max-min)));
    }
    public void add_individual_view(LinearLayout parent, dataType data){
        Slider slider = new Slider(parent.getContext());

        float slider_position = (float) ((int) data.get()-min) / (max-min);
        float step_size = (float) 1/max;
        int round_position = Math.round(slider_position / step_size);
        slider.setValue(round_position*step_size);

        slider.setStepSize((float) 1 / max);
        slider.setEnabled(false);
        parent.addView(slider);
    }


    private float calculateMean(int[] data) {
        float sum = 0;
        for (int value : data) {
            sum += (float) value;
        }
        return sum / data.length;
    }

    private float calculateStandardDeviation(int[] data, float mean) {
        float sum = 0;
        for (int value : data) {
            sum += Math.pow((float) value - mean, 2);
        }
        return (float) Math.sqrt(sum / (data.length - 1));
    }

    private List<Entry> generateNormalDistribution(float mean, float stdDev, int count, int scale) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float x = i;
            float y = (float) ((1 / (stdDev * Math.sqrt(2 * Math.PI)))
                    * Math.exp(-0.5 * Math.pow((x - mean) / stdDev, 2)));
            entries.add(new Entry(x, y*scale)); // Scale y for visibility
        }
        return entries;
    }



    public void add_compiled_view(LinearLayout parent, dataType[] data){
        LineChart chart = new LineChart(parent.getContext());
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layout.height = 350;
        chart.setLayoutParams(layout);
        chart.setBackgroundColor(0xff252025);

        int[] values = new int[max-min];
        for (int i = 0; i < data.length; i++)
            values[(int) data[i].get()-min-1]++;


        int[] temp = new int[data.length];
        for (int i = 0; i < data.length; i++)
            temp[i] = (int) data[i].get();



        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++)
            entries.add(new Entry(i, values[i]));


        LineDataSet dataSet = new LineDataSet(entries, name);
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);



        // Calculate mean and standard deviation
        float mean = calculateMean(temp);
        float stdDev = calculateStandardDeviation(temp, mean);

        // Generate normal distribution curve
        List<Entry> normalDistEntries = generateNormalDistribution(mean-min, stdDev, max-min, (max-min)/data.length);


        LineDataSet normalDistSet = new LineDataSet(normalDistEntries, "Normal Distribution");
        normalDistSet.setColor(Color.RED);
        normalDistSet.setDrawCircles(false);
        normalDistSet.setDrawValues(false);
        normalDistSet.setLineWidth(2f);




        LineData lineData = new LineData(dataSet, normalDistSet);



        chart.setData(lineData);
        chart.invalidate();

        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        dataSet.setValueTextColor(Color.RED);

        chart.getXAxis().setTextColor(Color.BLUE);
        chart.getAxisLeft().setTextColor(Color.GREEN);
        chart.getAxisRight().setTextColor(Color.GREEN);

        Legend legend = chart.getLegend();
        legend.setTextColor(Color.MAGENTA);

        parent.addView(chart);
    }
}