package com.android.inputmethod.keyStrokeLogging;

import android.view.MotionEvent;
import com.android.inputmethod.keyboard.Key;
import com.android.inputmethod.keyboard.KeyDetector;

import java.util.List;

class KeyStrokeLoggingHelper {

    static void logKeyEvent(List<KeyStrokeDataBean> keyStrokeDataList, final MotionEvent me, KeyDetector mKeyDetector) {
        final int x = (int)me.getX();
        final int y = (int)me.getY();
        final long eventTime = me.getEventTime();
        final float pressure = me.getPressure();
        final float orientation = (float)(me.getOrientation() * 180 / Math.PI);
        final float size = me.getSize();
        final float touchMinor = me.getTouchMinor();
        final float touchMajor = me.getTouchMajor();

        // Use the KeydeDector to detect a Key.
        // Not the most efficient way, since the Keyboard itself already did this, but it's much easier to do this here a second time
        final Key key = mKeyDetector.detectHitKey(x, y);
        String keyText = "noKey";
        int offsetX = 0;
        int offsetY = 0;
        int keyCenterX = -1;
        int keyCenterY = -1;
        if (key != null) {
            keyText = key.toShortString();
            keyCenterX = key.getHitBox().centerX();
            keyCenterY = key.getHitBox().centerY();
            //offsetX = Math.abs(keyCenterX - x);
            //offsetY = Math.abs(keyCenterY - y);
            offsetX = x - keyCenterX;
            offsetY = y - keyCenterY;
        }

        final int action = me.getActionMasked();
        String eventType;
        long holdTime = -1;
        long flightTime = -1;
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
             eventType = "down";
             if (keyStrokeDataList.size() >= 1) {
                 final long upTimestamp = keyStrokeDataList.get(keyStrokeDataList.size()-1).getEventTime();
                 flightTime = eventTime - upTimestamp;
             }
        } else {
            eventType = "up";
            final long downTimestamp = keyStrokeDataList.get(keyStrokeDataList.size()-1).getEventTime();
            holdTime = eventTime - downTimestamp;
        }

        keyStrokeDataList.add(new KeyStrokeDataBean(eventType, eventTime, keyText, x, y, offsetX, offsetY,
                keyCenterX, keyCenterY, orientation, touchMinor, touchMajor, size, holdTime, flightTime, pressure));
    }

    static void logLongPress(List<KeyStrokeDataBean> keyStrokeDataList, Key key) {
        if(keyStrokeDataList != null) {
            KeyStrokeDataBean bean = keyStrokeDataList.get(keyStrokeDataList.size()-1);
            bean.setLongPressed(true);
            // Just assumes the first of the moreKeyPanel was pressed, obviously not correct, but more is probably never needed anyway.
            if (key.getMoreKeys() != null && key.getMoreKeys().length > 0) {
                keyStrokeDataList.get(keyStrokeDataList.size()-1).setLongPressKey(key.getMoreKeys()[0].mLabel);
            }
        }
    }
}