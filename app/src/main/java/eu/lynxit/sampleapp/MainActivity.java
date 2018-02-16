package eu.lynxit.sampleapp;

import android.os.Bundle;

import org.gearvrf.GVRActivity;

public class MainActivity extends GVRActivity {
    private ApplicationMain main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = new ApplicationMain(this);
        setMain(main, "gvr.xml");
    }
}
