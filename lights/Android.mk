LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := android.hardware.light@2.0-service.RMX2001
LOCAL_MODULE_STEM := android.hardware.light@2.0-service-mediatek
LOCAL_MODULE_PATH := $(TARGET_OUT_PRODUCT)/vendor_overlay/$(PRODUCT_EXTRA_VNDK_VERSIONS)/bin
LOCAL_MODULE_RELATIVE_PATH := hw
LOCAL_MODULE_TAGS  := optional

LOCAL_SRC_FILES := \
    service.cpp \
    Light.cpp

LOCAL_SHARED_LIBRARIES := \
    liblog \
    libbase \
    libutils \
    libhidlbase \
    libhardware \
    android.hardware.light@2.0

include $(BUILD_EXECUTABLE)
