package com.example.soc_macmini_15.sqlitepractice.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.graphics.Color;


import android.content.res.Configuration;
import java.util.Locale;

import com.example.soc_macmini_15.sqlitepractice.DB.EmployeeOperations;
import com.example.soc_macmini_15.sqlitepractice.Model.Employee;
import com.example.soc_macmini_15.sqlitepractice.R;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    //our color theme initially default
    private String colorTheme = "Default";

    private String language = "English";


    private Button addEmployeeButton, editEmployeeButton, deleteEmployeeButton, viewAllEmployeeButton;
    private Button preferencesButton, restButton, restButton2, feedback;

    private EmployeeOperations employeeOperations=new EmployeeOperations(HomePage.this);
    private static final String EXTRA_EMP_ID = "com.example.soc_macmini_15.sqlitepractice.empId";
    private static final String EXTRA_ADD_UPDATE = "com.example.soc_macmini_15.sqlitepractice.add_update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomePage.this);

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
        setContentView(R.layout.activity_main2);
        addEmployeeButton = findViewById(R.id.btn_add_employee);
        editEmployeeButton = findViewById(R.id.btn_update_employee);
        deleteEmployeeButton = findViewById(R.id.btn_delete_employee);
        viewAllEmployeeButton = findViewById(R.id.btn_view_employees);
        preferencesButton = findViewById(R.id.btn_pref);
        restButton = findViewById(R.id.btn_rest);
        restButton2 = findViewById(R.id.btn_rest2);
        feedback = findViewById(R.id.btn_feedback);

        addEmployeeButton.setOnClickListener(this);
        editEmployeeButton.setOnClickListener(this);
        deleteEmployeeButton.setOnClickListener(this);
        viewAllEmployeeButton.setOnClickListener(this);
        preferencesButton.setOnClickListener(this);
        restButton.setOnClickListener(this);
        restButton2.setOnClickListener(this);
        feedback.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_employee:
                Intent i = new Intent(HomePage.this, AddUpdateEmployee.class);
                i.putExtra(EXTRA_ADD_UPDATE, "Add");
                startActivity(i);
                break;
            case R.id.btn_update_employee:
                getEmpIdAndUpdateEmp();
                break;
            case R.id.btn_delete_employee:
                getEmpIdRemoveEmp();
                break;
            case R.id.btn_view_employees:
                Intent intent = new Intent(HomePage.this, ViewAllEmployees.class);
                startActivity(intent);
                break;
            case R.id.btn_pref:
                Intent intentpref = new Intent(HomePage.this, PreferencesActivity.class);
                startActivity(intentpref);
                break;
            case R.id.btn_rest:
                Intent intentRest = new Intent(HomePage.this, handleEmployees.class);
                startActivity(intentRest);
                break;
            case R.id.btn_rest2:
                Intent intentRest2 = new Intent(HomePage.this, handleEmployeesTime.class);
                startActivity(intentRest2);
                break;
            case R.id.btn_feedback:
                Intent intentRest3 = new Intent(HomePage.this, Feedback.class);
                startActivity(intentRest3);
                break;

        }

    }

    private void getEmpIdRemoveEmp() {
        LayoutInflater li = LayoutInflater.from(this);
        View getEmpIdView = li.inflate(R.layout.dialog_get_emp_id, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(getEmpIdView);
        final EditText userInput = (EditText) getEmpIdView.findViewById(R.id.editTextDialogUserInput);
        //set Dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = userInput.getText().toString();
                        if (input.matches("[0-9]+") && Integer.parseInt(input) <= employeeOperations.getAllEmployees().size() ) {
//                            employeeOperations = new EmployeeOperations(HomePage.this);
                            Log.e("Test", "onClick: " + Long.parseLong(userInput.getText().toString()));
                            Employee employee = employeeOperations.getEmployee(Long.parseLong(userInput.getText().toString()));
                            if (employee != null) {
                                employeeOperations.removeEmployee(employee);
                                Toast.makeText(HomePage.this, "Employee Removed Successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomePage.this, "Employee Not Present", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HomePage.this, "Enter the Id", Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    private void getEmpIdAndUpdateEmp() {
        LayoutInflater li = LayoutInflater.from(this);
        View getEmpIdView = li.inflate(R.layout.dialog_get_emp_id, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //Setting dialog_get_emp_id.xml to alerttdialog builder
        alertDialogBuilder.setView(getEmpIdView);
        final EditText userInput = (EditText) getEmpIdView.findViewById(R.id.editTextDialogUserInput);

        //Set Dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //get use input and set it to result
                        //edit Text

                        String input = userInput.getText().toString();
                        if (input.matches("[0-9]+") && Integer.parseInt(input) <= employeeOperations.getAllEmployees().size())  {
                            Log.e("Test", "onClick: " + Long.parseLong(userInput.getText().toString()));
                            Intent intent = new Intent(HomePage.this, AddUpdateEmployee.class);
                            intent.putExtra(EXTRA_ADD_UPDATE, "Update");
                            intent.putExtra(EXTRA_EMP_ID, Long.parseLong(userInput.getText().toString()));
                            startActivity(intent);
                        } else {
                            Toast.makeText(HomePage.this, "Enter the Id", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (employeeOperations != null)
            employeeOperations.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (employeeOperations != null)
            employeeOperations.close();
    }
}
