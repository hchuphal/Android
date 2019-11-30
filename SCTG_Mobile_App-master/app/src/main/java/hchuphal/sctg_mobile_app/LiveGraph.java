package hchuphal.sctg_mobile_app;

import android.content.Intent;
import android.app.Fragment;
import android.graphics.Color;
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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.Random;

public class LiveGraph extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Handler mHandler = new Handler();
    //private long BeforeTime = System.currentTimeMillis();
    private float mStartTotalTxBytes = 0;

    private float mStartTotalRxBytes = 0;

    float dlBytes[]={0,0};
    float ulBytes[]={0,0};
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> series2;
    private int lastX1 = 0;
    private int lastX2 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_graph);


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



        Toast.makeText(this, "Run SysInfo..!", Toast.LENGTH_LONG).show();
        // setContentView(R.layout.activity_logs);
        mStartTotalTxBytes = getTotalTxBytes();

        mStartTotalRxBytes = getTotalRxBytes();

        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.GREEN);
        //graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.GREEN);
        series2 = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        graph.addSeries(series2);
        // set second scale
        //graph.getSecondScale().addSeries(series2);
// the y bounds are always manual for second scale
        //graph.getSecondScale().setMinY(0);
        //graph.getSecondScale().setMaxY(100);
        series2.setColor(Color.YELLOW);
        //graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.YELLOW);

        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(80);
        viewport.setScrollable(true);
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





    private final Runnable mRunnable = new Runnable() {

        public void run() {

            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(3);
            float TotalRxBytes,dlRate,ulRate,MdlRate,MulRate;
            float TotalTxBytes,dlgraph,ulgraph;
            TotalRxBytes = TrafficStats.getTotalRxBytes() - mStartTotalRxBytes;
            dlBytes[1]=TotalRxBytes;
            dlRate=dlBytes[1]-dlBytes[0];
            dlgraph=(dlRate/1048576)*8;
            TextView textDeviceUL = (TextView) findViewById(R.id.textGdl);

            textDeviceUL.setText(df.format((dlRate/1048576)*8) + " Mbps");
            dlBytes[0]=dlBytes[1];

            TotalTxBytes = TrafficStats.getTotalTxBytes() - mStartTotalTxBytes;
            ulBytes[1]=TotalTxBytes;
            ulRate=ulBytes[1]-ulBytes[0];
            TextView textDeviceDL = (TextView) findViewById(R.id.textGul);
            //textDeviceDL.setText(((int) TotalTxBeforeTest)/2048 + " Kbps");
            textDeviceDL.setText(df.format((ulRate/1048576)*8) + " Mbps");
            ulgraph=(ulRate/1048576)*8;
            ulBytes[0]=ulBytes[1];
            //series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 10);
            // here, we choose to display max 10 points on the viewport and we scroll to end
            series.appendData(new DataPoint(lastX1++, (int)dlgraph), true, 10);
            series2.appendData(new DataPoint(lastX2++, (int)ulgraph), true, 10);
            //series.appendData(new DataPoint(lastX++, (int)ulRate * 10d ), true, 10);
            // sleep to slow down the add of entries
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                // manage error ...
            }

            /*GraphView graph = (GraphView) findViewById(R.id.graph);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });
            graph.addSeries(series);

*/
            TextView infoViewMintf = (TextView)findViewById(R.id.textTotalData);
            //TextView infoViewAllintf = (TextView)findViewById(R.id.textAllintf);
            String infoMintf = "";
            infoViewMintf.setMovementMethod(null);
            //infoViewAllintf.setMovementMethod(null);

            infoMintf += "Total Data Downloaded and Uploaded : ";
            infoMintf += ("\n Total RX: " + TrafficStats.getTotalRxBytes()/1048576 + " MB  " );
            infoMintf += ("\n Total TX: " + TrafficStats.getTotalTxBytes() /1048576 + " MB " );

            infoViewMintf.setText(infoMintf);


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
            DlUlTPStats();

        }
        else if (id == R.id.nav_graph) {
            //liveGraph();
        }else if (id == R.id.nav_shell) {
            ShellExeu();
        }else if (id == R.id.nav_ping) {
            PingTest();

        }  else if (id == R.id.nav_manage) {
            Logs();

        } else if (id == R.id.nav_share) {
            Toast.makeText(LiveGraph.this,"Send your Queries to hchuphal@cisco.com",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {
            Toast.makeText(LiveGraph.this,"WinsPire Team SCTG App!",Toast.LENGTH_LONG).show();
            About();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Home(){
//Log.i(" App Home", "You Clicked to run DlUlTP ");
        Toast.makeText(LiveGraph.this,"Home",Toast.LENGTH_LONG).show();
        finish();
//System.exit(0);

    }


    public void DlUlTPStats(){
        Log.i(" DlUlTP commands", "You Clicked to run DlUlTP ");
        Toast.makeText(LiveGraph.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.DlUlTp"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void liveGraph(){
        Log.i(" Live Graph commands", "You Clicked to check Live Graph ");
        Toast.makeText(LiveGraph.this,"View DlUlTP ...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.LiveGraph"));

//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void SysInfo(){
        Log.i(" Sysinfo commands", "You Clicked to run SysInfo");
        Toast.makeText(LiveGraph.this,"Run SysInfo..!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.sysInfo"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    public void About(){

        Toast.makeText(LiveGraph.this,"About Us  :) ",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.About"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }
    public void ShellExeu(){
        Log.i(" Shell comamnds", "You Clicked to run shell commands");
        Toast.makeText(LiveGraph.this,"Run Shell commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.ShellExecuter"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

    private void CheckNetworkInfo() {
        Log.i(" Cisco Network on 4G", "You Clicked the Netwrok Button");
        Toast.makeText(LiveGraph.this, "Cisco Network Info!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.networkMonitor"));
    }
    private void Logs() {
        Log.i(" Logs Analysis!!", "You Clicked the Logs Button");
        Toast.makeText(LiveGraph.this, "Logs Analysis!!", Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Logs"));
    }
    public void PingTest(){
        Log.i(" Ping commands","You Clicked to run Ping commands");
        Toast.makeText(LiveGraph.this,"Run Ping commands...!",Toast.LENGTH_LONG).show();
        startActivity(new Intent("hchuphal.sctg_mobile_app.Ping"));
//Intent intent= new Intent("hchuphal.sctg_mobile_app.ShellExecuter");
//startActivity(intent);

    }

}
