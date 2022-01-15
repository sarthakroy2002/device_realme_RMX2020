package org.lineageos.oppo.imsinit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.os.UserHandle;
import android.util.Log;

import com.android.ims.ImsManager;

/*
 * WARNING: DIRTY HACK AHEAD
 * I have no idea why MTK's IMS does not work after a reboot on
 * SELinux enforcing. I have literally tried to put every service
 * with denial messages to permissive and the bug was still present.
 * But a global permissive environment fixes that magically.
 * Re-enabling Enhanced 4G manually also fixes the issue.
 * This is a dirty hack to toggle IMS off and back on again
 * on every boot other than the first boot. I figured this is
 * at least better than having to put the entire system on permissive.
 * But it is very dirty and can't be guaranteed to work.
 * We still need to figure out at some point why this happens.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "ImsInit";
    
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(LOG_TAG, "onBoot");

        /* Toggle airplane mode on and off at boot to enable VoLTE. */
        Log.i(LOG_TAG, "Trying to toggle airplane mode to enable VoLTE");
        new Thread(() -> {
            Intent tIntent;

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            tIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            tIntent.putExtra("state", true);
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 1);
            context.sendBroadcastAsUser(tIntent, UserHandle.ALL);

            /*
             * Sleep for a second before toggling airplane mode
             * off. Without this, airplane mode can sometimes
             * fail to be toggled off again.
             */
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}

            tIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            tIntent.putExtra("state", false);
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
            context.sendBroadcastAsUser(tIntent, UserHandle.ALL);
        }).start();
        
        context.startService(new Intent(context, PhoneStateService.class));
    }
}
