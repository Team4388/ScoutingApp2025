package com.astatin3.scoutingapp2025.ui.data;

import android.content.Context;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.astatin3.scoutingapp2025.types.input.dropdownType;
import com.astatin3.scoutingapp2025.types.input.inputType;
import com.astatin3.scoutingapp2025.types.input.sliderType;
import com.astatin3.scoutingapp2025.types.input.tallyType;
import com.astatin3.scoutingapp2025.types.input.textType;

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
            this.name = name;
            this.val = val;
            this.id = parameterTypeEnum.paramNumber;
        }
    }

    public static class paramString extends parameterType {
        public String val;
        public paramString(String name, String val){
            this.name = name;
            this.val = val;
            this.id = parameterTypeEnum.paramString;
        }
    }

    public static class paramStringArray extends parameterType{
        public String[] val;
        public paramStringArray(String name, String[] val){
            this.name = name;
            this.val = val;
            this.id = parameterTypeEnum.paramStringArray;
        }
    }

    private static final parameterType[] defaultSliderParams = new parameterType[]{
            new paramNumber("Min", 0),
            new paramNumber("Max", 10),
            new paramNumber("Default Value", 5)
    };
    private static final parameterType[] defaultDropdownParams = new parameterType[]{
            new paramStringArray("Default Value", new String[]{"Zero","One","Two","Three"}),
            new paramNumber("Default Option", 0),
    };
    private static final parameterType[] defaultTextParams = new parameterType[]{
            new paramString("Default Value", "")
    };
    private static final parameterType[] defaultTallyParams = new parameterType[]{
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



    private static View createNumberEdit(Context c){
        TextView tv = new TextView(c);
        tv.setText("Number edit");
        return tv;
    }

    private static View createStringEdit(Context c){
        TextView tv = new TextView(c);
        tv.setText("String edit");
        return tv;
    }

    private static View createStringArrayEdit(Context c){
        TextView tv = new TextView(c);
        tv.setText("String Array edit");
        return tv;
    }

    private static View createEdit(Context c, parameterType t){
        switch (t.id){
            case paramNumber:
                return createNumberEdit(c);
            case paramString:
                return createStringEdit(c);
            case paramStringArray:
                return createStringArrayEdit(c);
        }
        return null;
    }

    private parameterType[] types;
    private View[] views;
    public FieldEditorHelper(Context c, inputType t, TableLayout parentView){
        types = getParamsFromInputType(t);
        for(int i = 0; i < types.length; i++){
            TextView tv = new TextView(c);
            tv.setText(types[i].name);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextSize(20);
            parentView.addView(tv);

            parentView.addView(createEdit(c, types[i]));
        }
    }

}
