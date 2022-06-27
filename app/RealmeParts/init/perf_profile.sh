#!/system/bin/sh

## Common-default settings

# GED modules
echo 0 >/sys/module/ged/parameters/gx_game_mode
echo 0 >/sys/module/ged/parameters/gx_force_cpu_boost
echo 0 > /sys/module/ged/parameters/boost_amp
echo 0 > /sys/module/ged/parameters/boost_extra
echo 0 > /sys/module/ged/parameters/boost_gpu_enable
echo 0 > /sys/module/ged/parameters/enable_cpu_boost
echo 0 > /sys/module/ged/parameters/enable_gpu_boost
echo 0 > /sys/module/ged/parameters/enable_game_self_frc_detect
echo 100 > /sys/module/ged/parameters/gpu_idle

# Disable all PPM
echo 0 > /proc/ppm/enabled

# Enable Thermal Throttle
echo 4 1 > /proc/ppm/policy_status

# cpufreq mode
echo 0 > /proc/cpufreq/cpufreq_cci_mode
echo 0 > /proc/cpufreq/cpufreq_power_mode
echo 0 > /sys/devices/system/cpu/perf/enable

# Default CPU Freq
#big cluster
echo 0 -1 > /proc/ppm/policy/hard_userlimit_max_cpu_freq
echo 0 -1 > /proc/ppm/policy/hard_userlimit_min_cpu_freq
#LITTLE cluster
echo 1 -1 > /proc/ppm/policy/hard_userlimit_max_cpu_freq
echo 1 -1 > /proc/ppm/policy/hard_userlimit_min_cpu_freq

# Default GPU Freq
echo 0 > /proc/gpufreq/gpufreq_opp_freq

# set sched to hybrid (HMP,EAS)
echo 2 > /sys/devices/system/cpu/eas/enable

# schedutil rate-limit
echo 1000 > /sys/devices/system/cpu/cpufreq/schedutil/down_rate_limit_us
echo 1000 > /sys/devices/system/cpu/cpufreq/schedutil/up_rate_limit_us

# cpusets
echo 0-7 > /dev/cpuset/foreground/cpus
echo 0-5 > /dev/cpuset/background/cpus
echo 0-7 > /dev/cpuset/system-background/cpus
echo 0-7 > /dev/cpuset/top-app/cpus
echo 0-7 > /dev/cpuset/restricted/cpus

# OPPO touchpanel
echo 0 > /proc/touchpanel/oppo_tp_limit_enable
echo 1 > /proc/touchpanel/oppo_tp_direction
echo 0 > /proc/touchpanel/game_switch_enable

case $1 in
0)
	## Balanced profile(use all default settings)
	;;
1)
	## Power saving profile

    # GED Modules
    echo 0 >/sys/module/ged/parameters/gx_game_mode
	echo 0 >/sys/module/ged/parameters/gx_force_cpu_boost
	echo 0 > /sys/module/ged/parameters/boost_amp
	echo 0 > /sys/module/ged/parameters/boost_extra
	echo 0 > /sys/module/ged/parameters/boost_gpu_enable
	echo 0 > /sys/module/ged/parameters/enable_cpu_boost
	echo 0 > /sys/module/ged/parameters/enable_gpu_boost
	echo 0 > /sys/module/ged/parameters/enable_game_self_frc_detect
	echo 100 > /sys/module/ged/parameters/gpu_idle
	
	# cpufreq mode
	echo 1 >/proc/cpufreq/cpufreq_power_mode
	echo 0 >/proc/cpufreq/cpufreq_cci_mode
	echo 0 > /sys/devices/system/cpu/perf/enable

    # force sched to EAS
    echo 1 > /sys/devices/system/cpu/eas/enable

    # sched rate
	echo 0 > /sys/devices/system/cpu/cpufreq/schedutil/down_rate_limit_us
	echo 1000 > /sys/devices/system/cpu/cpufreq/schedutil/up_rate_limit_us
	
	# CPU SchedTune
	echo 0-5 > /dev/cpuset/foreground/cpus
	echo 0-3 > /dev/cpuset/background/cpus
	echo 0-7 > /dev/cpuset/system-background/cpus
	echo 0-7 > /dev/cpuset/top-app/cpus
	echo 0-4 > /dev/cpuset/restricted/cpus
	
	# PPM
	echo 1 > /proc/ppm/enabled
	echo 0 1 > /proc/ppm/policy_status
	echo 1 1 > /proc/ppm/policy_status
	echo 2 1 > /proc/ppm/policy_status
	echo 3 1 > /proc/ppm/policy_status
	echo 5 0 > /proc/ppm/policy_status
	echo 6 1 > /proc/ppm/policy_status
	echo 7 1 > /proc/ppm/policy_status
	echo 8 1 > /proc/ppm/policy_status
	echo 9 0 > /proc/ppm/policy_status
	#enable thermal throttle
    echo 4 1 > /proc/ppm/policy_status

    # Set CPU Freq
    # big cluster
	echo 1 998000 > /proc/ppm/policy/hard_userlimit_max_cpu_freq
	echo 1 850000 > /proc/ppm/policy/hard_userlimit_min_cpu_freq
	# LITTLE cluster
	echo 0 999000 > /proc/ppm/policy/hard_userlimit_max_cpu_freq
	echo 0 500000 > /proc/ppm/policy/hard_userlimit_min_cpu_freq
	
	# Lock Lowest GPU Freq
    echo 299000 > /proc/gpufreq/gpufreq_opp_freq
	
	# OPPO touchpanel
	echo 0 > /proc/touchpanel/oppo_tp_limit_enable
    echo 1 > /proc/touchpanel/oppo_tp_direction
    echo 0 > /proc/touchpanel/game_switch_enable
	;;
