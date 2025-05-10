package com.example.mycollege.objects;

public class Assignment {
    private String Heading;
    private String Description;
    private String SubmissionLink;
    private String Subject;
    private String Semester;


    // constructors
    public Assignment() {}

    public Assignment(String heading, String description, String submissionLink, String subject, String semester) {
        Heading = heading;
        Description = description;
        SubmissionLink = submissionLink;
        Subject = subject;
        Semester = semester;
    }


    // getters and setters
    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSubmissionLink() {
        return SubmissionLink;
    }

    public void setSubmissionLink(String submissionLink) {
        SubmissionLink = submissionLink;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getSemester() {
        return Semester;
    }

    public void setSemester(String semester) {
        Semester = semester;
    }
}
