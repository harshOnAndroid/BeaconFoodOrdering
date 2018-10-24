package com.inducesmile.androidfoodordering;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.inducesmile.androidfoodordering.adapter.CustomViewPagerAdapter;
import com.inducesmile.androidfoodordering.entities.HotDealObject;
import com.inducesmile.androidfoodordering.network.GsonRequest;
import com.inducesmile.androidfoodordering.network.VolleySingleton;
import com.inducesmile.androidfoodordering.util.Helper;

import java.util.ArrayList;
import java.util.List;


public class HotDealActvity extends AppCompatActivity {

    private static final String TAG = HotDealActvity.class.getSimpleName();

    private ViewPager viewPager;

    private CustomViewPagerAdapter mAdapter;

    private RadioGroup radioGroup;

    private Handler handler;
    private final int delay = 2000;
    private int page = 0;

    Runnable runnable = new Runnable() {
        public void run() {
            if (mAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_deal_actvity);

        setTitle(getString(R.string.hot_deal_menus));

        radioGroup = (RadioGroup)findViewById(R.id.radiogroup);

        handler = new Handler();
        viewPager = (ViewPager)findViewById(R.id.hot_deal_view_pager);

        // get all hot deals from server
        getHotDealsFromRemoteServer();
    }

    private void getHotDealsFromRemoteServer(){
        GsonRequest<HotDealObject[]> serverRequest = new GsonRequest<HotDealObject[]>(
                Request.Method.GET,
                Helper.PATH_TO_HOT_DEALS,
                HotDealObject[].class,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(serverRequest);
    }

    private Response.Listener<HotDealObject[]> createRequestSuccessListener() {
        return new Response.Listener<HotDealObject[]>() {
            @Override
            public void onResponse(HotDealObject[] response) {
                try {
                    Log.d(TAG, "Json Response " + response.toString());
                    if(response.length > 0){
                        //display restaurant menu information
                        List<HotDealObject> hotDealList = new ArrayList<>();
                        for(int i = 0; i < response.length; i++){
                            Log.d(TAG, "Menu name " + response[i].getItem_name());
                            hotDealList.add(new HotDealObject(response[i].getItem_name(), response[i].getDescription(), response[i].getItem_price()));
                        }

                        mAdapter = new CustomViewPagerAdapter(HotDealActvity.this, hotDealList);
                        viewPager.setAdapter(mAdapter);
                        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            }

                            @Override
                            public void onPageSelected(int position) {
                                page = position;
                                switch (position){
                                    case 0:
                                        radioGroup.check(R.id.radioButton);
                                        break;
                                    case 1:
                                        radioGroup.check(R.id.radioButton2);
                                        break;
                                    case 2:
                                        radioGroup.check(R.id.radioButton3);
                                        break;
                                }
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });

                    }else{
                        Toast.makeText(HotDealActvity.this, "Restaurant menu is empty", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
