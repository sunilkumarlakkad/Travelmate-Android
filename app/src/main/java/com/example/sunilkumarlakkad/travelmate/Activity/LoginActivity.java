package com.example.sunilkumarlakkad.travelmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.sunilkumarlakkad.travelmate.Fragment.LoginFragment;
import com.example.sunilkumarlakkad.travelmate.Fragment.RegisterUserFragment;
import com.example.sunilkumarlakkad.travelmate.R;

public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginActivityCallbacks {
    Fragment mContent;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null)
            mContent = fragmentManager.getFragment(savedInstanceState, "mContent");
        else {
            mContent = LoginFragment.newInstance();
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
    public void onRegisterClicked() {
        mContent = RegisterUserFragment.newInstance();
        fragmentManager.beginTransaction()
                .add(R.id.container, mContent)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLoginDone() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
