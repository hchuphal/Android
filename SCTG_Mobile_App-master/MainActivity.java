package hchuphal.sctg_mobile_app;

import android.Manifest;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btnDisplayNw;
    Boolean ConnectionStatus;
	private CheckBox airplanemode, lte, twog,threeg,global;
private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 225;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){Toast.makeText(getApplication(), "Permission Granted",Toast.LENGTH_LONG).show();  }
                else { Toast.makeText(getApplication(), "Permission required",Toast.LENGTH_LONG).show(); } return; } }}
    private Handler mainHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // only for gingerbread and newer versions
            String permission = Manifest.permission.READ_PHONE_STATE;
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                // Add your function here which open camera
            }
        } else {
            // Add your function here which open camera
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        airplanemode = (CheckBox) findViewById(R.id.checkBoxairplane);
        if (!airplanemode.isChecked()) {
            Toast.makeText(MainActivity.this,
                    "Airplane Not Enabled", Toast.LENGTH_LONG).show();
        }
        airplanemode.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        mainHandler.postDelayed(mRunnable, 1000);

    }
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            TextView textDeviceIP1 = (TextView) findViewById(R.id.textIPv4);
            //textDeviceIP1.setText(getMobileIP());
            String ipaddress = getMobileIP();
            if ( ipaddress!=null)
                textDeviceIP1.setText(getMobileIP());
            else {
                TextView textDeviceIP2 = (TextView) findViewById(R.id.textIPv4);
                textDeviceIP1.setText(getWifiIP());
            }



            TextView textDeviceIP4 = (TextView) findViewById(R.id.textConnStatus);

            ConnectionStatus = isConnectedFast(MainActivity.this);
            if (ConnectionStatus) {
                textDeviceIP4.setText("Active");
            } else {
                textDeviceIP4.setText("No Active Connections");
            }
            TextView textDeviceIP5 = (TextView) findViewById(R.id.textConnType);
            textDeviceIP5.setText(getNetworkClass(MainActivity.this));
            /*
            TextView textDeviceInfo = (TextView) findViewById(R.id.textInfo);
            if (new String(getNetworkClass(MainActivity.this)).equals("Active WIFI")) {
                textDeviceInfo.setText("Active wifi Connection Detected!");
            } else if (new String(getNetworkClass(MainActivity.this)).equals("Active 3G")) {
                textDeviceInfo.setText("Active 3G Connection Detected!");
            } else if (new String(getNetworkClass(MainActivity.this)).equals("Active 4G")) {
                textDeviceInfo.setText("Active 4G Connection Detected!");
            } else if (new String(getNetworkClass(MainActivity.this)).equals("Active 2G")) {
                textDeviceInfo.setText("Active 2G Connection Detected!");
            } else {
                textDeviceInfo.setText("NO ACTIVE Connections!!");
            }
            */
            TextView textDeviceIP6 = (TextView) findViewById(R.id.textInternet);
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                //new DownloadWebpageTask().execute(stringUrl);
                textDeviceIP6.setText("Active Internet Available");
            } else {
                textDeviceIP6.setText("No Internet available.");
            }
            TextView textDeviceIP7 = (TextView) findViewById(R.id.textMdataStatus);
            if (getConnectivityStatusString(MainActivity.this)) {
                textDeviceIP7.setText(" Enabled ");
            }else{
                textDeviceIP7.setText(" Disabled ");
            }
            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

            int cid = cellLocation.getCid();
            int lac = cellLocation.getLac();
            int psc = cellLocation.getPsc();
            TextView cellIdTxt = (TextView)findViewById(R.id.textCellid);
            cellIdTxt.setText(" "+cid);
            TextView lacTxt = (TextView)findViewById(R.id.textLac);
            lacTxt.setText(" "+lac);
            TextView pscTxt = (TextView)findViewById(R.id.textPsc);
            pscTxt.setText(" " + psc);
            String networkOperator = telephonyManager.getNetworkOperator();

            if (networkOperator != null) {
                TextView mccTxt = (TextView)findViewById(R.id.textMCC);
                mccTxt.setText(networkOperator.substring(0, 3));
                TextView mncTxt = (TextView)findViewById(R.id.textMNC);
                mncTxt.setText(networkOperator.substring(3));
            }



            mainHandler.postDelayed(mRunnable, 1000);
        }
    };

    public static boolean getConnectivityStatusString(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // Skip if no connection, or background data disabled
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null) {
                return false;
            }
