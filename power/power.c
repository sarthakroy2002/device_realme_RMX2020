/*
 * Copyright (C) 2012 The Android Open Source Project
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
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define LOG_TAG "MTKPowerHAL"
#include <utils/Log.h>

#include <hardware/hardware.h>
#include <hardware/power.h>

static void power_init(struct power_module *module)
{
	if(module)
    	ALOGI("power_init");
}

static void power_set_interactive(struct power_module *module, int on)
{
	if(module)
    	ALOGI("power_set_interactive on:%d", on);
}

static void power_set_feature(struct power_module *module, feature_t feature, int state)
{
	if(module)
	ALOGI("power_set_feature feature:%d, state:%d", feature, state);
}

static void power_hint(struct power_module *module, power_hint_t hint,
                       void *data) {

    int param = 0;

    if(data)
	param = *((int *)data);

    switch (hint) {
        case POWER_HINT_SUSTAINED_PERFORMANCE:
			if(module) {
				if(param)
            		ALOGI("POWER_HINT_SUSTAINED_PERFORMANCE, on");
				else
					ALOGI("POWER_HINT_SUSTAINED_PERFORMANCE, off");
			}
            break;
        case POWER_HINT_VR_MODE:
			if(module) {
				if(param)
            		ALOGI("POWER_HINT_VR_MODE, on");
				else
					ALOGI("POWER_HINT_VR_MODE, off");
			}
            break;
        default:
             break;
    }
}

static int power_open(const hw_module_t* module, const char* name,
                    hw_device_t** device)
{
	if(module)
    	ALOGI("%s: enter; name=%s", __FUNCTION__, name);
    int retval = 0; /* 0 is ok; -1 is error */

    if (strcmp(name, POWER_HARDWARE_MODULE_ID) == 0) {
        power_module_t *dev = (power_module_t *)calloc(1,
                sizeof(power_module_t));

        if (dev) {
            /* Common hw_device_t fields */
            dev->common.tag = HARDWARE_DEVICE_TAG;
            dev->common.module_api_version = POWER_MODULE_API_VERSION_0_2;
            dev->common.hal_api_version = HARDWARE_HAL_API_VERSION;

            dev->init = power_init;
            dev->powerHint = power_hint;
            dev->setInteractive = power_set_interactive;
            dev->get_number_of_platform_modes = NULL;
            dev->get_platform_low_power_stats = NULL;
            dev->get_voter_list = NULL;

            *device = (hw_device_t*)dev;
        } else
            retval = -ENOMEM;
    } else {
        retval = -EINVAL;
    }

    ALOGD("%s: exit %d", __FUNCTION__, retval);
    return retval;
}

static struct hw_module_methods_t power_module_methods = {
    .open = power_open,
};

struct power_module HAL_MODULE_INFO_SYM = {
    .common = {
        .tag = HARDWARE_MODULE_TAG,
        .module_api_version = POWER_MODULE_API_VERSION_0_2,
        .hal_api_version = HARDWARE_HAL_API_VERSION,
        .id = POWER_HARDWARE_MODULE_ID,
        .name = "Mediatek Power HAL",
        .author = "The Android Open Source Project",
        .methods = &power_module_methods,
    },

    .init = power_init,
    .setInteractive = power_set_interactive,
    .setFeature = power_set_feature,
    .powerHint = power_hint,
};
