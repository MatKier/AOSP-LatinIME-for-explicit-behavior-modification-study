package com.android.inputmethod.keyStrokeLogging.etc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.android.inputmethod.keyStrokeLogging.KeyStrokeDataBean;

import java.util.List;

public class KeyStrokeVisualizerView extends View {
    // Height and width of a standard key
    // Measured through key.getHitBox().width();
    private static final double standardKeyHitboxWidth = 145.0;
    // Measured through key.getHitBox().height();
    private static final double standardKeyHitboxHeight = 220.0;

    private static final int KEY_SHAPE_WIDTH = 100;
    private static final int KEY_SHAPE_HEIGHT = 160;

    private static final int colorBackground = Color.parseColor("#FAFAFA");
    private static final int colorRectangle = Color.parseColor("#D9DDE0");

    private static final Paint circlePaint = new Paint();
    private static final Paint rectanglePaint = new Paint();
    private static final Paint keyLabelPaint = new Paint();
    private static final Paint keyLabelPaintBold = new Paint();

    private static final int LABEL_FONT_SIZE = 85;

    private static final int CORNER_RADIUS = 15;

    private List<KeyStrokeDataBean> keyStrokeList;

    private static final int MIN_HOLD_TIME_MS = 100;
    private static final int MAX_HOLD_TIME_MS = 500;
    private static final int HOLD_TIME_SCALE = 15;

    private static final int MIN_FLIGHT_TIME_MS = 100;
    private static final int MAX_FLIGHT_TIME_MS = 1000;
    private static final int FLIGHT_TIME_SCALE = 10;


    private final RectF rectf = new RectF();

    public KeyStrokeVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        rectanglePaint.setColor(colorRectangle);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setAntiAlias(true);

        Typeface tfCourierNew = Typeface.createFromAsset(getContext().getAssets(), "fonts/CourierNew.ttf");
        Typeface tfCourierNewBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/CourierNewBold.ttf");

        keyLabelPaint.setColor(Color.BLACK);
        keyLabelPaint.setTextSize(LABEL_FONT_SIZE);
        keyLabelPaint.setTypeface(tfCourierNew);
        keyLabelPaint.setStyle(Paint.Style.FILL);
        keyLabelPaint.setAntiAlias(true);

        keyLabelPaintBold.setColor(Color.BLACK);
        keyLabelPaintBold.setTextSize(LABEL_FONT_SIZE);
        keyLabelPaintBold.setTypeface(tfCourierNewBold);
        keyLabelPaintBold.setStyle(Paint.Style.FILL);
        keyLabelPaintBold.setAntiAlias(true);
    }

    public void setKeyStrokeList(List<KeyStrokeDataBean> keyStrokeList) {
        this.keyStrokeList = keyStrokeList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(colorBackground);

        if (keyStrokeList == null) {
            return;
        }

        int currentX = 0;

        for (KeyStrokeDataBean bean : keyStrokeList) {
            /* Move to the right (in relation to the flightTime)*/
            if (bean.getEventType().equals(StudyConstants.EVENT_TYPE_DOWN) && bean.getFlightTime() != -1) {
                long fTime = bean.getFlightTime();
                if (fTime < MIN_FLIGHT_TIME_MS) {
                    currentX = currentX + MIN_FLIGHT_TIME_MS / FLIGHT_TIME_SCALE;
                } else if (fTime > MAX_FLIGHT_TIME_MS) {
                    currentX = currentX + MAX_FLIGHT_TIME_MS / FLIGHT_TIME_SCALE;
                } else {
                    currentX = (int)(currentX + fTime / FLIGHT_TIME_SCALE);
                }
            } else if (bean.getEventType().equals(StudyConstants.EVENT_TYPE_UP)) {
                /* Draw key shape */
                rectf.set(currentX, 0, currentX + KEY_SHAPE_WIDTH, KEY_SHAPE_HEIGHT);
                canvas.drawRoundRect(rectf, CORNER_RADIUS, CORNER_RADIUS, rectanglePaint);

                /* Draw label on key */
                String label = bean.getKeyValue().length() == 1 ? bean.getKeyValue() : "";
                Paint p = bean.getPressure() > 0.35f ? keyLabelPaintBold : keyLabelPaint;
                int halfLabelWidth = (int) (p.measureText(label) / 2);
                int halfLabelHeight = (int) (p.measureText(label) / 2);
                canvas.drawText(label, rectf.centerX() - halfLabelWidth, rectf.centerY() + halfLabelHeight, p);


                /* Calculate Circle radius */
                long hTime = bean.getHoldTime();
                float radius = 0;
                if (hTime < MIN_HOLD_TIME_MS) {
                    radius = MIN_HOLD_TIME_MS / HOLD_TIME_SCALE;
                } else if (hTime > MAX_HOLD_TIME_MS) {
                    radius = MAX_HOLD_TIME_MS / HOLD_TIME_SCALE;
                } else {
                    radius = bean.getHoldTime() / HOLD_TIME_SCALE;
                }

                /* Set Circle Alpha in dependence of the radius */
                if (radius <= 10f) {
                    circlePaint.setAlpha(255);
                } else {
                    circlePaint.setAlpha((int)(170 - radius));
                }

                /* Draw offset circle on key */
                double offsetXFactor = (bean.getOffsetX() / standardKeyHitboxWidth);
                // Make sure the circle can't go out of bounds (e.g. Space bar)
                offsetXFactor = (Math.abs(offsetXFactor) > 0.45 ? (0.45 *  Math.signum(offsetXFactor)) : offsetXFactor);
                float actualOffsetX = (float) (offsetXFactor * rectf.width());

                double offSetYFactor = (bean.getOffsetY() / standardKeyHitboxHeight);
                // Make sure the circle can't go out of bounds
                offSetYFactor = (Math.abs(offSetYFactor) > 0.45 ? (0.45 *  Math.signum(offSetYFactor)) : offSetYFactor);
                float actualOffsetY = (float)(offSetYFactor * rectf.height());
                canvas.drawCircle(rectf.centerX() + actualOffsetX, rectf.centerY() + actualOffsetY, radius, circlePaint);

                currentX = currentX + (int)rectf.width();
            }
        }
    }
}