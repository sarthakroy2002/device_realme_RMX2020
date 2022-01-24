/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.realmeparts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class DefaultIndicatorSeekBar extends SeekBar {

    private int mDefaultProgress = -1;

    public DefaultIndicatorSeekBar(Context context) {
        super(context);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attrs, int defStyleAttr,
                                   int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getDefaultProgress() {
        return mDefaultProgress;
    }

    /**
     * N.B. This sets the default *unadjusted* progress, i.e. in the SeekBar's [0 - max] terms.
     */
    public void setDefaultProgress(int defaultProgress) {
        if (mDefaultProgress != defaultProgress) {
            mDefaultProgress = defaultProgress;
            invalidate();
        }
    }
}
