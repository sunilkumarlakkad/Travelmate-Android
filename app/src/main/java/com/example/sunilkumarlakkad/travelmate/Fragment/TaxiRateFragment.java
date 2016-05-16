package com.example.sunilkumarlakkad.travelmate.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.sunilkumarlakkad.travelmate.Model.TaxiFinderResponse;
import com.example.sunilkumarlakkad.travelmate.R;
import com.example.sunilkumarlakkad.travelmate.WebService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */

public class TaxiRateFragment extends Fragment implements OnMapReadyCallback, DirectionCallback {

    public interface OnTaxiServiceFindCallbacks {
        void onTaxiServiceFindSelected(String location, double latitude, double longitude);
    }

    public ProgressDialog mProgressDialog;
    private GoogleMap googleMap;
    private LatLng camera, origin, destination;

    private double[] mapData;

    private TextView txtFare, txtTime, txtDistance, txtlocation;
    private RelativeLayout rloTaxiRate;
    OnTaxiServiceFindCallbacks mCallback;
    String locationName = "";

    public static TaxiRateFragment newInstance(double[] mapData) {
        TaxiRateFragment taxiRateFragment = new TaxiRateFragment();
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("mapData", mapData);
        taxiRateFragment.setArguments(bundle);
        return taxiRateFragment;
    }

    public TaxiRateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setRetainInstance(true);
        mCallback = (OnTaxiServiceFindCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getDoubleArray("mapData") != null)
            mapData = getArguments().getDoubleArray("mapData");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_taxi_rate, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Fetching Taxi Fare....");

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();

        txtFare = (TextView) rootView.findViewById(R.id.tf_rate);
        rloTaxiRate = (RelativeLayout) rootView.findViewById(R.id.rloTaxiRate);
        txtTime = (TextView) rootView.findViewById(R.id.tf_time);
        txtDistance = (TextView) rootView.findViewById(R.id.tf_distance);
        txtlocation = (TextView) rootView.findViewById(R.id.tf_locationinfo);
        rloTaxiRate.setVisibility(View.GONE);
        origin = new LatLng(mapData[0], mapData[1]);
        destination = new LatLng(mapData[2], mapData[3]);
        camera = new LatLng((mapData[0] + mapData[2]) / 2, (mapData[1] + mapData[3]) / 2);

        String strOrigin = String.valueOf(mapData[0]) + "," + String.valueOf(mapData[1]);
        String strDestination = String.valueOf(mapData[2]) + "," + String.valueOf(mapData[3]);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        findFare(strOrigin, strDestination);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_taxiservice);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!locationName.equals(""))
                    mCallback.onTaxiServiceFindSelected(locationName, mapData[0], mapData[1]);
            }
        });
        return rootView;
    }

    public void requestDirection() {
        String serverKey = "AIzaSyBbriU6Y9hk4TxGbCrxcSmI-n-89rgD0lM";
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);
            googleMap.addMarker(new MarkerOptions().position(origin).icon(icon));
            googleMap.addMarker(new MarkerOptions().position(destination));
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 5, Color.parseColor("#303F9F")));

        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Log.d("Failer", t.getMessage());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 12));
    }

    public void findFare(String strOrigin, String strDestination) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.taxifarefinder.com/")
                .build();

        WebService service = retrofit.create(WebService.class);

        Call<TaxiFinderResponse> call = service.findRate("juDuguveHa3e", strOrigin, strDestination);
        call.enqueue(new Callback<TaxiFinderResponse>() {
            @Override
            public void onResponse(Call<TaxiFinderResponse> call, Response<TaxiFinderResponse> response) {
                TaxiFinderResponse b = response.body();
                requestDirection();
                txtTime.setText(getPrettyTime(b.getDuration()));
                txtDistance.setText(getPrettyDistance(b.getDistance()));
                txtFare.setText(String.format("$ %s", b.getTotal_fare()));
                String temp = "Per " + b.getRate_area() + " rates includes " + b.getTip_percentage() + "% tip";
                txtlocation.setText(temp);
                locationName = b.getRate_area();

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                rloTaxiRate.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<TaxiFinderResponse> call, Throwable t) {

            }
        });
    }

    String getPrettyTime(String strSecond) {
        int second = Integer.parseInt(strSecond);

        int minute = second / 60;
        int hour = 0;
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String result;
        if (hour == 0) {
            result = String.valueOf(minute) + " min";
        } else {
            result = String.valueOf(hour) + "H " + String.valueOf(minute) + " min";
        }
        return result;
    }

    String getPrettyDistance(String strDistance) {
        double dist = Double.parseDouble(strDistance);
        dist = dist * 0.000621371;
        int temp = (int) dist;
        return "(" + String.valueOf(temp) + " mi)";
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Taxi Rate");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
