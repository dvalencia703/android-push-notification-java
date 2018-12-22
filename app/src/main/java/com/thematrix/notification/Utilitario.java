package com.thematrix.notification;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thematrix.notification.services.FirebaseRestService;

import java.util.HashMap;
import java.util.Map;

public final class Utilitario {

    public static String pushToken;


    public static String getDeviceId(Context ctx) {

        String deviceId = null;

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            if (ActivityCompat.checkSelfPermission(ctx.getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                deviceId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                deviceId = tm.getDeviceId();
            }

        } catch (Exception e) {
            e.printStackTrace();
            deviceId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return deviceId;
    }

    public static void registeringData(Context ctx, String name) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        Map<String, Object> input = new HashMap();
        Map<String, Object> data = new HashMap();
        data.put("push-token", pushToken);
        data.put("name", name);
        input.put(getDeviceId(ctx), data);

        myRef.updateChildren(input);
    }
}
