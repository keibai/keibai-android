package io.github.keibai.activities.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.github.keibai.R;

public class ActiveEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_events);

        // TODO: Change in next sprint, events will be retrieved using the API
        List<Event> events = new ArrayList<>();

        events.add(new Event(0, "Event 0", "Location 0"));
        events.add(new Event(1, "Event 1", "Location 1"));
        events.add(new Event(2, "Event 2", "Location 2"));
        events.add(new Event(3, "Event 3", "Location 3"));
        events.add(new Event(4, "Event 4", "Location 4"));
        events.add(new Event(5, "Event 5", "Location 5"));
        events.add(new Event(6, "Event 6", "Location 6"));
        events.add(new Event(7, "Event 7", "Location 7"));

        EventAdapter eventsAdapter = new EventAdapter(this, events);
        ListView listView = findViewById(R.id.active_events_list);
        listView.setAdapter(eventsAdapter);
    }

}
