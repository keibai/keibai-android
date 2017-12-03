package io.github.keibai.activities.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.keibai.R;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Event;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class ActiveEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_events);

        new Http(getApplicationContext()).get(HttpUrl.getEventListUrl(), new HttpCallback<Event[]>(Event[].class) {

            @Override
            public void onError(Error error) throws IOException {
                runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
            }

            @Override
            public void onSuccess(final Event[] response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventAdapter eventsAdapter = new EventAdapter(getApplicationContext(), Arrays.asList(response));
                        ListView listView = findViewById(R.id.active_events_list);
                        listView.setAdapter(eventsAdapter);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new RunnableToast(getApplicationContext(), e.toString()));
            }
        });
    }

}
