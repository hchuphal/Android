package hchuphal.sctg_mobile_app;

import android.app.Fragment;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class DlUlTp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Handler mHandler = new Handler();
    //private long BeforeTime = System.currentTimeMillis();
    private float mStartTotalTxBytes = 0;
    private float mStartMobileTotalRxBytes = 0;
    private float mStartTotalRxBytes = 0;
    private float mStartMobileTotalTxBytes = 0;
    float dlBytes[]={0,0};
    float ulBytes[]={0,0};
    float MdlBytes[]={0,0};
    float MulBytes[]={0,0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dl_ul_tp);

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




        mStartTotalTxBytes = getTotalTxBytes();
        mStartMobileTotalRxBytes = getMobileRxBytes();
        mStartTotalRxBytes = getTotalRxBytes();
        mStartMobileTotalTxBytes = getMobileTxBytes();


        if (mStartTotalTxBytes == TrafficStats.UNSUPPORTED || mStartTotalRxBytes == TrafficStats.UNSUPPORTED) {
            //AlertDialog.Builder alert = new AlertDialog.Builder(this);
            //alert.setTitle("Uh Oh!");
            //alert.setMessage("Your device does not support traffic stat monitoring.");
            //alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }

    }


    public float getTotalRxBytes() {
        return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes());
    }

    public float getTotalTxBytes() {
        return TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes());
    }

    public float getMobileRxBytes() {
        return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getMobileRxBytes());
    }

    public float getMobileTxBytes() {
        return TrafficStats.getMobileTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getMobileTxBytes());
    }

    private final Runnable mRunnable = new Runnable() {

        public void run() {

            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(3);
            float TotalRxBytes,dlRate,ulRate,MdlRate,MulRate;
            float TotalTxBytes;
            TotalRxBytes = TrafficStats.getTotalRxBytes() - mStartTotalRxBytes;
            dlBytes[1]=TotalRxBytes;
            dlRate=dlBytes[1]-dlBytes[0];
            TextView textDeviceUL = (TextView) findViewById(R.id.textDL);
            // textDeviceUL.setText(((int) TotalRxBeforeTest)/2048+ " Kbps");
            textDeviceUL.setText(df.format((dlRate/1048576)*8) + " Mbps");
            dlBytes[0]=dlBytes[1];

            TotalTxBytes = TrafficStats.getTotalTxBytes() - mStartTotalTxBytes;
            ulBytes[1]=TotalTxBytes;
            ulRate=ulBytes[1]-ulBytes[0];
            TextView textDeviceDL = (TextView) findViewById(R.id.textUL);
            //textDeviceDL.setText(((int) TotalTxBeforeTest)/2048 + " Kbps");
            textDeviceDL.setText(df.format((ulRate/1048576)*8) + " Mbps");
            ulBytes[0]=ulBytes[1];


            //DL Mobile Data
            float MobileTotalTxBytes = TrafficStats.getMobileTxBytes() - mStartMobileTotalTxBytes;
            MdlBytes[1]=MobileTotalTxBytes;
            dlRate=MdlBytes[1]-MdlBytes[0];
            TextView textDeviceMDL = (TextView) findViewById(R.id.textMul);
            MdlRate=MdlBytes[1]-MdlBytes[0];
            //textDeviceDL.setText(((int) TotalTxBeforeTest)/2048 + " Kbps");
            textDeviceMDL.setText(df.format((MdlRate/1048576)*8) + " Mbps");
            MdlBytes[0]=MdlBytes[1];


            //MOBILE UL
            float MobileTotalRxBytes = TrafficStats.getMobileRxBytes() - mStartMobileTotalRxBytes;
            MulBytes[1]=MobileTotalRxBytes;
            MulRate=MulBytes[1]-MulBytes[0];
            TextView textDeviceMUL = (TextView) findViewById(R.id.textMdl);
            // textDeviceUL.setText(((int) TotalRxBeforeTest)/2048+ " Kbps");
            textDeviceMUL.setText(df.format((MulRate/1048576)*8) + " Mbps");
            MulBytes[0]=MulBytes[1];

            TextView infoViewMintf = (TextView)findViewById(R.id.textMintf);
            //TextView infoViewAllintf = (TextView)findViewById(R.id.textAllintf);
            String infoMintf = "";
            String infoAllintf = "";
            infoViewMintf.setMovementMethod(null);
            //infoViewAllintf.setMovementMethod(null);

            infoMintf += "Mobile Interface : ";
            infoMintf += ("\n Total RX: " + TrafficStats.getMobileRxBytes() + " bytes/  " + TrafficStats.getMobileRxPackets() + " packets");
            infoMintf += ("\n Total TX: " + TrafficStats.getMobileTxBytes() + " bytes/ " + TrafficStats.getMobileTxPackets() + " packets");

            //infoAllintf += "All Interfaces:";
            //infoAllintf += ("RX : " + TrafficStats.getTotalRxBytes() + " bytes / " + TrafficStats.getTotalRxPackets() + " Packets");
            //infoAllintf += ("TX : " + TrafficStats.getTotalTxBytes() + " bytes / " + TrafficStats.getTotalTxPackets() + " Packets");

            infoViewMintf.setText(infoMintf);
            //infoViewMintf.setText(infoAllintf);
            mHandler.postDelayed(mRunnable, 1000);

        }

    };






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
            //DlUlTPStats();

        }
        else if (id == R.id.nav_graph) {
            liveGraph();
        }else if (id == R.id.nav_shell) {
            ShellExeu();
        }else if (id == R.id.nav_ping) {
            PingTest();

        }  else if (id == R.id.nav_manage) {
            Logs();

        } else if (id == R.id.nav_share) {
            Toast.makeText(DlUlTp.this,"Send your Queries to hchuphal@cisco.com",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {
            Toast.makeText(DlUlTp.this,"WinsPire Team SCTG App!",Toast.LENGTH_LONG).show();
            About();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Home(){
//Log.i(" App Home", "You Clicked to run DlUlTP ");
        Toast.makeText(DlUlTp.this,"Home",Toast.LENGTH_LONG).show();
        finish();
//System.exit(0);

    }


    public void DlUlTPStats(){
        Log.i(" DlUlTP commands", "You Clicked to run DlUlTP ");
        Toast.makeText(DlUlTp.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.DlUlTp"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void liveGraph(){
        Log.i(" Live Graph commands", "You Clicked to check Live Graph ");
        Toast.makeText(DlUlTp.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.LiveGraph"));

//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void SysInfo(){
        Log.i(" Sysinfo commands", "You Clicked to run SysInfo");
        Toast.makeText(DlUlTp.this, "Run SysInfo..!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.sysInfo"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    public void About(){

        Toast.makeText(DlUlTp.this,"About Us  :) ",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.About"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void ShellExeu(){
        Log.i(" Shell comamnds", "You Clicked to run shell commands");
        Toast.makeText(DlUlTp.this,"Run Shell commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.ShellExecuter"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    private void CheckNetworkInfo() {
        Log.i(" Cisco Network on 4G", "You Clicked the Netwrok Button");
        Toast.makeText(DlUlTp.this, "Cisco Network Info!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.networkMonitor"));
    }
    private void Logs() {
        Log.i(" Logs Analysis!!", "You Clicked the Logs Button");
        Toast.makeText(DlUlTp.this, "Logs Analysis!!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Logs"));
    }
    public void PingTest(){
        Log.i(" Ping commands","You Clicked to run Ping commands");
        Toast.makeText(DlUlTp.this,"Run Ping commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Ping"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

}



