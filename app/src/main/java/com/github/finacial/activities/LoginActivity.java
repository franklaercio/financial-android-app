package com.github.finacial.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.finacial.R;
import com.github.finacial.datasource.UserDatasource;
import com.github.finacial.domain.User;

public class LoginActivity extends AppCompatActivity {

    private UserDatasource userDatasource;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDatasource = new UserDatasource(this);

        emailEditText = findViewById(R.id.editTextEmailLogin);
        passwordEditText = findViewById(R.id.editTextPasswordLogin);

        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("user_id")) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

        Button authenticateButton = findViewById(R.id.button);
        authenticateButton.setOnClickListener(v -> authenticate());
    }

    public void onClickSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void authenticate() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            User user = userDatasource.findOneUserByEmailAndPassword(email, password);

            if (user != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", user.getId());
                editor.putString("first_name", user.getFirstName());
                editor.putString("last_name", user.getLastName());
                editor.putString("email", user.getEmail());
                editor.apply();

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Could not possible authenticate!", Toast.LENGTH_LONG).show();
            }
        } else {
            emailEditText.setError("Required field");
            passwordEditText.setError("Required field");
        }
    }
}