package com.thepriest.andrea.ramtool;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NotificationView extends Activity {
    Button buttonCleanMemory, buttonCleanDropCache, buttonCleanAll;
    String title;
    String text;
    TextView txttitle;
    TextView txttext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);
        buttonCleanMemory = (Button) findViewById(R.id.buttonCleanMemory);
        buttonCleanMemory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanMemory();
            }
        });
        buttonCleanDropCache = (Button) findViewById(R.id.buttonCleanDropCache);
        buttonCleanDropCache.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanDropCache();
            }
        });
        buttonCleanAll = (Button) findViewById(R.id.buttonCleanAll);
        buttonCleanAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanMemoryAndDropCache();
            }
        });

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        // Retrive the data from MainActivity.java
        Intent i = getIntent();

        title = i.getStringExtra("title");
        text = i.getStringExtra("text");

        // Locate the TextView
        txttitle = (TextView) findViewById(R.id.title);
        txttext = (TextView) findViewById(R.id.textUp);

        // Set the data into TextView
        txttitle.setText(title);
        txttext.setText(text);
    }
    private void cleanMemoryAndDropCache() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context
                .ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            //if (procInfos.get(i).processName.equals("com.android.music")) {
            //Toast.makeText(null, "music is running",
            //      Toast.LENGTH_LONG).show();
            activityManager.killBackgroundProcesses(procInfos.get(i).processName);
        }
/*
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(10);
        for (int i = 0; i < serviceInfos.size(); i++) {
            activityManager.killBackgroundProcesses(serviceInfos.get(i).process);
        }
*/
        String result1 = "";
        try {
            result1 = Shell.sudo("sync");
            result1 = Shell.sudo("echo 3 > /proc/sys/vm/drop_caches");
        } catch (com.thepriest.andrea.ramtool.Shell.ShellException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        }
        Toast.makeText(getApplicationContext(), "Memory and Drop Cache cleaned.", Toast.LENGTH_LONG).show();
        return;
    }

    private void cleanDropCache() {
        String result1 = "";
        try {
            result1 = Shell.sudo("sync");
            result1 = Shell.sudo("echo 3 > /proc/sys/vm/drop_caches");
        } catch (Shell.ShellException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        }
        Toast.makeText(getApplicationContext(), "Drop Cache cleaned.", Toast.LENGTH_LONG).show();
        return;
    }

    private void cleanMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context
                .ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            //if (procInfos.get(i).processName.equals("com.android.music")) {
            //Toast.makeText(null, "music is running",
            //      Toast.LENGTH_LONG).show();
            activityManager.killBackgroundProcesses(procInfos.get(i).processName);
        }
/*
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(10);
        for (int i = 0; i < serviceInfos.size(); i++) {
            activityManager.killBackgroundProcesses(serviceInfos.get(i).process);
        }
*/

        Toast.makeText(getApplicationContext(), getString(R.string.Memory_cleaned), Toast.LENGTH_LONG).show();
        return;
    }

}