DEVICE_PATH := device/realme/RMX2020

# Init scripts
PRODUCT_PACKAGES += \
    parts.rc

# Parts
PRODUCT_PACKAGES += \
    RealmeParts

PRODUCT_COPY_FILES += \
    $(DEVICE_PATH)/app/RealmeParts/init/perf_profile.sh:$(TARGET_COPY_OUT_SYSTEM)/bin/perf_profile.sh
