package com.android.inputmethod.ebmStudy.etc;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.inputmethod.ebmStudy.keyStrokeLogging.SimpleKeyStrokeDataBean;

import java.util.ArrayList;
import java.util.List;

public class StudyConfigBean implements Parcelable {
    private int featureCount;
    private int groupId;
    private String groupName;
    private String groupSortId;
    private boolean isIntroductionGroup;
    private int taskId;
    private String taskSortId;
    private int numberOfReps;
    private ArrayList<SimpleKeyStrokeDataBean> pwTask;

    public StudyConfigBean(int featureCount, int groupId, String groupName, String groupSortId, boolean isIntroductionGroup, int taskId, String taskSortId,
                           int numberOfReps, ArrayList<SimpleKeyStrokeDataBean> pwTask) {
        this.featureCount = featureCount;
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupSortId = groupSortId;
        this.isIntroductionGroup = isIntroductionGroup;
        this.taskId = taskId;
        this.taskSortId = taskSortId;
        this.numberOfReps = numberOfReps;
        this.pwTask = pwTask;
    }

    public int getFeatureCount() {
        return featureCount;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSortingGroupId() {
        return groupSortId;
    }

    public boolean isIntroductionGroup() {
        return isIntroductionGroup;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getSortingTaskId() {
        return taskSortId;
    }

    public int getNumberOfReps() {
        return numberOfReps;
    }

    public List<SimpleKeyStrokeDataBean> getPwTask() {
        return pwTask;
    }

    public String getTaskPWString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pwTask.size(); i = i + 2) {
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
        parcel.writeInt(featureCount);
        parcel.writeInt(groupId);
        parcel.writeString(groupName);
        parcel.writeString(groupSortId);
        parcel.writeByte((byte) (isIntroductionGroup ? 1 : 0));
        parcel.writeInt(taskId);
        parcel.writeString(taskSortId);
        parcel.writeInt(numberOfReps);
        parcel.writeTypedList(pwTask);
    }

    private StudyConfigBean(Parcel in) {
        this.featureCount = in.readInt();
        this.groupId = in.readInt();
        this.groupName = in.readString();
        this.groupSortId = in.readString();
        this.isIntroductionGroup = in.readByte() == 1;
        this.taskId = in.readInt();
        this.taskSortId = in.readString();
        this.numberOfReps = in.readInt();
        this.pwTask = in.createTypedArrayList(SimpleKeyStrokeDataBean.CREATOR);
    }

    public static final Creator<StudyConfigBean> CREATOR = new Creator<StudyConfigBean>() {
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