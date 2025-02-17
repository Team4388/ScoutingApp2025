package com.ridgebotics.ridgescout.ui.settings;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static com.ridgebotics.ridgescout.utility.settingsManager.AllyPosKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.FTPEnabled;
import static com.ridgebotics.ridgescout.utility.settingsManager.FTPSendMetaFiles;
import static com.ridgebotics.ridgescout.utility.settingsManager.FTPServer;
import static com.ridgebotics.ridgescout.utility.settingsManager.SelEVCodeKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.UnameKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.WifiModeKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.defaults;
import static com.ridgebotics.ridgescout.utility.settingsManager.getEditor;
import static com.ridgebotics.ridgescout.utility.settingsManager.prefs;

import android.app.AlertDialog;
import android.content.Context;
import android.health.connect.datatypes.units.Power;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ridgebotics.ridgescout.databinding.FragmentSettingsBinding;
import com.ridgebotics.ridgescout.types.data.intType;
import com.ridgebotics.ridgescout.ui.CustomSpinner;
import com.ridgebotics.ridgescout.utility.fileEditor;
import com.ridgebotics.ridgescout.utility.settingsManager;

import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.skydoves.powerspinner.SpinnerGravity;

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;


public class settingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private android.widget.ScrollView ScrollArea;
    private android.widget.TableLayout Table;

//    private void setDropdownItems(Spinner dropdown, String[] items){
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, items);
//        dropdown.setAdapter(adapter);
//    }

    private View[] concatArrays(View[] a, View[] b){
        return Stream.of(a, b).flatMap(Stream::of).toArray(View[]::new);
    }


    private View[] addViews(View[] a){
        for(int i = 0; i < a.length; i++){
            binding.SettingsTable.addView(a[i]);
        }
        return a;
    }

    private int safeToInt(String num){
        if(num.isEmpty())
            return 0;
        try {
            return Integer.parseInt(num);
        }catch (NumberFormatException e){
            return 0;
        }
    }

    private View[] createHeading(String name){
        TextView tv = new TextView(getContext());
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = 100;
        tv.setLayoutParams(params);
        tv.setTextSize(20);
        tv.setText(name);

        View divider = new MaterialDivider(getContext());

        return new View[]{tv, divider};
    }

    private View[] addStringEdit(String name, String key){
        View[] heading = createHeading(name);
        EditText et = new EditText(getContext());
        et.setText(prefs.getString(key, (String) defaults.get(key)));

        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getEditor().putString(key, s.toString()).apply();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        return concatArrays(heading, new View[]{et});
    }

    private View[] addNumberEdit(String name, String key){
        View[] heading = createHeading(name);
        EditText et = new EditText(getContext());
        et.setText(String.valueOf(prefs.getInt(key, (Integer) defaults.get(key))));
        et.setInputType(TYPE_CLASS_NUMBER);

        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getEditor().putInt(key, safeToInt(s.toString())).apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        return concatArrays(heading, new View[]{et});
    }

    private TextInputLayout addDropdownEdit(String name, String[] options, String key){

        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                getResources().getDisplayMetrics()
        );

        // Create TextInputLayout
        TextInputLayout textInputLayout = new TextInputLayout(getContext(), null,
                com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox_ExposedDropdownMenu);
        LinearLayout.LayoutParams inputLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textInputLayout.setLayoutParams(inputLayoutParams);
        textInputLayout.setHint("Select an item");

        // Create MaterialAutoCompleteTextView
        MaterialAutoCompleteTextView dropdownText = new MaterialAutoCompleteTextView(getContext());
        TextInputLayout.LayoutParams dropdownParams = new TextInputLayout.LayoutParams(
                TextInputLayout.LayoutParams.MATCH_PARENT,
                TextInputLayout.LayoutParams.WRAP_CONTENT
        );
        dropdownText.setLayoutParams(dropdownParams);
        dropdownText.setInputType(InputType.TYPE_NULL);

        textInputLayout.addView(dropdownText);


        // Create item layout programmatically
        TextView itemView = new TextView(getContext());
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        itemView.setLayoutParams(itemParams);
        itemView.setPadding(10, padding, padding, padding);
        itemView.setMaxLines(1);
        itemView.setEllipsize(TextUtils.TruncateAt.END);
        itemView.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle1);

        // Create and set adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                options
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                // Apply the same styling as our programmatic item layout
                textView.setPadding(padding, padding, padding, padding);
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle1);
                return textView;
            }
        };


