package com.brandonlehr.whendidiwork;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brandonlehr.whendidiwork.SheetListFragment.OnListFragmentInteractionListener;
import com.brandonlehr.whendidiwork.models.Sheet;

import java.util.List;

public class MySheetRecyclerViewAdapter extends RecyclerView.Adapter<MySheetRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MySheetRecyclerViewAdap";

    private final List<Sheet> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySheetRecyclerViewAdapter(List<Sheet> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        Log.d(TAG, "MySheetRecyclerViewAdapter: " + mValues);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sheet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getName());
//        holder.mContentView.setText(mValues.get(position).getId());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues == null) {
            return 0;
        }
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public Sheet mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
        }

    }
}
