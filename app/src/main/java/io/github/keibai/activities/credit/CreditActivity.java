package io.github.keibai.activities.credit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import io.github.keibai.R;
import io.github.keibai.activities.MainActivity;
import io.github.keibai.form.DefaultAwesomeValidation;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class CreditActivity extends AppCompatActivity {

    private DefaultAwesomeValidation validation;
    private Http http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        if (http == null) {
            http = new Http(getApplicationContext());
        }

        Button submit = findViewById(R.id.button_credit_submit);
        submit.setOnClickListener(new AddCredit());

        validation = new DefaultAwesomeValidation(getApplicationContext());
        validation.addValidation(this, R.id.edit_credit_credit, "[0-9]+", R.string.credit_invalid);
    }

    @Override
    protected void onStop() {
        super.onStop();

        http.close();
    }

    public User userFromForm() {
        EditText formCredit = findViewById(R.id.edit_credit_credit);

        User user = new User();
        user.credit = Double.parseDouble(formCredit.getText().toString());

        return user;
    }

    private class AddCredit implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!validation.validate()) {
                return;
            }

            Toast.makeText(getApplicationContext(), R.string.submitting, Toast.LENGTH_SHORT).show();
            User attemptUser = userFromForm();
            http.post(HttpUrl.userUpdateCreditUrl(), attemptUser, new HttpCallback<User>(User.class) {

                @Override
                public void onError(Error error) throws IOException {
                    runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
                }

                @Override
                public void onSuccess(User response) throws IOException {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new RunnableToast(getApplicationContext(), e.toString()));
                }
            });
        }
    }
}

