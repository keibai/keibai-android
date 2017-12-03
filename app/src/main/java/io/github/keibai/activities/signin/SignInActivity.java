package io.github.keibai.activities.signin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

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

        validation = new DefaultAwesomeValidation(getApplicationContext());
        validation.addValidation(this, R.id.edit_sign_in_email, Patterns.EMAIL_ADDRESS, R.string.email_invalid);
        validation.addValidation(this, R.id.edit_sign_in_password, ".+", R.string.password_invalid);

        // Sign In button
        Button signInButton = findViewById(R.id.button_sign_in_submit);
        signInButton.setOnClickListener(new SignIn());
    }

    public User userFromForm() {
        EditText formEmail = findViewById(R.id.edit_sign_in_email);
        EditText formPassword = findViewById(R.id.edit_sign_in_password);

        User user = new User();
        user.email = formEmail.getText().toString();
        user.password = formPassword.getText().toString();

        return user;
    }

    private class SignIn implements View.OnClickListener {
        @Override
        public void onClick(View v) {
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
}
