package com.android.inputmethod.ebmStudy.keyStrokeLogging;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleKeyStrokeDataBean implements Parcelable {
    private String eventType;
    private String keyValue;
    private int keyCenterX;
    private int keyCenterY;
    private int offsetX;
    private int offsetY;
    private long holdTime;

    private long flightTime;
    private float pressure;

    public SimpleKeyStrokeDataBean(String eventType, String keyValue, int keyCenterX, int keyCenterY, int offsetX,
                                   int offsetY, long holdTime, long flightTime, float pressure) {
        this.eventType = eventType;
        this.keyValue = keyValue;
        this.keyCenterX = keyCenterX;
        this.keyCenterY = keyCenterY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.holdTime = holdTime;
        this.flightTime = flightTime;
        this.pressure = pressure;
    }

    public String getEventType() {
        return eventType;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public int getKeyCenterX() {
        return keyCenterX;
    }

    public int getKeyCenterY() {
        return keyCenterY;
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

    public long getFlightTime() {
        return flightTime;
    }

    public float getPressure() {
        return pressure;
    }

    void setFlightTime(long flightTime) {
        this.flightTime = flightTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventType);
        parcel.writeString(keyValue);
        parcel.writeInt(keyCenterX);
        parcel.writeInt(keyCenterY);
        parcel.writeInt(offsetX);
        parcel.writeInt(offsetY);
        parcel.writeLong(holdTime);
        parcel.writeLong(flightTime);
        parcel.writeFloat(pressure);
    }

    private SimpleKeyStrokeDataBean(Parcel in) {
        this.eventType = in.readString();
        this.keyValue = in.readString();
        this.keyCenterX = in.readInt();
        this.keyCenterY = in.readInt();
        this.offsetX = in.readInt();
        this.offsetY = in.readInt();
        this.holdTime = in.readLong();
        this.flightTime = in.readLong();
        this.pressure = in.readFloat();
    }

    public static final Parcelable.Creator<SimpleKeyStrokeDataBean> CREATOR = new Parcelable.Creator<SimpleKeyStrokeDataBean>() {
        @Override
        public SimpleKeyStrokeDataBean createFromParcel(Parcel source) {
            // return new SimpleKeyStrokeDataBean(source.readString(), source.readString(), source.readInt(),
            //        source.readInt(), source.readLong(), source.readLong(), source.readFloat());
            return new SimpleKeyStrokeDataBean(source);
        }

        @Override
        public SimpleKeyStrokeDataBean[] newArray(int size) {
            return new SimpleKeyStrokeDataBean[size];
        }
    };

    /**
     * Returns a string with all the beans property values separated by a semicolon
     *
     * @return
     */
    String toCSVString() {
        return getEventType() + ";" + getKeyValue() + ";" + getOffsetX() + ";" + getOffsetY()
                + "; " + getKeyCenterX() + ";" + getKeyCenterY() + "; " + getHoldTime() + ";"
                + getFlightTime() + ";" + getPressure() + "\n";
    }

    String getCSVHeader() {
        return "eventType;keyValue;offsetX;offsetY;keyCenterX;keyCenterY;" +
                "holdTime;flightTime;pressure;\n";
    }
}