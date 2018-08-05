package com.android.inputmethod.ebmStudy.etc;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.inputmethod.ebmStudy.keyStrokeLogging.SimpleKeyStrokeDataBean;

import java.util.ArrayList;
import java.util.List;

public class StudyConfigBean implements Parcelable {
    private int groupId;
    private boolean isIntroductionGroup;
    private int taskId;
    private int numberOfReps;
    private ArrayList<SimpleKeyStrokeDataBean> pwTask;

    public StudyConfigBean(int groupId, boolean isIntroductionGroup, int taskId, int numberOfReps, ArrayList<SimpleKeyStrokeDataBean> pwTask) {
        this.groupId = groupId;
        this.isIntroductionGroup = isIntroductionGroup;
        this.taskId = taskId;
        this.numberOfReps = numberOfReps;
        this.pwTask = pwTask;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isIntroductionGroup() {
        return isIntroductionGroup;
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

    public String getTaskPWString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pwTask.size(); i = i+2) {
            sb.append(pwTask.get(i).getKeyValue());
        }
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(groupId);
        parcel.writeByte((byte) (isIntroductionGroup ? 1 : 0));
        parcel.writeInt(taskId);
        parcel.writeInt(numberOfReps);
        parcel.writeTypedList(pwTask);
    }

    private StudyConfigBean(Parcel in) {
        this.groupId = in.readInt();
        this.isIntroductionGroup = in.readByte() == 1;
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