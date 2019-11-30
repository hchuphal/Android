package hchuphal.sctg_mobile_app;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellExecuter extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    Button btn;
    EditText input;
    EditText out;
    String command;
    private Spinner spinner1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell_executer);

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



        addListenerOnSpinnerItemSelection();
    input = (EditText)findViewById(R.id.editCommand);
        //input.setScroller(new Scroller(this));
        //input.setMaxLines(1);
        //input.setVerticalScrollBarEnabled(true);
        //input.setMovementMethod(new ScrollingMovementMethod());

        btn = (Button)findViewById(R.id.buttonCommand);
    out = (EditText)findViewById(R.id.textOutput);

        out.setScroller(new Scroller(this));
        //input.setMaxLines(1);
        out.setVerticalScrollBarEnabled(true);
        out.setMovementMethod(new ScrollingMovementMethod());
    btn.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            ShellExecuter exe = new ShellExecuter();
            command = input.getText().toString();
            String outp = exe.MExecuter(command);
            out.setText(outp);
            Log.d("Output", outp);
        }
    });

}

    private String MExecuter(String command)
    {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;

    }
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinnerCmd);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(parent.getContext(),
                    "Command to execute : " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();
            EditText hostIP = (EditText) findViewById(R.id.editCommand);
            hostIP.setTextColor(Color.parseColor("blue"));
            hostIP.setText(parent.getItemAtPosition(pos).toString());

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

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
            //ShellExeu();
        }else if (id == R.id.nav_ping) {
            PingTest();

        }  else if (id == R.id.nav_manage) {
            Logs();

        } else if (id == R.id.nav_share) {
            Toast.makeText(ShellExecuter.this,"Send your Queries to hchuphal@cisco.com",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {
            Toast.makeText(ShellExecuter.this,"WinsPire Team SCTG App!",Toast.LENGTH_LONG).show();
            About();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Home(){
//Log.i(" App Home", "You Clicked to run DlUlTP ");
        Toast.makeText(ShellExecuter.this,"Home",Toast.LENGTH_LONG).show();
        finish();
//System.exit(0);

    }


    public void DlUlTPStats(){
        Log.i(" DlUlTP commands", "You Clicked to run DlUlTP ");
        Toast.makeText(ShellExecuter.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.DlUlTp"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void liveGraph(){
        Log.i(" Live Graph commands", "You Clicked to check Live Graph ");
        Toast.makeText(ShellExecuter.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.LiveGraph"));

//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void SysInfo(){
        Log.i(" Sysinfo commands", "You Clicked to run SysInfo");
        Toast.makeText(ShellExecuter.this,"Run SysInfo..!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.sysInfo"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    public void About(){

        Toast.makeText(ShellExecuter.this,"About Us  :) ",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.About"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void ShellExeu(){
        Log.i(" Shell comamnds", "You Clicked to run shell commands");
        Toast.makeText(ShellExecuter.this,"Run Shell commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.ShellExecuter"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    private void CheckNetworkInfo() {
        Log.i(" Cisco Network on 4G", "You Clicked the Netwrok Button");
        Toast.makeText(ShellExecuter.this, "Cisco Network Info!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.networkMonitor"));
    }
    private void Logs() {
        Log.i(" Logs Analysis!!", "You Clicked the Logs Button");
        Toast.makeText(ShellExecuter.this, "Logs Analysis!!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Logs"));
    }
    public void PingTest(){
        Log.i(" Ping commands","You Clicked to run Ping commands");
        Toast.makeText(ShellExecuter.this,"Run Ping commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Ping"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }



}
