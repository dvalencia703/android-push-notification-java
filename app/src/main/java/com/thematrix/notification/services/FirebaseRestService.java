package com.thematrix.notification.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GetTokenResult;
import com.thematrix.notification.HomeActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class FirebaseRestService {

    private static final String TAG = "FirebaseRestService";

    private static RequestQueue queue;
    private static String activityClass;

    public static void sendNotification(Activity currentActivity, String deviceToken, Map<String, Object> info) {

        Map<String, Object> requestData = new HashMap();
        requestData.put("to", deviceToken);

        requestData.put("data", info);


        FirebaseRestService.invoke(currentActivity, Request.Method.POST, new JSONObject(requestData), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "rest onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "rest onErrorResponse: " + error.toString());
            }
        });
    }


    public static void invoke(
                                Activity currentActivity,
                                int method,
                                JSONObject request,
                                Response.Listener<JSONObject> successListener,
                                Response.ErrorListener errorListener) {
        try {

            if (activityClass == null || !activityClass.equals(currentActivity.getClass().toString())) {

                if (queue != null) {
                    queue.getCache().clear();
                    queue.stop();
                }

                activityClass = currentActivity.getClass().toString();
                queue = Volley.newRequestQueue(currentActivity);
            }

            // prepare the Request
            JsonObjectRequest getRequest = getRequest = new JsonObjectRequest(method, "https://fcm.googleapis.com/fcm/send",
                    request, successListener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "key=AIzaSyC3lmvaGdFyeP7sr1xkf9Dzr962PRQbRco");
                    return params;
                }
            };

            // Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
            // Volley does retry for you if you have specified the policy.
            getRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(getRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
