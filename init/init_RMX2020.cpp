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

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/strings.h>

#include <android-base/properties.h>
#include "vendor_init.h"

#include <fs_mgr_dm_linear.h>

using android::base::GetProperty;
using android::base::ReadFileToString;

void property_override(char const prop[], char const value[])
{
    prop_info *pi;

    pi = (prop_info*) __system_property_find(prop);
    if (pi)
        __system_property_update(pi, value, strlen(value));
    else
        __system_property_add(prop, strlen(prop), value, strlen(value));
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

void init_fp_properties()
{
    char const *fp_name_file = "/proc/fp_id";
    std::string fp_name;

    if (ReadFileToString(fp_name_file, &fp_name)) {
        if (fp_name == "E_520") {
            property_override("persist.vendor.fingerprint.fp_id", "E_520");
        }
    }
}

void vendor_load_properties() {
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
