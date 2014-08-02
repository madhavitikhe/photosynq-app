package com.photosynq.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.photosynq.app.HTTP.HTTPConnection;
import com.photosynq.app.HTTP.PhotosynqResponse;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements PhotosynqResponse {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.photosynq.android.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private HTTPConnection mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}
		setContentView(R.layout.activity_login);
		
		copyAssets();
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		mEmail = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		mPassword = PrefUtils.getFromPrefs(getApplicationContext() , PrefUtils.PREFS_LOGIN_PASSWORD_KEY, PrefUtils.PREFS_DEFAULT_VAL);
		if(!mEmail.equals(PrefUtils.PREFS_DEFAULT_VAL) && !mPassword.equals(PrefUtils.PREFS_DEFAULT_VAL) )
		{ 
			if(CommonUtils.isConnected(getApplicationContext()))
			{
				connect();
			}
			else
			{
				Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intent);
			}
		}
		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});


		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
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
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		// else if (mPassword.length() < 4) {
		// mPasswordView.setError(getString(R.string.error_invalid_password));
		// focusView = mPasswordView;
		// cancel = true;
		// }

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
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
			if(CommonUtils.isConnected(getApplicationContext()))
			{
				connect();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "No Internet connection available.", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void connect() {
		mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
		showProgress(true);
		mAuthTask = new HTTPConnection(mEmail, mPassword);
		mAuthTask.delegate = this;
		mAuthTask.execute(getApplicationContext(),HTTPConnection.PHOTOSYNQ_LOGIN_URL,"POST");
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public void onResponseReceived(String result) {
		//Destroy Async task and stop showing spinning wheel.
		mAuthTask = null;
		showProgress(false);
		if (null != result) {
			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
				CheckBox isSaveCredentials = (CheckBox) findViewById(R.id.save_credentials);
				if (isSaveCredentials.isChecked()) {
					// Save authentication values to preferences
					PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_LOGIN_USERNAME_KEY,jsonResult.get("email").toString());
					PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_LOGIN_PASSWORD_KEY, mPassword);
				}
				PrefUtils.saveToPrefs(getApplicationContext(),PrefUtils.PREFS_AUTH_TOKEN_KEY,jsonResult.get("auth_token").toString());
			} catch (JSONException e) {
				// TODO Log error
				e.printStackTrace();
			}
			
			Intent intent = new Intent(getApplicationContext(),MainActivity.class);
			startActivity(intent);
			// finish();
		}		else
		{
			mPasswordView.setError(getString(R.string.error_incorrect_password));
			mPasswordView.requestFocus();
		}
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
	    	if ( !filename.startsWith("images") && !filename.startsWith("sounds") && !filename.startsWith("webkit"))
	    	{
		        InputStream in = null;
		        OutputStream out = null;
		        try {
		          in = assetManager.open(filename);
		          File outFile = new File(getExternalFilesDir(null), filename);
		          out = new FileOutputStream(outFile);
		          copyFile(in, out);
		          in.close();
		          in = null;
		          out.flush();
		          out.close();
		          out = null;
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
