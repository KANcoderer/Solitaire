package edu.psu.solitaire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.preference.PreferenceFragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar settingsToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(settingsToolbar);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_toolbar,menu);
        return true;
    }
    public void startHome(MenuItem item) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    public void startContinueGame(MenuItem item){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.root_preferences);
        }

    }

}