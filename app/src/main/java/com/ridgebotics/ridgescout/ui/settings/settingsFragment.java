package com.ridgebotics.ridgescout.ui.settings;

import static com.ridgebotics.ridgescout.utility.settingsManager.AllyPosKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.CustomEventsKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.SelEVCodeKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.UnameKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.WifiModeKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.YearNumKey;
import static com.ridgebotics.ridgescout.utility.settingsManager.defaults;
import static com.ridgebotics.ridgescout.utility.settingsManager.getEditor;
import static com.ridgebotics.ridgescout.utility.settingsManager.prefs;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ridgebotics.ridgescout.databinding.FragmentSettingsBinding;
import com.ridgebotics.ridgescout.ui.CustomSpinnerPopup;
import com.ridgebotics.ridgescout.ui.CustomSpinnerView;
import com.ridgebotics.ridgescout.utility.fileEditor;
import com.ridgebotics.ridgescout.utility.settingsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class settingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String[] alliance_pos_list = new String[]{"red-1", "red-2", "red-3",
                                                  "blue-1", "blue-2", "blue-3"};

        SettingsManager manager = new SettingsManager(getContext());


        manager.addItem(new CheckboxSettingsItem(CustomEventsKey, "Custom Events"));

        StringSettingsItem FTPServer = new StringSettingsItem(settingsManager.FTPServer, "FTP Server (Sync)");
        manager.addItem(FTPServer);
        CheckboxSettingsItem FTPSendMetaFiles = new CheckboxSettingsItem(settingsManager.FTPSendMetaFiles, "Sync meta files");
        manager.addItem(FTPSendMetaFiles);
        CheckboxSettingsItem FTPEnabled = new CheckboxSettingsItem(settingsManager.FTPEnabled, "FTP Enabled", FTPServer, FTPSendMetaFiles);
        manager.addItem(FTPEnabled);

        manager.addItem(new CheckboxSettingsItem(WifiModeKey, "Wifi Mode", FTPEnabled));

        manager.addItem(new NumberSettingsItem(YearNumKey, "Year", 0, 9999));

        manager.addItem(new DropdownSettingsItem(AllyPosKey, "Alliance Pos", alliance_pos_list));
        manager.addItem(new DropdownSettingsItem(SelEVCodeKey, "Event Code", fileEditor.getEventList().toArray(new String[0])));
        manager.addItem(new StringSettingsItem(UnameKey, "Username"));

        manager.getView(binding.SettingsTable);


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
        private T defaultValue;

        public SettingsItem(String key, String title, T defaultValue) {
            this.key = key;
            this.title = title;
            this.defaultValue = defaultValue;
        }

        public abstract View createView(Context context);
        public abstract T getValue();

        public String getKey() { return key; }
        public String getTitle() { return title; }
        public T getDefaultValue() { return defaultValue; }
        public abstract void setEnabled(boolean enabled);
    }

    public class StringSettingsItem extends SettingsItem<String> {
        public StringSettingsItem(String key, String title) {
            super(key, title, prefs.getString(key, (String) defaults.get(key)));
        }

        TextInputEditText editText;

        @Override
        public void setEnabled(boolean enabled){
            editText.setEnabled(enabled);
        }

        @Override
        public View createView(Context context) {
            TextInputLayout textInputLayout = new TextInputLayout(context);
            textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            editText = new TextInputEditText(context);
            editText.setText(getValue());

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

        public NumberSettingsItem(String key, String title, int min, int max) {
            super(key, title, prefs.getInt(key, (int) defaults.get(key)));
            this.min = min;
            this.max = max;
        }

        TextInputEditText editText;

        @Override
        public void setEnabled(boolean enabled){
            editText.setEnabled(enabled);
        }

        @Override
        public View createView(Context context) {
            TextView titleView = new TextView(context);
            titleView.setText(getTitle());
            titleView.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle1);

            TextInputLayout textInputLayout = new TextInputLayout(context);
            editText = new TextInputEditText(context);

            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setText(String.valueOf(getValue()));

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
            textInputLayout.addView(titleView);
            return textInputLayout;
        }

        @Override
        public Integer getValue() {
            return prefs.getInt(getKey(), (int) defaults.get(getKey()));
        }
    }

    public class DropdownSettingsItem extends SettingsItem<String> {
        private String[] options;

        private boolean enabled = true;

        @Override
        public void setEnabled(boolean enabled){
            this.enabled = enabled;
        }

        public DropdownSettingsItem(String key, String title, String[] options) {
            super(key, title, prefs.getString(key, (String) defaults.get(key)));
            this.options = options;
        }

        @Override
        public View createView(Context context) {
            CustomSpinnerView dropdown = new CustomSpinnerView(getContext());
            dropdown.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));


            ArrayList<String> optionsList = new ArrayList<>(Arrays.asList(options));

            dropdown.setTitle(getTitle());
            dropdown.setOptions(optionsList, getValue());
            dropdown.setOption(getValue());

            dropdown.setOnClickListener((item, index) -> {
                getEditor().putString(getKey(), item).apply();
            });

            return dropdown;
        }

        @Override
        public String getValue() {
            return prefs.getString(getKey(), (String) defaults.get(getKey()));
        }
    }

    public class CheckboxSettingsItem extends SettingsItem<Boolean> {
        private List<SettingsItem<?>> controlledItems;

        public CheckboxSettingsItem(String key, String title, SettingsItem<?>... controlledItems) {
            super(key, title, prefs.getBoolean(key, (Boolean) defaults.get(key)));
            this.controlledItems = Arrays.asList(controlledItems);
        }

        MaterialCheckBox checkBox;

        @Override
        public void setEnabled(boolean enabled){
            checkBox.setEnabled(enabled);
            for (SettingsItem<?> item : controlledItems) {
                item.setEnabled(enabled && checkBox.isChecked());
            }
        }

        @Override
        public View createView(Context context) {
            checkBox = new MaterialCheckBox(context);
            checkBox.setText(getTitle());
            checkBox.setChecked(getValue());
            checkBox.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle1);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                getEditor().putBoolean(getKey(), isChecked).apply();
                for (SettingsItem<?> item : controlledItems) {
                    item.setEnabled(isChecked);
                }
            });

            for (SettingsItem<?> item : controlledItems) {
                item.setEnabled(getValue());
            }

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
//        private LinearLayout container;

        public SettingsManager(Context context) {
            this.context = context;
            this.items = new ArrayList<>();
//            this.container = new LinearLayout(context);
//            this.container.setOrientation(LinearLayout.VERTICAL);
//            this.container.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            ));
        }

        private final List<View> views = new ArrayList<>();
        public void addItem(SettingsItem<?> item) {
            items.add(item);

            LinearLayout itemContainer = new LinearLayout(context);
            itemContainer.setOrientation(LinearLayout.VERTICAL);
            itemContainer.setPadding(32, 0, 32, 8);

            itemContainer.addView(item.createView(context));
            views.add(itemContainer);
        }

        public void getView(LinearLayout layout) {
            for(int i = views.size()-1; i >= 0; i--)
                layout.addView(views.get(i));
        }
    }
}