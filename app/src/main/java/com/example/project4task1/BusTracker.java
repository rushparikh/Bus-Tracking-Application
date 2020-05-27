package com.example.project4task1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class BusTracker extends AppCompatActivity {
    // in the spinner, default value is always set. just for UI purposes,
    // a dummy stop is created to select a null string.
    Stop nullStop = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final BusTracker bt = this;
        // Just for convenince
        nullStop = new Stop("-1", "");

        // As soon as the app loads show populate the routes dropdown which is the starting point of the app
        GetRoutes gr = new GetRoutes();
        gr.getRoutes("https://vast-plateau-66654.herokuapp.com/getroutes", this);

        // routes selector. To populate the data
        Spinner routesSpinner = (Spinner) findViewById(R.id.routes);
        // On routes select handler. Makes an API call to get the stops based on the route select
        routesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get relevant data
                String selectedRoute = (String) parentView.getItemAtPosition(position);
                Spinner directionsSpinner = (Spinner)findViewById(R.id.directions);
                String direction = directionsSpinner.getSelectedItem().toString();
                // Only if direction and the route is selected make an API call. This is a requirement ot the external API.
                if(!selectedRoute.equalsIgnoreCase("") && !direction.equalsIgnoreCase("")) {
                    GetStops gs = new GetStops();
                    gs.getStops("https://vast-plateau-66654.herokuapp.com/getstops?rt=" + selectedRoute +  "&dir=" + direction, bt, nullStop);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        // direction selector. Populate the dropdown. Boilerplate code..is used everywhere
        List<String> directions = new ArrayList<>();
        directions.add("");
        directions.add("INBOUND");
        directions.add("OUTBOUND");
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                this, android.R.layout.simple_spinner_item, directions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner directionsSpinner = (Spinner) findViewById(R.id.directions);
        directionsSpinner.setAdapter(adapter);

        // On directions select handler. Makes an API call to get the stops based on the route select
        directionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedDirection = (String) parentView.getItemAtPosition(position);
                Spinner routesSpinner = (Spinner)findViewById(R.id.routes);
                String route = "";
                // If route is null dont make an API call
                if(routesSpinner.getSelectedItem() != null) {
                    route = routesSpinner.getSelectedItem().toString();
                }
                // If direction is selected and route is selected then only make an api call
                if(!selectedDirection.equalsIgnoreCase("") && !route.equalsIgnoreCase("")) {
                    GetStops gs = new GetStops();
                    gs.getStops("https://vast-plateau-66654.herokuapp.com/getstops?rt=" + route +  "&dir=" + selectedDirection, bt, nullStop);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        // Submit button handler. To show the result
        Button submitButton = (Button) findViewById(R.id.button);

        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                Spinner stopsSpinner = (Spinner)findViewById(R.id.stops);
                Stop stop = (Stop)stopsSpinner.getSelectedItem();
                Spinner routesSpinner = (Spinner)findViewById(R.id.routes);
                String route = routesSpinner.getSelectedItem().toString();
                // If stop is selected and the route is selected then only make an api call. Requirement of the APi.
                if(!stop.id.equalsIgnoreCase("-1") && !route.equalsIgnoreCase(""))  {
                    GetPrediction gp =  new GetPrediction();
                    gp.getPredictions("https://vast-plateau-66654.herokuapp.com/getpredictions?rt=" + route +  "&stpid=" + stop.id, bt);
                }
            }
        });
    }

    /// When the route api is ready populate the stops dropdown. Again necessary code to populate it
    public void onRoutesReady(List<String> routes) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, routes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.routes);
        sItems.setAdapter(adapter);
    }

    // When the stop api is ready populate the stops dropdown. Again necessary code to populate it
    public void onStopsReady(final List<Stop> stops) {
        ArrayAdapter<Stop> adapter = new ArrayAdapter<Stop>(
                this, android.R.layout.simple_spinner_item, stops);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.stops);
        sItems.setAdapter(adapter);
    }

    public void onPredictionReady(List<Prediction> prediction) {
        // If no error is thrown...Populate the textbox.
        if(prediction != null && prediction.size() > 0 ) {
            // Get all the values of the spinners, show them to the user and then clear them
            Spinner routesSpinner = (Spinner) findViewById(R.id.routes);
            String route = routesSpinner.getSelectedItem().toString();

            Spinner stopsSpinner = (Spinner)findViewById(R.id.stops);
            String stop = stopsSpinner.getSelectedItem().toString();

            Spinner directionsSpinner = (Spinner)findViewById(R.id.directions);
            String direction = directionsSpinner.getSelectedItem().toString();

            // From the response construct the message that needs to be shown to the user.
            String[] dateTime =  prediction.get(0).prdtm.split(" ");
            String text = "The next " + direction + " " + route + " for the stop " + stop  + " is at " + dateTime[1];
            TextView predictionValue = (TextView) findViewById(R.id.prediction);
            predictionValue.setText(text);

            // reset all the dropdowns to empty string.
            ArrayAdapter<String> routesAdapter = (ArrayAdapter<String>) routesSpinner.getAdapter();
            int position = routesAdapter.getPosition("");
            routesSpinner.setSelection(position);

            ArrayAdapter<Stop> stopsAdapter = (ArrayAdapter<Stop>) stopsSpinner.getAdapter();
            position = stopsAdapter.getPosition(nullStop);
            stopsSpinner.setSelection(position);

            ArrayAdapter<String> directionsAdapter = (ArrayAdapter<String>) directionsSpinner.getAdapter();
            position = directionsAdapter.getPosition("");
            directionsSpinner.setSelection(position);

        } else {
            // Handle error and display the error to the user.
            TextView predictionValue = (TextView) findViewById(R.id.prediction);
            predictionValue.setText("No bus Service Found! Please try another");
        }
    }
}
