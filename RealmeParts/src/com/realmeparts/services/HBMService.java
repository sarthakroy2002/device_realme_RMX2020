/*
 * Copyright (C) 2021 HyperTeam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.realmeparts;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class HBMService extends Service {

    public BroadcastReceiver mHBMintent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_USER_PRESENT) {
                Utils.writeValue(HBMModeSwitch.getFile(), "1");
                Log.d("DeviceSettings", "HBM Enabled");
            } else if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
                Utils.writeValue(HBMModeSwitch.getFile(), "0");
                Log.d("DeviceSettings", "HBM Disabled");
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter HBMintent = new IntentFilter();
        HBMintent.addAction(Intent.ACTION_USER_PRESENT);
        HBMintent.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mHBMintent, HBMintent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHBMintent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}