package com.android.inputmethod.keyStrokeLogging.etc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.android.inputmethod.keyStrokeLogging.KeyStrokeDataBean;

import java.util.List;

public class FlightHoldTimeVisualizerView extends View {
    private static final int horizontalScale = 8;
    private static final int verticalScale = 2;
    private static final int PADDING = 5;
    private static final int STROKE_WIDTH = 20;
    private static final int CORNER_RADIUS = 30;

    // Max times in ms to visualize hold/flight time
    private static final int MAX_HOLD_TIME = 400;
    private static final int MAX_FLIGHT_TIME = 800;

    private List<KeyStrokeDataBean> keyStrokeList;

    private static final int colorHold = Color.parseColor("#389AC4");
    private static final int colorFlight = Color.parseColor("#959595");

    private static final int colorBackground = Color.parseColor("#FAFAFA");

    private static final Paint paintHold = new Paint();
    private static final Paint paintFlight = new Paint();

    private final RectF rectf = new RectF();

    public FlightHoldTimeVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintHold.setColor(colorHold);
        paintHold.setStyle(Paint.Style.FILL);
        paintFlight.setColor(colorFlight);
        paintFlight.setStyle(Paint.Style.FILL);
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
        int lastY = 0;
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int canvasMidY = canvasHeight / 2;

        for (KeyStrokeDataBean bean : keyStrokeList) {
            if (bean.getEventType().equals(StudyConstants.EVENT_TYPE_DOWN) && bean.getFlightTime() != -1) {
                // Draw flight time line (horizontal)
                int halfStrokeLength = (int) ((((bean.getFlightTime() > MAX_FLIGHT_TIME) ? MAX_FLIGHT_TIME : bean.getFlightTime()) / horizontalScale) / 2);
                int rightSide = currentX + halfStrokeLength;
                int leftSide = currentX - halfStrokeLength;

                int topSide = lastY;
                int bottomSide = topSide + STROKE_WIDTH;

                rectf.set(leftSide, topSide, rightSide, bottomSide);
                canvas.drawRoundRect(rectf, CORNER_RADIUS, CORNER_RADIUS, paintHold);
                currentX += ((MAX_FLIGHT_TIME / horizontalScale) / 2) + PADDING;
            } else if (bean.getEventType().equals(StudyConstants.EVENT_TYPE_UP)) {
                // Draw hold time line (vertical)
                int halfStrokeHeight = (int) ((((bean.getHoldTime() > MAX_HOLD_TIME) ? MAX_HOLD_TIME : bean.getHoldTime()) / verticalScale) / 2);
                int topSide = canvasMidY - halfStrokeHeight;
                int bottomSide = canvasMidY + halfStrokeHeight;

                int rightSide = currentX + STROKE_WIDTH;
                int leftSide = currentX;

                rectf.set(leftSide, topSide, rightSide, bottomSide);
                canvas.drawRoundRect(rectf, CORNER_RADIUS, CORNER_RADIUS, paintFlight);
                currentX += STROKE_WIDTH + PADDING + ((MAX_FLIGHT_TIME / horizontalScale) / 2);
                lastY = topSide;
            }
        }
    }
}