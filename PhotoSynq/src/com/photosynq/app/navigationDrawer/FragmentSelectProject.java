package com.photosynq.app.navigationDrawer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.MainActivity;
import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;
import com.squareup.picasso.Picasso;

public class FragmentSelectProject extends Fragment{
	
	private String recordid = ""; 
	private boolean quick_measure;
	DatabaseHelper db;
    public static FragmentSelectProject newInstance() {
        Bundle bundle = new Bundle();

        FragmentSelectProject fragment = new FragmentSelectProject();
        fragment.setArguments(bundle);

        return fragment;
    }	
    
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		View rootView = inflater.inflate(R.layout.activity_project_description, container, false);
		
		
		db = DatabaseHelper.getHelper(getActivity());
		Bundle extras = getArguments();
		if (extras != null) {
			recordid = extras.getString(DatabaseHelper.C_ID);
			quick_measure = extras.getBoolean(MainActivity.QUICK_MEASURE);
			System.out.println(this.getClass().getName()+"############quickmeasure="+quick_measure);
			ResearchProject rp = db.getResearchProject(recordid);
			
			
			SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int screenWidth = displaymetrics.widthPixels;
			//int screenHeight = displaymetrics.heightPixels;
			
			TextView tvProjetTitle = (TextView) rootView.findViewById(R.id.project_name);
			TextView tvProjetDesc = (TextView) rootView.findViewById(R.id.project_desc);
			TextView tvStartDate = (TextView) rootView.findViewById(R.id.start_date);
			TextView tvEndDate = (TextView) rootView.findViewById(R.id.end_date);
			TextView tvBeta = (TextView) rootView.findViewById(R.id.beta);
			Button selectprojectBtn = (Button) rootView.findViewById(R.id.participate_btn);
			selectprojectBtn.setText("Select Project");
			selectprojectBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "Project is selected", 5).show();
					FragmentManager fm = getActivity().getSupportFragmentManager();
//				    fm.popBackStack();
//			        fm.popBackStackImmediate();
//					getActivity().finish();
				}
			});
			
			tvProjetTitle.setText(rp.getName());
			tvProjetTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(screenWidth*0.06));
			if(!"null".equals(rp.getDescription()))
			{
				tvProjetDesc.setText(rp.getDescription());
			}else{tvProjetDesc.setText(getResources().getString(R.string.no_data_found));}
			
			if(!"null".equals(rp.getStartDate()))
			{
				tvStartDate.setText(outputDate.format(CommonUtils.convertToDate(rp.getStartDate())));
			}else{tvStartDate.setText(getResources().getString(R.string.no_data_found));}
			
			if(!"null".equals(rp.getEndDate()))
			{
				tvEndDate.setText(outputDate.format(CommonUtils.convertToDate(rp.getEndDate())));
			}else{tvEndDate.setText(getResources().getString(R.string.no_data_found));}
			
			if(!	"null".equals(rp.getBeta()))
			{
				tvBeta.setText(rp.getBeta());
			}else{tvBeta.setText(getResources().getString(R.string.no_data_found));}
			ImageView imageview = (ImageView) rootView.findViewById(R.id.projectImage); 
			Picasso.with(getActivity()).load(rp.getImageUrl()).into(imageview);
			try {
				StringBuffer dataString = new StringBuffer();
				String[] projectProtocols = rp.getProtocols_ids().split(",");
				if(rp.getProtocols_ids().length() >=1)
				{
					//JSONArray protocolJsonArray = new JSONArray();
					for (String protocolId : projectProtocols) {
						Protocol protocol = db.getProtocol(protocolId);
						JSONObject detailProtocolObject = new JSONObject();
						detailProtocolObject.put("protocolid", protocol.getId());
						detailProtocolObject.put("protocol_name", protocol.getId());
						detailProtocolObject.put("macro_id", protocol.getMacroId());
						//protocolJsonArray.put(detailProtocolObject);
						dataString.append("\""+protocol.getId()+"\""+":"+detailProtocolObject.toString()+",");
						
					}
					String data = "var protocols={"+dataString.substring(0, dataString.length()-1) +"}";
					
					// Writing macros_variable.js file with protocol and macro relations
					System.out.println("######Writing macros_variable.js file:"+data);
					CommonUtils.writeStringToFile(getActivity(), "macros_variable.js",data);
				}
				else
				{
					Toast.makeText(getActivity(), "No protocols assigned to this project, cannot continue.", Toast.LENGTH_SHORT).show();
					//finish();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);		
		inflater.inflate(R.menu.menu, menu);
	}
}
