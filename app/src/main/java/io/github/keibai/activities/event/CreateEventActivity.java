package io.github.keibai.activities.event;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;

import io.github.keibai.R;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Event;
import io.github.keibai.models.Model;
import io.github.keibai.models.meta.Error;
import io.github.keibai.models.meta.Msg;
import okhttp3.Call;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = findViewById(R.id.toolbar_create_event);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_create_event_save:
                onSave();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Event eventFromForm() {
        EditText formName = findViewById(R.id.edit_activity_create_name);
        Spinner formAuctionType = findViewById(R.id.spinner_event_type);
        EditText formTime = findViewById(R.id.edit_auction_create_time);
        EditText formLocation = findViewById(R.id.edit_activity_create_location);

        Event event = new Event();
        event.name = formName.getText().toString();
        event.auctionType = String.valueOf(formAuctionType.getSelectedItem());
        event.auctionTime = Integer.valueOf(formTime.getText().toString());
        event.location = formLocation.getText().toString();
        event.category = formLocation.getText().toString();

        return event;
    }

    public boolean validateForm() {
        EditText formName = findViewById(R.id.edit_activity_create_name);
        Spinner formAuctionType = findViewById(R.id.spinner_event_type);
        EditText formTime = findViewById(R.id.edit_auction_create_time);
        EditText formLocation = findViewById(R.id.edit_activity_create_location);

        if (formName.getText().toString().length() == 0) {
            formName.setError(formName.getHint() + " is required.");
            return false;
        } else {
            formName.setError(null);
        }

        if (formTime.getText().toString().length() == 0) {
            formTime.setError(formTime.getHint() + " is required.");
            return false;
        } else {
            formTime.setError(null);
        }

        if (formLocation.getText().toString().length() == 0) {
            formLocation.setError(formLocation.getHint() + " is required.");
            return false;
        } else {
            formLocation.setError(null);
        }

        return true;
    }

    public void onSave() {
        if (!validateForm()) {
            return;
        }

        Event attemptEvent = eventFromForm();
        new Http(getApplicationContext()).post(HttpUrl.newEventUrl(), attemptEvent, new HttpCallback<Event>() {
            @Override
            public Class model() {
                return Event.class;
            }

            @Override
            public void onError(Error error) throws IOException {
                System.out.println("Error " + error);
            }

            @Override
            public void onSuccess(Event response) throws IOException {
                System.out.println(response);
                Intent intent = new Intent(getApplicationContext(), DetailEventActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call call, IOException  e) {
                System.out.println("Unexpected error " + e.toString());
            }
        });
    }
}
