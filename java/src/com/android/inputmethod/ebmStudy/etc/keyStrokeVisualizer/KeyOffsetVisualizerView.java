package com.android.inputmethod.ebmStudy.etc.keyStrokeVisualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

@Deprecated
public class KeyOffsetVisualizerView extends View {
    // Height and width of a standard key
    // Measured through key.getHitBox().width();
    private static final double standardKeyHitboxWidth = 145.0;
    // Measured through key.getHitBox().height();
    private static final double standardKeyHitboxHeight = 220.0;

    private static final Paint circlePaint = new Paint();

    private int offsetX = Integer.MIN_VALUE;
    private int offsetY = Integer.MIN_VALUE;

    public KeyOffsetVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.FILL);
    }

    public void setTouchMarkerCords(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int midX = canvas.getWidth() / 2;
        int midY = canvas.getHeight() / 2;

        int actualOffsetX = (int)((offsetX / standardKeyHitboxWidth) * canvas.getWidth());
        int actualOffsetY = (int)((offsetY / standardKeyHitboxHeight) * canvas.getHeight());

        canvas.drawCircle(midX + actualOffsetX, midY + actualOffsetY, 15, circlePaint);
    }
}