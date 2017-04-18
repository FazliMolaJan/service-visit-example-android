package com.hypertrack.service_visit_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParams;
import com.hypertrack.lib.models.ActionParamsBuilder;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.Place;
import com.hypertrack.lib.models.SuccessResponse;
import com.hypertrack.service_visit_android.util.BaseActivity;
import com.hypertrack.service_visit_android.util.SharedPreferenceStore;

import java.util.Date;

public class MainActivity extends BaseActivity {

    private ProgressDialog mProgressDialog;
    // Click Listener for AcceptJob Button
    private View.OnClickListener acceptActionBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();

            Place expectedPlace = new Place().setLocation(28.56217, 77.16160)
                    .setAddress("HyperTrack, Vasant Vihar")
                    .setName("HyperTrack");
            // Create ActionParams object to define Action params
            ActionParams params = new ActionParamsBuilder()
                    .setExpectedPlace(expectedPlace)
                    .setExpectedAt(new Date())
                    .setType(Action.ACTION_TYPE_VISIT)
                    .build();

            // Call assignAction to start the tracking action
            HyperTrack.createAndAssignAction(params, new HyperTrackCallback() {
                @Override
                public void onSuccess(@NonNull SuccessResponse response) {


                    if (response.getResponseObject() != null) {
                        Action action = (Action) response.getResponseObject();

                        SharedPreferenceStore.setVisitActionId(MainActivity.this, action.getId());
                        createStopOverAction(action.getExpectedPlace().getId());

                        //Write your logic here

                    }
                }

                @Override
                public void onError(@NonNull ErrorResponse errorResponse) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }

                    Toast.makeText(MainActivity.this, "Action assigned failed: " + errorResponse.getErrorMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });


        }
    };
    // Click Listener for StartJob Button
    private View.OnClickListener startjobListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String visitActionId = SharedPreferenceStore.getVisitActionId(MainActivity.this);

            if (TextUtils.isEmpty(visitActionId)) {
                Toast.makeText(MainActivity.this, "VisitActionID is empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            HyperTrack.completeAction(visitActionId);

            Toast.makeText(MainActivity.this, "Job Started", Toast.LENGTH_SHORT).show();

            //Write your logic here

        }

    };
    //Click Listener for CloseJob Button
    private View.OnClickListener closejobBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String stopoverActionId = SharedPreferenceStore.getStopoverActionId(MainActivity.this);

            // Validate ActionId before complete action call
            if (TextUtils.isEmpty(stopoverActionId)) {
                Toast.makeText(MainActivity.this, "StopOverActionID is empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Complete Job
            HyperTrack.completeAction(stopoverActionId);

            Toast.makeText(MainActivity.this, "Job Closed Successfully", Toast.LENGTH_SHORT).show();

            //Write your logic here
        }
    };

    private void createStopOverAction(String placeID) {
        // Create ActionParams object to define Action params
        ActionParams stopOverParams = new ActionParamsBuilder()
                .setExpectedPlaceId(placeID)
                .setExpectedAt(new Date())
                .setType(Action.ACTION_TYPE_STOPOVER)
                .build();

        // Call assignAction to start the tracking action
        HyperTrack.createAndAssignAction(stopOverParams, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

                if (response.getResponseObject() != null) {
                    Action action = (Action) response.getResponseObject();
                    SharedPreferenceStore.setStopoverActionId(MainActivity.this, action.getId());

                    //Write your logic here

                    Toast.makeText(MainActivity.this, "Job (id = " + action.getId() + ") accepted successfully.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

                Toast.makeText(MainActivity.this, "Action assigned failed: " + errorResponse.getErrorMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar(getString(R.string.app_name), false);

        // Initialize UI Views
        initUIViews();

        /**
         * @IMPORTANT:
         * Implement Network call to fetch ORDERS/TRANSACTIONS for the User here.
         * Once the list of orders/transactions have been fetched, implement
         * assignAction and completeAction calls either with or without user interaction
         * depending on the specific requirements in the workflow of your business and your app.
         */
    }

    private void initUIViews() {
        // Initialize acceptjob Button
        Button acceptjob = (Button) findViewById(R.id.acceptjob);
        if (acceptjob != null)
            acceptjob.setOnClickListener(acceptActionBtnListener);

        // Initialize startJob Button
        Button startjob = (Button) findViewById(R.id.startjob);
        if (startjob != null)
            startjob.setOnClickListener(startjobListener);

        //Initialize closejob button
        Button closejob = (Button) findViewById(R.id.closejob);
        if (closejob != null)
            closejob.setOnClickListener(closejobBtnClickListener);
    }

    public void onLogoutClicked(MenuItem menuItem) {
        Toast.makeText(MainActivity.this, R.string.main_logout_success_msg, Toast.LENGTH_SHORT).show();

        // Stop HyperTrack SDK
        HyperTrack.stopTracking();

        // Proceed to LoginActivity for a fresh User Login
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
