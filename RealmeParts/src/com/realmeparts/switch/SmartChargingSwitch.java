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

import android.content.Context;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

public class SmartChargingSwitch implements OnPreferenceChangeListener {

    private static final String FILE = "/sys/class/power_supply/battery/mmi_charging_enable";
    private static Context mContext;

    public SmartChargingSwitch(Context context) {
        mContext = context;
    }

    public static String getFile() {
        if (Utils.fileWritable(FILE)) {
            return FILE;
        }
        return null;
    }

    public static boolean isSupported() {
        return Utils.fileWritable(getFile());
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Utils.getFileValueAsBoolean(getFile(), false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        if (enabled) {
            Utils.startService(mContext, com.realmeparts.SmartChargingService.class);
            DeviceSettings.mSeekBarPreference.setEnabled(true);
            DeviceSettings.mResetStats.setEnabled(true);
            DeviceSettings.mChargingSpeed.setEnabled(true);
        } else {
            Utils.stopService(mContext, com.realmeparts.SmartChargingService.class);
            DeviceSettings.mSeekBarPreference.setEnabled(false);
            DeviceSettings.mResetStats.setEnabled(false);
            DeviceSettings.mChargingSpeed.setEnabled(false);
            Utils.writeValue(FILE, "1");
        }
        //Utils.writeValue(getFile(), enabled ? "1" : "0");
        return true;
    }
}
