package com.example.soc_macmini_15.sqlitepractice.Activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.googlecode.gmail4j.rss.RssGmailClient;

import java.io.IOException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;


import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;
//import com.googlecode.gmail4j.GmailMessageList;
import com.googlecode.gmail4j.http.HttpGmailConnection;
import android.os.StrictMode;


//team-08

public class handleEmployees extends ListActivity {
    //our color theme initially default
    private String colorTheme = "Default";

    //queue that handles our requests
    RequestQueue requestQueue;

    //example URL for GET
    //String baseURL = "https://api.myjson.com/bins/qgwlw";
    String baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/janitor";
    /*for testing POST
     inspect the request with https://putsreq.com/NSt1X50NTEF3KVwtC4Im/inspect
    */
    String URL = "https://putsreq.com/NSt1X50NTEF3KVwtC4Im";
    /*
            // Build a response
        var msg = '200';
        var incomingJSON = JSON.parse(request.body);
        if(incomingJSON[0].destination) {
          msg = '[{"destination":"Roller_Coaster","firstName":"Oscar","staffId":1,"task":"Janitor","timeToDestination":"10"}]';
        } else {
          msg = request.body;
        }
        //msg = request.body[0];
        response.body = msg;
        // Forward a request
        // request.forwardTo = 'http://example.com/api';
     */

    //textview that displays the response code
    TextView tv;

    private Button getButton,postButton,startServer ;
    private EmployeeOperations employeeOperations;
    private Employee newEmployee;
    private Employee oldEmployee;
    private EmployeeOperations employeeData;
    List<Employee> employees;
    private Spinner spinner1, spinner2, spinner3;
    private static String firstItem;
    private String language = "English";
    String[] tasks;
    String selectedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(handleEmployees.this);
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
        setContentView(R.layout.handle_rest);

        employeeOperations=new EmployeeOperations(this);
        employeeOperations.open();
        employees=employeeOperations.getAllEmployees();
        employeeOperations.close();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        //final TextView jsonText = (TextView) findViewById(R.id.json); //get the id for TextView
        //jsonText.setText(gson.toJson(employees));
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
        // Get reference of widgets from XML layout

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

        //postButton = findViewById(R.id.btnChangeText);
        getButton = findViewById(R.id.btnChangeText2);
        //startServer = findViewById(R.id.server);

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(this);
        newEmployee = new Employee();
        oldEmployee = new Employee();
        employeeData = new EmployeeOperations(this);

        tv=findViewById(R.id.textView);
        /*
        startServer.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v){
                try {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8)
                    {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    //readEmail();
                    tv.setText("Reading Requests.....");
                    String host = "pop.gmail.com";// change accordingly
                    String mailStoreType = "pop3";
                    String username = "team0607082019@gmail.com";
                    String password = "Agile007";// change accordingly

                    check(host, mailStoreType, username, password);
                    }
                }
                catch (Throwable t) {
                    Log.e("eMAIL Exception", t.toString());
                }

            }

        });*/

        //handles the GET logic
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTask = spinner1.getSelectedItem().toString();
                getReq(selectedTask);
            }

        });
        //handles the POST logic
        /*postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReq(jsonformat);
            }
        });*/

    }

    private void readEmail(){
    GmailClient client = new RssGmailClient();
    String gmailPass = "Agile007";
    GmailConnection connection = new HttpGmailConnection("team0607082019@gmail.com", gmailPass.toCharArray());
                    client.setConnection(connection);
    final List<GmailMessage> messages = client.getUnreadMessages();
                    for (GmailMessage message : messages) {
        //System.out.println(message);
        tv.setText("Request is sent successfully with code :"+ message);
    }
    }

        public  void check(String host, String storeType, String user,
                                 String password)
        {
            try {

                //create properties field
                Properties properties = new Properties();

                properties.put("mail.pop3.host", host);
                properties.put("mail.pop3.port", "995");
                properties.put("mail.pop3.starttls.enable", "true");
                Session emailSession = Session.getInstance(properties);

                //create the POP3 store object and connect with the pop server
                Store store = emailSession.getStore("pop3s");

                store.connect(host, user, password);

                //create the folder object and open it
                Folder emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                // retrieve the messages from the folder in an array and print it
                Message[] messages = emailFolder.getMessages();
                tv.setText("Request is sent successfully with code :"+ messages.length);
                System.out.println("messages.length---" + messages.length);

                for (int i = 0, n = messages.length; i < n; i++) {
                    Message message = messages[i];
                    System.out.println("---------------------------------");
                    System.out.println("Email Number " + (i + 1));
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + message.getFrom()[0]);
                    System.out.println("Text: " + message.getContent().toString());

                }

                //close the store and folder objects
                emailFolder.close(false);
                store.close();

            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private void sendEmail(String message) {
        //Getting content for email
        String email = "guschuhi@student.gu.se";
        String subject = "team07-message";
        //String message = "GET Mesaage from team08";

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }


    private void postReq(String jsonformat)  {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            final JSONArray jsonBody = new JSONArray(jsonformat);

            final String requestBody = jsonBody.toString();
            //sendEmail(jsonBody.toString());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    tv.setText("Request is sent successfully with code :"+ response);
                    if (response.equals(200))
                    {
                        Toast.makeText(handleEmployees.this,
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
                    baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/janitor";
                    break;
                case "Security":
                    baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/security";
                    break;
                case "Ticket Sales":
                    baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/ticketsales";
                    break;
                case "Restaurant Attendant":
                    baseURL = "https://us-central1-team07-d5b52.cloudfunctions.net/staff/restaurantattendant";
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
                            tv.setText("Response Recieved from Team-08...\n");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject employee = jsonArray.getJSONObject(i);

                                String destination = employee.getString("destination");
                                String firstName = employee.getString("firstName");
                                String gender = employee.getString("gender");
                                String lastName = employee.getString("lastName");
                                int staffId = employee.getInt("staffId");
                                String status = employee.getString("status");
                                String task = employee.getString("task");
                                String scheduleDate = employee.getString("taskScheduleDate");
                                String timeToDestination = employee.getString("timeToDestination");
                                tv.append("destination: " + destination +"\n"
                                         + "firstName: "+ firstName +"\n"
                                         + "gender: "+ gender +"\n"
                                         + "lastName: "+ lastName +"\n"
                                         + "staffId: " + String.valueOf(staffId) + "\n"
                                         + "status: "+ status +"\n"
                                         + "task: "+ task +"\n"
                                         + "scheduleDate: "+ scheduleDate +"\n"
                                         + "timeToDestination: "+ timeToDestination +"\n\n");
                                employeeData.open();
                                oldEmployee.setEmpId(staffId);
                                oldEmployee.setFirstName(firstName);
                                oldEmployee.settta(timeToDestination);
                                oldEmployee.setLastName(lastName);
                                oldEmployee.setHireDate(scheduleDate);
                                oldEmployee.setTask(task);
                                oldEmployee.setStatus(status);
                                oldEmployee.setdestination(destination);
                                oldEmployee.setGender(gender);

                                employeeData.updateEmployee(oldEmployee);
                                employeeData.close();
                            }
                        } catch (JSONException e) {
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