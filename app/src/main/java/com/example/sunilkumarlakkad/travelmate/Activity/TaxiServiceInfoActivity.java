package com.example.sunilkumarlakkad.travelmate.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.example.sunilkumarlakkad.travelmate.Adapter.MyFragmentPagerAdapter;
import com.example.sunilkumarlakkad.travelmate.R;
import com.example.sunilkumarlakkad.travelmate.Utility;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class TaxiServiceInfoActivity extends AppCompatActivity {

    public static List<Business> businessList = new ArrayList<>();
    public ProgressDialog mProgressDialog;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    ViewPager viewPager;
    String locationName = "";
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_searvice_info);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Fetching Taxi Services....");

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();

        Intent intent = getIntent();
        if (intent != null) {
            locationName = intent.getStringExtra("locationName");
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Taxi Services");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        try {
            viewPager.setAdapter(myFragmentPagerAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager.setCurrentItem(0);
        viewPager.setPageTransformer(true, new AccordionTransformer());
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        YelpAPIFactory apiFactory = new YelpAPIFactory(Utility.yelp_consumerKey, Utility.yelp_consumerSecret,
                Utility.yelp_token, Utility.yelp_tokenSecret);
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();
        params.put("term", "taxi");
        if (latitude != 0.0 && longitude != 0.0)
            params.put("cll", String.valueOf(latitude) + "," + String.valueOf(longitude));


        Call<SearchResponse> call = yelpAPI.search(locationName, params);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();
                businessList = searchResponse.businesses();
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                myFragmentPagerAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(0);
                Log.d("taxi", String.valueOf(searchResponse.businesses().size()));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Failer", t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
    }
}
