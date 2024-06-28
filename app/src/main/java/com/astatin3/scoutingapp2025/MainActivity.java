package com.astatin3.scoutingapp2025;

import android.os.Bundle;

import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.astatin3.scoutingapp2025.databinding.ActivityMainBinding;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        latestSettings.update();

        if(!fileEditor.fileExist(fields.matchFieldsFilename)){
            fields.save(fields.matchFieldsFilename, fields.default_match_fields);
        }

        if(!fileEditor.fileExist(fields.pitsFieldsFilename)){
            fields.save(fields.pitsFieldsFilename, fields.default_pit_fields);
        }

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_scouting, R.id.navigation_transfer, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}