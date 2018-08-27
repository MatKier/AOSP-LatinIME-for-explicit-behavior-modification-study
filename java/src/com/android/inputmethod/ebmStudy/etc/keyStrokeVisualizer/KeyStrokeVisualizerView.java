package com.android.inputmethod.ebmStudy.etc.keyStrokeVisualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.SimpleKeyStrokeDataBean;

import java.util.List;

public class KeyStrokeVisualizerView extends View {
    // Height and width of a standard key
    // Measured through key.getHitBox().width();
    private static final double standardKeyHitboxWidth = 145.0;
    // Measured through key.getHitBox().height();
    private static final double standardKeyHitboxHeight = 220.0;

    private static final int BASE_KEY_SHAPE_WIDTH = 100;
    private static final int BASE_KEY_SHAPE_HEIGHT = 160;

    private static final int colorBackground = Color.parseColor("#FAFAFA");
    private static final int colorRectangle = Color.parseColor("#D9DDE0");

    private static final Paint circlePaint = new Paint();
    private static final Paint rectanglePaint = new Paint();
    private static final Paint keyLabelPaint = new Paint();

    private static final int LABEL_FONT_SIZE = 85;
    private static final int LABEL_FONT_SIZE_DROIDSANSMONO = 70;

    private static final int CORNER_RADIUS = 15;


    private List<? extends SimpleKeyStrokeDataBean>  keyStrokeList;

    public static final int DEFAULT_HOLD_TIME_MS = 80;
    public static final int MAX_HOLD_TIME_MS = DEFAULT_HOLD_TIME_MS * 5;
    private static final int HOLD_TIME_SCALE = 4;

    public static final int DEFAULT_FLIGHT_TIME_MS = 260;
    public static final int MAX_FLIGHT_TIME_MS = DEFAULT_FLIGHT_TIME_MS * 4;
    private static final int FLIGHT_TIME_SCALE = 10;

    private static final int PRESSURE_SCALE = 80;


    private final RectF rectf = new RectF();

    public KeyStrokeVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        rectanglePaint.setColor(colorRectangle);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setAntiAlias(true);

        Typeface tfDroidSansMono = Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSansMono.ttf");

        keyLabelPaint.setColor(Color.BLACK);
        keyLabelPaint.setTextSize(LABEL_FONT_SIZE_DROIDSANSMONO);
        keyLabelPaint.setTypeface(tfDroidSansMono);
        keyLabelPaint.setStyle(Paint.Style.FILL);
        keyLabelPaint.setAntiAlias(true);
    }

    public void setKeyStrokeList(List<? extends SimpleKeyStrokeDataBean> keyStrokeList) {
        this.keyStrokeList = keyStrokeList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(colorBackground);

        if (keyStrokeList == null) {
            return;
        }

        int yStartingPoint = canvas.getHeight() / 2 - BASE_KEY_SHAPE_HEIGHT / 2;
        int currentX = 30;
        for (SimpleKeyStrokeDataBean bean : keyStrokeList) {
            /* Move to the right (in relation to the flightTime)*/
            if (bean.getEventType().equals(StudyConstants.EVENT_TYPE_DOWN) && bean.getFlightTime() != -1) {
                long fTime = bean.getFlightTime();
                if (fTime < DEFAULT_FLIGHT_TIME_MS) {
                    currentX = currentX + DEFAULT_FLIGHT_TIME_MS / FLIGHT_TIME_SCALE;
                } else if (fTime > MAX_FLIGHT_TIME_MS) {
                    currentX = currentX + MAX_FLIGHT_TIME_MS / FLIGHT_TIME_SCALE;
                } else {
                    currentX = (int) (currentX + fTime / FLIGHT_TIME_SCALE);
                }
            } else if (bean.getEventType().equals(StudyConstants.EVENT_TYPE_UP)) {
                /* Draw key shape */
                int holdTime = (int) bean.getHoldTime();
                int keyShapeWidth;
                if (holdTime <= DEFAULT_HOLD_TIME_MS) {
                    keyShapeWidth = BASE_KEY_SHAPE_WIDTH;
                } else if (holdTime >= MAX_HOLD_TIME_MS) {
                    keyShapeWidth = BASE_KEY_SHAPE_WIDTH + (MAX_HOLD_TIME_MS - 100) / HOLD_TIME_SCALE;
                } else {
                    keyShapeWidth = BASE_KEY_SHAPE_WIDTH + (holdTime - 100) / HOLD_TIME_SCALE;
                }

                rectf.set(currentX, yStartingPoint, currentX + keyShapeWidth, BASE_KEY_SHAPE_HEIGHT + yStartingPoint);
                canvas.drawRoundRect(rectf, CORNER_RADIUS, CORNER_RADIUS, rectanglePaint);

                /* Draw label on key */
                String label = bean.getKeyValue().length() == 1 ? bean.getKeyValue() : "";
                int halfLabelWidth = (int) (keyLabelPaint.measureText(label) / 2);
                int halfLabelHeight = (int) (keyLabelPaint.measureText(label) / 2);
                canvas.drawText(label, rectf.centerX() - halfLabelWidth, rectf.centerY() + halfLabelHeight, keyLabelPaint);

                /* Calculate Circle radius in dependence of the pressure*/
                float pressure = bean.getPressure();
                float radius = pressure * PRESSURE_SCALE;

                /* Set Circle Alpha in dependence of the radius */
                circlePaint.setAlpha((int) (110 - radius / 4));

                /* Draw offset circle on key */
                double offsetXFactor = (bean.getOffsetX() / standardKeyHitboxWidth);
                // Make sure the circle can't go out of bounds (e.g. Space bar)
                offsetXFactor = (Math.abs(offsetXFactor) > 0.45 ? (0.45 * Math.signum(offsetXFactor)) : offsetXFactor);
                float actualOffsetX = (float) (offsetXFactor * rectf.width());

                double offSetYFactor = (bean.getOffsetY() / standardKeyHitboxHeight);
                // Make sure the circle can't go out of bounds
                offSetYFactor = (Math.abs(offSetYFactor) > 0.45 ? (0.45 * Math.signum(offSetYFactor)) : offSetYFactor);
                float actualOffsetY = (float) (offSetYFactor * rectf.height());
                canvas.drawCircle(rectf.centerX() + actualOffsetX, rectf.centerY() + actualOffsetY, radius, circlePaint);

                currentX = currentX + (int) rectf.width();
            }
        }
    }
}