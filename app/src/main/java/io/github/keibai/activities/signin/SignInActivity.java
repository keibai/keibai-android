package io.github.keibai.activities.signin;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.IOException;

import io.github.keibai.activities.MainActivity;
import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.form.DefaultAwesomeValidation;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class SignInActivity extends AppCompatActivity {

    private DefaultAwesomeValidation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = findViewById(R.id.toolbar_sign_in);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        validation = new DefaultAwesomeValidation(getApplicationContext());
        validation.addValidation(this, R.id.edit_sign_in_email, Patterns.EMAIL_ADDRESS, R.string.email_invalid);
        validation.addValidation(this, R.id.edit_sign_in_password, ".+", R.string.password_invalid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_in_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_sign_in:
                onSignIn();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public User userFromForm() {
        EditText formEmail = findViewById(R.id.edit_sign_in_email);
        EditText formPassword = findViewById(R.id.edit_sign_in_password);

        User user = new User();
        user.email = formEmail.getText().toString();
        user.password = formPassword.getText().toString();

        return user;
    }

    public void onSignIn() {
        if (!validation.validate()) {
            return;
        }

        User attemptUser = userFromForm();
        new Http(getApplicationContext()).post(HttpUrl.getUserAuthenticateUrl(), attemptUser, new HttpCallback<User>(User.class) {
            @Override
            public void onError(Error error) throws IOException {
                runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
            }

            @Override
            public void onSuccess(User response) throws IOException {
                SaveSharedPreference.setUserId(getApplicationContext(), response.id);
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
