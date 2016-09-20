package com.jbaysolutions.tutorial.whatsappclone.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.jbaysolutions.tutorial.whatsappclone.comms.ServerFacade;

import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by rui on 9/14/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        try {
            ServerFacade.sendRegistrationToServer(this, refreshedToken);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }
    }
}