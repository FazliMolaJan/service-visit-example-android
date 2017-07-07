package com.hypertrack.service_visit_android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by piyush on 30/09/16.
 */
public class SharedPreferenceStore {
    private static final String PREF_NAME = "com.hypertrack.example_android";
    private static final String DRIVER_ID_KEY = "driver_id";
    private static final String VISIT_ACTION_ID_KEY = "vist_action_id";
    private static final String STOPOVER_ACTION_ID_KEY = "stop_action_id";
    private static final String ORDER_ID_KEY = "order_id";

    public static void setDriverId(Context context, String driverID) {
        if (TextUtils.isEmpty(driverID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(DRIVER_ID_KEY, driverID);
        editor.commit();
    }

    public static void setOrderID(Context context, String orderID) {
        if (TextUtils.isEmpty(orderID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(ORDER_ID_KEY, orderID);
        editor.commit();
    }

    public static void setVisitActionId(Context context, String actionID) {
        if (TextUtils.isEmpty(actionID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(VISIT_ACTION_ID_KEY, actionID);
        editor.commit();
    }

    public static void setStopoverActionId(Context context, String actionID) {
        if (TextUtils.isEmpty(actionID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(STOPOVER_ACTION_ID_KEY, actionID);
        editor.commit();
    }

    public static String getDriverId(Context context) {
        return getSharedPreferences(context).getString(DRIVER_ID_KEY, null);
    }

    public static String getVisitActionId(Context context) {
        return getSharedPreferences(context).getString(VISIT_ACTION_ID_KEY, null);
    }

    public static String getStopoverActionId(Context context) {
        return getSharedPreferences(context).getString(STOPOVER_ACTION_ID_KEY, null);
    }

    public static String getOrderID(Context context) {
        return getSharedPreferences(context).getString(ORDER_ID_KEY, null);
    }

    public static void clearIDs(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(DRIVER_ID_KEY);
        editor.remove(ORDER_ID_KEY);
        editor.remove(STOPOVER_ACTION_ID_KEY);
        editor.remove(VISIT_ACTION_ID_KEY);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }
}
