package io.github.keibai.activities.signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.MainActivity;
import io.github.keibai.R;
import io.github.keibai.form.DefaultAwesomeValidation;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class SignUpActivity extends AppCompatActivity {

    private DefaultAwesomeValidation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        validation = new DefaultAwesomeValidation(getApplicationContext());
        validation.addValidation(this, R.id.edit_sign_up_name, "[a-zA-Z\\s]+", R.string.name_invalid);
        validation.addValidation(this, R.id.edit_sign_up_last_name, "[a-zA-Z\\s]+", R.string.last_name_invalid);
        validation.addValidation(this, R.id.edit_sign_up_email, Patterns.EMAIL_ADDRESS, R.string.email_invalid);
        validation.addValidation(this, R.id.edit_sign_up_repeat_password, R.id.edit_sign_up_password, R.string.repeat_email_invalid);
        validation.addValidation(this, R.id.edit_sign_up_password, ".{4,}", R.string.password_invalid);
        validation.addValidation(this, R.id.edit_sign_up_repeat_password, R.id.edit_sign_up_password, R.string.password_invalid);

        Button signUpButton = findViewById(R.id.button_sign_up_submit);
        signUpButton.setOnClickListener(new SignUp());
    }

    public User userFromForm() {
        EditText formName = findViewById(R.id.edit_sign_up_name);
        EditText formLastName = findViewById(R.id.edit_sign_up_last_name);
        EditText formEmail = findViewById(R.id.edit_sign_up_email);
        EditText formPassword = findViewById(R.id.edit_sign_up_password);

        User user = new User();
        user.name = formName.getText().toString();
        user.lastName = formLastName.getText().toString();
        user.email = formEmail.getText().toString();
        user.password = formPassword.getText().toString();

        return user;
    }

    private class SignUp implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!validation.validate()) {
                return;
            }

            User attemptUser = userFromForm();
            new Http(getApplicationContext()).post(HttpUrl.newUserUrl(), attemptUser, new HttpCallback<User>(User.class) {
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
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
