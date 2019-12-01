package com.example.soc_macmini_15.sqlitepractice.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.soc_macmini_15.sqlitepractice.DB.EmployeeOperations;
import com.example.soc_macmini_15.sqlitepractice.Fragments.DatePickerFragment;
import com.example.soc_macmini_15.sqlitepractice.Model.Employee;
import com.example.soc_macmini_15.sqlitepractice.R;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.widget.AdapterView;

public class AddUpdateEmployee extends AppCompatActivity implements DatePickerFragment.DateDialogListener {
    //our color theme initially default
    private String colorTheme = "Default";

    private static final String EXTRA_EMP_ID = "com.example.soc_macmini_15.sqlitepractice.empId";
    private static final String EXTRA_ADD_UPDATE = "com.example.soc_macmini_15.sqlitepractice.add_update";
    private static final String DIALOG_DATE = "DialogDate";
    private ImageView calendarImage;
    private RadioGroup radioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText tta;
    private EditText hireDateEditText;
    private Button addUpdateButton;
    private Employee newEmployee;
    private Employee oldEmployee;
    private String mode;
    private long empId;
    private EmployeeOperations employeeData;
    private Spinner spinner1, spinner2, spinner3;
    private static String firstItem;
    private String language = "English";



    String[] tasks = new String[]{
                "Ticket Sales",
                "Security",
                "Janitor",
                "Restaurant Attendant"
    };

    String[] destinations= new String[]{
            "Gate",
            "Roller Coaster",
            "Ticket Counter",
            "Restaurant"
    };


    String[] status = new String[]{
            "Available",
            "Busy"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddUpdateEmployee.this);
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
        setContentView(R.layout.activity_add_update_employee);

        newEmployee = new Employee();
        oldEmployee = new Employee();
        firstNameEditText = findViewById(R.id.edit_text_first_name);
        tta = findViewById(R.id.tta);
        lastNameEditText = findViewById(R.id.edit_text_last_name);
        hireDateEditText = findViewById(R.id.edit_text_hire_date);
        radioGroup = findViewById(R.id.radio_gender);
        femaleRadioButton = findViewById(R.id.radio_female);
        maleRadioButton = findViewById(R.id.radio_male);
        calendarImage = findViewById(R.id.image_view_hire_date);
        addUpdateButton = findViewById(R.id.button_add_update_employee);
        employeeData = new EmployeeOperations(this);

        //populate spinner items based on language
        language = prefs.getString("language", "English");

        switch (language) {
            case "English":
                tasks = new String[]{
                        "Ticket Sales",
                        "Security",
                        "Janitor",
                        "Restaurant Attendant"
                };

                destinations = new String[]{
                        "Gate",
                        "Roller Coaster",
                        "Ticket Counter",
                        "Restaurant"
                };

                status = new String[]{
                        "Available",
                        "Busy"
                };
                break;
            case "Svenska":
                tasks = new String[]{
                        "Biljettförsäljning",
                        "Säkerhetsvakt",
                        "Städare",
                        "Serveringspersonal"
                };

                destinations = new String[]{
                        "Entré",
                        "Berg och Dalbana",
                        "Biljettkassa",
                        "Restaurang"
                };

                status = new String[]{
                        "Tillgänglig",
                        "Upptagen"
                };
                break;
        }
        // Get reference of widgets from XML layout

        final Spinner spinner1 = (Spinner) findViewById(R.id.task_spinner);
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,tasks
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner1.setAdapter(spinnerArrayAdapter);
        //spinner1.setOnItemSelectedListener(new ItemSelectedListener());


