package com.brandonlehr.whendidiwork;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.services.DateFunctions;

import java.util.List;

/**
 * Created by blehr on 2/18/2018.
 */

public class CalendarEventListRecyclerViewAdapter extends RecyclerView.Adapter<CalendarEventListRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = "CalendarEventListRecycl";
    private List<Event> mEvents;
    Context mContext;
    private FirstScreenFragment.OnListFragmentInteractionListener mListener;

    public CalendarEventListRecyclerViewAdapter(List<Event> items, FirstScreenFragment.OnListFragmentInteractionListener listener) {
        mEvents = items;
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout event_item;
        TextView event_summary, event_start, event_end;

        public MyViewHolder(View itemView) {
            super(itemView);

            event_summary = itemView.findViewById(R.id.event_summary);
            event_start = itemView.findViewById(R.id.event_start);
            event_end = itemView.findViewById(R.id.event_end);
            event_item = itemView.findViewById(R.id.event_item);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Start ");
        Event mEvent = mEvents.get(position);
//        Log.d(TAG, "onBindViewHolder: mEvent:============= " + mEvent.toString());
        holder.event_summary.setText(mEvent.getSummary());
        if (mEvent.getStart() == null) {
            return;
        }
        if (mEvent.getStart().getDateTime() == null) {
            holder.event_start.setText(DateFunctions.displayDate(mEvent.getStart().getDate()));
            holder.event_end.setText("");
        } else {
            holder.event_start.setText(DateFunctions.displayDateTime(mEvent.getStart().getDateTime()));
            holder.event_end.setText(DateFunctions.displayDateTime(mEvent.getEnd().getDateTime()));
        }

        holder.event_item.setOnClickListener(v -> {
            Toast.makeText(mContext, mEvent.getSummary(), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

}
