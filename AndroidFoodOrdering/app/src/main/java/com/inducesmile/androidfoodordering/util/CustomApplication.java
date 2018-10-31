package com.inducesmile.androidfoodordering.util;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.estimote.coresdk.service.BeaconManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.inducesmile.androidfoodordering.util.Helper.ESTIMOTE_APP_ID;
import static com.inducesmile.androidfoodordering.util.Helper.ESTIMOTE_APP_TOKEN;


public class CustomApplication extends Application {

    private static final String CHANNEL_ID = "evvone_app";
    private Gson gson;
    private GsonBuilder builder;

    private CustomSharedPreference shared;
    private ProximityObserver.Handler observationHandler;
    private BeaconManager beaconManager;
    private int NOTIFY_ID = 101;

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new GsonBuilder();
        gson = builder.create();
        shared = new CustomSharedPreference(getApplicationContext());
    }

    //initializing beacon connection and listeners
    public void initBeacon() {
        //notiTest();


        ProximityZone teaZone;

        //setting up beacon for your app
        EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials(ESTIMOTE_APP_ID, ESTIMOTE_APP_TOKEN);
        //initialising beacon detection parameters
        ProximityObserver proximityObserver = new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                .withBalancedPowerMode()
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        return null;
                    }
                })
                .build();

        //setting up tea zone
        teaZone = new ProximityZoneBuilder()
                .forTag("tea")
                .inFarRange()
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneEnter(proximityContext.getTag());
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneExit(proximityContext.getTag());
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

        //setting up drinks zone
        ProximityZone drinksZone = new ProximityZoneBuilder()
                .forTag("drinks")
                .inFarRange()
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneEnter(proximityContext.getTag());
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneExit(proximityContext.getTag());
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


        //setting up snacks zone
        ProximityZone snacksZone = new ProximityZoneBuilder()
                .forTag("snacks")
                .inFarRange()
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneEnter(proximityContext.getTag());
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        onZoneExit(proximityContext.getTag());
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

        List<ProximityZone> list = new ArrayList<>();
        list.add(teaZone);
        list.add(drinksZone);
        list.add(snacksZone);
        observationHandler = proximityObserver.startObserving(list);

    }

    //notification for zone enter
    private void onZoneExit(String tag) {
        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.favorite_icon)
                        .setContentTitle("You have exited " + tag + " zone")
                        .setAutoCancel(true)
                        .setChannelId(CHANNEL_ID).build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "food order channel", NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(NOTIFY_ID, notification);
    }


    //notification for zone enter
    private void onZoneEnter(String tag) {

        Intent intent = new Intent(CustomApplication.this, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1212, intent, PendingIntent.FLAG_ONE_SHOT);


        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.favorite_icon)
                        .setContentTitle("You have entered " + tag + " zone")
                        .setAutoCancel(true)
                        .addAction(R.drawable.favorite_icon, "Order Now!", pendingIntent)
                        .setChannelId(CHANNEL_ID).build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "food order channel", NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    //dummy test notification
    void notiTest() {

        // Sets an ID for the notification, so it can be updated.
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.

        Intent intent = new Intent(CustomApplication.this, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1212, intent, PendingIntent.FLAG_ONE_SHOT);


        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.favorite_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setAutoCancel(true)
                        .addAction(R.drawable.favorite_icon, "Order Food Now!", pendingIntent)
                        .setChannelId(CHANNEL_ID).build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "channel name here", NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(notifyID, notification);

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