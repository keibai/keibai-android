package io.github.keibai.activities.event;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.keibai.R;
import io.github.keibai.activities.auction.Auction;
import io.github.keibai.activities.auction.AuctionAdapter;
import io.github.keibai.activities.auction.CreateAuctionActivity;
import io.github.keibai.activities.auction.DetailAuctionActivity;

public class DetailEventActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "EXTRA_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        Intent intent = getIntent();
        Resources res = getResources();

        Toolbar toolbar = findViewById(R.id.toolbar_detail_event);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(intent.getStringExtra(ActiveEventsActivity.EXTRA_EVENT_NAME));

        TextView textViewLocation = findViewById(R.id.event_detail_location);
        textViewLocation.setText(intent.getStringExtra(ActiveEventsActivity.EXTRA_EVENT_LOCATION));

        TextView textViewTimestamp = findViewById(R.id.event_detail_friendly_timestamp);
        CharSequence timestamp = intent.getCharSequenceExtra(ActiveEventsActivity.EXTRA_EVENT_CREATED_AT);
        String friendlyTimestamp = String.format(res.getString(R.string.created_at_placeholder), timestamp);
        textViewTimestamp.setText(friendlyTimestamp);

        TextView textViewAuctionType = findViewById(R.id.event_detail_auction_type);
        String auctionType = String.format(res.getString(R.string.auction_type_placeholder),
                intent.getStringExtra(ActiveEventsActivity.EXTRA_EVENT_AUCTION_TYPE));
        textViewAuctionType.setText(auctionType);

        // TODO: Auctions will be related with the event, change in next sprint
        List<Auction> auctions = new ArrayList<>();

        auctions.add(new Auction(0, "Auction 0", "Owner 0", R.drawable.ic_dori));
        auctions.add(new Auction(1, "Auction 1", "Owner 1", R.drawable.ic_dori));
        auctions.add(new Auction(2, "Auction 2", "Owner 2", R.drawable.ic_dori));
        auctions.add(new Auction(3, "Auction 3", "Owner 3", R.drawable.ic_dori));
        auctions.add(new Auction(4, "Auction 4", "Owner 4", R.drawable.ic_dori));
        auctions.add(new Auction(5, "Auction 5", "Owner 5", R.drawable.ic_dori));

        AuctionAdapter auctionAdapter = new AuctionAdapter(this, auctions);
        ListView listView = findViewById(R.id.event_auctions_list);
        listView.setAdapter(auctionAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Auction auctionClicked = (Auction) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), DetailAuctionActivity.class);
                intent.putExtra(EXTRA_NAME, auctionClicked.getName());
                startActivity(intent);
            }
        });

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
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
