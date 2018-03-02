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

import com.brandonlehr.whendidiwork.models.Sheet;

import java.util.List;

/**
 * Created by blehr on 2/14/2018.
 */

public class FileListAdapter extends ArrayAdapter {
    private static final String TAG = "FileListAdapter";

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<Sheet> items;
    private final int mResource;

    public FileListAdapter(@NonNull Context context, int resource, @NonNull List items) {
        super(context, resource, items);
        mContext = context;
        this.items = items;
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

        Sheet sheet = items.get(position);
        label.setText(sheet.getName());

        return view;
    }

    public int getIndexOfSheet(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (((Sheet) spinner.getItemAtPosition(i)).getId().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
