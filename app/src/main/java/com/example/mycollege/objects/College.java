package com.example.mycollege.objects;

public class College {

    private final String Name;
    private final String AdminName;
    private final String UniversityName;
    private final String Email;
    private final String RegNo;
    private final String Password;


    public College(String college_name, String uni_name, String admin_name, String reg_no, String mail, String pass) {
        this.Name = college_name;
        this.AdminName = admin_name;
        this.UniversityName = uni_name;
        this.Password = pass;
        this.Email = mail;
        this.RegNo = reg_no;
    }

    public String getAdminName() {
        return AdminName;
    }

    public String getName() {
        return Name;
    }

    public String getUniversityName() {
        return UniversityName;
    }

    public String getEmail() {
        return Email;
    }

    public String getRegNo() {
        return RegNo;
    }

    public String getPassword() {
        return Password;
    }
}
