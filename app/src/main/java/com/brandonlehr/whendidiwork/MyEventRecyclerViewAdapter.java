package com.brandonlehr.whendidiwork;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brandonlehr.whendidiwork.EventListFragment.OnListFragmentInteractionListener;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.services.DateFunctions;

import org.joda.time.DateTime;

import java.util.List;

public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MyEventRecyclerViewAdap";

    private final List<Event> mValues;
    private final OnListFragmentInteractionListener mListener;
    String format = "yyyyMMdd";

    public MyEventRecyclerViewAdapter(List<Event> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mEvent = mValues.get(position);
        Event mEvent = mValues.get(position);
        holder.event_summary.setText(mEvent.getSummary());
        if (mEvent.getStart() == null) {
            return;
        }
        if (mEvent.getStart().getDateTime() == null) {
            holder.event_start.setText(DateFunctions.displayDate(mEvent.getStart().getDate()));
            holder.event_end.setText("");
        } else {
            String startDt = new DateTime(mEvent.getStart().getDateTime()).toString(format);
            String endDt = new DateTime(mEvent.getEnd().getDateTime()).toString(format);

            if (startDt.equals(endDt)) {
                holder.event_start.setText(DateFunctions.displayDate(mEvent.getStart().getDateTime()));

                String startTime = DateFunctions.displayTimes(mEvent.getStart().getDateTime());
                String endTime = DateFunctions.displayTimes(mEvent.getEnd().getDateTime());
                holder.event_end.setText(startTime + " - " + endTime);
            } else {
                holder.event_start.setText(DateFunctions.displayDateTime(mEvent.getStart().getDateTime()));
                holder.event_end.setText(DateFunctions.displayDateTime(mEvent.getEnd().getDateTime()));
            }
        }


        holder.event_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(mEvent);
                }
            }
        });
    }

    public void remove(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void addBack(int position, Event event) {
        mValues.add(position, event);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ConstraintLayout event_item;
        public final TextView event_summary, event_start, event_end;
        public Event mEvent;


        public ViewHolder(View view) {
            super(view);
            event_summary = itemView.findViewById(R.id.event_summary);
            event_start = itemView.findViewById(R.id.event_start);
            event_end = itemView.findViewById(R.id.event_end);
            event_item = itemView.findViewById(R.id.event_item);
        }

    }
}
