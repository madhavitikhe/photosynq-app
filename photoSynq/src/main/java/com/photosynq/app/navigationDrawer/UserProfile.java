package com.photosynq.app.navigationDrawer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.photosynq.app.R;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;

public class UserProfile extends Activity {

    private String name;
    private String institute;
    private String avatar;
    private Typeface robotoRegular;
    private Typeface robotoMedium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        avatar = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_THUMB_URL_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        ImageView profileImage = (ImageView) findViewById(R.id.user_profile_image);
        Picasso.with(getApplicationContext())
                .load(avatar)
                .error(R.drawable.ic_launcher)
                .into(profileImage);

        robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");

        name = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_NAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        TextView tvLoggedUser = (TextView) findViewById(R.id.user_name);
        tvLoggedUser.setText(name);
        tvLoggedUser.setTypeface(robotoRegular);

        institute = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_INSTITUTE_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        TextView tvInstituteName = (TextView) findViewById(R.id.institute_name);
        tvInstituteName.setText(institute);
        tvInstituteName.setTypeface(robotoRegular);

        Button signOut = (Button) findViewById(R.id.sign_out_btn);
        signOut.setTypeface(robotoMedium);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),NavigationDrawer.class);
                intent.putExtra("finish", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

}
