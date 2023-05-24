#!/bin/bash
#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017-2020 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

set -e

DEVICE=RMX2020
VENDOR=realme

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "${MY_DIR}" ]]; then MY_DIR="${PWD}"; fi

ANDROID_ROOT="${MY_DIR}/../../.."

HELPER="${ANDROID_ROOT}/tools/extract-utils/extract_utils.sh"
if [ ! -f "${HELPER}" ]; then
    echo "Unable to find helper script at ${HELPER}"
    exit 1
fi
source "${HELPER}"

# Default to sanitizing the vendor folder before extraction
CLEAN_VENDOR=true

KANG=
SECTION=

while [ "${#}" -gt 0 ]; do
    case "${1}" in
        -n | --no-cleanup )
                CLEAN_VENDOR=false
                ;;
        -k | --kang )
                KANG="--kang"
                ;;
        -s | --section )
                SECTION="${2}"; shift
                CLEAN_VENDOR=false
                ;;
        * )
                SRC="${1}"
                ;;
    esac
    shift
done

if [ -z "${SRC}" ]; then
    SRC="adb"
fi

function blob_fixup {
    case "$1" in
        lib/libsink.so)
            grep -q "libshim_vtservice.so" "${2}" || patchelf --add-needed "libshim_vtservice.so" "${2}"
            ;;
        vendor/lib*/hw/audio.primary.mt6768.so)
            grep -q "libmedia_helper-v30.so" "${2}" || patchelf --replace-needed "libmedia_helper.so" "libmedia_helper-v30.so" "${2}"
            grep -q "libalsautils-v30.so" "${2}" || patchelf --replace-needed "libalsautils.so" "libalsautils-v30.so" "${2}"
            ;;
        vendor/lib*/hw/audio.usb.mt6768.so)
            grep -q "libalsautils-v30.so" "${2}" || patchelf --replace-needed "libalsautils.so" "libalsautils-v30.so" "${2}"
            ;; 
        vendor/bin/hw/android.hardware.wifi@1.0-service-lazy-mediatek)
            grep -q "libwifi-hal-mtk.so" "${2}" || patchelf --replace-needed "libwifi-hal.so" "libwifi-hal-mtk.so" "${2}"
            ;;
        vendor/bin/hw/android.hardware.wifi@1.0-service-lazy-mediatek)
            ;&
        vendor/bin/hw/hostapd)
            ;&
        vendor/bin/hw/wpa_supplicant)
            grep -q "libcompiler_rt.so" "${2}" || patchelf --add-needed "libcompiler_rt.so" ${2}
            ;;
        vendor/bin/hw/camerasloganserver)
            ;&
        vendor/lib/libmtkcam_stdutils.so)
            ;&
        vendor/lib64/libmtkcam_stdutils.so)
            grep -q "libutils-v29.so" "${2}" || patchelf --replace-needed "libutils.so" "libutils-v29.so" "${2}"
            ;;
        vendor/bin/hw/camerahalserver)
            grep -q "libutils-v30.so" "${2}" || patchelf --replace-needed "libutils.so" "libutils-v30.so" "${2}"
            ;;
        vendor/lib64/hw/android.hardware.camera.provider@2.4-impl-mediatek.so)
            grep -q libshim_camera_metadata.so "$2" || patchelf --add-needed libshim_camera_metadata.so "$2"
            ;;
        vendor/etc/init/android.hardware.bluetooth@1.0-service-mediatek.rc)
            sed -i '/vts/Q' "$2"
            ;;
        vendor/etc/init/vendor.oppo.hardware.biometrics.fingerprint@2.1-service.rc)
            sed -i '/cpuset/Q' "$2"
            ;;
        vendor/lib64/hw/dfps.mt6768.so)
            grep -q "libutils-v30.so" "${2}" || patchelf --replace-needed "libutils.so" "libutils-v30.so" "${2}"
            ;;
        vendor/lib64/hw/vendor.mediatek.hardware.pq@2.3-impl.so)
            grep -q "libutils-v30.so" "${2}" || patchelf --replace-needed "libutils.so" "libutils-v30.so" "${2}"
            ;;
        vendor/lib/libMtkOmxVdecEx.so)
            grep -q "libui-v32.so" "${2}" || patchelf --replace-needed "libui.so" "libui-v32.so" "$2"
            ;;
        vendor/etc/init/android.hardware.drm@1.4-service.widevine.rc)
            ;&
        vendor/etc/init/camerasloganserver.rc)
            sed -i 's|writepid /dev/cpuset/foreground/tasks|task_profiles ProcessCapacityHigh|g' "$2"
            ;;
        vendor/etc/init/camerahalserver.rc)
            sed -i 's|writepid /dev/cpuset/camera-daemon/tasks /dev/stune/top-app/tasks|task_profiles CameraServiceCapacity MaxPerformance|g' "$2"
            ;;
    esac
}

# Initialize the helper
setup_vendor "${DEVICE}" "${VENDOR}" "${ANDROID_ROOT}" false "${CLEAN_VENDOR}"

extract "${MY_DIR}/proprietary-files.txt" "${SRC}" "${KANG}" --section "${SECTION}"

"${MY_DIR}/setup-makefiles.sh"