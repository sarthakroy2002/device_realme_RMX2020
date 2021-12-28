/*
 * Copyright (C) 2018 The OmniROM Project
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

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

@TargetApi(24)
public class HBMModeTileService extends TileService {
    private boolean enabled = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPrefs.getBoolean("HBM_DeviceMatched", false)) {
            getQsTile().setState(Tile.STATE_UNAVAILABLE);
            getQsTile().setLabel(getResources().getString(R.string.unsupported));
        } else {
            enabled = HBMModeSwitch.isCurrentlyEnabled(this);
            getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            getQsTile().setLabel(getResources().getString(R.string.tile_hbm_mode));
        }
        getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        enabled = HBMModeSwitch.isCurrentlyEnabled(this);
        Utils.writeValue(HBMModeSwitch.getFile(), enabled ? "0" : "1");
        HBMModeSwitch.TriggerService(!enabled, this);
        sharedPrefs.edit().putBoolean(DeviceSettings.KEY_HBM_SWITCH, !enabled).apply();
        //getQsTile().setLabel(enabled ? "HBM off" : "HBM On");
        getQsTile().setState(enabled ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }
}
