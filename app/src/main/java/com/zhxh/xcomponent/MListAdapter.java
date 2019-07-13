package com.zhxh.xcomponent;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.zhxh.xcomponent.dummy.ChartData;
import com.zhxh.xcomponentlib.CTextView;

import java.util.List;

public class MListAdapter extends BaseAdapter {

    private final List<ChartData> mValues;

    public MListAdapter(List<ChartData> items) {
        mValues = items;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MViewHolder viewHold = new MViewHolder();
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.fragment_list_item, null);
            viewHold.mContentView = convertView.findViewById(R.id.content);
            convertView.setTag(viewHold);
        } else {
            viewHold = (MViewHolder) convertView.getTag();
        }

        viewHold.mContentView
            .withText(mValues.get(position).getName())
            .withRegex("^.")
            .withColor(Color.RED)
            .withBack(s -> null);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(parent.getContext(), mValues.get(position).getName(), Toast.LENGTH_LONG).show();
            }
        });
        return convertView;


    }

    public class MViewHolder {
        public CTextView mContentView;
    }
}
