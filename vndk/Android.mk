LOCAL_PATH := prebuilts/vndk

include $(CLEAR_VARS)
LOCAL_MODULE := libui-v32
LOCAL_SRC_FILES := v32/arm64/arch-arm-armv8-a/shared/vndk-core/libui.so
LOCAL_MODULE_SUFFIX := .so
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_TARGET_ARCH := arm
LOCAL_MODULE_TAGS := optional
LOCAL_CHECK_ELF_FILES := false
LOCAL_VENDOR_MODULE := true
LOCAL_REQUIRED_MODULES := android.hardware.graphics.common-V2-ndk_platform
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := android.hardware.graphics.common-V2-ndk_platform
LOCAL_SHARED_LIBRARIES := android.hardware.graphics.common-V2-ndk
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_TAGS := optional
LOCAL_VENDOR_MODULE := true
include $(BUILD_SHARED_LIBRARY)
