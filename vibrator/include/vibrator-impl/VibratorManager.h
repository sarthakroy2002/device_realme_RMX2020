/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma once

#include <aidl/android/hardware/vibrator/BnVibratorManager.h>

namespace aidl {
namespace android {
namespace hardware {
namespace vibrator {

class VibratorManager : public BnVibratorManager {
  public:
    VibratorManager(std::shared_ptr<IVibrator> vibrator) : mDefaultVibrator(std::move(vibrator)){};
    ndk::ScopedAStatus getCapabilities(int32_t* _aidl_return) override;
    ndk::ScopedAStatus getVibratorIds(std::vector<int32_t>* _aidl_return) override;
    ndk::ScopedAStatus getVibrator(int32_t vibratorId,
                                   std::shared_ptr<IVibrator>* _aidl_return) override;
    ndk::ScopedAStatus prepareSynced(const std::vector<int32_t>& vibratorIds) override;
    ndk::ScopedAStatus triggerSynced(const std::shared_ptr<IVibratorCallback>& callback) override;
    ndk::ScopedAStatus cancelSynced() override;

  private:
    std::shared_ptr<IVibrator> mDefaultVibrator;
};

}  // namespace vibrator
}  // namespace hardware
}  // namespace android
}  // namespace aidl
