package com.android.inputmethod.ebmStudy.keyStrokeLogging;

public class KeyStrokeDataBean {
    private String eventType;
    private long eventTime;
    private String keyValue;
    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private int keyCenterX;
    private int keyCenterY;
    private float orientation;
    private float touchMinor;
    private float touchMajor;
    private float size;
    private long holdTime;
    private long flightTime;
    private float pressure;
    private boolean isLongPressed;
    private String longPressKey;

    KeyStrokeDataBean(String eventType, long eventTime, String keyValue, int x, int y, int offsetX,
                      int offsetY, int keyCenterX, int keyCenterY, float orientation, float touchMinor,
                      float touchMajor, float size, long holdTime, long flightTime, float pressure) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.keyValue = keyValue;
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.keyCenterX = keyCenterX;
        this.keyCenterY = keyCenterY;
        this.orientation = orientation;
        this.touchMinor = touchMinor;
        this.touchMajor = touchMajor;
        this.size = size;
        this.holdTime = holdTime;
        this.flightTime = flightTime;
        this.pressure = pressure;
        this.isLongPressed = false;
        this.longPressKey = "";
    }

    static String getCSVHeader() {
        return "eventType; eventTime; keyValue; x; y; offsetX; offsetY; keyCenterX; keyCenterY; orientation;" +
                " touchMinor; touchMajor; size; holdTime; flightTime; pressure; isLongPressed; longPressKey \n";
    }

    long getEventTime() {
        return eventTime;
    }

    void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    void setLongPressed(boolean longPressed) {
        isLongPressed = longPressed;
    }

    int getX() {
        return x;
    }

    void setX(int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    void setY(int y) {
        this.y = y;
    }

    void setLongPressKey(String longPressKey) {
        this.longPressKey = longPressKey;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }


    public long getHoldTime() {
        return holdTime;
    }

    public long getFlightTime() { return flightTime; }

    public String getEventType() {
        return eventType;
    }

    public float getPressure() {
        return pressure;
    }

    String toCSVString() {
        return eventType + "; " + eventTime + "; " + keyValue + "; " + x + "; " + y + "; " + offsetX
                + "; " + offsetY + "; " + keyCenterX + "; " + keyCenterY + "; " + orientation + "; "
                + touchMinor + "; " + touchMajor + "; " + size + "; " + holdTime + ";" + flightTime +
                ";" + pressure + "; " + isLongPressed + "; " + longPressKey + "\n";
    }

    @Override
    public String toString() {
        return "eventType: " + eventType + "; eventTime: " + eventTime + "; keyValue: " + keyValue +
                "; x: " + x + "; y: " + y + "; pressure: " + pressure + "; isLongPressed: " + isLongPressed + "\n";
    }
}