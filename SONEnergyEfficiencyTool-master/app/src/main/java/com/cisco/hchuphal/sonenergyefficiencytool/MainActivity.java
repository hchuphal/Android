package com.cisco.hchuphal.sonenergyefficiencytool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ShareActionProvider;


public class MainActivity extends Activity {

    private WebView mWebView;
    String SonServer="";
    String g="10.142.112.57";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        g=MainPage.blueEyeUrl;
        SonServer="http://"+g+":8000/blueEye/#";
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        //mWebView.getSettings().setPluginsEnabled(true);
        //mWebView.getSettings().setSupportMultipleWindows (false);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        //mWebView.loadUrl("http://10.142.112.57:8000/blueEye/#");
        mWebView.loadUrl(SonServer);
        mWebView.loadUrl(SonServer);

                mWebView.setWebViewClient(new com.cisco.hchuphal.sonenergyefficiencytool.MyAppWebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        // TODO Auto-generated method stub
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // TODO Auto-generated method stub

                        view.loadUrl(url);
                        return true;

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // TODO Auto-generated method stub
                        super.onPageFinished(view, url);
                        //hide loading image
                        findViewById(R.id.progressBar1).setVisibility(View.GONE);
                        //show webview
                        findViewById(R.id.activity_main_webview).setVisibility(View.VISIBLE);
                        //view.loadUrl(SonServer);
                        //return true;

                    }
                });


    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private ShareActionProvider mShareActionProvider;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /** Inflating the current activity's menu with res/menu/items.xml */
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /** Getting the actionprovider associated with the menu item whose id is share */
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();

        /** Setting a share intent */
        mShareActionProvider.setShareIntent(getDefaultShareIntent());

        return super.onCreateOptionsMenu(menu);

    }

    /** Returns a share intent */
    private Intent getDefaultShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "SON procedure to improve energy efficiency in BS/NodeB/eNodeB ");
        intent.putExtra(Intent.EXTRA_TEXT," http://10.142.112.57:8000/blueEye/#");
        return intent;
    }


}