package com.example.xenhao.keycontrol;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by XenHao on 25/2/2015.
 */
public class SOSCall extends IntentService {

    public SOSCall(){   super("SOSCallingThread");}

    @Override
    protected void onHandleIntent(Intent intent){
        Log.i("IntentService", "IntentService started to call URL");

        //  testing call to external webpage
        try{
            URL url = new URL("http://webapps.alphacrossing.com/sos/sos.php");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            readStream(con.getInputStream());
        }catch(Exception e){
            Log.i("SOS URL Call", "readStream error");
            e.printStackTrace();
        }

        //  testing SOS function through SMS
//        hasSignal = signalCheck.getNetworkType() != android.telephony.TelephonyManager.NETWORK_TYPE_UNKNOWN;
//        try {
//            if (signalCheck.getNetworkType() != android.telephony.TelephonyManager.NETWORK_TYPE_UNKNOWN) {
//                Log.i("SOS SMS Call", "Phone Signal Detected");
//                try {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(getResources().getString(R.string.sos_number), null, getResources().getString(R.string.sos_content), null, null);
//                    Log.i("SOS SMS Call", "SOS SMS sent");
//                } catch (Exception e) {
//                    Log.i("SOS SMS Error", "SOS SMS sending failed");
//                    e.printStackTrace();
//                }
//            } else {
//                Log.i("SOS SMS Error", "No Phone Signal Detected");
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }

    private void readStream(InputStream inputStream) {
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = reader.readLine()) != null){
                System.out.println(line);
            }
        }catch (IOException e){
            e.printStackTrace();        Log.i("SOS URL Call Error", "Unable to call URL");
        }finally {
            if(reader != null){
                try{
                    reader.close();     Log.i("SOS URL Call", "Process done and closed");
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
