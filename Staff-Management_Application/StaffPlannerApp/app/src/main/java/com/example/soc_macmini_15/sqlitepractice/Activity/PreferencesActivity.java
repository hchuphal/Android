package com.example.soc_macmini_15.sqlitepractice.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.soc_macmini_15.sqlitepractice.R;


public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //keeps the standardAppTheme for preference
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Preference colorpref = getPreferenceManager().findPreference("color");
        final Preference langpref = getPreferenceManager().findPreference("language");

        if (colorpref != null) {
            colorpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    colorpref.setSummary(newValue.toString());
                    prefs.edit().putString("color", newValue.toString()).apply();
                    return true;
                }
            });
        }

        if (langpref != null) {
            langpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    langpref.setSummary(newValue.toString());
                    prefs.edit().putString("language", newValue.toString()).apply();
                    Log.d("language", newValue.toString());
                    return true;
                }
            });
        }
    }

    //ugly hack to call onCreate after pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(PreferencesActivity.this, HomePage.class);
        startActivity(intent);
    }


}
