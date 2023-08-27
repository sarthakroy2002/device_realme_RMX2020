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

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

public class DeviceSettings extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String KEY_OTG_SWITCH = "otg";
    public static final String KEY_PERF_PROFILE = "perf_profile";
    public static final String PERF_PROFILE_SYSTEM_PROPERTY = "persist.perf_profile";
    public static final String KEY_CABC = "cabc";
    public static final String CABC_SYSTEM_PROPERTY = "persist.cabc_profile";
    public static final String KEY_FPS_INFO = "fps_info";
    public static final String KEY_SETTINGS_PREFIX = "device_setting_";
    public static final String TP_DIRECTION = "/proc/touchpanel/oppo_tp_direction";
    private static final String ProductName = Utils.ProductName();
    private static final String KEY_CATEGORY_GRAPHICS = "graphics";
    public static DisplayManager mDisplayManager;
    private static NotificationManager mNotificationManager;
    public PreferenceCategory mPreferenceCategory;
    private TwoStatePreference mOTGModeSwitch;
    private SwitchPreference mFpsInfo;
    private boolean CABC_DeviceMatched;
    private SecureSettingListPreference mCABC;
    private SecureSettingListPreference mPerfProfile;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        prefs.edit().putString("ProductName", ProductName).apply();

        addPreferencesFromResource(R.xml.main);

        mOTGModeSwitch = (TwoStatePreference) findPreference(KEY_OTG_SWITCH);
        mOTGModeSwitch.setEnabled(OTGModeSwitch.isSupported());
        mOTGModeSwitch.setChecked(OTGModeSwitch.isCurrentlyEnabled(this.getContext()));
        mOTGModeSwitch.setOnPreferenceChangeListener(new OTGModeSwitch());

        mFpsInfo = findPreference(KEY_FPS_INFO);
        mFpsInfo.setChecked(prefs.getBoolean(KEY_FPS_INFO, false));
        mFpsInfo.setOnPreferenceChangeListener(this);

        mCABC = (SecureSettingListPreference) findPreference(KEY_CABC);
        mCABC.setValue(Utils.getStringProp(CABC_SYSTEM_PROPERTY, "0"));
        mCABC.setSummary(mCABC.getEntry());
        mCABC.setOnPreferenceChangeListener(this);

        mPerfProfile = (SecureSettingListPreference) findPreference(KEY_PERF_PROFILE);
        mPerfProfile.setValue(Utils.getStringProp(PERF_PROFILE_SYSTEM_PROPERTY, "0"));
        mPerfProfile.setSummary(mPerfProfile.getEntry());
        mPerfProfile.setOnPreferenceChangeListener(this);

        try {
            ParseJson();
        } catch (Exception e) {
            Log.d("DeviceSettings", e.toString());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFpsInfo) {
            boolean enabled = (Boolean) newValue;
            if (enabled) {
                Utils.startService(this.getContext(), com.realmeparts.FPSInfoService.class);
            } else {
                Utils.stopService(this.getContext(), com.realmeparts.FPSInfoService.class);
            }
        }

        if (preference == mCABC) {
            mCABC.setValue((String) newValue);
            mCABC.setSummary(mCABC.getEntry());
            Utils.setStringProp(CABC_SYSTEM_PROPERTY, (String) newValue);
        }

        if (preference == mPerfProfile) {
            mPerfProfile.setValue((String) newValue);
            mPerfProfile.setSummary(mPerfProfile.getEntry());
            Utils.setStringProp(PERF_PROFILE_SYSTEM_PROPERTY, (String) newValue);
        }
        return true;
    }

    private void ParseJson() throws JSONException {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        mPreferenceCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_GRAPHICS);
        String features_json = Utils.InputStreamToString(getResources().openRawResource(R.raw.realmeparts_features));
        JSONObject jsonOB = new JSONObject(features_json);

        JSONArray CABC = jsonOB.getJSONArray(KEY_CABC);
        for (int i = 0; i < CABC.length(); i++) {
            if (ProductName.toUpperCase().contains(CABC.getString(i))) {
                {
                    CABC_DeviceMatched = true;
                }
            }
        }

        // Remove CABC preference if device is unsupported
        if (!CABC_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_CABC));
            prefs.edit().putBoolean("CABC_DeviceMatched", false).apply();
        } else prefs.edit().putBoolean("CABC_DeviceMatched", true).apply();

    }
}
