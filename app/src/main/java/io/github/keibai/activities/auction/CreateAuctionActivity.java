package io.github.keibai.activities.auction;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.event.DetailEventActivity;
import io.github.keibai.form.DefaultAwesomeValidation;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Event;
import io.github.keibai.models.Good;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class CreateAuctionActivity extends AppCompatActivity {

    private int eventId;
    private DefaultAwesomeValidation auctionValidation;
    private DefaultAwesomeValidation combinatorialGoodValidation;
    private DefaultAwesomeValidation englishPriceValidation;
    private Http http;
    private Event event;
    private List<Good> goods;

    private EditText auctionNameEditText;
    private EditText auctionStartingPriceEditText;
    private EditText goodNameEditText;
    private Button addGoodButton;
    private ListView createAuctionGoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_auction);

        if (http == null) {
            http = new Http(getApplicationContext());
        }

        Toolbar toolbar = findViewById(R.id.toolbar_create_event);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        eventId = intent.getIntExtra(DetailEventActivity.EXTRA_EVENT_ID, -1);

        event = SaveSharedPreference.getCurrentEvent(getApplicationContext());
        goods = new ArrayList<>();

        auctionNameEditText = findViewById(R.id.edit_create_auction_name);
        auctionStartingPriceEditText = findViewById(R.id.edit_create_auction_starting_price);
        goodNameEditText = findViewById(R.id.edit_create_auction_good_name);
        addGoodButton = findViewById(R.id.add_good_button);
        createAuctionGoodList = findViewById(R.id.create_auction_good_list);

        auctionValidation = new DefaultAwesomeValidation(getApplicationContext());
        combinatorialGoodValidation = new DefaultAwesomeValidation(getApplicationContext());
        englishPriceValidation = new DefaultAwesomeValidation(getApplicationContext());

        auctionValidation.addValidation(this, R.id.edit_create_auction_name, "[a-zA-Z0-9\\s]+", R.string.auction_name_invalid);

        if (event.auctionType.equals(Event.ENGLISH)) {
            // English auction UI
            goodNameEditText.setVisibility(View.GONE);
            createAuctionGoodList.setVisibility(View.GONE);
            addGoodButton.setVisibility(View.GONE);
            englishPriceValidation.addValidation(this, R.id.edit_create_auction_starting_price, "[0-9\\.]+", R.string.starting_price_invalid);
        } else if (event.auctionType.equals(Event.COMBINATORIAL)) {
            // Combinatorial auction UI
            auctionStartingPriceEditText.setVisibility(View.GONE);
            combinatorialGoodValidation.addValidation(this, R.id.edit_create_auction_good_name, "[a-zA-Z0-9\\s]+", R.string.good_name_invalid);

            final GoodAdapter goodAdapter = new GoodAdapter(this, this.goods, true);
            createAuctionGoodList.setAdapter(goodAdapter);

            addGoodButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!combinatorialGoodValidation.validate()) {
                        return;
                    }
                    Good newGood = new Good();
                    newGood.name = goodNameEditText.getText().toString();
                    newGood.image = "1234";
                    // Auction ID will be set once the auction is created, for all goods!
                    goods.add(newGood);
                    goodAdapter.notifyDataSetChanged();
                    goodNameEditText.requestFocus();
                    goodNameEditText.setText("");
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        http.close();
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
                onSave();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Auction auctionFromForm() {
        EditText formName = findViewById(R.id.edit_create_auction_name);
        EditText formStartingPrice = findViewById(R.id.edit_create_auction_starting_price);

        Auction auction = new Auction();
        auction.name = formName.getText().toString();
        if (event.auctionType.equals(Event.ENGLISH)) {
            auction.startingPrice = Float.parseFloat(formStartingPrice.getText().toString());
        } else {
            auction.startingPrice = 1.0; // Ignored in case of combinatorial auctions
        }

        return auction;
    }

    private void onSave() {
        if (eventId == -1) {
            Toast.makeText(getApplicationContext(), R.string.event_id_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!auctionValidation.validate()) {
            return;
        }


        if (event.auctionType.equals(Event.ENGLISH) && !englishPriceValidation.validate()) {
            return;
        }
        if (event.auctionType.equals(Event.COMBINATORIAL) && goods.size() < 2) {
            Toast.makeText(getApplicationContext(), "Can not create combinatorial auction with less than 2 goods", Toast.LENGTH_SHORT).show();
            return;
        }

        Auction attemptAuction = auctionFromForm();
        attemptAuction.eventId = eventId;
        postAuction(attemptAuction);
    }

    private void postAuction(Auction attemptAuction) {
        Toast.makeText(getApplicationContext(), R.string.submitting, Toast.LENGTH_SHORT).show();

        http.post(HttpUrl.newAuctionUrl(), attemptAuction, new HttpCallback<Auction>(Auction.class) {
            @Override
            public void onError(Error error) throws IOException {
                runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
            }

            @Override
            public void onSuccess(final Auction response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (event.auctionType.equals(Event.ENGLISH)) {
                            Good good = new Good();
                            good.name = response.name;
                            good.auctionId = response.id;
                            good.image = "1234";
                            postGood(good, "Auction successfully created");
                        } else if (event.auctionType.equals(Event.COMBINATORIAL)) {
                            for (Good good: goods) {
                                // Update good auction IDs now that we know the auction ID
                                good.auctionId = response.id;
                                postGood(good, good.name + " created");
                            }
                            Toast.makeText(getApplicationContext(), "Auction successfully created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new RunnableToast(getApplicationContext(), e.toString()));
            }
        });
    }

    private void postGood(Good attemptGood, final String msg) {
        http.post(HttpUrl.newGoodUrl(), attemptGood, new HttpCallback<Good>(Good.class) {
            @Override
            public void onError(Error error) throws IOException {
                runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
            }

            @Override
            public void onSuccess(final Good response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
