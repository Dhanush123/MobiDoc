package com.x10host.dhanushpatel.mobidoc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class LinkActivity extends AppCompatActivity {

    String link;
    WebView webView;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

//        Intent i = getIntent();
//        String typey = i.getStringExtra("typeD");
        String typey = prefs.getString("top", "Cataract");
        switch (typey){
            case "Cataract":
                link = "http://www.webmd.com/eye-health/cataracts/default.htm";
                break;
            case "Red Eyes":
                link = "http://www.webmd.com/eye-health/why-eyes-red";
                break;
            case "Vitiligo":
                link = "http://www.webmd.com/skin-problems-and-treatments/vitiligo-11060";
                break;
            case "Corn":
                link = "http://www.webmd.com/skin-problems-and-treatments/tc/calluses-and-corns-topic-overview";
                break;
        }

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Android");
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.loadUrl(link);
        // Vitiligo: http://www.webmd.com/skin-problems-and-treatments/vitiligo-11060
        // Cartaract: http://www.webmd.com/eye-health/cataracts/default.htm
        // Corn: http://www.webmd.com/skin-problems-and-treatments/tc/calluses-and-corns-topic-overview
        // Red eye: http://www.webmd.com/eye-health/why-eyes-red

    }
}
