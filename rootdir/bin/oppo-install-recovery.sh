#!/system/bin/sh
if ! applypatch --check EMMC:/dev/block/platform/bootdevice/by-name/recovery:67108864:ff8f5ab1de5df25af359f6e51f361cc6a8048e22; then
  applypatch  \
          --patch /vendor/recovery-from-boot.p \
          --source EMMC:/dev/block/platform/bootdevice/by-name/boot:33554432:645f61465c4f9679e826a79485c29c337eb94ddb \
          --target EMMC:/dev/block/platform/bootdevice/by-name/recovery:67108864:ff8f5ab1de5df25af359f6e51f361cc6a8048e22 && \
      log -t recovery "Installing new oppo recovery image: succeeded" && \
      setprop ro.recovery.updated true || \
      log -t recovery "Installing new oppo recovery image: failed" && \
      setprop ro.recovery.updated false
else
  log -t recovery "Recovery image already installed"
  setprop ro.recovery.updated true
fi
