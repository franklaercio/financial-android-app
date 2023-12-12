package com.github.finacial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.finacial.R;
import com.github.finacial.datasource.UserDatasource;
import com.github.finacial.domain.User;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    private UserDatasource userDatasource;
    private TextInputLayout firstNameEditText;
    private TextInputLayout lastNameEditText;
    private TextInputLayout emailEditText;
    private TextInputLayout passwordEditText;
    private CheckBox checkBox;

    boolean hasErrors = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        userDatasource = new UserDatasource(this);

        firstNameEditText = findViewById(R.id.editTextDescription);
        lastNameEditText = findViewById(R.id.editTextDateTransaction);
        emailEditText = findViewById(R.id.editTextAmount);
        passwordEditText = findViewById(R.id.editTextPassword);
        checkBox = findViewById(R.id.checkBoxPrivacyTerms);
    }

    public void validate(View view) {
        String firstName = firstNameEditText.getEditText().getText().toString().trim();
        String lastName = lastNameEditText.getEditText().getText().toString().trim();
        String email = emailEditText.getEditText().getText().toString().trim();
        String password = passwordEditText.getEditText().getText().toString().trim();

        hasErrors = false;

        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("Required field");
            changeState();
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Required field");
            changeState();
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required field");
            changeState();
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required field");
            changeState();
        }

        if(!checkBox.isChecked()) {
            Toast.makeText(this, "You need to agree with terms!", Toast.LENGTH_LONG).show();
            changeState();
        }

        saveUser(firstName, lastName, email, password);
    }

    private void saveUser(String firstName, String lastName, String email, String password) {
        if(!hasErrors) {
            try {
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPassword(password);

                userDatasource.save(user);
                Toast.makeText(this, "User created with success!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(this, "Could not possible to create a user.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void changeState() {
        if(!hasErrors) {
            hasErrors = true;
        }
    }
}
