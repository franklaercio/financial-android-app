package com.github.finacial.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.finacial.R;
import com.github.finacial.datasource.TransactionDatasource;
import com.github.finacial.domain.Transaction;
import com.github.finacial.domain.TransactionType;
import com.google.android.material.textfield.TextInputLayout;

public class AddTransactionActivity extends AppCompatActivity {

    TransactionDatasource transactionDatasource;

    private RadioGroup radioGroup;
    private RadioButton radioButtonDebit;
    private RadioButton radioButtonCredit;
    private RadioButton radioButtonOther;
    private TextInputLayout descriptionEditText;
    private TextInputLayout dateEditText;
    private TextInputLayout amountEditText;

    private String optionChecked;

    boolean hasErrors = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });

        transactionDatasource = new TransactionDatasource(this);

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // Unchecked
                        new int[]{android.R.attr.state_checked} // Checked
                },
                new int[]{
                        com.google.android.material.R.attr.colorPrimary, // Unchecked
                        com.google.android.material.R.attr.colorSecondary // Checked
                }
        );

        radioGroup = findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);

            if (radioButton != null) {
                optionChecked = radioButton.getText().toString();
            }
        });

        radioButtonDebit = findViewById(R.id.radioButtonDebit);
        radioButtonDebit.setButtonTintList(colorStateList);
        radioButtonDebit.setChecked(true);
        radioButtonCredit = findViewById(R.id.radioButtonCredit);
        radioButtonCredit.setButtonTintList(colorStateList);
        radioButtonOther = findViewById(R.id.radioButtonOther);
        radioButtonOther.setButtonTintList(colorStateList);
        descriptionEditText = findViewById(R.id.editTextDescription);
        dateEditText = findViewById(R.id.editTextDateTransaction);
        amountEditText = findViewById(R.id.editTextAmount);
    }

    public void validate(View view) {
        String description = descriptionEditText.getEditText().getText().toString().trim();
        String date = dateEditText.getEditText().getText().toString().trim();
        String amount = amountEditText.getEditText().getText().toString().trim();

        hasErrors = false;

        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError("Required field");
            changeState();
        }

        if (TextUtils.isEmpty(date)) {
            dateEditText.setError("Required field");
            changeState();
        }

        if (TextUtils.isEmpty(amount)) {
            amountEditText.setError("Required field");
            changeState();
        }

        saveTransaction(description, date, optionChecked, amount);
    }

    private void saveTransaction(String description, String date, String type, String amount) {
        if(!hasErrors) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                int user_id = sharedPreferences.getInt("user_id", 0);

                if(user_id < 0) {
                    Toast.makeText(this, "Could not possible create transaction", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                }

                Transaction transaction = new Transaction();
                transaction.setUserId(user_id);
                transaction.setDescription(description);
                transaction.setDate(date);
                transaction.setType(TransactionType.valueOf(type));
                transaction.setAmount(Double.parseDouble(amount));

                transactionDatasource.save(transaction);
                Toast.makeText(this, "Transaction create with success", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(this, "Could not possible create transaction", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void changeState() {
        if(!hasErrors) {
            hasErrors = true;
        }
    }
}
