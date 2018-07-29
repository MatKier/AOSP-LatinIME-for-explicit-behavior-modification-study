package com.android.inputmethod.ebmStudy.keyStrokeLogging;

public class KeyStrokeDataBean extends SimpleKeyStrokeDataBean{
    private long eventTime;
    private int x;
    private int y;
    private int keyCenterX;
    private int keyCenterY;
    private float orientation;
    private float touchMinor;
    private float touchMajor;
    private float size;
    private boolean isLongPressed;
    private String longPressKey;

    KeyStrokeDataBean(String eventType, long eventTime, String keyValue, int x, int y, int offsetX,
                      int offsetY, int keyCenterX, int keyCenterY, float orientation, float touchMinor,
                      float touchMajor, float size, long holdTime, long flightTime, float pressure) {
        super(eventType, keyValue, offsetX, offsetY, holdTime, flightTime, pressure);
        this.eventTime = eventTime;
        this.x = x;
        this.y = y;
        this.keyCenterX = keyCenterX;
        this.keyCenterY = keyCenterY;
        this.orientation = orientation;
        this.touchMinor = touchMinor;
        this.touchMajor = touchMajor;
        this.size = size;
        this.isLongPressed = false;
        this.longPressKey = "";
    }

    long getEventTime() {
        return eventTime;
    }

    void setLongPressed(boolean longPressed) {
        isLongPressed = longPressed;
    }

    void setLongPressKey(String longPressKey) {
        this.longPressKey = longPressKey;
    }

    static String getCSVHeader() {
        return "eventType; eventTime; keyValue; x; y; offsetX; offsetY; keyCenterX; keyCenterY; orientation;" +
                " touchMinor; touchMajor; size; holdTime; flightTime; pressure; isLongPressed; longPressKey \n";
    }

    String toCSVString() {
        return getEventType() + "; " + eventTime + "; " + getKeyValue() + "; " + x + "; " + y + "; " + getOffsetX()
                + "; " + getOffsetY() + "; " + keyCenterX + "; " + keyCenterY + "; " + orientation + "; "
                + touchMinor + "; " + touchMajor + "; " + size + "; " + getHoldTime() + ";" + getFlightTime() +
                ";" + getPressure() + "; " + isLongPressed + "; " + longPressKey + "\n";
    }

    @Override
    public String toString() {
        return "eventType: " + getEventType() + "; eventTime: " + eventTime + "; keyValue: " + getKeyValue() +
                "; x: " + x + "; y: " + y + "; pressure: " + getPressure() + "; isLongPressed: " + isLongPressed + "\n";
    }
}