package com.kolyahd.erwatchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Calendar;

public interface I_Drawable_Watchface {
    void init(Context context, Bitmap backgroundBitmap);
    void draw(Canvas canvas, Calendar calendar, float centerX, float centerY, float radius, boolean ambientMode, boolean muteMode);
}