// Only update if WiFi and not roaming
            int netType = info.getType();
            int netSubtype = info.getSubtype();
            if (netType == ConnectivityManager.TYPE_WIFI) {
                return false;
            } else if (netType == ConnectivityManager.TYPE_MOBILE){
                return true;
            } else {
                return false;
            }

    }

    /**
     * Get the IP of current Wi-Fi connection
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
            Toast.makeText(MainActivity.this," Ip Not found",Toast.LENGTH_LONG).show();
            return null;
        }
    }

    class myCheckBoxChnageClicker implements CheckBox.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub;

            // Toast.makeText(CheckBoxCheckedDemo.this, &quot;Checked =&gt; &quot;+isChecked, Toast.LENGTH_SHORT).show();
            if (isChecked) {
                try{

                    //ConnectivityManager mgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    //Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
                    //dataMtd.setAccessible(true);
                    //dataMtd.invoke(mgr, true,false);

                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this,
                            "Data Not enabled", Toast.LENGTH_LONG).show();

                }
            }
            /*if (isChecked) {
                try {
                    boolean isEnabled = Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
                    Settings.System.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
                    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                    intent.putExtra("state", !isEnabled);
                    sendBroadcast(intent);
                    Toast.makeText(MainActivity.this,
                            "Airplane Mode is Enabled", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,
                            "Airplane Mode NOT Enabled", Toast.LENGTH_LONG).show();
                }
            }
                else{
                    Toast.makeText(MainActivity.this,
                            "Airplane Disabled back again", Toast.LENGTH_LONG).show();
                }*/

                //settings put global airplane_mode_on 1
                /*try {
                    //Process proc= Runtime.getRuntime().exec("adb settings put global airplane_mode_on 1");
                    //Process process = Runtime.getRuntime().exec("adb am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
                    //Process proc = Runtime.getRuntime().exec("pwd");
                    //BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                    Toast.makeText(MainActivity.this,
                            "Airplane Mode is Enabled", Toast.LENGTH_LONG).show();
                }
            catch (IOException e) {
                // body.append("Error\n");
                e.printStackTrace();
            }*/
                //Boolean isEnabled = isAirplaneModeOn(MainActivity.this);
                // Toggle airplane mode.
                //setSettings(MainActivity.this, isEnabled?1:0);
                // Post an intent to reload.



        }
        public  boolean isAirplaneModeOn(Context context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return Settings.System.getInt(context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0) != 0;
            } else {
                return Settings.Global.getInt(context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
            }
        }
        public void setSettings(Context context, int value) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(
                        context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, value);
            } else {
                Settings.Global.putInt(
                        context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, value);
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            System.exit(1);
        }

        return super.onOptionsItemSelected(item);
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
        else if (id == R.id.nav_tp) {
            DlUlTPStats();

        }else if (id == R.id.nav_shell) {
            ShellExeu();
        }else if (id == R.id.nav_ping) {
            PingTest();

        }else if (id == R.id.nav_codes) {
            MobileCodes();

        } else if (id == R.id.nav_kpi) {
            KPIDash();

        } else if (id == R.id.nav_manage) {
            Logs();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {
            Toast.makeText(MainActivity.this,"WinsPire Team SCTG App!",Toast.LENGTH_LONG).show();
            About();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private String getSystemIP(){
        try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {
                //Log.i(LOG_TAG, ex.toString());
            Toast.makeText(MainActivity.this," Ip Not found",Toast.LENGTH_LONG).show();
            }

            return null;


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
            Toast.makeText(MainActivity.this," Ip Not found",Toast.LENGTH_LONG).show();
        }
        return null;
    }


    public static boolean isConnectedFast(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
 // Skip if no connection, or background data disabled
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !cm.getBackgroundDataSetting()) {
            return false;
        }
// Only update if WiFi and not roaming
        int netType = info.getType();
        int netSubtype = info.getSubtype();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            return info.isConnected();
        } else if (netType == ConnectivityManager.TYPE_MOBILE
                && !tm.isNetworkRoaming()) {
            return info.isConnected();
        } else {
            return false;
        }
    }
    public static String getNetworkClass(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info==null || !info.isConnected())
            return "None"; //not connected
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

    public static String MobileSignalStrength(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            public void onCallForwardingIndicatorChanged(boolean cfi) {}
            public void onCallStateChanged(int state, String incomingNumber) {}
            public void onCellLocationChanged(CellLocation location) {}
            public void onDataActivity(int direction) {}
            public void onDataConnectionStateChanged(int state) {}
            public void onMessageWaitingIndicatorChanged(boolean mwi) {}
            public void onServiceStateChanged(ServiceState serviceState) {}
            //public void onSignalStrengthChanged(int asu) {}
        };
        tm.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |
                        PhoneStateListener.LISTEN_CALL_STATE |
                        PhoneStateListener.LISTEN_CELL_LOCATION |
                        PhoneStateListener.LISTEN_DATA_ACTIVITY |
                        PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                        PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |
                        PhoneStateListener.LISTEN_SERVICE_STATE );
                        //PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
        List<NeighboringCellInfo> n = tm.getNeighboringCellInfo();
        //List<NeighboringCellInfo> n = tm.getAllCellInfo();
        //List<CellInfo>  n =tm.getAllCellInfo();
