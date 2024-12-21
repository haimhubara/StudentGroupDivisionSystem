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

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentRowHolder> {
    ArrayList<Student> studentData;
    Context context;
    MyClickInterface myClickInterface;

    public StudentAdapter(ArrayList<Student> studentData,Context context,MyClickInterface myClickInterface){
        this.studentData = studentData;
        this.context = context;
        this.myClickInterface =myClickInterface;

    }
    @NonNull
    @Override
    public StudentAdapter.StudentRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_holder,parent,false);
        return new StudentRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.StudentRowHolder holder, int position) {
        Student student = studentData.get(position);
        holder.nameOfStudent.setText(student.getName());
        Glide.with(context).load(studentData.get(position).getProfilePicture()).error(R.drawable.account_img).into(holder.pictureOfStudent);

    }

    @Override
    public int getItemCount() {

        return studentData != null ? studentData.size() : 0;
    }
    class StudentRowHolder extends RecyclerView.ViewHolder {
        TextView nameOfStudent;
        ImageView pictureOfStudent;

        public StudentRowHolder(@NonNull View itemView) {
            super(itemView);
            nameOfStudent = itemView.findViewById(R.id.txtStudentName);
            pictureOfStudent = itemView.findViewById(R.id.img_pro_student);
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
