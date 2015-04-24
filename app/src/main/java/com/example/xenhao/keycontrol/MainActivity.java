package com.example.xenhao.keycontrol;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    //  Boolean for timer
    private Boolean SOStimer = false;

    //  timer to time quick press
    private Timer sosTimer;

    //  SOS button counter
    private int sosCounter = 0;

    //  boolean for gsm signal availability
    private boolean isSendSMS;

    //  Broadcast object
    BroadcastReceiver mybroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("[BroadcastReceiver]", "MyReceiver");

            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                Log.i("[BroadcastReceiver]", "Screen ON");
                if(!SOStimer){
                    SOStimer = true;
                    //  start SOS timer
                    startTimer();
                }else{
                    //  record button push count
                    if(++sosCounter >= 2){
                        //  execute SOS call, cancel sosTimer, reset sosCounter
                        Log.i("[SOS CALL]: ", "SOS function initiated");

                        //  network(internet) checking
                        Log.i("SOS Network Check", "Network availability is: " + isNetworkAvailable());

                        //  start IntentService to call URL
                        if(isNetworkAvailable()) {
                            Intent urlIntent = new Intent(MainActivity.this, SOSCall.class);
                            startService(urlIntent);
                        }else{
                            Log.i("Data Connection Error", "Internet Connection Unavailable");
                            Toast.makeText(context, "Internet Connection Unavailable", Toast.LENGTH_LONG).show();
                        }

                        //  GSM checking
                        Tel.listen(MyListener, PhoneStateListener.LISTEN_SERVICE_STATE);

                        //  start IntentService to send SMS
                        if(isSendSMS) {
                            Intent smsIntent = new Intent(MainActivity.this, SMSCall.class);
                            startService(smsIntent);
                        }else{
                            Log.i("GSM Error", "GSM Signal Unavailable");
                            Toast.makeText(context, "GSM Signal Unavailable", Toast.LENGTH_LONG).show();
                        }

                        resetCount();
                        Toast.makeText(context, "SOS INITIATED", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                Log.i("[BroadcastReceiver]", "Screen OFF");
                if(!SOStimer){
                    SOStimer = true;
                    //  start SOS timer
                    startTimer();
                }else{
                    //  record button push count
                    if(++sosCounter >= 2){
                        //  execute SOS call, cancel sosTimer, reset sosCounter
                        Log.i("[SOS CALL] ", "SOS function initiated");

                        //  network(internet) checking
                        Log.i("SOS Network Check", "Network availability is: " + isNetworkAvailable());

                        //  start IntentService to call URL
                        if(isNetworkAvailable()) {
                            Intent urlIntent = new Intent(MainActivity.this, SOSCall.class);
                            startService(urlIntent);
                        }else{
                            Log.i("Data Connection Error", "Internet Connection Unavailable");
                            Toast.makeText(context, "Internet Connection Unavailable", Toast.LENGTH_LONG).show();
                        }

                        //  GSM checking
                        Tel.listen(MyListener, PhoneStateListener.LISTEN_SERVICE_STATE);

                        //  start IntentService to send SMS
                        if(isSendSMS) {
                            Intent smsIntent = new Intent(MainActivity.this, SMSCall.class);
                            startService(smsIntent);
                        }else{
                            Log.i("GSM Error", "GSM Signal Unavailable");
                            Toast.makeText(context, "GSM Signal Unavailable", Toast.LENGTH_LONG).show();
                        }

                        resetCount();
                        Toast.makeText(context, "SOS INITIATED", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }


    };

    //  function to check network (WiFi or mobile data) availability
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        //  if no network is available, networkInfo will be null
        //  otherwise, check if we are connected
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    private void startTimer() {
        sosTimer = new Timer();
        sosTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                resetCount();
            }
        }, 2000, 1000);
    }

    private void resetCount() {
        Log.i("[Reset Counter] ", "sosTimer reset");
        sosTimer.cancel();
        sosTimer.purge();
        SOStimer = false;
        sosCounter = 0;
    }

    //  Key Event functions //

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
//        switch (keyCode){
//            case    KeyEvent.KEYCODE_MENU:
//                Toast.makeText(this, "App switch key pressed", Toast.LENGTH_SHORT).show();
//                return true;
//        }

        if(event.getKeyCode() == KeyEvent.KEYCODE_MENU){
            event.startTracking();
            Toast.makeText(this, "Power key Down", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_MENU:
                Toast.makeText(this, "App switch released", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event){
        Toast.makeText(this, "Power Key Long Press Detected", Toast.LENGTH_LONG).show();
        return true;
    }

    //  GSM Signal Strength Detection //

    TelephonyManager Tel;
    MyPhoneStateListener MyListener;

    //  PhoneStateListener class
    private class MyPhoneStateListener extends PhoneStateListener{
        //  get signal strength from the provider, each time there is an update
//        @Override
//        public void onSignalStrengthsChanged(SignalStrength signalStrength){
//            super.onSignalStrengthsChanged(signalStrength);
//            Toast.makeText(getApplicationContext(), "GSM Signal Strength = " + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();
//            Log.i("[GSM Signal Detection]", "GSM Signal Strength = " + String.valueOf(signalStrength.getGsmSignalStrength()));
//            Log.i("[GSM Signal Detection]", "isGSM() = " + signalStrength.isGsm());
//        }

        //  GSM signal availability
        @Override
        public void onServiceStateChanged(ServiceState serviceState){
            super.onServiceStateChanged(serviceState);
            switch (serviceState.getState()) {
                case ServiceState.STATE_IN_SERVICE:
                    isSendSMS = true;
                    Log.i("Phone State Listener", "onServiceStateChanged: STATE_IN_SERVICE");
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    isSendSMS = false;
                    Log.i("Phone State Listener", "onServiceStateChanged: STATE_OUT_OF_SERVICE");
                    break;
                case ServiceState.STATE_EMERGENCY_ONLY:
                    isSendSMS = false;
                    Log.i("Phone State Listener", "onServiceStateChanged: STATE_EMERGENCY_ONLY");
                    break;
                case ServiceState.STATE_POWER_OFF:
                    isSendSMS = false;
                    Log.i("Phone State Listener", "onServiceStateChanged: STATE_POWER_OFF");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Update listener & start it
        MyListener = new MyPhoneStateListener();
        Tel = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    @Override
    protected void onStart(){
        super.onStart();

        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
