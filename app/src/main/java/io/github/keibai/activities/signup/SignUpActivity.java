package io.github.keibai.activities.signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.MainActivity;
import io.github.keibai.R;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import okhttp3.Call;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signUpButton = findViewById(R.id.button_sign_up_submit);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etName = findViewById(R.id.sign_up_edit_text_name);
                EditText etLastName = findViewById(R.id.sign_up_edit_text_last_name);
                EditText etEmail = findViewById(R.id.sign_up_edit_text_email);
                EditText etRepeatEmail = findViewById(R.id.sign_up_edit_text_repeat_email);
                EditText etPassword = findViewById(R.id.sign_up_edit_text_password);
                EditText etRepeatPassword = findViewById(R.id.sign_up_edit_text_repeat_password);

                // Check email
                String email = etEmail.getText().toString();
                String repeatedEmail = etRepeatEmail.getText().toString();
                if (!email.equals(repeatedEmail)) {
                    Toast.makeText(getApplicationContext(), "Email not equals", Toast.LENGTH_SHORT).show();
                    etEmail.setText("");
                    etRepeatEmail.setText("");
                    return;
                }

                // Check password
                String password = etPassword.getText().toString();
                String repeatedPassword = etRepeatPassword.getText().toString();
                if (!password.equals(repeatedPassword)) {
                    Toast.makeText(getApplicationContext(), "Password not equals", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etRepeatPassword.setText("");
                    return;
                }

                // Others checks will be performed on the server side
                User attemptUser = new User();
                attemptUser.name = etName.getText().toString();
                attemptUser.lastName = etLastName.getText().toString();
                attemptUser.email = etEmail.getText().toString();
                attemptUser.password = etPassword.getText().toString();

                new Http(getApplicationContext()).post(HttpUrl.newUserUrl(), attemptUser, new HttpCallback<User>() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }

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
                        SaveSharedPreference.setUserId(getApplicationContext(), response.id);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
