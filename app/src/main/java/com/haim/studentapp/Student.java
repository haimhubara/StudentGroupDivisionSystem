package com.haim.studentapp;



public class Student extends User {

    private String roll = "student";



    public Student(){

    }

    public Student(String email, String password,String name,String profilePicture) {
        super(email,password,name,profilePicture);

    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getRoll() {
        return roll;
    }

}