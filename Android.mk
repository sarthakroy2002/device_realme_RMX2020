#
# Copyright (C) 2020 Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)

ifeq ($(TARGET_DEVICE),RMX2020)
include $(call all-makefiles-under,$(LOCAL_PATH))

GATEKEEPER_SYMLINK += $(TARGET_OUT_VENDOR)/lib/hw/gatekeeper.default.so
GATEKEEPER_SYMLINK += $(TARGET_OUT_VENDOR)/lib64/hw/gatekeeper.default.so
$(GATEKEEPER_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@mkdir -p $(dir $@)
	$(hide) ln -sf libSoftGatekeeper.so $@

ALL_DEFAULT_INSTALLED_MODULES += $(GATEKEEPER_SYMLINK)

GATEKEEPER_TRUSTONIC_SYMLINK += $(TARGET_OUT_VENDOR)/lib/hw/gatekeeper.mt6768.so
GATEKEEPER_TRUSTONIC_SYMLINK += $(TARGET_OUT_VENDOR)/lib/hw/gatekeeper.oppo6769.so
GATEKEEPER_TRUSTONIC_SYMLINK += $(TARGET_OUT_VENDOR)/lib64/hw/gatekeeper.mt6768.so
GATEKEEPER_TRUSTONIC_SYMLINK += $(TARGET_OUT_VENDOR)/lib64/hw/gatekeeper.oppo6769.so
$(GATEKEEPER_TRUSTKERNEL_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@mkdir -p $(dir $@)
	$(hide) ln -sf libMcGatekeeper.so $@

ALL_DEFAULT_INSTALLED_MODULES += $(GATEKEEPER_TRUSTONIC_SYMLINK)

VULKAN_SYMLINK += $(TARGET_OUT_VENDOR)/lib/hw/vulkan.mt6768.so
VULKAN_SYMLINK += $(TARGET_OUT_VENDOR)/lib64/hw/vulkan.mt6768.so
$(VULKAN_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@mkdir -p $(dir $@)
	$(hide) ln -sf ../egl/libGLES_mali.so $@

ALL_DEFAULT_INSTALLED_MODULES += $(VULKAN_SYMLINK)

endif
