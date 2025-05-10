package com.example.mycollege.objects;

public class Student {
    private String stuName;
    private String stuEnrollNo;
    private String stuEmail;
    private String stuPassword;
    private String stuCollege;
    private String stuBranch;
    private String stuCourse;
    private String stuSem;

    public Student() {}

    public Student(String stu_name, String stu_enrollNo, String stu_email, String stu_pass, String stu_college, String stu_branch, String stu_course, String stu_sem) {
        this.stuName = stu_name;
        this.stuEnrollNo = stu_enrollNo;
        this.stuEmail = stu_email;
        this.stuPassword = stu_pass;

        this.stuCollege = stu_college;
        this.stuBranch = stu_branch;
        this.stuCourse = stu_course;
        this.stuSem = stu_sem;
    }


    //setters...
    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public void setStuEnrollNo(String stuEnrollNo) {
        this.stuEnrollNo = stuEnrollNo;
    }

    public void setStuEmail(String stuEmail) {
        this.stuEmail = stuEmail;
    }

    public void setStuPassword(String stuPassword) {
        this.stuPassword = stuPassword;
    }

    public void setStuCollege(String stuCollege) {
        this.stuCollege = stuCollege;
    }

    public void setStuBranch(String stuBranch) {
        this.stuBranch = stuBranch;
    }

    public void setStuCourse(String stuCourse) {
        this.stuCourse = stuCourse;
    }

    public void setStuSem(String stuSem) {
        this.stuSem = stuSem;
    }


    //getters...
    public String getStuName() {
        return stuName;
    }
    public String getStuEnrollNo() {
        return stuEnrollNo;
    }
    public String getStuEmail() {
        return stuEmail;
    }
    public String getStuPassword() {
        return stuPassword;
    }
    public String getStuCollege() {
        return stuCollege;
    }
    public String getStuBranch() {
        return stuBranch;
    }
    public String getStuCourse() {
        return stuCourse;
    }
    public String getStuSem() {
        return stuSem;
    }
}
