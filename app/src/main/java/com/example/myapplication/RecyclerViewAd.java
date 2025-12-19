package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.database.Post;

import java.util.List;

public class RecyclerViewAd extends RecyclerView.Adapter<RecyclerViewAd.ViewHolder> {

    private final List<Post> expenseList;
    private final Context context;

    public RecyclerViewAd(List<Post> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post expense = expenseList.get(position);

        holder.remarkTextView.setText(expense.getRemark());
        holder.categoryTextView.setText(expense.getCategory());

        String amountText = expense.getAmount() + " " + expense.getCurrency();
        holder.amountTextView.setText(amountText);

        holder.itemView.setOnClickListener(v -> {
            Log.d("RecyclerViewAd", "Item clicked at position: " + position);
            Intent intent = new Intent(context, ViewDetailActivity.class);
            intent.putExtra("remark", expense.getRemark());
            intent.putExtra("category", expense.getCategory());
            intent.putExtra("amount", expense.getAmount());
            intent.putExtra("currency", expense.getCurrency());
            intent.putExtra("date", expense.getCreatedDate());
            intent.putExtra("createdBy", expense.getCreatedBy());
            intent.putExtra("id", expense.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("RecyclerViewAd", "getItemCount called with size: " + expenseList.size());
        return expenseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView remarkTextView;
        TextView categoryTextView;
        TextView amountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            remarkTextView = itemView.findViewById(R.id.item_remark);
            categoryTextView = itemView.findViewById(R.id.item_category);
            amountTextView = itemView.findViewById(R.id.item_amount);
        }
    }
}
