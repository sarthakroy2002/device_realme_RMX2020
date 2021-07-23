#!/system/bin/sh
#
#ifdef VENDOR_EDIT
#huacai.zhou@PSW.BSP.kernel.drv, 2018/03/09, adjust zram size according to total ram size
MemTotalStr=`cat /proc/meminfo | grep MemTotal`
MemTotal=${MemTotalStr:16:8}

if [ $MemTotal -le 2097152 ]; then
  #config 1GB zram size with memory less than 2 GB
  echo zstd > /sys/block/zram0/comp_algorithm
  echo 1342177280 > /sys/block/zram0/disksize
  echo 180 > /proc/sys/vm/swappiness
  echo 0 > /proc/sys/vm/direct_swappiness
elif [ $MemTotal -le 3145728 ]; then
  #config 1.6GB zram size with memory less than 3 GB
  echo lz4 > /sys/block/zram0/comp_algorithm
  echo 1717986918 > /sys/block/zram0/disksize
  echo 160 > /proc/sys/vm/swappiness
  echo 60 > /proc/sys/vm/direct_swappiness
elif [ $MemTotal -le 4194304 ]; then
  #config 2GB zram size with memory less than 4 GB
  echo lz4 > /sys/block/zram0/comp_algorithm
  echo 2147483648 > /sys/block/zram0/disksize
  echo 160 > /proc/sys/vm/swappiness
  echo 60 > /proc/sys/vm/direct_swappiness
elif [ $MemTotal -le 6291456 ]; then
  #config 1GB zram size with memory 6 GB
  echo lz4 > /sys/block/zram0/comp_algorithm
  echo 1073741824 > /sys/block/zram0/disksize
  echo 160 > /proc/sys/vm/swappiness
  echo 60 > /proc/sys/vm/direct_swappiness
else
  #config 1GB zram size with memory greater than 6 GB
  echo lz4 > /sys/block/zram0/comp_algorithm
  echo 1073741824 > /sys/block/zram0/disksize
  echo 160 > /proc/sys/vm/swappiness
  echo 60 > /proc/sys/vm/direct_swappiness
fi
mkswap /dev/block/zram0
swapon /dev/block/zram0
#endif /* VENDOR_EDIT */
