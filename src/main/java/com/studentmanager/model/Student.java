package com.studentmanager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Student
 */
public class Student {
    private int id;
    private String studentId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String major;
    private List<Grade> grades;

    // Constructors
    public Student() {
        this.grades = new ArrayList<>();
    }

    public Student(String studentId, String fullName, String email, String phoneNumber, String major) {
        this();
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.major = major;
    }

    public Student(int id, String studentId, String fullName, String email, String phoneNumber, String major) {
        this(studentId, fullName, email, phoneNumber, major);
        this.id = id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public void addGrade(Grade grade) {
        this.grades.add(grade);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", major='" + major + '\'' +
                ", grades=" + grades.size() + " grades" +
                '}';
    }
}