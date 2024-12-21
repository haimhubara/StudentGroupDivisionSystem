package com.haim.studentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizRowHolder> {
    ArrayList<Quiz> QuizData;
    Context context;
    MyClickInterface myClickInterface;

    public QuizAdapter(ArrayList<Quiz> QuizData, Context context,MyClickInterface myClickInterface) {
        this.myClickInterface = myClickInterface;
        this.QuizData =  QuizData;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizAdapter.QuizRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_holder,parent,false);
        return new QuizRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizAdapter.QuizRowHolder holder, int position) {
        holder.textQuiz.setText("New quiz from "+QuizData.get(position).getTeacherName());
        Glide.with(context).load(QuizData.get(position).getQuizPicture()).error(R.drawable.account_img).into(holder.imageOfTeacher);



    }

    @Override
    public int getItemCount() {
        return QuizData.size();
    }

    class QuizRowHolder extends RecyclerView.ViewHolder{
        TextView textQuiz;
        ImageView imageOfTeacher;

        public QuizRowHolder(@NonNull View itemView) {
            super(itemView);
            textQuiz = itemView.findViewById(R.id.txtUsername);
            imageOfTeacher = itemView.findViewById(R.id.img_pro);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickInterface.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    interface MyClickInterface{
        void onItemClick(int positionOfQuiz);

    }

}
