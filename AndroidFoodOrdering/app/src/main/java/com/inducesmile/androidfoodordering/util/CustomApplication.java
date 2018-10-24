package com.inducesmile.androidfoodordering.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inducesmile.androidfoodordering.MainActivity;
import com.inducesmile.androidfoodordering.R;
import com.inducesmile.androidfoodordering.entities.CartObject;
import com.inducesmile.androidfoodordering.entities.LoginObject;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;


public class CustomApplication extends Application implements BootstrapNotifier
{

    private Gson gson;
    private GsonBuilder builder;

    private CustomSharedPreference shared;
    private BackgroundPowerSaver backgroundPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new GsonBuilder();
        gson = builder.create();
        shared = new CustomSharedPreference(getApplicationContext());
    }

    public void initBeacon(){
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("com.inducesmile.androidfoodordering", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    public CustomSharedPreference getShared(){
        return shared;
    }

    public Gson getGsonObject(){
        return gson;
    }

    public LoginObject getLoginUser(){
        Gson mGson = getGsonObject();
        String storedUser = getShared().getUserData();
        return mGson.fromJson(storedUser, LoginObject.class);
    }

    public int cartItemCount(){
        String orderList = getShared().getCartItems();
        CartObject[] allCart = getGsonObject().fromJson(orderList, CartObject[].class);
        if(allCart == null){
            return 0;
        }
        return allCart.length;
    }

        private RegionBootstrap regionBootstrap;

        @Override
        public void didDetermineStateForRegion(int arg0, Region arg1) {
            // Don't care
        }

        @Override
        public void didEnterRegion(Region arg0) {
            // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
            // if you want the Activity to launch every single time beacons come into view, remove this call.
            regionBootstrap.disable();
            Intent intent = new Intent(this, MainActivity.class);
            // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
            // created when a user launches the activity manually and it gets launched from here.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }

        @Override
        public void didExitRegion(Region arg0) {
            // Don't care
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.favorite_icon)
                    .setContentTitle("Beacon Exit")
                    .setContentText("You have just exited the beacon region.")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat.from(this).notify(0, mBuilder.build());

        }

}