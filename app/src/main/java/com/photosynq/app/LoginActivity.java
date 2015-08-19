package com.photosynq.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.photosynq.app.http.HTTPConnection;
import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;

import io.fabric.sdk.android.Fabric;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements PhotosynqResponse {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private HTTPConnection mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // Data references.
    private String mStrEmail;
    private String mStrPassword;
    private boolean mIsChangeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        copyAssets();

        PrefUtils.saveToPrefs(getApplicationContext(), PrefUtils.PREFS_IS_SYNC_IN_PROGRESS, "false");

        mStrEmail = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        mStrPassword = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_PASSWORD_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        if(!mStrEmail.equals(PrefUtils.PREFS_DEFAULT_VAL) && !mStrPassword.equals(PrefUtils.PREFS_DEFAULT_VAL) ){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }else {

            mIsChangeUser = getIntent().getBooleanExtra("change_user", false);
            if (mIsChangeUser) {

                startLoginActivity();

            } else {

                startWelcomeActivity();
            }

        }
    }

    private void startWelcomeActivity() {

        setContentView(R.layout.activity_welcome);

        Typeface robotoLightFace = CommonUtils.getInstance(this).getFontRobotoLight();
        Typeface robotoMediumFace = CommonUtils.getInstance(this).getFontRobotoMedium();

        setShadeToAppName();

        TextView tvWelcomeDesc = (TextView) findViewById(R.id.txtWelDesc);
        tvWelcomeDesc.setTypeface(robotoLightFace);

        Button signIn = (Button) findViewById(R.id.sign_in_button);
        signIn.setTypeface(robotoMediumFace);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startLoginActivity();
            }
        });

        Button createNewAccount = (Button) findViewById(R.id.create_new_account_button);
        createNewAccount.setTypeface(robotoMediumFace);
        createNewAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uriUrl = Uri.parse(Constants.SERVER_URL + "users/sign_up");
                Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(intent);
            }
        });
    }

    private void startLoginActivity() {

        setContentView(R.layout.activity_login);

        setShadeToAppName();

        Typeface robotoRegularFace = CommonUtils.getInstance(this).getFontRobotoRegular();
        Typeface robotoMediumFace = CommonUtils.getInstance(this).getFontRobotoMedium();


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setTypeface(robotoRegularFace);
        mEmailView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEmailView, InputMethodManager.SHOW_IMPLICIT);

        if(!mStrEmail.equals(PrefUtils.PREFS_DEFAULT_VAL))
            mEmailView.setText(mStrEmail);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setTypeface(robotoRegularFace);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setTypeface(robotoMediumFace);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void setShadeToAppName() {

        Typeface uifontFace = CommonUtils.getInstance(this).getFontUiFontSolid();
        Typeface openSansLightFace = CommonUtils.getInstance(this).getFontOpenSansLight();

//        TextView tvIcon = (TextView) findViewById(R.id.txtAppIcon);
//        tvIcon.setTypeface(uifontFace);
//        TextView tvAppName = (TextView) findViewById(R.id.txtAppName);
//        tvAppName.setTypeface(openSansLightFace);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mStrEmail = mEmailView.getText().toString();
        mStrPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mStrPassword) && !isPasswordValid(mStrPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mStrEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(mStrEmail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new HTTPConnection(mStrEmail, mStrPassword);
            mAuthTask.delegate = this;
            mAuthTask.execute(getApplicationContext(), Constants.PHOTOSYNQ_LOGIN_URL,"POST", this);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onResponseReceived(String result) {
        //Destroy Async task and stop showing spinning wheel.
        mAuthTask = null;
        showProgress(false);

        if (null != result) {
            if(result.equals(Constants.SERVER_NOT_ACCESSIBLE))
            {
                Toast.makeText(getApplicationContext(), R.string.server_not_reachable, Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject jsonResult = null;
            try {
                jsonResult = new JSONObject(result);
                JSONObject userJsonObject = new JSONObject(jsonResult.get("user").toString());
                JSONObject creatorAvatar = userJsonObject.getJSONObject("avatar");

                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_LOGIN_USERNAME_KEY,userJsonObject.get("email").toString());
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_LOGIN_PASSWORD_KEY, mStrPassword);
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_AUTH_TOKEN_KEY,userJsonObject.get("auth_token").toString());
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_BIO_KEY,userJsonObject.get("bio").toString());
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_NAME_KEY,userJsonObject.get("name").toString());
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_INSTITUTE_KEY,userJsonObject.get("institute").toString());
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_CREATOR_ID,userJsonObject.getString("id").toString());
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_THUMB_URL_KEY,creatorAvatar.getString("original").toString());
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_PROJECTS,userJsonObject.get("projects").toString());
                PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_CONTRIBUTIONS,userJsonObject.get("contributions").toString());
            } catch (JSONException e) {
                // TODO Log error
                e.printStackTrace();
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }else
        {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            if ( !filename.startsWith("images") && !filename.startsWith("sounds") && !filename.startsWith("webkit") && !filename.startsWith("html"))
            {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File outFile = new File(getExternalFilesDir(null), filename);
                    if(outFile.exists() == false) {
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                        out.flush();
                        out.close();
                        out = null;
                    }
                    in.close();
                    in = null;
                } catch(IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}



