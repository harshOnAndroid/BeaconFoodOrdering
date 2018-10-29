package com.inducesmile.androidfoodordering.util;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inducesmile.androidfoodordering.IntroActivity;
import com.inducesmile.androidfoodordering.R;
import com.inducesmile.androidfoodordering.entities.CartObject;
import com.inducesmile.androidfoodordering.entities.LoginObject;

import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.inducesmile.androidfoodordering.util.Helper.ESTIMOTE_APP_ID;
import static com.inducesmile.androidfoodordering.util.Helper.ESTIMOTE_APP_TOKEN;


public class CustomApplication extends Application {

    private static final String CHANNEL_ID = "evvone";
    private Gson gson;
    private GsonBuilder builder;

    private CustomSharedPreference shared;
    private ProximityObserver.Handler observationHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new GsonBuilder();
        gson = builder.create();
        shared = new CustomSharedPreference(getApplicationContext());
    }

    public void initBeacon() {
        ProximityZone teaZone;
        EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials(ESTIMOTE_APP_ID, ESTIMOTE_APP_TOKEN);
        ProximityObserver proximityObserver = new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                .withBalancedPowerMode()
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        return null;
                    }
                })
                .build();

        teaZone = new ProximityZoneBuilder()
                .forTag("tea")
                .inFarRange()
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneEnter("tea");
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneExit();
                        return null;
                    }
                })
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> proximityZoneContexts) {
                        return null;
                    }
                })
                .build();

        ProximityZone drinksZone = new ProximityZoneBuilder()
                .forTag("drinks")
                .inFarRange()
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneEnter("drinks");
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneExit();
                        return null;
                    }
                })
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> proximityZoneContexts) {
                        return null;
                    }
                })
                .build();

        ProximityZone snacksZone = new ProximityZoneBuilder()
                .forTag("snacks")
                .inFarRange()
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneEnter("snacks");
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneExit();
                        return null;
                    }
                })
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> proximityZoneContexts) {
                        return null;
                    }
                })
                .build();

        observationHandler = proximityObserver.startObserving(teaZone);

    }

    private void onZoneExit() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(CustomApplication.this)
                .setSmallIcon(R.drawable.favorite_icon)
                .setContentTitle("Beacon Exit")
                .setContentText("You have just exited the beacon region.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(CustomApplication.this).notify(0, mBuilder.build());
    }

    private void onZoneEnter(String tag) {
        Intent intent = new Intent(CustomApplication.this, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CustomApplication.this.startActivity(intent);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1212, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(CustomApplication.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.favorite_icon)
                .setContentTitle("Beacon Exit")
                .setContentText("You have just exited the beacon region.")
                .addAction(R.drawable.favorite_icon, "launching app", pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(CustomApplication.this).notify(0, mBuilder.build());

        if (observationHandler != null)
            observationHandler.stop();
    }

    public CustomSharedPreference getShared() {
        return shared;
    }

    public Gson getGsonObject() {
        return gson;
    }

    public LoginObject getLoginUser() {
        Gson mGson = getGsonObject();
        String storedUser = getShared().getUserData();
        return mGson.fromJson(storedUser, LoginObject.class);
    }

    public int cartItemCount() {
        String orderList = getShared().getCartItems();
        CartObject[] allCart = getGsonObject().fromJson(orderList, CartObject[].class);
        if (allCart == null) {
            return 0;
        }
        return allCart.length;
    }


}