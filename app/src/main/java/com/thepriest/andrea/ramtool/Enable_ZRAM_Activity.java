package com.thepriest.andrea.ramtool;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class Enable_ZRAM_Activity extends AppCompatActivity {
    private static final String TAG = Enable_ZRAM_Activity.class.getSimpleName();
    Switch switchZRAM;
    Button buttonApply;
    SeekBar seekBarSize, seekBarSwappiness, seekBarVFSCachePressure;
    //TextView textViewSize,textViewSwappiness,textViewVFSCachePressure;
    EditText textViewSize, textViewSwappiness, textViewVFSCachePressure;
    int iCurrentRefreshFreq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        //this.setTheme(R.style.Black);
        //setProgressBarVisibility(false);
        //setProgressBarIndeterminate(true);
        setContentView(R.layout.activity_enable__zram_);
        switchZRAM = (Switch) findViewById(R.id.switchZRAM);
        if (RAMToolApp.iZRAMSize > 0) {
            switchZRAM.setChecked(true);
        }
        textViewSize = (EditText) findViewById(R.id.textViewSize);
        textViewSize.setSelectAllOnFocus(true);
        textViewSize.setText("" + RAMToolApp.iZRAMSize);
        textViewSize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textViewSize.setSelection(textViewSize.getText().length(), textViewSize.getText().length());
            }
        });
        textViewSize.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    ((View) v).setSelected(true);
                seekBarSize.setProgress(Integer.parseInt(textViewSize.getText().toString())/50);
            }
        });
        textViewSize.setOnKeyListener(new TextView.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                seekBarSize.setProgress(Integer.parseInt(textViewSize.getText().toString())/50);
                return false;
            }

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    ((View) v).setSelected(true);
                seekBarSize.setProgress(Integer.parseInt(textViewSize.getText().toString())/50);
            }
        });
        seekBarSize = (SeekBar) findViewById(R.id.seekBarSize); // make seekbar object
        seekBarSize.setProgress(RAMToolApp.iZRAMSize / 50);

        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //       textViewSize.setText("" + seekBarSize.getProgress() );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                //Toast.makeText(Enable_ZRAM_Activity.this,"" + seekBarSize.getProgress(), Toast.LENGTH_SHORT).show();
                if (fromUser) textViewSize.setText("" + seekBarSize.getProgress()*50);
                textViewSize.setSelection(textViewSize.getText().length(), textViewSize.getText().length());
            }
        });
        textViewSwappiness = (EditText) findViewById(R.id.textViewSwappiness);
        textViewSwappiness.setSelectAllOnFocus(true);
        textViewSwappiness.setText("" + RAMToolApp.iSwappiness);
        textViewSwappiness.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textViewSwappiness.setSelection(textViewSwappiness.getText().length(), textViewSwappiness.getText().length());
            }
        });
        textViewSwappiness.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    ((View) v).setSelected(true);
                seekBarSwappiness.setProgress(Integer.parseInt(textViewSwappiness.getText().toString())/5);
            }
        });
        textViewSwappiness.setOnKeyListener(new TextView.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (textViewSwappiness.getText().length() > 0)
                    seekBarSwappiness.setProgress(Integer.parseInt(textViewSwappiness.getText().toString())/5);
                return false;
            }

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    ((View) v).setSelected(true);
                seekBarSwappiness.setProgress(Integer.parseInt(textViewSwappiness.getText().toString())/5);
            }
        });
        seekBarSwappiness = (SeekBar) findViewById(R.id.seekBarSwappiness); // make seekbar object
        seekBarSwappiness.setProgress(RAMToolApp.iSwappiness/5);
        seekBarSwappiness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser) textViewSwappiness.setText("" + seekBarSwappiness.getProgress()*5);
                textViewSwappiness.setSelection(textViewSwappiness.getText().length(), textViewSwappiness.getText().length());

            }
        });
        textViewVFSCachePressure = (EditText) findViewById(R.id.textViewVFSCachePressure);
        textViewVFSCachePressure.setSelectAllOnFocus(true);
        textViewVFSCachePressure.setText("" + RAMToolApp.iVFSCachePressure);
        textViewVFSCachePressure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textViewVFSCachePressure.setSelection(textViewVFSCachePressure.getText().length(), textViewVFSCachePressure.getText().length());
            }
        });
        textViewVFSCachePressure.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    ((View) v).setSelected(true);
                seekBarVFSCachePressure.setProgress(Integer.parseInt(textViewVFSCachePressure.getText().toString())/10);
            }
        });
        textViewVFSCachePressure.setOnKeyListener(new TextView.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (textViewVFSCachePressure.getText().length() > 0)
                    seekBarVFSCachePressure.setProgress(Integer.parseInt(textViewVFSCachePressure.getText().toString())/10);
                return false;
            }

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    ((View) v).setSelected(true);
                seekBarVFSCachePressure.setProgress(Integer.parseInt(textViewVFSCachePressure.getText().toString())/10);
            }
        });
        seekBarVFSCachePressure = (SeekBar) findViewById(R.id.seekBarVFSCachePressure); // make seekbar object
        seekBarVFSCachePressure.setProgress(RAMToolApp.iVFSCachePressure/10);

        seekBarVFSCachePressure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //       textViewSize.setText("" + seekBarSize.getProgress() );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                //Toast.makeText(Enable_ZRAM_Activity.this,"" + seekBarSize.getProgress(), Toast.LENGTH_SHORT).show();
                if (fromUser)
                    textViewVFSCachePressure.setText("" + seekBarVFSCachePressure.getProgress()*10);
                textViewVFSCachePressure.setSelection(textViewVFSCachePressure.getText().length(), textViewVFSCachePressure.getText().length());
            }
        });
        buttonApply = (Button) findViewById(R.id.buttonApply);
        buttonApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setZRAM();

                //               finish();

