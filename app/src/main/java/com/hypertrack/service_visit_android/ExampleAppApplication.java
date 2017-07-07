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

        // Initialize HyperTrack SDK with your Publishable Key here
        // Refer to documentation at
        // https://docs.hypertrack.com/gettingstarted/authentication.html
        // @NOTE: Add **YOUR_PUBLISHABLE_KEY** here for SDK to be
        // authenticated with HyperTrack Server
        HyperTrack.initialize(this, <YOUR_PUBLISHABLE_KEY_HERE>);

        // Uncomment this to enable SDK's logging
        // HyperTrack.enableDebugLogging(Log.VERBOSE);
    }
}