//Construct the string
        String s = "Cell Id : NA and Signal Power: 0 dBm";
        int rss = 0;
        int cid = 0;
        for (NeighboringCellInfo nci : n)
        {
            cid = nci.getCid();
            rss = -113 + 2*nci.getRssi();
            s += "Cell ID: " + Integer.toString(cid) + "     Signal Power (dBm): " +
                    Integer.toString(rss) + "\n";
        }

        return s;
    }

    public void PingTest(){
        Log.i(" Ping commands","You Clicked to run Ping commands");
        Toast.makeText(MainActivity.this,"Run Ping commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Ping"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    public void MobileCodes(){
        Log.i(" MobileCodes commands","You Clicked to run MobileCodes ");
        Toast.makeText(MainActivity.this,"View MobileCodes ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.MobileCodes"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void KPIDash(){
        Log.i(" KPIDash commands","You Clicked to run KPIDash ");
        Toast.makeText(MainActivity.this,"View KPIDash ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.KPIDash"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void DlUlTPStats(){
        Log.i(" DlUlTP commands","You Clicked to run DlUlTP ");
        Toast.makeText(MainActivity.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.DlUlTp"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void SysInfo(){
        Log.i(" Sysinfo commands","You Clicked to run SysInfo");
        Toast.makeText(MainActivity.this,"Run SysInfo..!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.sysInfo"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }

    public void About(){

        Toast.makeText(MainActivity.this,"About Us  :) ",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.About"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void ShellExeu(){
        Log.i(" Shell comamnds","You Clicked to run shell commands");
        Toast.makeText(MainActivity.this,"Run Shell commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.ShellExecuter"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }

    private void CheckNetworkInfo() {
        Log.i(" Cisco Network on 4G", "You Clicked the Netwrok Button");
        Toast.makeText(MainActivity.this, "Cisco Network Info!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.networkMonitor"));
    }
    private void Logs() {
        Log.i(" Logs Analysis!!", "You Clicked the Logs Button");
        Toast.makeText(MainActivity.this, "Logs Analysis!!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Logs"));
    }

}
