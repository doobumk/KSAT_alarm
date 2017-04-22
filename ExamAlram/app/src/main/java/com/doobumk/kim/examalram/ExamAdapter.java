package com.doobumk.kim.examalram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by kim on 2017-03-03.
 */

public class ExamAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] text;
    private final int[] image;
    private LayoutInflater mInflater;

    public ExamAdapter(Context context, String[] text, int[] image){
        mContext = context;
        this.text = text;
        this.image = image;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount(){
        return text.length;
    }
    public Object getItem(int position){
        return null;
    }

    public long getItemId(int position){
        return 0;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_main, null);
            TextView textView = (TextView) grid.findViewById(R.id.charactor);
            ImageView imageView = (ImageView) grid.findViewById(R.id.picture);
            textView.setText(text[position]);
            imageView.setImageResource(image[position]);
        } else {
            grid = convertView;
        }
        return grid;
    }

}
