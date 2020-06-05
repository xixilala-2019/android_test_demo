package com.demo.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by hc on 2019.1.11.
 */
public class MyLateralAdapter extends BaseAdapter {

    Context context;
    String[] texts;

    public MyLateralAdapter(Context context, String[] texts) {
        this.context = context;
        this.texts = texts;
    }

    @Override
    public int getCount() {
        return texts.length;
    }

    @Override
    public Object getItem(int position) {
        return texts[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = new TextView(context);
        }
        TextView textView = (TextView) convertView;
        textView.setText(texts[position]);
        return convertView;
    }

    public void setSelectIndex(int index) {

    }
}
