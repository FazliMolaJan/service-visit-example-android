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
import java.util.UUID;

public class MainActivity extends BaseActivity {

    private ProgressDialog mProgressDialog;
    private View.OnClickListener acceptJobBtnListener, startJobBtnListener,
            closeJobBtnListener;

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
        // Initialize Click Listeners for Job buttons
        initClickListeners();

        // Initialize acceptJob Button
        Button acceptJob = (Button) findViewById(R.id.accept_job_btn);
        if (acceptJob != null)
            acceptJob.setOnClickListener(acceptJobBtnListener);

        // Initialize startJob Button
        Button startJob = (Button) findViewById(R.id.start_job_btn);
        if (startJob != null)
            startJob.setOnClickListener(startJobBtnListener);

        //Initialize closeJob button
        Button closeJob = (Button) findViewById(R.id.close_job_btn);
        if (closeJob != null)
            closeJob.setOnClickListener(closeJobBtnListener);
    }

    private void initClickListeners() {
        // Click Listener for AcceptJob Button
        acceptJobBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show Progress Dialog
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.show();

                /**
                 * You can specify a lookup_id to Actions which maps to your internal id of that
                 * service visit. This will help you search for the service visit on
                 * HyperTrack dashboard, and get custom views for the specific service visit.
                 *
                 * @NOTE: A randomly generated UUID is used as the lookup_id here. This will be the actual
                 * orderID in your case which will be fetched from either your server or generated locally.
                 */
                final String orderID = UUID.randomUUID().toString();

                /**
                 * Create Actions for current Service Visit with a given OrderID
                 */
                createActionsForJob(orderID);
            }
        };

        // Click Listener for StartJob Button
        startJobBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visitActionId = SharedPreferenceStore.getVisitActionId(MainActivity.this);

                if (TextUtils.isEmpty(visitActionId)) {
                    Toast.makeText(MainActivity.this, "VisitActionID is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Complete action using actionId for the Visit Action that you created
                HyperTrack.completeAction(visitActionId);

                Toast.makeText(MainActivity.this, "Job Started", Toast.LENGTH_SHORT).show();
            }
        };

        // Click Listener for CloseJob Button
        closeJobBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stopoverActionId = SharedPreferenceStore.getStopoverActionId(MainActivity.this);

                // Validate ActionId before complete action call
                if (TextUtils.isEmpty(stopoverActionId)) {
                    Toast.makeText(MainActivity.this, "StopOverActionID is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Complete action using actionId for the Stopover Action that you created
                HyperTrack.completeAction(stopoverActionId);

                Toast.makeText(MainActivity.this, "Job Closed Successfully", Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * This method creates and assigns VISIT and STOPOVER type actions to the current user on
     * HyperTrack API Server for the current job's orderID.
     *
     * @param orderID Internal order_id which maps to the current job being performed
     */
    private void createActionsForJob(String orderID) {
        /**
         * Construct a place object for Action's expected place
         *
         * @NOTE: Either the coordinates for the Service Visit's location
         * or it's address can be used to construct the expected place for the Action
         */
        Place expectedPlace = new Place().setLocation(28.56217, 77.16160)
                .setAddress("HyperTrack, Vasant Vihar")
                .setName("HyperTrack");

        /**
         * Create ActionParams object specifying the Visit Action parameters including ExpectedPlace,
         * ExpectedAt time and Lookup_id.
         */
        ActionParams visitTypeActionParams = new ActionParamsBuilder()
                .setExpectedPlace(expectedPlace)
                .setExpectedAt(new Date())
                .setType(Action.ACTION_TYPE_VISIT)
                .setLookupId(orderID)
                .build();

        /**
         * Call createAndAssignAction to assign Visit action to the current user configured
         * in the SDK using the ActionParams created above.
         */
        HyperTrack.createAndAssignAction(visitTypeActionParams, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                // Handle createAndAssignAction API success here
                Action action = (Action) response.getResponseObject();
                SharedPreferenceStore.setVisitActionId(MainActivity.this, action.getId());

                /**
                 * The VISIT Action just created has the tracking url which can be shared with your customers.
                 * This will enable the customer to live track the Service professional.
                 *
                 * @NOTE You can now share this tracking_url with your customers via an SMS
                 * or via your Customer app using in-app notifications.
                 */
                String trackingUrl = action.getTrackingURL();

                /**
                 * Yay! VISIT Type Action has been successfully created and assigned to current user.
                 * Now, we need to createAndAssignAction for STOPOVER Type Action using same
                 * expected place and same lookup_id.
                 */
                assignStopoverActionForJob(action.getExpectedPlace().getId(), action.getLookupId());
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

    /**
     * This method creates and assigns STOPOVER type action using given expectedPlaceId and lookup_id
     *
     * @param expectedPlaceID ExpectedPlace Id created on HyperTrack API Server for the VISIT type action.
     * @param orderId         Internal order_id which maps to the current job being performed.
     */
    private void assignStopoverActionForJob(String expectedPlaceID, final String orderId) {
        /**
         * Create ActionParams object specifying the Stopover Action parameters including
         * already created ExpectedPlaceId, ExpectedAt time and same Lookup_id as for the Visit type action.
         */
        ActionParams stopOverTypeActionParams = new ActionParamsBuilder()
                .setExpectedPlaceId(expectedPlaceID)
                .setExpectedAt(new Date())
                .setType(Action.ACTION_TYPE_STOPOVER)
                .setLookupId(orderId)
                .build();

        /**
         * Call createAndAssignAction to assign Stopover action to the current user configured
         * in the SDK using the ActionParams created above.
         */
        HyperTrack.createAndAssignAction(stopOverTypeActionParams, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

                // Handle createAndAssignAction API success here
                Action action = (Action) response.getResponseObject();
                SharedPreferenceStore.setStopoverActionId(MainActivity.this, action.getId());
                SharedPreferenceStore.setOrderID(MainActivity.this, orderId);

                Toast.makeText(MainActivity.this, "Job (id = " + action.getId() + ") accepted successfully.",
                        Toast.LENGTH_SHORT).show();
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

    /**
     * This method is called when Driver clicks on LOGOUT button in the toolbar. On Logout,
     * HyperTrack's stopTracking API is called to stop tracking the driver and
     * Note that this method is linked with the menu file (menu_main.xml)
     * using this menu item's onClick attribute. So no need to invoke this
     * method or handle logout button's click listener explicitly.
     *
     * @param menuItem
     */
    public void onLogoutClicked(MenuItem menuItem) {
        Toast.makeText(MainActivity.this, R.string.main_logout_success_msg, Toast.LENGTH_SHORT).show();

        // Stop tracking a user
        HyperTrack.stopTracking();

        // Clear Driver Session here
        SharedPreferenceStore.clearIDs(this);

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
