package com.thepriest.andrea.ramtool;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class RAMToolApp extends Application implements OnSharedPreferenceChangeListener {

    private static final String TAG = RAMToolApp.class.getSimpleName();
    SharedPreferences prefs;
    public static LogHelper mLogHelper;
    static public int iRefreshFrequency;
    static public String sZRAMDirectory, sLanguage;
    static public boolean bShowNotification, bShowAdvancedNotification, bDoubleBackToExit, bEnableDropCache, bLog, bScreenIsOn,bEnableKill;
    static public int iDiskNum;
    static public int iZRAMSize, iZRAMComprDataSize, iZRAMTotalMemoryUsed, iZRAMMaximumUsage;
    static public int iFreeMemory, iCachedMemory, iBuffersMemory, iTotalFreeMemory, iTotalMemory, iMinFreeMemory, iMaxFreeMemory;
    static public int iZRAMUsage;
    // static public int iMaximumZRAMUsage;
    static public int iSwappiness;
    static public int iVFSCachePressure;
    static public int iDiskSize0, iDiskSize1, iDiskSize2, iDiskSize3;
    static public int iOrigDataSize0, iOrigDataSize1, iOrigDataSize2, iOrigDataSize3;
    static public int iMemUsedTotal0, iMemUsedTotal1, iMemUsedTotal2, iMemUsedTotal3;
    static public int iComprDataSize0, iComprDataSize1, iComprDataSize2, iComprDataSize3;
    public static int iZRAMStatus[] = new int[4];
    public static int iMemory[] = new int[5];
    public static int memory[] = new int[5];
    public static int iMemoryLimitToDropCache,iMemoryLimitToKill;
    public static int iProcessLimit;
    //public static String sLogText = "";
    static public BroadcastReceiver mReceiver;
    public static String sWhiteColor = "<br><font color=\"#FFFFFF\">"; //white
    public static String sGrayColor = "<br><font color=\"#666666\">"; //gray
    public static String sType1Color = "<br><font color=\"#FF0000\">"; //red
    public static String sType2Color = "<br><font color=\"#00FF00\">"; //green
    public static String sType3Color = "<br><font color=\"#0099cc\">"; //blue
    public static String sClose = "</font><font color=\"#FFEEEE\"></font>";
    public static final String sMemInfo = "/proc/meminfo";
//    holo blue light = 33b5e5 ( rgb: 51, 181, 229 )
//    holo blue dark = 0099cc ( rgb: 0, 153 204 )
//    holo blue bright = 00ddff ( rgb: 0, 221, 255 )﻿

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        bScreenIsOn = true;
        //sLogText = getCSSStyle();
        mLogHelper.clearLog();
        //if (BuildConfig.DEBUG) Log.d(TAG, "The log msg");
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        iMinFreeMemory = 0;
        iMaxFreeMemory = 0;
        sZRAMDirectory = "/sys/devices/virtual/block";
        // Read SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        String prefString = prefs.getString("pref_ZRAM_directory", "1");
        int ipref = Integer.parseInt(prefString);
        if (ipref == 1) {
            sZRAMDirectory = "/sys/devices/virtual/block";
        }
        if (ipref == 0) sZRAMDirectory = "/dev/block";
        Log.d(TAG, "pref_ZRAM_directory= " + sZRAMDirectory);
        prefString = prefs.getString("refresh_frequency", "5");
        ipref = Integer.parseInt(prefString);
        Log.d(TAG, "ipref= " + ipref);
        iRefreshFrequency = ipref * 1000;
        if (ipref == -1) iRefreshFrequency = ipref * 3600000;
        Log.d(TAG, "refresh_frequency= " + iRefreshFrequency);
        bShowNotification = prefs.getBoolean("enable_notification", true);
        Log.d(TAG, "enable_notification= " + bShowNotification);
        if (bShowNotification) startService(new Intent(this, NotificationService.class));
        else stopService(new Intent(this, NotificationService.class));
        bShowAdvancedNotification = prefs.getBoolean("enable_advanced_notification", false);
        Log.d(TAG, "enable_advanced_notification= " + bShowAdvancedNotification);
        bDoubleBackToExit = prefs.getBoolean("double_back_to_exit", false);
        Log.d(TAG, "double_back_to_exit= " + bDoubleBackToExit);
        bEnableDropCache = prefs.getBoolean("enable_auto_drop_cache", false);
        Log.d(TAG, "bEnableDropCache= " + bEnableDropCache);
        bEnableKill = prefs.getBoolean("enable_auto_kill", false);
        Log.d(TAG, "bEnableKill= " + bEnableKill);
        prefString = prefs.getString("memory_limit_to_drop_cache", "160");
        ipref = Integer.parseInt(prefString);
        iMemoryLimitToDropCache = ipref;
        Log.d(TAG, "memory_limit_to_drop_cache= " + iMemoryLimitToDropCache);
        prefString = prefs.getString("memory_limit_to_kill", "128");
        ipref = Integer.parseInt(prefString);
        iMemoryLimitToKill = ipref;
        Log.d(TAG, "iMemoryLimitToKill= " + iMemoryLimitToKill);
        prefString = prefs.getString("process_limit", "30");
        ipref = Integer.parseInt(prefString);
        iProcessLimit = ipref;
        Log.d(TAG, "process_limit= " + iProcessLimit);
        bLog = prefs.getBoolean("enable_log", false);
        Log.d(TAG, "bLog= " + bLog);
        if (RAMToolApp.bLog)
            mLogHelper.appendLog("RAMToolApp::onCreate()", LogHelper.LogColor.GRAY);
        /**
         * set language
         */
        sLanguage = prefs.getString("language", "en");
        Locale locale = new Locale(sLanguage);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

/*
    private static String getCSSStyle() {
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


    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged -> key= " + key);
        if (RAMToolApp.bLog)
            mLogHelper.appendLog("RAMToolApp::onSharedPreferenceChanged()-> key= " + key);
        sZRAMDirectory = "/sys/devices/virtual/block";
        // Read SharedPreferences
        // prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefString = prefs.getString("pref_ZRAM_directory", "1");
        int ipref = Integer.parseInt(prefString);
        if (ipref == 1) {
            sZRAMDirectory = "/sys/devices/virtual/block";
        }
        if (ipref == 0) sZRAMDirectory = "/dev/block";
        Log.d(TAG, "pref_ZRAM_directory= " + sZRAMDirectory);
        prefString = prefs.getString("refresh_frequency", "5");
        ipref = Integer.parseInt(prefString);
        Log.d(TAG, "ipref= " + ipref);
        iRefreshFrequency = ipref * 1000;
        if (ipref == -1) iRefreshFrequency = ipref * 3600000;
        Log.d(TAG, "refresh_frequency= " + iRefreshFrequency);
        bShowNotification = prefs.getBoolean("enable_notification", true);
        Log.d(TAG, "enable_notification= " + bShowNotification);
        // passes values to MainActivity
        MainActivity.sZRAMDirectory = sZRAMDirectory;
        MainActivity.iRefreshFrequency = iRefreshFrequency;
        MainActivity.bShowNotification = bShowNotification;
        // Starts or stops the service
        if (bShowNotification) {
            startService(new Intent(this, NotificationService.class));
            NotificationService.iRefreshFrequency = iRefreshFrequency;
        } else {
            stopService(new Intent(this, NotificationService.class));
        }
        bShowAdvancedNotification = prefs.getBoolean("enable_advanced_notification", false);
        bDoubleBackToExit = prefs.getBoolean("double_back_to_exit", false);
        bEnableDropCache = prefs.getBoolean("enable_auto_drop_cache", false);
        bEnableKill = prefs.getBoolean("enable_auto_kill", false);
        prefString = prefs.getString("memory_limit_to_drop_cache", "128");
        ipref = Integer.parseInt(prefString);
        iMemoryLimitToDropCache = ipref;
        prefString = prefs.getString("memory_limit_to_kill", "128");
        ipref = Integer.parseInt(prefString);
        iMemoryLimitToKill = ipref;
        prefString = prefs.getString("process_limit", "30");
        ipref = Integer.parseInt(prefString);
        iProcessLimit = ipref;
        bLog = prefs.getBoolean("enable_log", false);
        if (key.equals("enable_log")) {
            if (bLog == false) mLogHelper.appendLog(getString(R.string.log_disabled));
            else mLogHelper.appendLog(getString(R.string.log_enabled));
        }
        /**
         * language
         */
        sLanguage = prefs.getString("language", "en");
        Locale locale = new Locale(sLanguage);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //iMemoryLimitToDropCache = prefs.getInt("memory_limit_to_drop_cache", 128);


    }

    public static boolean hasZRAM0() {
//        Log.d(TAG,sZRAMDirectory + "/zram0/disksize " + new File(sZRAMDirectory + "/zram0/disksize").exists());
        return new File(sZRAMDirectory + "/zram0/disksize").exists();
    }

    public static boolean hasZRAM1() {
        return new File(sZRAMDirectory + "/zram1/disksize").exists();
    }

    public static boolean hasZRAM2() {
        return new File(sZRAMDirectory + "/zram2/disksize").exists();
    }

    public static boolean hasZRAM3() {
        return new File(sZRAMDirectory + "/zram3/disksize").exists();
    }

    public static void updateZRAMStatus() {
/*
       try
        {
            //BufferedReader reader = new BufferedReader("/proc/sys/vm/swappiness", "r");
            RandomAccessFile reader = new RandomAccessFile("/proc/sys/vm/swappiness", "r");
            String load = reader.readLine();
            while (load != null)
            {
                textViewTotalSize.setText(load);
                Log.d("swappiness", "swappiness: " + load);
                load = reader.readLine();

            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            textViewTotalMemoryUsed.setText("IOException");
        }
*/
        // if (b_isActivityVisible==false) return;
        // Log.d(TAG, "updateZRAMStatus()");

        int diskNum = 0;
        if (hasZRAM0() == true) diskNum++;
        if (hasZRAM1() == true) diskNum++;
        if (hasZRAM2() == true) diskNum++;
        if (hasZRAM3() == true) diskNum++;
        // textViewDiskNum.setText(getString(R.string.ZRAM_disk_number) + diskNum);
        iDiskNum = diskNum;
        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/swappiness"));
            String line;

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                // textViewSwappiness.setText("Swappiness: " + line);
                iSwappiness = Integer.parseInt(line);

            }
