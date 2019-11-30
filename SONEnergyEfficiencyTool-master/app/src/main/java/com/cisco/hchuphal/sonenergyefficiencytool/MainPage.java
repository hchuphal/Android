package com.cisco.hchuphal.sonenergyefficiencytool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainPage extends Activity {
    private Spinner spinner1;
    public static String blueEyeUrl="";
    EditText hostIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Button btnCode = (Button) findViewById(R.id.button);
        hostIP = (EditText) findViewById(R.id.editText);

        addListenerOnSpinnerItemSelection();

        //blueEyeUrl=hostIP.getText();



            btnCode.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    //Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://management2.dev.ubiquisys.local/export/scratchpad/jenkins/kpi_dashboard/dashboardTool/KpiDashboard.html#dash0"));
                    //startActivity(intent);
                    blueEyeUrl=hostIP.getText().toString();
                    if(blueEyeUrl != null && !blueEyeUrl.isEmpty()){
                    Log.i(" son server", blueEyeUrl);
                        Log.i(" son server", "Yes *****");
                    Toast.makeText(MainPage.this, "Login ..Please wait", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(".MainActivity"));
                }
                    else{
                        Log.i(" son server",blueEyeUrl);
                        Toast.makeText(MainPage.this, "Please enter a valid server!!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }


    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinnerIP);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(parent.getContext(),
                    "SON SERVER IP : " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();

            hostIP.setTextColor(Color.parseColor("blue"));
            hostIP.setText(parent.getItemAtPosition(pos).toString());
            //blueEyeUrl=hostIP.getText().toString();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}
