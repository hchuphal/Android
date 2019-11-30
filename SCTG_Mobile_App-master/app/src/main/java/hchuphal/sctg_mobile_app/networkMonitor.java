package hchuphal.sctg_mobile_app;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;


public class networkMonitor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Handler mainHandler = new Handler();
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

    Context mcontext;


    public static String LOG_TAG = "CustomPhoneStateListener";
    private static final boolean DBG = false;
    public String InfotoPrint="";
    String SignalInfo="";
    //EditText SignalText = (EditText)findViewById(R.id.editTextSignal);
    EditText cellInfo ;//= (EditText)findViewById(R.id.textCellInfo);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_monitor);


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




        //TextView textDeviceID = (TextView)findViewById(R.id.deviceId);
        TextView textDeviceIP = (TextView)findViewById(R.id.textViewIP);
        //retrieve a reference to an instance of TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        //textDeviceID.setText(getDeviceID(telephonyManager));

        //WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //WifiInfo wifiInf = wifiMan.getConnectionInfo();
        //int ipAddress = wifiInf.getIpAddress();
        //String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        //textDeviceIP.setText(getIpAddress());

        /*TextView textDeviceWIFI = (TextView)findViewById(R.id.textWIFIinfo);
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String mac="";
        mac = wm.getConnectionInfo().getBSSID();

        String info=" Mobile WIFI Info : \n ";
        //info+="\n Phone No : "+phonenumber;
        info+="\n \n MAC : "+mac;


        textDeviceWIFI.setText(info);*/
        mainHandler.postDelayed(mRunnable, 1000);

    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
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
                    Toast.makeText(networkMonitor.this,
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
                    Toast.makeText(networkMonitor.this,
                            "CDMA", Toast.LENGTH_LONG).show();
                }

                if ("460".equals(tm.getSimOperator().substring(0, 3)))
                    Toast.makeText(networkMonitor.this,
                            "460", Toast.LENGTH_LONG).show();
            }
            else{
                operatorcode=mcc=mnc=phonenumber= " NA ";
                lac=psc=cellId = 0;
                //cellId = gsm.getCi();
                radioType = " NO Cell";

            }

            //EditText cellInfo = (EditText)findViewById(R.id.textCellInfo);
            cellInfo = (EditText)findViewById(R.id.editTextSignal);
            cellInfo.setText("");
            cellInfo.setScroller(new Scroller(networkMonitor.this));
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
                            | PhoneStateListener.LISTEN_CELL_INFO // Requires API 17
                            | PhoneStateListener.LISTEN_CELL_LOCATION
                            | PhoneStateListener.LISTEN_DATA_ACTIVITY
                            | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                            | PhoneStateListener.LISTEN_SERVICE_STATE
                            | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                            | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                            | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);
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



            mainHandler.postDelayed(mRunnable, 1000);
}
};

    private String getIpAddress(){
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces =
                    NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface =
                        enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress =
                        networkInterface.getInetAddresses();
                while(enumInetAddress.hasMoreElements()){
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    String ipAddress = "";
                    if(inetAddress.isLoopbackAddress()){
                        ipAddress = "LoopbackAddress: ";
                    }else if(inetAddress.isSiteLocalAddress()){
                        ipAddress = "SiteLocalAddress: ";
                    }else if(inetAddress.isLinkLocalAddress()){
                        ipAddress = "LinkLocalAddress: ";
                    }else if(inetAddress.isMulticastAddress()){
                        ipAddress = "MulticastAddress: ";
                    }
                    ip += ipAddress + inetAddress.getHostAddress() + "\n";
                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
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
                return "GSM: IMEI=" + id;

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA: MEID/ESN=" + id;

 /*
  *  for API Level 11 or above
  *  case TelephonyManager.PHONE_TYPE_SIP:
  *   return "SIP";
  */

            default:
                return "UNKNOWN: ID=" + id;
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
            //CheckNetworkInfo();
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
            PingTest();

        }  else if (id == R.id.nav_manage) {
            Logs();

        } else if (id == R.id.nav_share) {
            Toast.makeText(networkMonitor.this,"Send your Queries to hchuphal@cisco.com",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {
            Toast.makeText(networkMonitor.this,"WinsPire Team SCTG App!",Toast.LENGTH_LONG).show();
            About();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Home(){
//Log.i(" App Home", "You Clicked to run DlUlTP ");
        Toast.makeText(networkMonitor.this,"Home",Toast.LENGTH_LONG).show();
        finish();
//System.exit(0);

    }


    public void DlUlTPStats(){
        Log.i(" DlUlTP commands", "You Clicked to run DlUlTP ");
        Toast.makeText(networkMonitor.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.DlUlTp"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void liveGraph(){
        Log.i(" Live Graph commands", "You Clicked to check Live Graph ");
        Toast.makeText(networkMonitor.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.LiveGraph"));

//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void SysInfo(){
        Log.i(" Sysinfo commands", "You Clicked to run SysInfo");
        Toast.makeText(networkMonitor.this, "Run SysInfo..!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.sysInfo"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    public void About(){

        Toast.makeText(networkMonitor.this,"About Us  :) ",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.About"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void ShellExeu(){
        Log.i(" Shell comamnds", "You Clicked to run shell commands");
        Toast.makeText(networkMonitor.this,"Run Shell commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.ShellExecuter"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    private void CheckNetworkInfo() {
        Log.i(" Cisco Network on 4G", "You Clicked the Netwrok Button");
        Toast.makeText(networkMonitor.this, "Cisco Network Info!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.networkMonitor"));
    }
    private void Logs() {
        Log.i(" Logs Analysis!!", "You Clicked the Logs Button");
        Toast.makeText(networkMonitor.this, "Logs Analysis!!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Logs"));
    }
    public void PingTest(){
        Log.i(" Ping commands","You Clicked to run Ping commands");
        Toast.makeText(networkMonitor.this,"Run Ping commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Ping"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

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

            SignalText.setScroller(new Scroller(networkMonitor.this));

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

