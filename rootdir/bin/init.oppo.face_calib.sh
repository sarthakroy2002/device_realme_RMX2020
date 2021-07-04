#!/system/bin/sh

/system/bin/mkdir /sdcard/.faceauth
/system/bin/mkdir /sdcard/.facedldata
/system/bin/mv /data/vendor_de/0/.faceauth/authdata.txt /sdcard/.faceauth/
/system/bin/touch /data/vendor_de/0/.faceauth/signdata.txt
/system/bin/chown system:system /data/vendor_de/0/.faceauth/signdata.txt
/system/bin/touch /data/vendor_de/0/.facedldata/Tiger_181.bin
/system/bin/chown system:system /data/vendor_de/0/.facedldata/Tiger_181.bin
/system/bin/touch /data/vendor_de/0/.facedldata/Tiger_181.ref
/system/bin/chown system:system /data/vendor_de/0/.facedldata/Tiger_181.ref
/system/bin/touch /data/vendor_de/0/.facedldata/stereoParams.bin
/system/bin/chown system:system /data/vendor_de/0/.facedldata/stereoParams.bin
/system/bin/touch /data/vendor_de/0/.facedldata/DepthParams.bin
/system/bin/chown system:system /data/vendor_de/0/.facedldata/DepthParams.bin
/system/bin/touch /data/vendor_de/0/.facedldata/REF_IR.bmp
/system/bin/chown system:system /data/vendor_de/0/.facedldata/REF_IR.bmp
/system/bin/touch /data/vendor_de/0/.facedldata/Rectified_REF_IR.bmp
/system/bin/chown system:system /data/vendor_de/0/.facedldata/Rectified_REF_IR.bmp
/system/bin/touch /data/vendor_de/0/.facedldata/digest.txt
/system/bin/chown system:system /data/vendor_de/0/.facedldata/digest.txt
