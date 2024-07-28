package com.astatin3.scoutingapp2025;

import android.os.Bundle;

import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.utility.SentimentAnalysis;
import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.astatin3.scoutingapp2025.databinding.ActivityMainBinding;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private BottomNavigationView navView;

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        latestSettings.update();

        if(!fileEditor.fileExist(fields.matchFieldsFilename)){
            fields.save(fields.matchFieldsFilename, fields.default_match_fields);
        }

        if(!fileEditor.fileExist(fields.pitsFieldsFilename)){
            fields.save(fields.pitsFieldsFilename, fields.default_pit_fields);
        }

        AlertManager.init(this);
        SentimentAnalysis.init(this);

        Objects.requireNonNull(getSupportActionBar()).hide();




        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        navView = findViewById(R.id.nav_view);
//        appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_scouting,
//                R.id.navigation_match_scouting,
//                R.id.navigation_team_selector,
//                R.id.navigation_pit_scouting,
//
//                R.id.navigation_data,
//                R.id.navigation_data_status,
//                R.id.navigation_data_teams,
//                R.id.navigation_data_compile,
//                R.id.navigation_data_fields_chooser,
//                R.id.navigation_data_fields,
//
//                R.id.navigation_transfer,
//                R.id.navigation_file_selector,
//                R.id.navigation_transfer_selector,
//                R.id.navigation_code_generator,
//                R.id.navigation_code_scanner,
//                R.id.navigation_bluetooth_sender,
//                R.id.navigation_bluetooth_receiver,
//                R.id.navigation_tba,
//
//                R.id.navigation_settings)
//                .build();

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_scouting,
                R.id.navigation_data,
                R.id.navigation_transfer,
                R.id.navigation_settings)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}