/*
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finish();
*/
                //  startActivity(new Intent(Enable_ZRAM_Activity.this, MainActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_enable__zram_, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setZRAM() {
        String sZRAM = "";
        String sCommand = "";
        if (switchZRAM.isChecked() == false) {
            new BackgroundThread().execute();

/*
            new BackgroundThread().execute("echo 1 > /sys/block/zram0/reset");
            new BackgroundThread().execute("swapoff /dev/block/zram1");
            new BackgroundThread().execute("echo 1 > /sys/block/zram1/reset");
            new BackgroundThread().execute("swapoff /dev/block/zram2");
            new BackgroundThread().execute("echo 1 > /sys/block/zram2/reset");
            new BackgroundThread().execute("swapoff /dev/block/zram3");
            new BackgroundThread().execute("echo 1 > /sys/block/zram3/reset");
            new BackgroundThread().execute("echo 0 > /proc/sys/vm/swappiness");
*/
/*
            try {
                Shell.sudo("swapoff /dev/block/zram0");
                Shell.sudo("echo 1 > /sys/block/zram0/reset");
                Shell.sudo("swapoff /dev/block/zram1");
                Shell.sudo("echo 1 > /sys/block/zram1/reset");
                Shell.sudo("swapoff /dev/block/zram2");
                Shell.sudo("echo 1 > /sys/block/zram2/reset");
                Shell.sudo("swapoff /dev/block/zram3");
                Shell.sudo("echo 1 > /sys/block/zram3/reset");
                Shell.sudo("echo 0 > /proc/sys/vm/swappiness");
                Toast.makeText(this, "ZRAM Disabled.", Toast.LENGTH_LONG).show();
            } catch (Shell.ShellException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //return;
*/
        } else {
            try {
/*
                swapoff /dev/block/zram0
        echo 1 > /sys/block/zram0/reset
                echo 78643200 > /sys/block/zram0/disksize
        mkswap /dev/block/zram0
        swapon /dev/block/zram0
                swapoff /dev/block/zram1
        echo 1 > /sys/block/zram1/reset
        echo [ZRAM1]: Enabling.
                echo 78643200 > /sys/block/zram1/disksize
        mkswap /dev/block/zram1
        swapon /dev/block/zram1
        echo [ZRAM1]: Ok.
                echo -------------------------------
                echo [ZRAM2]: Disabling.
                swapoff /dev/block/zram2
        echo 1 > /sys/block/zram2/reset
        echo [ZRAM2]: Enabling.
                echo 78643200 > /sys/block/zram2/disksize
        mkswap /dev/block/zram2
        swapon /dev/block/zram2
        echo [ZRAM2]: Ok.
                echo -------------------------------
                echo [ZRAM3]: Disabling.
                swapoff /dev/block/zram3
        echo 1 > /sys/block/zram3/reset
        echo [ZRAM3]: Enabling.
                echo 78643200 > /sys/block/zram3/disksize
        mkswap /dev/block/zram3
        swapon /dev/block/zram3
        echo [ZRAM3]: Ok.
                echo -------------------------------
                echo [ZRAM]: Setting swappiness
        echo 60 > /proc/sys/vm/swappiness
        echo -------------------------------
                echo [ZRAM]: ALL DONE!
*/
                int diskSize = Integer.parseInt(textViewSize.getText().toString());
                double dDiskSize = diskSize / MainActivity.iDiskNum + 0.5;
                diskSize = (int) dDiskSize;
                diskSize = diskSize * 1024 * 1024;
                for (int iDisk = 0; iDisk < MainActivity.iDiskNum; iDisk++) {
                    sZRAM = "zram" + iDisk;
                    sCommand = "swapoff /dev/block/" + sZRAM;
                    Shell.sudo(sCommand);
                    sCommand = "echo 1 > /sys/block/" + sZRAM + "/reset";
                    Shell.sudo(sCommand);
                    sCommand = "echo " + diskSize + " > /sys/block/" + sZRAM + "/disksize";
                    Shell.sudo(sCommand);
//            sCommand="echo " + diskSize + " > /sys/devices/virtual/block/" + sZRAM + "/disksize";
//            Shell.sudo(sCommand);
                    sCommand = "mkswap /dev/block/" + sZRAM;
                    Shell.sudo(sCommand);
                    sCommand = "swapon /dev/block/" + sZRAM;
                    Shell.sudo(sCommand);
                }
            } catch (Shell.ShellException e) {
                e.printStackTrace();
            } catch (java.lang.ArithmeticException e) {
                e.printStackTrace();
            }
            try {
                sCommand = "echo " + Integer.parseInt(textViewSwappiness.getText().toString()) + " > /proc/sys/vm/swappiness";
                Shell.sudo(sCommand);
                sCommand = "echo " + Integer.parseInt(textViewVFSCachePressure.getText().toString()) + " > /proc/sys/vm/vfs_cache_pressure";
                //sCommand="sysctl -w vm.vfs_cache_pressure=" + Integer.parseInt(textViewVFSCachePressure.getText().toString());
                Shell.sudo(sCommand);
            } catch (Shell.ShellException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), getString(R.string.ZRAM_enabled), Toast.LENGTH_LONG).show();
            finish();
        }
        return;
    }

    class BackgroundThread extends AsyncTask<String, Integer, String> {
        int iSleepTime = 200;

        protected void onPreExecute() {
            iCurrentRefreshFreq = RAMToolApp.iRefreshFrequency;
            //RAMToolApp.iRefreshFrequency = 3600000;
            //if (RAMToolApp.bShowNotification) NotificationService.iRefreshFrequency = 3600000;
            //setProgressBarIndeterminate(true);
            setProgressBarIndeterminateVisibility(true);
            setSupportProgressBarIndeterminateVisibility(true);
            buttonApply.setText(getString(R.string.Disabling_ZRAM));
            buttonApply.setEnabled(false);
            // Log.d(TAG, "On pre Exceute......");
        }

        protected String doInBackground(String... arg0) {
            Log.d(TAG, "On doInBackground...");
            publishProgress(0);
            try {
                Shell.sudo("swapoff /dev/block/zram0");
                Thread.sleep(iSleepTime);
                publishProgress(10);
                Shell.sudo("echo 1 > /sys/block/zram0/reset");
                Thread.sleep(iSleepTime);
                publishProgress(20);
                Shell.sudo("swapoff /dev/block/zram1");
                Thread.sleep(iSleepTime);
                publishProgress(30);
                Shell.sudo("echo 1 > /sys/block/zram1/reset");
                Thread.sleep(iSleepTime);
                publishProgress(40);
                Shell.sudo("swapoff /dev/block/zram2");
                Thread.sleep(iSleepTime);
                publishProgress(50);
                Shell.sudo("echo 1 > /sys/block/zram2/reset");
                Thread.sleep(iSleepTime);
                publishProgress(60);
                Shell.sudo("swapoff /dev/block/zram3");
                Thread.sleep(iSleepTime);
                publishProgress(70);
                Shell.sudo("echo 1 > /sys/block/zram3/reset");
                Thread.sleep(iSleepTime);
                publishProgress(80);
                Shell.sudo("echo 0 > /proc/sys/vm/swappiness");
                Thread.sleep(iSleepTime);
                publishProgress(90);
                Thread.sleep(iSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Shell.ShellException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                String sCommand = "";
                sCommand = "echo " + Integer.parseInt(textViewSwappiness.getText().toString()) + " > /proc/sys/vm/swappiness";
                Shell.sudo(sCommand);
                sCommand = "echo " + Integer.parseInt(textViewVFSCachePressure.getText().toString()) + " > /proc/sys/vm/vfs_cache_pressure";
                //sCommand="sysctl -w vm.vfs_cache_pressure=" + Integer.parseInt(textViewVFSCachePressure.getText().toString());
                Shell.sudo(sCommand);
            } catch (Shell.ShellException e) {
                e.printStackTrace();
            }
            publishProgress(100);
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer... a) {
            try {
                Thread.sleep(100);
                buttonApply.setText(getString(R.string.Disabling_ZRAM) + a[0] + "%");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();

                //Log.d(TAG,"You are in progress update ... " + a[0]);
            }
        }

        protected void onPostExecute(String result) {
            setProgressBarIndeterminateVisibility(false);
            Toast.makeText(getApplicationContext(), getString(R.string.ZRAM_disabled), Toast.LENGTH_LONG).show();
            buttonApply.setEnabled(true);
            buttonApply.setText(getString(R.string.apply));
            //RAMToolApp.iRefreshFrequency = iCurrentRefreshFreq;
            //if (RAMToolApp.bShowNotification)
            //    NotificationService.iRefreshFrequency = iCurrentRefreshFreq;
            finish();
            //Debug.stopMethodTracing();
            //Log.d(TAG + result,"");
        }
    }
}

