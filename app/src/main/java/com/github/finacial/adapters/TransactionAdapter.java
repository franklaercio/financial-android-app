package com.github.finacial.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.finacial.R;
import com.github.finacial.domain.Transaction;
import com.github.finacial.domain.TransactionType;
import com.github.finacial.utils.BigDecimalUtils;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDate;
        private final TextView txtDescription;
        private final TextView txtAmount;

        public ViewHolder(View view) {
            super(view);
            txtDate = view.findViewById(R.id.textTitle);
            txtDescription = view.findViewById(R.id.textSubtitle);
            txtAmount = view.findViewById(R.id.textAmount);
        }

        public TextView getTxtDate() {
            return txtDate;
        }

        public TextView getTxtDescription() {
            return txtDescription;
        }

        public TextView getTxtAmount() {
            return txtAmount;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction item = transactions.get(position);
        holder.txtDate.setText(item.getDate());
        holder.txtDescription.setText(item.getDescription());

        if(item.getType().equals(TransactionType.DEBIT)) {
            holder.txtAmount.setText(BigDecimalUtils.toBRCurrencyFormat(-item.getAmount()));
            holder.txtAmount.setTextColor(Color.RED);
        } else {
            holder.txtAmount.setText(BigDecimalUtils.toBRCurrencyFormat(item.getAmount()));
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
