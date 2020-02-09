package com.example.kyrznermanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.kyrznermanager.clases.Tag;

import java.util.ArrayList;

public class AdapterTag extends BaseAdapter {
    private Context mcontext;
    private ArrayList<Tag> mtags;

    @Override
    public int getCount() {
        return mtags.size();
    }

    @Override
    public Object getItem(int position) {
        return mtags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }
}
