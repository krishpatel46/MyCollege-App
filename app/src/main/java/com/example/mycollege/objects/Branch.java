package com.example.mycollege.objects;

public class Branch {

    private final String CourseName;
    private final String CourseDuration;
    private final int NumOfSem;

    public Branch(String courseName, String courseDuration, int numOfSem) {
        this.CourseName = courseName;
        this.CourseDuration = courseDuration;
        this.NumOfSem = numOfSem;
    }

    public String getCourseName() {
        return CourseName;
    }

    public String getCourseDuration() {
        return CourseDuration;
    }

    public int getNumOfSem() {
        return NumOfSem;
    }
}
