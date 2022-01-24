#!/system/bin/sh
#
# This script is Needed to replace the FP hal for egis devices
# Fix by @sarthakroy2002
#

mount -o rw /vendor

FP=$(cat /proc/fp_id)

if [ $FP = "E_520" ]; then
    echo "EGIS FP Detected"
    cp -r /vendor/fptmp/* /vendor/bin/hw/
fi 

rm -rf /vendor/fptmp
