# Copyright (C) 2009 The Android Open Source Project
# Copyright (C) 2019 The Mokee Open Source Project
# Copyright (C) 2019 The LineageOS Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import common

def FullOTA_InstallEnd(info):
  OTA_InstallEnd(info, False)

def IncrementalOTA_InstallEnd(info):
  OTA_InstallEnd(info, True)

def AddImageOnly(info, basename, incremental, firmware):
  if incremental:
    input_zip = info.source_zip
  else:
    input_zip = info.input_zip
  if firmware:
    data = input_zip.read("RADIO/" + basename)
  else:
    data = input_zip.read("IMAGES/" + basename)
  common.ZipWriteStr(info.output_zip, basename, data)

def AddImage(info, basename, dest, incremental):
  AddImageOnly(info, basename, incremental, False)
  info.script.Print("Patching {} image unconditionally...".format(dest.split('/')[-1]))
  info.script.AppendExtra('package_extract_file("%s", "%s");' % (basename, dest))

def OTA_InstallEnd(info, incremental):
  AddImage(info, "vbmeta.img", "/dev/block/platform/bootdevice/by-name/vbmeta", incremental)

  bin_map = {
      'logo': ['logo']
      }

  img_map = {
      'dtbo': ['dtbo'],
      'gz': ['gz1', 'gz2'],
      'lk': ['lk', 'lk2'],
      'md1img': ['md1img'],
      'scp': ['scp1', 'scp2'],
      'spmfw': ['spmfw'],
      'sspm': ['sspm_1', 'sspm_2'],
      'tee': ['tee1', 'tee2']
      }

  pl = 'preloader_emmc'
  pl_part = ['mmcblk0boot0', 'mmcblk0boot1']

  fw_cmd = 'ui_print("Patching radio images unconditionally...");\n'

  AddImageOnly(info, "{}.img".format(pl), incremental, True)
  for part in pl_part:
      fw_cmd += 'package_extract_file("{}.img", "/dev/block/{}");\n'.format(pl, part)

  for img in img_map.keys():
    AddImageOnly(info, '{}.img'.format(img), incremental, True)
    for part in img_map[img]:
      fw_cmd += 'package_extract_file("{}.img", "/dev/block/platform/bootdevice/by-name/{}");\n'.format(img, part)

  for _bin in bin_map.keys():
    AddImageOnly(info, '{}.bin'.format(_bin), incremental, True)
    for part in bin_map[_bin]:
      fw_cmd += 'package_extract_file("{}.bin", "/dev/block/platform/bootdevice/by-name/{}");\n'.format(_bin, part)

  info.script.AppendExtra(fw_cmd)
