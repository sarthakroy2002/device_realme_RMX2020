/*
 * Copyright (C) 2017 The LineageOS jProject
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <hidl/HidlTransportSupport.h>
#include "Usb.h"

using android::sp;

// libhwbinder:
using android::hardware::configureRpcThreadpool;
using android::hardware::joinRpcThreadpool;

// Generated HIDL files
using android::hardware::usb::V1_0::IUsb;
using android::hardware::usb::V1_0::implementation::Usb;

using android::status_t;
using android::OK;

int main() {
    android::sp<IUsb> service = new Usb();

    configureRpcThreadpool(1, true /*callerWillJoin*/);
    status_t status = service->registerAsService();

    if (status != OK) {
        ALOGE("Cannot register USB HAL service");
        return 1;
    }

    ALOGI("USB HAL Ready.");
    joinRpcThreadpool();
    // Under noraml cases, execution will not reach this line.
    ALOGI("USB HAL failed to join thread pool.");
    return 1;
}
