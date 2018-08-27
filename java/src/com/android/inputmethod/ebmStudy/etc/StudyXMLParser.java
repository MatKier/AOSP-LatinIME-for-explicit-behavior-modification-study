package com.android.inputmethod.ebmStudy.etc;

import android.content.Context;

import com.android.inputmethod.ebmStudy.etc.keyStrokeVisualizer.KeyStrokeVisualizerView;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.SimpleKeyStrokeDataBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class StudyXMLParser {
    private static final String TAG_TASK = "task";
    private static final String ATTR_TASK_ID = "taskId";
    private static final String ATTR_NUM_REPS = "numberOfReps";
    private static final String TAG_LETTER = "letter";
    private static final String ATTR_CHAR = "char";
    private static final String ATTR_OFFSET = "offset";
    private static final String ATTR_VAL_OFFSET_CENTER = "center";
    private static final String ATTR_VAL_OFFSET_LEFT = "left";
    private static final String ATTR_VAL_OFFSET_RIGHT = "right";
    private static final String ATTR_VAL_OFFSET_TOP = "top";
    private static final String ATTR_VAL_OFFSET_BOTTOM = "bottom";
    private static final String ATTR_AREA = "area";
    private static final String ATTR_VAL_AREA_SMALL = "small";
    private static final String ATTR_VAL_AREA_BIG = "big";
    private static final String ATTR_HTIME = "htime";
    private static final String ATTR_VAL_TIME_SHORT = "short";
    private static final String ATTR_VAL_TIME_LONG = "long";
    private static final String ATTR_VAL_TIME_DEFAULT = "default";
    private static final String ATTR_FTIME = "ftime";
    private static final String ATTR_GROUP_ID = "groupId";
    private static final String ATTR_GROUP_NAME = "name";
    private static final String ATTR_IS_INTRODUCTION_GROUP = "isIntroductionGroup";
    private static final String TAG_TASK_GROUP = "taskGroup";
    private static final String TAG_FEATURE_GROUP = "featureGroup";
    private static final String ATTR_FEATURE_COUNT = "featureCount";
    private static final String ATTR_SORTING_GROUP_ID = "sortingGroupId";
    private static final String ATTR_SORTING_TASK_ID = "sortingTaskId";


    public static ArrayList<StudyConfigBean> parseStudyConfig(Context ctx, Integer xmlNo) throws Exception {
        InputStream is;
        if(xmlNo != null) {
            is = ctx.getAssets().open("studyConfig/latinSquareBalanced/tasks_" + xmlNo + ".xml");
        } else {
            is = ctx.getAssets().open("studyConfig/tasks_short.xml");
        }
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        ArrayList<StudyConfigBean> studyConfigList = new ArrayList<>();

        // Iterate over tasks
        NodeList taskList = doc.getElementsByTagName(TAG_TASK);
        for (int i = 0; i < taskList.getLength(); i++) {
            Node taskNode = taskList.item(i);
            if (taskNode.getNodeType() == Node.ELEMENT_NODE) {
                Element taskGroupNode = (Element) taskNode.getParentNode();
                int groupId = Integer.parseInt(taskGroupNode.getAttribute(ATTR_GROUP_ID));
                String groupName = taskGroupNode.getAttribute(ATTR_GROUP_NAME);
                String sortingGroupId = taskGroupNode.getAttribute(ATTR_SORTING_GROUP_ID);

                boolean isIntroductionGroup = Boolean.parseBoolean(taskGroupNode.getAttribute(ATTR_IS_INTRODUCTION_GROUP));

                Element featureGroupNode = (Element) taskGroupNode.getParentNode();
                int featureCount = Integer.parseInt(featureGroupNode.getAttribute(ATTR_FEATURE_COUNT));

                int taskId = Integer.parseInt(((Element) taskNode).getAttribute(ATTR_TASK_ID));
                String sortingTaskId = ((Element) taskNode).getAttribute(ATTR_SORTING_TASK_ID);
                if (taskId != i + 1) {
                    throw new Exception("taskId: " + taskId + " [Task order is wrong]");
                }
                int numOfReps = Integer.parseInt(((Element) taskNode).getAttribute(ATTR_NUM_REPS));
                if (numOfReps < 1) {
                    throw new Exception("taskId: " + taskId + " [numberOfReps must not be lower than 1]");
                }

                ArrayList<SimpleKeyStrokeDataBean> keyStrokeTask = new ArrayList<>();

                // Iterate over letters in task
                NodeList letterList = ((Element) taskNode).getElementsByTagName(TAG_LETTER);
                for (int j = 0; j < letterList.getLength(); j++) {
                    Node letterNode = letterList.item(j);
                    String keyChar = ((Element) letterNode).getAttribute(ATTR_CHAR);
                    if (keyChar.length() != 1) {
                        throw new Exception("taskId: " + taskId + " [Char length != 1: " + keyChar + "]");
                    }

                    String offsetString = ((Element) letterNode).getAttribute(ATTR_OFFSET);
                    int offSetX = 0;
                    int offSetY = 0;
                    if (offsetString.equals(ATTR_VAL_OFFSET_CENTER)) {
                        offSetX = 0;
                        offSetY = 0;
                    } else if (offsetString.equals(ATTR_VAL_OFFSET_LEFT)) {
                        offSetX = -145;
                    } else if (offsetString.equals(ATTR_VAL_OFFSET_RIGHT)) {
                        offSetX = 145;
                    } else if (offsetString.equals(ATTR_VAL_OFFSET_TOP)) {
                        offSetY = 220;
                    } else if (offsetString.equals(ATTR_VAL_OFFSET_BOTTOM)) {
                        offSetY = -220;
                    } else {
                        throw new Exception("taskId: " + taskId + " [Unsupported offset: " + offsetString + "]");
                    }

                    String areaString = ((Element) letterNode).getAttribute(ATTR_AREA);
                    float area = 0.0f;
                    if (areaString.equals(ATTR_VAL_AREA_SMALL)) {
                        area = 0.20f;
                    } else if (areaString.equals(ATTR_VAL_AREA_BIG)) {
                        area = 0.45f;
                    } else {
                        throw new Exception("taskId: " + taskId + " [Unsupported area: " + areaString + "]");
                    }

                    String hTimeString = ((Element) letterNode).getAttribute(ATTR_HTIME);
                    int holdTime = 0;
                    if (hTimeString.equals(ATTR_VAL_TIME_LONG)) {
                        holdTime = KeyStrokeVisualizerView.MAX_HOLD_TIME_MS;
                    } else if (hTimeString.equals(ATTR_VAL_TIME_SHORT)) {
                        holdTime = KeyStrokeVisualizerView.DEFAULT_HOLD_TIME_MS;
                    } else if (hTimeString.equals(ATTR_VAL_TIME_DEFAULT)) {
                        holdTime = KeyStrokeVisualizerView.DEFAULT_HOLD_TIME_MS;
                    } else {
                        throw new Exception("taskId: " + taskId + " [Unsupported htime: " + hTimeString + "]");
                    }

                    int flightTime;
                    if (j != 0) {
                        String fTimeString = ((Element) letterNode).getAttribute(ATTR_FTIME);
                        flightTime = 0;
                        if (fTimeString.equals(ATTR_VAL_TIME_LONG)) {
                            flightTime = KeyStrokeVisualizerView.MAX_FLIGHT_TIME_MS;
                        } else if (fTimeString.equals(ATTR_VAL_TIME_SHORT)) {
                            flightTime = KeyStrokeVisualizerView.DEFAULT_FLIGHT_TIME_MS;
                        } else if (fTimeString.equals(ATTR_VAL_TIME_DEFAULT)) {
                            flightTime = KeyStrokeVisualizerView.DEFAULT_FLIGHT_TIME_MS;
                        } else {
                            throw new Exception("taskId: " + taskId + " [Unsupported ftime: " + fTimeString + "]");
                        }
                    } else {
                        flightTime = -1;
                    }

                    keyStrokeTask.add(new SimpleKeyStrokeDataBean(StudyConstants.EVENT_TYPE_DOWN, keyChar, offSetX, offSetY, -1, flightTime, area));
                    keyStrokeTask.add(new SimpleKeyStrokeDataBean(StudyConstants.EVENT_TYPE_UP, keyChar, offSetX, offSetY, holdTime, -1, area));
                }
                studyConfigList.add(new StudyConfigBean(featureCount, groupId, groupName, sortingGroupId, isIntroductionGroup, taskId, sortingTaskId, numOfReps, keyStrokeTask));
            }
        }
        return studyConfigList;
    }
}