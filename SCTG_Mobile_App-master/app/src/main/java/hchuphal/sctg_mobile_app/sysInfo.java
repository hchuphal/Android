package hchuphal.sctg_mobile_app;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

public class sysInfo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    Boolean ConnectionStatus;
    Button btnCode;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override


        public void onReceive(Context c, Intent i) {
            int level = i.getIntExtra("level", 0);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);


            pb.setProgress(level);


            TextView tv = (TextView )findViewById(R.id.textBattery);


            tv.setText("Battery Level: "+Integer.toString(level)+"%");


        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_info);


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





        //setContentView(R.layout.activity_main);
        TextView textDeviceIP1 = (TextView) findViewById(R.id.textWIFIIP);
        textDeviceIP1.setText(getWifiIP());
        TextView textDeviceT1 = (TextView) findViewById(R.id.textAndroidV);
        textDeviceT1.setText(getAndroidVersion());
        TextView textDeviceIP2 = (TextView)findViewById(R.id.textMIP);
        textDeviceIP2.setText(getMobileIP());
        TextView textDeviceT2 = (TextView)findViewById(R.id.textIMEI);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        textDeviceT2.setText(getDeviceID(telephonyManager));
        TextView textDeviceT3 = (TextView)findViewById(R.id.textConnStatus);
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        ConnectionStatus= isConnectedFast(sysInfo.this);
        if( ConnectionStatus){
            textDeviceT3.setText("Active (Mobile/Wifi)");
        }
        else{
            textDeviceT3.setText("No Active Connections");
        }

        TextView textDeviceT4 = (TextView)findViewById(R.id.textConnType);
        textDeviceT4.setText(getNetworkClass(sysInfo.this));

        btnCode = (Button)findViewById(R.id.btnAndroidCodes);

        btnCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    //What the dialer really does when you enter the code is extracting the number between *#*# and #*#* and then broadcasting the following intent:
                    sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://4636")));

                } catch (ActivityNotFoundException activityException) {
                    Log.e("Wrong Code", "Call failed", activityException);
                }
            }
        });

    }

    public static String getNetworkClass(Context context)
    {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info==null || !info.isConnected())
            return "-"; //not connected
        if(info.getType() == ConnectivityManager.TYPE_WIFI)
            return "Active WIFI";
        if(info.getType() == ConnectivityManager.TYPE_MOBILE){
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return "Active 2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                    return "Active 3G";
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    return "Active 4G";
                default:
                    return "?";
            }
        }
        return "?";
    }

    public static boolean isConnectedFast(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo info = cm.getActiveNetworkInfo();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //return (info != null && info.isConnected() && Connectivity.isConnectionFast(info.getType(), tm.getNetworkType()));


        //ConnectivityManager mConnectivity = null;
        //TelephonyManager mTelephony = null;
// Skip if no connection, or background data disabled
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !cm.getBackgroundDataSetting()) {
            return false;
        }

// Only update if WiFi or 3G is connected and not roaming
        int netType = info.getType();
        int netSubtype = info.getSubtype();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            return info.isConnected();
        } else if (netType == ConnectivityManager.TYPE_MOBILE
                && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
                && !tm.isNetworkRoaming()) {
            return info.isConnected();
        } else {
            return false;
        }
    }
    String getDeviceID(TelephonyManager phonyManager){

        String id = phonyManager.getDeviceId();
        if (id == null){
            id = "not available";
        }

        int phoneType = phonyManager.getPhoneType();
        switch(phoneType){
            case TelephonyManager.PHONE_TYPE_NONE:
                return "NONE: " + id;

            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM IMEI " + id;

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA MEID/ESN " + id;

 /*
  *  for API Level 11 or above
  *  case TelephonyManager.PHONE_TYPE_SIP:
  *   return "SIP";
  */

            default:
                return "UNKNOWN: ID=" + id;
        }

    }
    /** Get IP For mobile */
    public String getMobileIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    //if (!inetAddress.isLoopbackAddress()) {
                    if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address){
                        //if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {

                        String ipaddress = inetAddress .getHostAddress().toString();
                        return ipaddress;
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e(TAG, "Exception in Get IP Address: " + ex.toString());
            Toast.makeText(sysInfo.this," Ip Not found",Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    /**
     * Get the IP of current Wi-Fi connection
     *
     * @return IP as string
     */
    private String getWifiIP() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception ex) {
            Toast.makeText(sysInfo.this, " Ip Not found", Toast.LENGTH_LONG).show();
            return null;
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
            //SysInfo();
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
            PingTest();

        }  else if (id == R.id.nav_manage) {
            Logs();

        } else if (id == R.id.nav_share) {
            Toast.makeText(sysInfo.this,"Send your Queries to hchuphal@cisco.com",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {
            Toast.makeText(sysInfo.this,"WinsPire Team SCTG App!",Toast.LENGTH_LONG).show();
            About();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Home(){
//Log.i(" App Home", "You Clicked to run DlUlTP ");
        Toast.makeText(sysInfo.this,"Home",Toast.LENGTH_LONG).show();
        finish();
//System.exit(0);

    }


    public void DlUlTPStats(){
        Log.i(" DlUlTP commands", "You Clicked to run DlUlTP ");
        Toast.makeText(sysInfo.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.DlUlTp"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void liveGraph(){
        Log.i(" Live Graph commands", "You Clicked to check Live Graph ");
        Toast.makeText(sysInfo.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.LiveGraph"));

//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void SysInfo(){
        Log.i(" Sysinfo commands", "You Clicked to run SysInfo");
        Toast.makeText(sysInfo.this,"Run SysInfo..!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.sysInfo"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    public void About(){

        Toast.makeText(sysInfo.this,"About Us  :) ",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.About"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void ShellExeu(){
        Log.i(" Shell comamnds", "You Clicked to run shell commands");
        Toast.makeText(sysInfo.this,"Run Shell commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.ShellExecuter"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    private void CheckNetworkInfo() {
        Log.i(" Cisco Network on 4G", "You Clicked the Netwrok Button");
        Toast.makeText(sysInfo.this, "Cisco Network Info!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.networkMonitor"));
    }
    private void Logs() {
        Log.i(" Logs Analysis!!", "You Clicked the Logs Button");
        Toast.makeText(sysInfo.this, "Logs Analysis!!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Logs"));
    }
    public void PingTest(){
        Log.i(" Ping commands","You Clicked to run Ping commands");
        Toast.makeText(sysInfo.this,"Run Ping commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Ping"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

}