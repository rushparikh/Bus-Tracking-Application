package com.example.project4task1;
import androidx.annotation.NonNull;

// Class to store the stop data structure. For Future Reference we might need to store other data
public class Stop {
    public String id;
    public String name;

    public Stop(String id, String name) {
        this.id = id;
        this.name = name;
    }
    // to string for the spinner. Shows the name of the stop.
    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
