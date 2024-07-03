package com.astatin3.scoutingapp2025.scoutingData;

import android.app.slice.Slice;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.slider.Slider;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.skydoves.powerspinner.SpinnerGravity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class
ScoutingVersion {
    public static final int slider_type_id = 255;
    public static final int dropdownType = 254;
    public static final int notesType = 253;



    public enum inputTypes {
//        USERNAME,
        SLIDER,
        DROPDOWN,
        NOTES_INPUT
    }

    public enum valueTypes {
        NUM,
        STRING
    }




















    public abstract class inputType {
        public String name;
        public Object default_value;
        public abstract inputTypes getInputType();
        public abstract valueTypes getValueType();
        public abstract Object get_fallback_value();
        public abstract int get_byte_id();
        public inputType(){}
        public inputType(String name){
            this.name = name;
        }
        public abstract byte[] encode() throws ByteBuilder.buildingException;
        public abstract void decode(byte[] bytes) throws BuiltByteParser.byteParsingExeption;

        public abstract View createView(Context context, Function<dataType, Integer> onUpdate);
        public void setViewValue(dataType type){setViewValue(type.get());}
        public abstract void setViewValue(Object value);
        public abstract dataType getViewValue();
        public abstract void add_individual_view(LinearLayout parent, dataType data);
        public abstract void add_compiled_view(LinearLayout parent, dataType[] data);

    }








    public class sliderType extends inputType {
//        public int defaultValue;
        public int min;
        public int max;
        public int get_byte_id() {return slider_type_id;}
        public inputTypes getInputType(){return inputTypes.SLIDER;}
        public valueTypes getValueType(){return valueTypes.NUM;}
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













    public class dropdownType extends inputType {
        public String[] text_options;
        public int get_byte_id() {return dropdownType;}
        public inputTypes getInputType(){return inputTypes.DROPDOWN;}
        public valueTypes getValueType(){return valueTypes.NUM;}
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
            dropdown.selectItemByIndex((int) value);
        }
        public dataType getViewValue(){
            if(dropdown == null) return null;
            return new intType(name, dropdown.getSelectedIndex());
        }
        public void add_individual_view(LinearLayout parent, dataType data){
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
        public void add_compiled_view(LinearLayout parent, dataType[] data){
            TextView tv = new TextView(parent.getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText("<add pie chart thing here>");
            tv.setTextSize(20);
            parent.addView(tv);
        }
    }











    public class notesType extends inputType {
        public int get_byte_id() {return notesType;}
        public inputTypes getInputType(){return inputTypes.NOTES_INPUT;}
        public valueTypes getValueType(){return valueTypes.STRING;}
        public Object get_fallback_value(){return "<no-notes>";}
        public notesType(){};
        public notesType(String name, String default_text){
            super(name);
            this.default_value = default_text;
        }
        public byte[] encode() throws ByteBuilder.buildingException {
            ByteBuilder bb = new ByteBuilder();
            bb.addString(name);
            bb.addString((String) default_value);
            return bb.build();
        }
        public void decode(byte[] bytes) throws BuiltByteParser.byteParsingExeption {
            BuiltByteParser bbp = new BuiltByteParser(bytes);
            ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();

            name          = (String) objects.get(0).get();
            default_value =          objects.get(1).get();
        }

        public EditText text = null;

        public View createView(Context context, Function<dataType, Integer> onUpdate){
            text = new EditText(context);
            text.setText((String)default_value);
            text.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    onUpdate.apply(getViewValue());                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

            return text;

        };
        public void setViewValue(Object value) {
            if(text == null) return;
            text.setText((String) value);
        }
        public dataType getViewValue(){
            if(text == null) return null;
            return new stringType(name, text.getText().toString());
        }
        public void add_individual_view(LinearLayout parent, dataType data){
            TextView tv = new TextView(parent.getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText((String) data.get());
            tv.setTextSize(18);
            parent.addView(tv);
        }
        public void add_compiled_view(LinearLayout parent, dataType[] data){
            TextView tv = new TextView(parent.getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText("<add word cloud thing here>");
            tv.setTextSize(20);
            parent.addView(tv);
        }
    }























    public abstract class dataType {
        public String name;
        public Object value;
        public abstract valueTypes getValueType();
        public Object get(){
            return value;
        }
        public void set(Object value){
            this.value = value;
        }
        public dataType(String name){
            this.name = name;
        }
    }

    public class intType extends dataType{
        public valueTypes getValueType() {return valueTypes.NUM;}
        public intType(String name, int value){
            super(name);
            this.value = value;
        }
    }

    public class stringType extends dataType{
        public valueTypes getValueType() {return valueTypes.STRING;}
        public stringType(String name, String value){
            super(name);
            this.value = value;
        }
    }



    public enum transferValue {
        DIRECT,
//        RENAME,
        CREATE
//        DELETE
//        UP_TO_DATE
    }

    public abstract class transferType {
        public String name;
        public abstract transferValue getType();
        public transferType(String name){
            this.name = name;
        }
    }

    public class directTransferType extends transferType {
        public transferValue getType() {return transferValue.DIRECT;}
        public directTransferType(String name){
            super(name);
        }
    }

//    public class renameTransferType extends transferType {
//        public String new_name;
//        public transferValue getType() {return transferValue.RENAME;}
//        public renameTransferType(String name, String new_name){
//            super(name);
//            this.new_name = new_name;
//        }
//    }


    public class createTransferType extends transferType {
        public transferValue getType() {return transferValue.CREATE;}
        public createTransferType(String name){
            super(name);
        }
    }

//    public class deleteTransferType extends transferType {
//        public transferValue getType() {return transferValue.DELETE;}
//        public deleteTransferType(String name){
//            super(name);
//        }
//    }

//    public class uptodateTransferType extends transferType {
//        public String name;
//        public transferValue getType() {return transferValue.UP_TO_DATE;}
//        public uptodateTransferType(String name){
//            super(name);
//        }
//    }



    public class ScoutingArray {
        public int version;
        public dataType[] array;
        public ScoutingVersion.inputType[][] values;
        public int latest_version_num;
        public transferType[][] transfer_values;

        public ScoutingArray(int version, dataType[] array, ScoutingVersion.inputType[][] values, transferType[][] transfer_values){
            this.version = version;
            this.array = array;
            this.values = values;
            this.latest_version_num = values.length-1;
            this.transfer_values = transfer_values;
        }

        public ScoutingArray(int version, dataType[] array, ScoutingVersion.inputType[][] values){
            this(version, array, values, get_transfer_values(values));
        }

        public void update(){
            while(version<latest_version_num){
                dataType[] new_values = new dataType[transfer_values[version].length];
                for(int i = 0; i < transfer_values[version].length; i++){
                    transferType tv = transfer_values[version][i];
                    switch (tv.getType()){
                        case DIRECT:
                            new_values[i] = direct_transfer((directTransferType) tv);
                            continue;
//                        case RENAME:
//                            new_values[i] = rename_transfer((renameTransferType) tv);
//                            continue;
                        case CREATE:
                            new_values[i] = create_transfer((createTransferType) tv);
                            continue;
                    }
                }
                this.array = new_values;
                version++;
                System.out.println("Updated to " + version);
            }
        }

        private inputType get_input_type_by_name(int version, String name){
            for(inputType it : values[version]){
                if(it.name.equals(name)){
                    return it;
                }
            }
            return null;
        }

        private dataType get_data_type_by_name(String name){
            for(dataType dt : array){
                if(dt.name.equals(name)){
                    return dt;
                }
            }
            return null;
        }

        private dataType direct_transfer(directTransferType tv){
            return get_data_type_by_name(tv.name);
        }

//        private dataType rename_transfer(renameTransferType tv){
//            dataType dt = get_data_type_by_name(tv.name);
//            dt.name = tv.new_name;
//            return dt;
//        }

        private dataType create_transfer(createTransferType tv){
            inputType it = get_input_type_by_name(version+1, tv.name);
            switch (it.getValueType()){
                case NUM:
                    return new intType(it.name, (int) it.default_value);
                case STRING:
                    return new stringType(it.name, (String) it.default_value);
            }
            return null;
        }

    }

    private inputType get_input_type_by_name(inputType[] values, String name){
        for(inputType it : values){
            if(it.name.equals(name)){
                return it;
            }
        }
        return null;
    }

    public transferType[][] get_transfer_values(inputType[][] values) {
        transferType[][] output = new transferType[values.length][];
        for(int a = 1; a < values.length; a++){
            transferType[] v = new transferType[values[a].length];
            for(int b = 0; b < values[a].length; b++){
                String name = values[a][b].name;
                if(get_input_type_by_name(values[a-1], name) != null){
                    v[b] = new directTransferType(name);
                }else{
                    v[b] = new createTransferType(name);
                }
            }
            output[a-1] = v;
        }
        return output;
    }
}
