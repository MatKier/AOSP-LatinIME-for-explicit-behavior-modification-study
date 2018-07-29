package com.android.inputmethod.ebmStudy.etc;

import android.content.Context;

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
    private static final String ATTR_FTIME = "ftime";


    public static ArrayList<StudyConfigBean> doTheThing(Context ctx) throws Exception {
        InputStream is = ctx.getAssets().open("studyConfig/tasks.xml");

        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        ArrayList<StudyConfigBean> studyConfigList = new ArrayList<>();

        // Iterate over tasks
        NodeList taskList = doc.getElementsByTagName(TAG_TASK);
        for (int i = 0; i < taskList.getLength(); i++) {
            Node taskNode = taskList.item(i);
            if (taskNode.getNodeType() == Node.ELEMENT_NODE) {
                int taskId = Integer.parseInt(((Element) taskNode).getAttribute(ATTR_TASK_ID));
                int numOfReps = Integer.parseInt(((Element) taskNode).getAttribute(ATTR_NUM_REPS));

                ArrayList<SimpleKeyStrokeDataBean> keyStrokeTask = new ArrayList<>();

                // Iterate over letters in task
                NodeList letterList = ((Element) taskNode).getElementsByTagName(TAG_LETTER);
                for (int j = 0; j < letterList.getLength(); j++) {
                    Node letterNode = letterList.item(j);
                    String keyChar = ((Element) letterNode).getAttribute(ATTR_CHAR);
                    if (keyChar.length() != 1) {
                        throw new Exception("Char length != 1: " + keyChar);
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
                        throw new Exception("Unsupported offset: " + offsetString);
                    }

                    String areaString = ((Element) letterNode).getAttribute(ATTR_AREA);
                    float area = 0.0f;
                    if (areaString.equals(ATTR_VAL_AREA_SMALL)) {
                        area = 0.0f;
                    } else if (areaString.equals(ATTR_VAL_AREA_BIG)) {
                        area = 0.5f;
                    } else {
                        throw new Exception("Unsupported area: " + areaString);
                    }

                    String hTimeString = ((Element) letterNode).getAttribute(ATTR_HTIME);
                    int holdTime = 0;
                    if (hTimeString.equals(ATTR_VAL_TIME_LONG)) {
                        holdTime = 10000;
                    } else if (hTimeString.equals(ATTR_VAL_TIME_SHORT)) {
                        holdTime = 0;
                    } else {
                        throw new Exception("Unsupported htime: " + hTimeString);
                    }

                    int flightTime;
                    if(j != 0) {
                        String fTimeString = ((Element) letterNode).getAttribute(ATTR_FTIME);
                        flightTime = 0;
                        if (fTimeString.equals(ATTR_VAL_TIME_LONG)) {
                            flightTime = 10000;
                        } else if (fTimeString.equals(ATTR_VAL_TIME_SHORT)) {
                            flightTime = 0;
                        } else {
                            throw new Exception("Unsupported ftime: " + fTimeString);
                        }
                    } else {
                        flightTime = -1;
                    }

                    keyStrokeTask.add(new SimpleKeyStrokeDataBean(StudyConstants.EVENT_TYPE_DOWN, keyChar, offSetX, offSetY, -1, flightTime, area));
                    keyStrokeTask.add(new SimpleKeyStrokeDataBean(StudyConstants.EVENT_TYPE_UP, keyChar, offSetX, offSetY, holdTime, -1, area));
                }
                studyConfigList.add(new StudyConfigBean(taskId, numOfReps, keyStrokeTask));
            }
        }
        return studyConfigList;
    }
}