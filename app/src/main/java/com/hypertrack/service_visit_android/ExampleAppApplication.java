package com.hypertrack.service_visit_android;

import android.app.Application;

import com.hypertrack.lib.HyperTrack;

/**
 * Created by piyush on 23/09/16.
 */
public class ExampleAppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize HyperTrack SDK with the Publishable Key
        // Refer to documentation at https://docs.hypertrack.com/v3/gettingstarted/authentication.html#publishable-key
        // @NOTE: Add **YOUR_PUBLISHABLE_KEY** here for SDK to be authenticated with HyperTrack Server
        HyperTrack.initialize(this, < YOUR_PUBLISHABLE_KEY_HERE >)
    }
}
