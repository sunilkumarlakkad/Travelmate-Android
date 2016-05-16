package com.example.sunilkumarlakkad.travelmate.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sunilkumarlakkad.travelmate.R;
import com.example.sunilkumarlakkad.travelmate.Utility;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public ProgressDialog mProgressDialog;

    public interface LoginActivityCallbacks {
        void onRegisterClicked();

        void onLoginDone();
    }

    Firebase firebaseRef;

    EditText txtUsername, txtPassword;

    LoginActivityCallbacks mCallback;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setRetainInstance(true);
        mCallback = (LoginActivityCallbacks) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        firebaseRef = new Firebase(Utility.FIREBASEREF);

        txtUsername = (EditText) rootView.findViewById(R.id.txtUsername);
        txtPassword = (EditText) rootView.findViewById(R.id.txtPassword);

        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performLogin();
                    handled = true;
                }
                return handled;
            }
        });
        Button btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
        Button btnRegister = (Button) rootView.findViewById(R.id.btnRgister);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performLogin();
                }
            });
        }
        if (btnRegister != null) {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onRegisterClicked();
                }
            });
        }
        return rootView;
    }

    void performLogin() {
        firebaseRef.authWithPassword(txtUsername.getText().toString(), txtPassword.getText().toString(), hander);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating....");

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    final Firebase.AuthResultHandler hander = new Firebase.AuthResultHandler() {
        @Override
        public void onAuthenticated(AuthData authData) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            Log.d("Login", (String) authData.getProviderData().get("email"));
            Utility.Username = (String) authData.getProviderData().get("email");

            mCallback.onLoginDone();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            Snackbar snackbar = Snackbar.make(txtUsername, firebaseError.getMessage(), Snackbar.LENGTH_SHORT);
            snackbar.show();

        }
    };
}
