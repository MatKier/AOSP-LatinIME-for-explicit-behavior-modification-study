package com.android.inputmethod.ebmStudy.keyStrokeLogging;

import android.view.MotionEvent;

import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.keyboard.Key;

import java.util.List;

class KeyStrokeLoggingHelper {

    static void logKeyEvent(List<KeyStrokeDataBean> keyStrokeDataList, final MotionEvent me, Key key) {
        final long unixTime = System.currentTimeMillis();
        final int x = (int)me.getX();
        final int y = (int)me.getY();
        final long eventTime = me.getEventTime();
        final float pressure = me.getPressure();
        final float orientation = (float)(me.getOrientation() * 180 / Math.PI);
        final float size = me.getSize();
        final float touchMinor = me.getTouchMinor();
        final float touchMajor = me.getTouchMajor();

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
//        if (key.getCode() == 108 || key.getCode() == 97) {
//            offsetX = (int)(offsetX * 0.6682);
//        }

        final int action = me.getActionMasked();
        String eventType;
        long holdTime = -1;
        long flightTime = -1;
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
             eventType = StudyConstants.EVENT_TYPE_DOWN;
             if (keyStrokeDataList.size() >= 1) {
                 final long upTimestamp = keyStrokeDataList.get(keyStrokeDataList.size()-1).getEventTime();
                 flightTime = eventTime - upTimestamp;
             }
        } else {
            eventType = StudyConstants.EVENT_TYPE_UP;
            final long downTimestamp = keyStrokeDataList.get(keyStrokeDataList.size()-1).getEventTime();
            holdTime = eventTime - downTimestamp;
        }

        keyStrokeDataList.add(new KeyStrokeDataBean(unixTime, eventType, eventTime, keyText, x, y, offsetX, offsetY,
                keyCenterX, keyCenterY, orientation, touchMinor, touchMajor, size, holdTime, flightTime, pressure));
    }

    static void logLongPress(List<KeyStrokeDataBean> keyStrokeDataList, Key key) {
        if(keyStrokeDataList != null) {
            KeyStrokeDataBean bean = keyStrokeDataList.get(keyStrokeDataList.size()-1);
            bean.setLongPressed(true);
            keyStrokeDataList.get(keyStrokeDataList.size()-1).setLongPressKey(key.toShortString());

            // TODO log longPressUpEvent?
        }
    }
}