package hchuphal.sctg_mobile_app;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;


public class Ping extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    Button btn2;
    EditText input2, textDeviceping;
    String command;
    //String Host = "www.google.com";
    private Spinner spinner1;
    boolean flag = false;
    String pingStr = "";
    Handler handler;
    Timer timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Mail to @ hchuphal@cisco.com", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        //addItemsOnSpinner2();
        //addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        input2 = (EditText) findViewById(R.id.editPing);

        btn2 = (Button) findViewById(R.id.buttonPing);
        textDeviceping = (EditText) findViewById(R.id.textPingResult);
        handler = new Handler(callback);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment objFragment = null;
        if (id == R.id.nav_nw_info) {
            // Check nw info here
            CheckNetworkInfo();
        }else if (id == R.id.nav_sys) {
            SysInfo();
        }
        else if (id == R.id.nav_home) {
            Home();
        }
        else if (id == R.id.nav_tp) {
            DlUlTPStats();

        }
        else if (id == R.id.nav_graph) {
            liveGraph();
        }else if (id == R.id.nav_shell) {
            ShellExeu();
        }else if (id == R.id.nav_ping) {
            //PingTest();

        }  else if (id == R.id.nav_manage) {
           Logs();

        } else if (id == R.id.nav_share) {
            Toast.makeText(Ping.this,"Send your Queries to hchuphal@cisco.com",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {
            Toast.makeText(Ping.this,"WinsPire Team SCTG App!",Toast.LENGTH_LONG).show();
            About();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Home(){
        //Log.i(" App Home", "You Clicked to run DlUlTP ");
        Toast.makeText(Ping.this,"Home",Toast.LENGTH_LONG).show();
        finish();
        //System.exit(0);

    }


    public void DlUlTPStats(){
        Log.i(" DlUlTP commands", "You Clicked to run DlUlTP ");
        Toast.makeText(Ping.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.DlUlTp"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void liveGraph(){
        Log.i(" Live Graph commands", "You Clicked to check Live Graph ");
        Toast.makeText(Ping.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.LiveGraph"));

        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void SysInfo(){
        Log.i(" Sysinfo commands", "You Clicked to run SysInfo");
        Toast.makeText(Ping.this,"Run SysInfo..!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.sysInfo"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }

    public void About(){

        Toast.makeText(Ping.this,"About Us  :) ",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.About"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void ShellExeu(){
        Log.i(" Shell comamnds", "You Clicked to run shell commands");
        Toast.makeText(Ping.this,"Run Shell commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.ShellExecuter"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }

    private void CheckNetworkInfo() {
        Log.i(" Cisco Network on 4G", "You Clicked the Netwrok Button");
        Toast.makeText(Ping.this, "Cisco Network Info!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.networkMonitor"));
    }
    private void Logs() {
        Log.i(" Logs Analysis!!", "You Clicked the Logs Button");
        Toast.makeText(Ping.this, "Logs Analysis!!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Logs"));
    }
    public void PingTest(){
        Log.i(" Ping commands","You Clicked to run Ping commands");
        Toast.makeText(Ping.this,"Run Ping commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Ping"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }




    public void CheckPingClick(View arg0) {
        // TODO Auto-generated method stub
        textDeviceping.setTextColor(Color.parseColor("blue"));
        textDeviceping.setText("Checking Please wait....");
        timer.schedule(new SmallDelay(), 100);

    }

    Callback callback = new Callback() {
        public boolean handleMessage(Message msg) {
            ShellExecuter exe = new ShellExecuter();
            command = input2.getText().toString();
            //executeCmd(command, true);


            textDeviceping.setVisibility(View.VISIBLE);
            //textDeviceping.setText(" Trying to Ping the HOST...");

            String str = "";
            int k = 0;
            try {
                Process process = Runtime.getRuntime().exec(
                        "/system/bin/ping -c 5 " + command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                int i;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                if ((reader.read(buffer)) < 0) {
                    textDeviceping.setTextColor(Color.parseColor("red"));
                    textDeviceping.setText("Host Not Reachable !! " + "\n" + "Please Check Mobile Connections !!");
                    flag = false;

                } else {
                    flag = true;
                    textDeviceping.setTextColor(Color.parseColor("black"));
                    //BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while((str = reader.readLine())!= null) {
                    //while (k < 6) {
                        textDeviceping.append("\n " + str);
                        //pingStr = reader.readLine();
                    }
                }
                process.destroy();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(" Exception:" + e);
            }
            return true;
        }
    };

    class SmallDelay extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinnerIP);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    private class CustomOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(parent.getContext(),
                    "Host IP to Ping : " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();
            EditText hostIP = (EditText) findViewById(R.id.editPing);
            hostIP.setTextColor(Color.parseColor("blue"));
            hostIP.setText(parent.getItemAtPosition(pos).toString());

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}

