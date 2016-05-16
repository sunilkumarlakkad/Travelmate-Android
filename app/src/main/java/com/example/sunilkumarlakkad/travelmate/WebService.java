package com.example.sunilkumarlakkad.travelmate;

import com.example.sunilkumarlakkad.travelmate.Model.TaxiFinderResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WebService {

    @GET("fare?")
    Call<TaxiFinderResponse> findRate(@Query("key") String key, @Query("origin") String origin, @Query("destination") String destination);

}