//            <com.google.android.material.textfield.TextInputLayout
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:hint="Select an item"
//        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox.ExposedDropdownMenu">
//
//        <com.google.android.material.textfield.MaterialAutoCompleteTextView
//        android:id="@+id/dropdown_text"
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:inputType="none"/>
//
//    </com.google.android.material.textfield.TextInputLayout>

//        PowerSpinnerView dropdown = new PowerSpinnerView(getContext());
//
//        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
//        for(int i = 0; i < options.length; i++){
//            iconSpinnerItems.add(new IconSpinnerItem(options[i]));
//        }
//        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(dropdown);
//
//        dropdown.setGravity(Gravity.CENTER);
//
//        dropdown.setSpinnerAdapter(iconSpinnerAdapter);
//        dropdown.setItems(iconSpinnerItems);
//        dropdown.setHint("Unselected");
//
//        dropdown.setPadding(10,20,10,20);
//        dropdown.setBackgroundColor(0xf0000000);
//        dropdown.setTextColor(0xff00ff00);
//        dropdown.setTextSize(14.5f);
//        dropdown.setArrowGravity(SpinnerGravity.END);
//        dropdown.setArrowPadding(8);
//        dropdown.setSpinnerPopupElevation(14);

        return textInputLayout;
    }

    private View[] addDropdownByString(String name, String[] options, String key){
        View[] heading = createHeading(name);
        TextInputLayout dropdown = addDropdownEdit(name, options, key);
        int index = Arrays.asList(options).indexOf(prefs.getString(key, (String) defaults.get(key)));
        System.out.println(index);


//        dropdown.setOnSpinnerItemSelectedListener(
//                (OnSpinnerItemSelectedListener<IconSpinnerItem>)
//                        (oldIndex, oldItem, newIndex, newItem) ->  getEditor().putString(key, newItem.getText().toString()).apply()
//        );

        return concatArrays(heading, new View[]{dropdown});
    }

    private View[] addDropdownByIndex(String name, String[] options, String key){
        View[] heading = createHeading(name);
        TextInputLayout dropdown = addDropdownEdit(name, options, key);

        int index = prefs.getInt(key, (Integer) defaults.get(key));

//        if(dropdown.length() != 0 && index != -1){
//            dropdown.selectItemByIndex(index);
//        }
//
//        dropdown.setOnSpinnerItemSelectedListener(
//                (OnSpinnerItemSelectedListener<IconSpinnerItem>)
//                        (oldIndex, oldItem, newIndex, newItem) -> getEditor().putInt(key, newIndex).apply()
//        );

        return concatArrays(heading, new View[]{dropdown});
    }

    private View[] addCheckbox(String name, String key, View[] dependency){
        CheckBox cb = new CheckBox(getContext());
        cb.setText(name);
        cb.setTextSize(22);
        boolean checked = prefs.getBoolean(key, (Boolean) defaults.get(key));
        cb.setChecked(checked);

        if(dependency != null && !checked){
            for(int i = 0; i < dependency.length; i++){
                dependency[i].setVisibility(View.GONE);
            }
        }

        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getEditor().putBoolean(key, isChecked).apply();
            if(dependency != null){
                for(int i = 0; i < dependency.length; i++){
                    dependency[i].setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    System.out.println(dependency[i]);
                }
            }
        });

        return new View[]{new MaterialDivider(getContext()), cb};
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String[] alliance_pos_list = new String[]{"red-1", "red-2", "red-3",
                                                  "blue-1", "blue-2", "blue-3"};

        SettingsManager manager = new SettingsManager(getContext());

        manager.addItem(new StringSettingsItem(
                UnameKey,
                "Username",
                ""
        ));
        manager.addItem(new DropdownSettingsItem(SelEVCodeKey, "Event Code", "", fileEditor.getEventList().toArray(new String[0])));
        manager.addItem(new DropdownSettingsItem(AllyPosKey, "Alliance Pos", "", alliance_pos_list));



        binding.SettingsTable.addView(manager.getSettingsView());

