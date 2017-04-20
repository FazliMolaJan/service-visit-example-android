package com.hypertrack.service_visit_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.Window;

import com.hypertrack.service_visit_android.util.BaseActivity;
import com.hypertrack.service_visit_android.util.SharedPreferenceStore;

/**
 * Created by piyush on 21/03/17.
 */

public class SplashScreen extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Check if Driver is logged in currently
        String driverId = SharedPreferenceStore.getDriverId(this);
        if (TextUtils.isEmpty(driverId)) {

            // Initiate Driver Login by starting LoginActivity
            TaskStackBuilder.create(SplashScreen.this)
                    .addNextIntentWithParentStack(new Intent(SplashScreen.this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    .startActivities();
            finish();
        } else {

            // Start Driver Session by starting MainActivity
            TaskStackBuilder.create(SplashScreen.this)
                    .addNextIntentWithParentStack(new Intent(SplashScreen.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    .startActivities();
            finish();
        }
    }
}