2)
	## Performance Profile

    # GED Modules
	echo 1 >/sys/module/ged/parameters/gx_game_mode
	echo 1 >/sys/module/ged/parameters/gx_force_cpu_boost
	echo 1 > /sys/module/ged/parameters/boost_amp
	echo 1 > /sys/module/ged/parameters/boost_extra
	echo 1 > /sys/module/ged/parameters/boost_gpu_enable
	echo 1 > /sys/module/ged/parameters/enable_cpu_boost
	echo 1 > /sys/module/ged/parameters/enable_gpu_boost
	echo 1 > /sys/module/ged/parameters/enable_game_self_frc_detect
	echo 0 > /sys/module/ged/parameters/gpu_idle

    # cpufreq mode
	echo 3 > /proc/cpufreq/cpufreq_power_mode
	echo 1 > /proc/cpufreq/cpufreq_cci_mode
    echo 1 > /sys/devices/system/cpu/perf/enable

	# force sched to HMP
	echo 0 > /sys/devices/system/cpu/eas/enable

    # sched rate
	echo 0 > /sys/devices/system/cpu/cpufreq/schedutil/down_rate_limit_us
	echo 0 > /sys/devices/system/cpu/cpufreq/schedutil/up_rate_limit_us

    # CPU SchedTune
	echo 0-7 > /dev/cpuset/foreground/cpus
	echo 0 > /dev/cpuset/background/cpus
	echo 0-7 > /dev/cpuset/system-background/cpus
	echo 0-7 > /dev/cpuset/top-app/cpus
	echo 0 > /dev/cpuset/restricted/cpus

    # PPM
	echo 1 > /proc/ppm/enabled
	echo 0 1 > /proc/ppm/policy_status
	echo 1 1 > /proc/ppm/policy_status
	echo 2 1 > /proc/ppm/policy_status
	echo 3 1 > /proc/ppm/policy_status
	echo 5 1 > /proc/ppm/policy_status
	echo 6 1 > /proc/ppm/policy_status
	echo 7 1 > /proc/ppm/policy_status
	echo 8 1 > /proc/ppm/policy_status
	echo 9 1 > /proc/ppm/policy_status
	#disable thermal throttle
    echo 4 0 > /proc/ppm/policy_status

    # Set maximum CPU Freq
    # big cluster
	echo 1 2000000 >/proc/ppm/policy/hard_userlimit_max_cpu_freq
	echo 1 2000000 >/proc/ppm/policy/hard_userlimit_min_cpu_freq
    # LITTLE cluster
	echo 0 1700000 >/proc/ppm/policy/hard_userlimit_max_cpu_freq
	echo 0 1700000 >/proc/ppm/policy/hard_userlimit_min_cpu_freq
	
    # Set maximum GPU Freq
	echo 823000 > /proc/gpufreq/gpufreq_opp_freq

    # OPPO touchpanel
	echo 0 > /proc/touchpanel/oppo_tp_limit_enable
	echo 1 > /proc/touchpanel/oppo_tp_direction
	echo 1 > /proc/touchpanel/game_switch_enable
	;;
esac

