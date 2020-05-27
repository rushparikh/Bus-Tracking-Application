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

public class GetStops {

    BusTracker bt = null;
    Stop nullStop;
    List<Stop> stops = null;

    public void getStops(String url, BusTracker bt, Stop nullStop) {
        this.bt = bt;

        stops = new ArrayList<>();
        this.nullStop = nullStop;
        stops.add(this.nullStop);

        new APICall().execute(url);
    }

    private class APICall extends AsyncTask<String, Void, List<Stop>> {

        @Override
        public List<Stop> doInBackground(String... urls) {
            Document doc = getRemoteXML(urls[0]);
            NodeList nl = doc.getElementsByTagName("stop");
            for(int i=0; i<nl.getLength(); i++){
                Node node = nl.item(i);
                if(node instanceof Element) {
                    Element child = (Element) node;
                    String stopId = child.getElementsByTagName("stpid").item(0).getTextContent();
                    String stopName = child.getElementsByTagName("stpnm").item(0).getTextContent();
                    Stop stop = new Stop(stopId, stopName);
                    stops.add(stop);
                }
            }
            return stops;
        }

        protected void onPostExecute(List<Stop> stops) {
            bt.onStopsReady(stops);
        }


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