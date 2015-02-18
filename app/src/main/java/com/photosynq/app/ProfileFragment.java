package com.photosynq.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.PrefUtils;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        final Context context = getActivity();
        String imageUrl = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_THUMB_URL_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        ImageView profileImage = (ImageView) rootView.findViewById(R.id.user_profile_image);
        Picasso.with(context)
                .load(imageUrl)
                .error(R.drawable.ic_launcher)
                .into(profileImage);

        String strName = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_NAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        TextView tvLoggedUser = (TextView) rootView.findViewById(R.id.user_name);
        tvLoggedUser.setText(strName);
        tvLoggedUser.setTypeface(CommonUtils.getInstance(context).getFontRobotoRegular());

        String strInstitute = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_INSTITUTE_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        TextView tvInstituteName = (TextView) rootView.findViewById(R.id.institute_name);
        tvInstituteName.setText(strInstitute);
        tvInstituteName.setTypeface(CommonUtils.getInstance(context).getFontRobotoRegular());

        TextView tvContactLbl = (TextView) rootView.findViewById(R.id.tv_contact_lbl);
        tvContactLbl.setTypeface(CommonUtils.getInstance(context).getFontRobotoRegular());

        String strContactMailId = PrefUtils.getFromPrefs(context , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        TextView tvContact = (TextView) rootView.findViewById(R.id.tv_contact);
        tvContact.setText(strContactMailId);
        tvContact.setTypeface(CommonUtils.getInstance(context).getFontRobotoRegular());

        Button signOut = (Button) rootView.findViewById(R.id.sign_out_btn);
        signOut.setTypeface(CommonUtils.getInstance(context).getFontRobotoMedium());
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("change_user", true);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}