/*
 * Copyright (C) The Android Open Source Project
 * Copyright (C) The LineageOS Project
 * Copyright (C) Yet Another AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

use std::collections::HashMap;
use std::fs;
use std::io::Write;
use std::path::Path;
use std::sync::Mutex;

use log::{debug, warn};

use android_hardware_light::aidl::android::hardware::light::{
    HwLight::HwLight, HwLightState::HwLightState, ILights::ILights, LightType::LightType,
};

use binder::{ExceptionCode, Interface, Status};

struct Light {
    hw_light: HwLight,
    state: HwLightState,
}

const LCD_LED_DIR: &str = "/sys/class/leds/lcd-backlight/";
const BRIGHTNESS_FILE: &str = "brightness";
const MAX_BRIGHTNESS_FILE: &str = "max_brightness";

const NUM_DEFAULT_LIGHTS: i32 = 1;

pub struct LightsService {
    lights: Mutex<HashMap<i32, Light>>,
}

impl Interface for LightsService {}

impl LightsService {
    fn new(hw_lights: impl IntoIterator<Item = HwLight>) -> Self {
        let mut lights_map = HashMap::new();
        for hw_light in hw_lights {
            lights_map.insert(hw_light.id, Light { hw_light, state: Default::default() });
        }
        Self { lights: Mutex::new(lights_map) }
    }

    fn get_brightness_from_state(state: &HwLightState) -> u32 {
        // Extract brightness from AARRGGBB
        let alpha = ((state.color >> 24) & 0xFF) as u32;
        let red = ((state.color >> 16) & 0xFF) as u32;
        let green = ((state.color >> 8) & 0xFF) as u32;
        let blue = (state.color & 0xFF) as u32;

        // Scale RGB brightness using Alpha brightness.
        let red = red * alpha / 0xFF;
        let green = green * alpha / 0xFF;
        let blue = blue * alpha / 0xFF;

        (77 * red + 150 * green + 29 * blue) >> 8
    }

    fn scale_brightness(br: u32, max_brightness: u32) -> u32 {
        if br == 0 {
            return 0;
        }
        // (brightness - 1) * (maxBrightness - 19) / (0xFF - 1) + 19
        ((br - 1) * (max_brightness.saturating_sub(19)) / (0xFF - 1)) + 19
    }

    fn get_scaled_brightness(state: &HwLightState, max_brightness: u32) -> u32 {
        let br = Self::get_brightness_from_state(state);
        Self::scale_brightness(br, max_brightness)
    }

    fn read_max_brightness(path: &Path) -> u32 {
        match fs::read_to_string(path) {
            Ok(s) => s.trim().parse::<u32>().unwrap_or_else(|e| {
                warn!("failed to parse max_brightness from {}: {}", path.display(), e);
                0
            }),
            Err(e) => {
                warn!("failed to read {}: {}", path.display(), e);
                0
            }
        }
    }

    fn write_to_sysfs<P: AsRef<Path>, S: AsRef<str>>(path: P, value: S) -> bool {
        match fs::OpenOptions::new().write(true).open(path.as_ref()) {
            Ok(mut file) => {
                if let Err(e) = file.write_all(value.as_ref().as_bytes()) {
                    warn!("failed to write '{}' to {}: {}", value.as_ref(), path.as_ref().display(), e);
                    false
                } else {
                    true
                }
            }
            Err(e) => {
                warn!("failed to open {} for writing: {}", path.as_ref().display(), e);
                false
            }
        }
    }

    fn handle_backlight(state: &HwLightState) {
        let max_brightness_path = Path::new(LCD_LED_DIR).join(MAX_BRIGHTNESS_FILE);
        let max_brightness = Self::read_max_brightness(&max_brightness_path);

        let scaled = Self::get_scaled_brightness(state, max_brightness);
        let brightness_path = Path::new(LCD_LED_DIR).join(BRIGHTNESS_FILE);

        if Self::write_to_sysfs(&brightness_path, scaled.to_string()) {
            debug!(
                "wrote scaled brightness {} to {} (max={})",
                scaled,
                brightness_path.display(),
                max_brightness
            );
        } else {
            warn!(
                "failed to write scaled brightness {} to {}",
                scaled,
                brightness_path.display()
            );
        }
    }
}

impl Default for LightsService {
    fn default() -> Self {
        let id_mapping = |light_id| HwLight { id: light_id, ordinal: light_id, r#type: LightType::BACKLIGHT };
        Self::new((1..=NUM_DEFAULT_LIGHTS).map(id_mapping))
    }
}

impl ILights for LightsService {
    fn setLightState(&self, id: i32, state: &HwLightState) -> binder::Result<()> {
        debug!("setLightState called for id={} color={:08x}", id, state.color);

        let mut guard = self.lights.lock().unwrap();

        if let Some(light) = guard.get_mut(&id) {
            // We only need to support BACKLIGHT
            if light.hw_light.r#type == LightType::BACKLIGHT {
                light.state = *state;
                LightsService::handle_backlight(state);
                Ok(())
            } else {
                Err(Status::new_exception(ExceptionCode::UNSUPPORTED_OPERATION, None))
            }
        } else {
            Err(Status::new_exception(ExceptionCode::UNSUPPORTED_OPERATION, None))
        }
    }

    fn getLights(&self) -> binder::Result<Vec<HwLight>> {
        debug!("getLights called");
        let guard = self.lights.lock().unwrap();
        Ok(guard.values().map(|l| l.hw_light).collect())
    }
}
