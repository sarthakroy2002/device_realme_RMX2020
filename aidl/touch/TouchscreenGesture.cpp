/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "TouchscreenGestureService"

#include "TouchscreenGesture.h"
#include <android-base/logging.h>
#include <fstream>

namespace aidl {
namespace vendor {
namespace lineage {
namespace touch {

const std::map<int32_t, TouchscreenGesture::GestureInfo> TouchscreenGesture::kGestureInfoMap = {
    {0, {251, "Two fingers down swipe", "/proc/touchpanel/double_swipe_enable"}},
    {1, {255, "Up arrow", "/proc/touchpanel/down_arrow_enable"}},
    {2, {252, "Down arrow", "/proc/touchpanel/up_arrow_enable"}},
    {3, {254, "Left arrow", "/proc/touchpanel/right_arrow_enable"}},
    {4, {253, "Right arrow", "/proc/touchpanel/left_arrow_enable"}},
    {5, {64, "One finger up swipe", "/proc/touchpanel/down_swipe_enable"}},
    {6, {66, "One finger down swipe", "/proc/touchpanel/up_swipe_enable"}},
    {7, {65, "One finger left swipe", "/proc/touchpanel/right_swipe_enable"}},
    {8, {63, "One finger right swipe", "/proc/touchpanel/left_swipe_enable"}},
    {9, {247, "Letter M", "/proc/touchpanel/letter_m_enable"}},
    {10, {250, "Letter O", "/proc/touchpanel/letter_o_enable"}},
    {11, {246, "Letter W", "/proc/touchpanel/letter_w_enable"}},
};

ndk::ScopedAStatus TouchscreenGesture::getSupportedGestures(std::vector<Gesture>* _aidl_return) {
    std::vector<Gesture> gestures;

    for (const auto& entry : kGestureInfoMap) {
        gestures.push_back({entry.first, entry.second.name, entry.second.keycode});
    }
    *_aidl_return = gestures;

    return ndk::ScopedAStatus::ok();
}

ndk::ScopedAStatus TouchscreenGesture::setGestureEnabled(const Gesture& gesture, bool enabled) {
    const auto entry = kGestureInfoMap.find(gesture.id);
    if (entry == kGestureInfoMap.end()) {
        return ndk::ScopedAStatus::fromExceptionCode(EX_UNSUPPORTED_OPERATION);
    }

    std::ofstream file(entry->second.path);
    file << (enabled ? "1" : "0");
    LOG(DEBUG) << "Wrote file " << entry->second.path << " fail " << file.fail();
    if (file.fail()) return ndk::ScopedAStatus::fromExceptionCode(EX_ILLEGAL_STATE);

    return ndk::ScopedAStatus::ok();
}

}  // namespace touch
}  // namespace lineage
}  // namespace vendor
}  // namespace aidl
