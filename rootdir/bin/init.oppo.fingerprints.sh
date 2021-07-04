#!/system/bin/sh

chown system system /sys/bus/platform/devices/soc/soc\:fpc_interrupt/clk_enable
chown system system /sys/bus/platform/devices/soc/soc\:fpc_interrupt/wakelock_enable
chown system system /sys/bus/platform/devices/soc/soc\:fpc_interrupt/irq
chown system system /sys/bus/platform/devices/soc/soc\:fpc_interrupt/irq_enable
chmod 0200 /sys/bus/platform/devices/soc/soc\:fpc_interrupt/irq_enable
chmod 0200 /sys/bus/platform/devices/soc/soc\:fpc_interrupt/clk_enable
chmod 0200 /sys/bus/platform/devices/soc/soc\:fpc_interrupt/wakelock_enable
chmod 0600 /sys/bus/platform/devices/soc/soc\:fpc_interrupt/irq

#add for silead
chown system:system /dev/silead_fp
chmod 0666 /dev/silead_fp