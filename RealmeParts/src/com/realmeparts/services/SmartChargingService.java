/*
 * Copyright (C) 2020 The LineageOS Project
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
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class SmartChargingService extends Service {

    private static final boolean Debug = false;
    private static final boolean resetBatteryStats = false;
    public static String cool_down = "/sys/class/power_supply/battery/cool_down";
    public static String current = "/sys/class/power_supply/battery/current_now";
    public static String mmi_charging_enable = "/sys/class/power_supply/battery/mmi_charging_enable";
    public static String battery_capacity = "/sys/class/power_supply/battery/capacity";
    public static String battery_temperature = "/sys/class/power_supply/battery/temp";
    private boolean mconnectionInfoReceiver;
    private SharedPreferences sharedPreferences;
    public BroadcastReceiver mBatteryInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float battTemp = ((float) Integer.parseInt(Utils.readLine(battery_temperature))) / 10;
            int battCap = Integer.parseInt(Utils.readLine(battery_capacity));
            int coolDown = Integer.parseInt(Utils.readLine(cool_down));
            int currentmA = -(Integer.parseInt(Utils.readLine(current)));
            int chargingLimit = Integer.parseInt(Utils.readLine(mmi_charging_enable));
            int userSelectedChargingLimit = sharedPreferences.getInt("seek_bar", 95);
            int chargingSpeed = Settings.Secure.getInt(context.getContentResolver(), "charging_speed", 0);

            Log.d("DeviceSettings", "Battery Temperature: " + battTemp + ", Battery Capacity: " + battCap + "%, " + "\n" + "Charging Speed: " + currentmA + " mA, " + "Cool Down: " + coolDown);

            if (isCoolDownAvailable() && chargingLimit == 1) {
                // Setting cool down values based on user selected charging speed
                if (chargingSpeed != 0 && coolDown != chargingSpeed) {
                    Utils.writeValue(cool_down, String.valueOf(chargingSpeed));
                    Log.d("DeviceSettings", "Battery Temperature: " + battTemp + "\n" + "Battery Capacity: " + battCap + "%" + "\n");
                } else if (chargingSpeed == 0) {
                    // Setting cool down values based on battery temperature
                    if (battTemp >= 39.5 && coolDown != 2 && coolDown == 0) {
                        Utils.writeValue(cool_down, "2");
                        Log.d("DeviceSettings", "Battery Temperature: " + battTemp + "\n" + "Battery Capacity: " + battCap + "%" + "\n" + "Applied cool down");
                    } else if (battTemp <= 38.5 && coolDown != 0 && coolDown == 2) {
                        Utils.writeValue(cool_down, "0");
                        Log.d("DeviceSettings", "Battery Temperature: " + battTemp + "\n" + "Battery Capacity: " + battCap + "%" + "\n" + "No cool down applied");
                    }
                }
            }

            // Charging limit based on user selected battery percentage
            if (((userSelectedChargingLimit == battCap) || (userSelectedChargingLimit < battCap)) && chargingLimit != 0) {
                if (isCoolDownAvailable()) Utils.writeValue(cool_down, "0");
                Utils.writeValue(mmi_charging_enable, "0");
                Log.d("DeviceSettings", "Battery Temperature: " + battTemp + ", Battery Capacity: " + battCap + "%, " + "User selected charging limit: " + userSelectedChargingLimit + "%. Stopped charging");
            } else if (userSelectedChargingLimit > battCap && chargingLimit != 1) {
                Utils.writeValue(mmi_charging_enable, "1");
                Log.d("DeviceSettings", "Charging...");
            }
        }
    };
    public BroadcastReceiver mconnectionInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int battCap = Integer.parseInt(Utils.readLine(battery_capacity));
            if (intent.getAction() == Intent.ACTION_POWER_CONNECTED) {
                if (!mconnectionInfoReceiver) {
                    IntentFilter batteryInfo = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    context.getApplicationContext().registerReceiver(mBatteryInfo, batteryInfo);
                    mconnectionInfoReceiver = true;
                }
                Log.d("DeviceSettings", "Charger/USB Connected");
            } else if (intent.getAction() == Intent.ACTION_POWER_DISCONNECTED) {
                if (sharedPreferences.getBoolean("reset_stats", false) && sharedPreferences.getInt("seek_bar", 95) == battCap)
                    resetStats();
                if (mconnectionInfoReceiver) {
                    context.getApplicationContext().unregisterReceiver(mBatteryInfo);
                    mconnectionInfoReceiver = false;
                }
                Log.d("DeviceSettings", "Charger/USB Disconnected");
            }
        }
    };

    public static boolean isCoolDownAvailable() {
        return Utils.fileWritable(cool_down);
    }

    public static void resetStats() {
        try {
            Runtime.getRuntime().exec("dumpsys batterystats --reset");
            Thread.sleep(1000);
        } catch (Exception e) {
            Log.e("DeviceSettings", "SmartChargingService: " + e.toString());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        IntentFilter connectionInfo = new IntentFilter();
        connectionInfo.addAction(Intent.ACTION_POWER_CONNECTED);
        connectionInfo.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(mconnectionInfo, connectionInfo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mconnectionInfo);
        if (mconnectionInfoReceiver) getApplicationContext().unregisterReceiver(mBatteryInfo);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
