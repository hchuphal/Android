
package com.example.soc_macmini_15.sqlitepractice.Activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.soc_macmini_15.sqlitepractice.R;
import android.content.Intent;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    //our color theme initially default
    private String colorTheme = "Default";
    private String language = "English";
    Button b1,b2;
    EditText ed1,ed2;

    private TextView textViewResult;
    TextView tx1;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        language = prefs.getString("language", "English");
        Configuration config = getBaseContext().getResources().getConfiguration();

        switch (language){
            case "English":
                Locale locale = new Locale("en");
                config.locale = locale;
                break;
            case "Svenska":
                Locale locale2 = new Locale("sv");
                config.locale = locale2;
                break;

        }

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


        colorTheme = prefs.getString("color", "Default");
        Log.d("theme",colorTheme);
        switch (colorTheme) {
            case "Default":
                setTheme(R.style.DefaultTheme);
                break;
            case "Dark Theme":
                setTheme(R.style.DarkTheme);
                break;
            case "Bright Theme":
                setTheme(R.style.BrightTheme);
                break;
            case "Disneyworld Theme":
                setTheme(R.style.DisneyworldTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);

        b2 = (Button)findViewById(R.id.button2);
        tx1 = (TextView)findViewById(R.id.textView3);
        tx1.setVisibility(View.GONE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed1.getText().toString().equals("admin") &&
                        ed2.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();

                    tx1.setVisibility(View.VISIBLE);
                    tx1.setBackgroundColor(Color.RED);
                    counter--;
                    tx1.setText(Integer.toString(counter));

                    if (counter == 0) {
                        b1.setEnabled(false);
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}