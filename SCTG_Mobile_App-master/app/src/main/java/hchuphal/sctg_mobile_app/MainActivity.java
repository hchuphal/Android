package hchuphal.sctg_mobile_app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import it.sauronsoftware.ftp4j.FTPClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context mcontext;


    public static String LOG_TAG = "CustomPhoneStateListener";
    private static final boolean DBG = false;
    public String InfotoPrint="";
    String SignalInfo="";
    //EditText SignalText = (EditText)findViewById(R.id.editTextSignal);
    EditText cellInfo ;//= (EditText)findViewById(R.id.textCellInfo);


    Boolean ConnectionStatus;
    private CheckBox sctglog;
    private Handler mainHandler = new Handler();
    //private TelephonyManager manager;
    private PhoneStateListener listener;
    private GsmCellLocation gsm;
    private CellIdentityLte lte;
    private CdmaCellLocation cdma;
    int lac,psc,cellId;
    String mcc, mnc,radioType;
    int lteasu,lteta,lteci,ltepci,ltemnc,ltemcc,ltetac;
    public String mSignalStrength,mRsrp,mRsrq,mRssnr,mCqi;
    boolean mCallFwd,mMsgWait;
    public String mState,mDataState, mCallState;
    int dbm=0;
    public String operatorcode;

    //TP Start
    private float mStartTotalTxBytes = 0;
    private float mStartTotalRxBytes = 0;
    float dlBytes[]={0,0};
    float ulBytes[]={0,0};
    //TP stop

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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
        cellInfo = (EditText)findViewById(R.id.editTextSignal);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        //TP
        mStartTotalTxBytes = getTotalTxBytes();
        mStartTotalRxBytes = getTotalRxBytes();
        sctglog = (CheckBox) findViewById(R.id.checkBoxLog);
        if (!sctglog.isChecked()) {
            Toast.makeText(MainActivity.this,
                    "Logging Not Enabled", Toast.LENGTH_LONG).show();
        }
        sctglog.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info==null || !info.isConnected()) {
            showAlertDialog(MainActivity.this, "No Internet Connection",
                    "You don't have Any Active connections.", false);

        }else if(info.getType() == ConnectivityManager.TYPE_WIFI) {
            showAlertDialog(MainActivity.this, "Internet Connection",
                    "You have an active wifi connection", true);
        }else if(info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {
                showAlertDialog(MainActivity.this, "Mobile Connection",
                        "You have an active LTE connection", true);

            }
        }
        /*As you can see in the above class, a custom Android PhoneStateListener is used to capture
        the updates of signal strengths with the help of TelephonyManager class. as soon as the listener
         is registered, we start receiving callbacks in Android PhoneStateListener onSignalStrengthsChanged method.
         */
        //TelephonyManager tManager;
        //tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //tManager.listen(new CustomPhoneStateListener1(this),
                //PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        mainHandler.postDelayed(mRunnable, 1000);

    }
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
// only for gingerbread and newer versions
                String permission = Manifest.permission.ACCESS_COARSE_LOCATION;
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
// Add your function here which open camera
                }
            } else {
// Add your function here which open camera
            }
            TextView textDeviceIP1 = (TextView) findViewById(R.id.textIPv4);
            //textDeviceIP1.setText(getMobileIP());
            String ipaddress = getMobileIP();
            if ( ipaddress!=null)
                textDeviceIP1.setText(getMobileIP());
            else {
                TextView textDeviceIP2 = (TextView) findViewById(R.id.textIPv4);
                textDeviceIP1.setText(getWifiIP());
            }



            /*TextView textDeviceIP4 = (TextView) findViewById(R.id.textConnStatus);

            ConnectionStatus = isConnectedFast(MainActivity.this);
            if (ConnectionStatus) {
                textDeviceIP4.setTextColor(Color.parseColor("#1d6d05"));
                textDeviceIP4.setText("Active");

            } else {
                textDeviceIP4.setTextColor(Color.parseColor("red"));
                textDeviceIP4.setText("No Active Connections");

            }*/
            TextView textDeviceIP5 = (TextView) findViewById(R.id.textConnType);
            textDeviceIP5.setText(getNetworkClass(MainActivity.this));
            TextView textDeviceIP6 = (TextView) findViewById(R.id.textInternet);
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                //new DownloadWebpageTask().execute(stringUrl);
                textDeviceIP6.setTextColor(Color.parseColor("#1d6d05"));
                textDeviceIP6.setText("Active Internet Available");
            } else {
                textDeviceIP6.setTextColor(Color.parseColor("red"));
                textDeviceIP6.setText("No Internet available.");
            }
            TextView textDeviceIP7 = (TextView) findViewById(R.id.textMdataStatus);
            if (getConnectivityStatusString(MainActivity.this)) {
                textDeviceIP7.setTextColor(Color.parseColor("#1d6d05"));
                textDeviceIP7.setText(" Enabled ");
            }else{
                textDeviceIP7.setTextColor(Color.parseColor("red"));
                textDeviceIP7.setText(" Disabled ");

            }


            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
           List<CellInfo> cellInfoList = tm.getAllCellInfo();
            //String lteasu,letta,lteci,ltepci,ltemnc,ltemcc,ltetac;
            if (cellInfoList != null){
            for (CellInfo info : cellInfoList) {
                if (info instanceof CellInfoLte) {
                    CellIdentityLte identity = ((CellInfoLte) info).getCellIdentity();
                    if (identity.getMcc() == Integer.MAX_VALUE) return;
                    CellSignalStrengthLte strength = ((CellInfoLte) info).getCellSignalStrength();
                    lteasu=strength.getAsuLevel();
                    Log.i(LOG_TAG, "lte ASU : " + strength.getAsuLevel());

                    lteta=strength.getTimingAdvance();
                    Log.i(LOG_TAG, "lte TA : " + strength.getTimingAdvance());

                    //Log.i(LOG_TAG, "lte1 : " + strength.getLevel());
                    //Log.i(LOG_TAG, "Full  : " + strength.toString());
                    Log.i(LOG_TAG, "lte1 : " + identity.getMnc());
                    Log.i(LOG_TAG, "lte2 : " + identity.getMcc());
                    Log.i(LOG_TAG, "lte3 : " + identity.getTac());
                    Log.i(LOG_TAG, "lte4 : " + identity.getCi());
                    Log.i(LOG_TAG, "lte4 : " + identity.getPci());
                    Log.i(LOG_TAG, "lte4 : " + identity.toString());

                }
            }
            }



            //TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            //listener = new PhoneStateListener();
            //tm.listen(listener, 0);
            int type = tm.getNetworkType();
            String SIMSerialNumber=tm.getSimSerialNumber();
            String networkCountryISO=tm.getNetworkCountryIso();
            String SIMCountryISO=tm.getSimCountryIso();
            String voiceMailNumber=tm.getVoiceMailNumber();
            String operatorname = tm.getNetworkOperatorName();
            operatorcode = tm.getNetworkOperator();
            String simoperator = tm.getSimOperatorName();
            String phonenumber = tm.getLine1Number();

            if (type == TelephonyManager.NETWORK_TYPE_GPRS || type ==TelephonyManager.NETWORK_TYPE_EDGE
                    || type ==TelephonyManager.NETWORK_TYPE_UMTS ||type ==TelephonyManager.NETWORK_TYPE_EVDO_0
                    || type ==TelephonyManager.NETWORK_TYPE_EVDO_A || type ==TelephonyManager.NETWORK_TYPE_HSDPA
                    || type ==TelephonyManager.NETWORK_TYPE_EVDO_B ||type ==TelephonyManager.NETWORK_TYPE_EHRPD
                    ||type ==TelephonyManager.NETWORK_TYPE_HSPAP ||type ==TelephonyManager.NETWORK_TYPE_LTE)
            {
                gsm = ((GsmCellLocation) tm.getCellLocation());

                if (gsm == null)
                {
                    Toast.makeText(MainActivity.this,
                            "NO GSM", Toast.LENGTH_LONG).show();
                }
                lac  = gsm.getLac();
                psc = gsm.getPsc();
                mcc = tm.getNetworkOperator().substring(0, 3);
                mnc = tm.getNetworkOperator().substring(3, 5);
                cellId = gsm.getCid();
                //cellId = gsm.getCi();
                radioType = "GSM";
            }
            else if (type == TelephonyManager.NETWORK_TYPE_CDMA || type ==TelephonyManager.NETWORK_TYPE_1xRTT) {
                cdma = ((CdmaCellLocation) tm.getCellLocation());
                if (cdma == null)
                {
                    Toast.makeText(MainActivity.this,
                            "CDMA", Toast.LENGTH_LONG).show();
                }

                if ("460".equals(tm.getSimOperator().substring(0, 3)))
                    Toast.makeText(MainActivity.this,
                            "460", Toast.LENGTH_LONG).show();
            }
            else{
                operatorcode=mcc=mnc=phonenumber= " NA ";
                lac=psc=cellId = 0;
                //cellId = gsm.getCi();
                radioType = " NO Cell";

            }

            //EditText cellInfo = (EditText)findViewById(R.id.textCellInfo);
            cellInfo.setText("");
            cellInfo.setScroller(new Scroller(MainActivity.this));
            //input.setMaxLines(1);
            cellInfo.setVerticalScrollBarEnabled(true);
            cellInfo.setMovementMethod(new ScrollingMovementMethod());



            //Get the phone type
            String strphoneType="";

            int phoneType=tm.getPhoneType();

            switch (phoneType)
            {
                case (TelephonyManager.PHONE_TYPE_CDMA):
                    strphoneType=" CDMA";
                    break;
                case (TelephonyManager.PHONE_TYPE_GSM):
                    strphoneType=" GSM";
                    break;
                case (TelephonyManager.PHONE_TYPE_SIP):
                    strphoneType=" via SIP";
                    break;
                case (TelephonyManager.PHONE_TYPE_NONE):
                    strphoneType="No phone radio.";
                    break;
            }
            TelephonyManager tManager;
            tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);



            tManager.listen(new CustomPhoneStateListener1(mcontext),
                    PhoneStateListener.LISTEN_CALL_STATE
                            | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

            //CustomPhoneStateListener mApp = ((CustomPhoneStateListener)getApplicationContext());
            //String globalVarValue = mApp.getSomeVariable();

            //getting information if phone is in roaming
            boolean isRoaming=tm.isNetworkRoaming();

            //info=" Mobile Cell Info : ";
            //info+="\n Phone No : "+phonenumber;
            //info+="\n Network Radio Type : "+strphoneType;
            InfotoPrint=" GSM Cell ID : "+cellId;
            //info+="\n Singal Strength : "+CellInfoAll;
            InfotoPrint+="\n TAC : "+operatorcode;
            InfotoPrint+=" ,LAC :"+lac;
            InfotoPrint+=" ,PSC:"+psc;
            InfotoPrint+="\n Operater : "+operatorname;
            InfotoPrint+="\n Phone No : "+phonenumber;
            InfotoPrint+="\n MNC : "+mnc;
            InfotoPrint+="   & MCC : "+mcc;
            InfotoPrint+="\n Signal Strength (dBm): "+dbm;
            //info+="\n Sim Serial : "+SIMSerialNumber;
            InfotoPrint+="\n Country: "+networkCountryISO;
            //info+="\n SIM ISO: "+SIMCountryISO;
            //info+="\n Voice Mail No. : "+voiceMailNumber;
            InfotoPrint+="   In Roaming? :  "+isRoaming;
            cellInfo.setText( InfotoPrint);

            //cellInfo.setText(info);
            //SignalText.setText(CellInfoAll);
            //lac=cellId=psc=0;
            //mnc=mcc=null;
            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(3);
            float TotalRxBytes,dlRate,ulRate,MdlRate,MulRate;
            float TotalTxBytes;
            TotalRxBytes = TrafficStats.getTotalRxBytes() - mStartTotalRxBytes;
            dlBytes[1]=TotalRxBytes;
            dlRate=dlBytes[1]-dlBytes[0];
            TextView textDeviceUL = (TextView) findViewById(R.id.textDLTP);
            // textDeviceUL.setText(((int) TotalRxBeforeTest)/2048+ " Kbps");
            if( ((dlRate/1048576)*8) >= 1.00) {
                textDeviceUL.setText(df.format((dlRate / 1048576) * 8) + " Mbps");
            }else{
                textDeviceUL.setText(df.format((dlRate / 1024) * 8) + " Kbps");
            }
            dlBytes[0]=dlBytes[1];

            TotalTxBytes = TrafficStats.getTotalTxBytes() - mStartTotalTxBytes;
            ulBytes[1]=TotalTxBytes;
            ulRate=ulBytes[1]-ulBytes[0];
            TextView textDeviceDL = (TextView) findViewById(R.id.textULTP);
            //textDeviceDL.setText(((int) TotalTxBeforeTest)/2048 + " Kbps");
            if( ((ulRate/1048576)*8) >= 1.00) {
                textDeviceDL.setText(df.format((ulRate / 1048576) * 8) + " Mbps");
            }else{
                textDeviceDL.setText(df.format((ulRate / 1024) * 8) + " Kbps");
            }
            ulBytes[0]=ulBytes[1];



            mainHandler.postDelayed(mRunnable, 1000);
        }
    };

    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.sys : R.drawable.tp);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

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

    public class myCheckBoxChnageClicker implements CheckBox.OnCheckedChangeListener {
        /*********
         * work only for Dedicated IP
         ***********/
        static final String FTP_HOST = "10.142.112.115";

        /*********
         * FTP USERNAME
         ***********/
        static final String FTP_USER = "trup";

        /*********
         * FTP PASSWORD
         ***********/
        static final String FTP_PASS = "trup";

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub;

            Toast.makeText(MainActivity.this,
                    "Logging Now Enabled....", Toast.LENGTH_LONG).show();
            String LogTimeStamp = "";
            String MobileId = "";
            TelephonyManager phonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String id = phonyManager.getDeviceId();
            if (id == null) {
                id = "not available";
            }

            int phoneType = phonyManager.getPhoneType();
            switch (phoneType) {
                case TelephonyManager.PHONE_TYPE_NONE:
                    MobileId = "NONE: " + id;

                case TelephonyManager.PHONE_TYPE_GSM:
                    MobileId = "GSM_IMEI " + id;

                case TelephonyManager.PHONE_TYPE_CDMA:
                    MobileId = "CDMA_ESN " + id;
                default:
                    MobileId = "UNKNOWN_ID=" + id;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            String format = simpleDateFormat.format(new Date());
            LogTimeStamp = format;
            String fileName = "/sdcard/SCTG_LOG_" + LogTimeStamp + "__" + MobileId + ".txt";
            if (isChecked) {
                try {

                    File myFile = new File(fileName);
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(LogTimeStamp);
                    myOutWriter.close();
                    fOut.close();

                    //********** Pick file from sdcard *******/
                    File fload = new File(fileName);
                    this.uploadFile(fload);



                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,
                            "Data Not enabled", Toast.LENGTH_LONG).show();

                }


            }

        }

        public void uploadFile(File fileName) {


            FTPClient client = new FTPClient();

            try {

                client.connect(FTP_HOST, 21);
                client.login(FTP_USER, FTP_PASS);
                client.setType(FTPClient.TYPE_BINARY);
                client.setPassive(true);
                client.noop();
                //client.changeDirectory("/upload/");
                Toast.makeText(MainActivity.this," Login Success ...",Toast.LENGTH_LONG).show();

                //client.upload(fileName, new MyTransferListener());
                client.upload(fileName);

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
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
        else if (id == R.id.nav_home) {

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
            Toast.makeText(MainActivity.this,"Send your Queries to hchuphal@cisco.com",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {
            Toast.makeText(MainActivity.this,"WinsPire Team SCTG App!",Toast.LENGTH_LONG).show();
            About();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public void DlUlTPStats(){
        Log.i(" DlUlTP commands", "You Clicked to run DlUlTP ");
        Toast.makeText(MainActivity.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.DlUlTp"));
        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void liveGraph(){
        Log.i(" Live Graph commands", "You Clicked to check Live Graph ");
        Toast.makeText(MainActivity.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.LiveGraph"));

        //Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
        //startActivity(intent);

    }
    public void SysInfo(){
        Log.i(" Sysinfo commands", "You Clicked to run SysInfo");
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
        Log.i(" Shell comamnds", "You Clicked to run shell commands");
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
                    return "Active 4G LTE";
                default:
                    return "?";
            }
        }
        return "?";
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


    public float getTotalRxBytes() {
        return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes());
    }

    public float getTotalTxBytes() {
        return TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes());
    }

private class CustomPhoneStateListener1 extends PhoneStateListener {
    Context mContext;
    private String LOG_TAG = "CustomPhoneStateListener";

    boolean flag=false;
    //public String CellInfoAll;

   // private int mTimingAdvance;
    //public int signalStrengthValue;
    EditText SignalText = (EditText)findViewById(R.id.editTextSignal);

    public CustomPhoneStateListener1(Context context) {
        InfotoPrint+="\n \n ****** LTE CELL Info ****** ";
        if(lteci==0) {
            InfotoPrint += "\n LTE PCI : " + "Not Supported";
        }else{
            InfotoPrint += "\n LTE PCI : " + ltepci;

        }
        if(lteci==0){
            InfotoPrint += "\n LTE Cell ID : " + cellId;
        }else {
            InfotoPrint += "\n LTE Cell ID : " + lteci;
        }
        InfotoPrint+="\n RSRP : " +mRsrp;
        InfotoPrint+=" (dBm) & ASU : " +lteasu;
        InfotoPrint+="\n RSRQ : " +mRsrq;
        //InfotoPrint+="  ,RS SNR : " +mRssnr;
        if(ltetac==0) {
            InfotoPrint += "\n TAC : " + operatorcode;
        }else{
            InfotoPrint += "\n TAC : " + ltetac;
        }
        if(ltemcc==0 && ltemnc==0) {
            InfotoPrint += "\n MCC : " + mcc;
            InfotoPrint += " & MNC : " + mnc;
        } else{
            InfotoPrint += "\n MCC : " + ltemcc;
            InfotoPrint += " & MNC : " + ltemnc;

        }
        InfotoPrint+=" \n \n ****** Mobile STATES ****** ";
        InfotoPrint+="\n Service State : " +mState;
        InfotoPrint+="\n Call State : " +mCallState;
        InfotoPrint+="\n Data State : " +mDataState;
        InfotoPrint+="\n Call Forward ? : " +mCallFwd;
        InfotoPrint+="\n Message Waiting ? : " +mMsgWait;
        //InfotoPrint+=" & MNC : " +ltemnc;



        //InfotoPrint+=" &  MCC : " +ltemcc;


        cellInfo.setText( InfotoPrint);
        mContext = context;

        SignalText.setScroller(new Scroller(MainActivity.this));

        //input.setMaxLines(1);
        SignalText.setVerticalScrollBarEnabled(true);
        SignalText.setMovementMethod(new ScrollingMovementMethod());
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList = tm.getAllCellInfo();
        //String lteasu,letta,lteci,ltepci,ltemnc,ltemcc,ltetac;
        if (cellInfoList != null){
            for (CellInfo info : cellInfoList) {
                if (info instanceof CellInfoLte) {
                    CellIdentityLte identity = ((CellInfoLte) info).getCellIdentity();
                    if (identity.getMcc() == Integer.MAX_VALUE) return;
                    CellSignalStrengthLte strength = ((CellInfoLte) info).getCellSignalStrength();
                    lteasu=strength.getAsuLevel();
                    Log.i(LOG_TAG, "lte ASU : " + strength.getAsuLevel());

                    lteta=strength.getTimingAdvance();
                    Log.i(LOG_TAG, "lte TA : " + strength.getTimingAdvance());

                    //Log.i(LOG_TAG, "lte1 : " + strength.getLevel());
                    //Log.i(LOG_TAG, "Full  : " + strength.toString());
                    ltemnc=identity.getMnc();

                    Log.i(LOG_TAG, "lte MNC : " + identity.getMnc());
                    ltemcc=identity.getMcc();
                    Log.i(LOG_TAG, "lte MCC : " + identity.getMcc());
                    ltetac=identity.getTac();
                    Log.i(LOG_TAG, "lte TAC : " + identity.getTac());
                    lteci=identity.getCi();
                    Log.i(LOG_TAG, "lte CEllID : " + identity.getCi());
                    ltepci=identity.getPci();
                    Log.i(LOG_TAG, "lte PCI : " + identity.getPci());
                    Log.i(LOG_TAG, "lte String : " + identity.toString());

                }
            }
        }
        else{
            lteasu=lteta=lteci=ltepci=ltemnc=ltemcc=ltetac=0;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCellInfoChanged(List<CellInfo> Cellinfo) {
        super.onCellInfoChanged(Cellinfo);


        //cellInfo.append("\n" + "Himanshu");
        Log.i(LOG_TAG, "onCellInfoChanged: " + Cellinfo);
        if (Cellinfo == null) return;

    }

    @Override
    public void onDataActivity(int direction) {
        super.onDataActivity(direction);
        switch (direction) {
            case TelephonyManager.DATA_ACTIVITY_NONE:
                mDataState=" NONE";
                //Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_NONE");
                break;
            case TelephonyManager.DATA_ACTIVITY_IN:
                mDataState=" IN";
                //Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_IN");
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                mDataState=" OUT";
                //Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_OUT");
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                mDataState=" IN & OUT";
                //Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_INOUT");
                break;
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                mDataState=" DORMANT ";
                //Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_DORMANT");
                break;
            default:
                Log.w(LOG_TAG, "onDataActivity: UNKNOWN " + direction);
                mDataState=" UNKNOWN " + direction;
                break;
        }
    }
    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        /*Log.i(LOG_TAG, "onServiceStateChanged: " + serviceState.toString());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorAlphaLong "
                + serviceState.getOperatorAlphaLong());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorAlphaShort "
                + serviceState.getOperatorAlphaShort());
        Log.i(LOG_TAG, "onServiceStateChanged: getOperatorNumeric "
                + serviceState.getOperatorNumeric());
        Log.i(LOG_TAG, "onServiceStateChanged: getIsManualSelection "
                + serviceState.getIsManualSelection());
        Log.i(LOG_TAG,
                "onServiceStateChanged: getRoaming "
                        + serviceState.getRoaming());
    */
        switch (serviceState.getState()) {
            case ServiceState.STATE_IN_SERVICE:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_IN_SERVICE");
                mState="IN SERVICE";
                break;
            case ServiceState.STATE_OUT_OF_SERVICE:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_OUT_OF_SERVICE");
                mState="OUT OF SERVICE";
                break;
            case ServiceState.STATE_EMERGENCY_ONLY:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_EMERGENCY_ONLY");
                mState="EMERGENCY ONLY";
                break;
            case ServiceState.STATE_POWER_OFF:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_POWER_OFF");
                mState="POWER OFF";
                break;
        }
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_IDLE");
                mCallState="IDLE";
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_RINGING");
                mCallState="RINGING";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_OFFHOOK");
                mCallState="OFF Hook";
                break;
            default:
                Log.i(LOG_TAG, "UNKNOWN_STATE: " + state);
                mCallState="UNKNOWN STATE";

                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
        if (location instanceof GsmCellLocation) {
            GsmCellLocation gcLoc = (GsmCellLocation) location;
            Log.i(LOG_TAG,
                    "onCellLocationChanged: GsmCellLocation "
                            + gcLoc.toString());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getCid "
                    + gcLoc.getCid());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getLac "
                    + gcLoc.getLac());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation getPsc"
                    + gcLoc.getPsc()); // Requires min API 9
        }
        else if (location instanceof CdmaCellLocation) {
            CdmaCellLocation ccLoc = (CdmaCellLocation) location;
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation "
                            + ccLoc.toString());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getBaseStationId "
                            + ccLoc.getBaseStationId());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getBaseStationLatitude "
                            + ccLoc.getBaseStationLatitude());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getBaseStationLongitude"
                            + ccLoc.getBaseStationLongitude());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getNetworkId "
                            + ccLoc.getNetworkId());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getSystemId "
                            + ccLoc.getSystemId());
        } else {
            Log.i(LOG_TAG, "onCellLocationChanged: " + location.toString());
        }
    }

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        super.onCallForwardingIndicatorChanged(cfi);
        mCallFwd=cfi;
        //Log.i(LOG_TAG, "onCallForwardingIndicatorChanged: " + cfi);
    }

    @Override
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        super.onMessageWaitingIndicatorChanged(mwi);
        mMsgWait=mwi;
        //Log.i(LOG_TAG, "onMessageWaitingIndicatorChanged: " + mwi);
    }


    //***

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        //TextView SignalText = (TextView) findViewById(R.id.textSignal);
        //Log.i(LOG_TAG, "onSignalStrengthsChanged: " + signalStrength);

        String ssignal = signalStrength.toString();

        String[] parts = ssignal.split(" ");
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //int dbm = 0;

        if ( tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){

            dbm = Integer.parseInt(parts[8])*2-113;

        }
        else{

            if (signalStrength.getGsmSignalStrength() != 99) {
                int intdbm = -113 + 2
                        * signalStrength.getGsmSignalStrength();
                //dbm = Integer.toString(intdbm);
                dbm = intdbm;

            }

        }
        //SignalInfo +="\n Signal Strength:" +dbm +"dbm";



        if (signalStrength.isGsm()) {
            //Log.i(LOG_TAG, "onSignalStrengthsChanged: getGsmBitErrorRate "
                   // + signalStrength.getGsmBitErrorRate());
            //this.SignalInfo += "\n Gsm BitError Rate : " + signalStrength.getGsmBitErrorRate();
            //Log.i(LOG_TAG, "onSignalStrengthsChanged: getGsmSignalStrength "
                   // + signalStrength.getGsmSignalStrength());
            //this.SignalInfo += "\n Gsm Signal Strength : " + signalStrength.getGsmSignalStrength();
        } else if (signalStrength.getCdmaDbm() > 0) {
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getCdmaDbm "
                    + signalStrength.getCdmaDbm());
            SignalInfo += "\n Cdma Dbm : " + signalStrength.getCdmaDbm();
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getCdmaEcio "
                    + signalStrength.getCdmaEcio());
            SignalInfo += "\n Cdma Ecio : " + signalStrength.getCdmaEcio();
        } else {
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoDbm "
                    + signalStrength.getEvdoDbm());
            SignalInfo += "\n Evdo Dbm : " + signalStrength.getEvdoDbm();
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoEcio "
                    + signalStrength.getEvdoEcio());
            SignalInfo += "\n Evdo Ecio : " + signalStrength.getEvdoEcio();
            Log.i(LOG_TAG, "onSignalStrengthsChanged: getEvdoSnr "
                    + signalStrength.getEvdoSnr());
            SignalInfo += "\n Evdo Snr : " + signalStrength.getEvdoSnr();
        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        //SignalInfo+="\n\n LTE Signal ";

        if(info==null || !info.isConnected()
                || info.getType() == ConnectivityManager.TYPE_WIFI){
            mSignalStrength=mRsrp=mRsrq=mRssnr=mCqi=" NA";
            //mSignalStrength += "\n Signal Strength : " + "NA";
            //SignalInfo += "\n RSRP ( dBm ) : " + "NA";
            //SignalInfo += "\n RSRQ : " + "NA";
            //SignalInfo += " RS SNR : " + "NA";
            //SignalInfo += "\n LteCqi  :" + "NA";

            //cellInfo.append(SignalInfo);
            //SignalText.setText(SignalInfo);


        }
        else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {

                try {
                    Method[] methods = android.telephony.SignalStrength.class
                            .getMethods();

                    for (Method mthd : methods) {
                        if (mthd.getName().equals("getLteSignalStrength")) {
                            //SignalInfo += "\n LTE Signal Strength  " + mthd.invoke(signalStrength);
                            // SignalInfo += " " + mthd.getName() + " " + mthd.invoke(signalStrength);
                            // Log.i(LOG_TAG,
                            //   "Log SS: " + SignalInfo);
                            flag = true;
                        }
                        if (mthd.getName().equals("getLteRsrp")) {
                            //SignalInfo += "\n RSRP ( dBm ) : " + mthd.invoke(signalStrength);
                            mRsrp=""+mthd.invoke(signalStrength);
                            flag = true;
                        }
                        if (mthd.getName().equals("getLteRsrq")) {
                            //SignalInfo += "\n RSRQ ( dBm ) : " + mthd.invoke(signalStrength);
                            mRsrq=""+mthd.invoke(signalStrength);
                            flag = true;
                        }
                        if (mthd.getName().equals("getLteRssnr")) {
                            //SignalInfo += "\n RS SNR   :   " + mthd.invoke(signalStrength);
                            mRssnr=""+mthd.invoke(signalStrength);
                            flag = true;
                        }
                        if (mthd.getName().equals("getLteCqi")) {
                            //SignalInfo += " " + mthd.getName() + " " + mthd.invoke(signalStrength);
                           // SignalInfo += "\n LteCqi    : " + mthd.invoke(signalStrength);
                            flag = true;

                        }
                        if ((mthd.getName().equals("getLteSignalStrength"))
                                && (mthd.getName().equals("getLteRsrp"))
                                && (mthd.getName().equals("getLteRsrq"))
                                && (mthd.getName().equals("getLteRssnr"))
                                && (mthd.getName().equals("getLteCqi"))) {
                            Log.i(LOG_TAG,
                                    "onSignalStrengthsChanged: " + mthd.getName() + " "
                                            + mthd.invoke(signalStrength));

                        }
                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (!flag) {
                    mSignalStrength=mRsrp=mRsrq=mRssnr=mCqi=" NA";
                   // SignalInfo += "\n\n Signal Strength : " + "NA";
                    //SignalInfo += "\n RSRP ( dBm ) : " + "NA";
                    //SignalInfo += "\n RSRQ : " + "NA";
                    //SignalInfo += " RS SNR : " + "NA";
                    //SignalInfo += "\n CQI (LTE) :" + "NA";

                    //SignalText.setText(SignalInfo);

                }
                //SignalText.setText(SignalInfo);

            }
        }
        }

        }


    }




