package com.photosynq.app.questions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.photosynq.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shekhar on 8/20/15.
 */
public class ImageSpinnerAdapter extends ArrayAdapter {
    List<String> imageList;
    Context context;

    public ImageSpinnerAdapter(Context context, int resource, List<String> imageList) {
        super(context, resource, imageList);
        this.imageList = imageList;
        this.context =context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return customView(position,convertView,parent);
    }

    private View customView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_image_text, parent, false);
        TextView tv = (TextView)view.findViewById(R.id.spinner_item_text);
        ImageView iv = (ImageView)view.findViewById(R.id.spinner_item_image);
        String[] splitOptionText = imageList.get(position).split(",");
        tv.setText(splitOptionText[0]);
        Picasso.with(context)
                .load(splitOptionText[1])
                .placeholder(R.drawable.ic_launcher1)
                .resize(100,100)
                .error(R.drawable.ic_launcher1)
                .into(iv);
        return view;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return customView(position,convertView,parent);
    }
}