//        addViews(addStringEdit("Username", UnameKey));
//        addViews(addDropdownByString("Event Code", fileEditor.getEventList().toArray(new String[0]), SelEVCodeKey));
//        addViews(addDropdownByString("Alliance Position", alliance_pos_list, AllyPosKey));
//        addViews(addNumberEdit("Team Number", TeamNumKey));
//
//        View[] FTPDependency = concatArrays(
//                addCheckbox("Send Meta Files", FTPSendMetaFiles, new View[]{}),
//                addStringEdit("FTP Server", FTPServer)
//        );
//
//        View[] WifiDependency = addCheckbox("FTP Enabled", FTPEnabled, FTPDependency);
//        addViews(addCheckbox("Wifi Mode", WifiModeKey, concatArrays(FTPDependency, WifiDependency)));
//        addViews(WifiDependency);
//        addViews(FTPDependency);



        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }














    public abstract class SettingsItem<T> {
        private String key;
        private String title;
        private String description;
        private T defaultValue;
        private boolean enabled = true;

        public SettingsItem(String key, String title, String description, T defaultValue) {
            this.key = key;
            this.title = title;
            this.description = description;
            this.defaultValue = defaultValue;
        }

        public abstract View createView(Context context);
        public abstract T getValue();

        public String getKey() { return key; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public T getDefaultValue() { return defaultValue; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isEnabled() { return enabled; }
    }

    public class StringSettingsItem extends SettingsItem<String> {
        public StringSettingsItem(String key, String title, String description) {
            super(key, title, description, prefs.getString(key, (String) defaults.get(key)));
        }

        @Override
        public View createView(Context context) {
            TextInputLayout textInputLayout = new TextInputLayout(context);
            textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            TextInputEditText editText = new TextInputEditText(context);
            editText.setText(getValue());
            editText.setEnabled(isEnabled());

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    getEditor().putString(getKey(), s.toString()).apply();
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

            textInputLayout.addView(editText);
            return textInputLayout;
        }

        @Override
        public String getValue() {
            return prefs.getString(getKey(), (String) defaults.get(getKey()));
        }
    }

    public class NumberSettingsItem extends SettingsItem<Integer> {
        private int min;
        private int max;

        public NumberSettingsItem(String key, String title, String description, int min, int max) {
            super(key, title, description, prefs.getInt(key, (int) defaults.get(key)));
            this.min = min;
            this.max = max;
        }

        @Override
        public View createView(Context context) {
            TextInputLayout textInputLayout = new TextInputLayout(context);
            TextInputEditText editText = new TextInputEditText(context);

            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setText(getValue());
            editText.setEnabled(isEnabled());

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        int value = Integer.parseInt(s.toString());
                        if (value >= min && value <= max) {
                            getEditor().putInt(getKey(), value).apply();
                        }
                    } catch (NumberFormatException e) {
                        editText.setText(String.valueOf(getDefaultValue()));
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

            textInputLayout.addView(editText);
            return textInputLayout;
        }

        @Override
        public Integer getValue() {
            return prefs.getInt(getKey(), (int) defaults.get(getKey()));
        }
    }

    public class DropdownSettingsItem extends SettingsItem<String> {
        private String[] options;

        public DropdownSettingsItem(String key, String title, String description, String[] options) {
            super(key, title, description, prefs.getString(key, (String) defaults.get(key)));
            this.options = options;
        }

        @Override
        public View createView(Context context) {
            TextInputLayout textInputLayout = new TextInputLayout(context);
            textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textInputLayout.setHint("Select " + getTitle());



            TextView dropdown = new TextView(getContext());

            dropdown.setTextSize(24);
            dropdown.setText(getValue());

            ArrayList optionsList = new ArrayList();
            for(int i = 0; i < options.length; i++)
                optionsList.add(options[i]);

            CustomSpinner dialog = CustomSpinner.newInstance(optionsList);

            dialog.setOnOptionSelectedListener(option -> {
                getEditor().putString(getKey(), option).apply();
                dropdown.setText(option);
            });

            dropdown.setOnClickListener(v -> {
                dialog.show(getActivity().getSupportFragmentManager(), getTitle());
            });

//            List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
//            for(int i = 0; i < options.length; i++){
//                iconSpinnerItems.add(new IconSpinnerItem(options[i]));
//            }
//            IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(dropdown);
//            dropdown.setSpinnerAdapter(iconSpinnerAdapter);
//
//            dropdown.setEnabled(isEnabled());
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                    context,
//                    android.R.layout.simple_spinner_dropdown_item,
//                    options
//            );
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//            int index = Arrays.asList(options).indexOf(getValue());
//
//            if(dropdown.length() != 0 && index != -1){
//                dropdown.selectItemByIndex(index);
//            }
//
//            dropdown.setOnSpinnerItemSelectedListener(
//                (OnSpinnerItemSelectedListener<IconSpinnerItem>)
//                    (oldIndex, oldItem, newIndex, newItem) ->  getEditor().putString(getKey(), newItem.getText().toString()).apply());
//
//            dropdown.setPadding(10,20,10,20);
//            dropdown.setBackgroundColor(0xf0000000);
//            dropdown.setTextColor(0xff00ff00);
//            dropdown.setTextSize(14.5f);
//            dropdown.setArrowGravity(SpinnerGravity.END);
//            dropdown.setArrowPadding(8);
//            dropdown.setSpinnerPopupElevation(14);


//            spinner.setAdapter(adapter);

            // Set initial selection
//            String currentValue = getValue();
//            for (int i = 0; i < options.length; i++) {
//                if (options[i].equals(currentValue)) {
//                    spinner.setSelection(i);
//                    break;
//                }
//            }
//            spinner.setText(getValue());
//            spinner.setTextSize(24);


//            spinner.addOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    settings.put(getKey(), options[position]);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                    settings.put(getKey(), getDefaultValue());
//                }
//            });

            textInputLayout.addView(dropdown);
            return textInputLayout;
        }

        @Override
        public String getValue() {
            return prefs.getString(getKey(), (String) defaults.get(getKey()));
        }
    }

    public class CheckboxSettingsItem extends SettingsItem<Boolean> {
        private List<SettingsItem<?>> controlledItems;

        public CheckboxSettingsItem(String key, String title, String description, SettingsItem<?>... controlledItems) {
            super(key, title, description, prefs.getBoolean(key, (Boolean) defaults.get(key)));
            this.controlledItems = Arrays.asList(controlledItems);
        }

        @Override
        public View createView(Context context) {
            MaterialCheckBox checkBox = new MaterialCheckBox(context);
            checkBox.setText(getTitle());
            checkBox.setChecked(getValue());

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                getEditor().putBoolean(getKey(), isChecked).apply();
                for (SettingsItem<?> item : controlledItems) {
                    item.setEnabled(isChecked);
                }
            });

            return checkBox;
        }

        @Override
        public Boolean getValue() {
            return prefs.getBoolean(getKey(), (Boolean) defaults.get(getKey()));
        }
    }

    public class SettingsManager {
        private Context context;
        private HashMap<String, Object> settings;
        private List<SettingsItem<?>> items;
        private LinearLayout container;

        public SettingsManager(Context context) {
            this.context = context;
            this.items = new ArrayList<>();
            this.container = new LinearLayout(context);
            this.container.setOrientation(LinearLayout.VERTICAL);
            this.container.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
        }

        public void addItem(SettingsItem<?> item) {
            items.add(item);

            MaterialCardView card = new MaterialCardView(context);
            LinearLayout itemContainer = new LinearLayout(context);
            itemContainer.setOrientation(LinearLayout.VERTICAL);
            itemContainer.setPadding(32, 16, 32, 16);

            TextView titleView = new TextView(context);
            titleView.setText(item.getTitle());
            titleView.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle1);

            TextView descriptionView = new TextView(context);
            descriptionView.setText(item.getDescription());
            descriptionView.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2);

            itemContainer.addView(titleView);
            itemContainer.addView(descriptionView);
            itemContainer.addView(item.createView(context));

            card.addView(itemContainer);
            container.addView(card);
        }

        public View getSettingsView() {
            return container;
        }
    }

}