package com.github.finacial.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.finacial.R;
import com.github.finacial.adapters.TransactionAdapter;
import com.github.finacial.datasource.TransactionDatasource;
import com.github.finacial.domain.Transaction;
import com.github.finacial.utils.BigDecimalUtils;
import com.github.finacial.utils.DateUtils;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TransactionDatasource transactionDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        transactionDatasource = new TransactionDatasource(this);

        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String firstName = sharedPreferences.getString("first_name", null);

        TextView textView = findViewById(R.id.textHomeUserName);
        textView.setText(firstName);

        TextView textViewCurrentDate = findViewById(R.id.textViewCurrentDate);
        textViewCurrentDate.setText(DateUtils.showCurrentDate());

        TextView textViewTotalAomount = findViewById(R.id.textViewTotalAmount);
        textViewTotalAomount.setText(BigDecimalUtils.toBRCurrencyFormat(transactionDatasource.getTotalAmount()));

        TextView textViewAdvice = findViewById(R.id.textViewAdvice);
        textViewAdvice.setText(setAdvice(transactionDatasource.getTotalAmount()));

        RecyclerView recyclerView = findViewById(R.id.homeTransactionList);

        List<Transaction> listTransactions = getAllTransactions();

        TransactionAdapter transactionAdapter = new TransactionAdapter(listTransactions);
        recyclerView.setAdapter(transactionAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void onClickAddTransaction(View view) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        startActivity(intent);
    }

    private List<Transaction> getAllTransactions() {
        try {
            return transactionDatasource.getAll();
        } catch (Exception ex) {
            Toast.makeText(this, "Could not possible save transaction", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return null;
        }
    }

    private String setAdvice(double amount) {
        if(amount > 0) {
            return "Money's good, let's save wisely.";
        } else if (amount < 0) {
            return "Debt alert, time to strategize.";
        } else {
            return "Broke, time to budget wisely.";
        }
    }
}
