package com.example.soc_macmini_15.sqlitepractice.Activity;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.soc_macmini_15.sqlitepractice.DB.EmployeeOperations;
import com.example.soc_macmini_15.sqlitepractice.Model.Employee;
import com.example.soc_macmini_15.sqlitepractice.R;

import java.util.List;

public class ViewAllEmployees extends ListActivity {
    private String colorTheme = "Default";

    private EmployeeOperations employeeOperations;
    List<Employee> employees;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ViewAllEmployees.this);
        colorTheme = prefs.getString("color", "Default");
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
        setContentView(R.layout.activity_view_all_employees);

        employeeOperations=new EmployeeOperations(this);
        employeeOperations.open();
        employees=employeeOperations.getAllEmployees();
        employeeOperations.close();
        if(!employees.isEmpty()) {
            ArrayAdapter<Employee> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, employees);
            setListAdapter(adapter);
        }
        else
            Toast.makeText(this, "No Data Present", Toast.LENGTH_SHORT).show();
    }
}
