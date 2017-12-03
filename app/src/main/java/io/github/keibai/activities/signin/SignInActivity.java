package io.github.keibai.activities.signin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import io.github.keibai.activities.MainActivity;
import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Model;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import okhttp3.Call;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Sign In button
        Button signInButton = findViewById(R.id.button_sign_in_submit);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etEmail = findViewById(R.id.sign_in_edit_text_email);
                EditText etPassword = findViewById(R.id.sign_in_edit_text_password);

                User attemptUser = new User();
                attemptUser.email = etEmail.getText().toString();
                attemptUser.password = etPassword.getText().toString();

                new Http(getApplicationContext()).post(HttpUrl.getUserAuthenticateUrl(), attemptUser, new HttpCallback<User>(User.class) {

                    @Override
                    public void onError(Error error) throws IOException {
                        // TODO: Change this
                        System.out.println("Sign in error");
                        System.out.println(error);
                    }

                    @Override
                    public void onSuccess(User response) throws IOException {
                        System.out.println(response.toString());
                        SaveSharedPreference.setUserId(getApplicationContext(), response.id);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        // TODO: Change this
                        System.out.println("Sign in exception");
                        System.out.println(e.toString());
                    }
                });
            }
        });
    }
}