        final Spinner spinner2 = (Spinner) findViewById(R.id.status_spinner);
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,status
        );
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
        spinner2.setAdapter(spinnerArrayAdapter1);



        final Spinner spinner3 = (Spinner) findViewById(R.id.destination_spinner);
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,destinations
        );
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
        spinner3.setAdapter(spinnerArrayAdapter2);

        Log.d("test", "onCreate: " + getIntent().getStringExtra(EXTRA_ADD_UPDATE));
        mode = getIntent().getStringExtra(EXTRA_ADD_UPDATE);
        if (mode != null)
            if (mode.equals("Update")) {
                addUpdateButton.setText("Update Employee");
                empId = getIntent().getLongExtra(EXTRA_EMP_ID, 0);

                initializeEmployee(empId);
            }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_male) {
                    newEmployee.setGender("M");
                    if (mode.equals("Update")) {
                        oldEmployee.setGender("M");
                    }
                } else if (i == R.id.radio_female) {
                    newEmployee.setGender("F");
                    if (mode.equals("Update")) {
                        oldEmployee.setGender("F");
                    }
                }
            }
        });
        calendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.show(manager, DIALOG_DATE);
            }
        });
        addUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employeeData.open();
                if (mode != null) {
                    if (mode.equals("Add")) {
                        newEmployee.setFirstName(firstNameEditText.getText().toString());
                        newEmployee.settta(tta.getText().toString());
                        newEmployee.setLastName(lastNameEditText.getText().toString());
                        newEmployee.setHireDate(hireDateEditText.getText().toString());
                        newEmployee.setTask(spinner1.getSelectedItem().toString());
                        newEmployee.setStatus(spinner2.getSelectedItem().toString());
                        newEmployee.setdestination(spinner3.getSelectedItem().toString());
                        if (maleRadioButton.isChecked())
                            newEmployee.setGender("M");
                        else
                            newEmployee.setGender("F");


                        employeeData.addEmployee(newEmployee);
                        employeeData.close();
                        Toast.makeText(AddUpdateEmployee.this, "Employee " + newEmployee.getFirstName() +
                                " is added successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AddUpdateEmployee.this, HomePage.class);
                        startActivity(i);
                    } else {
                        oldEmployee.setFirstName(firstNameEditText.getText().toString());
                        oldEmployee.settta(tta.getText().toString());
                        oldEmployee.setLastName(lastNameEditText.getText().toString());
                        oldEmployee.setHireDate(hireDateEditText.getText().toString());
                        oldEmployee.setTask(spinner1.getSelectedItem().toString());
                        oldEmployee.setStatus(spinner2.getSelectedItem().toString());
                        oldEmployee.setdestination(spinner3.getSelectedItem().toString());
                        if (maleRadioButton.isChecked())
                            oldEmployee.setGender("M");
                        else
                            oldEmployee.setGender("F");
                        employeeData.updateEmployee(oldEmployee);
                        employeeData.close();
                        Toast.makeText(AddUpdateEmployee.this, "Staff " + oldEmployee.getFirstName() +
                                " is added successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AddUpdateEmployee.this, HomePage.class);
                        startActivity(i);
                    }
                }
            }
        });
    }


    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

        //get strings of first item
        String firstItem = String.valueOf(spinner1.getSelectedItem());

        public  void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (firstItem.equals(String.valueOf(spinner1.getSelectedItem()))) {
                // ToDo when first item is selected
                //deptEditText = firstItem;

            } else {
                Toast.makeText(parent.getContext(),
                        "You have selected Task : " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                // Todo when item is selected by the user
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }

    public void initializeEmployee(long empId) {
        oldEmployee = employeeData.getEmployee(empId);
        if (oldEmployee != null) {
            firstNameEditText.setText(oldEmployee.getFirstName());
            lastNameEditText.setText(oldEmployee.getLastName());
            tta.setText(oldEmployee.gettta());
            hireDateEditText.setText(oldEmployee.getHireDate());
            radioGroup.check(oldEmployee.getGender().equals("M") ? R.id.radio_male : R.id.radio_female);
            String searchedItem = oldEmployee.getTask();
            Spinner spinner1 = (Spinner) findViewById(R.id.task_spinner);
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this,R.layout.spinner_item,tasks
            );
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinner1.setAdapter(spinnerArrayAdapter);
            int itemPosition = spinnerArrayAdapter.getPosition(searchedItem);
            if(itemPosition == -1)
            {
                String message = searchedItem + " : Item not found."+oldEmployee;
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
            else
            {
                String message = searchedItem + " : Item found and selected."+searchedItem;
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                spinner1.setSelection(itemPosition);
            }

            //SPINNER 2
            String searchedItem2 = oldEmployee.getStatus();
            Spinner spinner2 = (Spinner) findViewById(R.id.status_spinner);
            final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                    this,R.layout.spinner_item,status
            );
            spinnerArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
            spinner2.setAdapter(spinnerArrayAdapter2);
            int itemPosition2 = spinnerArrayAdapter2.getPosition(searchedItem2);
            if(itemPosition2 == -1)
            {
                String message = searchedItem2 + " : Item not found."+oldEmployee;
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
            else
            {
                String message = searchedItem2 + " : Item found and selected."+searchedItem2;
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                spinner2.setSelection(itemPosition2);
            }

            //SPINNER 3
            String searchedItem3 = oldEmployee.getdestination();
            Spinner spinner3 = (Spinner) findViewById(R.id.destination_spinner);
            final ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                    this,R.layout.spinner_item,destinations
            );
            spinnerArrayAdapter3.setDropDownViewResource(R.layout.spinner_item);
            spinner3.setAdapter(spinnerArrayAdapter3);
            int itemPosition3 = spinnerArrayAdapter3.getPosition(searchedItem3);
            if(itemPosition3 == -1)
            {
                String message = searchedItem3 + " : Item not found."+oldEmployee;
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
            else
            {
                String message = searchedItem3 + " : Item found and selected."+searchedItem3;
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                spinner3.setSelection(itemPosition3);
            }

        } else {
            Toast.makeText(this, "Staff not Present", Toast.LENGTH_SHORT).show();
        }
    }


    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String hireDate = sdf.format(date);
        return hireDate;
    }

    @Override
    public void onFinishDialog(Date date) {
        hireDateEditText.setText(formatDate(date));
    }
}
