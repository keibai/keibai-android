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
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;

import io.github.keibai.R;
import io.github.keibai.activities.auction.AuctionAdapter;
import io.github.keibai.activities.auction.CreateAuctionActivity;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Event;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class DetailEventActivity extends AppCompatActivity {

    public static final String EXTRA_AUCTION_NAME = "EXTRA_AUCTION_NAME";
    public static final String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        Intent intent = getIntent();
        final Resources res = getResources();

        this.event = new Gson().fromJson(intent.getStringExtra(ActiveEventsActivity.EXTRA_JSON_EVENT), Event.class);

        Toolbar toolbar = findViewById(R.id.toolbar_detail_event);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(this.event.name);

        TextView textViewLocation = findViewById(R.id.event_detail_location);
        textViewLocation.setText(this.event.location);

        TextView textViewTimestamp = findViewById(R.id.event_detail_friendly_timestamp);

        long now = System.currentTimeMillis();
        CharSequence friendlyTimestamp = DateUtils.getRelativeTimeSpanString(
                event.createdAt.getTime(), now, DateUtils.DAY_IN_MILLIS);
        textViewTimestamp.setText(friendlyTimestamp);

        TextView textViewAuctionType = findViewById(R.id.event_detail_auction_type);
        String auctionType = String.format(res.getString(R.string.auction_type_placeholder), event.auctionType);
        textViewAuctionType.setText(auctionType);

        fetchAuctionList();
    }

    private void fetchAuctionList() {
        new Http(getApplicationContext()).get(HttpUrl.getAuctionListByEventId(this.event.id),
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
        AuctionAdapter auctionAdapter = new AuctionAdapter(this, Arrays.asList(auctions));
        ListView listView = findViewById(R.id.event_auctions_list);
        listView.setAdapter(auctionAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Auction auctionClicked = (Auction) parent.getItemAtPosition(position);
//                Intent intent = new Intent(getApplicationContext(), DetailAuctionActivity.class);
//                intent.putExtra(EXTRA_AUCTION_NAME, auctionClicked.name);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_event_menu, menu);
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
}
