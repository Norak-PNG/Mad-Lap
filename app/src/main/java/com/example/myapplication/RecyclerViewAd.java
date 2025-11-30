package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// 1. The adapter needs to extend RecyclerView.Adapter
//    and specify its custom ViewHolder class.
public class RecyclerViewAd extends RecyclerView.Adapter<RecyclerViewAd.ViewHolder> {

    // 2. Add variables for the data list and context.
    private final List<Post> expenseList;
    private final Context context;

    // 3. Create a constructor to get the data and context.
    public RecyclerViewAd(List<Post> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
    }

    // 4. onCreateViewHolder: This is called when a new view holder is needed.
    //    It inflates the layout for a single list item.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout XML file.
        // Let's assume you create a file named 'item_expense.xml' for this.
        View view = LayoutInflater.from(context).inflate(R.layout.view_layout, parent, false);
        return new ViewHolder(view);
    }

    // 5. onBindViewHolder: This is called to display the data at a specific position.
    //    It takes data from the 'expenseList' and binds it to the views in the ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data object for the current position.
        Post expense = expenseList.get(position);

        // Bind the data to the TextViews in the ViewHolder.
        holder.remarkTextView.setText(expense.getRemark());
        holder.categoryTextView.setText(expense.getCategory());

        // Combine amount and currency for display.
        String amountText = expense.getAmount() + " " + expense.getCurrency();
        holder.amountTextView.setText(amountText);
    }

    // 6. getItemCount: This must return the total number of items in your data list.
    @Override
    public int getItemCount() {
        Log.d("RecyclerViewAd", "getItemCount called with size: " + expenseList.size());
        return expenseList.size();
    }

    // 7. ViewHolder: This inner class holds the views for a single list item.
    //    It acts as a cache for the views, so you don't have to call findViewById repeatedly.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView remarkTextView;
        TextView categoryTextView;
        TextView amountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views from the item_expense.xml layout.
            remarkTextView = itemView.findViewById(R.id.item_remark);
            categoryTextView = itemView.findViewById(R.id.item_category);
            amountTextView = itemView.findViewById(R.id.item_amount);
        }
    }
}
