package com.photosynq.app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ImageButtonActivity extends ActionBarActivity {

	Context context;
	int i;
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_button);

		//LinearLayOut Setup
        LinearLayout linearLayout= new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        //ImageView Setup
        for(i=0;i<=3;i++)
        {
        ImageView imageView = new ImageView(this);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You Clicked On Image"+ i, 5).show();
            }
        });
        //setting image resource
        imageView.setImageResource(R.drawable.simple_leaf);
        //setting image position
        imageView.setLayoutParams(new LayoutParams(
LayoutParams.MATCH_PARENT,
LayoutParams.WRAP_CONTENT));

        //adding view to layout
        linearLayout.addView(imageView);
        //make visible to program
        setContentView(linearLayout);
        }
        
//		GridLayout gridLayout = (GridLayout)findViewById(R.id.tableGrid);
//
//	    gridLayout.removeAllViews();
//
//	    int total = 12;
//	    int column = 5;
//	    int row = total / column;
//	    gridLayout.setColumnCount(column);
//	    gridLayout.setRowCount(row + 1);
//	    for(int i =0, c = 0, r = 0; i < total; i++, c++)
//	    {
//	        if(c == column)
//	        {
//	            c = 0;
//	            r++;
//	        }
//	        ImageView oImageView = new ImageView(this);
//	        oImageView.setImageResource(R.drawable.ic_launcher);
//	        GridLayout.LayoutParams param =new GridLayout.LayoutParams();
//	        param.height = LayoutParams.WRAP_CONTENT;
//	        param.width = LayoutParams.WRAP_CONTENT;
//	        param.rightMargin = 5;
//	        param.topMargin = 5;
//	        param.setGravity(Gravity.CENTER);
//	        param.columnSpec = GridLayout.spec(c);
//	        param.rowSpec = GridLayout.spec(r);
//	        oImageView.setLayoutParams (param);
//	        gridLayout.addView(oImageView);
//	    }
        
        
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_button, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
