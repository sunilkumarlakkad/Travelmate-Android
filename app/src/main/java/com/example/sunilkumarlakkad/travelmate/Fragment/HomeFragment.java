package com.example.sunilkumarlakkad.travelmate.Fragment;


import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sunilkumarlakkad.travelmate.Adapter.PlaceAutocompleteAdapter;
import com.example.sunilkumarlakkad.travelmate.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */

public class HomeFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    AutoCompleteTextView mAutocompleteViewOrigin;

    Location location;
    public ProgressDialog mProgressDialog;

    public interface OnFindTaxiFareClickedCallbacks {
        void onFindTaxiFareClicked(double[] mapData);

        void toStartService(String location);
    }

    private static final String TAG = "Activity";


    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    RelativeLayout relativeLayout;
    private Activity mActivity;
    OnFindTaxiFareClickedCallbacks mCallback;
    private double originLatitude;
    private double originLongitude;
    private double destinationLatitude;
    private double destinationLongitude;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mCallback = (OnFindTaxiFareClickedCallbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.rloHomeFragment);
        relativeLayout.setVisibility(View.GONE);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Fetching Location....");

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();

        TranslateAnimation moveLeftToRight = new TranslateAnimation(20, 400, 0, 0);
        moveLeftToRight.setDuration(5000);
        moveLeftToRight.setRepeatCount(ValueAnimator.INFINITE);
        moveLeftToRight.setRepeatMode(ValueAnimator.RESTART);
        ImageView imgCar = (ImageView) rootView.findViewById(R.id.imgCar);
        imgCar.startAnimation(moveLeftToRight);

        mAutocompleteViewOrigin = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextViewOrigin);
        final AutoCompleteTextView mAutocompleteViewDestination = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextViewDestination);
        ImageView mImageViewOrigin = (ImageView) rootView.findViewById(R.id.imgViewClearOrigin);
        ImageView mImageViewDestination = (ImageView) rootView.findViewById(R.id.imgViewClearDestination);
        Button findTaxiButton = (Button) rootView.findViewById(R.id.button_findTaxi);


        mAutocompleteViewDestination.setOnItemClickListener(mAutocompleteDestinationClickListener);

        mAutocompleteViewOrigin.setOnItemClickListener(mAutocompleteOriginClickListener);
        mAdapter = new PlaceAutocompleteAdapter(mActivity, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteViewOrigin.setAdapter(mAdapter);

        mAutocompleteViewDestination.setAdapter(mAdapter);

        mImageViewOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteViewOrigin.setText("");
            }
        });

        mImageViewDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteViewDestination.setText("");
            }
        });

        findTaxiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double[] mapData = {originLatitude, originLongitude, destinationLatitude, destinationLongitude};
                mCallback.onFindTaxiFareClicked(mapData);
            }
        });
        return rootView;

    }

    private AdapterView.OnItemClickListener mAutocompleteOriginClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdateOriginPlaceDetailsCallback);

        }
    };

    private AdapterView.OnItemClickListener mAutocompleteDestinationClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdateDestinationPlaceDetailsCallback);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdateOriginPlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            originLatitude = place.getLatLng().latitude;
            originLongitude = place.getLatLng().longitude;
            places.release();
        }
    };

    private ResultCallback<PlaceBuffer> mUpdateDestinationPlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            destinationLatitude = place.getLatLng().latitude;
            destinationLongitude = place.getLatLng().longitude;
            places.release();
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        Toast.makeText(mActivity,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location l) {
        if (location == null) {
            location = l;
            currentLocationRequestCompleted(location);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        LocationRequest locationRequest = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null)
            currentLocationRequestCompleted(location);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    void currentLocationRequestCompleted(Location location) {
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                System.out.println(addresses.get(0).getAddressLine(0));
                System.out.println(addresses.get(0).toString());

                Address address = addresses.get(0);
                double currentLatitude = address.getLatitude();
                double currentLongitude = address.getLongitude();
                originLatitude = currentLatitude;
                originLongitude = currentLongitude;

                mAutocompleteViewOrigin.setText(String.format("%s, %s", address.getAddressLine(0), address.getLocality()));
                mCallback.toStartService(address.getLocality() + " " + address.getAdminArea());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        relativeLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Travelmate");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
