RealmeParts
-----------
RealmeParts is an open-source Android application written in java. It offers several customization settings while making use of various device-specific nodes on Realme devices.

## Features
RealmeParts offers various features. You can grab a quick view of the features offered by RealmeParts in the GIF below:

<p align="center">
  <img src="assets/RealmeParts.gif">
</p>

Here is a complete detailed list of the features with their caveats and support status.

- **Ambient Display Gestures**:
  - Always on Display (AOD) to keep Ambient Display always on,
  - Pulse notifications on pick up,
  - Hand wave gesture to light up display on hand's wave.

- **CABC Mode**: CABC automatically dims the backlight to the lowest level required for the brightest pixel on the display reducing the overall average power needed by the display and backlight without affecting performance.
> Supported Devices: Realme 3 Pro & 5 Pro

- **DC-Dimming**: DC dimming lowers the actual amount of power powering the display at lower brightness levels. This gives an improvement in battery life.

- **Game Mode**:
  - Boosting touch sampling rate for better input responsiveness,
  - Do-Not-Disturb (DND) toggle to hide intrusive notifications during gaming.
>TODO: Enable Game Mode automatically based on user-selected game packages.

- **HBM Mode**: High Brightness Mode bumps your screen's peak luminance for better visibility outdoors at the cost of increased battery drain.

- **Misc Settings**: 
  - USB-OTG power supply for supported external devices,
  - FPS Overlay toogle to show current FPS

- **Screen Refresh Rate Mode**:
  - Ability to choose between 60Hz or 90Hz,
  - Smooth Display toggle for forcing display refresh rate to 90Hz
>TODO: Add Per-App refresh rate setting

- **Smart Charging**:
  - Cool down based on battery temperature during charging,
  - Max charging limit control,
  - Battery stats reset toggle,
  - Charging speed control.
> Available Charging Speeds: 1.8A | 15W, and default speed

- **sRGB Mode**:  SRGB mode provides accurate SRGB colors without any additional messing with color settings by users.

## Building RealmeParts
RealmeParts is supposed to be built using AOSP's build (make) system. To build it, clone this repository into the `packages/apps/RealmeParts` directory of your OS's source code and include the following lines in your device's makefile:
```
# RealmeParts
$(call inherit-product, packages/apps/RealmeParts/parts.mk)
```
If your device supports CABC feature (Check compability in **Features** section), you need to include an additional `.rc` file to enable it's support. You can do it by including the following lines in your device's makefile:
```
PRODUCT_COPY_FILES += \
    packages/apps/RealmeParts/init/cabc.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/cabc.rc
```
## Development
RealmeParts is licensed and distributed under **The GNU General Public License v3.0**. 

Its lead developers and contributors are:
- [Ajith](https://github.com/4j17h)
- [Karthick Chandran](https://github.com/karthick111)

You can view the complete list of contributors here on [GitHub's Contributors Graph](https://github.com/HyperTeam/packages_apps_RealmeParts/graphs/contributors).

You can open a Pull Request or an Issue in case you want to contribute to this project or raise an issue/request. Contributions are always welcome.