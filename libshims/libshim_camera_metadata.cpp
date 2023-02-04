/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <dlfcn.h>
#include <system/camera_metadata.h>
#include <vector>

extern "C" int add_camera_metadata_entry(camera_metadata_t* dst, uint32_t tag, const void* data,
                                         size_t data_count) {
    static auto add_camera_metadata_entry_orig =
            reinterpret_cast<typeof(add_camera_metadata_entry)*>(
                    dlsym(RTLD_NEXT, "add_camera_metadata_entry"));

    if (tag == ANDROID_REQUEST_AVAILABLE_CAPABILITIES) {
        std::vector<uint8_t> caps;
        auto u8 = reinterpret_cast<const uint8_t*>(data);

        for (size_t i = 0; i < data_count; i++) {
            if (u8[i] != ANDROID_REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA) {
                caps.emplace_back(u8[i]);
            }
        }

        return add_camera_metadata_entry_orig(dst, tag, caps.data(), caps.size());
    }

    return add_camera_metadata_entry_orig(dst, tag, data, data_count);
}
