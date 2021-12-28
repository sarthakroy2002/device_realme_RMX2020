/*
* Copyright (C) 2021 The LineageOS Project
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

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class PerfProfileTileService extends TileService {

    @Override
    public void onStartListening() {
        int currentState = Utils.getintProp(DeviceSettings.PERF_PROFILE_SYSTEM_PROPERTY, 0);

        Tile tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        tile.setLabel(getResources().getStringArray(R.array.perf_profiles)[currentState]);

        tile.updateTile();
        super.onStartListening();
    }

    @Override
    public void onClick() {
        int currentState = Utils.getintProp(DeviceSettings.PERF_PROFILE_SYSTEM_PROPERTY, 0);

        int nextState;
        if (currentState == 2) {
            nextState = 0;
        } else {
            nextState = currentState + 1;
        }

        Tile tile = getQsTile();
        Utils.setintProp(DeviceSettings.PERF_PROFILE_SYSTEM_PROPERTY, nextState);
        tile.setLabel(getResources().getStringArray(R.array.perf_profiles)[nextState]);

        tile.updateTile();
        super.onClick();
    }
}
