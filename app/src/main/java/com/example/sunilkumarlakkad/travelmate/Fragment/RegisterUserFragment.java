package com.example.sunilkumarlakkad.travelmate.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.sunilkumarlakkad.travelmate.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterUserFragment extends Fragment {

    public ProgressDialog mProgressDialog;
    Firebase firebaseRef;
    private static final String FIREBASEREF = "https://travelmatesunil.firebaseio.com/";

    private static final String USER_CREATION_SUCCESS = "Successfully created user";
    private static final String USER_CREATION_ERROR = "User creation error";
    private static final String EMAIL_INVALID = "email is invalid :";
    EditText txtUsername, txtPassword;

    public static RegisterUserFragment newInstance() {
        return new RegisterUserFragment();
    }

    public RegisterUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_register_user, container, false);
        firebaseRef = new Firebase(FIREBASEREF);

        txtUsername = (EditText) rootView.findViewById(R.id.txtUsername);
        txtPassword = (EditText) rootView.findViewById(R.id.txtPassword);

        Button btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
        if (btnRegister != null) {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setMessage("Authenticating....");

                    if (!mProgressDialog.isShowing())
                        mProgressDialog.show();
                    createUser();
                }
            });
        }
        return rootView;
    }

    public void createUser() {
        if (txtPassword.getText() == null || !isEmailValid(txtUsername.getText().toString())) {
            return;
        }
        firebaseRef.createUser(txtUsername.getText().toString(), txtPassword.getText().toString(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Snackbar snackbar = Snackbar.make(txtUsername, USER_CREATION_SUCCESS, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        getFragmentManager().popBackStack();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(txtUsername, USER_CREATION_ERROR, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
    }

    private boolean isEmailValid(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            txtUsername.setError(EMAIL_INVALID + email);
            return false;
        }
        return true;
    }
}
