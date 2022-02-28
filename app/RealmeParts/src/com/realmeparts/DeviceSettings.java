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

    public static final String KEY_SRGB_SWITCH = "srgb";
    public static final String KEY_HBM_SWITCH = "hbm";
    public static final String KEY_DC_SWITCH = "dc";
    public static final String KEY_OTG_SWITCH = "otg";
    public static final String KEY_PERF_PROFILE = "perf_profile";
    public static final String PERF_PROFILE_SYSTEM_PROPERTY = "persist.perf_profile";
    public static final String KEY_CHARGING_SWITCH = "smart_charging";
    public static final String KEY_CHARGING_SPEED = "charging_speed";
    public static final String KEY_RESET_STATS = "reset_stats";
    public static final String KEY_CABC = "cabc";
    public static final String CABC_SYSTEM_PROPERTY = "persist.cabc_profile";
    public static final String KEY_FPS_INFO = "fps_info";
    public static final String KEY_SETTINGS_PREFIX = "device_setting_";
    public static final String TP_DIRECTION = "/proc/touchpanel/oppo_tp_direction";
    private static final String ProductName = Utils.ProductName();
    private static final String KEY_CATEGORY_CHARGING = "charging";
    private static final String KEY_CATEGORY_GRAPHICS = "graphics";
    private static final String KEY_CATEGORY_REFRESH_RATE = "refresh_rate";
    public static SecureSettingListPreference mChargingSpeed;
    public static TwoStatePreference mResetStats;
    public static TwoStatePreference mRefreshRate90Forced;
    public static RadioButtonPreference mRefreshRate90;
    public static RadioButtonPreference mRefreshRate60;
    public static SeekBarPreference mSeekBarPreference;
    public static DisplayManager mDisplayManager;
    private static NotificationManager mNotificationManager;
    public PreferenceCategory mPreferenceCategory;
    private TwoStatePreference mDCModeSwitch;
    private TwoStatePreference mSRGBModeSwitch;
    private TwoStatePreference mHBMModeSwitch;
    private TwoStatePreference mOTGModeSwitch;
    private TwoStatePreference mGameModeSwitch;
    private TwoStatePreference mSmartChargingSwitch;
    private SwitchPreference mFpsInfo;
    private boolean CABC_DeviceMatched;
    private boolean DC_DeviceMatched;
    private boolean HBM_DeviceMatched;
    private boolean sRGB_DeviceMatched;
    private SecureSettingListPreference mCABC;
    private SecureSettingListPreference mPerfProfile;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        prefs.edit().putString("ProductName", ProductName).apply();

        addPreferencesFromResource(R.xml.main);

        mDCModeSwitch = findPreference(KEY_DC_SWITCH);
        mDCModeSwitch.setEnabled(DCModeSwitch.isSupported());
        mDCModeSwitch.setChecked(DCModeSwitch.isCurrentlyEnabled(this.getContext()));
        mDCModeSwitch.setOnPreferenceChangeListener(new DCModeSwitch());

        mSRGBModeSwitch = findPreference(KEY_SRGB_SWITCH);
        mSRGBModeSwitch.setEnabled(SRGBModeSwitch.isSupported());
        mSRGBModeSwitch.setChecked(SRGBModeSwitch.isCurrentlyEnabled(this.getContext()));
        mSRGBModeSwitch.setOnPreferenceChangeListener(new SRGBModeSwitch());

        mHBMModeSwitch = (TwoStatePreference) findPreference(KEY_HBM_SWITCH);
        mHBMModeSwitch.setEnabled(HBMModeSwitch.isSupported());
        mHBMModeSwitch.setChecked(HBMModeSwitch.isCurrentlyEnabled(this.getContext()));
        mHBMModeSwitch.setOnPreferenceChangeListener(new HBMModeSwitch(getContext()));

        mOTGModeSwitch = (TwoStatePreference) findPreference(KEY_OTG_SWITCH);
        mOTGModeSwitch.setEnabled(OTGModeSwitch.isSupported());
        mOTGModeSwitch.setChecked(OTGModeSwitch.isCurrentlyEnabled(this.getContext()));
        mOTGModeSwitch.setOnPreferenceChangeListener(new OTGModeSwitch());

        mSmartChargingSwitch = findPreference(KEY_CHARGING_SWITCH);
        mSmartChargingSwitch.setChecked(prefs.getBoolean(KEY_CHARGING_SWITCH, false));
        mSmartChargingSwitch.setOnPreferenceChangeListener(new SmartChargingSwitch(getContext()));

        mChargingSpeed = findPreference(KEY_CHARGING_SPEED);
        mChargingSpeed.setEnabled(mSmartChargingSwitch.isChecked());
        mChargingSpeed.setOnPreferenceChangeListener(this);

        mResetStats = findPreference(KEY_RESET_STATS);
        mResetStats.setChecked(prefs.getBoolean(KEY_RESET_STATS, false));
        mResetStats.setEnabled(mSmartChargingSwitch.isChecked());
        mResetStats.setOnPreferenceChangeListener(this);

        mSeekBarPreference = findPreference("seek_bar");
        mSeekBarPreference.setEnabled(mSmartChargingSwitch.isChecked());
        SeekBarPreference.mProgress = prefs.getInt("seek_bar", 95);

        mRefreshRate90Forced = findPreference("refresh_rate_90Forced");
        mRefreshRate90Forced.setChecked(prefs.getBoolean("refresh_rate_90Forced", false));
        mRefreshRate90Forced.setOnPreferenceChangeListener(new RefreshRateSwitch(getContext()));

        mRefreshRate90 = findPreference("refresh_rate_90");
        mRefreshRate90.setChecked(RefreshRateSwitch.isCurrentlyEnabled(this.getContext()));
        mRefreshRate90.setOnPreferenceChangeListener(new RefreshRateSwitch(getContext()));

        mRefreshRate60 = findPreference("refresh_rate_60");
        mRefreshRate60.setChecked(!RefreshRateSwitch.isCurrentlyEnabled(this.getContext()));
        mRefreshRate60.setOnPreferenceChangeListener(new RefreshRateSwitch(getContext()));

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

        // Few checks to enable/disable options when activity is launched
        if ((prefs.getBoolean("refresh_rate_90", false) && prefs.getBoolean("refresh_rate_90Forced", false))) {
            mRefreshRate60.setEnabled(false);
            mRefreshRate90.setEnabled(false);
        } else if ((prefs.getBoolean("refresh_rate_60", false))) {
            mRefreshRate90Forced.setEnabled(false);
        }

        isCoolDownAvailable();
        DisplayRefreshRateModes();
        try {
            ParseJson();
        } catch (Exception e) {
            Log.d("DeviceSettings", e.toString());
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mRefreshRate90) {
            mRefreshRate60.setChecked(false);
            mRefreshRate90.setChecked(true);
            mRefreshRate90Forced.setEnabled(true);
            return true;
        } else if (preference == mRefreshRate60) {
            mRefreshRate60.setChecked(true);
            mRefreshRate90.setChecked(false);
            mRefreshRate90Forced.setEnabled(false);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
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

        if (preference == mChargingSpeed) {
            mChargingSpeed.setValue((String) newValue);
            mChargingSpeed.setSummary(mChargingSpeed.getEntry());
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

    // Remove Charging Speed preference if cool_down node is unavailable
    private void isCoolDownAvailable() {
        mPreferenceCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_CHARGING);

        if (Utils.fileWritable(SmartChargingService.mmi_charging_enable)) {
            if (!Utils.fileWritable(SmartChargingService.cool_down)) {
                mPreferenceCategory.removePreference(findPreference(KEY_CHARGING_SPEED));
            }
        } else {
            getPreferenceScreen().removePreference(mPreferenceCategory);
        }
    }

    // Remove display refresh rate modes category if display doesn't support 90hz
    private void DisplayRefreshRateModes() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String refreshRate = "";
        mDisplayManager = (DisplayManager) this.getContext().getSystemService(Context.DISPLAY_SERVICE);
        Display.Mode[] DisplayModes = mDisplayManager.getDisplay(Display.DEFAULT_DISPLAY).getSupportedModes();
        for (Display.Mode mDisplayMode : DisplayModes) {
            DecimalFormat df = new DecimalFormat("0.##");
            refreshRate += df.format(mDisplayMode.getRefreshRate()) + "Hz, ";
        }
        Log.d("DeviceSettings", "Device supports " + refreshRate + "refresh rate modes");

        if (!refreshRate.contains("90")) {
            prefs.edit().putBoolean("refresh_rate_90_device", false).apply();
            mPreferenceCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_REFRESH_RATE);
            getPreferenceScreen().removePreference(mPreferenceCategory);
        } else prefs.edit().putBoolean("refresh_rate_90_device", true).apply();
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

        JSONArray DC = jsonOB.getJSONArray(KEY_DC_SWITCH);
        for (int i = 0; i < DC.length(); i++) {
            if (ProductName.toUpperCase().contains(DC.getString(i))) {
                {
                    DC_DeviceMatched = true;
                }
            }
        }

        JSONArray HBM = jsonOB.getJSONArray(KEY_HBM_SWITCH);
        for (int i = 0; i < HBM.length(); i++) {
            if (ProductName.toUpperCase().contains(HBM.getString(i))) {
                {
                    HBM_DeviceMatched = true;
                }
            }
        }

        JSONArray sRGB = jsonOB.getJSONArray(KEY_SRGB_SWITCH);
        for (int i = 0; i < sRGB.length(); i++) {
            if (ProductName.toUpperCase().contains(DC.getString(i))) {
                {
                    sRGB_DeviceMatched = true;
                }
            }
        }

        // Remove CABC preference if device is unsupported
        if (!CABC_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_CABC));
            prefs.edit().putBoolean("CABC_DeviceMatched", false).apply();
        } else prefs.edit().putBoolean("CABC_DeviceMatched", true).apply();

        // Remove DC-Dimming preference if device is unsupported
        if (!DC_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_DC_SWITCH));
            prefs.edit().putBoolean("DC_DeviceMatched", false).apply();
        } else prefs.edit().putBoolean("DC_DeviceMatched", true).apply();

        // Remove HBM preference if device is unsupported
        if (!HBM_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_HBM_SWITCH));
            prefs.edit().putBoolean("HBM_DeviceMatched", false).apply();
        } else prefs.edit().putBoolean("HBM_DeviceMatched", true).apply();

        // Remove sRGB preference if device is unsupported
        if (!sRGB_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_SRGB_SWITCH));
            prefs.edit().putBoolean("sRGB_DeviceMatched", false).apply();
        } else prefs.edit().putBoolean("sRGB_DeviceMatched", true).apply();
    }
}
