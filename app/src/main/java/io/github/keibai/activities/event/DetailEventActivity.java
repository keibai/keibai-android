package io.github.keibai.activities.event;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.auction.AuctionAdapter;
import io.github.keibai.activities.auction.CreateAuctionActivity;
import io.github.keibai.activities.auction.DetailAuctionActivity;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Event;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class DetailEventActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";

    private Http http;

    private TextView textViewLocation;
    private TextView textViewTimestamp;
    private TextView textViewAuctionType;
    private TextView textEventStatus;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        http = new Http(getApplicationContext());

        final Resources res = getResources();

        this.event = SaveSharedPreference.getCurrentEvent(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar_detail_event);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(this.event.name);

        // Retrieve text views
        textViewLocation = findViewById(R.id.event_detail_location);
        textViewTimestamp = findViewById(R.id.event_detail_friendly_timestamp);
        textViewAuctionType = findViewById(R.id.event_detail_auction_type);
        textEventStatus = findViewById(R.id.text_event_status);

        // Set UI data
        textViewLocation.setText(this.event.location);

        long now = System.currentTimeMillis();
        CharSequence friendlyTimestamp = DateUtils.getRelativeTimeSpanString(
                event.createdAt.getTime(), now, DateUtils.DAY_IN_MILLIS);
        textViewTimestamp.setText(friendlyTimestamp);

        String auctionType = String.format(res.getString(R.string.auction_type_placeholder), event.auctionType);
        textViewAuctionType.setText(auctionType);

        textEventStatus.setText(event.status);

        fetchAuctionList();
    }

    void fetchAuctionList() {
        http.get(HttpUrl.getAuctionListByEventId(this.event.id),
                new HttpCallback<Auction[]>(Auction[].class) {
                    @Override
                    public void onError(Error error) throws IOException {
                        runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
                    }

                    @Override
                    public void onSuccess(final Auction[] response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                renderAuctionList(response);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new RunnableToast(getApplicationContext(), e.toString()));
                    }
                });
    }

    private void renderAuctionList(Auction[] auctions) {
        AuctionAdapter auctionAdapter = new AuctionAdapter(this, new ArrayList<>(Arrays.asList(auctions)));
        auctionAdapter.setEvent(event);
        ListView listView = findViewById(R.id.event_auctions_list);
        listView.setAdapter(auctionAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Auction auctionClicked = (Auction) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), DetailAuctionActivity.class);
                SaveSharedPreference.setCurrentAuction(getApplication(), auctionClicked);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_event_menu, menu);

        if (!event.status.equals(Event.OPENED)) {
            // Hide create auction button, event is no longer active
            MenuItem item = menu.findItem(R.id.item_detail_event_create_auction);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_detail_event_create_auction: {
                Intent intent = new Intent(getApplicationContext(), CreateAuctionActivity.class);
                intent.putExtra(EXTRA_EVENT_ID, this.event.id);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAuctionList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        http.close();
    }
}
