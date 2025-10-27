package com.studentmanager.model;

/**
 * Model class representing a Grade/Score
 */
public class Grade {
    private int id;
    private int studentId;
    private String subject;
    private double score;
    private double coefficient; // Hệ số môn học
    private String semester;
    private int year;

    // Constructors
    public Grade() {}

    public Grade(int studentId, String subject, double score, double coefficient, String semester, int year) {
        this.studentId = studentId;
        this.subject = subject;
        this.score = score;
        this.coefficient = coefficient;
        this.semester = semester;
        this.year = year;
    }

    public Grade(int id, int studentId, String subject, double score, double coefficient, String semester, int year) {
        this(studentId, subject, score, coefficient, semester, year);
        this.id = id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getWeightedScore() {
        return score * coefficient;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", subject='" + subject + '\'' +
                ", score=" + score +
                ", coefficient=" + coefficient +
                ", semester='" + semester + '\'' +
                ", year=" + year +
                '}';
    }
}