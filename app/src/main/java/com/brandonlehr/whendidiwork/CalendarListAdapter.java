package com.brandonlehr.whendidiwork;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.brandonlehr.whendidiwork.models.Calendar;

import java.util.List;

/**
 * Created by blehr on 2/13/2018.
 */

public class CalendarListAdapter extends ArrayAdapter {
    private static final String TAG = "CalendarListAdapter";

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<Calendar> items;
    private final int mResource;

    public CalendarListAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContext = context;
        this.items = objects;
        mResource = resource;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);
        TextView label = view.findViewById(R.id.calendar_id);

        Calendar calendar = items.get(position);
        label.setText(calendar.getSummary());

        return view;
    }

    public int getIndexOfCalendar(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (((Calendar) spinner.getItemAtPosition(i)).getId().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
