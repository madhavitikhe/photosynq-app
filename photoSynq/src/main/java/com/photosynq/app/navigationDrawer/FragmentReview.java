package com.photosynq.app.navigationDrawer;

import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.photosynq.app.R;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Question;
import com.photosynq.app.utils.DataUtils;
import com.photosynq.app.utils.PrefUtils;

public class FragmentReview extends Fragment {
	
	private DatabaseHelper db;
	private String userId;
	private TableLayout questionsTableLayout;
	private String projectId;
	private Data data;
	
	public static FragmentReview newInstance() {
        Bundle bundle = new Bundle();
		FragmentReview fragment = new FragmentReview();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        userId = PrefUtils.getFromPrefs(getActivity() , PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        db = DatabaseHelper.getHelper(getActivity());
        projectId = db.getSettings(userId).getProjectId();
        AppSettings appSettings = db.getSettings(userId);

        questionsTableLayout = (TableLayout) rootView.findViewById(R.id.questionsTable);
        TextView tvMode = (TextView) rootView.findViewById(R.id.mode_text);
        TextView tvUser = (TextView) rootView.findViewById(R.id.user_text);
        TextView tvBluetoothId = (TextView) rootView.findViewById(R.id.connection_text);
        TextView tvProjectId = (TextView) rootView.findViewById(R.id.project_text);
        //TextView tvQuestions = (TextView) rootView.findViewById(R.id.tvQuestions);

        //Set cuurent settings
        if(null != appSettings.getModeType())
        {
            if (appSettings.getModeType().equals(Utils.APP_MODE_NORMAL))
            {
                tvMode.setText(getResources().getString(R.string.normal_mode));
            }
            else if(appSettings.getModeType().equals(Utils.APP_MODE_STREAMLINE))
            {
                tvMode.setText(getResources().getString(R.string.streamlined_mode));
            }
        }

        if(null != appSettings.getProjectId())
        {
            String loggedInUserName = PrefUtils.getFromPrefs(getActivity(), PrefUtils.PREFS_USER,null);
            tvUser.setText(loggedInUserName);

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(appSettings.connectionId);
            tvBluetoothId.setText(device.getName());


            String projectId = appSettings.getProjectId();
            tvProjectId.setText(db.getResearchProject(projectId).getName());


            List<Question> questions = db.getAllQuestionForProject(appSettings.getProjectId());
            TableRow row = new TableRow(getActivity());


            int maxLoop = 0;
            for (Question question : questions) {
                TextView tv = new TextView(getActivity());
                tv.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
                tv.setText(question.getQuestionText());
                tv.setLines(3);
                tv.setPadding(10, 10, 10, 10);
                tv.setBackgroundResource(R.drawable.border);
                row.addView(tv);
                data = db.getData(userId, projectId, question.getQuestionId());
                if(null != data.getType()){
                    String[] items = data.getValue().split(",");
                    if(data.getType().equals(Data.AUTO_INCREMENT))
                    {
                        int from = Integer.parseInt(items[0]);
                        int to = Integer.parseInt(items[1]);
                        int repeat = Integer.parseInt(items[2]);
                        if(maxLoop < ((to - (from-1))*repeat))
                        {
                            maxLoop = ((to - (from-1))*repeat);
                        }
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Please fill the Questions data", Toast.LENGTH_LONG).show();
                }
            }
            questionsTableLayout.addView(row);

            try{
                for (int i = 0; i < maxLoop; i++) {
                    TableRow rowOptions = new TableRow(getActivity());
                    for (Question question : questions) {
                        TextView tv = new TextView(getActivity());

                        // tv.setBackgroundColor(getResources().getColor(R.color.blue_dark));

                        data = db.getData(userId, projectId, question.getQuestionId());
                        if (data.getType().equals(Data.USER_SELECTED))
                        {
                            tv.setText("User");
                        }
                        else if (data.getType().equals(Data.FIXED_VALUE))
                        {
                            tv.setText(data.getValue());

                        }
                        else if (data.getType().equals(Data.AUTO_INCREMENT))
                        {
                            tv.setText(DataUtils.getAutoIncrementedValue(getActivity(), question.getQuestionId(), ""+i));
                        }
                        else if (data.getType().equals(Data.SCAN_CODE))
                        {
                            tv.setText("Scan");
                        }
                        tv.setPadding(10, 10, 10, 10);
                        tv.setBackgroundResource(R.drawable.border);
                        rowOptions.addView(tv);
                    }
                    questionsTableLayout.addView(rowOptions);
                }
            }
            catch(Exception e){

            }
        }
        else
        {
            Toast.makeText(getActivity(), "Please Select Project from list", Toast.LENGTH_LONG).show();
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
