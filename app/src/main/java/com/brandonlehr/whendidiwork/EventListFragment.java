package com.brandonlehr.whendidiwork;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.viewModels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class EventListFragment extends Fragment {
    private static final String TAG = "EventListFragment";

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    List<Event> mEvents = new ArrayList<>();
    RecyclerView recyclerView;
    MyEventRecyclerViewAdapter adapter;
    Calendar mSelectedCalendar;
    private Paint p = new Paint();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    MainActivityViewModel model;


    public EventListFragment() {
    }

    public static EventListFragment newInstance(int columnCount) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((Whendidiwork) getActivity().getApplication()).getDIComponent().inject(this);

        model = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);

        model.subscribeToEvents().observe(this, events -> {
            mEvents.clear();
            if (events != null)
                mEvents.addAll(events);
            adapter.notifyDataSetChanged();
        });

        model.retrieveSelectedCalendar().observe(this, calendar -> mSelectedCalendar = calendar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new MyEventRecyclerViewAdapter(mEvents, mListener));
        adapter = (MyEventRecyclerViewAdapter) recyclerView.getAdapter();
        setUpItemTouchHelper();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Event item);
    }

    private void handleUndoSnackbar(int position, Event eventToDelete) {
        Snackbar undoSnackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator), "Undo", Snackbar.LENGTH_SHORT);

        undoSnackbar.setAction("Undo Delete", v -> {
            // undo the delete
            adapter.addBack(position, eventToDelete);
        });

        undoSnackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (event != DISMISS_EVENT_ACTION) {
                    // continue to delete
                    Log.d(TAG, "onDismissed: time to delete the event ");
                    model.deleteEvent(mSelectedCalendar.getId(), eventToDelete.getId());
                }
            }
            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
            }
        });
        undoSnackbar.show();
    }


    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Event swipedEvent = mEvents.get(position);
                Log.d(TAG, "onSwiped: SwipedEvent " + swipedEvent.toString());
                if (direction == ItemTouchHelper.LEFT) {
                    adapter.remove(position);
                    handleUndoSnackbar(position, swipedEvent);
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    Intent editIntent = new Intent(getActivity(), CreateEventActivity.class);
                    editIntent.putExtra("EVENT_ID_TO_EDIT", swipedEvent.getId());
                    startActivity(editIntent);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // not sure why, but this method get's called for viewholder that are already swiped away
                    if (viewHolder.getAdapterPosition() == -1) {
                        // not interested in those
                        return;
                    }

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_edit);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }


}
