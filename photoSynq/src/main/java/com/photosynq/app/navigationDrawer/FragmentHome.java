package com.photosynq.app.navigationDrawer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.photosynq.app.AlarmReceiver;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.utils.PrefUtils;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FragmentHome extends Fragment {

    private OnFragmentInteractionListener mListener;
    private DatabaseHelper db;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FragmentHome() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home, container, false);
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        db = DatabaseHelper.getHelper(getActivity());

        //When user install app first time following thing are set default.
        String first_run = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_FIRST_RUN, "YES");
        if (first_run.equals("YES"))
        {
            System.out.println("First time running? = YES");
            setAlarm(getActivity());
            String userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
            AppSettings appSettings = db.getSettings(userId);
            appSettings.setModeType(Utils.APP_MODE_NORMAL);
            db.updateSettings(appSettings);
            PrefUtils.saveToPrefs(getActivity(), PrefUtils.PREFS_FIRST_RUN,"NO");
            PrefUtils.saveToPrefs(getActivity(),PrefUtils.PREFS_SAVE_SYNC_INTERVAL,"2");
        }
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * This method sets interval time to executing sync in background and show notification to user.
     * time in milliseconds.
     * @param context
     */
    public void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent  alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),3600000*2, alarmIntent);//3600000*2 = 2 Hours
        System.out.println("-----------Alarm is set-------");
    }
}
