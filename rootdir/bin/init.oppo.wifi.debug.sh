#! /system/bin/sh
#***********************************************************
#** Copyright (C), 2008-2016, OPPO Mobile Comm Corp., Ltd.
#** VENDOR_EDIT
#** 
#** Version: 1.0
#** Date : 2019/10/18
#** Author: JiaoBo@PSW.CN.WiFi.Basic.Log.1162004, 2019/10/18
#** Add for: mtk coredump related log collection and DCS handle
#**
#** ---------------------Revision History: ---------------------
#**  <author>    <data>       <version >       <desc>
#**  Bo.Jiao    2019/10/18     1.0        build this module
#****************************************************************/

config="$1"

#collectWifiCoredumpLog service start kernel log collect and zip file to DCS path
function collectWifiCoredumpLog(){
    WIFI_DUMP_PARENT_DIR=/data/vendor/connsyslog/
    DCS_WIFI_LOG_PATH=/data/oppo/log/DCS/de/network_logs/stp_dump
    if [ ! -d ${DCS_WIFI_LOG_PATH} ];then
        mkdir -p ${DCS_WIFI_LOG_PATH}
    fi
    chown -R system:system ${DCS_WIFI_LOG_PATH}
    chmod -R 777 ${WIFI_DUMP_PARENT_DIR}

    zip_name=`getprop vendor.connsys.wifi.dump.zip.name`
    kinfo_name=`getprop vendor.connsys.wifi.dump.kinfo.name`
    dmesg > ${WIFI_DUMP_PARENT_DIR}/${kinfo_name}.kinfo
    sleep 2
    $XKIT tar -czvf  ${DCS_WIFI_LOG_PATH}/${zip_name}.tar.gz -C ${WIFI_DUMP_PARENT_DIR} ${WIFI_DUMP_PARENT_DIR}
    chown -R system:system ${DCS_WIFI_LOG_PATH}
    chmod -R 777 ${DCS_WIFI_LOG_PATH}
    rm -rf ${WIFI_DUMP_PARENT_DIR}/*

    chown -R system:system ${WIFI_DUMP_PARENT_DIR}
    chmod -R 776 ${WIFI_DUMP_PARENT_DIR}
    setprop vendor.connsys.wifi.dump.status "0"
}

case "$config" in
        "collectWifiCoredumpLog")
        collectWifiCoredumpLog
    ;;
esac
