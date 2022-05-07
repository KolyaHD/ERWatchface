package com.kolyahd.erwatchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import androidx.palette.graphics.Palette;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WatchfaceDigital implements I_Drawable_Watchface{
    private final String TAG = "WF_Digital";

    private Paint mDigitPaint;
    private int mWatchDigitColor;
    private int mWatchDigitShadowColor;
    private static final float SHADOW_RADIUS_RATIO = (6f / 160f);
    private static final float TEXT_SIZE = 0.3f;

    @Override
    public void init(Context context, Bitmap backgroundBitmap) {
        /* Extracts colors from background image to improve watchface style. */
        Palette.from(backgroundBitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                if (palette != null) {
                    mWatchDigitColor = palette.getVibrantColor(Color.RED);
                    mWatchDigitShadowColor = palette.getDarkMutedColor(Color.BLACK);
                }
            }
        });

        mDigitPaint = new Paint();
        mDigitPaint.setColor(mWatchDigitColor);
        mDigitPaint.setAntiAlias(true);
        mDigitPaint.setTextAlign(Paint.Align.CENTER);
        Typeface typeface = context.getResources().getFont(R.font.square_sans_serif_7);
        mDigitPaint.setTypeface(typeface);
        mDigitPaint.setFakeBoldText(true);
    }

    @Override
    public void draw(Canvas canvas, Calendar calendar, float centerX, float centerY, float radius, boolean ambientMode, boolean muteMode) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String textTime = formatter.format(calendar.getTime());

        if (ambientMode) {
            mDigitPaint.clearShadowLayer();
        } else {
            mDigitPaint.setShadowLayer(SHADOW_RADIUS_RATIO * radius, 0, 0, mWatchDigitShadowColor);
        }
        mDigitPaint.setAlpha(muteMode ? 100 : 255);
        float desiredTextSize = TEXT_SIZE * radius;
        mDigitPaint.setTextSize(desiredTextSize);

        canvas.drawText(textTime, centerX, centerY + (TEXT_SIZE * radius * 0.25f), mDigitPaint);
    }
}
