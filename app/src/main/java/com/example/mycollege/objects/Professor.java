package com.example.mycollege.objects;

public class Professor {

    private String profName;
    private String profEmail;
    private String profPassword;
    private String profCollege;
    private String profDesignation;
    private String profDepartment;
    private String profBranch;
    private String profSem;

    public Professor() {}

    public Professor(String prof_name, String prof_email, String prof_pass, String prof_college, String prof_designation, String prof_department, String prof_branch, String prof_sem) {
        this.profName = prof_name;
        this.profEmail = prof_email;
        this.profPassword = prof_pass;

        this.profCollege = prof_college;
        this.profDesignation = prof_designation;
        this.profDepartment = prof_department;
        this.profBranch = prof_branch;
        this.profSem = prof_sem;
    }


    //getters
    public String getProfName() {
        return profName;
    }

    public String getProfEmail() {
        return profEmail;
    }

    public String getProfPassword() {
        return profPassword;
    }

    public String getProfCollege() {
        return profCollege;
    }

    public String getProfDesignation() {
        return profDesignation;
    }

    public String getProfDepartment() {
        return profDepartment;
    }

    public String getProfBranch() {
        return profBranch;
    }

    public String getProfSem() {
        return profSem;
    }
}
