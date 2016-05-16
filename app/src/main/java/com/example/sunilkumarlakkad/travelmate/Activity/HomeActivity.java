package com.example.sunilkumarlakkad.travelmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.sunilkumarlakkad.travelmate.DownloadIntentService;
import com.example.sunilkumarlakkad.travelmate.Fragment.AttractionFragment;
import com.example.sunilkumarlakkad.travelmate.Fragment.HomeFragment;
import com.example.sunilkumarlakkad.travelmate.Fragment.NotesListFragment;
import com.example.sunilkumarlakkad.travelmate.Fragment.TaxiRateFragment;
import com.example.sunilkumarlakkad.travelmate.Fragment.TaxiServiceFragment;
import com.example.sunilkumarlakkad.travelmate.Fragment.YoutubeVideoListFragment;
import com.example.sunilkumarlakkad.travelmate.Model.VideoItem;
import com.example.sunilkumarlakkad.travelmate.R;
import com.example.sunilkumarlakkad.travelmate.Utility;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFindTaxiFareClickedCallbacks, YoutubeVideoListFragment.OnItemSelectCallbacks,
        TaxiRateFragment.OnTaxiServiceFindCallbacks, AttractionFragment.OnAttractionSelectionCallbacks {

    public static List<VideoItem> videoItemList = new ArrayList<>();
    public static List<Business> businessList = new ArrayList<>();
    Fragment mContent;
    DrawerLayout androidDrawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        androidDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, androidDrawerLayout, toolbar, R.string.open, R.string.close);
        androidDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBarDrawerToggle.syncState();

        View headerLayout = navigationView.getHeaderView(0);
        TextView txtUsername = (TextView) headerLayout.findViewById(R.id.nav_username);
        txtUsername.setText(Utility.Username);

        if (savedInstanceState != null)
            mContent = fragmentManager.getFragment(savedInstanceState, "mContent");
        else {
            mContent = HomeFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.container, mContent)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (mContent != null && mContent.isAdded())
            fragmentManager.putFragment(bundle, "mContent", mContent);
    }

    @Override
    public void onFindTaxiFareClicked(double[] mapData) {
        mContent = TaxiRateFragment.newInstance(mapData);
        fragmentManager.beginTransaction()
                .replace(R.id.container, mContent)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void toStartService(String location) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadIntentService.class);
        intent.putExtra("location", location);
        startService(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.taxi:
                mContent = HomeFragment.newInstance();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mContent)
                        .commit();
                break;
            case R.id.attraction:
                mContent = AttractionFragment.newInstance();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mContent)
                        .commit();
                break;

            case R.id.video:
                mContent = YoutubeVideoListFragment.newInstance();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mContent)
                        .commit();
                break;
            case R.id.notes:
                mContent = NotesListFragment.newInstance();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mContent)
                        .commit();
                break;
            default:
                break;
        }
        androidDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(String videoID) {
        Intent intent = new Intent(getApplication(), YoutubePlayerActivity.class);
        intent.putExtra("VIDEO_ID", videoID);
        startActivity(intent);
    }

    @Override
    public void onTaxiServiceFindSelected(String location, double latitude, double longitude) {

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadIntentService.class);
        intent.putExtra("location", location);
        startService(intent);


        Intent intent1 = new Intent(this, TaxiServiceInfoActivity.class);
        intent1.putExtra("locationName", location);
        intent1.putExtra("latitude", latitude);
        intent1.putExtra("longitude", longitude);
        startActivity(intent1);
    }

    @Override
    public void onAttractionSelected(int position) {
        mContent = TaxiServiceFragment.newInstance(HomeActivity.businessList.get(position));
        fragmentManager.beginTransaction()
                .replace(R.id.container, mContent)
                .addToBackStack(null)
                .commit();
    }
}
