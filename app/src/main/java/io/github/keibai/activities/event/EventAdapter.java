package io.github.keibai.activities.event;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;
import io.github.keibai.models.Event;

/**
 * EventAdapter class
 */

public class EventAdapter extends ArrayAdapter {

    public EventAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item,
                    parent, false);
        }

        Event currentEvent = (Event) getItem(position);

        TextView idTextView = listItemView.findViewById(R.id.text_event_id);
        idTextView.setText(String.valueOf(currentEvent.id));

        TextView nameTextView = listItemView.findViewById(R.id.text_event_name);
        nameTextView.setText(currentEvent.name);

        TextView locationTextView = listItemView.findViewById(R.id.text_event_location);
        locationTextView.setText(currentEvent.location);

        TextView statusTextView = listItemView.findViewById(R.id.text_event_status);
        statusTextView.setText(currentEvent.status);

        return listItemView;
    }
}
