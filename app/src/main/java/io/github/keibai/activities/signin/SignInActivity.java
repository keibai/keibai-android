package io.github.keibai.activities.signin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.github.keibai.activities.MainActivity;
import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.models.User;

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

                // TODO: Connection with the server here
                Toast.makeText(getApplicationContext(), attemptUser.toString(), Toast.LENGTH_LONG).show();
//                SaveSharedPreference.setUserId(getApplicationContext(), 1);
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }
        });
    }
}
