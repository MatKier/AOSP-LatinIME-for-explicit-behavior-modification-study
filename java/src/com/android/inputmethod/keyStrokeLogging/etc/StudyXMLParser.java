package com.android.inputmethod.keyStrokeLogging.etc;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class StudyXMLParser {

    public static void doTheThing(Context ctx) {
        try {
            InputStream is = ctx.getAssets().open("studyConfig/tasks.xml");

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            // Iterate over tasks
            NodeList taskList = doc.getElementsByTagName("task");
            for (int i = 0; i < taskList.getLength(); i++) {
                Node taskNode = taskList.item(i);
                if (taskNode.getNodeType() == Node.ELEMENT_NODE) {
                    // TODO Add Id + numberOFReps to data structure
                    Log.d("Config taskId: ", "" + ((Element) taskNode).getAttribute("taskId"));
                    Log.d("Config numberOfReps: ", "" + ((Element) taskNode).getAttribute("numberOfReps"));
                    // Iterate over letters
                    NodeList letterList = ((Element) taskNode).getElementsByTagName("letter");
                    for (int j = 0; j < letterList.getLength(); j++) {
                        Node letterNode = letterList.item(j);
                        // TODO Add Attributes to data structure
                        Log.d("Config char: ", "" + ((Element) letterNode).getAttribute("char"));
                        Log.d("Config offset: ", "" + ((Element) letterNode).getAttribute("offset"));
                        Log.d("Config area: ", "" + ((Element) letterNode).getAttribute("area"));
                        Log.d("Config htime: ", "" + ((Element) letterNode).getAttribute("htime"));
                        Log.d("Config ftime: ", "" + ((Element) letterNode).getAttribute("ftime"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}