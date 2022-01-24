/*
 * Copyright (C) 2016 The OmniROM Project
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
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

public class RefreshRateSwitch implements OnPreferenceChangeListener {

    private static final IBinder SF = ServiceManager.getService("SurfaceFlinger");
    public static int setRefreshRate;
    private final Context mContext;

    public RefreshRateSwitch(Context context) {
        mContext = context;
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Settings.System.getFloat(context.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), 90f) == 90f;
    }

    public static void setPeakRefresh(Context context, boolean enabled) {
        Settings.System.putFloat(context.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), enabled ? 90f : 60f);
        Settings.System.putFloat(context.getContentResolver(), "MIN_REFRESH_RATE".toLowerCase(), enabled ? 90f : 60f);
    }

    public static void setForcedRefreshRate(int value) {
        Parcel Info = Parcel.obtain();
        Info.writeInterfaceToken("android.ui.ISurfaceComposer");
        Info.writeInt(value);
        try {
            SF.transact(1035, Info, null, 0);
        } catch (RemoteException e) {
            Log.e("DeviceSettings", e.toString());
        } finally {
            Info.recycle();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;

        if (preference == DeviceSettings.mRefreshRate90 && enabled) {
            setRefreshRate = 1;
        } else if (preference == DeviceSettings.mRefreshRate60 && enabled) {
            setRefreshRate = 0;
        } else if (preference == DeviceSettings.mRefreshRate90Forced && enabled) {
            DeviceSettings.mRefreshRate60.setEnabled(false);
            DeviceSettings.mRefreshRate90.setEnabled(false);
            setRefreshRate = 2;
        } else if (preference == DeviceSettings.mRefreshRate90Forced && !enabled) {
            DeviceSettings.mRefreshRate60.setEnabled(true);
            DeviceSettings.mRefreshRate90.setEnabled(true);
            setRefreshRate = 3;
        }

        switch (setRefreshRate) {
            case 1:
                Settings.System.putFloat(mContext.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), 90f);
                Settings.System.putFloat(mContext.getContentResolver(), "MIN_REFRESH_RATE".toLowerCase(), 90f);
                break;
            case 0:
                Settings.System.putFloat(mContext.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), 60f);
                Settings.System.putFloat(mContext.getContentResolver(), "MIN_REFRESH_RATE".toLowerCase(), 60f);
                break;
            case 2:
                setForcedRefreshRate(0);
                break;
            case 3:
                setForcedRefreshRate(-1);
                break;
        }
        return true;
    }
}
