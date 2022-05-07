package com.kolyahd.erwatchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.palette.graphics.Palette;

import java.util.Calendar;

public class WatchfaceAnalogue implements I_Drawable_Watchface{
    private final String TAG = "WF_Analogue";

    private float mSecondHandLength;
    private float sMinuteHandLength;
    private float sHourHandLength;
    private int mWatchHandColor;
    private int mWatchHandHighlightColor;
    private int mWatchHandShadowColor;
    private Paint mHourPaint;
    private Paint mMinutePaint;
    private Paint mSecondPaint;
    private Paint mTickAndCirclePaint;

    private static final float HOUR_STROKE_WIDTH_RATIO = (5f / 160f);
    private static final float MINUTE_STROKE_WIDTH_RATIO = (3f / 160f);
    private static final float SECOND_TICK_STROKE_WIDTH_RATIO = (2f / 160f);
    private static final float CENTER_GAP_AND_CIRCLE_RADIUS_RATIO = (4f / 160f);
    private static final float SHADOW_RADIUS_RATIO = (6f / 160f);

    @Override
    public void init(Context context, Bitmap backgroundBitmap) {
        /* Extracts colors from background image to improve watchface style. */
        Palette.from(backgroundBitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                if (palette != null) {
                    mWatchHandHighlightColor = palette.getVibrantColor(Color.RED);
                    mWatchHandColor = palette.getLightVibrantColor(Color.WHITE);
                    mWatchHandShadowColor = palette.getDarkMutedColor(Color.BLACK);
                }
            }
        });

        mHourPaint = new Paint();
        mHourPaint.setColor(mWatchHandColor);
        mHourPaint.setAntiAlias(true);
        mHourPaint.setStrokeCap(Paint.Cap.ROUND);

        mMinutePaint = new Paint();
        mMinutePaint.setColor(mWatchHandColor);
        mMinutePaint.setAntiAlias(true);
        mMinutePaint.setStrokeCap(Paint.Cap.ROUND);

        mSecondPaint = new Paint();
        mSecondPaint.setColor(mWatchHandHighlightColor);
        mSecondPaint.setAntiAlias(true);
        mSecondPaint.setStrokeCap(Paint.Cap.ROUND);

        mTickAndCirclePaint = new Paint();
        mTickAndCirclePaint.setColor(mWatchHandColor);
        mTickAndCirclePaint.setAntiAlias(true);
        mTickAndCirclePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, Calendar calendar, float centerX, float centerY, float radius, boolean ambientMode, boolean muteMode) {
        mSecondHandLength = (float) (radius * 0.875);
        sMinuteHandLength = (float) (radius * 0.75);
        sHourHandLength = (float) (radius * 0.5);

        //set paints
        updatePaintStyles(radius, ambientMode, muteMode);

        //draw ticks
        float innerTickRadius = radius * (float) 0.9375;
        float outerTickRadius = radius;
        for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
            float tickRot = (float) (tickIndex * Math.PI * 2 / 12);
            float innerX = (float) Math.sin(tickRot) * innerTickRadius;
            float innerY = (float) -Math.cos(tickRot) * innerTickRadius;
            float outerX = (float) Math.sin(tickRot) * outerTickRadius;
            float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
            canvas.drawLine(centerX + innerX, centerY + innerY,
                    centerX + outerX, centerY + outerY, mTickAndCirclePaint);
        }

        final float seconds = (calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f);
        final float secondsRotation = seconds * 6f;
        final float minutesRotation = calendar.get(Calendar.MINUTE) * 6f;
        final float hourHandOffset = calendar.get(Calendar.MINUTE) / 2f;
        final float hoursRotation = (calendar.get(Calendar.HOUR) * 30) + hourHandOffset;

        canvas.save();

        canvas.rotate(hoursRotation, centerX, centerY);
        canvas.drawLine(
                centerX,
                centerY - (CENTER_GAP_AND_CIRCLE_RADIUS_RATIO * radius),
                centerX,
                centerY - sHourHandLength,
                mHourPaint);

        canvas.rotate(minutesRotation - hoursRotation, centerX, centerY);
        canvas.drawLine(
                centerX,
                centerY - (CENTER_GAP_AND_CIRCLE_RADIUS_RATIO * radius),
                centerX,
                centerY - sMinuteHandLength,
                mMinutePaint);

        /*
         * Ensure the "seconds" hand is drawn only when we are in interactive mode.
         * Otherwise, we only update the watch face once a minute.
         */
        if (!ambientMode) {
            canvas.rotate(secondsRotation - minutesRotation, centerX, centerY);
            canvas.drawLine(
                    centerX,
                    centerY - (CENTER_GAP_AND_CIRCLE_RADIUS_RATIO * radius),
                    centerX,
                    centerY - mSecondHandLength,
                    mSecondPaint);

        }
        canvas.drawCircle(
                centerX,
                centerY,
                CENTER_GAP_AND_CIRCLE_RADIUS_RATIO * radius,
                mTickAndCirclePaint);

        /* Restore the canvas" original orientation. */
        canvas.restore();
    }

    private void updatePaintStyles(float radius, boolean ambientMode, boolean muteMode) {
        mHourPaint.setStrokeWidth(HOUR_STROKE_WIDTH_RATIO * radius);
        mMinutePaint.setStrokeWidth(MINUTE_STROKE_WIDTH_RATIO * radius);
        mSecondPaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH_RATIO * radius);
        mTickAndCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH_RATIO * radius);

        if (ambientMode) {
            mHourPaint.setColor(Color.WHITE);
            mMinutePaint.setColor(Color.WHITE);
            mSecondPaint.setColor(Color.WHITE);
            mTickAndCirclePaint.setColor(Color.WHITE);

            mHourPaint.setAntiAlias(false);
            mMinutePaint.setAntiAlias(false);
            mSecondPaint.setAntiAlias(false);
            mTickAndCirclePaint.setAntiAlias(false);

            mHourPaint.clearShadowLayer();
            mMinutePaint.clearShadowLayer();
            mSecondPaint.clearShadowLayer();
            mTickAndCirclePaint.clearShadowLayer();

        } else {
            mHourPaint.setColor(mWatchHandColor);
            mMinutePaint.setColor(mWatchHandColor);
            mSecondPaint.setColor(mWatchHandHighlightColor);
            mTickAndCirclePaint.setColor(mWatchHandColor);

            mHourPaint.setAntiAlias(true);
            mMinutePaint.setAntiAlias(true);
            mSecondPaint.setAntiAlias(true);
            mTickAndCirclePaint.setAntiAlias(true);

            mHourPaint.setShadowLayer(SHADOW_RADIUS_RATIO * radius, 0, 0, mWatchHandShadowColor);
            mMinutePaint.setShadowLayer(SHADOW_RADIUS_RATIO * radius, 0, 0, mWatchHandShadowColor);
            mSecondPaint.setShadowLayer(SHADOW_RADIUS_RATIO * radius, 0, 0, mWatchHandShadowColor);
            mTickAndCirclePaint.setShadowLayer(SHADOW_RADIUS_RATIO * radius, 0, 0, mWatchHandShadowColor);
        }

        //mute mode
        mHourPaint.setAlpha(muteMode ? 100 : 255);
        mMinutePaint.setAlpha(muteMode ? 100 : 255);
        mSecondPaint.setAlpha(muteMode ? 80 : 255);
    }
}
