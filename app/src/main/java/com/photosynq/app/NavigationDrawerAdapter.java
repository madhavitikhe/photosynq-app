package com.photosynq.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.photosynq.app.utils.CommonUtils;

/**
 * Created by kalpesh on 26/01/15.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int id;
    private String[] items;
    private int mSelectedPosition;

    public NavigationDrawerAdapter(Context context, int resourceId , String[] list )
    {
        super(context, resourceId, list);
        mContext = context;
        id = resourceId;
        items = list;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.tvNavigationItem);

        if(items[position] != null )
        {
            text.setTextColor(Color.WHITE);
            text.setText(items[position]);
            text.setTypeface(CommonUtils.getInstance(mContext).getFontRobotoLight());

            if(position == mSelectedPosition)
                ((View)text.getParent()).setBackgroundColor(mContext.getResources().getColor( R.color.green));
            else
                ((View)text.getParent()).setBackgroundColor(mContext.getResources().getColor( R.color.transparent));

        }

        return mView;
    }

    public void setItemSelected(int position) {
        mSelectedPosition = position;
    }
}
