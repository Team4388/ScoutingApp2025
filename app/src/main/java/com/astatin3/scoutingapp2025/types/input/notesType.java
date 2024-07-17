package com.astatin3.scoutingapp2025.types.input;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astatin3.scoutingapp2025.types.data.dataType;
import com.astatin3.scoutingapp2025.types.data.intType;
import com.astatin3.scoutingapp2025.types.data.stringType;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;

import java.util.ArrayList;
import java.util.function.Function;

public class notesType extends inputType {
    public int get_byte_id() {return notesType;}
    public inputTypes getInputType(){return inputTypes.NOTES_INPUT;}
    public dataType.valueTypes getValueType(){return dataType.valueTypes.STRING;}
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
        if(text.getVisibility() == View.GONE) return new stringType(name, (String) stringType.getNullValue());
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

