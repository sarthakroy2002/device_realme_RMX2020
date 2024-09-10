#!/bin/bash
#
# SPDX-FileCopyrightText: 2016 The CyanogenMod Project
# SPDX-FileCopyrightText: 2017-2024 The LineageOS Project
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

function blob_fixup() {
    case "${1}" in
        vendor/lib*/hw/audio.primary.mt6768.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libmedia_helper.so" "libmedia_helper-v30.so" "${2}"
            "${PATCHELF}" --replace-needed "libalsautils.so" "libalsautils-mtk.so" "${2}"
            ;;
        vendor/lib*/hw/audio.usb.mt6768.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libalsautils.so" "libalsautils-mtk.so" "${2}"
            ;;
        vendor/lib64/hw/android.hardware.camera.provider@2.6-impl-mediatek.so)
            [ "$2" = "" ] && return 0
            grep -q libshim_camera_metadata.so "$2" || patchelf --add-needed libshim_camera_metadata.so "$2"
            ;;
        vendor/etc/init/android.hardware.bluetooth@1.0-service-mediatek.rc)
            [ "$2" = "" ] && return 0
            sed -i '/vts/Q' "$2"
            ;;
        vendor/lib*/libmtkcam_stdutils.so \
        |vendor/lib64/hw/dfps.mt6768.so \
        |vendor/lib64/hw/vendor.mediatek.hardware.pq@2.6-impl.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libutils.so" "libutils-v30.so" "${2}"
            ;;
        vendor/lib/libMtkOmxCore.so)
            [ "$2" = "" ] && return 0
            sed -i "s/mtk.vendor.omx.core.log/ro.vendor.mtk.omx.log\x00\x00/" "$2"
            ;;
        vendor/lib/libMtkOmxVdecEx.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libui.so" "libui-v32.so" "$2"
            sed -i "s/ro.mtk_crossmount_support/ro.vendor.mtk_crossmount\x00/" "$2"
            sed -i "s/ro.mtk_deinterlace_support/ro.vendor.mtk_deinterlace\x00/" "$2"
            ;;
        vendor/lib64/hw/android.hardware.thermal@2.0-impl.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libutils.so" "libutils-v32.so" "$2"
            ;;
        vendor/lib64/libmtkcam_featurepolicy.so)
            [ "$2" = "" ] && return 0
            # evaluateCaptureConfiguration()
            sed -i "s/\x34\xE8\x87\x40\xB9/\x34\x28\x02\x80\x52/" "$2"
            ;;
        vendor/lib64/libwvhidl.so \ 
        |vendor/lib64/mediadrm/libwvdrmengine.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libprotobuf-cpp-lite-3.9.1.so" "libprotobuf-cpp-full-3.9.1.so" "${2}"
            ;;
        vendor/bin/mnld \
        |vendor/lib64/libaalservice.so \
        |vendor/lib64/libcam.utils.sensorprovider.so)
            [ "$2" = "" ] && return 0
            grep -q "libshim_sensors.so" "$2" || "$PATCHELF" --add-needed "libshim_sensors.so" "$2"
            ;;
        vendor/lib64/libwifi-hal-mtk.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --set-soname "libwifi-hal-mtk.so" "${2}"
            ;;
        system_ext/lib64/libsource.so)
            [ "$2" = "" ] && return 0
            grep -q libui_shim.so "$2" || "$PATCHELF" --add-needed libui_shim.so "$2"
            ;;
        *)
            return 1
            ;;
    esac

    return 0
}

function blob_fixup_dry() {
    blob_fixup "$1" ""
}

# Initialize the helper
setup_vendor "${DEVICE}" "${VENDOR}" "${ANDROID_ROOT}" false "${CLEAN_VENDOR}"

extract "${MY_DIR}/proprietary-files.txt" "${SRC}" "${KANG}" --section "${SECTION}"

"${MY_DIR}/setup-makefiles.sh"