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


// This method gets all the routes in pittsburgh
public class GetRoutes {
    // Store the main activity object
    BusTracker bt = null;

    // Method that the main activity calls
    public void getRoutes(String url,BusTracker bt) {
        this.bt = bt;
        new APICall().execute(url);
    }
    //Async thread
    private class APICall extends AsyncTask<String, Void, List<String>> {
        @Override
        public List<String> doInBackground(String... urls) {
            List<String> routes = new ArrayList<>(); //List to store all the routes
            routes.add(""); // An empty string just to ensure that the first element is not set as the value. Only "" string is set
            Document doc = getRemoteXML(urls[0]); // gets the response to the request

            // Parsing the data from the XML and storing it tp the list
            NodeList nl = doc.getElementsByTagName("route");
            for(int i=0; i<nl.getLength(); i++){
                Node node = nl.item(i);
                if(node instanceof Element) {
                    Element child = (Element) node;
                    String attribute = child.getElementsByTagName("rt").item(0).getTextContent();
                    routes.add(attribute);
                }
            }

            return routes;
        }
        // Callback to the main activity when the thread has done its work.
        protected void onPostExecute(List<String> routes) {
            bt.onRoutesReady(routes);
        }

        // method to get the API response
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