package com.ridgebotics.ridgescout.ui.data;

import static android.text.InputType.TYPE_CLASS_NUMBER;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ridgebotics.ridgescout.types.input.dropdownType;
import com.ridgebotics.ridgescout.types.input.inputType;
import com.ridgebotics.ridgescout.types.input.sliderType;
import com.ridgebotics.ridgescout.types.input.tallyType;
import com.ridgebotics.ridgescout.types.input.textType;
import com.ridgebotics.ridgescout.utility.AlertManager;

public class FieldEditorHelper {
    private enum parameterTypeEnum {
        paramNumber,
        paramString,
        paramStringArray
    }

    public static class parameterType {
        public String name;
        public parameterTypeEnum id;
    }

    public static class paramNumber extends parameterType{
        public int val;
        public paramNumber(String name, int val){
            this.name = name + " (Number)";
            this.val = val;
            this.id = parameterTypeEnum.paramNumber;
        }
    }

    public static class paramString extends parameterType {
        public String val;
        public paramString(String name, String val){
            this.name = name + " (String)";
            this.val = val;
            this.id = parameterTypeEnum.paramString;
        }
    }

    public static class paramStringArray extends parameterType{
        public String[] val;
        public paramStringArray(String name, String[] val){
            this.name = name + " (String array)";
            this.val = val;
            this.id = parameterTypeEnum.paramStringArray;
        }
    }

    public static final parameterType[] defaultSliderParams = new parameterType[]{
            new paramNumber("Min", 0),
            new paramNumber("Max", 10),
            new paramNumber("Default Value", 5)
    };
    public static final parameterType[] defaultDropdownParams = new parameterType[]{
            new paramStringArray("Default Value", new String[]{"Zero","One","Two","Three"}),
            new paramNumber("Default Option", 0),
    };
    public static final parameterType[] defaultTextParams = new parameterType[]{
            new paramString("Default Value", "")
    };
    public static final parameterType[] defaultTallyParams = new parameterType[]{
            new paramNumber("Default Value", 0)
    };


    private static parameterType[] getSliderParams(sliderType s){
        return new parameterType[]{
            new paramNumber("Min", s.min),
            new paramNumber("Max", s.max),
            new paramNumber("Default Value", (int) s.default_value)
        };
    }

    private static parameterType[] getDropdownParams(dropdownType s){
        return new parameterType[]{
                new paramStringArray("Default Value",s.text_options),
                new paramNumber("Default Option", (int) s.default_value),
        };
    }

    private static parameterType[] getTextParams(textType s){
        return new parameterType[]{
                new paramString("Default Value", (String) s.default_value)
        };
    }

    private static parameterType[] getTallyParams(tallyType s){
        return new parameterType[]{
                new paramNumber("Default Value", (int) s.default_value)
        };
    }



    public static void setSliderParams(sliderType s, parameterType[] types){
        s.min = ((paramNumber) types[0]).val;
        s.max = ((paramNumber) types[1]).val;
        s.default_value = ((paramNumber) types[2]).val;
    }

    public static void setDropdownParams(dropdownType s, parameterType[] types){
        s.text_options = ((paramStringArray) types[0]).val;
        s.default_value = ((paramNumber) types[1]).val;
    }

    public static void setTextParams(textType s, parameterType[] types){
        s.default_value = ((paramString) types[0]).val;
    }

    public static void setTallyParams(tallyType s, parameterType[] types){
        s.default_value = ((paramNumber) types[0]).val;
    }

    private static void setInputParameter(inputType t, parameterType[] types){
        switch (t.getInputType()){
            case TALLY:
                setTallyParams((tallyType) t, types);
                break;
            case SLIDER:
                setSliderParams((sliderType) t, types);
                break;
            case DROPDOWN:
                setDropdownParams((dropdownType) t, types);
                break;
            case NOTES_INPUT:
                setTextParams((textType) t, types);
                break;
        }
    }



    private static parameterType[] getParamsFromInputType(inputType t){
        switch (t.getInputType()){
            case TALLY:
                return getTallyParams((tallyType) t);
            case SLIDER:
                return getSliderParams((sliderType) t);
            case DROPDOWN:
                return getDropdownParams((dropdownType) t);
            case NOTES_INPUT:
                return getTextParams((textType) t);
        }
        return new parameterType[]{};
    }



    private static View createNumberEdit(Context c, int value){
        EditText text = new EditText(c);
        text.setInputType(TYPE_CLASS_NUMBER);
        text.setText(String.valueOf(value));
        return text;
    }

    private static View createStringEdit(Context c, String value){
        EditText text = new EditText(c);
        text.setText(value);
        return text;
    }

    private static View createStringArrayEdit(Context c, String[] value){
        EditText text = new EditText(c);
        text.setText(String.join("\n", value));
        return text;
    }

    private static View createEdit(Context c, parameterType t){
        switch (t.id){
            case paramNumber:
                return createNumberEdit(c, ((paramNumber) t).val);
            case paramString:
                return createStringEdit(c, ((paramString) t).val);
            case paramStringArray:
                return createStringArrayEdit(c, ((paramStringArray) t).val);
        }
        return null;
    }


    private static boolean readEdit(View v, parameterType t){
        try{
            String val;
            switch (t.id) {
                case paramNumber:
                    val = ((EditText) v).getText().toString();
                    if(val.isEmpty() || val.isBlank()) return false;
                    ((paramNumber) t).val = Integer.parseInt(val);
                    break;
                case paramString:
                    val = ((EditText) v).getText().toString();
                    //if(val.isEmpty() || val.isBlank()) return false;
                    ((paramString) t).val = val;
                    break;
                case paramStringArray:
                    val = ((EditText) v).getText().toString();
                    if(val.isEmpty() || val.isBlank()) return false;
                    ((paramStringArray) t).val = val.split("\n");
                    break;
            }
        } catch (Exception e) {
            AlertManager.error(e);
            return false;
        }

        return true;
    }


    private parameterType[] types;
    private View[] views;
    private inputType t;
    public FieldEditorHelper(Context c, inputType t, TableLayout parentView, parameterType[] tmptypes){
        this.types = tmptypes;
        this.t = t;
        views = new View[types.length];
        for(int i = 0; i < types.length; i++){
            TextView tv = new TextView(c);
            tv.setText(types[i].name);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextSize(20);
            parentView.addView(tv);

            views[i] = createEdit(c, types[i]);
            parentView.addView(views[i]);
        }
    }
    public FieldEditorHelper(Context c, inputType t, TableLayout parentView){
        this(c,t,parentView,getParamsFromInputType(t));
    }

    public boolean save(){
        for(int i = 0; i < types.length; i++){
            if(!readEdit(views[i], types[i]))
                return false;
        }
        setInputParameter(t, types);
        return true;
    }

}