package com.example.sunilkumarlakkad.travelmate;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.sunilkumarlakkad.travelmate.Activity.HomeActivity;
import com.example.sunilkumarlakkad.travelmate.Fragment.AttractionFragment;
import com.example.sunilkumarlakkad.travelmate.Fragment.YoutubeVideoListFragment;
import com.example.sunilkumarlakkad.travelmate.Model.VideoItem;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DownloadIntentService extends IntentService {

    public DownloadIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String searchTerm = intent.getStringExtra("location");

        YoutubeConnector yc = new YoutubeConnector(getApplicationContext());
        List<VideoItem> videoItemList = yc.search(searchTerm + " attractions");

        HomeActivity.videoItemList.clear();
        HomeActivity.videoItemList.addAll(videoItemList);
        if (YoutubeVideoListFragment.myYoutubeRecyclerAdapter != null)
            YoutubeVideoListFragment.myYoutubeRecyclerAdapter.notifyDataSetChanged();

        YelpAPIFactory apiFactory = new YelpAPIFactory(Utility.yelp_consumerKey, Utility.yelp_consumerSecret,
                Utility.yelp_token, Utility.yelp_tokenSecret);
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();
        params.put("term", "attraction");

        Call<SearchResponse> call = yelpAPI.search(searchTerm, params);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();
                List<Business> businessList = searchResponse.businesses();
                HomeActivity.businessList.clear();
                HomeActivity.businessList.addAll(businessList);
                if (AttractionFragment.mAdapter != null)
                    AttractionFragment.mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Failer", t.getMessage());
            }
        });
    }
}
