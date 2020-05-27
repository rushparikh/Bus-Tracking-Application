package com.example.project4task1;

import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// This class is to get the result for the next bus service
public class GetPrediction {
    BusTracker bt = null;

    public void getPredictions(String url, BusTracker bt) {
        this.bt = bt;
        new APICall().execute(url); // Async Task
    }

    private class APICall extends  AsyncTask<String, Void, List<Prediction>> {
        @Override
        protected List<Prediction> doInBackground(String... urls) {
            List<Prediction> prediction = new ArrayList<>(); // List to store all the next predictions
            Document doc = getRemoteXML(urls[0]);
            if(doc == null) {
                return null;
            }
            NodeList nl = doc.getElementsByTagName("prd"); //Gets an array of nodes for the next bust services
            for(int i=0; i<nl.getLength(); i++){
                Node node = nl.item(i);
                if(node instanceof Element) {
                    Element child = (Element) node;
                    String attribute = child.getElementsByTagName("prdtm").item(0).getTextContent(); // Get the exact time from the element prd
                    prediction.add(new Prediction(attribute)); //store it in the list to show it to the user
                }
            }
            return prediction;
        }

        protected void onPostExecute(List<Prediction> prediciton) {
            bt.onPredictionReady(prediciton); // Pass on this value to the Main Activity.
        }


        // Gets the data from the server and passes it back as amn XML
        private Document getRemoteXML(String url) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource(url);
                return db.parse(is);
            } catch (Exception e) {
                System.out.print("Yikes, hit the error: " + e);
                return null;
            }
        }
    }


}