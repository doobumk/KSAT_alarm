package com.doobumk.kim.examalram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 2017-04-20.
 */

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> list = new ArrayList<>();

    public ListViewAdapter(ArrayList<ListViewItem> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.datalist,parent,false);
        }

        TextView time = (TextView)convertView.findViewById(R.id.text2);
        TextView percent = (TextView)convertView.findViewById(R.id.text3);
        TextView date = (TextView)convertView.findViewById(R.id.text4);

        ListViewItem listViewItem = list.get(position);


        time.setText(listViewItem.getTime());
        percent.setText(listViewItem.getPercent());
        date.setText(listViewItem.getDate());
        return convertView;
    }



}
