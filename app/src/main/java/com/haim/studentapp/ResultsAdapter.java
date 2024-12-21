package com.haim.studentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsRowHolder> {
    ArrayList<QuizResult> quizData;
    Context context;
    MyClickInterface myClickInterface;


    public ResultsAdapter (ArrayList<QuizResult> quizData, Context context, MyClickInterface myClickInterface) {
        this.myClickInterface = myClickInterface;
        this.quizData = quizData != null ? quizData : new ArrayList<>();  // Ensure it's not null
        this.context = context;
    }

    @NonNull
    @Override
    public ResultsAdapter.ResultsRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.results_holder, parent, false);
        return new ResultsAdapter.ResultsRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.ResultsRowHolder holder, int position) {
        QuizResult quizResult = quizData.get(position);

        // Make sure quizResult is not null and display proper data
        if (quizResult != null) {
            holder.textResult.setText(quizResult.getText()+" "+(position+1));  // Assuming QuizResult has a quizName field
        }

        // Handle click events
        holder.itemView.setOnClickListener(v -> {
            if (myClickInterface != null) {
                myClickInterface.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizData.size();
    }

    class ResultsRowHolder extends RecyclerView.ViewHolder {
        TextView textResult;

        public ResultsRowHolder(@NonNull View itemView) {
            super(itemView);
            textResult = itemView.findViewById(R.id.resTxtUsername);
        }
    }

    interface MyClickInterface {
        void onItemClick(int positionOfResult);
    }
}
