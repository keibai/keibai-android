package io.github.keibai.activities.credit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.MainActivity;
import io.github.keibai.activities.home.HomeFragment;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Bid;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.models.meta.Msg;
import okhttp3.Call;

/**
 * Created by hzhu on 02/12/2017.
 */

public class CreditActivity extends AppCompatActivity {

    private EditText text_add_credit;
    private Double credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_credit);

        Button submit = findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_add_credit = (EditText) findViewById(R.id.add_credit);
                credit = Double.parseDouble(text_add_credit.getText().toString());

                User attemptUser = new User();
                attemptUser.credit = credit;

                new Http(getApplicationContext()).post(HttpUrl.userUpdateCreditUrl(), attemptUser, new HttpCallback<User>() {
                    @Override
                    public Class<User> model() {
                        return User.class;
                    }

                    @Override
                    public void onError(Error error) throws IOException {
                        System.out.println("this is an error");
                        System.out.println(error);
                    }

                    @Override
                    public void onSuccess(User response) throws IOException {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println(e.toString());
                        //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
//                SaveSharedPreference.setUserId(getApplicationContext(), 1);
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }
        });
    }
}

