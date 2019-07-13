package com.zhxh.xcomponent;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhxh.xchartlib.LineChart;
import com.zhxh.xcomponent.dummy.ChartData;
import com.zhxh.xcomponentlib.CTextView;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private final List<ChartData> mValues;
    private final ItemFragment.OnListFragmentInteractionListener mListener;

    public ItemRecyclerViewAdapter(List<ChartData> items, ItemFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.content
            .withText(mValues.get(position).getName())
            .withRegex("^.")
            .withColor(Color.RED)
            .withBack(s -> null);
        holder.lineChart.bindData(mValues.get(position).getList());
        holder.lineChart.bindAnimTime(50);
        holder.lineChart.show();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CTextView content;
        public final LineChart lineChart;
        public ChartData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            content = view.findViewById(R.id.content);
            lineChart = view.findViewById(R.id.lineChart);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + content.getText() + "'";
        }
    }
}
