package com.studentmanager.controller;

import com.studentmanager.dao.StudentDAO;
import com.studentmanager.dao.GradeDAO;
import com.studentmanager.model.Student;
import com.studentmanager.model.Grade;
import com.studentmanager.strategy.*;

import java.util.List;

/**
 * Controller class to manage student operations
 */
public class StudentController {
    private StudentDAO studentDAO;
    private GradeDAO gradeDAO;
    private GradeCalculator gradeCalculator;

    public StudentController() {
        this.studentDAO = new StudentDAO();
        this.gradeDAO = new GradeDAO();
        this.gradeCalculator = new GradeCalculator();
    }

    // Student operations
    
    /**
     * Add a new student
     * @param student Student to add
     * @return true if successful, false otherwise
     */
    public boolean addStudent(Student student) {
        // Validate student data
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            System.err.println("Student ID cannot be empty");
            return false;
        }
        if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
            System.err.println("Student name cannot be empty");
            return false;
        }
        
        // Check if student ID already exists
        if (studentDAO.getStudentByStudentId(student.getStudentId()) != null) {
            System.err.println("Student ID already exists: " + student.getStudentId());
            return false;
        }
        
        return studentDAO.addStudent(student);
    }
    
    /**
     * Get student by student ID
     * @param studentId Student ID
     * @return Student object or null if not found
     */
    public Student getStudentByStudentId(String studentId) {
        Student student = studentDAO.getStudentByStudentId(studentId);
        if (student != null) {
            // Load grades for this student
            List<Grade> grades = gradeDAO.getGradesByStudentId(student.getId());
            student.setGrades(grades);
        }
        return student;
    }
    
    /**
     * Get all students
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = studentDAO.getAllStudents();
        // Load grades for each student
        for (Student student : students) {
            List<Grade> grades = gradeDAO.getGradesByStudentId(student.getId());
            student.setGrades(grades);
        }
        return students;
    }
    
    /**
     * Update student information
     * @param student Student with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateStudent(Student student) {
        if (student.getId() <= 0) {
            System.err.println("Invalid student ID for update");
            return false;
        }
        return studentDAO.updateStudent(student);
    }
    
    /**
     * Delete student
     * @param studentId Student ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteStudent(String studentId) {
        Student student = studentDAO.getStudentByStudentId(studentId);
        if (student == null) {
            System.err.println("Student not found: " + studentId);
            return false;
        }
        return studentDAO.deleteStudent(student.getId());
    }
    
    /**
     * Search students by name
     * @param name Name to search for
     * @return List of matching students
     */
    public List<Student> searchStudentsByName(String name) {
        return studentDAO.searchStudentsByName(name);
    }

    // Grade operations
    
    /**
     * Add grade for a student
     * @param studentId Student ID
     * @param subject Subject name
     * @param score Score (0-10)
     * @param coefficient Coefficient
     * @param semester Semester
     * @param year Year
     * @return true if successful, false otherwise
     */
    public boolean addGrade(String studentId, String subject, double score, double coefficient, String semester, int year) {
        Student student = studentDAO.getStudentByStudentId(studentId);
        if (student == null) {
            System.err.println("Student not found: " + studentId);
            return false;
        }
        
        // Validate score
        if (score < 0 || score > 10) {
            System.err.println("Score must be between 0 and 10");
            return false;
        }
        
        Grade grade = new Grade(student.getId(), subject, score, coefficient, semester, year);
        return gradeDAO.addGrade(grade);
    }
    
    /**
     * Get grades for a student
     * @param studentId Student ID
     * @return List of grades
     */
    public List<Grade> getGradesByStudentId(String studentId) {
        Student student = studentDAO.getStudentByStudentId(studentId);
        if (student == null) {
            System.err.println("Student not found: " + studentId);
            return null;
        }
        return gradeDAO.getGradesByStudentId(student.getId());
    }
    
    /**
     * Update grade
     * @param grade Grade with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateGrade(Grade grade) {
        if (grade.getScore() < 0 || grade.getScore() > 10) {
            System.err.println("Score must be between 0 and 10");
            return false;
        }
        return gradeDAO.updateGrade(grade);
    }
    
    /**
     * Delete grade
     * @param gradeId Grade ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteGrade(int gradeId) {
        return gradeDAO.deleteGrade(gradeId);
    }

    // Grade calculation methods using Strategy Pattern
    
    /**
     * Calculate average grade for a student
     * @param studentId Student ID
     * @return Average grade as string
     */
    public String calculateAverageGrade(String studentId) {
        List<Grade> grades = getGradesByStudentId(studentId);
        if (grades == null) return "0.0";
        
        gradeCalculator.setStrategy(new AverageGradeStrategy());
        return gradeCalculator.calculateGrade(grades);
    }
    
    /**
     * Calculate letter grade for a student
     * @param studentId Student ID
     * @return Letter grade as string
     */
    public String calculateLetterGrade(String studentId) {
        List<Grade> grades = getGradesByStudentId(studentId);
        if (grades == null) return "F";
        
        gradeCalculator.setStrategy(new LetterGradeStrategy());
        return gradeCalculator.calculateGrade(grades);
    }
    
    /**
     * Calculate classification for a student
     * @param studentId Student ID
     * @return Classification as string
     */
    public String calculateClassification(String studentId) {
        List<Grade> grades = getGradesByStudentId(studentId);
        if (grades == null) return "Không xếp loại";
        
        gradeCalculator.setStrategy(new ClassificationStrategy());
        return gradeCalculator.calculateGrade(grades);
    }
    
    /**
     * Calculate grade using specific strategy
     * @param studentId Student ID
     * @param strategy Grade calculation strategy
     * @return Calculated result as string
     */
    public String calculateGradeWithStrategy(String studentId, GradeCalculationStrategy strategy) {
        List<Grade> grades = getGradesByStudentId(studentId);
        if (grades == null) return "N/A";
        
        gradeCalculator.setStrategy(strategy);
        return gradeCalculator.calculateGrade(grades);
    }
    
    /**
     * Get available calculation strategies
     * @return Array of available strategies
     */
    public GradeCalculationStrategy[] getAvailableStrategies() {
        return new GradeCalculationStrategy[] {
            new AverageGradeStrategy(),
            new LetterGradeStrategy(),
            new ClassificationStrategy(),
            new GPACalculationStrategy()
        };
    }
}