/*
            mounts.close();
            //mounts = new BufferedReader(new FileReader("/sys/block/zram0/disksize"));
            mounts = new BufferedReader(new FileReader("/sys/devices/virtual/block/zram0/disksize"));

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                textViewTotalMemoryUsed.setText("ZRAM: " + line);


            }
            */
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
            //textViewComprDataSize.setText("FileNotFoundException");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
            //textViewComprDataSize.setText("IOException");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/vfs_cache_pressure"));
            String line;

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                //textViewSwappiness.setText("Swappiness: " + line);
                //textViewVFS_cache_pressure.setText("VFS cache pressure: " + line);
                iVFSCachePressure = Integer.parseInt(line);

            }
/*
            mounts.close();
            //mounts = new BufferedReader(new FileReader("/sys/block/zram0/disksize"));
            mounts = new BufferedReader(new FileReader("/sys/devices/virtual/block/zram0/disksize"));

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                textViewTotalMemoryUsed.setText("ZRAM: " + line);


            }
            */
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
            // textViewComprDataSize.setText("FileNotFoundException");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
            // textViewComprDataSize.setText("IOException");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        Shell shell = new Shell();
        String result1 = "";
        String result2 = "";
        String result3 = "";
        String result4 = "";
        int r1num = 0;
        int r2num = 0;
        int r3num = 0;
        int r4num = 0;
        int ZRAMSizeTot = 0;
        try {
            if (hasZRAM0() == true || hasZRAM0() == false) {
                //result1 = Shell.sudo("cat /sys/devices/virtual/block/zram0/disksize");
                //result2 = Shell.sudo("cat /sys/devices/virtual/block/zram0/size");
                result3 = Shell.sudo("cat /sys/devices/virtual/block/zram0/orig_data_size");
                result2 = Shell.sudo("cat /sys/devices/virtual/block/zram0/mem_used_total");
                result4 = Shell.sudo("cat /sys/devices/virtual/block/zram0/compr_data_size");
                //r1num = r1num + Integer.parseInt(result1.toString());
                r1num = getZRAMDiskSize(0);
                ZRAMSizeTot += r1num;
                r2num = r2num + Integer.parseInt(result2.toString());
                r3num = r3num + Integer.parseInt(result3.toString());
                r4num = r4num + Integer.parseInt(result4.toString());
                iDiskSize0 = r1num;
                iOrigDataSize0 = Integer.parseInt(result2.toString());
                iMemUsedTotal0 = Integer.parseInt(result3.toString());
                iComprDataSize0 = Integer.parseInt(result4.toString());
            }
            if (hasZRAM1() == true) {
                //result1 = Shell.sudo("cat /sys/devices/virtual/block/zram1/disksize");
                //result2 = Shell.sudo("cat /sys/devices/virtual/block/zram0/size");
                result3 = Shell.sudo("cat /sys/devices/virtual/block/zram1/orig_data_size");
                result2 = Shell.sudo("cat /sys/devices/virtual/block/zram1/mem_used_total");
                result4 = Shell.sudo("cat /sys/devices/virtual/block/zram1/compr_data_size");
                r1num = getZRAMDiskSize(1);
                ZRAMSizeTot += r1num;
                r2num = r2num + Integer.parseInt(result2.toString());
                r3num = r3num + Integer.parseInt(result3.toString());
                r4num = r4num + Integer.parseInt(result4.toString());
                iDiskSize1 = r1num;
                iOrigDataSize1 = Integer.parseInt(result2.toString());
                iMemUsedTotal1 = Integer.parseInt(result3.toString());
                iComprDataSize1 = Integer.parseInt(result4.toString());
            }
            if (hasZRAM2() == true) {
                result3 = Shell.sudo("cat /sys/devices/virtual/block/zram1/orig_data_size");
                result2 = Shell.sudo("cat /sys/devices/virtual/block/zram1/mem_used_total");
                result4 = Shell.sudo("cat /sys/devices/virtual/block/zram1/compr_data_size");
                r1num = getZRAMDiskSize(2);
                ZRAMSizeTot += r1num;
                r2num = r2num + Integer.parseInt(result2.toString());
                r3num = r3num + Integer.parseInt(result3.toString());
                r4num = r4num + Integer.parseInt(result4.toString());
                iDiskSize2 = r1num;
                iOrigDataSize1 = Integer.parseInt(result2.toString());
                iMemUsedTotal1 = Integer.parseInt(result3.toString());
                iComprDataSize1 = Integer.parseInt(result4.toString());
            }
            if (hasZRAM3() == true) {
                result3 = Shell.sudo("cat /sys/devices/virtual/block/zram1/orig_data_size");
                result2 = Shell.sudo("cat /sys/devices/virtual/block/zram1/mem_used_total");
                result4 = Shell.sudo("cat /sys/devices/virtual/block/zram1/compr_data_size");
                r1num = getZRAMDiskSize(3);
                ZRAMSizeTot += r1num;
                r2num = r2num + Integer.parseInt(result2.toString());
                r3num = r3num + Integer.parseInt(result3.toString());
                r4num = r4num + Integer.parseInt(result4.toString());
                iDiskSize3 = r1num;
                iOrigDataSize1 = Integer.parseInt(result2.toString());
                iMemUsedTotal1 = Integer.parseInt(result3.toString());
                iComprDataSize1 = Integer.parseInt(result4.toString());
            }
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG) Log.d(TAG, "java.lang.NullPointerException");
        } catch (Shell.ShellException e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG) Log.d(TAG, "Shell.ShellException");
        } catch (NumberFormatException nfe) {
            if (BuildConfig.DEBUG) Log.d(TAG, "NumberFormatException: can't parse " + nfe);
        } finally {
            try {
                iZRAMSize = ZRAMSizeTot / 1024 / 1024;
//                r1num = Integer.parseInt(result1.toString());
                r1num = r1num / 1024 / 1024;
                //              r2num = Integer.parseInt(result2.toString());
                r2num = r2num / 1024 / 1024;
                //            r3num = Integer.parseInt(result3.toString());
                r3num = r3num / 1024 / 1024;
                //          r4num = Integer.parseInt(result4.toString());
                r4num = r4num / 1024 / 1024;
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        }
        //iZRAMSize = r1num;
        iZRAMComprDataSize = r4num;
        iZRAMTotalMemoryUsed = r2num;
        //textViewTotalSize.setText(getString(R.string.ZRAM_size) + r1num + " MB");
        //textViewTotalMemoryUsed.setText(getString(R.string.ZRAM_total) + r2num + " MB");
        //textViewOrigDataSize.setText(getString(R.string.ZRAM_original) + r3num + " MB");
        //textViewComprDataSize.setText(getString(R.string.ZRAM_compressed) + r4num + " MB");
        //int iMemory[] = new int[5];
        //iMemory = getMemoryInfo();
        //textViewFreeRam.setText("Free memory: " + iMemory[0] + " MB");
        //textViewCached.setText("Cached: " + iMemory[2] + " MB");
        //textViewBuffers.setText("Buffers: " + iMemory[1] + " MB");
        //textViewTotalFree.setText("Total free memory: " + iMemory[3] + " MB");
        //textViewTotal.setText("Total memory: " + iMemory[4] + " MB");
        iZRAMUsage = r3num;
        if (iZRAMUsage > iZRAMMaximumUsage) iZRAMMaximumUsage = iZRAMUsage;
        //if (r3num > iZRAMMaximumUsage) iZRAMMaximumUsage = r3num;
        //textViewMaxZRAMUsage.setText("Maximum ZRAM usage: " + iMaximumZRAMUsage + " MB");
/*
        if (bShowNotification) {
            NotificationCompat.Builder appLaunch = new NotificationCompat.Builder(this);
            appLaunch.setSmallIcon(R.drawable.ic_launcher_48);
            appLaunch.setContentTitle("RAMTool");
            appLaunch.setContentText("ZRAM: " + r3num + " - Max ZRAM: " + iMaximumZRAMUsage);
            //appLaunch.setAutoCancel(true);
            appLaunch.setOngoing(true);
            appLaunch.setUsesChronometer(true);
            Intent targetIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            appLaunch.setContentIntent(contentIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mNotificationManager.cancelAll();
            mNotificationManager.notify(0, appLaunch.build());
        }
*/
    }

    public static void updateStatus() {
        // Log.d(TAG, "updateZRAMStatus2()");
        //int diskNum = 0;
        //if (hasZRAM0() == true) diskNum++;
        //if (hasZRAM1() == true) diskNum++;
        //if (hasZRAM2() == true) diskNum++;
        //if (hasZRAM3() == true) diskNum++;
        //iDiskNum = diskNum;
        //getSwappiness();
        //getVFSCachePressure();
        //int r1num = 0;
        //int r2num = 0;
        //int r3num = 0;
        //int r4num = 0;
        // int ZRAMSizeTot = 0;
/*
        try {
            if (hasZRAM0() == true || hasZRAM0() == false) {
                iZRAMStatus = getZRAMStatus(0);
                r1num = iZRAMStatus[0];
                ZRAMSizeTot += r1num;
                r2num = iZRAMStatus[2];
                r3num = iZRAMStatus[1];
                r4num = iZRAMStatus[3];
                iDiskSize0 = r1num;
                iOrigDataSize0 = r2num;
                iMemUsedTotal0 = r3num;
                iComprDataSize0 = r4num;
            }
            if (hasZRAM1() == true) {
                iZRAMStatus = getZRAMStatus(1);
                r1num = iZRAMStatus[0];
                ZRAMSizeTot += r1num;
                r2num += iZRAMStatus[2];
                r3num += iZRAMStatus[1];
                r4num += iZRAMStatus[3];
            }
            if (hasZRAM2() == true) {
                iZRAMStatus = getZRAMStatus(2);
                r1num = iZRAMStatus[0];
                ZRAMSizeTot += r1num;
                r2num += iZRAMStatus[2];
                r3num += iZRAMStatus[1];
                r4num += iZRAMStatus[3];
            }
            if (hasZRAM3() == true) {
                iZRAMStatus = getZRAMStatus(3);
                r1num = iZRAMStatus[0];
                ZRAMSizeTot += r1num;
                r2num += iZRAMStatus[2];
                r3num += iZRAMStatus[1];
                r4num += iZRAMStatus[3];
            }
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG) Log.d(TAG, "java.lang.NullPointerException");
        } catch (NumberFormatException nfe) {
            if (BuildConfig.DEBUG) Log.d(TAG, "NumberFormatException: can't parse " + nfe);
        } finally {
            try {
                iZRAMSize = ZRAMSizeTot / 1024 / 1024;
//                r1num = Integer.parseInt(result1.toString());
                r1num = r1num / 1024 / 1024;
                //              r2num = Integer.parseInt(result2.toString());
                r2num = r2num / 1024 / 1024;
                //            r3num = Integer.parseInt(result3.toString());
                r3num = r3num / 1024 / 1024;
                //          r4num = Integer.parseInt(result4.toString());
                r4num = r4num / 1024 / 1024;
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        }
*/
        //iZRAMSize = r1num;
        //iZRAMComprDataSize = r4num;
        //iZRAMTotalMemoryUsed = r2num;
        //int iMemory[] = new int[5];
        //iMemory = getMemoryInfo();
        //iZRAMUsage = r3num;
        //if (iZRAMUsage > iZRAMMaximumUsage) iZRAMMaximumUsage = iZRAMUsage;
        //if (r3num > iMaximumZRAMUsage) iMaximumZRAMUsage = r3num;
        String str2;
        String[] arrayOfString;
        // int memory[] = new int[5];
        try {
            FileReader localFileReader = new FileReader(sMemInfo);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
/*
            while ((str2 = localBufferedReader.readLine()) != null) {
                if (RAMToolApp.bLog) RAMToolApp.mLogHelper.appendLog(str2);
            }
*/
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            iMemory[4] = Integer.valueOf(arrayOfString[1]).intValue();
            iMemory[4] = iMemory[4] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            iMemory[0] = Integer.valueOf(arrayOfString[1]).intValue();
            iMemory[0] = iMemory[0] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            iMemory[1] = Integer.valueOf(arrayOfString[1]).intValue();
            iMemory[1] = iMemory[1] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            iMemory[2] = Integer.valueOf(arrayOfString[1]).intValue();
            iMemory[2] = iMemory[2] / 1024;
            iMemory[3] = iMemory[0] + iMemory[1] + iMemory[2];
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
         catch (Exception e) {
            e.printStackTrace();
        }
*/
        iFreeMemory = iMemory[0];
        iCachedMemory = iMemory[2];
        iBuffersMemory = iMemory[1];
        iTotalFreeMemory = iMemory[3];
        iTotalMemory = iMemory[4];
        if (iMinFreeMemory == 0) iMinFreeMemory = iTotalFreeMemory;
        if (iTotalFreeMemory > iMaxFreeMemory)
            iMaxFreeMemory = iTotalFreeMemory;
        if (iTotalFreeMemory < iMinFreeMemory)
            iMinFreeMemory = iTotalFreeMemory;
    }

    public static void updateRAMStatus() {
        //int iMemory[] = new int[5];
        iMemory = getMemoryInfo();
        iFreeMemory = iMemory[0];
        iCachedMemory = iMemory[2];
        iBuffersMemory = iMemory[1];
        iTotalFreeMemory = iMemory[3];
        iTotalMemory = iMemory[4];
        if (iMinFreeMemory == 0) iMinFreeMemory = iTotalFreeMemory;
        if (iTotalFreeMemory > iMaxFreeMemory)
            iMaxFreeMemory = iTotalFreeMemory;
        if (iTotalFreeMemory < iMinFreeMemory)
            iMinFreeMemory = iTotalFreeMemory;
    }

    private static void getVFSCachePressure() {
        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/vfs_cache_pressure"));
            String line;
            while ((line = mounts.readLine()) != null) {
                iVFSCachePressure = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
            // textViewComprDataSize.setText("FileNotFoundException");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
            // textViewComprDataSize.setText("IOException");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
    }

    private static void getSwappiness() {
        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/swappiness"));
            String line;
            while ((line = mounts.readLine()) != null) {
                iSwappiness = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }

    }

    public static void updateZRAMStatus2() {
        // Log.d(TAG, "updateZRAMStatus2()");
        int diskNum = 0;
        if (hasZRAM0() == true) diskNum++;
        if (hasZRAM1() == true) diskNum++;
        if (hasZRAM2() == true) diskNum++;
        if (hasZRAM3() == true) diskNum++;
        iDiskNum = diskNum;
        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/swappiness"));
            String line;
            while ((line = mounts.readLine()) != null) {
                iSwappiness = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/vfs_cache_pressure"));
            String line;
            while ((line = mounts.readLine()) != null) {
                iVFSCachePressure = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
            // textViewComprDataSize.setText("FileNotFoundException");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
            // textViewComprDataSize.setText("IOException");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        Shell shell = new Shell();
        String result1 = "";
        String result2 = "";
        String result3 = "";
        String result4 = "";
        int r1num = 0;
        int r2num = 0;
        int r3num = 0;
        int r4num = 0;
        int ZRAMSizeTot = 0;
        try {
            if (hasZRAM0() == true || hasZRAM0() == false) {
                r1num = getZRAMDiskSize(0);
                ZRAMSizeTot += r1num;
                r3num = getZRAMorig_data_size(0);
                r2num = getZRAMmem_used_total(0);
                r4num = getZRAMcompr_data_size(0);
                iDiskSize0 = r1num;
                iOrigDataSize0 = r2num;
                iMemUsedTotal0 = r3num;
                iComprDataSize0 = r4num;
            }
            if (hasZRAM1() == true) {
                r1num = getZRAMDiskSize(1);
                ZRAMSizeTot += r1num;
                r3num += getZRAMorig_data_size(1);
                r2num += getZRAMmem_used_total(1);
                r4num += getZRAMcompr_data_size(1);
                iDiskSize0 = r1num;
                iOrigDataSize0 = r2num;
                iMemUsedTotal0 = r3num;
                iComprDataSize0 = r4num;
            }
            if (hasZRAM2() == true) {
                r1num = getZRAMDiskSize(2);
                ZRAMSizeTot += r1num;
                r3num += getZRAMorig_data_size(2);
                r2num += getZRAMmem_used_total(2);
                r4num += getZRAMcompr_data_size(2);
                iDiskSize0 = r1num;
                iOrigDataSize0 = r2num;
                iMemUsedTotal0 = r3num;
                iComprDataSize0 = r4num;
            }
            if (hasZRAM3() == true) {
                r1num = getZRAMDiskSize(3);
                ZRAMSizeTot += r1num;
                r3num += getZRAMorig_data_size(3);
                r2num += getZRAMmem_used_total(3);
                r4num += getZRAMcompr_data_size(3);
                iDiskSize0 = r1num;
                iOrigDataSize0 = r2num;
                iMemUsedTotal0 = r3num;
                iComprDataSize0 = r4num;
            }
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG) Log.d(TAG, "java.lang.NullPointerException");
        } catch (NumberFormatException nfe) {
            if (BuildConfig.DEBUG) Log.d(TAG, "NumberFormatException: can't parse " + nfe);
        } finally {
            try {
                iZRAMSize = ZRAMSizeTot / 1024 / 1024;
//                r1num = Integer.parseInt(result1.toString());
                r1num = r1num / 1024 / 1024;
                //              r2num = Integer.parseInt(result2.toString());
                r2num = r2num / 1024 / 1024;
                //            r3num = Integer.parseInt(result3.toString());
                r3num = r3num / 1024 / 1024;
                //          r4num = Integer.parseInt(result4.toString());
                r4num = r4num / 1024 / 1024;
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        }
        //iZRAMSize = r1num;
        iZRAMComprDataSize = r4num;
        iZRAMTotalMemoryUsed = r2num;
        //int iMemory[] = new int[5];
        //iMemory = getMemoryInfo();
        iZRAMUsage = r3num;
        if (iZRAMUsage > iZRAMMaximumUsage) iZRAMMaximumUsage = iZRAMUsage;
        //  if (r3num > iMaximumZRAMUsage) iMaximumZRAMUsage = r3num;
    }

    public static int getZRAMDiskSize(int disk) {
        String path = "/sys/devices/virtual/block/zram";
        path += disk;
        path += "/disksize";
        try {
            BufferedReader mounts = new BufferedReader(new FileReader(path));
            String line;
            while ((line = mounts.readLine()) != null) {
                disk = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        return disk;
    }

    public static int getZRAMorig_data_size(int disk) {
        String path = "/sys/devices/virtual/block/zram";
        path += disk;
        path += "/orig_data_size";
        try {
            BufferedReader mounts = new BufferedReader(new FileReader(path));
            String line;
            while ((line = mounts.readLine()) != null) {
                disk = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        return disk;
    }

    public static int getZRAMmem_used_total(int disk) {
        String path = "/sys/devices/virtual/block/zram";
        path += disk;
        path += "/mem_used_total";
        try {
            BufferedReader mounts = new BufferedReader(new FileReader(path));
            String line;
            while ((line = mounts.readLine()) != null) {
                disk = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        return disk;
    }

    public static int getZRAMcompr_data_size(int disk) {
        String path = "/sys/devices/virtual/block/zram";
        path += disk;
        path += "/compr_data_size";
        try {
            BufferedReader mounts = new BufferedReader(new FileReader(path));
            String line;
            while ((line = mounts.readLine()) != null) {
                disk = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        return disk;
    }

    /**
     * Get ZRAM information of the specified ZRAM disk and returns an array containing the data.
     *
     * @param disk ZRAM disk.
     * @return array containing the data of the specified ZRAM disk.
     */
    public static int[] getZRAMStatus(int disk) {
        int iResult = 0;
        String path;
        try {
            path = "/sys/devices/virtual/block/zram";
            path += disk;
            path += "/disksize";
            BufferedReader mounts = new BufferedReader(new FileReader(path));
            String line;
            while ((line = mounts.readLine()) != null) {
                iResult = Integer.parseInt(line);
            }
            mounts.close();
            iZRAMStatus[0] = iResult;
            path = "/sys/devices/virtual/block/zram";
            path += disk;
            path += "/orig_data_size";
            mounts = new BufferedReader(new FileReader(path));
            while ((line = mounts.readLine()) != null) {
                iResult = Integer.parseInt(line);
            }
            mounts.close();
            path = "/sys/devices/virtual/block/zram";
            iZRAMStatus[1] = iResult;
            path += disk;
            path += "/mem_used_total";
            mounts = new BufferedReader(new FileReader(path));
            while ((line = mounts.readLine()) != null) {
                iResult = Integer.parseInt(line);
            }
            mounts.close();
            iZRAMStatus[2] = iResult;
            path = "/sys/devices/virtual/block/zram";
            path += disk;
            path += "/compr_data_size";
            mounts = new BufferedReader(new FileReader(path));
            while ((line = mounts.readLine()) != null) {
                iResult = Integer.parseInt(line);
            }
            mounts.close();
        } catch (FileNotFoundException e) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Cannot find ZRAM...");
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Ran into problems reading...");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        iZRAMStatus[3] = iResult;
        return iZRAMStatus;
    }

    public static int[] getMemoryInfo() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        // int memory[] = new int[5];
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            iMemory[4] = Integer.valueOf(arrayOfString[1]).intValue();
            iMemory[4] = iMemory[4] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            iMemory[0] = Integer.valueOf(arrayOfString[1]).intValue();
            iMemory[0] = iMemory[0] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            iMemory[1] = Integer.valueOf(arrayOfString[1]).intValue();
            iMemory[1] = iMemory[1] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            iMemory[2] = Integer.valueOf(arrayOfString[1]).intValue();
            iMemory[2] = iMemory[2] / 1024;
            iMemory[3] = iMemory[0] + iMemory[1] + iMemory[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return memory;
    }
}
