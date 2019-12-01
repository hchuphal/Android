package com.example.soc_macmini_15.sqlitepractice.Activity;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soc_macmini_15.sqlitepractice.DB.EmployeeOperations;
import com.example.soc_macmini_15.sqlitepractice.Model.Employee;
import com.example.soc_macmini_15.sqlitepractice.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

//team-06

public class handleEmployeesTime extends ListActivity {
    //our color theme initially default
    private String colorTheme = "Default";

    //queue that handles our requests
    RequestQueue requestQueue;

    //example URL for GET
    String baseURL = "" +
            "https://api.myjson.com/bins/eex88";

    /*for testing POST
     inspect the request with https://putsreq.com/NSt1X50NTEF3KVwtC4Im/inspect
    */
    //String URL = "https://putsreq.com/NSt1X50NTEF3KVwtC4Im";
    String URL = "https://putsreq.com/WoEWlraANJ2tHskspZRI";

    //textview that displays the response code
    TextView tv;

    private Button getButton,postButton;
    private EmployeeOperations employeeOperations;
    List<Employee> employees;
    private String language = "English";
    String[] tasks;
    String selectedTask;
    private static String firstItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(handleEmployeesTime.this);
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
        setContentView(R.layout.handle_rest2);


        employeeOperations=new EmployeeOperations(this);
        employeeOperations.open();
        employees=employeeOperations.getAllEmployeesTime();
        employeeOperations.close();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        //final TextView jsonText = (TextView) findViewById(R.id.json2); //get the id for TextView
        //jsonText.setText(gson.toJson(employees));
        language = prefs.getString("language", "English");

        switch (language) {
            case "English":
                tasks = new String[]{
                        "Ticket Sales",
                        "Security",
                        "Janitor",
                        "Restaurant Attendant"
                };

                break;
            case "Svenska":
                tasks = new String[]{
                        "Biljettförsäljning",
                        "Säkerhetsvakt",
                        "Städare",
                        "Serveringspersonal"
                };
                break;
        }
        final Spinner spinner1 = (Spinner) findViewById(R.id.task_spinner);
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,tasks
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner1.setAdapter(spinnerArrayAdapter);
        //spinner1.setOnItemSelectedListener(new ItemSelectedListener());
        //spinner1.getSelectedItem().toString();
        firstItem = String.valueOf(spinner1.getSelectedItem());
        selectedTask = spinner1.getSelectedItem().toString();

        final String jsonformat=gson.toJson(employees);
        Toast.makeText(this, "Staff Communication", Toast.LENGTH_SHORT).show();

        postButton = findViewById(R.id.btnChangeText4);
        getButton = findViewById(R.id.btnChangeText3);

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(this);

        tv=findViewById(R.id.textViewTime);


        //handles the GET logic
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTask = spinner1.getSelectedItem().toString();
                getReq(selectedTask);
            }

        });
        //handles the POST logic
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReq(jsonformat);
            }
        });

    }

    private void postReq(String jsonformat)  {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            final JSONArray jsonBody = new JSONArray(jsonformat);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    tv.setText("Request is sent successfully with code :"+ response);
                    if (response.equals(200))
                    {
                        Toast.makeText(handleEmployeesTime.this,
                                "Request is sent successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getReq(String selectedTask){
        switch (selectedTask) {
            case "Janitor":
                baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/janitortime";
                break;
            case "Security":
                baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/securitytime";
                break;
            case "Ticket Sales":
                baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/ticketsalestime";
                break;
            case "Restaurant Attendant":
                baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/restaurantattendanttime";
                break;
            default:
                baseURL ="UnkownUrl";
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, baseURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray jsonArray = response;
                            tv.setText("\n\n");
                            tv.setText("Request Recieved from Team-06...\n\n");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject employee = jsonArray.getJSONObject(i);
                                String destination = employee.getString("destination");
                                String task = employee.getString("task");
                                tv.append("destination: " + destination +"\n"
                                         + "task: "+ task +"\n\n");
                            }
                        } catch (JSONException e) {
                            tv.setText("Error Getting the Request\n\n");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

}