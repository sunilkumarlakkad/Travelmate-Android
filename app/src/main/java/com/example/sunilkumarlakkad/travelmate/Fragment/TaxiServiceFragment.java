package com.example.sunilkumarlakkad.travelmate.Fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunilkumarlakkad.travelmate.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaxiServiceFragment extends Fragment implements OnMapReadyCallback {
    private Business business;
    private LatLng origin;

    public static TaxiServiceFragment newInstance(Business business) {
        TaxiServiceFragment taxiServiceFragment = new TaxiServiceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("business", business);
        taxiServiceFragment.setArguments(bundle);
        return taxiServiceFragment;
    }

    public TaxiServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments().getSerializable("business") != null)
            business = (Business) getArguments().getSerializable("business");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_taxi_service, container, false);

        TextView ftr_name = (TextView) rootView.findViewById(R.id.ftr_name);
        TextView ftr_categories = (TextView) rootView.findViewById(R.id.ftr_categories);
        TextView ftr_timing = (TextView) rootView.findViewById(R.id.ftr_timing);
        ImageView ftr_rating = (ImageView) rootView.findViewById(R.id.ftr_rating);
        TextView ftr_callInfo = (TextView) rootView.findViewById(R.id.ftr_callInfo);

        Picasso.with(getActivity()).load(business.ratingImgUrlLarge()).into(ftr_rating);
        ftr_name.setText(business.name());
        ftr_categories.setText(business.categories().get(0).name());
        if (business.isClosed()) {
            ftr_timing.setText(R.string.closed);
            ftr_timing.setTextColor(Color.RED);
        } else {
            ftr_timing.setText(R.string.open);
            ftr_timing.setTextColor(Color.GREEN);
        }

        ftr_callInfo.setText(String.format("Contact No: %s", business.displayPhone()));

        origin = new LatLng(business.location().coordinate().latitude(), business.location().coordinate().longitude());
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.ftr_map);
        supportMapFragment.getMapAsync(this);

        FloatingActionButton fab_call = (FloatingActionButton) rootView.findViewById(R.id.fab_call);
        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + business.phone();
                Intent in = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                try {
                    startActivity(in);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 14));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);
            googleMap.addMarker(new MarkerOptions().position(origin).icon(icon));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        try {
            Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.ftr_map));
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
