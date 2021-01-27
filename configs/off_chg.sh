#!/sbin/sh
#
# Fix offline charge

rm /system_root/system/bin/charger
mv /tmp/install/bin/charger /system_root/system/bin/charger
chmod +x /system_root/system/bin/charger
