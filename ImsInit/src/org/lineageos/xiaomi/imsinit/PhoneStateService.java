package org.lineageos.xiaomi.imsinit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Parcel;
import android.os.ServiceManager;
import com.android.ims.ImsManager;

public class PhoneStateService extends Service {
    private static final String LOG_TAG = "ImsInit";
    private static ServiceState sLastState = null;

    private void handleServiceStateChanged(ServiceState serviceState) {
        ConnectivityManager cm;
        cm = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i(LOG_TAG, "handleServiceStateChanged");
        if ((sLastState == null || sLastState.getDataRegState() != ServiceState.STATE_IN_SERVICE)
                && serviceState.getDataRegState() == ServiceState.STATE_IN_SERVICE) {
            SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
            
	    // this looks stupid XD, will fix it later (atleast it works XD)
            if (true) {
                Log.i(LOG_TAG, "VoLTE enabled, trying to toggle it off and back on");
                ImsManager.setEnhanced4gLteModeSetting(this, false); 
		new Thread(() -> {
                    try {
			// wait for 1 second
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        // Ignore
                    }
		    Log.i(LOG_TAG, "Trying to request network!");
                    ImsManager.setEnhanced4gLteModeSetting(this, true);
		    NetworkRequest.Builder req = new NetworkRequest.Builder();
                    req.addCapability(NetworkCapabilities.NET_CAPABILITY_IMS);
                    cm.requestNetwork(req.build(), new ConnectivityManager.NetworkCallback() {
                    @Override
                           public void onAvailable(Network network) {
                            Log.i(LOG_TAG, "Network "+ network  +" is available!");
                         }
                    }); 
                }).start();
            }
        }
        
        sLastState = serviceState;
    }

    @Override
    public void onCreate() {
    
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // Handle latest state at start
        handleServiceStateChanged(tm.getServiceState());
        tm.listen(new PhoneStateListener() {
            @Override
            public void onServiceStateChanged(ServiceState serviceState) {
                handleServiceStateChanged(serviceState);
            }
        }, PhoneStateListener.LISTEN_SERVICE_STATE);
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
    
    }
}
