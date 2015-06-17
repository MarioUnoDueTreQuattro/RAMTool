package com.thepriest.andrea.ramtool;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;


public class Log_Activity extends ActionBarActivity {
    Button buttonClear, buttonUpdate;
    WebView editTextLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_);
        editTextLog = (WebView) findViewById(R.id.editTextLog);
        //editTextLog.setKeyListener(null);
        buttonClear = (Button) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clearLog();
            }
        });
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateLog();
            }
        });
        editTextLog.loadData(RAMToolApp.mLogHelper.getLogText(), "text/html", "utf-8");
    }

    private void updateLog() {
        editTextLog.loadUrl("about:blank");
        editTextLog.loadData(RAMToolApp.mLogHelper.getLogText(), "text/html", "utf-8");
    }

    private void clearLog() {
        //editTextLog.loadUrl("about:blank");
        //editTextLog.loadData(getCSSStyle(), "text/html", "utf-8");
        RAMToolApp.mLogHelper.clearLog();
        editTextLog.loadData(RAMToolApp.mLogHelper.getLogText(), "text/html", "utf-8");
    }

/*
    private String getCSSStyle() {
        return
                "<style type=\"text/css\">"
                        + "body {background-color: #000000; }"
                        + "h1 {color: #bebebe; }"
                        + "li {color: #bebebe; }"
                        + "ul {color: #bebebe; }"
                        + "h1 { margin-left: 0px; font-size: 16pt; }"
                        + "li { margin-left: 0px; font-size: 6pt; }"
                        + "ul { padding-left: 30px;}"
                        + "</style>";
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
