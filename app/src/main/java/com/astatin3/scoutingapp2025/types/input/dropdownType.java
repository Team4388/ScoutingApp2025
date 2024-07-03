package com.astatin3.scoutingapp2025.types.input;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.astatin3.scoutingapp2025.types.data.dataType;
import com.astatin3.scoutingapp2025.types.data.intType;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;
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

