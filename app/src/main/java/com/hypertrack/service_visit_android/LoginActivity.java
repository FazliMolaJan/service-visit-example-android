package com.hypertrack.service_visit_android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;
import com.hypertrack.lib.models.User;
import com.hypertrack.service_visit_android.util.BaseActivity;
import com.hypertrack.service_visit_android.util.SharedPreferenceStore;

/**
 * This class can be used to enable Driver's Login flow in your app. This Activity consists of optional
 * input fields for Driver's Name and his phone number. Once the Location Services are enabled and
 * Location Permission has been granted, the driver can proceed with the login. This will be when a User
 * entity will be created on HyperTrack API Server and the SDK will be configured for this created User.
 *
 * Once a User has been created, you can start your Driver's session in the app and navigate to MainActivity
 * where he can receive, perform and complete the service visits assigned to him.
 */
public class LoginActivity extends BaseActivity {

    private EditText driverNameText, driverPhoneNumberText;
    private LinearLayout loginBtnLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Toolbar
        initToolbar(getString(R.string.login_activity_title), false);

        // Initialize UI Views
        initUIViews();
    }

    /**
     * Call this method to initialize UI views and handle listeners for these views
     */
    private void initUIViews() {
        // Initialize DriverName Views
        driverNameText = (EditText) findViewById(R.id.login_name);

        // Initialize PhoneNumber Views
        driverPhoneNumberText = (EditText) findViewById(R.id.login_phone_number);

        // Initialize Login Btn Loader
        loginBtnLoader = (LinearLayout) findViewById(R.id.login_driver_login_btn_loader);
    }

    /**
     * Call this method when Driver Login button has been clicked.
     * Note that this method is linked with the layout file (content_login.xml)
     * using this button's layout's onClick attribute. So no need to invoke this
     * method or handle login button's click listener explicitly.
     *
     * @param view
     */
    public void onLoginButtonClick(View view) {
        // Check if Location Settings are enabled, if yes then attempt DriverLogin
        checkForLocationSettings();
    }

    /**
     * Call this method to check Location Settings before proceeding for Driver Login
     */
    private void checkForLocationSettings() {
        // Check for Location permission
        if (!HyperTrack.checkLocationPermission(this)) {
            HyperTrack.requestPermissions(this);
            return;
        }

        // Check for Location settings
        if (!HyperTrack.checkLocationServices(this)) {
            HyperTrack.requestLocationServices(this, null);
        }

        // Location Permissions and Settings have been enabled
        // Proceed with your app logic here i.e Driver Login in this case
        attemptDriverLogin();
    }

    /**
     * Call this method to attempt driver login. This method will create a User on HyperTrack Server
     * and configure the SDK using this generated UserId.
     */
    private void attemptDriverLogin() {
        // Show Login Button loader
        loginBtnLoader.setVisibility(View.VISIBLE);

        // Get Driver details, if provided
        final String name = driverNameText.getText().toString();
        final String phoneNumber = driverPhoneNumberText.getText().toString();

        // PhoneNumber is used as the lookup_id here but you can specify any other entity as the lookup_id.
        final String lookupId = phoneNumber;

        /**
         * Create a User on HyperTrack Server here to login your driver & configure HyperTrack SDK with
         * this generated HyperTrack UserId.
         * OR
         * Implement your API call for Driver Login and get back a HyperTrack UserId from your API Server
         * to be configured in the HyperTrack SDK.
         *
         * @NOTE:
         * Specify Driver name, phone number and a lookup_id denoting your driver's internal id.
         * PhoneNumber is used as the lookup_id here but you can specify any other entity as the lookup_id.
         */
        HyperTrack.createUser(name, phoneNumber, lookupId, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                // Hide Login Button loader
                loginBtnLoader.setVisibility(View.GONE);

                User user = (User) successResponse.getResponseObject();
                // Handle createUser success here, if required
                // HyperTrack SDK auto-configures UserId on createUser API call, so no need to call
                // HyperTrack.setUserId() API

                // On DriverLogin success
                onDriverLoginSuccess();
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                // Hide Login Button loader
                loginBtnLoader.setVisibility(View.GONE);

                Toast.makeText(LoginActivity.this, R.string.login_error_msg + " " + errorResponse.getErrorMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Call this method when Driver has successfully logged in
     */
    private void onDriverLoginSuccess() {

        // To start tracking your driver, call HyperTrack's startTracking API
        HyperTrack.startTracking(new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                // Hide Login Button loader
                loginBtnLoader.setVisibility(View.GONE);

                // Handle startTracking API response
                SharedPreferenceStore.setDriverId(LoginActivity.this, HyperTrack.getUserId());
                Toast.makeText(LoginActivity.this, R.string.login_success_msg, Toast.LENGTH_SHORT).show();

                // Start Driver Session by starting MainActivity
                TaskStackBuilder.create(LoginActivity.this)
                        .addNextIntentWithParentStack(new Intent(LoginActivity.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        .startActivities();
                finish();
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                // Hide Login Button loader
                loginBtnLoader.setVisibility(View.GONE);

                Toast.makeText(LoginActivity.this, R.string.login_error_msg + " " + errorResponse.getErrorMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location Permission granted successfully, proceed with Driver Login flow
                checkForLocationSettings();

            } else {
                // Handle Location Permission denied error
                Toast.makeText(this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_SERVICES) {
            if (resultCode == Activity.RESULT_OK) {
                // Location Services enabled successfully, proceed with Driver Login flow
                checkForLocationSettings();

            } else {
                // Handle Enable Location Services request denied error
                Toast.makeText(this, R.string.enable_location_settings, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
