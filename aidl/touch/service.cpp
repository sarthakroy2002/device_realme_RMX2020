/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "vendor.lineage.touch-service.RMX2020"

#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>
#include "TouchscreenGesture.h"

using aidl::vendor::lineage::touch::TouchscreenGesture;

int main() {
    ABinderProcess_setThreadPoolMaxThreadCount(0);

    std::shared_ptr<TouchscreenGesture> tg = ndk::SharedRefBase::make<TouchscreenGesture>();
    binder_status_t status = AServiceManager_addService(
            tg->asBinder().get(), TouchscreenGesture::makeServiceName("default").c_str());
    CHECK_EQ(status, STATUS_OK) << "Cannot register touchscreen gesture HAL service.";

    LOG(INFO) << "Touchscreen HAL service ready.";

    ABinderProcess_joinThreadPool();

    LOG(ERROR) << "Touchscreen HAL service failed to join thread pool.";
    return EXIT_FAILURE;  // should not reach
}
