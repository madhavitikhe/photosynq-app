package com.photosynq.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.http.PhotosynqResponse;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.utils.BluetoothService;
import com.photosynq.app.utils.CommonUtils;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;
import com.photosynq.app.utils.SyncHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class QuickModeFragment extends Fragment implements PhotosynqResponse{

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static int mSectionNumber;

    private DatabaseHelper dbHelper;
    private ProtocolArrayAdapter arrayAdapter;
    private ListView protocolList;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static QuickModeFragment newInstance(int sectionNumber) {
        QuickModeFragment fragment = new QuickModeFragment();
        mSectionNumber = sectionNumber;
        return fragment;
    }

    public QuickModeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quick_mode, container, false);

        dbHelper = DatabaseHelper.getHelper(getActivity());

        // Initialize ListView
        protocolList = (ListView) rootView.findViewById(R.id.lv_protocol);
        showFewProtocolList();

        if(arrayAdapter.isEmpty())
        {
            MainActivity mainActivity = (MainActivity)getActivity();
            SyncHandler syncHandler = new SyncHandler(mainActivity, mainActivity.getProgressBar());
            syncHandler.DoSync(SyncHandler.PROTOCOL_LIST_MODE);
        }

        final Button showAllProtocolsBtn = (Button) rootView.findViewById(R.id.show_all_protocol_btn);
        showAllProtocolsBtn.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoMedium());
        showAllProtocolsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showAllProtocolsBtn.getText().equals("Show All Protocols")){
                    showAllProtocolList();
                    showAllProtocolsBtn.setText("Show Pre-Selected Protocols");
                }else if (showAllProtocolsBtn.getText().equals("Show Pre-Selected Protocols")){
                    showFewProtocolList();
                    showAllProtocolsBtn.setText("Show All Protocols");
                }
            }
        });

        protocolList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Protocol protocol = (Protocol) protocolList.getItemAtPosition(position);
                Log.d("GEtting protocol id : ", protocol.getId());
                Intent intent = new Intent(getActivity(),QuickMeasurmentActivity.class);
                intent.putExtra(DatabaseHelper.C_PROTOCOL_JSON, protocol.getProtocol_json());
                intent.putExtra(Protocol.NAME, protocol.getName());
                intent.putExtra(Protocol.DESCRIPTION, protocol.getDescription());
                try {
                    StringBuffer dataString = new StringBuffer();
                    dataString.append("var protocols={");
                    JSONObject detailProtocolObject = new JSONObject();
                    detailProtocolObject.put("protocolid", protocol.getId());
                    detailProtocolObject.put("protocol_name", protocol.getName());
                    detailProtocolObject.put("macro_id", protocol.getMacroId());
                    dataString.append("\"" + protocol.getId() + "\"" + ":" + detailProtocolObject.toString());
                    dataString.append("}");

                    //	System.out.println("###### writing macros_variable.js :"+dataString);
                    System.out.println("###### writing macros_variable.js :......");
                    CommonUtils.writeStringToFile(getActivity(), "macros_variable.js", dataString.toString());

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void showFewProtocolList() {
        List<Protocol> protocols = dbHelper.getFewProtocolList();
        arrayAdapter = new ProtocolArrayAdapter(getActivity(), protocols);
        protocolList.setAdapter(arrayAdapter);
    }

    private void showAllProtocolList() {
        List<Protocol> protocols = dbHelper.getAllProtocolsList();
        arrayAdapter = new ProtocolArrayAdapter(getActivity(), protocols);
        protocolList.setAdapter(arrayAdapter);
    }

    @Override
    public void onResponseReceived(String result) {

        if(result.equals(Constants.SERVER_NOT_ACCESSIBLE)){
            Toast.makeText(getActivity(), R.string.server_not_reachable, Toast.LENGTH_LONG).show();
        }else {
            showFewProtocolList();
            Toast.makeText(getActivity(), "Protocol list up to date", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(mSectionNumber);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ProtocolArrayAdapter extends BaseAdapter implements ListAdapter {

        public final Context context;
        public final List<Protocol> protocolList;
        LayoutInflater mInflater;

        public ProtocolArrayAdapter(Context context, List<Protocol> protocolList) {
            assert context != null;
            assert protocolList != null;

            this.protocolList = protocolList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (null == protocolList)
                return 0;
            else
                return protocolList.size();
        }

        @Override
        public Protocol getItem(int position) {
            if (null == protocolList)
                return null;
            else
                return protocolList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.protocol_list_item, null);

            TextView tvProtocolName = (TextView) convertView.findViewById(R.id.tv_protocol_name);
            tvProtocolName.setTypeface(CommonUtils.getInstance(getActivity()).getFontRobotoRegular());
            Protocol protocol = getItem(position);
            if (null != protocol) {
                try {
                    tvProtocolName.setText(protocol.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return convertView;
        }
    }
}