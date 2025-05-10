package com.example.mycollege.objects;

public class Attendance {

    private long Total;
    private long Present;
    private long Absent;
    private String SubjectName;


    public Attendance() {}

    public Attendance(long total, long present, long absent, String subjectName) {
        Total = total;
        Present = present;
        Absent = absent;
        SubjectName = subjectName;
    }


    //getters and setters...
    public long getTotal() {
        return Total;
    }

    public void setTotal(long total) {
        Total = total;
    }

    public long getPresent() {
        return Present;
    }

    public void setPresent(long present) {
        Present = present;
    }

    public long getAbsent() {
        return Absent;
    }

    public void setAbsent(long absent) {
        Absent = absent;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }
}
