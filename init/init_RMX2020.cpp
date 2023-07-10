/*
   Copyright (c) 2014, The Linux Foundation. All rights reserved.
   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of The Linux Foundation nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.
   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
   WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
   ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
   SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
   BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
   OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
   IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <stdlib.h>
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>
#include <sys/sysinfo.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <vector>

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/strings.h>

#include <android-base/properties.h>
#include "vendor_init.h"

#include <fs_mgr_dm_linear.h>

using android::base::GetProperty;
using android::base::ReadFileToString;

std::vector<std::string> ro_props_default_source_order = {
    "",
    "odm.",
    "system.",
    "product.",
    "system_ext.",
    "vendor.",
    "vendor_dlkm.",
};

void property_override(char const prop[], char const value[])
{
    prop_info *pi;

    pi = (prop_info*) __system_property_find(prop);
    if (pi)
        __system_property_update(pi, value, strlen(value));
    else
        __system_property_add(prop, strlen(prop), value, strlen(value));
}

void set_device_props(const std::string device, const std::string model,
        const std::string name, const std::string marketname) {
    const auto set_ro_product_prop = [](const std::string &source,
                                        const std::string &prop,
                                        const std::string &value) {
        auto prop_name = "ro.product." + source + prop;
        property_override(prop_name.c_str(), value.c_str());
    };

    for (const auto &source : ro_props_default_source_order) {
        set_ro_product_prop(source, "device", device);
        set_ro_product_prop(source, "model", model);
        set_ro_product_prop(source, "name", name);
        set_ro_product_prop(source, "marketname", marketname);
    }
}

void init_opperator_name_properties()
{
    char const *opperator_name_file = "/proc/oppoVersion/operatorName";
    std::string opperator_name;

    if (ReadFileToString(opperator_name_file, &opperator_name)) {
        /*
         * Setup ro.separate.soft value to their OPPO project version
         * For current OPPO project version, here the following mapping:
         *
         * 101 -> NON NFC
         * 111 -> NON NFC
         * 112 -> NON NFC
         * 113 -> NFC
         * 114 -> NON NFC
         * 115 -> NON NFC
         * 116 -> NON NFC
         * 117 -> NON NFC
         * 121 -> NON NFC
         * 122 -> NFC
         * 123 -> NON NFC
         * 124 -> NON NFC
         * 125 -> NON NFC
         * 126 -> NON NFC
         */
        if (opperator_name == "113" || opperator_name == "122") {
            property_override("ro.boot.product.hardware.sku", "nfc");
        }
    }
    else {
        LOG(ERROR) << "Unable to read operatorName from " << opperator_name_file;
    }
}

std::string read_cmdline(const std::string& key) {
    std::ifstream cmdline("/proc/cmdline");
    std::string line;
    std::string value;

    if (cmdline.is_open()) {
        while (std::getline(cmdline, line)) {
            std::stringstream ss(line);
            std::string token;

            while (std::getline(ss, token, ' ')) {
                std::string::size_type pos = token.find(key);
                if (pos != std::string::npos) {
                    value = token.substr(pos + key.length() + 1);
                    return value;
                }
            }
        }
    }

    return value;
}

void init_fp_properties()
{
    char const *fp_name_file = "/proc/fp_id";
    std::string fp_name;
    int avail = 0;
    std::string boardid = read_cmdline("board_id");
    std::string boardIds[] ={
        "S98670AA1","S98670BA1","S98670CA1",
	    "S98670GA1","S98670HA1","S98670JA1",
	    "S98670KA1","S98670LA1","S98670MA1",
	    "S98670NA1","S98670PA1","S98670WA1",
	    "S986703A1","S986704A1"
    };

    bool found = false;
    for (int i = 0; i < sizeof(boardIds) / sizeof(boardIds[0]); ++i) {
        if (boardIds[i] == boardid) {
            found = true;
            break;
        }
    }

    if (!found) {
        property_override("persist.vendor.fingerprint.available", "false");
        set_device_props("RMX2027", "RMX2027", "RMX2027", "RMX2027");
        property_override("ro.build.fingerprint", "realme/RMX2027/RMX2027:10/QP1A.190711.020/1651798546:user/release-keys");
        avail = 1;
    }

    if (ReadFileToString(fp_name_file, &fp_name)) {
        if (fp_name == "E_520" && avail == 0) {
            property_override("persist.vendor.fingerprint.fp_id", "E_520");
        }
    }
}

void set_avoid_gfxaccel_config() {
    struct sysinfo sys;
    sysinfo(&sys);

    if (sys.totalram <= 3072ull * 1024 * 1024) {
        // Reduce memory footprint
        property_override("ro.config.avoid_gfx_accel", "true");
    }
}

void vendor_load_properties() {
    set_avoid_gfxaccel_config();
    init_opperator_name_properties();
    init_fp_properties();

#ifdef __ANDROID_RECOVERY__
    std::string buildtype = GetProperty("ro.build.type", "userdebug");
    if (buildtype != "user") {
        property_override("ro.debuggable", "1");
        property_override("ro.adb.secure.recovery", "0");
    }
#endif
}
