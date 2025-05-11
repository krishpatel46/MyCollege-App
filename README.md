# 📚 MyCollege App

MyCollege App is a smart Android platform built to digitize and simplify college life for students and professors. Built with Java for Android, it offers seamless communication, efficient academic management, and a user-friendly interface tailored for educational institutions.
- Its key feature is a QR-based online attendance system, making attendance tracking seamless, fast, and reliable. 
- The app also supports communication, academic tracking, and resource sharing, all backed by Firebase.

## Application Features

**1. QR-Based Attendance System**

Professors generate QR codes for each session; students scan to mark attendance—no manual work, fast and reliable.

**2. User Authentication**

Role-based login for students, professors, and admins via Firebase Authentication.

**3. Course & Subject Management**

Professors can create/manage subjects; students can enroll and view their academic content.

**4. Assignment Handling**

Professors upload assignments and review submissions; students submit within the app.

**5. User Profiles**

View and edit personal details, including enrolled subjects and attendance logs.

**🛠️ Admin Panel (College Management)**

A dedicated panel for college administrators to:
- Add student/professor records

- Update existing user details

- Delete students or professors from the system
This is directly integrated with the central college database hosted in Firebase Firestore, allowing real-time data management accessible from within the app.

## Instructions
Before launching the application, integrate Firebase Firestore database with the application.
Here's the database schema:

- Starting from Two main collections: BranchDetails and CollgeDetails...
![image](https://github.com/user-attachments/assets/18ba2516-f2c7-4716-b9dd-4559be7070ea)

- Within BranchDetails, each branch has certain details and a sub-collection CourseDetails...
![image](https://github.com/user-attachments/assets/a7618df6-eacd-420c-adfc-76608e270e9a)

- This in turn has related details and a sub-collection SubjectDetails...
![image](https://github.com/user-attachments/assets/659f2823-80ea-4483-86b8-5ff3fd0e5537)

- SubjectDetails has data of subjects per semester...
![image](https://github.com/user-attachments/assets/625ac24a-c226-4b1b-b9b0-59593341ef1e)
