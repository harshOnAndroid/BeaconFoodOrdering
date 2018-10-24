package com.inducesmile.notification;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;


public class CustomsFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = CustomsFirebaseInstanceIDService.class.getSimpleName();

    private RequestQueue queue;

    private TokenObject tokenObject;

    private MySharedPreference mySharedPreference;

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token Values: " + refreshedToken);

        sendTheRegisteredTokenToWebServer(refreshedToken);
    }

    private void sendTheRegisteredTokenToWebServer(final String token){
        queue = Volley.newRequestQueue(getApplicationContext());
        mySharedPreference = new MySharedPreference(getApplicationContext());

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_TOKEN_STORAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                tokenObject = gson.fromJson(response, TokenObject.class);

                if (null == tokenObject) {
                    Toast.makeText(getApplicationContext(), "Can't send a token to the server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(false);

                } else {
                    Toast.makeText(getApplicationContext(), "Token successfully send to server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(true);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Helper.TOKEN_TO_SERVER, token);
                return params;
            }
        };
        queue.add(stringPostRequest);
    }
}
