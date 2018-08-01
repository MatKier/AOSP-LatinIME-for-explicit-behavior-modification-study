package com.android.inputmethod.ebmStudy.etc;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.inputmethod.ebmStudy.keyStrokeLogging.SimpleKeyStrokeDataBean;

import java.util.ArrayList;
import java.util.List;

public class StudyConfigBean implements Parcelable {
    private int groupId;
    private int taskId;
    private int numberOfReps;
    private ArrayList<SimpleKeyStrokeDataBean> pwTask;

    public StudyConfigBean(int groupId, int taskId, int numberOfReps, ArrayList<SimpleKeyStrokeDataBean> pwTask) {
        this.groupId = groupId;
        this.taskId = taskId;
        this.numberOfReps = numberOfReps;
        this.pwTask = pwTask;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getNumberOfReps() {
        return numberOfReps;
    }

    public List<SimpleKeyStrokeDataBean> getPwTask() {
        return pwTask;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(taskId);
        parcel.writeInt(numberOfReps);
        parcel.writeTypedList(pwTask);
    }

    private StudyConfigBean(Parcel in) {
        this.taskId = in.readInt();
        this.numberOfReps = in.readInt();
        this.pwTask = in.createTypedArrayList(SimpleKeyStrokeDataBean.CREATOR);
    }

    public static final Parcelable.Creator<StudyConfigBean> CREATOR = new Parcelable.Creator<StudyConfigBean>() {
        @Override
        public StudyConfigBean createFromParcel(Parcel source) {
            // return new StudyConfigBean(source.readInt(), source.readInt());
            return new StudyConfigBean(source);
        }

        @Override
        public StudyConfigBean[] newArray(int size) {
            return new StudyConfigBean[size];
        }
    };
}