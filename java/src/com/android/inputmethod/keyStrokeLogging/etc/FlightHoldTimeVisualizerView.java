package com.android.inputmethod.keyStrokeLogging.etc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.android.inputmethod.keyStrokeLogging.KeyStrokeDataBean;

import java.util.Iterator;
import java.util.List;

public class FlightHoldTimeVisualizerView extends View {
    private static final int horizontalScale = 8;
    private static final int verticalScale = 2;
    private static final int PADDING = 5;
    private static final int STROKE_WIDTH = 20;
    private static final int CORNER_RADIUS = 30;

    // MAx times in ms to visualize hold/flight time
    private static final int MAX_HOLDTIME = 400;
    private static final int MAX_FLIGHTTIME = 800;

    private List<KeyStrokeDataBean> keyStrokeList;

    private static final int colorHold = Color.parseColor("#389AC4");
    private static final int colorFlight = Color.parseColor("#959595");

    private static final int colorBackground = Color.parseColor("#FAFAFA");

    private static final Paint paintHold = new Paint();
    private static final Paint paintFlight = new Paint();

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
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int canvasMidY = canvasHeight / 2;

        for (KeyStrokeDataBean bean : keyStrokeList) {
            if (bean.getEventType().equals("down") && bean.getFlightTime() != -1) {
                // Draw flight time line (horizontal)
                int rightSide = (int) (((bean.getFlightTime() > MAX_FLIGHTTIME) ? MAX_FLIGHTTIME : bean.getFlightTime()) / horizontalScale);
                int topSide = canvasMidY + STROKE_WIDTH / 2;
                int bottomSide = canvasMidY - STROKE_WIDTH / 2;
                canvas.drawRoundRect(new RectF(currentX, topSide, rightSide + currentX, bottomSide), CORNER_RADIUS, CORNER_RADIUS, paintHold);
                currentX += rightSide + PADDING;
            } else if (bean.getEventType().equals("up")) {
                // Draw hold time line (vertical)
                int strokeHeight = (int) (((bean.getHoldTime() > MAX_HOLDTIME) ? MAX_HOLDTIME : bean.getHoldTime()) / verticalScale);
                int topSide = canvasMidY + strokeHeight / 2;
                int bottomSide = canvasMidY - strokeHeight / 2;
                canvas.drawRoundRect(new RectF(currentX, topSide, currentX + STROKE_WIDTH, bottomSide), CORNER_RADIUS, CORNER_RADIUS, paintFlight);
                currentX += STROKE_WIDTH + PADDING;
            }
        }
    }
}