package io.github.keibai.activities.auction;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.event.DetailEventActivity;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Good;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class CreateAuctionActivity extends AppCompatActivity {

    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_auction);

        Toolbar toolbar = findViewById(R.id.toolbar_create_event);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_auction_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_create_auction_save:
                buildAndSendAuction();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildAndSendAuction() {
        EditText editTextAuctionName = findViewById(R.id.create_auction_name);
        EditText editTextAuctionStartingPrice = findViewById(R.id.create_auction_starting_price);
        EditText editTextGoodName = findViewById(R.id.create_auction_good_name);

        String auctionName = editTextAuctionName.getText().toString();
        double startingPrice = Double.valueOf(editTextAuctionStartingPrice.getText().toString());
        final String goodName = editTextGoodName.getText().toString();

        Intent intent = getIntent();
        eventId = intent.getIntExtra(DetailEventActivity.EXTRA_EVENT_ID, 0);

        Toast.makeText(getApplicationContext(), R.string.submitting, Toast.LENGTH_SHORT).show();
        Auction auction = new Auction();
        auction.eventId = eventId;
        auction.name = auctionName;
        auction.startingPrice = startingPrice;
        auction.ownerId = (int) SaveSharedPreference.getUserId(getApplicationContext());
        auction.status = Auction.OPENED;
        auction.winnerId = 0;
        auction.isValid = false;

        new Http(getApplicationContext()).post(HttpUrl.newAuctionUrl(), auction, new HttpCallback<Auction>(Auction.class) {
            @Override
            public void onError(Error error) throws IOException {
                runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
            }

            @Override
            public void onSuccess(final Auction response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendGood(response.id, goodName);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new RunnableToast(getApplicationContext(), e.toString()));
            }
        });
    }

    private void sendGood(int auctionId, String goodName) {
        Good good = new Good();
        good.name = goodName;
        good.auctionId = auctionId;
        good.image = "TODO";

        new Http(getApplicationContext()).post(HttpUrl.newGoodUrl(), good, new HttpCallback<Good>(Good.class) {
            @Override
            public void onError(Error error) throws IOException {
                runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
            }

            @Override
            public void onSuccess(Good response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Auction successfully created", Toast.LENGTH_LONG).show();
                        finish();
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
